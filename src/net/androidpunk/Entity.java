package net.androidpunk;

import android.graphics.Bitmap;
import android.graphics.Point;

public class Entity extends Positionable {

    /**
     * If the Entity should render.
     */
    public boolean visible = true;

    /**
     * If the Entity should respond to collision checks.
     */
    public boolean collidable = true;

    /**
     * X position of the Entity in the World.
     */
    public int x = 0;

    /**
     * Y position of the Entity in the World.
     */
    public int y = 0;

    /**
     * Width of the Entity's hitbox.
     */
    public int width;

    /**
     * Height of the Entity's hitbox.
     */
    public int height;

    /**
     * X origin of the Entity's hitbox.
     */
    public int originX;

    /**
     * Y origin of the Entity's hitbox.
     */
    public int originY;

    /**
     * The BitmapData target to draw the Entity to. Leave as null to render to the current screen buffer (default).
     */
    public Bitmap renderTarget;
    
    // Entity information.
    private World mWorld;
    private String mType;
    private String mName;
    private int mLayer;
    private Entity mUpdatePrev;
    private Entity mUpdateNext;
    private Entity mRenderPrev;
    private Entity mRenderNext;
    private Entity mTypePrev;
    private Entity mTypeNext;
    private Entity mRecycleNext;
    
    // Collision information.
    private final Mask HITBOX = new Mask();
    private Mask mMask;
    private float mX;
    private float mY;
    private float mMoveX;
    private float mMoveY;
    
    // Rendering information.
    private Graphic mGraphic;
    private Point mPoint = FP.point;
    private Point mCamera = FP.point2;
    
    public Entity(float x, float y) {
        this(x, y, null, null);
    }
    public Entity(float x, float y, Graphic graphic) {
        this(x, y, graphic, null);
    }
    
    public Entity(float x, float y, Graphic graphic, Mask mask) {
        mX = x;
        mY = y;
        if (graphic != null) { 
            mGraphic = graphic;
            mGraphic.mAssign.assigned(this);
        }
        if (mask != null) {
            mMask = mask;
        }
        HITBOX.assignTo(this);
            
    }
    
}
