package net.androidpunk;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.graphics.Point;
import android.util.Log;

public class World extends Tweener {
	
	
	private static final String TAG = "World";
	
	/**
	 * If the render() loop is performed.
	 */
	public boolean visible = true;

	/**
	 * Point used to determine drawing offset in the render loop.
	 */
	public Point camera = new Point();
	
	
	// Adding and removal.
	private Vector<Entity> mAdd = new Vector<Entity>();
	private Vector<Entity> mRemove = new Vector<Entity>();

	// Update information.
	private Entity mUpdateFirst;
	private int mCount;

	// Render information.
	private boolean mLayerSort;
	Vector<Integer> mLayerList = new Vector<Integer>();
	Map<Integer, Integer> mLayerCount = new HashMap<Integer, Integer>();
	Map<Integer, Entity> mRenderFirst = new HashMap<Integer, Entity>();
	Map<Integer, Entity> mRenderLast = new HashMap<Integer, Entity>();
	/*
	private var mRenderFirst:Array = [];
	private var mRenderLast:Array = [];
	private var mLayerList:Array = [];
	private var mLayerCount:Array = [];
	private var mLayerSort:Boolean;
	private var mTempArray:Array = [];
	 */
	protected Map<String, Entity> mTypeFirst = new HashMap<String, Entity>();
	protected Map<String, Integer> mTypeCount = new HashMap<String, Integer>();
	
	/**
	 * Constructor.
	 */
	public World() {

	}

	/**
	 * Override this; called when World is switch to, and set to the currently active world.
	 */
	public void begin() {

	}

	/**
	 * Override this; called when World is changed, and the active world is no longer this.
	 */
	public void end() {

	}
	
	/**
	 * Performed by the game loop, updates all contained Entities.
	 * If you override this to give your World update code, remember
	 * to call super.update() or your Entities will not be updated.
	 */
	public void update() {
		// update the entities
		Entity e = mUpdateFirst;
		while (e != null){
			if (e.active) {
				if (e.mTween != null)
					e.updateTweens();
				e.update();
			}
			Graphic g = e.getGraphic();
			if (g != null && g.active) 
				g.update();
			e = e.mUpdateNext;
		}
	}
	
	/**
	 * Performed by the game loop, renders all contained Entities.
	 * If you override this to give your World render code, remember
	 * to call super.render() or your Entities will not be rendered.
	 */
	public void render() {
		// render the entities in order of depth
		Entity e;
		int i = mLayerList.size();
		while (i-- > 0) {
			e = mRenderLast.get(mLayerList.get(i));
			while (e != null) {
				if (e.visible) 
					e.render();
				e = e.mRenderPrev;
			}
		}
	}
	
	/**
	 * X position of the touches in the World.
	 */
	public int[] getTouchX() {
		int x[] = FP.screen.getTouchX();
		for (int i = 0; i < x.length; i++) {
			x[i] += FP.camera.x;
		}
		return x;
	}
	
	/**
	 * Y position of the touches in the World.
	 */
	public int[] getTouchY() {
		int y[] = FP.screen.getTouchY();
		for (int i = 0; i < y.length; i++) {
			y[i] += FP.camera.y;
		}
		return y;
	}
	
	/**
	 * Adds the Entity to the World at the end of the frame.
	 * @param	e		Entity object you want to add.
	 * @return	The added Entity object.
	 */
	public Entity add(Entity e){
		if (e.getWorld() != null) 
			return e;
		mAdd.add(e);
		e.setWorld(this);
		return e;
	}

	/**
	 * Removes the Entity from the World at the end of the frame.
	 * @param	e		Entity object you want to remove.
	 * @return	The removed Entity object.
	 */
	public Entity remove(Entity e) {
		if (e.getWorld() != this) 
			return e;
		mRemove.add(e);
		e.setWorld(null);
		return e;
	}

