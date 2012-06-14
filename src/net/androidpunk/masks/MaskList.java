package net.androidpunk.masks;

import android.graphics.Canvas;

import net.androidpunk.Entity;
import net.androidpunk.Mask;

import java.util.Vector;

public class MaskList extends Hitbox {
    
    private Vector<Mask> mMasks = new Vector<Mask>();
    
    public MaskList(Mask... args) {
        for(Mask m : args) {
            add(m);
        }
    }
    
    /** @private Collide against a mask. */
    @Override
    public boolean collide(Mask mask) {
        for (Mask m : mMasks) {
            if (m.collide(mask)) return true;
        }
        return false;
    }
    
    /** @private Collide against a MaskList. */
    @Override
    protected boolean collideMaskList(MaskList other) {
        for (Mask a : mMasks) {
            for (Mask b : other.mMasks) {
                if (a.collide(b)) return true;
            }
        }
        return false;
    }
    
    /**
     * Adds a Mask to the list.
     * @param   mask        The Mask to add.
     * @return  The added Mask.
     */
    public Mask add(Mask mask) {
        mMasks.add(mask);
        mask.list = this;
        mask.parent = parent;
        update();
        return mask;
    }
    
    /**
     * Removes the Mask from the list.
     * @param   mask        The Mask to remove.
     * @return  The removed Mask.
     */
    public Mask remove(Mask mask) {
        mMasks.remove(mask);
        return mask;
    }
    
    /**
     * Removes the Mask at the index.
     * @param   index       The Mask index.
     */
    public void removeAt(int index) {
        mMasks.remove(index);
    }
    
    /**
     * Removes all Masks from the list.
     */
    public void removeAll() {
        for (Mask m : mMasks) {
            m.list = null;
        }
        mMasks.clear();
        update();
    }
    
    /**
     * Gets a Mask from the list.
     * @param   index       The Mask index.
     * @return  The Mask at the index.
     */
    public Mask getMask(int index) {
        return mMasks.get(index % mMasks.size());
    }

    @Override
    public void assignTo(Entity parent) {
        for (Mask m : mMasks) {
            m.parent = parent;
        }
        super.assignTo(parent);
    }
    
    /** @private Updates the parent's bounds for this mask. */
    @Override 
    protected void update() { 
        // find bounds of the contained masks
        int t = 0,l = 0, r = 0, b = 0;
        boolean matchFound = false;
        for (Mask m : mMasks) {
            if (m instanceof Hitbox) {
                Hitbox h = (Hitbox)m;
                l = h.getX();
                t = h.getY();
                r = h.getX() + h.getWidth();
                b = h.getY() + h.getHeight();
                matchFound = true;
                break;
            }
        }
        if (!matchFound) {
            super.update();
            return;
        }
        
        int i = mMasks.size();
        for (Mask m : mMasks) {
            if (m instanceof Hitbox) {
                Hitbox h = (Hitbox)m;
                if (h.getX() < l) 
                    l = h.getX();
                if (h.getY() < t) 
                    t = h.getY();
                if (h.getX() + h.getWidth() > r) 
                    r = h.getX() + h.getWidth();
                if (h.getY() + h.getHeight() > b) 
                    b = h.getY() + h.getHeight();
            }
        }

        // update hitbox bounds
        mX = l;
        mY = t;
        mWidth = r - l;
        mHeight = b - t;
        super.update();
    }

    /** Used to render debug information in console. */
    @Override
    public void renderDebug(Canvas c) {
        for (Mask m : mMasks) {
            m.renderDebug(c);
        }
    }
    
    public int getCount() {
        return mMasks.size();
    }
}
