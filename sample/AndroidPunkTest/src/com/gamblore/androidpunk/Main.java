package com.gamblore.androidpunk;

import net.androidpunk.Engine;
import net.androidpunk.FP;
import net.androidpunk.Sfx;
import net.androidpunk.graphics.Text;
import net.androidpunk.graphics.opengl.Atlas;
import android.graphics.Typeface;
import android.util.Log;

public class Main extends Engine {

	private static final String TAG = "Game";
	public static Sfx mBonk;
	public static Sfx mJump;
	public static Sfx mDeath;
	
	public static final String DATA_CURRENT_LEVEL = "current_level";
	
	public static Atlas mAtlas;
	public static Typeface mTypeface;

	public Main(int width, int height, float frameRate, boolean fixed) {
		super(width, height, frameRate, fixed);
		
		mAtlas = new Atlas("textures/texture1.xml");
		mTypeface = Text.getFontFromRes(R.raw.font_fixed_bold);
		
		FP.setWorld(new MenuWorld());
	
		mBonk = new Sfx(R.raw.bonk);
		mJump = new Sfx(R.raw.jump);
		mDeath = new Sfx(R.raw.death);
	}

	@Override
	public void init() {
		Log.d(TAG, "At init!");
	}
}

