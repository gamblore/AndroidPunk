package com.gamblore.androidpunk.entities;

import com.gamblore.androidpunk.R;
import com.gamblore.androidpunk.R.drawable;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.FP.TweenOptions;
import net.androidpunk.graphics.SpriteMap;
import net.androidpunk.tweens.misc.AngleTween;
import android.graphics.Bitmap;

public class Exit extends Entity {

	private AngleTween mAngleTween = new AngleTween(null, LOOPING);
	
	public Exit(int x, int y) {
		super(x, y);
		Bitmap blackHole = FP.getBitmap(R.drawable.black_hole);
		SpriteMap map = new SpriteMap(blackHole, blackHole.getWidth()/5, blackHole.getHeight());
		map.add("blink", FP.frames(0, 4), 3);
		setGraphic(map);
		map.play("blink");
		
		mAngleTween.tween(0, 360, 10.0f);
		FP.getWorld().addTween(mAngleTween);
		
		map.scale = 0.75f;
		setHitbox(blackHole.getWidth()/5, blackHole.getHeight());
	}
}
