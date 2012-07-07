package com.gamblore.androidpunk;

import net.androidpunk.Engine;
import net.androidpunk.FP;
import net.androidpunk.Sfx;
import android.util.Log;

public class Main extends Engine {

	private static final String TAG = "Game";
	public static Sfx mBonk;
	public static Sfx mJump;
	public static Sfx mDeath;

	public Main(int width, int height, float frameRate, boolean fixed) {
		super(width, height, frameRate, fixed);
		FP.setWorld(new MenuWorld());
	
		mBonk = new Sfx(R.raw.bonk);
		mJump = new Sfx(R.raw.jump);
		mDeath = new Sfx(R.raw.death);
		//FP.setWorld(new OgmoEditorWorld(R.raw.big_4));

	}

	@Override
	public void init() {
		Log.d(TAG, "At init!");
	}
}
