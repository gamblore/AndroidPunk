package com.gamblore.androidpunk.entities;

import net.androidpunk.FP;
import net.androidpunk.graphics.atlas.GraphicList;
import net.androidpunk.graphics.atlas.SpriteMap;
import net.androidpunk.graphics.atlas.TiledSpriteMap;
import net.androidpunk.graphics.opengl.SubTexture;

import com.gamblore.androidpunk.Main;

public class Lightning extends StaticDanger {
	
	private static final String TAG = "Lightning";
	
	private TiledSpriteMap mMap;
	private SpriteMap mBallMap;
	
	private static final String ANIM_SHOCK = "shock";
	
	public Lightning(int x, int y, int width, int angle) {
		this(x, y, width, 32, angle);
	}
	
	public Lightning(int x, int y, int width, int height, int angle) {
		super(x, y, width, height, angle);
		
		SubTexture lightning = Main.mAtlas.getSubTexture("lightning");
		SubTexture lightningBall = Main.mAtlas.getSubTexture("lightning_ball");
		
		int gridWidth = lightning.getWidth()/5;
		mMap = new TiledSpriteMap(lightning, gridWidth, (int) lightning.getHeight(), width, height);
		//mMap.add(ANIM_STANDING, new int[] {0}, 0);
		mMap.add(ANIM_SHOCK, FP.frames(0, 4), 15);
		mMap.play(ANIM_SHOCK);
		
		mBallMap = new SpriteMap(lightningBall, lightningBall.getWidth()/4, lightningBall.getHeight());
		
		switch(angle) {
		case 0:
			mBallMap.x = -lightningBall.getWidth()/4;
			mBallMap.y = 0;
			break;
		case 90:
			mBallMap.x = -lightningBall.getWidth()/4;
			mBallMap.y = -lightningBall.getHeight();
			break;
		case 180:
			mBallMap.x = 0;
			mBallMap.y = -lightningBall.getHeight();
			break;
		case 270:
			mBallMap.x = 0;
			mBallMap.y = 0;
			break;
		}
		mBallMap.add(ANIM_SHOCK, FP.frames(0, 3), 15);
		mBallMap.play(ANIM_SHOCK);
		
		setGraphic(new GraphicList(mMap, mBallMap));
	}

	@Override
	public void toggleEnabled() {
		collidable = !collidable;
		mMap.visible = collidable;
	}
}
