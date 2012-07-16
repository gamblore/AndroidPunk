package net.androidpunk.android;

import java.nio.ByteBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.opengl.GLUtils;
import android.util.Log;

public class Texture {

	private static final String TAG = "Texture";
	
	/**
	 * Name to use in glBindTexture;
	 */
	protected int name = -1;
	
	/**
	 * The opacity of the texture
	 */
	public float alpha = 1.0f;
	
	/**
	 * The tint of the texture.
	 */
	public float red = 1.0f;
	
	/**
	 * The tint of the texture.
	 */
	public float green = 1.0f;
	
	/**
	 * The tint of the texture.
	 */
	public float blue = 1.0f;
	
	/**
	 * Set this to offset the texture.
	 */
	public int offsetX = 0, offsetY = 0;
	
	/**
	 * Set to flip the X axis.
	 */
	public boolean flipX = false;
	
	/**
	 * Set to flip the Y axis.
	 */
	public boolean flipY = false;
	
	/**
	 * Does this texture clamp or repeat.
	 */
	private boolean mRepeat;
	
	/**
	 * Has the texture been created?
	 */
	private boolean mCreated = false;
	/**
	 * The crop of the texture.
	 * Cannot be a repeating texture and have a crop.
	 */
	private final Rect mCrop = new Rect();
	
	// store infromation for creating texture coordinates.
	private int imageWidth, imageHeight, texWidth, texHeight;
	
	public Texture() {
		
	}
	
	public static void checkGLError(GL10 gl) {
		
	}
	
	public static int nextHigher2(int v) {
		if (v == 0)
			return 1;
		v--;
		v |= v >> 1;
		v |= v >> 2;
		v |= v >> 4;
		v |= v >> 8;
		v |= v >> 16;
		return ++v;
	}
	
	/**
	 * Creates a non-repeating texture in an OpenGL thread (MUST be called from OpenGL Thread)
	 * @param gl
	 * @param bm
	 */
	public void createTexture(GL10 gl, Bitmap bm) {
		createTexture(gl, bm, false);
	}
	
	/**
	 * Creates a texture in an OpenGL thread (MUST be called from OpenGL Thread)
	 * @param gl the context
	 * @param bm the bitmap to use
	 * @param repeat if this bitmap is a repeating texture.
	 */
	public void createTexture(GL10 gl, Bitmap bm, boolean repeat) {
		mRepeat = repeat;
		gl.glEnable(GL10.GL_TEXTURE_2D);
		imageWidth = bm.getWidth();
		imageHeight = bm.getHeight();
		texWidth = Texture.nextHigher2(imageWidth);
		texHeight = Texture.nextHigher2(imageHeight);
		Bitmap po2Bitmap;
		if (repeat) {
			po2Bitmap = Bitmap.createScaledBitmap(bm, (int) texWidth, (int) texHeight, true);
			mCrop.set(0, 0, texWidth, texHeight);
		} else {
			po2Bitmap = Bitmap.createBitmap((int) texWidth,  (int) texHeight, Config.ARGB_8888);
			Canvas c = new Canvas(po2Bitmap);
			c.drawBitmap(bm, 0, 0, null);
			mCrop.set(0, 0, imageWidth, imageHeight);
		}
		
		int textures[] = new int[1];
		gl.glGenTextures(1, textures, 0); OpenGLSystem.checkGLError(gl);
		
		name = textures[0];
		// Select this OpenGL texture
		gl.glBindTexture(GL10.GL_TEXTURE_2D, name);
		Log.d(TAG, "Texture is " + texWidth + " by " + texHeight + " binded to " + name);
		// Set the texture parameters
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		if (repeat) {
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
		} else {
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE); 
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
		}
		
		// Allocate room for the texture
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, po2Bitmap, 0);
		
		po2Bitmap.recycle();
		
		mCreated = true;
	}
	
	public void updateTexture(GL10 gl, Bitmap bm) {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, name);
		GLUtils.texSubImage2D(GL10.GL_TEXTURE_2D, 0, 0, 0, bm);
		mCreated = true;
	}
	
	/**
	 * Crop the texture to these values (in pixels of the original texture size).
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	public void setCrop(int left, int top, int right, int bottom) {
		mCrop.set(left, top, right, bottom);
	}
	
	/**
	 * Crop the texture to these values (in pixels of the original texture size).
	 * @param r Rectangle to use for area.
	 */
	public void setCrop(Rect r) {
		mCrop.set(r);
	}
	
	/**
	 * Sets the crop back the the full 
	 */
	public void removeCrop() {
		if (mRepeat)
			mCrop.set(0, 0, texWidth, texHeight);
		else
			mCrop.set(0,0, imageWidth, imageHeight);
	}
	
	/**
	 * Will set the provided array with the texture coordinates of this texture.
	 * @param texCoords an 8-element array. (x,y) * 4 vertices.
	 */
	public void setTexCoords(float[] texCoords) {
		float left = (float)(offsetX+mCrop.left)/texWidth;
		float top = (float)(offsetY+mCrop.top)/texHeight;
		float right, bottom;
		
		right = (float)(offsetX+mCrop.right)/texWidth;
		bottom = (float)(offsetY+mCrop.bottom)/texHeight;
		
		if (flipX) {
			float tmp = left;
			left = right;
			right = tmp;
		}
		if (flipY) {
			float tmp = top;
			top = bottom;
			bottom = tmp;
		}
		texCoords[0] = left; texCoords[1] = top;
		texCoords[2] = right; texCoords[3] = top;
		texCoords[4] = left; texCoords[5] = bottom;
		texCoords[6] = right; texCoords[7] = bottom;
	}
	
	/**
	 * Set the texcoords using width and height to influence texture repeat.
	 * 
	 * @param texCoords the array to set.
	 * @param width the width of the quad.
	 * @param height the height of the quad.
	 */
	public void setTexCoords(float[] texCoords, int width, int height) {
		float left = (float)(offsetX+mCrop.left)/texWidth;
		float top = (float)(offsetY+mCrop.top)/texHeight;
		float right, bottom;

		// No crop here the whole texture is used.
		right = (float)offsetX/texWidth+((float)width/imageWidth);
		bottom = (float)offsetY/texHeight+((float)height/imageHeight);

		texCoords[0] = left; texCoords[1] = top;
		texCoords[2] = right; texCoords[3] = top;
		texCoords[4] = left; texCoords[5] = bottom;
		texCoords[6] = right; texCoords[7] = bottom;
	}
	
	/**
	 * Get if the texture is repeating
	 * @return was the texture created to repeat.
	 */
	public boolean isRepeating() {
		return mRepeat;
	}
	
	/**
	 * Can this be drawn?
	 * @return if it is drawable
	 */
	public boolean isDrawable() {
		return mCreated;
	}
	
	/**
	 * Free memory.
	 * @param gl the GL engine to use.
	 */
	public void releaseTexture(GL10 gl) {
		int textures[] = new int[1];
		textures[0] = name;
		gl.glDeleteTextures(1, textures, 0);
		mCreated = false;
	}
}
