package net.androidpunk.graphics;

import net.androidpunk.FP;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.util.Log;

/**
 * Special Image object that can display blocks of tiles.
 */
public class TiledImage extends Image {

	private static final String TAG = "TiledImage"; 
	
	// Drawing information.
	private static final Canvas mCanvas = FP.canvas;
	private static final Paint mPaint = FP.paint;
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
	 */
	public TiledImage(Bitmap texture, int width, int height) {
		this(texture, width, height, null);
	}
	
	/**
	 * Constructs the TiledImage.
	 * @param	texture		Source texture.
	 * @param	width		The width of the image (the texture will be drawn to fill this area).
	 * @param	height		The height of the image (the texture will be drawn to fill this area).
	 * @param	clipRect	An area of the source texture to use (eg. a tile from a tileset).
	 */
	public TiledImage(Bitmap texture, int width, int height, Rect clipRect) {
		super(texture, clipRect);
		mWidth = width;
		mHeight = height;
		
		if (clipRect != null) {
			mTexture = Bitmap.createBitmap(clipRect.width(), clipRect.height(), Config.ARGB_8888);
			mCanvas.setBitmap(mTexture);
			Rect r = FP.rect;
			r.set(0, 0, mTexture.getWidth(), mTexture.getHeight());
			mCanvas.drawBitmap(texture, clipRect, r, null);
			mBitmapShader = new BitmapShader(mTexture, TileMode.REPEAT, TileMode.REPEAT);
		} else {
			mTexture = texture;
			mBitmapShader = new BitmapShader(texture, TileMode.REPEAT, TileMode.REPEAT);
		}
		
		createBuffer();
		updateBuffer();
	}
	
	/** @return 
	 * @private Creates the buffer. */
	@Override 
	protected void createBuffer() {
		if (mPaint == null) {
			return;
		}
		if (mWidth == 0) 
			mWidth = mSourceRect.width();
		if (mHeight == 0) 
			mHeight = mSourceRect.height();
		if (mBuffer != null) {
			mBuffer.recycle();
		}
		mBuffer = Bitmap.createBitmap(mWidth, mHeight, Config.ARGB_8888);
		mBufferRect = new Rect(0, 0, mBuffer.getWidth(), mBuffer.getHeight());
	}
	
	/** @private Updates the buffer. */
	@Override 
	public void updateBuffer() {
		updateBuffer(true);
	}
	
	/** @private Updates the buffer. */
	@Override 
	public void updateBuffer(boolean clearBefore) {
		if (mSource == null)
			return;
		if (mPaint == null) {
			return;
		}
		mPaint.reset();
		mPaint.setShader(mBitmapShader);
		/*
		if (mTexture == null) {
			mTexture = Bitmap.createBitmap(mSourceRect.width(), mSourceRect.height(), Config.ARGB_8888);
			mCanvas.setBitmap(mTexture);
			mCanvas.drawBitmap(mSource, 0, 0, null);
			mBitmapShader = new BitmapShader(mTexture, TileMode.REPEAT, TileMode.REPEAT);
		}
		*/
		mBuffer.eraseColor(0);
		mCanvas.setBitmap(mBuffer);
		if (mOffsetX != 0 || mOffsetY != 0) {
			int x = Math.round(mOffsetX);
			int y = Math.round(mOffsetY);
			
			mCanvas.translate(-x, -y);
			mCanvas.drawRect(mBufferRect, mPaint);
			mCanvas.translate(x, y);
		}
		else {
			//Log.d(TAG, String.format("Blitting texture size %dx%d", mBufferRect.width(), mBufferRect.height()));
			
			mCanvas.drawRect(mBufferRect, mPaint);
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
