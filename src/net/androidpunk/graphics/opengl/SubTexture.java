package net.androidpunk.graphics.opengl;

import android.graphics.Rect;

public class SubTexture {

	private final Rect mRect = new Rect();
	private Texture mTexture;
	
	/**
	 * Create a subtexture object to describe a texture in a texture.
	 * @param t The base Texture to use.
	 * @param x The start x coord of the subtexture (in pixels).
	 * @param y The start y coord of the subtexture (in pixels).
	 * @param width The width of the subtexture (in pixels).
	 * @param height The height of the subtexture (in pixels).
	 */
	public SubTexture(Texture t, int x, int y, int width, int height) {
		mTexture = t;
		mRect.set(x,y,x+width,y+height);
	}
	
	/**
	 * Gets the texture this subtexture uses.
	 * @return The subtexture.
	 */
	public Texture getTexture() {
		return mTexture;
	}
	
	/**
	 * The bounds of the whole subtexture. 
	 * @return
	 */
	public Rect getBounds() {
		return mRect;
	}
	
	/**
	 * The width of the subtexture.
	 * @return
	 */
	public int getWidth() {
		return mRect.width();
	}
	
	/**
	 * The height of the subtexture.
	 * @return
	 */
	public int getHeight() {
		return mRect.height();
	}
	
	/**
	 * Set a rect to the clip for that frame in the whole texture
	 * @param r the rect to set to the clip of the whole texture.
	 */
	public void getFrame(Rect r, int index, int frameWidth, int frameHeight) {
		int x = index * frameWidth;
		int y = (x / mRect.width()) * frameHeight;
		x %= mRect.width();
		r.set(mRect.left + x, mRect.top + y, mRect.left + x + frameWidth, mRect.top + y + frameHeight);
	}
	
	/**
	 * Set a rec to the clip based on a relative rect.
	 * @param clipRect the rect to set to the clip of the whole texture.
	 * @param relativeClipRect a relative rect of the subtexture.
	 */
	public void GetAbsoluteClipRect(Rect clipRect, Rect relativeClipRect) {
		int width = relativeClipRect.width();
		int height = relativeClipRect.height();
		clipRect.left = relativeClipRect.left + mRect.left;
		clipRect.top = relativeClipRect.top + mRect.top;
		clipRect.right = clipRect.left + width;
		clipRect.bottom = clipRect.top + height;
	}
}
