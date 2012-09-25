package com.gamblore.androidpunk.entities;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.graphics.atlas.SpriteMap;
import net.androidpunk.graphics.opengl.SubTexture;
import net.androidpunk.tweens.motion.LinearPath;

import com.gamblore.androidpunk.Main;
import com.gamblore.androidpunk.OgmoEditorWorld;

public class Monster extends Entity {
	
	
	private SpriteMap mMap;
	
	private static final String ANIM_WALKING = "walking";
	
	private LinearPath mPath;
	private float mSpeed = 100.0f;
	
	private boolean mMoving;
	
	public Monster(int x, int y) {
		super(x, y);
		
		SubTexture enemy = Main.mAtlas.getSubTexture("enemy");
		mMap = new SpriteMap(enemy, (int) enemy.getWidth()/6, (int) enemy.getHeight());
		//mMap.add(ANIM_STANDING, new int[] {0}, 0);
		mMap.add(ANIM_WALKING, FP.frames(0, 5), 20);
		setStanding();
		//mMap.play(ANIM_STANDING);
		setGraphic(mMap);
		//setGraphic(new Image(FP.getBitmap(R.drawable.ogmo), new Rect(45,0,90,45)));
		mMap.play(ANIM_WALKING);
		setHitbox((int) enemy.getWidth()/6, (int) enemy.getHeight());
		setType(OgmoEditorWorld.TYPE_DANGER);
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

	private void setStanding() {
		mMap.setFrame(0);
	}
	
	public void setSpeed(float speed) {
		mSpeed = speed;
		if (mPath != null) {
			mPath.setMotionSpeed(mSpeed);
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
			mPath.setMotionSpeed(mSpeed);
			addTween(mPath);
		}
	}
}