package net.androidpunk.graphics;

import net.androidpunk.FP;
import net.androidpunk.Graphic;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Paint.Style;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;

/**
 * A  multi-purpose drawing canvas, for comatability with flash library.
 */
public class CanvasGraphic extends Graphic {

	
	// Buffer information.
	protected Bitmap mBuffer;
	protected int mWidth;
	protected int mHeight;
	protected int mMaxWidth = 4000;
	protected int mMaxHeight = 4000;

	// Color tinting information.
	private int mColor;
	private ColorFilter mTint;

	// Global objects.
	private Rect mRect = new Rect();
	private Canvas mCanvas = FP.canvas;
	private Paint mPaint = FP.paint;

	/**
	 * Constructor.
	 * @param	width		Width of the canvas.
	 * @param	height		Height of the canvas.
	 */
	public CanvasGraphic(int width, int height) {
		mWidth = width;
		mHeight = height;
		mBuffer = Bitmap.createBitmap(width, height, Config.ARGB_8888);
	}
	
	/** @private Renders the canvas. */
	@Override 
	public void render(Bitmap target, Point point, Point camera) {
		// determine drawing location
		mPoint.x = (int)(point.x + x - camera.x * scrollX);
		mPoint.y = (int)(point.y + y - camera.y * scrollY);

		mPaint.reset();
		if (mTint != null) {
			mPaint.setColorFilter(mTint);
		}
		mCanvas.setBitmap(target);
		mCanvas.drawBitmap(mBuffer, mPoint.x, mPoint.y, mPaint);
	}
	
	/**
	 * Draws to the canvas the whole bitmap.
	 * @param	x			X position to draw.
	 * @param	y			Y position to draw.
	 * @param	source		Source BitmapData.
	 */
	public void draw(int x, int y, Bitmap source) {
		draw(x,y,source,null);
	}
	/**
	 * Draws to the canvas.
	 * @param	x			X position to draw.
	 * @param	y			Y position to draw.
	 * @param	source		Source BitmapData.
	 * @param	src		Optional area of the source image to draw from. If null, the entire BitmapData will be drawn.
	 */
	public void draw(int x, int y, Bitmap source, Rect src) {
		mCanvas.setBitmap(mBuffer);
		if (src != null) {
			mRect.set(x, y, x + src.width(), y + src.height());
			mCanvas.drawBitmap(source, src, mRect, null);
		} else {
			mCanvas.drawBitmap(source, x, y, null);
		}
	}
	
	/**
	 * Fills the rectangular area of the canvas. The previous contents of that area are completely removed and replaced with black.
	 * @param	rect		Fill rectangle.
	 */
	public void fill(Rect rect) {
		fill(rect, 0xff000000);
	}
	
	/**
	 * Fills the rectangular area of the canvas. The previous contents of that area are completely removed.
	 * @param	rect		Fill rectangle.
	 * @param	color		Fill color.
	 */
	public void fill(Rect rect, int color) {
		mCanvas.setBitmap(mBuffer);
		mPaint.reset();
		mPaint.setColor(color);
		mPaint.setStyle(Style.FILL);
		mCanvas.drawRect(rect, mPaint);
	}

	/**
	 * Fills the rectangle area of the canvas with the texture.
	 * @param	rect		Fill rectangle.
	 * @param	texture		Fill texture.
	 */
	public void fillTexture(Rect rect, Bitmap texture) {
		mCanvas.setBitmap(mBuffer);
		mPaint.reset();
		mPaint.setShader(new BitmapShader(texture, TileMode.REPEAT, TileMode.REPEAT));
		
		mCanvas.drawRect(rect, mPaint);
	}
	
	/**
	 * Draws the Graphic object to the canvas.
	 * @param	x			X position to draw.
	 * @param	y			Y position to draw.
	 * @param	source		Graphic to draw.
	 */
	public void drawGraphic(int x, int y, Graphic source) {
		mPoint.x = x;
		mPoint.y = y;
		source.render(mBuffer, mPoint, FP.zero);
	}
	
	/**
	 * The tinted color of the Canvas. Use 0xFFFFFF to draw the it normally.
	 */
	public int getColor() { return mColor; }
	public void setColor(int value) {
		if (mColor == value) 
			return;
		
		mColor = value;
		float matrix[] = new float[20];
		//red
		matrix[0] = Color.red(value) / 255f;
		//blue
		matrix[6] = Color.blue(value) / 255f;
		//green
		matrix[12] = Color.green(value) / 255f;
		//alpha
		matrix[18] = Color.alpha(value) / 255f;
		
		mTint = new ColorMatrixColorFilter(matrix);
	}
	
	/**
	 * Shifts the canvas' pixels by the offset.
	 * @param	x	Horizontal shift.
	 * @param	y	Vertical shift.
	 */
	public void shift(int x, int y) {
		drawGraphic(x, y, this);
	}
	
	/**
	 * Width of the canvas.
	 */
	public int getWidth() { return mWidth; }
	
	/**
	 * Height of the canvas.
	 */
	public int getHeigth() { return mHeight; }
}
