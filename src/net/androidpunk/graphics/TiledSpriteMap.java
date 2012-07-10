package net.androidpunk.graphics;

import net.androidpunk.FP;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

/**
 * Special Spritemap object that can display blocks of animated sprites.
 */
public class TiledSpriteMap extends SpriteMap {

	private static final String TAG = "TiledSpriteMap";
	private final Canvas mCanvas = new Canvas();
	private Paint mPaint = FP.paint;
	private Rect mRect = FP.rect;
	private int mImageWidth;
	private int mImageHeight;
	private int mOffsetX = 0;
	private int mOffsetY = 0;
	
	
	/**
	 * Constructs the tiled spritemap.
	 * @param	source			Source image.
	 */
	public TiledSpriteMap(Bitmap source) {
		this(source, 0, 0, 0, 0, null);
	}
	
	/**
	 * Constructs the tiled spritemap.
	 * @param	source			Source image.
	 * @param	frameWidth		Frame width.
	 * @param	frameHeight		Frame height.
	 */
	public TiledSpriteMap(Bitmap source, int frameWidth, int frameHeight) {
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
	 * @param	callback		callback function for animation end.
	 */
	public TiledSpriteMap(Bitmap source, int frameWidth, int frameHeight, int width, int height, OnAnimationEndCallback callback) {
		super(source, frameWidth, frameHeight, callback);
		mImageWidth = width;
		mImageHeight = height;
		
		createBuffer();
		updateBuffer();
	}
	
	/** @private Creates the buffer. */
	@Override 
	protected void createBuffer() {
		if (mImageWidth == 0) 
			mImageWidth = mSourceRect.width();
		if (mImageHeight == 0) 
			mImageHeight = mSourceRect.height();
		if (mBuffer != null) {
			mBuffer.recycle();
		}
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
		if (mPaint == null) {
			return;
		}
		mPaint.reset();
		int newX = mSourceRect.width() * mFrame;
		mSourceRect.offsetTo(newX % mSource.getWidth(), (int)((int)(newX / mSource.getWidth()) * mSourceRect.height()));
		
		// render it repeated to the buffer
		int xx = mOffsetX % mImageWidth;
		int yy = mOffsetY % mImageHeight;
		if (xx > 0) 
			xx -= mImageWidth;
		if (yy > 0) 
			yy -= mImageHeight;
		FP.point.x = xx;
		FP.point.y = yy;
		mRect.set(FP.point.x, FP.point.y, FP.point.x + mSourceRect.width(), FP.point.y + mSourceRect.height());
		mBuffer.eraseColor(0);
		mCanvas.setBitmap(mBuffer);
		while (FP.point.y < mImageHeight) {
			while (FP.point.x < mImageWidth) {
				mCanvas.drawBitmap(mSource, mSourceRect, mRect, null);
				FP.point.x += mSourceRect.width();
				mRect.offset(mSourceRect.width(), 0);
			}
			FP.point.x = xx;
			FP.point.y += mSourceRect.height();
			mRect.offset(xx - FP.point.x, mSourceRect.height());
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
