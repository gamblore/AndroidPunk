package com.gamblore.androidpunk;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.FP.TweenOptions;
import net.androidpunk.Tween;
import net.androidpunk.graphics.SpriteMap;
import net.androidpunk.utils.Input;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;

public class MyEntity extends Entity {

	private static final String TAG = "MyEntity";
	
	private Tween tween;
	
	public MyEntity() {
		super();
		
		Bitmap ogmo = FP.getBitmap(R.drawable.ogmo);
		SpriteMap map = new SpriteMap(ogmo, (int) ogmo.getWidth()/6, (int) ogmo.getHeight());
		map.add("Walking", new int[] {0, 1, 2, 3, 4 ,5}, 20);
		map.play("Walking");
		setGraphic(map);
		//setGraphic(new Image(FP.getBitmap(R.drawable.ogmo), new Rect(45,0,90,45)));
		
		x = -ogmo.getWidth();
		y = FP.screen.getHeight() - ogmo.getHeight();

		//setGraphic(new Image(FP.getBitmap(R.drawable.ic_launcher)));

		TweenOptions options = new TweenOptions(LOOPING, null, null, this);
		tween = FP.tween(this, FP.tweenmap("x", 845), 5.0f, options);
		//tween = FP.tween(getGraphic(), FP.tweenmap("angle", 360), 2.0f, options);
	}

	@Override
	public void update() {
		super.update();
		if (Input.mousePressed) {
			
			Point points[] = Input.getTouches();
			int count = Input.getTouchesCount();
			for (int i = 0; i < count; i++) {
				int x = points[i].x;
				int y = points[i].y;
				Log.d(TAG, String.format("pointer down at %d, %d", x, y));
			}
		}
		
	}
	
}
