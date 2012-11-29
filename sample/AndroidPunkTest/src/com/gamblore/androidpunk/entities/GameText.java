package com.gamblore.androidpunk.entities;

import net.androidpunk.Entity;
import net.androidpunk.graphics.atlas.AtlasText;

import com.gamblore.androidpunk.Main;

public class GameText extends Entity {
	
	private AtlasText mText;
	
	public GameText(int x, int y, String text) {
		super();
		setLayer(99);
		
		mText = new AtlasText(text, 20, Main.mTypeface);
		setGraphic(mText);
	}
	
	
	public void setText(String text) {
		mText.setText(text);
	}
	
	public void setVisible(boolean value) {
		getGraphic().visible = value;
	}
}