	/**
	 * Removes all Entities from the World at the end of the frame.
	 */
	public void removeAll() {
		Entity e = mUpdateFirst;
		while (e != null) {
			mRemove.add(e);
			e.setWorld(null);
			e = e.mUpdateNext;
		}
	}
	
	/**
	 * Adds multiple Entities to the world.
	 * @param	...list		Several Entities (as arguments) or an Array/Vector of Entities.
	 */
	public void addList(List<Entity> entities) {
		for (Entity e : entities) {
			add(e);
		}
	}
	
	/**
	 * Adds multiple Entities to the world.
	 * @param	...list		Several Entities (as arguments) or an Array/Vector of Entities.
	 */
	public void addList(Entity... entities) {
		for (Entity e : entities) {
			add(e);
		}
	}
	
	/**
	 * Removes multiple Entities from the world.
	 * @param	...list		Several Entities (as arguments) or an Array/Vector of Entities.
	 */
	public void removeList(List<Entity> entities){
		for (Entity e : entities) {
			remove(e);
		}
	}
	
	/**
	 * Removes multiple Entities from the world.
	 * @param	...list		Several Entities (as arguments) or an Array/Vector of Entities.
	 */
	public void removeList(Entity... entities){
		for (Entity e : entities) {
			remove(e);
		}
	}
	
	
	public Entity addGraphic(Graphic graphic){
		return addGraphic(graphic, 0, 0, 0);
	}
	
	public Entity addGraphic(Graphic graphic, int layer){
		return addGraphic(graphic, layer, 0, 0);
	}
	/**
	 * Adds an Entity to the World with the Graphic object.
	 * @param	graphic		Graphic to assign the Entity.
	 * @param	x			X position of the Entity.
	 * @param	y			Y position of the Entity.
	 * @param	layer		Layer of the Entity.
	 * @return	The Entity that was added.
	 */
	public Entity addGraphic(Graphic graphic, int layer, int x, int y){
		Entity e = new Entity(x, y, graphic);
		if (layer != 0) 
			e.setLayer(layer);
		e.active = false;
		return add(e);
	}
	

	public Entity addMask(Mask mask, String type) {
		return addMask(mask, type, 0, 0);
	}
	/**
	 * Adds an Entity to the World with the Mask object.
	 * @param	mask	Mask to assign the Entity.
	 * @param	type	Collision type of the Entity.
	 * @param	x		X position of the Entity.
	 * @param	y		Y position of the Entity.
	 * @return	The Entity that was added.
	 */
	public Entity addMask(Mask mask, String type, int x, int y) {
		Entity e = new Entity(x,y, null, mask);
		if(!"".equals(type)) 
			e.setType(type);
		e.active = e.visible = false;
		return add(e);
	}
	
	/**
	 * Brings the Entity to the front of its contained layer.
	 * @param	e		The Entity to shift.
	 * @return	If the Entity changed position.
	 */
	public boolean bringToFront(Entity e) {
		if (e.getWorld() != this || e.mRenderPrev == null)
			return false;
		// pull from list
		e.mRenderPrev.mRenderNext = e.mRenderNext;
		if (e.mRenderNext != null) 
			e.mRenderNext.mRenderPrev = e.mRenderPrev;
		else
			mRenderFirst.put(e.getLayer(), e.mRenderPrev);
		// place at the start
		e.mRenderNext = mRenderFirst.get(e.getLayer());
		e.mRenderNext.mRenderPrev = e;
		mRenderFirst.put(e.getLayer(), e);
		e.mRenderPrev = null;
		return true;
	}
	
	/**
	 * Sends the Entity to the back of its contained layer.
	 * @param	e		The Entity to shift.
	 * @return	If the Entity changed position.
	 */
	public boolean sendToBack(Entity e) {
		if (e.getWorld() != this || e.mRenderNext == null)
			return false;
		// pull from list
		e.mRenderNext.mRenderPrev = e.mRenderPrev;
		if (e.mRenderPrev != null) 
			e.mRenderPrev.mRenderNext = e.mRenderNext;
		else 
			mRenderFirst.put(e.getLayer(), e.mRenderNext);
		// place at the end
		e.mRenderPrev = mRenderLast.get(e.getLayer());
		e.mRenderPrev.mRenderNext = e;
		mRenderLast.put(e.getLayer(), e);
		e.mRenderNext = null;
		return true;
	}
	
