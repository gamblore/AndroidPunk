package net.androidpunk.graphics.opengl;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.FP;
import net.androidpunk.Graphic;
import net.androidpunk.R;
import net.androidpunk.android.OpenGLSystem;
import net.androidpunk.android.OpenGLSystem.OpenGLRunnable;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class GLGraphic extends Graphic {
	
	private static final String TAG = "GLGraphic";
	
	public boolean alphablend = true;
	
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
	
	private int mModelViewHandle;
	private int mProjectionViewHandle;
	
	protected int mBlendColorHandle;
	protected int mPositionHandle;
	protected int mTextureHandle;
	
	private static Rect mRect = FP.rect;
	protected static final FloatBuffer QUAD_FLOAT_BUFFER_1 = getDirectFloatBuffer(8);
	protected static final FloatBuffer QUAD_FLOAT_BUFFER_2 = getDirectFloatBuffer(8);
	protected int mColor = 0xffffffff;
	
	public static final float[] PROJECTION_MATRIX = new float[16];
	public static final float[] IDENTITY_MATRIX = new float[16];

	
	private float[] mMatrix = new float[16];
	protected int mProgram = -1;
	
	private static int DEFAULT_PROGRAM = -1; 
	
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
		if (textureBuffer != null) {
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
		}
		
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
	    Log.d(TAG, GLES20.glGetShaderInfoLog(shader));

	    return shader;
	}
	
	public static String convertStreamToString(InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
	public GLGraphic() {		
		if (DEFAULT_PROGRAM == -1) {
			DEFAULT_PROGRAM = Shader.getProgram(R.raw.shader_g_flat, R.raw.shader_f_flat);
		}
		mProgram = DEFAULT_PROGRAM;
		GLES20.glUseProgram(mProgram);
		mModelViewHandle = GLES20.glGetUniformLocation(mProgram, "uModelView");
		mProjectionViewHandle = GLES20.glGetUniformLocation(mProgram, "uProjectionView");
		mBlendColorHandle = GLES20.glGetUniformLocation(mProgram, "uBlendColor");
		
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "Position");
		mTextureHandle = GLES20.glGetAttribLocation(mProgram, "TexCoord");
		
		GLES20.glUniformMatrix4fv(mProjectionViewHandle, 1, false, PROJECTION_MATRIX, 0);
	}
	
	public GLGraphic(int geometryRes, int fragmentRes) {
		useShaders(geometryRes, fragmentRes);
	}
	
	/**
	 * Safely load a shader.
	 * @param geometryRes
	 * @param fragmentRes
	 */
	protected void useShaders(int geometryRes, int fragmentRes) {
		final int geoShader = geometryRes;
		final int fragShader = fragmentRes;
		
		OpenGLSystem.postRunnable(new OpenGLRunnable() {
			@Override
			public void run(GL10 gl) {
				mProgram = Shader.getProgram(geoShader, fragShader);
				GLES20.glUseProgram(mProgram);
				mModelViewHandle = GLES20.glGetUniformLocation(mProgram, "uModelView");
				mProjectionViewHandle = GLES20.glGetUniformLocation(mProgram, "uProjectionView");
				mBlendColorHandle = GLES20.glGetUniformLocation(mProgram, "uBlendColor");
				
				mPositionHandle = GLES20.glGetAttribLocation(mProgram, "Position");
				mTextureHandle = GLES20.glGetAttribLocation(mProgram, "TexCoord");
				
				GLES20.glUniformMatrix4fv(mProjectionViewHandle, 1, false, PROJECTION_MATRIX, 0);
			}
		});
	}

	protected void setMatrix() {
		// Translate to origin
		// scale the sprite
		// rotate the sprite
		// translate to position + origin.
		float sX = scaleX * scale * FP.scale;
		float sY = scaleY * scale * FP.scale;
		//Matrix.setIdentityM(mMatrix, 0);
		Matrix.translateM(mMatrix, 0, IDENTITY_MATRIX, 0, 
				(originX * Math.abs(sX)) + mPoint.x,// * Math.abs(sX),
				(originY * Math.abs(sY)) + mPoint.y,// * Math.abs(sY),
				0f);
		
		if (angle != 0) {
			Matrix.rotateM(mMatrix, 0, angle, 0, 0, 1.0f);
		}
		if (sX != 1 || sY != 1) {
			Matrix.scaleM(mMatrix, 0, sX, sY, 1.0f);
		}
		
		Matrix.translateM(mMatrix, 0, -originX, -originY, 1.0f);
		
		GLES20.glUniformMatrix4fv(mModelViewHandle, 1, false, mMatrix, 0);
	}
	
	public void applyColor() {
		if (mColor == 0xffffffff) {
			GLES20.glUniform4f(mBlendColorHandle, 1.0f, 1.0f, 1.0f, 1.0f);
		} else {
			float red = Color.red(mColor) / 255f;
			float green = Color.green(mColor) / 255f;
			float blue = Color.blue(mColor) / 255f;
			float alpha = Color.alpha(mColor) / 255f;
		
			GLES20.glUniform4f(mBlendColorHandle, red, green, blue, alpha);
		}
		//gl.glColor4f(red, green, blue, alpha);
	}
	
	/**
	 * Sets the color filter and loads the texture if it is not already loaded.
	 */
	public void render(GL10 gl, Point point, Point camera) {
		if (mProgram < 0) {
			mProgram = DEFAULT_PROGRAM;
		}
		if (mProgram < 0) {
			Log.e(TAG, "No Program");
			return;
		}
		GLES20.glUseProgram(mProgram);
		
		applyColor();

		mPoint.x = (int)(point.x + x - camera.x * scrollX);
		mPoint.y = (int)(point.y + y - camera.y * scrollY);
		
		setMatrix();
		
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