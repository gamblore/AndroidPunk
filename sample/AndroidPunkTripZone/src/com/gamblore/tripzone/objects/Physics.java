package com.gamblore.tripzone.objects;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import android.graphics.Point;
import android.graphics.PointF;

public class Physics extends Entity {

	public static final String TYPE_SOLID = "Solid";
	
	protected PointF speed = new PointF(0f, 0f);
	protected PointF acceleration = new PointF(0f, 0f);
	protected PointF friction = new PointF(0.5f, 0.5f);
	protected int slopeHeight = 1;
	protected PointF maxSpeed = new PointF(50.0f, 80f); 
	protected float gravity = 10f;
	
	public Physics(int x, int y) {
		super(x, y);
	}

	@Override
	public void update() {
		super.update();
		
		motion();
		gravity();
	}
	
	/**
	 * Moves this entity at it's current speed (speed.x, speed.y) and increases speed based on acceleration (acceleration.x, acceleration.y)
	 */
	public void motion() {
		motion(true, true);
	}
	
	/**
	 * Moves this entity at it's current speed (speed.x, speed.y) and increases speed based on acceleration (acceleration.x, acceleration.y)
	 * @param	mx		Include horizontal movement
	 * @param	my		Include vertical movement
	 */
	public void motion(boolean mx, boolean my) {
		if (mx) {
			if (!motionx(this, speed.x * FP.elapsed)) {
				speed.x = 0;
			}
			speed.x += acceleration.x * FP.elapsed;
		}
		if (my) {
			if (!motiony(this, speed.y * FP.elapsed)) {
				speed.y = 0;
			}
			speed.y += acceleration.y * FP.elapsed;
		}
	}
	
	/**
	 * Increases this entities speed, based on its gravity (gravity)
	 */
	public void gravity() {
		speed.y += gravity * FP.elapsed;
	}
	
	/**
	 * Slows this entity down, according to its friction (friction.x, friction.y)
	 */
	public void friction() {
		//speed > 0, then slow down
		if ( speed.x > 0 ) {
			speed.x -= friction.x;
			//if we go below 0, stop.
			if ( speed.x < 0 ) {
				speed.x = 0; 
			}
		}
		//speed < 0, then "speed up" (in a sense)
		if ( speed.x < 0 ) {
			speed.x += friction.x;
			//if we go above 0, stop.
			if ( speed.x > 0 ) {
				speed.x = 0; 
			}
		}
	}
	
	/**
	 * Stops entity from moving to fast, according to maxspeed (maxspeed.x, maxspeed.y)
	 */
	public void maxspeed() {
		maxspeed(true, true);
	}
	
	/**
	 * Stops entity from moving to fast, according to maxspeed (mMaxspeed.x, mMaxspeed.y)
	 * @param	mx		Include horizontal movement
	 * @param	my		Include vertical movement
	 */
	public void maxspeed(boolean x, boolean y) {
		if ( x ) {
			if ( Math.abs(speed.x) > maxSpeed.x ) {
				speed.x = maxSpeed.x * FP.sign(speed.x);
			}
		}
		if ( y ) {
			if ( Math.abs(speed.y) > maxSpeed.y ) {
				speed.y = maxSpeed.y * FP.sign(speed.y);
			}
		}
	}
	
	/**
	 * Moves the set entity horizontal at a given speed, checking for collisions and slopes
	 * @param	e		The entity you want to move
	 * @param	speedx	The speed at which the entity should move
	 * @return	true (didn't hit a solid) or false (hit a solid)
	 */
	public boolean motionx(Entity e, float speedx) {
		//check each pixel before moving it
		for (int i = 0; i < Math.abs(speedx); i++) {
			//if we've moved
			boolean moved = false;
			boolean below = true;
			
			if (e.collide(TYPE_SOLID, e.x, e.y + 1) == null) {
				below = false; 
			}
			
			
			//run through how high a slope we can move up
			for (int s = 0; s <= slopeHeight; s++) {
				//if we don't hit a solid in the direction we're moving, move....
				if (e.collide(TYPE_SOLID, e.x + FP.sign(speedx), e.y - s) == null) {
					//increase/decrease positions
					//if the player is in the way, simply don't move (but don't count it as stopping)
					if (e.collide(Player.TYPE_PLAYER, e.x + FP.sign(speedx), e.y - s) == null) {
						e.x += FP.sign(speedx);
					}
					
					//move up the slope
					e.y -= s;
					
					//we've moved
					moved = true;
					
					//stop checking for slope (so we don't fly up into the air....)
					break;
				}
			}
			
			//if we are now in the air, but just above a platform, move us down.
			if (below && e.collide(TYPE_SOLID,e.x, e.y + 1) == null) {
				e.y += 1;
			}
			
			//if we haven't moved, set our speed to 0
			if ( !moved ) {
				return false; 
			}
		}
		return true;
	}
	
	/**
	 * Moves the set entity vertical at a given speed, checking for collisions
	 * @param	e		The entity you want to move
	 * @param	spdy	The speed at which the entity should move
	 * @return	True (didn't hit a solid) or false (hit a solid)
	 */
	public boolean motiony(Entity e, float speedy) {
		//for each pixel that we will move...
		for (int i = 0; i < Math.abs(speedy); i++ ) {
			//if we DON'T collide with solid
			if (e.collide(TYPE_SOLID, e.x, e.y + FP.sign(speedy)) == null) { 
				//if we don't run into a player, them move us
				if (e.collide(Player.TYPE_PLAYER, e.x, e.y + FP.sign(speedy)) == null) { 
					e.y += FP.sign(speedy); 
				//but note that we wont stop our movement if we hit a player.
				}
			} else { 
				//stop movement if we hit a solid
				return false; 
			}
		}
		//hit nothing!
		return true;
	}
	
	/**
	 * Moves an entity of the given type that is on top of this entity (if any). Also moves player if it's on top of the entity on top of this one. (confusing.. eh?).
	 * Mostly used for moving platforms
	 * @param	type	Entity type to check for
	 * @param	speed	The speed at which to move the thing above you
	 */
	public void moveontop(String type, float speed) {
		Entity e = collide(type, x, y - 1);
		if (e != null) {
			motionx(e, speed);
			
			//if the player is on tope of the thing that's being moved, move him/her too.
			if (e instanceof Physics) {
				((Physics)e).moveontop(Player.TYPE_PLAYER, speed);
			}
		}
	}
}
