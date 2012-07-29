package com.gamblore.androidpunk.entities;

import net.androidpunk.FP;
import net.androidpunk.graphics.atlas.TiledSpriteMap;
import net.androidpunk.graphics.opengl.SubTexture;

import com.gamblore.androidpunk.Main;

public class Lightning extends StaticDanger {
	
	private static final String TAG = "Lightning";
	
	private TiledSpriteMap mMap;
	private static final String ANIM_SHOCK = "shock";
	
	public Lightning(int x, int y, int width, int height, int angle) {
		super(x, y, width, height, angle);
		
		SubTexture lightning = Main.mAtlas.getSubTexture("lightning");
		
		int gridWidth = lightning.getWidth()/5;
		mMap = new TiledSpriteMap(lightning, gridWidth, (int) lightning.getHeight(), width, height);
		//mMap.add(ANIM_STANDING, new int[] {0}, 0);
		mMap.add(ANIM_SHOCK, FP.frames(0, 4), 15);
		mMap.play(ANIM_SHOCK);
		setGraphic(mMap);
	}
}
