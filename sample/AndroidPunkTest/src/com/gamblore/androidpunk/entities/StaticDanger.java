package com.gamblore.androidpunk.entities;

import net.androidpunk.Entity;
import net.androidpunk.Graphic;
import net.androidpunk.graphics.atlas.AtlasGraphic;
import net.androidpunk.masks.Hitbox;

public class StaticDanger extends Entity {
	
	private int mAngle;
	
	public StaticDanger(int x, int y, int width, int height) {
		this(x, y, width, height, 0);
	}

	public StaticDanger(int x, int y, int width, int height, int angle) {
		super(x, y);
		
		mAngle = angle;
		
		switch(angle) {
		case 0:
			setMask(new Hitbox(width, height));
			break;
		case 90:
			setMask(new Hitbox(height, width, -height, 0));
			break;
		case 180:
			setMask(new Hitbox(width, height, -width, -height));
			break;
		case 270:
			setMask(new Hitbox(height, width, 0, -width));
			break;
		}
		
		setType("danger");
	}

	@Override
	public void setGraphic(Graphic g) {
		super.setGraphic(g);
		if (g instanceof AtlasGraphic) {
			((AtlasGraphic)g).angle = mAngle;
		}
	}
	
}
