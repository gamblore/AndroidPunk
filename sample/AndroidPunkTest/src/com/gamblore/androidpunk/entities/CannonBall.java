package com.gamblore.androidpunk.entities;

import com.gamblore.androidpunk.OgmoEditorWorld;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.graphics.opengl.shapes.Shape;

public class CannonBall extends Entity {

	private static final float SPEED = 250f;
	private static float mVelocityX, mVelocityY;

	public CannonBall(int x, int y, float angle) {
		super(x,y);
		
		mVelocityX = (float) Math.cos(angle*FP.RAD) * SPEED;
		mVelocityY = (float) Math.sin(angle*FP.RAD) * SPEED;
		
		Shape circle = Shape.circle(10, 10, 8);
		circle.setColor(0xff221122);
		setGraphic(circle);
		
		setHitbox(16, 16);
		setType(OgmoEditorWorld.TYPE_DANGER);
		setLayer(6);
	}

	@Override
	public void update() {
		super.update();
		
		x += mVelocityX * FP.elapsed;
		y += mVelocityY * FP.elapsed;
		
		OgmoEditorWorld world = (OgmoEditorWorld) FP.getWorld();
		if (collide("level", x, y) != null) {
			getWorld().remove(this);
			collidable = false;
		} else if (world != null && (x < 0 || y < 0 || x > world.getWidth() || y > world.getHeight())) {
			getWorld().remove(this);
			collidable = false;
		}
	}
	
}
