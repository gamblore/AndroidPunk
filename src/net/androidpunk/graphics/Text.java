package net.androidpunk.graphics;

import net.androidpunk.FP;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint;
import android.graphics.Paint.Align;

/**
 * Used for drawing text using embedded fonts.
 */
public class Text extends Image {

	/**
	 * The font size to assign to new Text objects.
	 */
	public static int size = 16;
	
	// Text information.
	private int mWidth;
	private int mHeight;
	private Paint mPaint;
	private String mText;
	private int mSize;
	
	
	public Text(String text, int x, int y) {
		this(text, x, y, 0, 0);
	}
	
	/**
	 * Constructor.
	 * @param	text		Text to display.
	 * @param	x			X offset.
	 * @param	y			Y offset.
	 * @param	width		Image width (leave as 0 to size to the starting text string).
	 * @param	height		Image height (leave as 0 to size to the starting text string).
	 */
	public Text(String text, int x, int y, int width, int height) {
		super(init(text, x, y, width, height));
		mPaint = new Paint();
		mPaint.setTextSize(Text.size);
		mPaint.setColor(0xffffffff);
		mPaint.setAntiAlias(true);
		mPaint.setTextAlign(Align.LEFT);
		mText = text;
		mSize = Text.size;
		updateBuffer();
		this.x = x;
		this.y = y;
	}
	
	private static Bitmap init( String text, int x, int y, int width, int height) {
		Paint p = FP.paint;
		p.reset();
		p.setTextSize(Text.size);
		if (width == 0)
			width = (int)(p.measureText(text));
		if (height == 0)
			height = (int)(Text.size);

		Bitmap source = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		return source;
	}
	
	/** @private Updates the drawing buffer. */
	public void updateBuffer() {
		updateBuffer(false);
	}
	
	/** @private Updates the drawing buffer. */
	@Override 
	public void updateBuffer(boolean clearBefore) {
		FP.canvas.setBitmap(getSource());
		mPaint.setTextSize(mSize);
		mWidth = (int)(mPaint.measureText(mText));
		mHeight = (int)(mSize);
		FP.canvas.drawColor(0);
		FP.canvas.drawText(mText, 0, 0, mPaint);
		super.updateBuffer(clearBefore);
	}
	
	/** @private Centers the Text's originX/Y to its center. */
	@Override 
	public void centerOrigin() {
		originX = mWidth / 2;
		originY = mHeight / 2;
	}
	
	/**
	 * Text string.
	 */
	public String getText() { return mText; }
	
	/**
	 * Text string.
	 */
	public void setText(String value) {
		if (mText == value) 
			return;
		mText = value;
		updateBuffer();
	}
	
	/**
	 * Font size.
	 */
	public int getSize() { return mSize; }
	
	/**
	 * Font size.
	 */
	public void setSize(int value) {
		if (mSize == value) 
			return;
		mSize = value;
		updateBuffer();
	}
	
	/**
	 * Width of the text image.
	 */
	@Override 
	public int getWidth() { return mWidth; }

	/**
	 * Height of the text image.
	 */
	@Override 
	public int getHeight() { return mHeight; }
}
