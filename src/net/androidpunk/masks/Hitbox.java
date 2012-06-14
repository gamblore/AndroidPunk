package net.androidpunk.masks;

import net.androidpunk.Mask;

public class Hitbox extends Mask {

    protected int mWidth, mHeight;
    protected int mX, mY;
    
    public Hitbox() {
        this(1, 1, 0, 0);
    }
    
    public Hitbox(int width) {
        this(width, 1, 0, 0);
    }
    
    public Hitbox(int width, int height) {
        this(width, height, 0, 0);
    }
    
    public Hitbox(int width, int height, int x) {
        this(width, height, x, 0);
    }
    
    /**
     * Constructor.
     * @param   width       Width of the hitbox.
     * @param   height      Height of the hitbox.
     * @param   x           X offset of the hitbox.
     * @param   y           Y offset of the hitbox.
     */
    public Hitbox(int width, int height, int x, int y) {
        super();
        mWidth = width;
        mHeight = height;
        mX = x;
        mY = y;
        
        mCheck.put(Mask.class, new CollideCallback() {
            
            @Override
            public boolean collide(Mask m) {
                return collideMask(m);
            }
        });
        
        mCheck.put(Hitbox.class, new CollideCallback() {
            
            @Override
            public boolean collide(Mask m) {
                return collideHitbox((Hitbox)m);
            }
        });
    }

    /** @private Collides against an Entity. */
    private boolean collideMask(Mask other) {
        return parent.x + mX + mWidth > other.parent.x - other.parent.originX
            && parent.y + mY + mHeight > other.parent.y - other.parent.originY
            && parent.x + mX < other.parent.x - other.parent.originX + other.parent.width
            && parent.y + mY < other.parent.y - other.parent.originY + other.parent.height;
    }

    /** @private Collides against a Hitbox. */
    private boolean collideHitbox(Hitbox other)
    {
        return parent.x + mX + mWidth > other.parent.x + other.mX
            && parent.y + mY + mHeight > other.parent.y + other.mY
            && parent.x + mX < other.parent.x + other.mX + other.mWidth
            && parent.y + mY < other.parent.y + other.mY + other.mHeight;
    }

    private void checkUpdate() {
        if (list != null) 
            list.update();
        else if (parent != null)
            update();
    }
    
    public int getX() {
        return mX;
    }
    
    public void setX(int x) {
        if (x == mX) 
            return;
        mX = x;
        checkUpdate();
    }
    
    public int getY() {
        return mY;
    }
    
    public void setY(int y) {
        if (y == mY) 
            return;
        mY = y;
        checkUpdate();
    }
    
    public int getWidth() {
        return mWidth;
    }
    
    public void setWidth(int width) {
        if (width == mWidth) 
            return;
        mWidth = width;
        checkUpdate();
    }
    public int getHeight() {
        return mHeight;
    }
    
    public void setHeight(int height) {
        if (height == mHeight) 
            return;
        mHeight = height;
        checkUpdate();
    }
    
    /** 
     * Updates the parent's bounds for this mask.
     */
    @Override
    protected void update() {
        if (list != null) {
            // update parent list
            list.update();
        }
        else if (parent != null) {
            // update entity bounds
            parent.originX = -mX;
            parent.originY = -mY;
            parent.width = mWidth;
            parent.height = mHeight;
        }
    }
}
