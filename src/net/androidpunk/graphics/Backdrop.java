package net.androidpunk.graphics;

import net.androidpunk.FP;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Point;

/**
 * A background texture that can be repeated horizontally and vertically
 * when drawn. Really useful for parallax backgrounds, textures, etc.
 */
public class Backdrop extends CanvasGraphic {
	
	// Backdrop information.
	private Bitmap mTexture;
	private int mTextWidth;
	private int mTextHeight;
	private boolean mRepeatX;
	private boolean mRepeatY;
	private int mX;
	private int mY;
	
	/**
	 * Constructor. Repeats horizontally and vertically.
	 * @param	texture		Source texture.
	 */
	public Backdrop(Bitmap texture)  {
		this(texture, true, true);
	}

	/**
	 * Constructor.
	 * @param	texture		Source texture.
	 * @param	repeatX		Repeat horizontally.
	 * @param	repeatY		Repeat vertically.
	 */
	public Backdrop(Bitmap texture, boolean repeatX, boolean repeatY)  {
		super(FP.width * (repeatX ? 1 : 0) +  ( texture != null ? texture.getWidth() : FP.width),
				FP.height * (repeatY ? 1 : 0) + (texture != null ? texture.getHeight() : FP.height));
		if (texture == null)
			mTexture = Bitmap.createBitmap(FP.width, FP.height, Config.ARGB_8888);
		else 
			mTexture = texture;
		
		mRepeatX = repeatX;
		mRepeatY = repeatY;
		mTextWidth = mTexture.getWidth();
		mTextHeight = mTexture.getHeight();
		FP.rect.left = FP.rect.top = 0;
		FP.rect.right = mWidth;
		FP.rect.bottom = mHeight;
		fillTexture(FP.rect, mTexture);
	}
	
	/** @private Renders the Backdrop. */
	@Override 
	public void render(Bitmap target, Point point, Point camera) {
		mPoint.x = (int)(point.x + x - camera.x * scrollX);
		mPoint.y = (int)(point.y + y - camera.y * scrollY);

		if (mRepeatX) {
			mPoint.x %= mTextWidth;
			if (mPoint.x > 0) 
				mPoint.x -= mTextWidth;
		}

		if (mRepeatY) {
			mPoint.y %= mTextHeight;
			if (mPoint.y > 0) 
				mPoint.y -= mTextHeight;
		}

		mX = (int)x; mY = (int)y;
		x = y = 0;
		super.render(target, mPoint, FP.zero);
		x = mX; y = mY;
	}
}