	/**
	 * Shifts the Entity one place towards the front of its contained layer.
	 * @param	e		The Entity to shift.
	 * @return	If the Entity changed position.
	 */
	public boolean bringForward(Entity e) {
		if (e.getWorld() != this || e.mRenderPrev == null)
			return false;
		// pull from list
		e.mRenderPrev.mRenderNext = e.mRenderNext;
		if (e.mRenderNext != null) 
			e.mRenderNext.mRenderPrev = e.mRenderPrev;
		else 
			mRenderLast.put(e.getLayer(), e.mRenderPrev);
		// shift towards the front
		e.mRenderNext = e.mRenderPrev;
		e.mRenderPrev = e.mRenderPrev.mRenderPrev;
		e.mRenderNext.mRenderPrev = e;
		if (e.mRenderPrev != null) 
			e.mRenderPrev.mRenderNext = e;
		else 
			mRenderFirst.put(e.getLayer(), e);
		return true;
	}
	
	/**
	 * Shifts the Entity one place towards the back of its contained layer.
	 * @param	e		The Entity to shift.
	 * @return	If the Entity changed position.
	 */
	public boolean sendBackward(Entity e) {
		if (e.getWorld() != this || e.mRenderNext == null)
			return false;
		// pull from list
		e.mRenderNext.mRenderPrev = e.mRenderPrev;
		if (e.mRenderPrev != null)
			e.mRenderPrev.mRenderNext = e.mRenderNext;
		else 
			mRenderFirst.put(e.getLayer(), e.mRenderNext);
		// shift towards the back
		e.mRenderPrev = e.mRenderNext;
		e.mRenderNext = e.mRenderNext.mRenderNext;
		e.mRenderPrev.mRenderNext = e;
		if (e.mRenderNext != null)
			e.mRenderNext.mRenderPrev = e;
		else 
			mRenderLast.put(e.getLayer(), e);
		return true;
	}
	
	/**
	 * If the Entity as at the front of its layer.
	 * @param	e		The Entity to check.
	 * @return	True or false.
	 */
	public boolean isAtFront(Entity e) {
		return e.mRenderPrev == null;
	}
	
	/**
	 * If the Entity as at the back of its layer.
	 * @param	e		The Entity to check.
	 * @return	True or false.
	 */
	public boolean isAtBack(Entity e) {
		return e.mRenderNext == null;
	}
	
	/**
	 * Returns the first Entity that collides with the rectangular area.
	 * @param	type		The Entity type to check for.
	 * @param	rX			X position of the rectangle.
	 * @param	rY			Y position of the rectangle.
	 * @param	rWidth		Width of the rectangle.
	 * @param	rHeight		Height of the rectangle.
	 * @return	The first Entity to collide, or null if none collide.
	 */
	public Entity collideRect(String type, int rX, int rY, int rWidth, int rHeight) {
		Entity e = mTypeFirst.get(type);
		
		while (e != null) {
			if (e.collideRect(e.x, e.y, rX, rY, rWidth, rHeight)) 
				return e;
			e = e.mTypeNext;
		}
		return null;
	}
	
	/**
	 * Returns the first Entity found that collides with the position.
	 * @param	type		The Entity type to check for.
	 * @param	pX			X position.
	 * @param	pY			Y position.
	 * @return	The collided Entity, or null if none collide.
	 */
	public Entity collidePoint(String type, int pX, int pY) {
		Entity e = mTypeFirst.get(type);
		while (e != null) {
			if (e.collidePoint(e.x, e.y, pX, pY)) 
				return e;
			e = e.mTypeNext;
		}
		return null;
	}
	
