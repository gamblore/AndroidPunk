package com.gamblore.androidpunk.test.scripts;

import net.androidpunk.Entity;
import net.androidpunk.graphics.atlas.AtlasText;
import net.androidpunk.graphics.opengl.TextAtlas;
import android.graphics.Typeface;

public class TestScriptEntity extends Entity {

	public TestScriptEntity(String text) {
		Typeface tf = TextAtlas.getFontFromRes(R.raw.font_fixed_bold);
		AtlasText mText = new AtlasText(text, 22, tf);
		setGraphic(mText);
	}
}
