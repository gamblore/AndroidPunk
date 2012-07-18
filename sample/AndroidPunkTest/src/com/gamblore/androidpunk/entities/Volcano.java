package com.gamblore.androidpunk.entities;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.graphics.atlas.SpriteMap;
import net.androidpunk.graphics.opengl.SubTexture;

import com.gamblore.androidpunk.Main;

public class Volcano extends Entity {
	private SpriteMap mMap;
	
	private static final String ANIM_LOOP = "Loop";
	public Volcano(int x, int y) {
		super(x, y);
		
		SubTexture volcano = Main.mAtlas.getSubTexture("volcano");
		mMap = new SpriteMap(volcano, (int) volcano.getWidth()/6, (int) volcano.getHeight());
		//mMap.add(ANIM_STANDING, new int[] {0}, 0);
		mMap.add(ANIM_LOOP, FP.frames(0, 4), 4);
		mMap.play(ANIM_LOOP);
	}
}
