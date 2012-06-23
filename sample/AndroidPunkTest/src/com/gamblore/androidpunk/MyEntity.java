package com.gamblore.androidpunk;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.graphics.Image;
import net.androidpunk.graphics.SpriteMap;
import android.util.Log;

public class MyEntity extends Entity {

	private static final String TAG = "MyEntity";
	
	public MyEntity() {
		super();
		SpriteMap map = new SpriteMap(FP.getBitmap(R.drawable.ogmo), 30, 30);
		map.add("Walking", new int[] {0, 1, 2, 3, 4 ,5}, 2);
		map.play("Walking");
		setGraphic(map);
		
		
		//setGraphic(new Image(FP.getBitmap(R.drawable.ic_launcher)));

		//FP.tween(this, FP.tweenmap("x", 100), 10.0f);
	}

	@Override
	public void update() {
		super.update();
		Log.d(TAG, "My Entity Updates");
	}

}
