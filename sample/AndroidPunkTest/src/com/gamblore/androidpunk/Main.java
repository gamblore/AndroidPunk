package com.gamblore.androidpunk;

import net.androidpunk.Engine;
import net.androidpunk.FP;
import net.androidpunk.Sfx;
import android.util.Log;

public class Main extends Engine {

	private static final String TAG = "Game";
	public static Sfx mBonk;
	public Main(int width, int height, float frameRate, boolean fixed) {
		super(width, height, frameRate, fixed);
		mBonk = new Sfx(R.raw.bonk);
		FP.setWorld(new OgmoEditorWorld(R.raw.intro_1));
	}

	@Override
	public void init() {
		Log.d(TAG, "At init!");
	}
}
