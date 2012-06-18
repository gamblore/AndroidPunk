package com.gamblore.androidpunk;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.graphics.Image;
import android.util.Log;

public class MyEntity extends Entity {

	private static final String TAG = "MyEntity";
	
	public MyEntity() {
		super();
		setGraphic(new Image(FP.getBitmap(R.drawable.ic_launcher)));

		FP.tween(this, FP.tweenmap("x", 100), 10.0f);
	}

	@Override
	public void update() {
		super.update();
		Log.d(TAG, "My Entity Updates");
	}

}
