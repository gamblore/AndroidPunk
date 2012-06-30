package com.gamblore.androidpunk.entities;

import net.androidpunk.Entity;
import net.androidpunk.graphics.Text;

public class GameText extends Entity {
	
	private Text mText;
	
	public GameText(int x, int y, String text) {
		super();
		setLayer(99);
		
		mText = new Text(text, x, y);
		setGraphic(mText);
	}
	
	
	public void setText(String text) {
		mText.setText(text);
	}
	
	public void setVisible(boolean value) {
		getGraphic().visible = value;
	}
}
