package net.androidpunk.masks;

import net.androidpunk.FP;
import net.androidpunk.Mask;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

public class PixelMask extends Hitbox {
	/**
	 * Alpha threshold of the bitmap used for collision.
	 */
	public final int threshold = 1;
	
	protected Bitmap mData;
	private Rect mRect = FP.rect;
	private Point mPoint = FP.point;
	private Point mPoint2 = FP.point2;
	
	public PixelMask(Bitmap source) {
		this(source,0,0);
	}
	
	public PixelMask(Bitmap source, int x) {
		this(source,x,0);
	}
	
	/**
	 * Constructor.
	 * @param	source		The image to use as a mask.
	 * @param	x			X offset of the mask.
	 * @param	y			Y offset of the mask.
	 */
	public PixelMask(Bitmap source, int x, int y) {
		super(source.getWidth(),source.getHeight(), x, y);
		// fetch mask data
		mData = source;
		// set callback functions
		mCheck.put(Mask.class, new CollideCallback() {
            
            @Override
            public boolean collide(Mask m) {
                return collideMask((Mask)m);
            }
        });
		mCheck.put(PixelMask.class, new CollideCallback() {
            
            @Override
            public boolean collide(Mask m) {
                return collidePixelMask((PixelMask)m);
            }
        });
		mCheck.put(Hitbox.class, new CollideCallback() {
            
            @Override
            public boolean collide(Mask m) {
                return collideHitbox((Hitbox)m);
            }
        });
	}
	
	/** @private Collide against an Entity. */
	private boolean collideMask(Mask other) {
		mPoint.x = parent.x + mX;
		mPoint.y = parent.y + mY;
		mRect.left = other.parent.x - other.parent.originX;
		mRect.top = other.parent.y - other.parent.originY;
		mRect.right = mRect.left + other.parent.width;
		mRect.bottom = mRect.top + other.parent.height;
		return hitTest(mData, mPoint, threshold, mRect);
	}
	
	/** @private Collide against a Hitbox. */
	private boolean collideHitbox(Hitbox other) {
		mPoint.x = parent.x + mX;
		mPoint.y = parent.y + mY;
		mRect.left = other.parent.x + other.mX;
		mRect.top = other.parent.y + other.mY;
		mRect.right = mRect.left + other.getWidth();
		mRect.bottom = mRect.top + other.getHeight();
		return hitTest(mData, mPoint, threshold, mRect);
	}
	
	/** @private Collide against a Pixelmask. */
	private boolean collidePixelMask(PixelMask other) {
		mPoint.x = parent.x + mX;
		mPoint.y = parent.y + mY;
		mPoint2.x = other.parent.x + other.mX;
		mPoint2.y = other.parent.y + other.mY;
		return hitTest(mData, mPoint, threshold, other.mData, mPoint2, other.threshold);
	}
}
