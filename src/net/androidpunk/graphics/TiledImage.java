package net.androidpunk.graphics;

import net.androidpunk.FP;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;

/**
 * Special Image object that can display blocks of tiles.
 */
public class TiledImage extends Image {

	
	// Drawing information.
	private Canvas mCanvas = FP.canvas;
	private Paint mPaint = FP.paint;
	private BitmapShader mBitmapShader;
	private Bitmap mTexture;
	private int mWidth;
	private int mHeight;
	private int mOffsetX = 0;
	private int mOffsetY = 0;

	/**
	 * Constructs the TiledImage.
	 * @param	texture		Source texture.
	 * @param	width		The width of the image (the texture will be drawn to fill this area).
	 * @param	height		The height of the image (the texture will be drawn to fill this area).
	 * @param	clipRect	An optional area of the source texture to use (eg. a tile from a tileset).
	 */
	public TiledImage(Bitmap texture, int width, int height, Rect clipRect) {
		super(texture, clipRect);
		mWidth = width;
		mHeight = height;
		
		mBitmapShader = new BitmapShader(texture, TileMode.REPEAT, TileMode.REPEAT);
		
	}
	
	/** @return 
	 * @private Creates the buffer. */
	@Override 
	protected void createBuffer() {
		if (mWidth == 0) 
			mWidth = mSourceRect.width();
		if (mHeight == 0) 
			mHeight = mSourceRect.height();
		mBuffer = Bitmap.createBitmap(mWidth, mHeight, Config.ARGB_8888);
		mBufferRect = new Rect(0, 0, mBuffer.getWidth(), mBuffer.getHeight());
	}
	
	/** @private Updates the buffer. */
	@Override 
	public void updateBuffer() {
		updateBuffer(false);
	}
	
	/** @private Updates the buffer. */
	@Override 
	public void updateBuffer(boolean clearBefore) {
		if (mSource == null)
			return;
		mPaint.reset();
		if (mTint != null)
			mPaint.setColorFilter(mTint);
		mPaint.setShader(mBitmapShader);
		if (mTexture == null) {
			mTexture = Bitmap.createBitmap(mSourceRect.width(), mSourceRect.height(), Config.ARGB_8888);
			mCanvas.setBitmap(mTexture);
			mCanvas.drawBitmap(mSource, 0, 0, null);
		}
		mCanvas.setBitmap(mBuffer);
		mCanvas.drawColor(0);
		if (mOffsetX != 0 || mOffsetY != 0) {
			float xoff = Math.round(mOffsetX);
			float yoff = Math.round(mOffsetY);
			mCanvas.translate(-xoff, -yoff);
			mCanvas.drawBitmap(mTexture, xoff, yoff, mPaint);
			mCanvas.translate(xoff, yoff);
		}
		else 
			mCanvas.drawBitmap(mTexture, 0, 0, mPaint);
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
