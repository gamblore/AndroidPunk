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

	//private AngleTween mAngleTween = new AngleTween(null, LOOPING);
	private SpriteMap mMap;
	
	public Exit(int x, int y) {
		super(x, y);
		Bitmap blackHole = FP.getBitmap(R.drawable.black_hole);
		mMap = new SpriteMap(blackHole, blackHole.getWidth()/5, blackHole.getHeight());
		mMap.add("blink", FP.frames(0, 4), 3);
		setGraphic(mMap);
		mMap.play("blink");
		mMap.centerOrigin();
		
		//mAngleTween.tween(0, 359, 10.0f);
		//addTween(mAngleTween, true);
		
		mMap.scale = 0.75f;
		setHitbox(blackHole.getWidth()/5, blackHole.getHeight());
	}

	@Override
	public void update() {
		super.update();
		mMap.angle += FP.elapsed * 360/10.0f;
		if (mMap.angle > 360) {
			mMap.angle -= 360;
		}
		//mMap.angle = mAngleTween.angle;
	}
	
	
}
