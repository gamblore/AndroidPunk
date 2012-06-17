package net.androidpunk.graphics;

import net.androidpunk.FP;
import net.androidpunk.Graphic;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * A simple non-transformed, non-animated graphic.
 */
public class Stamp extends Graphic {

	// Stamp information.
	private Bitmap mSource;
	private Rect mSourceRect = new Rect();
	
	private Rect mRect = FP.rect;

	public Stamp(Bitmap source) {
		this(source, 0, 0);
	}
	
	/**
	 * Constructor.
	 * @param	source		Source image.
	 * @param	x			X offset.
	 * @param	y			Y offset.
	 */
	public Stamp(Bitmap source, int x, int y) {
		// set the origin
		this.x = x;
		this.y = y;

		// set the graphic
		if (source == null)
			return;
		mSource = source;
		if (mSource != null) 
			mSourceRect.set(0, 0, mSource.getWidth(), mSource.getHeight());
	}
	
	/** @private Renders the Graphic. */
	@Override 
	public void render(Bitmap target, Point point, Point camera) {
		if (mSource == null)
			return;
		mPoint.x = (int)(point.x + x - camera.x * scrollX);
		mPoint.y = (int)(point.y + y - camera.y * scrollY);
		FP.canvas.setBitmap(target);
		mRect.set(mPoint.x, mPoint.y, mSourceRect.width(), mSourceRect.height());
		FP.canvas.drawBitmap(mSource, mSourceRect, mRect, null);
	}
	
	/**
	 * Source BitmapData image.
	 */
	public Bitmap getSource() { return mSource; }
	
	/**
	 * Source BitmapData image.
	 */
	public void setSource(Bitmap value) {
		mSource = value;
		if (mSource != null)
			mSourceRect.set(0, 0, mSource.getWidth(), mSource.getHeight());
	}
}
