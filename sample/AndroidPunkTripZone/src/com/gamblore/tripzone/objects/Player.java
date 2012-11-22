package com.gamblore.tripzone.objects;

import net.androidpunk.FP;
import net.androidpunk.graphics.atlas.AtlasGraphic;
import net.androidpunk.graphics.atlas.SpriteMap;
import net.androidpunk.utils.Input;
import android.graphics.Color;
import android.graphics.Point;

import com.gamblore.tripzone.Main;
import com.gamblore.tripzone.OgmoEditorWorld;

public abstract class Player extends Physics {

	public static final String TYPE_PLAYER = "Player";
	
	public static boolean INPUT_LEFT = false;
	public static boolean INPUT_RIGHT = false;
	public static boolean INPUT_JUMP = false;
	
	public static boolean LAST_INPUT_LEFT = false;
	public static boolean LAST_INPUT_RIGHT = false;
	public static boolean LAST_INPUT_JUMP = false;
	
	protected static String ANIM_STAND = "stand";
	protected static String ANIM_WALK = "walk";
	protected static String ANIM_JUMP = "jump";
	protected static String ANIM_SLIDE = "slide";
	
	//how fast we accelerate
	public int movement = 1;
	public int jump = 8;
	
	//right = true, left = false
	public boolean direction = true;
	//are we on the ground?
	public boolean onground = false;
	
	//are we walljumping? (0 = no, 1 = left, 2 = right)
	public int walljumping = 0;
	//can we double jump? (false = no, true = yes)
	public boolean doublejump = false;
	
	public boolean dead = false;
	
	public Player(int x, int y) {
		super(x, y);
		
		setupGraphic();
		
		setType(TYPE_PLAYER);
	}
	
	@Override
	public void added() {
		super.added();
		Main.player = this;
	}

	/**
	 * This function should setup a "stand", "walk", "jump", "slide"(onwall) in a spritemap.
	 */
	public abstract void setupGraphic();

	public void setInputs() {
		LAST_INPUT_JUMP = INPUT_JUMP;
		LAST_INPUT_LEFT = INPUT_LEFT;
		LAST_INPUT_RIGHT = INPUT_RIGHT;
		
		INPUT_JUMP = INPUT_LEFT = INPUT_RIGHT = false;
		int touchesCount = Input.getTouchesCount();
		if (touchesCount > 0) {
			Point[] touches = Input.getTouches();
			
			if (touchesCount > 1) {
				INPUT_JUMP = true;
			}
		
			if (touches[0].y < FP.height/8) {
				INPUT_JUMP = true;
			} else if (touches[0].x < FP.width/2) {
				INPUT_LEFT = true;
			} else {
				INPUT_RIGHT = true;
			}
		}
	}
	
	@Override
	public void update() {
		setInputs();
		
		AtlasGraphic ag = (AtlasGraphic)getGraphic();
		if (dead) {
			int color = ag.getColor();
			int alpha = Color.alpha(color);
			alpha -= (int)(0.1 * 255);
			alpha = Math.max(alpha, 0);
			ag.setColor((alpha << 24) | (color & 0x00ffffff));
		} else if (Color.alpha(ag.getColor()) < 255) {
			int color = ag.getColor();
			int alpha = Color.alpha(color);
			alpha += (int)(0.1 * 255);
			alpha = Math.min(alpha, 255);
			ag.setColor((alpha << 24) | (color & 0x00ffffff));
		}
		
		onground = false;
		if (collide(TYPE_SOLID, x,  y +1) != null) {
			onground = true;
			walljumping = 0;
			doublejump = true;
		}
		
		acceleration.x = 0;
		
		if (INPUT_LEFT && speed.x > -maxSpeed.x) {
			acceleration.x = -movement;
			direction = false;
		}
		if (INPUT_RIGHT && speed.x < maxSpeed.x){
			acceleration.x = movement;
			direction = true;
		}
		
		if (!(INPUT_LEFT || INPUT_RIGHT) || Math.abs(speed.x) > maxSpeed.x ) {
			friction();
		}
		
		if (INPUT_JUMP) {
			boolean jumped = false;
			
			//normal jump
			if (onground) {
				speed.y = -jump;
				jumped = true;
			}
			
			//wall jump
			if (collide(TYPE_SOLID, x-1, y) != null && !jumped && walljumping != 3) {
				speed.y = -jump;			//jump up
				speed.x = maxSpeed.x * 2;	//move right fast
				walljumping = 2;			//and set wall jump direction
				jumped = true;				//so we don't "use up" or double jump
			}
			
			//same as above
			if (collide(TYPE_SOLID, x + 1, y) != null && !jumped && walljumping != 3) { 
				speed.y = -jump; 
				speed.x = - maxSpeed.x * 2;
				walljumping = 1;
				jumped = true;
			}
			
			//set double jump to false
			if (!onground && !jumped && !LAST_INPUT_JUMP && doublejump) { 
				speed.y = -jump;
				doublejump = false;
				//set walljumping to 0 so we can move back in any direction again
				//incase we were wall jumping prior to this double jump.
				//if you don't want to allow walljumping after a double jump, set this to 3.
				walljumping = 0;
			} 
		}
		
		gravity();
		
		if (speed.y < 0 && !INPUT_JUMP) {
			gravity(); gravity();
		}
		
		if (ag instanceof SpriteMap) {
			SpriteMap sprite = (SpriteMap)ag;
			
			//set the sprites according to if we're on the ground, and if we are moving or not
			if (direction) {
				sprite.scaleX = Math.abs(sprite.scaleX);
			} else {
				sprite.scaleX = -Math.abs(sprite.scaleX);
			}
			if (onground) {
				if (Math.abs(speed.x) > 0) { 
					sprite.play("walk"); 
				}
				
				if (speed.x == 0) {
					sprite.play("stand"); 
				}
			} else {
				sprite.play("jump"); 
				
				//are we sliding on a wall?
				if (collide(TYPE_SOLID, x - 1, y) != null) {
					sprite.play("slide"); 
				}
				else if (collide(TYPE_SOLID, x + 1, y) != null) {
					sprite.play("slide"); 
				}
			}
		}
		
		//set the motion. We set this later so it stops all movement if we should be stopped
		motion();
		//did we just get.. KILLED? D:
		if (collide("Spikes", x, y) != null && speed.y > 0) {
			//killme!
			killme();
		}
	}
	
	public void killme() {
		dead = true;
		Main.restart = true;
	}
	
}
