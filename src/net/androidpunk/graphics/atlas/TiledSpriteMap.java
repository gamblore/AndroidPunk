package net.androidpunk.graphics.atlas;

import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.android.OpenGLSystem;
import net.androidpunk.graphics.opengl.SubTexture;
import android.graphics.Point;

public class TiledSpriteMap extends SpriteMap {

	private static final String TAG = "TiledSpriteMap";
	
	private int mImageWidth;
	private int mImageHeight;
	private int mOffsetX = 0;
	private int mOffsetY = 0;
			
	/**
	 * Constructs the tiled spritemap.
	 * @param	source			Source image.
	 */
	public TiledSpriteMap(SubTexture source) {
		this(source, 0, 0, 0, 0, null);
	}
	
	/**
	 * Constructs the tiled spritemap.
	 * @param	source			Source image.
	 * @param	frameWidth		Frame width.
	 * @param	frameHeight		Frame height.
	 */
	public TiledSpriteMap(SubTexture source, int frameWidth, int frameHeight) {
		this(source, frameWidth, frameHeight, 0, 0, null);
	}
	
	/**
	 * Constructs the tiled spritemap.
	 * @param	source			Source image.
	 * @param	frameWidth		Frame width.
	 * @param	frameHeight		Frame height.	
	 * @param	width			Width of the block to render.
	 * @param	height			Height of the block to render.
	 */
	public TiledSpriteMap(SubTexture source, int frameWidth, int frameHeight, int width, int height) {
		this(source, frameWidth, frameHeight, width, height, null);
	}
	
	/**
	 * Constructs the tiled spritemap.
	 * @param	source			Source image.
	 * @param	frameWidth		Frame width.
	 * @param	frameHeight		Frame height.	
	 * @param	width			Width of the block to render.
	 * @param	height			Height of the block to render.
	 * @param	callback		callback function for animation end.
	 */
	public TiledSpriteMap(SubTexture source, int frameWidth, int frameHeight, int width, int height, OnAnimationEndCallback callback) {
		super(source, frameWidth, frameHeight, callback);
		mImageWidth = width;
		mImageHeight = height;
	}
	/**
	 * The x-offset of the texture.
	 */
	public int getOffsetX() { return mOffsetX; }
	public void setOffsetX(int value) {
		if (mOffsetX == value) 
			return;
		mOffsetX = value;
	}
	
	/**
	 * The y-offset of the texture.
	 */
	public int getOffsetY() { return mOffsetY; }
	public void setOffsetY(int value) {
		if (mOffsetY == value) 
			return;
		mOffsetY = value;
	}
	
	/**
	 * Sets the texture offset.
	 * @param	x		The x-offset.
	 * @param	y		The y-offset.
	 */
	public void setOffset(int x, int y) {
		if (mOffsetX == x && mOffsetY == y) 
			return;
		mOffsetX = x;
		mOffsetY = y;
	}

	@Override
	public void render(GL10 gl, Point point, Point camera) {
		// Need to pull out AtlasGraphic instead of rendering the spritemap.
		// For optimisation
		getAtlas().mColorFilter.setColor(mColor);
		getAtlas().mColorFilter.applyColorFilter(gl);
		OpenGLSystem.setTexture(gl, getAtlas());
		
		if (!getAtlas().isLoaded()) {
			return;
		}
		mPoint.x = (int)(point.x + x - camera.x * scrollX);
		mPoint.y = (int)(point.y + y - camera.y * scrollY);

		int xx = mPoint.x;
		int yy = mPoint.y;
		
		mTextureBuffer.position(8 * mFrame);
		setBuffers(gl, mVertexBuffer, mTextureBuffer);
		
		gl.glPushMatrix();
		{
			setMatrix(gl);
			//gl.glTranslatef(mPoint.x, mPoint.y, 0);
			while (yy  < mPoint.y + mImageHeight) {
				while (xx < mPoint.x + mImageWidth) {
					gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
					
					xx += mFrameWidth;
					gl.glTranslatef(mFrameWidth, 0, 0);
					
				}
				
				yy += mFrameHeight;
				gl.glTranslatef(mPoint.x - xx, mFrameHeight, 0);
				xx = mPoint.x;
			}
		}
		gl.glPopMatrix();
	}
	

}
