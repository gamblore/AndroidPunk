package net.androidpunk;

import java.util.Vector;

import net.androidpunk.graphics.GraphicList;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;

public class Entity extends Tweener {

	private static final String TAG = "Entity";
	
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
    protected boolean mAdded;
    protected String mType = "";
    private String mName;
    private int mLayer;
    protected Entity mUpdatePrev;
    protected Entity mUpdateNext;
    protected Entity mRenderPrev;
    protected Entity mRenderNext;
    protected Entity mTypePrev;
    protected Entity mTypeNext;
    
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
    
    
    public Entity() {
        this(0,0, null, null);
    }
    public Entity(int x, int y) {
        this(x, y, null, null);
    }
    public Entity(int x, int y, Graphic graphic) {
        this(x, y, graphic, null);
    }
    
    public Entity(int x, int y, Graphic graphic, Mask mask) {
        this.x = x;
        this.y = y;
        if (graphic != null) { 
            mGraphic = graphic;
            mGraphic.mAssign.assigned(this);
        }
        if (mask != null) {
            mMask = mask;
        }
        HITBOX.assignTo(this);
            
    }
    
    /**
	 * Override this, called when the Entity is added to a World.
	 */
	public void added() {

	}
	
	/**
	 * Override this, called when the Entity is removed from a World.
	 */
	public void removed() {

	}
	
	/**
	 * Updates the Entity.
	 */
	public void update() { 

	}
	
	/**
	 * Renders the Entity. If you override this for special behaviour,
	 * remember to call super.render() to render the Entity's graphic.
	 */
	public void render() { 
		if (mGraphic != null && mGraphic.visible) {
			if (mGraphic.relative) {
				mPoint.x = x;
				mPoint.y = y;
			}
			else 
				mPoint.x = mPoint.y = 0;
			mCamera.x = FP.camera.x;
			mCamera.y = FP.camera.y;
			mGraphic.render(renderTarget != null ? renderTarget : FP.buffer, mPoint, mCamera);
			//Log.d(TAG, "Entity rendered to " + FP.buffer.toString());
		}
	}
	
	/**
	 * Checks for a collision against an Entity type.
	 * @param	type		The Entity type to check for.
	 * @param	x			Virtual x position to place this Entity.
	 * @param	y			Virtual y position to place this Entity.
	 * @return	The first Entity collided with, or null if none were collided.
	 */
	public Entity collide(String type, int x, int y) {
		if (mWorld == null) 
			return null;

		Entity e = mWorld.mTypeFirst.get(type);
		if (!collidable || e == null) 
			return null;

		mX = this.x; mY = this.y;
		this.x = x; this.y = y;

		if (mMask == null) {
			while (e != null) {
				if (x - originX + width > e.x - e.originX
				&& y - originY + height > e.y - e.originY
				&& x - originX < e.x - e.originX + e.width
				&& y - originY < e.y - e.originY + e.height
				&& e.collidable && e != this)
				{
					if (e.mMask == null || e.mMask.collide(HITBOX))
					{
						this.x = (int)mX; this.y = (int)mY;
						return e;
					}
				}
				e = e.mTypeNext;
			}
			this.x = (int)mX; this.y = (int)mY;
			return null;
		}

		while (e != null) {
			if (x - originX + width > e.x - e.originX
			&& y - originY + height > e.y - e.originY
			&& x - originX < e.x - e.originX + e.width
			&& y - originY < e.y - e.originY + e.height
			&& e.collidable && e != this) {
				if (mMask.collide(e.mMask != null ? e.mMask : e.HITBOX)) {
					this.x = (int)mX; this.y = (int)mY;
					return e;
				}
			}
			e = e.mTypeNext;
		}
		this.x = (int)mX; this.y = (int)mY;
		return null;
	}
	
	/**
	 * Checks for collision against multiple Entity types.
	 * @param	types		An Array or Vector of Entity types to check for.
	 * @param	x			Virtual x position to place this Entity.
	 * @param	y			Virtual y position to place this Entity.
	 * @return	The first Entity collided with, or null if none were collided.
	 */
	public Entity collideTypes(Vector<String> types, int x, int y) {
		if (mWorld == null)
			return null;
		Entity e;
		for (String type : types) {
			if ((e = collide(type, x, y)) != null) 
				return e;
		}
		return null;
	}
	
