package com.gamblore.androidpunk.entities;

import com.gamblore.androidpunk.Main;
import com.gamblore.androidpunk.OgmoEditorWorld;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.graphics.atlas.GraphicList;
import net.androidpunk.graphics.atlas.Image;
import net.androidpunk.graphics.atlas.SpriteMap;
import net.androidpunk.graphics.opengl.SubTexture;

public class Cannon extends Entity {

	private static final float SHOOT_INTERVAL = 5f;
	
	private Image mCannonImage;
	private float mTimer;
	
	public Cannon(int x, int y, int angle) {
		super(x, y);
		SubTexture cannon = Main.mAtlas.getSubTexture("cannon");
		width = (int) cannon.getWidth()/2;
		height = (int) cannon.getHeight();
		
		FP.rect.set(0, 0, width, height);
		Image mBase = new Image(cannon, FP.rect);
		
		FP.rect.offset(width, 0);
		mCannonImage = new Image(cannon, FP.rect);
		mCannonImage.x = 5;
		mCannonImage.originX = 21;
		mCannonImage.originY = 25;
		
		mBase.angle = angle;
		
		setGraphic(new GraphicList(mBase, mCannonImage));
		setLayer(5);
		mTimer = SHOOT_INTERVAL/2;
	}
	
	@Override
	public void update() {
		super.update();
		
		if (OgmoEditorWorld.mOgmo != null) {
			setTarget(OgmoEditorWorld.mOgmo);
		}
		
		mTimer += FP.elapsed;
		if (mTimer >= SHOOT_INTERVAL) {
			mTimer = 0.0f;
			shoot();
		}
	}
	
	public void setTarget(Entity e) {
		mCannonImage.angle = -(float)FP.angle(x, y, e.x, e.y);
	}
	
	private void shoot() {
		getWorld().add(new CannonBall(x+21, y+25, -mCannonImage.angle));
	}
	
}
