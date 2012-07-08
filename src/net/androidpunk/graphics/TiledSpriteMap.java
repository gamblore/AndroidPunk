package net.androidpunk.graphics;

import net.androidpunk.FP;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Special Spritemap object that can display blocks of animated sprites.
 */
public class TiledSpriteMap extends SpriteMap {

	private Canvas mCanvas = FP.canvas;
	private Paint mPaint = FP.paint;
	private Rect mRect = FP.rect;
	private int mImageWidth;
	private int mImageHeight;
	private int mOffsetX = 0;
	private int mOffsetY = 0;
	
	public TiledSpriteMap(Bitmap source) {
		this(source, 0, 0, 0, 0, null);
	}
	public TiledSpriteMap(Bitmap source, int frameWidth, int frameHeight) {
		this(source, frameWidth, frameHeight, 0, 0, null);
	}
	
	public TiledSpriteMap(Bitmap source, int frameWidth, int frameHeight, int width, int height) {
		this(source, frameWidth, frameHeight, width, height, null);
	}
	/**
	 * Constructs the tiled spritemap.
	 * @param	source			Source image.
	 * @param	frameWidth		Frame width.
	 * @param	frameHeight		Frame height.	
	 * @param	width			Width of the block to render.
	 * @param	height			Height of the block to render.
	 * @param	callback		Optional callback function for animation end.
	 */
	public TiledSpriteMap(Bitmap source, int frameWidth, int frameHeight, int width, int height, OnAnimationEndCallback callback) {
		super(source, frameWidth, frameHeight, callback);
		mImageWidth = width;
		mImageHeight = height;
	}
	
	/** @private Creates the buffer. */
	@Override 
	protected void createBuffer() {
		if (mImageWidth == 0) 
			mImageWidth = mSourceRect.width();
		if (mImageHeight == 0) 
			mImageHeight = mSourceRect.height();
		mBuffer = Bitmap.createBitmap(mImageWidth, mImageHeight, Config.ARGB_8888);
		mBufferRect = new Rect(0, 0, mBuffer.getWidth(), mBuffer.getHeight());
	}
	
	/** @private Updates the buffer. */
	public void updateBuffer() {
		updateBuffer(true);
	}
	
	/** @private Updates the buffer. */
	@Override
	public void updateBuffer(boolean clearBefore) {
		// get position of the current frame
		mPaint.reset();
		
		mRect.offsetTo(mRect.width() * mFrame, (int)(mRect.left / mWidth) * mRect.height());
		mRect.offsetTo(mRect.left % mWidth, 0);
		if (mFlipped)
			mRect.offsetTo((mWidth - mRect.width()) - mRect.left, 0);

		// render it repeated to the buffer
		int xx = mOffsetX % mImageWidth;
		int yy = mOffsetY % mImageHeight;
		if (xx >= 0) 
			xx -= mImageWidth;
		if (yy >= 0) 
			yy -= mImageHeight;
		FP.point.x = xx;
		FP.point.y = yy;
		while (FP.point.y < mImageHeight) {
			while (FP.point.x < mImageWidth) {
				mCanvas.setBitmap(mBuffer);
				mRect.set(FP.point.x, FP.point.y, mSourceRect.width(), mSourceRect.height());
				mCanvas.drawBitmap(mSource, mSourceRect, mRect, mPaint);
				FP.point.x += mSourceRect.width();
			}
			FP.point.x = xx;
			FP.point.y += mSourceRect.height();
		}
	}
	
	/**
	 * The x-offset of the texture.
	 */
	public int getOffsetX() { return mOffsetX; }
	public void setOffsetX(int value) {
		if (mOffsetX == value) 
			return;
		mOffsetX = value;
		updateBuffer();
	}
	
	/**
	 * The y-offset of the texture.
	 */
	public int getOffsetY() { return mOffsetY; }
	public void setOffsetY(int value) {
		if (mOffsetY == value) 
			return;
		mOffsetY = value;
		updateBuffer();
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
		updateBuffer();
	}
}