	public Entity collideLine(String type, int fromX, int fromY, int toX, int toY){
		return collideLine(type, fromX, fromY, toX, toY, 1, null);
	}
	/**
	 * Returns the first Entity found that collides with the line.
	 * @param	type		The Entity type to check for.
	 * @param	fromX		Start x of the line.
	 * @param	fromY		Start y of the line.
	 * @param	toX			End x of the line.
	 * @param	toY			End y of the line.
	 * @param	precision		
	 * @param	p
	 * @return
	 */
	public Entity collideLine(String type, int fromX, int fromY, int toX, int toY, int precision, Point p) {
		// If the distance is less than precision, do the short sweep.
		if (precision < 1) 
			precision = 1;
		if (FP.distance(fromX, fromY, toX, toY) < precision)
		{
			if (p != null)
			{
				if (fromX == toX && fromY == toY)
				{
					p.x = toX; p.y = toY;
					return collidePoint(type, toX, toY);
				}
				return collideLine(type, fromX, fromY, toX, toY, 1, p);
			}
			else return collidePoint(type, fromX, toY);
		}

		// Get information about the line we're about to raycast.
		int xDelta = Math.abs(toX - fromX);
		int yDelta = Math.abs(toY - fromY);
		int xSign = toX > fromX ? precision : -precision;
		int ySign = toY > fromY ? precision : -precision;
		int x = fromX;
		int y = fromY;
		Entity e;

		// Do a raycast from the start to the end point.
		if (xDelta > yDelta) {
			ySign *= yDelta / xDelta;
			if (xSign > 0) {
				while (x < toX) {
					if ((e = collidePoint(type, x, y)) != null) {
						if (p == null) 
							return e;
						if (precision < 2) {
							p.x = x - xSign; p.y = y - ySign;
							return e;
						}
						return collideLine(type, x - xSign, y - ySign, toX, toY, 1, p);
					}
					x += xSign; y += ySign;
				}
			} else {
				while (x > toX) {
					if ((e = collidePoint(type, x, y)) != null) {
						if (p == null)
							return e;
						if (precision < 2) {
							p.x = x - xSign; p.y = y - ySign;
							return e;
						}
						return collideLine(type, x - xSign, y - ySign, toX, toY, 1, p);
					}
					x += xSign; y += ySign;
				}
			}
		} else {
			xSign *= xDelta / yDelta;
			if (ySign > 0) {
				while (y < toY) {
					if ((e = collidePoint(type, x, y)) != null) {
						if (p == null) 
							return e;
						if (precision < 2) {
							p.x = x - xSign; p.y = y - ySign;
							return e;
						}
						return collideLine(type, x - xSign, y - ySign, toX, toY, 1, p);
					}
					x += xSign; y += ySign;
				}
			} else {
				while (y > toY) {
					if ((e = collidePoint(type, x, y)) != null) {
						if (p == null)
							return e;
						if (precision < 2) {
							p.x = x - xSign; p.y = y - ySign;
							return e;
						}
						return collideLine(type, x - xSign, y - ySign, toX, toY, 1, p);
					}
					x += xSign; y += ySign;
				}
			}
		}

		// Check the last position.
		if (precision > 1) {
			if (p == null)
				return collidePoint(type, toX, toY);
			if (collidePoint(type, toX, toY) != null) 
				return collideLine(type, x - xSign, y - ySign, toX, toY, 1, p);
		}

		// No collision, return the end point.
		if (p != null)
		{
			p.x = toX;
			p.y = toY;
		}
		return null;
	}
	
