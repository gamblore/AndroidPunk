package com.gamblore.androidpunk.test.games;

import net.androidpunk.graphics.atlas.Backdrop;
import net.androidpunk.graphics.atlas.GraphicList;
import net.androidpunk.graphics.opengl.Atlas;

public class AwesomeBackdrop extends GraphicList {
	
	private Atlas mAtlas;
	private Backdrop mSky, mGrass, mTrees1, mTrees2;
	
	public AwesomeBackdrop() {
		super();
		
		mAtlas = new Atlas("textures/texture2.xml");
		mSky = new Backdrop(mAtlas.getSubTexture("sky"), true, false);
		mSky.scrollX = 0.1f;
		
		mTrees2 = new Backdrop(mAtlas.getSubTexture("trees_2"), true, false);
		mTrees2.scrollX = .25f;
		
		mTrees1 = new Backdrop(mAtlas.getSubTexture("trees_1"), true, false);
		mTrees1.scrollX = .5f;
		
		mGrass = new Backdrop(mAtlas.getSubTexture("grass"), true, false);
		mGrass.y = 363;
		
		add(mSky);
		add(mTrees2);
		add(mTrees1);
		add(mGrass);
	}
	
}
