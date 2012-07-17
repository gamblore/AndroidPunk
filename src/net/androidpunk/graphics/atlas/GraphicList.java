package net.androidpunk.graphics.atlas;

import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.Graphic;
import android.graphics.Point;

/**
 * A Graphic that can contain multiple Graphics of one or various types.
 * Useful for drawing sprites with multiple different parts, etc.
 */
public class GraphicList extends Graphic {

	// List information.
	private Vector<Graphic> mGraphics = new Vector<Graphic>();
	private Vector<Graphic> mTemp = new Vector<Graphic>();
	private int mCount;
	private Point mCamera = new Point();
	
	/**
	 * Constructor.
	 * @param	...graphic		Graphic objects to add to the list.
	 */
	public GraphicList(Graphic... graphics) {
		for (int i = 0; i < graphics.length; i++) {
			add(graphics[i]);
		}
	}
	
	/** @private Updates the graphics in the list. */
	@Override
	public void update() {
		int size = mGraphics.size();
		for (int i = 0; i < size; i++) {
			Graphic g = mGraphics.get(i);
			if (g.active)
				g.update();
		}
	}
	
	/** @private Renders the Graphics in the list. */
	@Override
	public void render(GL10 gl, Point point, Point camera) {
		point.x += x;
		point.y += y;
		camera.x *= scrollX;
		camera.y *= scrollY;
		int size = mGraphics.size();
		for (int i = 0; i < size; i++) {
			Graphic g = mGraphics.get(i);
			if (g.visible) {
				if (g.relative) {
					mPoint.x = point.x;
					mPoint.y = point.y;
				}
				else 
					mPoint.x = mPoint.y = 0;
				mCamera.x = camera.x;
				mCamera.y = camera.y;
				
				g.render(gl, mPoint, mCamera);
			}
		}
	}
	
	/**
	 * Adds the Graphic to the list.
	 * @param	graphic		The Graphic to add.
	 * @return	The added Graphic.
	 */
	public Graphic add(Graphic graphic) {
		mGraphics.add(graphic);
		if (!active)
			active = graphic.active;
		return graphic;
	}
	
	/**
	 * Removes the Graphic from the list.
	 * @param	graphic		The Graphic to remove.
	 * @return	The removed Graphic.
	 */
	public Graphic remove(Graphic graphic) {
		if(mGraphics.indexOf(graphic)< 0)
			return graphic;
		mTemp.clear();
		int size = mGraphics.size();
		for (int i = 0; i < size; i++) {
			Graphic g = mGraphics.get(i);
			if (g == graphic) 
				mCount--;
			else
				mTemp.add(g);
		}
		Vector<Graphic> temp = mGraphics; 
		mGraphics = mTemp;
		mTemp = temp;
		updateCheck();
		return graphic;
	}
	
	public void removeAt() {
		removeAt(0);
	}
	/**
	 * Removes the Graphic from the position in the list.
	 * @param	index		Index to remove.
	 */
	public void removeAt(int index) {
		if (mGraphics.size() == 0) 
			return;
		index %= mGraphics.size();
		remove(mGraphics.get(index));
		updateCheck();
	}
	
	/**
	 * Removes all Graphics from the list.
	 */
	public void removeAll() {
		mGraphics.clear();
		mTemp.clear();
		mCount = 0;
		active = false;
	}
	
	/**
	 * All Graphics in this list.
	 */
	public Vector<Graphic> getChildren() { return mGraphics; }
	
	/**
	 * Amount of Graphics in this list.
	 */
	public int getCount() { return mCount; }
	
	/**
	 * Check if the Graphiclist should update.
	 */
	private void updateCheck() {
		active = false;
		int size = mGraphics.size();
		for (int i = 0; i < size; i++) {
			Graphic g = mGraphics.get(i);
			if (g.active) {
				active = true;
				return;
			}
		}
	}
}
