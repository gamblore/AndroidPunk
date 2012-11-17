package com.gamblore.androidpunk.test.scripts;

import java.io.IOException;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.graphics.atlas.AtlasText;
import net.androidpunk.graphics.opengl.TextAtlas;
import net.androidpunk.scripts.APScript;
import net.androidpunk.scripts.javascript.JavaScript;
import android.graphics.Typeface;

public class TestScriptEntity extends Entity {

	private String mText;
	private AtlasText mTextGraphic;
	private APScript mScript;
	
	public TestScriptEntity(String text) {
		mText = text;
		Typeface tf = TextAtlas.getFontFromRes(R.raw.font_fixed_bold);
		mTextGraphic = new AtlasText(text, 22, tf);
		setGraphic(mTextGraphic);
		
		try {
			mScript = new JavaScript(FP.context.getAssets().open("scripts/TestScriptEntity.js"), "TestScriptEntity.js");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setText(String text) {
		mText = text;
		mTextGraphic.setText(mText);
	}

	@Override
	public void update() {
		super.update();
		mScript.callFunction("update", this);
	}
	
	
}