	/**
	 * Populates an array with all Entities that collide with the rectangle. This
	 * function does not empty the array, that responsibility is left to the user.
	 * @param	type		The Entity type to check for.
	 * @param	rX			X position of the rectangle.
	 * @param	rY			Y position of the rectangle.
	 * @param	rWidth		Width of the rectangle.
	 * @param	rHeight		Height of the rectangle.
	 * @param	into		The Array or Vector to populate with collided Entities.
	 */
	public void collideRectInto(String type, int rX, int rY, int rWidth, int rHeight, Vector<Entity> into) {
		Entity e = mTypeFirst.get(type);
		while (e != null) {
			if (e.collideRect(e.x, e.y, rX, rY, rWidth, rHeight)) 
				into.add(e);
			e = e.mTypeNext;
		}
	}
	
	/**
	 * Populates an array with all Entities that collide with the position. This
	 * function does not empty the array, that responsibility is left to the user.
	 * @param	type		The Entity type to check for.
	 * @param	pX			X position.
	 * @param	pY			Y position.
	 * @param	into		The Array or Vector to populate with collided Entities.
	 * @return	The provided Array.
	 */
	public void collidePointInto(String type, int pX, int pY, Vector<Entity> into){
		Entity e = mTypeFirst.get(type);
		while (e != null) {
			if (e.collidePoint(e.x, e.y, pX, pY)) 
				into.add(e);
			e = e.mTypeNext;
		}
	}
	
	/**
	 * Finds the Entity nearest to the rectangle.
	 * @param	type		The Entity type to check for.
	 * @param	x			X position of the rectangle.
	 * @param	y			Y position of the rectangle.
	 * @param	width		Width of the rectangle.
	 * @param	height		Height of the rectangle.
	 * @return	The nearest Entity to the rectangle.
	 */
	public Entity nearestToRect(String type, int x, int y, int width, int height) {
		Entity n = mTypeFirst.get(type);
		double nearDist = Double.MAX_VALUE;
		Entity near = null;
		double dist;
		while (n != null) {
			dist = squareRects(x, y, width, height, n.x - n.originX, n.y - n.originY, n.width, n.height);
			if (dist < nearDist) {
				nearDist = dist;
				near = n;
			}
			n = n.mTypeNext;
		}
		return near;
	}
	
	public Entity nearestToEntity(String type, Entity e) {
		return nearestToEntity(type, e, false);
	}
	/**
	 * Finds the Entity nearest to another.
	 * @param	type		The Entity type to check for.
	 * @param	e			The Entity to find the nearest to.
	 * @param	useHitboxes	If the Entities' hitboxes should be used to determine the distance. If false, their x/y coordinates are used.
	 * @return	The nearest Entity to e.
	 */
	public Entity nearestToEntity(String type, Entity e, boolean useHitboxes) {
		if (useHitboxes)
			return nearestToRect(type, e.x - e.originX, e.y - e.originY, e.width, e.height);
		Entity n = mTypeFirst.get(type);
		double nearDist = Double.MAX_VALUE;
		Entity near = null;
		double dist;
		int x = e.x - e.originX;
		int y = e.y - e.originY;
		while (n != null) {
			dist = (x - n.x) * (x - n.x) + (y - n.y) * (y - n.y);
			if (dist < nearDist) {
				nearDist = dist;
				near = n;
			}
			n = n.mTypeNext;
		}
		return near;
	}
	
	public Entity nearestToPoint(String type, int x, int y) {
		return nearestToPoint(type, x, y, false);
	}
	/**
	 * Finds the Entity nearest to the position.
	 * @param	type		The Entity type to check for.
	 * @param	x			X position.
	 * @param	y			Y position.
	 * @param	useHitboxes	If the Entities' hitboxes should be used to determine the distance. If false, their x/y coordinates are used.
	 * @return	The nearest Entity to the position.
	 */
	public Entity nearestToPoint(String type, int x, int y, boolean useHitboxes) {
		Entity n = mTypeFirst.get(type);
		double nearDist = Double.MAX_VALUE;
		Entity near = null;
		double dist;
		if (useHitboxes) {
			while (n != null) {
				dist = squarePointRect(x, y, n.x - n.originX, n.y - n.originY, n.width, n.height);
				if (dist < nearDist) {
					nearDist = dist;
					near = n;
				}
				n = n.mTypeNext;
			}
			return near;
		}
		while (n != null) {
			dist = (x - n.x) * (x - n.x) + (y - n.y) * (y - n.y);
			if (dist < nearDist) {
				nearDist = dist;
				near = n;
			}
			n = n.mTypeNext;
		}
		return near;
	}
	
