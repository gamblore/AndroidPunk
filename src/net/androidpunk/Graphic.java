package net.androidpunk;

import net.androidpunk.android.Texture;
import android.graphics.Bitmap;
import android.graphics.Point;

public class Graphic {

	protected Texture mTexture = new Texture(); 
	
	public interface OnAssignToEntityCallback {
		public void assigned(Entity e);
	}
    
	private static final OnAssignToEntityCallback NULL_CALLBACK = new OnAssignToEntityCallback() {
		public void assigned(Entity e) { }
	};
	
    /**
     * If the graphic should update.
     */
    public boolean active = false;
    
    /**
     * If the graphic should render.
     */
    public boolean visible = true;
    
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
    protected OnAssignToEntityCallback mAssign = NULL_CALLBACK;
    
    /**
     * Constructor.
     */
    public Graphic() {
        
    }
    
    /**
     * Updates the graphic
     */
    public void update() {
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
    
    /**
     * Releases any native memory, like bitmaps, So that memory is handled better. 
     */
    protected void release() {
    	
    }
}
