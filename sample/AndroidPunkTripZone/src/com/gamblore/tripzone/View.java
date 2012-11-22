package com.gamblore.tripzone;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import android.graphics.Rect;

public class View extends Entity {

	public static final String TYPE_VIEW = "View";
	
	private Entity mToFollow;
	private Rect mWithin;
	private float mSpeed;
	
	public View(Entity toFollow) {
		this(toFollow, null, 1);
	}
	
	public View(Entity toFollow, Rect within) {
		this(toFollow, within, 1);
	}
	
	public View(Entity toFollow, Rect within, float speed) {
		
		x = FP.camera.x;
		y = FP.camera.y;
		
		setView(toFollow, within, speed);
		setType(TYPE_VIEW);
	}
	
	
	/**
	 * Sets the view (camera) to follow a particular entity within a rectangle, at a set speed
	 * @param	tofollow	The entity to follow
	 */
	public void setView(Entity toFollow) {
		setView(toFollow, null, 1.0f);
	}
	
	/**
	 * Sets the view (camera) to follow a particular entity within a rectangle, at a set speed
	 * @param	tofollow	The entity to follow
	 * @param	within		The rectangle that the view should stay within (if any)
	 * @return	void
	 */
	public void setView(Entity toFollow, Rect within) {
		setView(toFollow, within, 1.0f);
	}
	/**
	 * Sets the view (camera) to follow a particular entity within a rectangle, at a set speed
	 * @param	tofollow	The entity to follow
	 * @param	within		The rectangle that the view should stay within (if any)
	 * @param	speed		Speed at which to follow the entity (1=static with entity, >1=follows entity)
	 * @return	void
	 */
	public void setView(Entity toFollow, Rect within, float speed) {
		mToFollow = toFollow;
		mWithin = within;
		mSpeed = speed;
	}

	@Override
	public void update() {
		
		double dist = FP.distance(mToFollow.x - FP.screen.getWidth() / 2, mToFollow.y - FP.screen.getHeight() / 2, FP.camera.x, FP.camera.y);
		
		float speed = (float) (dist / mSpeed) * FP.elapsed;
		
		FP.stepTowards(this, mToFollow.x - FP.screen.getWidth() / 2, mToFollow.y - FP.screen.getHeight() / 2, speed);
		
		FP.camera.x = x;
		FP.camera.y = y;
		
		if (mWithin != null) {
			if (FP.camera.x < mWithin.left) {
				FP.camera.x = mWithin.left; 
			}
			if (FP.camera.y < mWithin.top) {
				FP.camera.y = mWithin.top; 
			}
			
			if (FP.camera.x + FP.screen.getWidth() > mWithin.left + mWithin.width()) {
				FP.camera.x = mWithin.left + mWithin.width() - FP.screen.getWidth(); 
			}
			
			if (FP.camera.y + FP.screen.getHeight() > mWithin.top + mWithin.height()) {
				FP.camera.y = mWithin.top + mWithin.height() - FP.screen.getHeight(); 
			}
		
		}
	}
	
	
	
	
}
