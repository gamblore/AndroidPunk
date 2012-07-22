package com.gamblore.androidpunk.entities;

import com.gamblore.androidpunk.Main;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.graphics.atlas.SpriteMap;
import net.androidpunk.graphics.opengl.SubTexture;
import net.androidpunk.tweens.motion.LinearPath;
import net.androidpunk.tweens.motion.QuadPath;
import net.androidpunk.utils.Ease;

public class Bird extends Entity {

	private static final String ANIM_FLAPPING = "flapping";
	private SpriteMap mMap;
	private LinearPath mPath;
	
	public Bird(int x, int y) {
		super(x, y);
		
		SubTexture bird = Main.mAtlas.getSubTexture("bird");
		mMap = new SpriteMap(bird, (int) bird.getWidth()/4, (int) bird.getHeight());
		mMap.add(ANIM_FLAPPING, FP.frames(0, 3), 10);
		mMap.play(ANIM_FLAPPING);
		setGraphic(mMap);
		
		setHitbox(mMap.getFrameWidth(), mMap.getFrameHeight());
		setType("danger");
	}
	
	@Override
	public void update() {
		super.update();
		
		int lastX = x;
		if (mPath != null) {
			x = (int)mPath.x;
			y = (int)mPath.y;
		}
		if (lastX > x) {
			mMap.scaleX = -1;
		} else if (lastX < x) {
			mMap.scaleX = 1;
		}
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
			mPath.setMotionSpeed(100f);
			addTween(mPath);
		}
	}
}
