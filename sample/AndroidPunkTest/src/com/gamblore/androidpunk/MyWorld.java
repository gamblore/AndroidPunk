package com.gamblore.androidpunk;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.World;
import net.androidpunk.graphics.Text;
import android.util.Log;

public class MyWorld extends World {

	public MyWorld() {
		add(new MyEntity());
		
		
		Text.size = 20;
		Text t = new Text("OGMO", 0, 0);
		t.setColor(0xffffffff);
		
		Entity e = new Entity(FP.screen.getWidth()/2, 0, t);
		e.x -= t.getWidth()/2;
		FP.tween(e, FP.tweenmap("y", FP.screen.getHeight()/2), 3.0f);
		Log.d("MyWorld", "Text at " + e.x + " " + e.y);
		add(e);
	}

}
