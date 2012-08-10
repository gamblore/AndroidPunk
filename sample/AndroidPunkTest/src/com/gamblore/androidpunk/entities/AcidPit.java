package com.gamblore.androidpunk.entities;

import net.androidpunk.FP;
import net.androidpunk.graphics.atlas.TiledSpriteMap;
import net.androidpunk.graphics.opengl.SubTexture;

import com.gamblore.androidpunk.Main;

public class AcidPit extends StaticDanger {
	
	private TiledSpriteMap mMap;
	private static final String ANIM_BUBBLE = "bubble";
	private static final int HEIGHT  = 32;
	
	public AcidPit(int x, int y, int width) {
		super(x, y, width, HEIGHT, 0);
		
		SubTexture pit = Main.mAtlas.getSubTexture("acid_pit");
		
		int gridWidth = pit.getWidth()/3;
		mMap = new TiledSpriteMap(pit, gridWidth, HEIGHT, width, height);
		mMap.add(ANIM_BUBBLE, FP.frames(0, 2), 10);
		mMap.play(ANIM_BUBBLE);
		
		setGraphic(mMap);
	}
}