	/**
	 * Checks if this Entity collides with a specific Entity.
	 * @param	e		The Entity to collide against.
	 * @param	x		Virtual x position to place this Entity.
	 * @param	y		Virtual y position to place this Entity.
	 * @return	The Entity if they overlap, or null if they don't.
	 */
	public Entity collideWith(Entity e, int x, int y) {
		mX = this.x; mY = this.y;
		this.x = x; this.y = y;

		if (x - originX + width > e.x - e.originX
		&& y - originY + height > e.y - e.originY
		&& x - originX < e.x - e.originX + e.width
		&& y - originY < e.y - e.originY + e.height
		&& collidable && e.collidable)
		{
			if (mMask == null) {
				if (e.mMask == null || e.mMask.collide(HITBOX)) {
					this.x = (int)mX; this.y = (int)mY;
					return e;
				}
				this.x = (int)mX; this.y = (int)mY;
				return null;
			}
			if (mMask.collide(e.mMask != null ? e.mMask : e.HITBOX))
			{
				this.x = (int)mX; this.y = (int)mY;
				return e;
			}
		}
		this.x = (int)mX; this.y = (int)mY;
		return null;
	}
	
	/**
	 * Checks if this Entity overlaps the specified rectangle.
	 * @param	x			Virtual x position to place this Entity.
	 * @param	y			Virtual y position to place this Entity.
	 * @param	rX			X position of the rectangle.
	 * @param	rY			Y position of the rectangle.
	 * @param	rWidth		Width of the rectangle.
	 * @param	rHeight		Height of the rectangle.
	 * @return	If they overlap.
	 */
	public boolean collideRect(int x, int y, int rX, int rY, int rWidth, int rHeight) {
		if (x - originX + width >= rX && y - originY + height >= rY
		&& x - originX <= rX + rWidth && y - originY <= rY + rHeight) {
			if (mMask == null) 
				return true;
			mX = this.x; mY = this.y;
			this.x = x; this.y = y;
			FP.entity.x = rX;
			FP.entity.y = rY;
			FP.entity.width = rWidth;
			FP.entity.height = rHeight;
			if (mMask.collide(FP.entity.HITBOX)) {
				this.x = (int)mX; this.y = (int)mY;
				return true;
			}
			this.x = (int)mX; this.y = (int)mY;
			return false;
		}
		return false;
	}
	
	/**
	 * Checks if this Entity overlaps the specified position.
	 * @param	x			Virtual x position to place this Entity.
	 * @param	y			Virtual y position to place this Entity.
	 * @param	pX			X position.
	 * @param	pY			Y position.
	 * @return	If the Entity intersects with the position.
	 */
	public boolean collidePoint(int x, int y, int pX, int pY) {
		if (pX >= x - originX && pY >= y - originY
		&& pX < x - originX + width && pY < y - originY + height) {
			if (mMask == null) 
				return true;
			mX = this.x; mY = this.y;
			this.x = x; this.y = y;
			FP.entity.x = pX;
			FP.entity.y = pY;
			FP.entity.width = 1;
			FP.entity.height = 1;
			if (mMask.collide(FP.entity.HITBOX)) {
				this.x = (int)mX; this.y = (int)mY;
				return true;
			}
			this.x = (int)mX; this.y = (int)mY;
			return false;
		}
		return false;
	}
	
