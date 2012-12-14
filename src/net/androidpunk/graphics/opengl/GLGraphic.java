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

	public GLGraphic() {
		super();
	}

	protected void setMatrix(GL10 gl) {
		// Translate to origin
		// scale the sprite
		// rotate the sprite
		// translate to position + origin.
		float sX = scaleX * scale * FP.scale;
		float sY = scaleY * scale * FP.scale;
		gl.glTranslatef((originX * Math.abs(sX)) + mPoint.x * Math.abs(FP.scale), (originY * Math.abs(sY)) + mPoint.y * Math.abs(FP.scale), 0f);
		
		
		if (angle != 0) {
			gl.glRotatef(angle, 0, 0, 1.0f);
		}
		
		gl.glScalef(sX, sY, 1.0f);
		gl.glTranslatef(-originX, -originY, 0.0f);
	}
	
	public void applyColor(GL10 gl) {
		float red = Color.red(mColor) / 255f;
		float green = Color.green(mColor) / 255f;
		float blue = Color.blue(mColor) / 255f;
		float alpha = Color.alpha(mColor) / 255f;
		
		gl.glColor4f(red, green, blue, alpha);
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