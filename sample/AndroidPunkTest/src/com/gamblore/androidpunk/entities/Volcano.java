package com.gamblore.androidpunk.entities;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.graphics.atlas.SpriteMap;
import net.androidpunk.graphics.opengl.SubTexture;
import net.androidpunk.masks.Hitbox;

import com.gamblore.androidpunk.Main;

public class Volcano extends Entity {
	
	private static final String ANIM_BUBBLE = "Bubble";
	private static final float SPEW_INTERVAL = 2.5f;
	private float mTimer;
	private SpriteMap mMap;
	
	
	
	public Volcano(int x, int y, int angle) {
		super(x, y);
		
		SubTexture volcano = Main.mAtlas.getSubTexture("volcano");
		width = (int) volcano.getWidth()/4;
		height = (int) volcano.getHeight();
		
		mMap = new SpriteMap(volcano, width, height);
		mMap.add(ANIM_BUBBLE, FP.frames(0, 3), 4);
		mMap.play(ANIM_BUBBLE);
		setGraphic(mMap);
		
		mMap.angle = angle;
		
		mTimer = SPEW_INTERVAL/2;
		
	}
	
	@Override
	public void update() {
		super.update();
		mTimer += FP.elapsed;
		if (mTimer >= SPEW_INTERVAL) {
			mTimer = 0.0f;
			spew();
		}
	}
	
	public int getAngle() {
		return (int)mMap.angle;
	}
	
	private void spew() {
		getWorld().add(new FireBall(this));
	}
	
}
