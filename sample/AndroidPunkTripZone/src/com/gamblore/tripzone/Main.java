package com.gamblore.tripzone;

import net.androidpunk.Engine;
import net.androidpunk.FP;
import net.androidpunk.graphics.opengl.Atlas;
import net.androidpunk.utils.Data;

import com.gamblore.tripzone.objects.Player;

public class Main extends Engine {

	public static final String DATA_CURRENT_LEVEL = "current_level";
	public static final String DATA_DEATHS = "deaths";
	
	public static Atlas mAtlas;
	
	public static View view;
	public static Player player;
	
	public static boolean restart = false;
	public static boolean finished = false;
	
	public Main(int width, int height, float frameRate, boolean fixed) {
		super(width, height, frameRate, fixed);
		
		mAtlas = new Atlas("textures/texture1.xml");
		if (Data.getData().getInt(Main.DATA_CURRENT_LEVEL, 1) > FP.getAssetList(OgmoEditorWorld.LEVEL_FOLDER).length) {
			FP.setWorld(new OgmoEditorWorld(1));
		} else {
			FP.setWorld(new OgmoEditorWorld(Data.getData().getInt(Main.DATA_CURRENT_LEVEL, 1)));
		}
	}

}
