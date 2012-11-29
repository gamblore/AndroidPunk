package com.gamblore.androidpunk;

import net.androidpunk.Engine;
import net.androidpunk.FP;
import net.androidpunk.Sfx;
import net.androidpunk.debug.Command;
import net.androidpunk.debug.Console;
import net.androidpunk.graphics.atlas.Backdrop;
import net.androidpunk.graphics.opengl.Atlas;
import net.androidpunk.graphics.opengl.TextAtlas;
import net.androidpunk.utils.Data;
import android.graphics.Typeface;
import android.util.Log;

public class Main extends Engine {

	private static final String TAG = "Game";
	public static Sfx mBonk;
	public static Sfx mJump;
	public static Sfx mDeath;
	public static Sfx mBGM;
	
	public static final String DATA_CURRENT_LEVEL = "current_level";
	
	public static final String MUTE_PREF = "mute";
	public static Atlas mAtlas;
	public static Typeface mTypeface;

	public Main(int width, int height, float frameRate, boolean fixed) {
		super(width, height, frameRate, fixed);
		
		mAtlas = new Atlas("textures/texture1.xml");
		mTypeface = TextAtlas.getFontFromRes(R.raw.font_fixed_bold);
		
		FP.setWorld(new MenuWorld());
	
		Log.d(TAG, "Loading sounds");
		
		mBonk = new Sfx(R.raw.bonk);
		mJump = new Sfx(R.raw.jump);
		mDeath = new Sfx(R.raw.death);
		mBGM = new Sfx(R.raw.bgm);
		
		if (FP.debug) {
			Command changeLevel = new Command() {
				
				@Override
				public String execute(String... args) {
					try {
						int level = Integer.parseInt(args[0]);
						FP.getWorld().active = false;
						FP.setWorld(new OgmoEditorWorld(level));
						return "Changing level to "+ level + "\r\n"; 
					} catch (Exception e) {
						e.printStackTrace();
						return "Bad argument to \"level\": " + args[0] + "\r\n"; 
					}
				}
			};
			
			Console.registerCommand("level", changeLevel);
		}
	}

	public static void setMute(boolean mute) {
		Data.getData().edit().putBoolean(MUTE_PREF, mute).commit();
		Sfx.setMasterVolume(mute ? 0 : 1);
	}
	
	public static boolean isMute() {
		return Data.getData().getBoolean(MUTE_PREF, false);
	}
	
	public static Backdrop getLevelBackdrop(int level) {
		Backdrop bd;
		switch(((level-1)/10)+1) {
		case 3: 
			bd = new Backdrop(Main.mAtlas.getSubTexture("background_city"));
			break;
		case 2:
			bd = new Backdrop(Main.mAtlas.getSubTexture("background_desert"));
			break;
		case 1:
			bd = new Backdrop(Main.mAtlas.getSubTexture("background_forest"));
			break;
		default:
			bd = new Backdrop(Main.mAtlas.getSubTexture("background_forest"));
		}
		return bd;
	}
	
	@Override
	public void init() {
		Log.d(TAG, "At init!");
	}
}

