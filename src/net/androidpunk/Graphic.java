package net.androidpunk;

import android.graphics.Bitmap;
import android.graphics.Point;

public class Graphic {

    /**
     * If the graphic should update.
     */
    public boolean active = false;
    
    /**
     * If the graphic should render.
     */
    public boolean visiable = true;
    
    /**
     * X offset.
     */
    public float x = 0;
    
    /**
     * Y offset.
     */
    public float y = 0;
    
    /**
     * X scrollfactor, effects how much the camera offsets the drawn graphic.
     * Can be used for parallax effect, eg. Set to 0 to follow the camera,
     * 0.5 to move at half-speed of the camera, or 1 (default) to stay still.
     */
    public float scrollX = 1;
    
    /**
     * Y scrollfactor, effects how much the camera offsets the drawn graphic.
     * Can be used for parallax effect, eg. Set to 0 to follow the camera,
     * 0.5 to move at half-speed of the camera, or 1 (default) to stay still.
     */
    public float scrollY = 1;
    
    /**
     * If the graphic should render at its position relative to its parent Entity's position.
     */
    public boolean relative = true;
    
    protected final Point mPoint = new Point();
    protected OnAssignToEntityCallback mAssign = null;
    
    public interface OnAssignToEntityCallback {
        public void assigned(Entity e);
    }
    
    /**
     * Constructor.
     */
    public Graphic() {
        
    }
    
    /**
     * Updates the graphic
     */
    public void update() {
        update(0.03f);
    }
    
    /**
     * Updates the graphic
     * @param delta time that has passed in seconds.
     */
    public void update(float delta) {
        
    }
    /**
     * Renders the graphic to the screen buffer.
     * @param target the bitmap to draw into
     * @param point The position to draw the graphic.
     * @param camera The camera offset.
     */
    public void render(Bitmap target, Point point, Point camera) {
        
    }
    
    protected void setOnAssign(OnAssignToEntityCallback func) { 
        mAssign = func;
    }
    
    protected OnAssignToEntityCallback getOnAssignToEntityCallback() { 
        return mAssign;
    }
}
