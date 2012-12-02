package com.gamblore.androidpunk.entities;

import com.gamblore.androidpunk.Main;
import com.gamblore.androidpunk.OgmoEditorWorld;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.graphics.atlas.GraphicList;
import net.androidpunk.graphics.atlas.Image;
import net.androidpunk.graphics.atlas.SpriteMap;
import net.androidpunk.graphics.opengl.SubTexture;
import net.androidpunk.utils.TaskTimer;
import net.androidpunk.utils.TaskTimer.OnTimeup;

public class Cannon extends Entity {

	private static final float SHOOT_INTERVAL = 3f;
	
	private Image mCannonImage;
	private Image mBase;
	
	private TaskTimer mTimer;
	
	public Cannon(int x, int y, int angle) {
		this(x, y, angle, SHOOT_INTERVAL);
	}
	
	public Cannon(int x, int y, int angle, float interval) {
		super(x, y);
		SubTexture cannon = Main.mAtlas.getSubTexture("cannon");
		width = (int) cannon.getWidth()/2;
		height = (int) cannon.getHeight();
		
		FP.rect.set(0, 0, width, height);
		mBase = new Image(cannon, FP.rect);
		
		FP.rect.offset(width, 0);
		mCannonImage = new Image(cannon, FP.rect);
		mCannonImage.originX = 16;
		mCannonImage.originY = 16;
		
		mBase.angle = angle;
		
		setGraphic(new GraphicList(mBase, mCannonImage));
		setLayer(5);
		
		mTimer = new TaskTimer(interval, new OnTimeup() {
			@Override
			public void run() {
				shoot();
			}
		});
		mTimer.step(interval/2);
	}
	
	@Override
	public void update() {
		super.update();
		
		if (OgmoEditorWorld.mOgmo != null) {
			setTarget(OgmoEditorWorld.mOgmo);
		}
		mTimer.step(FP.elapsed);
	}
	
	public void setTarget(Entity e) {
		mCannonImage.angle = -(float)FP.angle(x+16, y+16, e.x + e.width/4, e.y + e.height/4);
	}
	
	private void shoot() {
		getWorld().add(new CannonBall(x+8, y+8, -mCannonImage.angle));
	}
	
}
