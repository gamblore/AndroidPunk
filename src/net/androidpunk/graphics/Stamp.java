package net.androidpunk.graphics;

import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.FP;
import net.androidpunk.Graphic;
import net.androidpunk.android.OpenGLSystem;
import net.androidpunk.android.OpenGLSystem.OpenGLRunnable;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

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
		
		OpenGLSystem.postRunnable(new OpenGLRunnable() {
			public void run(GL10 gl) {
				mTexture.createTexture(gl, mSource);
			}
		});
	}
	
	/** @private Renders the Graphic. */
	@Override 
	public void render(Bitmap target, Point point, Point camera) {
		if (mSource == null)
			return;
		mPoint.x = (int)(point.x + x - camera.x * scrollX);
		mPoint.y = (int)(point.y + y - camera.y * scrollY);
		//FP.canvas.setBitmap(target);
		mRect.set(mPoint.x, mPoint.y, mSourceRect.width(), mSourceRect.height());
		//FP.canvas.drawBitmap(mSource, mSourceRect, mRect, null);
		GL10 gl = OpenGLSystem.getGL();
		OpenGLSystem.drawTexture(gl, mRect.left, mRect.top, mRect.width(), mRect.height(), mTexture);
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
