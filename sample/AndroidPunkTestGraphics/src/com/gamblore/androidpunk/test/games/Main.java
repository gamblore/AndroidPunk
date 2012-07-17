package com.gamblore.androidpunk.test.games;

import net.androidpunk.Engine;
import net.androidpunk.FP;
import net.androidpunk.graphics.opengl.Atlas;
import android.util.Log;

public class Main extends Engine {

	private static final String TAG = "Game";
	
	private static Atlas mAtlas;

	public Main(int width, int height, float frameRate, boolean fixed) {
		super(width, height, frameRate, fixed);
		
		mAtlas = new Atlas("textures/texture1.xml");
		
		FP.setWorld(new GraphicsWorld());

	}

	public static Atlas getAtlas() {
		return mAtlas;
	}
	
	@Override
	public void init() {
		Log.d(TAG, "At init!");
	}
}
