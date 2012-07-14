package com.gamblore.androidpunk.entities;

import android.graphics.Bitmap;

import com.gamblore.androidpunk.R;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.graphics.SpriteMap;

public class Volcano extends Entity {
	private SpriteMap mMap;
	
	private static final String ANIM_LOOP = "Loop";
	public Volcano(int x, int y) {
		super(x, y);
		Bitmap volcano = FP.getBitmap(R.drawable.volcano);
		mMap = new SpriteMap(volcano, (int) volcano.getWidth()/6, (int) volcano.getHeight());
		//mMap.add(ANIM_STANDING, new int[] {0}, 0);
		mMap.add(ANIM_LOOP, FP.frames(0, 4), 4);
		mMap.play(ANIM_LOOP);
	}
}
