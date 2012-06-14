package net.androidpunk;

import android.graphics.Canvas;

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
    
    public final Map<Class, CollideCallback> mCheck = new HashMap<Class, CollideCallback>();
    
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
