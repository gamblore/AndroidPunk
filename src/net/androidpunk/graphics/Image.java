package net.androidpunk.graphics;

import java.util.Map;

import net.androidpunk.FP;
import net.androidpunk.Graphic;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

public class Image extends Graphic {
	
	public static final String TAG = "Image";
	
	/**
	 * Rotation of the image, in degrees.
	 */
	public double angle = 0;

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

	// Source and buffer information.
	protected Bitmap mSource;
	protected Rect mSourceRect;
	protected Bitmap mBuffer;
	protected Rect mBufferRect;
	private Bitmap mBitmap;
	
	// Color and alpha information.
	private int mAlpha = 255;
	private int mColor = 0xffffffff;
	protected ColorFilter mTint;
	private ColorFilter mColorTransform;
	private Matrix mMatrix = FP.matrix;

	// Flipped image information.
	protected boolean mFlipped;
	private Bitmap mFlip;
	private Map<String, Integer> mFlips;
	
	private static final Paint PAINT = new Paint();
	public Image(Bitmap source) {
		this(source, null);
	}
	/**
	 * Constructor.
	 * @param	source		Source image.
	 * @param	clipRect	Optional rectangle defining area of the source image to draw.
	 */
	public Image(Bitmap source, Rect clipRect) {
		mSource = source;
		if (mSource == null) {
			Log.e(TAG, "Invalid source image.");
			return;
		}
		
		mSourceRect = new Rect(0,0,mSource.getWidth(),mSource.getHeight());
		if (clipRect != null){
			if (clipRect.width() > 0)
				clipRect.right = mSourceRect.width();
			if (clipRect.height() > 0)
				clipRect.bottom = mSourceRect.height();
			mSourceRect = clipRect;
		}
		
		createBuffer();
		updateBuffer();
	}
	
	/** @private Creates the buffer. */
	protected void createBuffer() {
		mBuffer = Bitmap.createBitmap(mSourceRect.width(), mSourceRect.height(), Config.ARGB_8888);
		mBufferRect = new Rect(0, 0, mBuffer.getWidth(), mBuffer.getHeight());
		mBitmap = mBuffer;
	}

	/** @private Renders the image. */
	public void render(Bitmap target, Point point, Point camera) {
		// quit if no graphic is assigned
		if (mBuffer == null) 
			return;

		Canvas c = new Canvas(target);
		// determine drawing location
		mPoint.x = (int)(point.x + x - camera.x * scrollX);
		mPoint.y = (int)(point.y + y - camera.y * scrollY);

		// render without transformation
		if (angle == 0 && scaleX * scale == 1 && scaleY * scale == 1) {
			c.drawBitmap(mBitmap, point.x, point.y, null);
			return;
		}
		
		
		// a(0) c(1) tx(2)
		// b(3) d(4) ty(5)
		// u(6) v(7) w(8)
		mMatrix.reset();
		mMatrix.getValues(FP.MATRIX_VALUES);
		
		// render with transformation
		FP.MATRIX_VALUES[3] = FP.MATRIX_VALUES[1] = 0;
		FP.MATRIX_VALUES[0] = scaleX * scale;
		FP.MATRIX_VALUES[4] = scaleY * scale;
		FP.MATRIX_VALUES[2] = -originX * FP.MATRIX_VALUES[0];
		FP.MATRIX_VALUES[5] = -originY * FP.MATRIX_VALUES[4];
		mMatrix.setValues(FP.MATRIX_VALUES);
		if (angle != 0) 
			mMatrix.postRotate((float)(angle*FP.RAD));
		
		mMatrix.getValues(FP.MATRIX_VALUES);
		FP.MATRIX_VALUES[2] += originX * mPoint.x;
		FP.MATRIX_VALUES[5] += originY * mPoint.y;
		c.drawBitmap(mBitmap, mMatrix, null);
	}
	
	/**
	 * Creates a new white rectangle Image.
	 * @param	width		Width of the rectangle.
	 * @param	height		Height of the rectangle..
	 * @return	A new Image object.
	 */
	public static Image createRect(int width, int height) {
		return createRect(width, height, 0xffffffff);
	}
	/**
	 * Creates a new rectangle Image.
	 * @param	width		Width of the rectangle.
	 * @param	height		Height of the rectangle.
	 * @param	color		Color of the rectangle.
	 * @return	A new Image object.
	 */
	public static Image createRect(int width, int height, int color) {
		Bitmap source = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas c = new Canvas(source);
		PAINT.setColor(color);
		PAINT.setStyle(Style.FILL);
		c.drawRect(0, 0, width, height, PAINT);
		return new Image(source);
	}
	
