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
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

public class Image extends Graphic {
	
	public static final String TAG = "Image";
	
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

	// Source and buffer information.
	protected Bitmap mSource;
	protected Rect mSourceRect;
	protected Bitmap mBuffer;
	protected Rect mBufferRect;
	
	// Color and alpha information.
	private int mAlpha = 255;
	private int mColor = 0xffffffff;
	protected ColorFilter mTint;
	private Rect mRect = FP.rect;
	private Matrix mMatrix = FP.matrix;
	private final Canvas mCanvas = FP.canvas;
	private Paint mPaint = FP.paint;


	// Flipped image information.
	protected boolean mFlipped;
	private Bitmap mFlip;
	
	private final float matrix[] = new float[20];
	/**
	 * Constructor with a clip rect of the whole image.
	 * @param	source		Source image.
	 */
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
		
		if (clipRect != null){
			if (clipRect.width() == 0)
				clipRect.right = mSource.getWidth();
			if (clipRect.height() == 0)
				clipRect.bottom = mSource.getHeight();
			mSourceRect = clipRect;
		} else {
			mSourceRect = new Rect(0, 0, mSource.getWidth(), mSource.getHeight());
		}
		
		createBuffer();
		updateBuffer();
	}
	
	/** @private Creates the buffer. */
	protected void createBuffer() {
		mBuffer = Bitmap.createBitmap(mSourceRect.width(), mSourceRect.height(), Config.ARGB_8888);
		mBufferRect = new Rect(0, 0, mBuffer.getWidth(), mBuffer.getHeight());
		centerOrigin();
	}

	/** @private Renders the image. */
	public void render(Bitmap target, Point point, Point camera) {
		// quit if no graphic is assigned
		if (mBuffer == null) 
			return;

		mCanvas.setBitmap(target);
		// determine drawing location
		mPoint.x = (int)(point.x + x - camera.x * scrollX);
		mPoint.y = (int)(point.y + y - camera.y * scrollY);

		// render without transformation
		/*
		if (angle == 0 && scaleX * scale == 1 && scaleY * scale == 1) {
			mRect.set(mPoint.x, mPoint.y, mPoint.x + mBufferRect.width(), mPoint.y + mBufferRect.height());
			Log.d(TAG, String.format("Drawing buffer from %s to %s", mBufferRect.toShortString(), mRect.toShortString()));
			mCanvas.drawBitmap(mBuffer, mBufferRect, mRect, null);
			return;
		}
		*/
		
		setMatrix();
		/*
		// a(0) c(1) tx(2)
		// b(3) d(4) ty(5)
		// u(6) v(7) w(8)
		mMatrix.reset();
		float sX = scaleX * scale;
		float sY = scaleY * scale;
		mMatrix.postScale(sX, sY);
		mMatrix.postTranslate(-originX * sX, -originY * sY);
		//mMatrix.getValues(FP.MATRIX_VALUES);
		
		// render with transformation
		//FP.MATRIX_VALUES[3] = FP.MATRIX_VALUES[1] = 0;
		//FP.MATRIX_VALUES[0] = scaleX * scale;
		//FP.MATRIX_VALUES[4] = scaleY * scale;
		//FP.MATRIX_VALUES[2] = -originX * FP.MATRIX_VALUES[0];
		//FP.MATRIX_VALUES[5] = -originY * FP.MATRIX_VALUES[4];
		if (angle != 0) {
			//mMatrix.setValues(FP.MATRIX_VALUES);
			mMatrix.postRotate((float)(angle*FP.RAD));
			//mMatrix.getValues(FP.MATRIX_VALUES);
		}
		mMatrix.postTranslate(originX + mPoint.x, originY + mPoint.y);
		//FP.MATRIX_VALUES[2] += originX + mPoint.x;
		//FP.MATRIX_VALUES[5] += originY + mPoint.y;
		//mMatrix.setValues(FP.MATRIX_VALUES);
		*/
		
		if (mTint != null) {
			mPaint.reset();
			mPaint.setColorFilter(mTint);
			mCanvas.drawBitmap(mBuffer, mMatrix, mPaint);
		} else {
			mCanvas.drawBitmap(mBuffer, mMatrix, null);
		}
		
		//mCanvas.drawBitmap(mSource, 0, 0, null);
	}
	
	private void setMatrix() {
		mMatrix.reset();
		float sX = scaleX * scale;
		float sY = scaleY * scale;
		mMatrix.postScale(sX, sY);
		mMatrix.postTranslate(-originX * sX, -originY * sY);
		if (angle != 0) {
			mMatrix.postRotate(angle);
		}
		mMatrix.postTranslate(originX + mPoint.x, originY + mPoint.y);
	}
	
	/**
	 * Updates the image buffer.
	 */
	public void updateBuffer() {
		updateBuffer(true);
	}
	
	/**
	 * Updates the image buffer.
	 */
	public void updateBuffer(boolean clearBefore) {
		
		mPaint.reset();
		if (mSource == null)
			return;
		
		
		
		if (clearBefore) {
			mBuffer.eraseColor(0);
		}
		if (mTint != null)
			mPaint.setColorFilter(mTint);
		
		mCanvas.setBitmap(mBuffer);
		if (mSourceRect.equals(mBufferRect)) {
			mCanvas.drawBitmap(mSource, 0, 0, mPaint);
		} else {
			mCanvas.drawBitmap(mSource, mSourceRect, mBufferRect, mPaint);
		}
		
		//Log.d(TAG, "Image "+ mSourceRect.toShortString() + " rendering into " + mBufferRect.toShortString());
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
		FP.paint.setColor(color);
		FP.paint.setStyle(Style.FILL);
		c.drawRect(0, 0, width, height, FP.paint);
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
		FP.paint.setColor(color);
		FP.paint.setStyle(Style.FILL);
		c.drawCircle(radius, radius, radius, FP.paint);
		return new Image(source);
	}
	
	/**
	 * Clears the image buffer.
	 */
	public void clear() {
		mBuffer.eraseColor(0xff000000);
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
		
		//red
		matrix[0] = Color.red(value) / 255f;
		//green
		matrix[6] = Color.green(value) / 255f;
		//blue
		matrix[12] = Color.blue(value) / 255f;
		//alpha
		matrix[18] = Color.alpha(value) / 255f;
		
		mTint = new ColorMatrixColorFilter(matrix);
		//updateBuffer();
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
			return;
		}
		mSource = Bitmap.createBitmap(mSource.getWidth(), mSource.getHeight(), Config.ARGB_8888);
		mFlip = temp;
		
		FP.matrix.reset();
		FP.matrix.setScale(-1, 1);
		FP.matrix.postTranslate(mSource.getWidth(), 0);
		
		
		//FP.matrix.getValues(FP.MATRIX_VALUES);
		//FP.MATRIX_VALUES[0] = -1;
		//FP.MATRIX_VALUES[2] = mSource.getWidth();
		//FP.matrix.setValues(FP.MATRIX_VALUES);
		
		mCanvas.setBitmap(mSource);
		
		mCanvas.drawBitmap(temp, FP.matrix, null);
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
