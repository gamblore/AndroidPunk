package com.gamblore.androidpunk.entities;

import com.gamblore.androidpunk.R;
import com.gamblore.androidpunk.R.drawable;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.FP.TweenOptions;
import net.androidpunk.graphics.SpriteMap;
import android.graphics.Bitmap;

public class Exit extends Entity {

	public Exit(int x, int y) {
		super(x, y);
		Bitmap blackHole = FP.getBitmap(R.drawable.black_hole);
		SpriteMap map = new SpriteMap(blackHole, blackHole.getWidth()/5, blackHole.getHeight());
		map.add("blink", FP.frames(0, 4), 3);
		setGraphic(map);
		map.play("blink");
		FP.tween(map, FP.tweenmap("angle", 360), 10.0f, new TweenOptions(LOOPING, null, null, this));
		
		map.scale = 0.75f;
		setHitbox(blackHole.getWidth()/5, blackHole.getHeight());
	}
}