	/**
	 * Creates a new white circle Image.
	 * @param	radius		Radius of the circle.
	 * @return	A new Circle object.
	 */
	public static Image createCircle(int radius) {
		return createCircle(radius, 0xffffffff);
	}
	/**
	 * Creates a new circle Image.
	 * @param	radius		Radius of the circle.
	 * @param	color		Color of the circle.
	 * @return	A new Circle object.
	 */
	public static Image createCircle(int radius, int color) {
		Bitmap source = Bitmap.createBitmap(radius * 2, radius *2, Config.ARGB_8888);
		Canvas c = new Canvas(source);
		c.drawColor(0);
		PAINT.setColor(color);
		PAINT.setStyle(Style.FILL);
		c.drawCircle(radius, radius, radius, PAINT);
		return new Image(source);
	}
	
	/**
	 * Updates the image buffer.
	 */
	public void updateBuffer() {
		updateBuffer(false);
	}
	
	/**
	 * Updates the image buffer.
	 */
	public void updateBuffer(boolean clearBefore) {
		if (mSource == null)
			return;
		Canvas c = new Canvas(mBuffer);
		if (clearBefore) {
			PAINT.setColor(0xff000000);
			c.drawRect(mBufferRect, PAINT);
		}
		if (mTint != null)
			PAINT.setColorFilter(mTint);
		c.drawBitmap(mSource, mSourceRect.left, mSourceRect.top, PAINT);
	}
	
	/**
	 * Clears the image buffer.
	 */
	public void clear() {
		Canvas c = new Canvas(mBuffer);
		c.drawColor(0xff000000);
	}
	
	/**
	 * The tinted color of the Image. Use 0xFFFFFFFF to draw the Image normally.
	 */
	public int getColor() { return mColor; }
	/**
	 * The tinted color of the Image. Use 0xFFFFFFFF to draw the Image normally.
	 */
	public void setColor(int value) {
		if (mColor == value) 
			return;
		
		mColor = value;
		float matrix[] = new float[20];
		//red
		matrix[0] = Color.red(value) / 255f;
		//green
		matrix[6] = Color.green(value) / 255f;
		//blue
		matrix[12] = Color.blue(value) / 255f;
		//alpha
		matrix[18] = Color.alpha(value) / 255f;
		
		mTint = new ColorMatrixColorFilter(matrix);
		updateBuffer();
	}
	
	/**
	 * If you want to draw the Image horizontally flipped. This is
	 * faster than setting scaleX to -1 if your image isn't transformed.
	 */
	public boolean getFlipped() { return mFlipped; }
	
	/**
	 * If you want to draw the Image horizontally flipped. This is
	 * faster than setting scaleX to -1 if your image isn't transformed.
	 */
	public void setFlipped(boolean value) {
		if (mFlipped == value)
			return;
		mFlipped = value;
		Bitmap temp = mSource;
		
		if (!value || mFlip != null) {
			mSource = mFlip;
			mFlip = temp;
			updateBuffer();
		}
		mSource = Bitmap.createBitmap(mSource.getWidth(), mSource.getHeight(), Config.ARGB_8888);
		mFlip = temp;
		
		FP.matrix.reset();
		FP.matrix.getValues(FP.MATRIX_VALUES);
		FP.MATRIX_VALUES[0] = -1;
		FP.MATRIX_VALUES[2] = mSource.getWidth();
		FP.matrix.setValues(FP.MATRIX_VALUES);
		Canvas c = new Canvas(mSource);
		c.drawBitmap(temp, FP.matrix, null);
		updateBuffer();
	}
	
	/**
	 * Centers the Image's originX/Y to its center.
	 */
	public void centerOrigin() {
		originX = mBufferRect.width() / 2;
		originY = mBufferRect.height() / 2;
	}

	/**
	 * Centers the Image's originX/Y to its center, and negates the offset by the same amount.
	 */
	public void centerOO() {
		x += originX;
		y += originY;
		centerOrigin();
		x -= originX;
		y -= originY;
	}
	
	/**
	 * Width of the image.
	 */
	public int getWidth() { return mBufferRect.width(); }

	/**
	 * Height of the image.
	 */
	public int getHeight() { return mBufferRect.height(); }
	
	/**
	 * The scaled width of the image.
	 */
	public int getScaledWidth() { return (int)(mBufferRect.width() * scaleX * scale); }

	/**
	 * The scaled height of the image.
	 */
	public int getScaledHeight() { return (int)(mBufferRect.height() * scaleY * scale); }
	
	/**
	 * Clipping rectangle for the image.
	 */
	public Rect getClipRect() { return mSourceRect; }

	/** @private Source BitmapData image. */
	protected Bitmap getSource() { return mSource; }
}
