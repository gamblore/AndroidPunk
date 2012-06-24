package com.gamblore.androidpunk;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.World;
import net.androidpunk.FP.TweenOptions;
import net.androidpunk.graphics.Text;
import android.util.Log;

public class MyWorld extends World {

	public MyWorld() {
		add(new MyEntity());
		
		
		Text.size = 35;
		Text t = new Text("OGMO", 0, 0);
		t.setColor(0xffffffff);
		
		Entity e = new Entity(FP.screen.getWidth()/2, -t.getHeight(), t);
		e.x -= t.getWidth()/2;
		FP.tween(e, FP.tweenmap("y", FP.screen.getHeight()/2), 3.0f);
		TweenOptions options = new TweenOptions(ONESHOT, null, null, this);
		FP.tween(t, FP.tweenmap("scale", 1.5f), 1.0f, options);
		add(e);
	}

}