	/**
	 * Populates an array with all collided Entities of a type.
	 * @param	type		The Entity type to check for.
	 * @param	x			Virtual x position to place this Entity.
	 * @param	y			Virtual y position to place this Entity.
	 * @param	array		The Array or Vector object to populate.
	 * @return	The array, populated with all collided Entities.
	 */
	public void collideInto(String type, int x, int y, Vector<Entity> array) {
		if (mWorld == null) 
			return;

		Entity e = mWorld.mTypeFirst.get(type);
		if (!collidable || e == null) 
			return;

		mX = this.x; mY = this.y;
		this.x = x; this.y = y;

		if (mMask == null){
			while (e != null){
				if (x - originX + width > e.x - e.originX
				&& y - originY + height > e.y - e.originY
				&& x - originX < e.x - e.originX + e.width
				&& y - originY < e.y - e.originY + e.height
				&& e.collidable && e != this)
				{
					if (e.mMask == null || e.mMask.collide(HITBOX))
						array.add(e);
				}
				e = e.mTypeNext;
			}
			this.x = (int)mX; this.y = (int)mY;
			return;
		}

		while (e != null) {
			if (x - originX + width > e.x - e.originX
			&& y - originY + height > e.y - e.originY
			&& x - originX < e.x - e.originX + e.width
			&& y - originY < e.y - e.originY + e.height
			&& e.collidable && e != this)
			{
				if (mMask.collide(e.mMask != null ? e.mMask : e.HITBOX)) 
					array.add(e);
			}
			e = e.mTypeNext;
		}
		this.x = (int)mX; this.y = (int)mY;
		return;
	}
	
	/**
	 * Populates an array with all collided Entities of multiple types.
	 * @param	types		An array of Entity types to check for.
	 * @param	x			Virtual x position to place this Entity.
	 * @param	y			Virtual y position to place this Entity.
	 * @param	array		The Array or Vector object to populate.
	 * @return	The array, populated with all collided Entities.
	 */
	public void collideTypesInto(Vector<String> types, int x, int y, Vector<Entity> array) {
		if (mWorld == null) 
			return;
		for (String type : types) {
			collideInto(type, x, y, array);
		}
	}
	
	/**
	 * If the Entity collides with the camera rectangle.
	 */
	public boolean onCamera() {
		return collideRect(x, y, FP.camera.x, FP.camera.y, FP.width, FP.height);
	}
	
	/**
	 * The World object this Entity has been added to.
	 */
	public World getWorld() {
		return mWorld;
	}
	
	/**
	 * The World object this Entity has been added to.
	 */
	public void setWorld(World w) {
		mWorld = w;
	}
	
	/**
	 * Half the Entity's width.
	 */
	public float getHalfWidth() { return width / 2; }

	/**
	 * Half the Entity's height.
	 */
	public float getHalfHeight() { return height / 2; }
	
	/**
	 * The center x position of the Entity's hitbox.
	 */
	public float getCenterX() { return x - originX + width / 2; }

	/**
	 * The center y position of the Entity's hitbox.
	 */
	public float getCenterY() { return y - originY + height / 2; }
	
	/**
	 * The leftmost position of the Entity's hitbox.
	 */
	public int getLeft() { return x - originX; }

	/**
	 * The rightmost position of the Entity's hitbox.
	 */
	public int getRight() { return x - originX + width; }
	
	/**
	 * The topmost position of the Entity's hitbox.
	 */
	public int getTop() { return y - originY; }

	/**
	 * The bottommost position of the Entity's hitbox.
	 */
	public int getBottom() { return y - originY + height; }
	
	/**
	 * The rendering layer of this Entity. Higher layers are rendered first.
	 */
	public int getLayer() { return mLayer; }
	public void setLayer(int value) {
		if (mLayer == value) 
			return;
		if (!mAdded) {
			mLayer = value;
			return;
		}
		mWorld.removeRender(this);
		mLayer = value;
		mWorld.addRender(this);
	}
	
	/**
	 * The collision type, used for collision checking.
	 */
	public String getType() { return mType; }
	public void setType(String value) {
		if (mType.equals(value))
			return;
		if (!mAdded)
		{
			mType = value;
			return;
		}
		if (!"".equals(mType)) 
			mWorld.removeType(this);
		mType = value;
		if (!"".equals(value)) 
			mWorld.addType(this);
	}
	
	/**
	 * An optional Mask component, used for specialized collision. If this is
	 * not assigned, collision checks will use the Entity's hitbox by default.
	 */
	public Mask getMask() { return mMask; }
	public void setMask(Mask value) {
		if (mMask == value) 
			return;
		if (mMask != null) 
			mMask.assignTo(null);
		mMask = value;
		if (value != null) 
			mMask.assignTo(this);
	}
	