	/**
	 * How many Entities are in the World.
	 */
	public int getCount() {
		return mCount;
	}
	
	/**
	 * Returns the amount of Entities of the type are in the World.
	 * @param	type		The type (or Class type) to count.
	 * @return	How many Entities of type exist in the World.
	 */
	public int typeCount(String type) {
		Integer i = mTypeCount.get(type);
		if (i == null) 
			return 0;
		else
			return i;
	}
	
	/**
	 * Returns the amount of Entities are on the layer in the World.
	 * @param	layer		The layer to count Entities on.
	 * @return	How many Entities are on the layer.
	 */
	public int layerCount(int layer) {
		if (layer >= mLayerCount.size())
			return 0;
		Integer i = mLayerCount.get(layer);
		if (i == null) 
			return 0;
		else
			return i;
	}
	
	/**
	 * The first Entity in the World.
	 */
	public Entity getFirst() {
		return mUpdateFirst;
	}
	
	/**
	 * How many Entity layers the World has.
	 */
	public int getLayers() {
		return mLayerList.size();
	}
	
	/**
	 * The first Entity of the type.
	 * @param	type		The type to check.
	 * @return	The Entity.
	 */
	public Entity typeFirst(String type) {
		if (mUpdateFirst == null)
			return null;
		return mTypeFirst.get(type);
	}
	
	/**
	 * The first Entity on the Layer.
	 * @param	layer		The layer to check.
	 * @return	The Entity.
	 */
	public Entity layerFirst(int layer) {
		if (mUpdateFirst == null)
			return null;
		return mRenderFirst.get(layer);
	}
	
	/**
	 * The last Entity on the Layer.
	 * @param	layer		The layer to check.
	 * @return	The Entity.
	 */
	public Entity layerLast(int layer) {

		if (mUpdateFirst == null || layer >= mRenderLast.size())
			return null;
		return mRenderLast.get(layer);
	}
	
	/**
	 * The Entity that will be rendered first by the World.
	 */
	public Entity getFarthest() {
		if (mUpdateFirst == null || mLayerList.size() == 0)
			return null;
		return mRenderLast.get(mLayerList.lastElement());
	}
	
	/**
	 * The Entity that will be rendered last by the world.
	 */
	public Entity getNearest() {
		if (mUpdateFirst == null || mLayerList.size() == 0)
			return null;
		return mRenderFirst.get(mLayerList.firstElement());
	}
	
	/**
	 * The layer that will be rendered first by the World.
	 */
	public int getLayerFarthest() {
		if (mUpdateFirst == null || mLayerList.size() == 0)
			return 0;
		return mLayerList.get(mLayerList.lastElement());
	}
	
	/**
	 * The layer that will be rendered last by the World.
	 */
	public int getLayerNearest() {
		if (mUpdateFirst == null || mLayerList.size() == 0)
			return 0;
		return mLayerList.get(0);
	}
	
	/**
	 * How many different types have been added to the World.
	 */
	public int getUniqueTypes() {
		return mTypeCount.keySet().size();
	}
	
	/**
	 * Pushes all Entities in the World of the type into the Array or Vector.
	 * @param	type		The type to check.
	 * @param	into		The Array or Vector to populate.
	 * @return	The same array, populated.
	 */
	public void getType(String type, Vector<Entity> into) {
		Entity e = mTypeFirst.get(type);
		while (e != null) {
			into.add(e);
			e = e.mTypeNext;
		}
	}
	
