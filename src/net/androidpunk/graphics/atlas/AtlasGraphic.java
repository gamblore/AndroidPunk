package net.androidpunk.graphics.atlas;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.FP;
import net.androidpunk.Graphic;
import net.androidpunk.android.OpenGLSystem;
import net.androidpunk.graphics.opengl.SubTexture;
import net.androidpunk.graphics.opengl.Texture;
import android.graphics.Point;
import android.graphics.Rect;

public class AtlasGraphic extends Graphic {
	
	private static final String TAG = "AtlasGraphic";
	
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
	
	private Texture mAtlas;
	
	protected SubTexture mSubTexture;

	public AtlasGraphic(SubTexture subTexture) {
		mSubTexture = subTexture;
		mAtlas = subTexture.getTexture();
	}
	
	public static CharBuffer getDirectCharBuffer(int numByte) {
		return ByteBuffer.allocateDirect(numByte * Character.SIZE).order(ByteOrder.nativeOrder()).asCharBuffer();
	}
	
	public static ShortBuffer getDirectShortBuffer(int numShorts) {
		return ByteBuffer.allocateDirect(numShorts * Short.SIZE).order(ByteOrder.nativeOrder()).asShortBuffer();
	}
	
	public static FloatBuffer getDirectFloatBuffer(int numFloats) {
		return ByteBuffer.allocateDirect(numFloats * Float.SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();
	}
	
	public Texture getAtlas() {
		return mAtlas;
	}

	/**
	 * Sets the color filter and loads the texture if it is not already loaded.
	 */
	public void render(GL10 gl, Point point, Point camera) {
		getAtlas().mColorFilter.setColor(mColor);
		getAtlas().mColorFilter.applyColorFilter(gl);
		OpenGLSystem.setTexture(gl, getAtlas());

	}
	
	public static void setBuffers(GL10 gl, FloatBuffer vertexBuffer, FloatBuffer textureBuffer) {
		//gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
    	gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);
	}
	
	public static FloatBuffer setGeometryBuffer(FloatBuffer fb, int x, int y, int width, int height) {
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
	
	
	public static FloatBuffer setTextureBuffer(FloatBuffer fb, Texture t, int x, int y, int width, int height) {
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
	
	public static FloatBuffer setTextureBuffer(FloatBuffer fb, SubTexture t, int index, int frameWidth, int frameHeight) {
		t.getFrame(mRect, index, frameWidth, frameHeight);
		return setTextureBuffer(fb, t.getTexture(), mRect);
	}
	
 	
	protected void setMatrix(GL10 gl) {
		
		//mMatrix.reset();
		float sX = scaleX * scale;
		float sY = scaleY * scale;
		//mMatrix.postScale(sX, sY);
		
		
		//mMatrix.postTranslate(-originX * sX, -originY * sY);
		//gl.glTranslatef(-originX * sX, -originY * sY, 0.0f);
		
		//mMatrix.postTranslate(originX + mPoint.x, originY + mPoint.y);
		
		gl.glTranslatef(originX + mPoint.x, originY + mPoint.y, 0f);
		
		gl.glScalef(sX, sY, 1.0f);
		if (angle != 0) {
			gl.glRotatef(angle, 0, 0, 1.0f);
			
			//mMatrix.postRotate(angle);
		}
		gl.glTranslatef(-originX, -originY, 0.0f);
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
	
	/**
	 * Get the width of the graphic.
	 * @return width
	 */
	public int getWidth() {
		return mSubTexture.getWidth();
	}
	
	/**
	 * Get the height of the graphic.
	 * @return height
	 */
	public int getHeight() {
		return mSubTexture.getHeight();
	}
	
	/**
	 * Centers the Image's originX/Y to its center.
	 */
	public void centerOrigin() {
		originX = mSubTexture.getWidth() / 2;
		originY = mSubTexture.getHeight() / 2;
	}
}