	public Graphic getGraphic() { return mGraphic; }
    public void setGraphic(Graphic g) {
    	if (g != null) { 
            mGraphic = g;
            mGraphic.mAssign.assigned(this);
            mGraphic.active = true;
        }
    }
    
    public Graphic addGraphic(Graphic g) {
    	Graphic graphic = getGraphic();
    	if (graphic instanceof GraphicList) {
    		((GraphicList)graphic).add(g);	
    	} else {
    		GraphicList list = new GraphicList();
    		if (graphic != null) {
    			list.add(graphic);
    		}
    		setGraphic(list);
    	}
    	return g;
    }

    /**
	 * Sets the Entity's hitbox properties.
	 * @param	width		Width of the hitbox.
	 * @param	height		Height of the hitbox.
	 */
    public void setHitbox(int width, int height) {
    	setHitbox(width, height, 0, 0);
    }
    
	/**
	 * Sets the Entity's hitbox properties.
	 * @param	width		Width of the hitbox.
	 * @param	height		Height of the hitbox.
	 * @param	originX		X origin of the hitbox.
	 * @param	originY		Y origin of the hitbox.
	 */
	public void setHitbox(int width, int height, int originX, int originY) {
		this.width = width;
		this.height = height;
		this.originX = originX;
		this.originY = originY;
	}
	
	/**
	 * Sets the Entity's hitbox to match that of the provided object.
	 * @param	o		The object defining the hitbox (eg. an Image or Rectangle).
	 */
	public void setHitboxTo(Rect r) {
		setHitbox(r.width(), r.height(), -r.left, -r.top);
	}
	
	/**
	 * Sets the origin of the Entity.
	 * @param	x		X origin.
	 * @param	y		Y origin.
	 */
	public void setOrigin(int x , int y) {
		originX = x;
		originY = y;
	}
	
	/**
	 * Center's the Entity's origin (half width & height).
	 */
	public void centerOrigin() {
		originX = width / 2;
		originY = height / 2;
	}
	
	public double distanceFrom(Entity e) {
		return distanceFrom(e, false);
	}
	/**
	 * Calculates the distance from another Entity.
	 * @param	e				The other Entity.
	 * @param	useHitboxes		If hitboxes should be used to determine the distance. If not, the Entities' x/y positions are used.
	 * @return	The distance.
	 */
	public double distanceFrom(Entity e, boolean useHitboxes) {
		if (!useHitboxes) 
			return Math.sqrt((x - e.x) * (x - e.x) + (y - e.y) * (y - e.y));
		return FP.distanceRects(x - originX, y - originY, width, height, e.x - e.originX, e.y - e.originY, e.width, e.height);
	}
	
	public double distanceToPoint(int px, int py) {
		return distanceToPoint(px, py, false);
	}
	/**
	 * Calculates the distance from this Entity to the point.
	 * @param	px				X position.
	 * @param	py				Y position.
	 * @param	useHitboxes		If hitboxes should be used to determine the distance. If not, the Entities' x/y positions are used.
	 * @return	The distance.
	 */
	public double distanceToPoint(int px, int py, boolean useHitbox) {
		if (!useHitbox) 
			return Math.sqrt((x - px) * (x - px) + (y - py) * (y - py));
		return FP.distanceRectPoint(px, py, x - originX, y - originY, width, height);
	}
	
	/**
	 * Calculates the distance from this Entity to the rectangle.
	 * @param	rx			X position of the rectangle.
	 * @param	ry			Y position of the rectangle.
	 * @param	rwidth		Width of the rectangle.
	 * @param	rheight		Height of the rectangle.
	 * @return	The distance.
	 */
	public double distanceToRect(int rx, int ry, int rwidth, int rheight) {
		return FP.distanceRects(rx, ry, rwidth, rheight, x - originX, y - originY, width, height);
	}
	
