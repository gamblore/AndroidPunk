package net.androidpunk;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;

import net.androidpunk.masks.CollideCallback;
import net.androidpunk.masks.MaskList;

import java.util.HashMap;
import java.util.Map;

public class Mask {

    /**
     * The parent Entity of this mask.
     */
    public Entity parent;
    /**
     * The parent Masklist of the mask.
     */
    public MaskList list;
    
    public final Map<Class<?>, CollideCallback> mCheck = new HashMap<Class<?>, CollideCallback>();
    
    public Mask() {
        mCheck.put(Mask.class, new CollideCallback() {
            
            @Override
            public boolean collide(Mask m) {
                return collideMask(m);
            }
        });
        
        mCheck.put(MaskList.class, new CollideCallback() {
            
            @Override
            public boolean collide(Mask m) {
                return collideMaskList((MaskList) m);
            }
        });
    }
    
    protected boolean hitTest(Bitmap bm, Point firstPoint, int alphaThreshold, Point p) {
        int alpha = Color.alpha(bm.getPixel(p.x, p.y));
        return alpha > alphaThreshold;
    }
    
    protected boolean hitTest(Bitmap bm, Point firstPoint, int alphaThreshold, Rect r) {
    	Rect checkRect = FP.rect;
    	checkRect.left = Math.max(0, r.left);
    	checkRect.top = Math.max(0, r.top);
    	checkRect.right = Math.min(checkRect.left + r.width(), bm.getWidth());
    	checkRect.bottom = Math.min(checkRect.top + r.height(), bm.getHeight());
        int pixels[] = new int[checkRect.width() * checkRect.height()];
        int alpha;
        bm.getPixels(pixels, 0, checkRect.width(), checkRect.left, checkRect.top, checkRect.width(), checkRect.height());
        for (int i = 0; i < pixels.length; i++) {
            alpha = Color.alpha(pixels[i]);
            if (alpha > alphaThreshold) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean hitTest(Bitmap bm, Point firstPoint, int alphaThreshold, Bitmap bm2, Point secondPoint, int secondAlphaThreshold) {
        int alpha1, alpha2;
        int pixel1, pixel2;
        int fheight = bm.getHeight() - firstPoint.y;
        int fwidth = bm.getWidth() - firstPoint.x;
        for (int y = 0; y < fheight ; y++) {
        	for (int x = 0; x < fwidth; x++) {
        		try {
        			pixel1 = bm.getPixel(x+firstPoint.x, y+firstPoint.y);
        			pixel2 = bm2.getPixel(x+secondPoint.x, y+secondPoint.y);
        			alpha1 = Color.alpha(pixel1);
        			alpha2 = Color.alpha(pixel2);
        			if (alpha1 > 0 && alpha2 > 0) {
        				return true;
        			}
        		} catch (IllegalArgumentException e) {  
        			break;
        		}
        	}
        }
        return false;
    }
    
    public boolean collide(Mask mask) {
        CollideCallback them = mCheck.get(mask.getClass());
        if (them != null)
            return them.collide(mask);
        CollideCallback us = mask.mCheck.get(getClass());
        if (us != null) 
            us.collide(this);
        return false;
    }
    
    /**
     *  Collide against an Entity.
     */
    private boolean collideMask(Mask other) {
        return parent.x - parent.originX + parent.width > other.parent.x - other.parent.originX
            && parent.y - parent.originY + parent.height > other.parent.y - other.parent.originY
            && parent.x - parent.originX < other.parent.x - other.parent.originX + other.parent.width
            && parent.y - parent.originY < other.parent.y - other.parent.originY + other.parent.height;
    }
    
    /** 
     * @private Collide against a Masklist.
     */
    protected boolean collideMaskList(MaskList other) {
        return other.collide(this);
    }

    /** @private Updates the parent's bounds for this mask. */
    protected void update() {

    }
    
    /** @private Assigns the mask to the parent. */
    public void assignTo(Entity parent) {
        this.parent = parent;
        if (list == null && parent != null) 
            update();
    }
    
    /** 
     * Used to render debug information in console. 
     */
    public void renderDebug(Canvas c) {

    }
}