	/**
	 * Pushes all Entities in the World on the layer into the Array or Vector.
	 * @param	layer		The layer to check.
	 * @param	into		The Array or Vector to populate.
	 * @return	The same array, populated.
	 */
	public void getLayer(int layer, Vector<Entity> into) {
		if (layer >= mRenderLast.size())
			return;
		Entity e = mRenderLast.get(layer);
		while (e != null) {
			into.add(e);
			e = e.mUpdatePrev;
		}
	}
	
	/**
	 * Pushes all Entities in the World into the array.
	 * @param	into		The Array or Vector to populate.
	 * @return	The same array, populated.
	 */
	public void getAll(Vector<Entity> into) {
		Entity e = mUpdateFirst;
		while (e != null) {
			into.add(e);
			e = e.mUpdateNext;
		}
	}
	
	/**
	 * Updates the add/remove lists at the end of the frame.
	 */
	public void updateLists() {
		// remove entities
		if (mRemove.size() > 0) {
			for (Entity e : mRemove) {
				
				if (e.mAdded != true && mAdd.indexOf(e) >= 0) {
					mAdd.remove(e);
					continue;
				}
				e.mAdded = false;
				
				e.removed();
				removeUpdate(e);
				removeRender(e);
				if (e.mTween != null) 
					e.clearTweens();
			}
			mRemove.clear();
		}

		// add entities
		if (mAdd.size() > 0){
			for (Entity e : mAdd) {
				e.mAdded = true;
				addUpdate(e);
				addRender(e);
				if (!"".equals(e.mType))
					addType(e);
				e.added();
			}
			mAdd.clear();
		}

		// sort the depth list
		if (mLayerSort) {
			if (mLayerList.size() > 1) {
				Integer[] array = (Integer[]) mLayerList.toArray();
				Arrays.sort(array);
				mLayerList.clear();
				mLayerList.copyInto(array);
			}
				
			mLayerSort = false;
		}
	}

	/** @private Adds Entity to the update list. */
	protected void addUpdate(Entity e) {
		// add to update list
		if (mUpdateFirst != null) {
			mUpdateFirst.mUpdatePrev = e;
			e.mUpdateNext = mUpdateFirst;
		}
		else 
			e.mUpdateNext = null;
		e.mUpdatePrev = null;
		mUpdateFirst = e;
		mCount++;
	}

	/** @private Removes Entity from the update list. */
	protected void removeUpdate(Entity e) {
		// remove from the update list
		if (mUpdateFirst == e) mUpdateFirst = e.mUpdateNext;
		if (e.mUpdateNext != null)
			e.mUpdateNext.mUpdatePrev = e.mUpdatePrev;
		if (e.mUpdatePrev != null)
			e.mUpdatePrev.mUpdateNext = e.mUpdateNext;
		e.mUpdateNext = e.mUpdatePrev = null;

		mCount --;
	}

	/** @private Adds Entity to the render list. */
	protected void addRender(Entity e) {
		Entity f = mRenderFirst.get(e.getLayer());
		
		if (f != null) {
			// Append entity to existing layer.
			e.mRenderNext = f;
			f.mRenderPrev = e;
			mLayerCount.put(e.getLayer(), mLayerCount.get(e.getLayer()) +1);
		} else {
			// Create new layer with entity.
			mRenderLast.put(e.getLayer(), e);
			mRenderLast.put(e.getLayer(), e);
			mLayerList.add(e.getLayer());
			mLayerSort = true;
			e.mRenderNext = null;
			mLayerCount.put(e.getLayer(), 1);
		}
		mRenderFirst.put(e.getLayer(), e);
		e.mRenderPrev = null;
	}

