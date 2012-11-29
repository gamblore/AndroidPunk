package com.gamblore.androidpunk.entities;

import net.androidpunk.Entity;
import net.androidpunk.graphics.atlas.TiledImage;
import net.androidpunk.graphics.opengl.SubTexture;
import net.androidpunk.tweens.motion.LinearPath;
import android.graphics.Point;

import com.gamblore.androidpunk.Main;

public class Platform extends Entity {

	public static final String TYPE = "platform";
	
	private TiledImage mGraphic;
	
	private LinearPath mPath;
	private float mSpeed = 100.0f;
	
	private final Point mDelta = new Point();
	
	public Platform(int x, int y,int width) {
		this(x, y, width, 100.0f);
	}
	
	public Platform(int x, int y, int width, float speed) {
		super(x,y);
		
		SubTexture platform = Main.mAtlas.getSubTexture("platform");
		
		mGraphic = new TiledImage(platform, width, platform.getHeight());
		
		setType(TYPE);
		
		setHitbox(width, platform.getHeight());
		
		setGraphic(mGraphic);
	}
	
	@Override
	public void update() {
		super.update();
		
		int lastX = x;
		int lastY = y;
		
		if (mPath != null) {
			mDelta.set((int)mPath.x - lastX, (int)mPath.y - lastY);
			moveontop(Ogmo.TYPE_PLAYER);
			x = (int)mPath.x;
			y = (int)mPath.y;
		}
		
	}
	
	public Point getDelta() {
		return mDelta;
	}
	
	public void addPoint(int x, int y) {
		if (mPath == null) {
			mPath = new LinearPath(null, LOOPING);
			mPath.addPoint(this.x, this.y);
		}
		mPath.addPoint(x, y);
	}
	
	public void start() {
		if (mPath != null) {
			mPath.setMotionSpeed(mSpeed);
			addTween(mPath);
		}
	}
	
	/**
	 * Moves an entity of the given type that is on top of this entity (if any). Also moves player if it's on top of the entity on top of this one. (confusing.. eh?).
	 * Mostly used for moving platforms
	 * @param	type	Entity type to check for
	 */
	public void moveontop(String type) {
		
		if (Math.abs(mDelta.x) != 0) {
			Entity e = collide(type, x+mDelta.x, y);
			if (e != null) {
				e.x += mDelta.x;
				e.y += mDelta.y;
			}
		}
		Entity e = collide(type, x, y - 1);
		if (e != null) {
			e.x += mDelta.x;
			e.y += mDelta.y;
		}
	}
}
