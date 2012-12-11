package net.androidpunk.graphics.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.FP;
import net.androidpunk.Graphic;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.Matrix;

public class GLGraphic extends Graphic {

	/**
	 * Rotation of the image, in degrees.
	 */
	public float angle = 0;
	/**
	 * Scale of the image, effects both x and y scale.
	 */
	public float scale = 1;
	/**
	 * X scale of the image.
	 */
	public float scaleX = 1;
	/**
	 * Y scale of the image.
	 */
	public float scaleY = 1;
	/**
	 * X origin of the image, determines transformation point.
	 */
	public int originX;
	/**
	 * Y origin of the image, determines transformation point.
	 */
	public int originY;
	private static Rect mRect = FP.rect;
	protected static final FloatBuffer QUAD_FLOAT_BUFFER_1 = getDirectFloatBuffer(8);
	protected static final FloatBuffer QUAD_FLOAT_BUFFER_2 = getDirectFloatBuffer(8);
	protected int mColor = 0xffffffff;
	
	private float[] mMatrix = new float[16];
	private int mProgram = -1;
	
	private static int DEFAULT_PROGRAM = -1; 
	
	private final String DEFAULT_VERTEX_SHADER_CODE =
		    "attribute vec4 vPosition;" +
		    "uniform mat4 uMVPMatrix;" +
		    "void main() {" +
		    "  gl_Position = vPosition * uMVPMatrix;" +
		    "}";

	private final String DEFAULT_FRAGMENT_SHADER_CODE =
		    "precision mediump float;" +
		    "uniform vec4 vColor;" +
		    "void main() {" +
		    "  gl_FragColor = vColor;" +
		    "}";
	
	/**
	 * Get a char direct buffer that is native order. This is to be used in OpenGL calls.
	 * @param numFloats the number of chars you want.
	 * @return The allocated buffer.
	 */
	public static CharBuffer getDirectCharBuffer(int numByte) {
		return ByteBuffer.allocateDirect(numByte * Character.SIZE).order(ByteOrder.nativeOrder()).asCharBuffer();
	}

	/**
	 * Get a short direct buffer that is native order. This is to be used in OpenGL calls.
	 * @param numFloats the number of shorts you want.
	 * @return The allocated buffer.
	 */
	public static ShortBuffer getDirectShortBuffer(int numShorts) {
		return ByteBuffer.allocateDirect(numShorts * Short.SIZE).order(ByteOrder.nativeOrder()).asShortBuffer();
	}

	/**
	 * Get a float direct buffer that is native order. This is to be used in OpenGL calls.
	 * @param numFloats the number of floats you want.
	 * @return The allocated buffer.
	 */
	public static FloatBuffer getDirectFloatBuffer(int numFloats) {
		return ByteBuffer.allocateDirect(numFloats * Float.SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();
	}

	public static void setBuffers(GL10 gl, FloatBuffer vertexBuffer, FloatBuffer textureBuffer) {
		//gl.glEnable(GL10.GL_TEXTURE_2D);
		if (textureBuffer != null) 
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
		
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);
	}

	public static FloatBuffer setGeometryBuffer(FloatBuffer fb, int x,
			int y, int width, int height) {
				fb.position(0);
				fb.put(x).put(y);
				fb.put(x + width).put(y);
				fb.put(x).put(y + height);
				fb.put(x + width).put(y + height);
				fb.position(0);
				//Log.d(TAG, String.format("Object geometry (%d, %d) -> (%d, %d)", x,y, x+width, y+height) );
				return fb;
			}

	public static FloatBuffer setGeometryBuffer(FloatBuffer fb, Rect r) {
		return setGeometryBuffer(fb, r.left, r.top, r.width(), r.height());
	}

	public static FloatBuffer setTextureBuffer(FloatBuffer fb, Texture t,
			int x, int y, int width, int height) {
				fb.position(0);
				fb.put((float)x/t.getWidth()).put((float)y/t.getHeight());
				fb.put((float)(x + width)/t.getWidth()).put((float)y/t.getHeight());
				fb.put((float)x/t.getWidth()).put((float)(y + height)/t.getHeight());
				fb.put((float)(x + width)/t.getWidth()).put((float)(y + height)/t.getHeight());
				fb.position(0);
				//Log.d(TAG, String.format("Object texture (%f, %f) -> (%f, %f)", (float)x/t.getWidth(), (float)y/t.getHeight(), (float)(x + width)/t.getWidth(), (float)(y + height)/t.getHeight()) );
				return fb;
			}

	public static FloatBuffer setTextureBuffer(FloatBuffer fb, Texture t, Rect r) {
		return setTextureBuffer(fb, t, r.left, r.top, r.width(), r.height());
	}