	/** @private Removes Entity from the render list. */
	protected void removeRender(Entity e) {
		if (e.mRenderNext != null) 
			e.mRenderNext.mRenderPrev = e.mRenderPrev;
		else 
			mRenderLast.put(e.getLayer(), e.mRenderPrev);
		if (e.mRenderPrev != null) 
			e.mRenderPrev.mRenderNext = e.mRenderNext;
		else {
			// Remove this entity from the layer.
			mRenderFirst.put(e.getLayer(), e.mRenderNext);
			if (e.mRenderNext == null) {
				// Remove the layer from the layer list if this was the last entity.
				mLayerList.remove((Integer)e.getLayer());
			}
		}
		mLayerCount.put(e.getLayer(), mLayerCount.get(e.getLayer()) -1);
		e.mRenderNext = e.mRenderPrev = null;
	}

	/** @private Adds Entity to the type list. */
	protected void addType(Entity e) {
		// add to type list
		if (mTypeFirst.get(e.mType) != null) {
			mTypeFirst.get(e.mType).mTypePrev = e;
			e.mTypeNext = mTypeFirst.get(e.mType);
			mTypeCount.put(e.mType, (Integer)(mTypeCount.get(e.mType) +1));
		} else {
			e.mTypeNext = null;
			mTypeCount.put(e.mType, 1);
		}
		e.mTypePrev = null;
		
		mTypeFirst.put(e.mType, e);
	}

	/** @private Removes Entity from the type list. */
	protected void removeType(Entity e) {
		// remove from the type list
		if (mTypeFirst.get(e.mType) == e)
			mTypeFirst.put(e.mType, e.mTypeNext);
		if (e.mTypeNext != null)
			e.mTypeNext.mTypePrev = e.mTypePrev;
		if (e.mTypePrev != null)
			e.mTypePrev.mTypeNext = e.mTypeNext;
		e.mTypeNext = e.mTypePrev = null;
		mTypeCount.put(e.mType, mTypeCount.get(e.mType) -1);
	}
	
	/** @private Calculates the squared distance between two rectangles. */
	private static double squareRects(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
		if (x1 < x2 + w2 && x2 < x1 + w1)
		{
			if (y1 < y2 + h2 && y2 < y1 + h1) 
				return 0;
			if (y1 > y2) 
				return (y1 - (y2 + h2)) * (y1 - (y2 + h2));
			return (y2 - (y1 + h1)) * (y2 - (y1 + h1));
		}
		if (y1 < y2 + h2 && y2 < y1 + h1)
		{
			if (x1 > x2) 
				return (x1 - (x2 + w2)) * (x1 - (x2 + w2));
			return (x2 - (x1 + w1)) * (x2 - (x1 + w1));
		}
		if (x1 > x2)
		{
			if (y1 > y2) 
				return squarePoints(x1, y1, (x2 + w2), (y2 + h2));
			return squarePoints(x1, y1 + h1, x2 + w2, y2);
		}
		if (y1 > y2) 
			return squarePoints(x1 + w1, y1, x2, y2 + h2);
		return squarePoints(x1 + w1, y1 + h1, x2, y2);
	}
	
	/** @private Calculates the squared distance between two points. */
	private static double squarePoints(int x1, int y1, int x2, int y2) {
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
	}
	
	/** @private Calculates the squared distance between a rectangle and a point. */
	private static double squarePointRect(int px, int py, int rx, int ry, int rw, int rh) {
		if (px >= rx && px <= rx + rw) {
			if (py >= ry && py <= ry + rh) return 0;
			if (py > ry) return (py - (ry + rh)) * (py - (ry + rh));
			return (ry - py) * (ry - py);
		}
		if (py >= ry && py <= ry + rh) {
			if (px > rx) return (px - (rx + rw)) * (px - (rx + rw));
			return (rx - px) * (rx - px);
		}
		if (px > rx) {
			if (py > ry) 
				return squarePoints(px, py, rx + rw, ry + rh);
			return squarePoints(px, py, rx + rw, ry);
		}
		if (py > ry) 
			return squarePoints(px, py, rx, ry + rh);
		return squarePoints(px, py, rx, ry);
	}

}
