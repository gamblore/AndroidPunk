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
			x = (int)mPath.x;
			y = (int)mPath.y;
		}
		
		mDelta.set(x - lastX, y - lastY);
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
}