	public static FloatBuffer setTextureBuffer(FloatBuffer fb, Texture t) {
		return setTextureBuffer(fb, t, 0, 0, t.getWidth(), t.getHeight());
	}

	public static FloatBuffer setTextureBuffer(FloatBuffer fb, SubTexture t) {
		return setTextureBuffer(fb, t.getTexture(), t.getBounds());
	}

	public static FloatBuffer setTextureBuffer(FloatBuffer fb, SubTexture t,
			int index, int frameWidth, int frameHeight) {
				t.getFrame(mRect, index, frameWidth, frameHeight);
				return setTextureBuffer(fb, t.getTexture(), mRect);
			}

	public static int loadShader(int type, String code) {
		int shader = GLES20.glCreateShader(type);

	    // add the source code to the shader and compile it
	    GLES20.glShaderSource(shader, code);
	    GLES20.glCompileShader(shader);

	    return shader;
	}
	
	public GLGraphic() {
		super();
		
		if (DEFAULT_PROGRAM == -1) {
			int geometry = loadShader(GLES20.GL_VERTEX_SHADER, DEFAULT_VERTEX_SHADER_CODE);
			int fragment = loadShader(GLES20.GL_FRAGMENT_SHADER, DEFAULT_FRAGMENT_SHADER_CODE);
			
			DEFAULT_PROGRAM = GLES20.glCreateProgram();
			GLES20.glAttachShader(DEFAULT_PROGRAM, geometry);
			GLES20.glAttachShader(DEFAULT_PROGRAM, fragment);
			GLES20.glLinkProgram(DEFAULT_PROGRAM);
		}
		mProgram = DEFAULT_PROGRAM;
	}
	
	public GLGraphic(String geometryShader, String fragmentShader) {
		super();
		
		int geometry = loadShader(GLES20.GL_VERTEX_SHADER, geometryShader);
		int fragment = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);
		
		mProgram = GLES20.glCreateProgram();
		GLES20.glAttachShader(mProgram, geometry);
		GLES20.glAttachShader(mProgram, fragment);
		GLES20.glLinkProgram(mProgram);
	}

	protected void setMatrix(GL10 gl) {
		// Translate to origin
		// scale the sprite
		// rotate the sprite
		// translate to position + origin.
		float sX = scaleX * scale * FP.scale;
		float sY = scaleY * scale * FP.scale;
		Matrix.orthoM(mMatrix, 0, 0, FP.width, FP.height, 0, 1, -1);
		
		Matrix.translateM(mMatrix, 0, 
				(originX * Math.abs(sX)) + mPoint.x * Math.abs(sX), (originY * Math.abs(sY)) + mPoint.y * Math.abs(sY), 0f);
		//gl.glTranslatef((originX * Math.abs(sX)) + mPoint.x * Math.abs(sX), (originY * Math.abs(sY)) + mPoint.y * Math.abs(sY), 0f);
		
		
		if (angle != 0) {
			Matrix.rotateM(mMatrix, 0, angle, 0, 0, 1.0f);
			//gl.glRotatef(angle, 0, 0, 1.0f);
			//mMatrix.postRotate(angle);
		}
		
		Matrix.scaleM(mMatrix, 0, sX, sY, 1.0f);
		//gl.glScalef(sX, sY, 1.0f);
		Matrix.translateM(mMatrix, 0, -originX, -originY, 1.0f);
		//gl.glTranslatef(-originX, -originY, 0.0f);
		
		
		int mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMatrix, 0);
	}
	
	public void applyColor(GL10 gl) {
		float red = Color.red(mColor) / 255f;
		float green = Color.green(mColor) / 255f;
		float blue = Color.blue(mColor) / 255f;
		float alpha = Color.alpha(mColor) / 255f;
		
		int colorLocation = GLES20.glGetUniformLocation(mProgram, "vColor");
		GLES20.glUniform1i(colorLocation, mColor);
		//gl.glColor4f(red, green, blue, alpha);
	}
	
	/**
	 * Sets the color filter and loads the texture if it is not already loaded.
	 */
	public void render(GL10 gl, Point point, Point camera) {
		applyColor(gl);
		
		mPoint.x = (int)(point.x + x - camera.x * scrollX);
		mPoint.y = (int)(point.y + y - camera.y * scrollY);
	}

	/**
	 * The tinted color of the Image. Use 0xFFFFFFFF to draw the Image normally.
	 */
	public int getColor() { 
		return mColor; 
	}

	/**
	 * The tinted color of the Image. Use 0xFFFFFFFF to draw the Image normally.
	 */
	public void setColor(int value) {
		mColor = value;
	}

}