	public void moveBy(int x, int y) {
		moveBy(x,y, null, false);
	}
	/**
	 * Moves the Entity by the amount, retaining integer values for its x and y.
	 * @param	x			Horizontal offset.
	 * @param	y			Vertical offset.
	 * @param	solidType	An optional collision type to stop flush against upon collision.
	 * @param	sweep		If sweeping should be used (prevents fast-moving objects from going through solidType).
	 */
	public void moveBy(int x, int y, String solidType, boolean sweep) {
		mMoveX += x;
		mMoveY += y;
		x = Math.round(mMoveX);
		y = Math.round(mMoveY);
		mMoveX -= x;
		mMoveY -= y;
		if (solidType != null) {
			int sign;
			Entity e;
			if (x != 0) {
				if (collidable && (sweep || collide(solidType, this.x + x, this.y)  != null)) {
					sign = x > 0 ? 1 : -1;
					while (x != 0) {
						if ((e = collide(solidType, this.x + sign, this.y)) != null) {
							moveCollideX(e);
							break;
						} else {
							this.x += sign;
							x -= sign;
						}
					}
				}
				else 
					this.x += x;
			}
			if (y != 0) {
				if (collidable && (sweep || collide(solidType, this.x, this.y + y) != null)) {
					sign = y > 0 ? 1 : -1;
					while (y != 0) {
						if ((e = collide(solidType, this.x, this.y + sign)) != null) {
							moveCollideY(e);
							break;
						} else {
							this.y += sign;
							y -= sign;
						}
					}
				}
				else 
					this.y += y;
			}
		} else {
			this.x += x;
			this.y += y;
		}
	}
	
	public void moveTo(int x, int y) {
		moveTo(x,y,null,false);
	}
	/**
	 * Moves the Entity to the position, retaining integer values for its x and y.
	 * @param	x			X position.
	 * @param	y			Y position.
	 * @param	solidType	An optional collision type to stop flush against upon collision.
	 * @param	sweep		If sweeping should be used (prevents fast-moving objects from going through solidType).
	 */
	public void moveTo(int x, int y, String solidType, boolean sweep) {
		moveBy(x - this.x, y - this.y, solidType, sweep);
	}
	
	public void moveTowards(int x, int y, int amount) {
		moveTowards(x, y, amount, null, false);
	}
	/**
	 * Moves towards the target position, retaining integer values for its x and y.
	 * @param	x			X target.
	 * @param	y			Y target.
	 * @param	amount		Amount to move.
	 * @param	solidType	An optional collision type to stop flush against upon collision.
	 * @param	sweep		If sweeping should be used (prevents fast-moving objects from going through solidType).
	 */
	public void moveTowards(int x, int y, int amount, String solidType, boolean sweep) {
		mPoint.x = x - this.x;
		mPoint.y = y - this.y;
		double len = PointF.length(mPoint.x, mPoint.y);
		mPoint.x /= len;
		mPoint.y /= len;
		moveBy(mPoint.x, mPoint.y, solidType, sweep);
	}
	
	/**
	 * When you collide with an Entity on the x-axis with moveTo() or moveBy().
	 * @param	e		The Entity you collided with.
	 */
	public void moveCollideX(Entity e) {

	}
	
	/**
	 * When you collide with an Entity on the y-axis with moveTo() or moveBy().
	 * @param	e		The Entity you collided with.
	 */
	public void moveCollideY(Entity e) {

	}
	
	public void clampHorizontal(int left, int right) {
		clampHorizontal(left, right, 0);
	}
	/**
	 * Clamps the Entity's hitbox on the x-axis.
	 * @param	left		Left bounds.
	 * @param	right		Right bounds.
	 * @param	padding		Optional padding on the clamp.
	 */
	public void clampHorizontal(int left, int right, int padding) {
		if (x - originX < left + padding)
			x = left + originX + padding;
		if (x - originX + width > right - padding)
			x = right - width + originX - padding;
	}
	
	public void clampVertical(int top, int bottom) {
		clampVertical(top, bottom, 0);
	}
	/**
	 * Clamps the Entity's hitbox on the y axis.
	 * @param	top			Min bounds.
	 * @param	bottom		Max bounds.
	 * @param	padding		Optional padding on the clamp.
	 */
	public void clampVertical(int top, int bottom, int padding) {
		if (y - originY < top + padding)
			y = top + originY + padding;
		if (y - originY + height > bottom - padding)
			y = bottom - height + originY - padding;
	}
}
