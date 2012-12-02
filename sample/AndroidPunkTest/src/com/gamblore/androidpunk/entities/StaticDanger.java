package com.gamblore.androidpunk.entities;

import java.util.Vector;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.Graphic;
import net.androidpunk.graphics.atlas.AtlasGraphic;
import net.androidpunk.graphics.atlas.GraphicList;
import net.androidpunk.masks.Hitbox;
import net.androidpunk.utils.TaskTimer;

import com.gamblore.androidpunk.OgmoEditorWorld;

public class StaticDanger extends Entity {
	
	private int mAngle;
	
	private TaskTimer mTimer;
	
	public StaticDanger(int x, int y, int width, int height) {
		this(x, y, width, height, 0, 0, 0);
	}
	public StaticDanger(int x, int y, int width, int height, int angle) {
		this(x, y, width, height, angle, 0, 0);
	}
	public StaticDanger(int x, int y, int width, int height, int angle, float timer, float offset) {
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
		
		setType(OgmoEditorWorld.TYPE_DANGER);
		if (timer != 0) {
			mTimer = new TaskTimer(timer, new TaskTimer.OnTimeup() {
				@Override
				public void run() {
					toggleEnabled();
				}
			}, offset);
		}
	}

	@Override
	public void setGraphic(Graphic g) {
		super.setGraphic(g);
		if (g instanceof AtlasGraphic) {
			((AtlasGraphic)g).angle = mAngle;
		} else if (g instanceof GraphicList) {
			GraphicList list = (GraphicList)g;
			Vector<Graphic> vector = list.getChildren();
			for (int i = 0; i <  vector.size(); i++) {
				Graphic item = vector.get(i);
				if (item instanceof AtlasGraphic) {
					((AtlasGraphic)item).angle = mAngle;
				}
			}
		}
	}

	@Override
	public void update() {
		super.update();
		if (mTimer != null) {
			mTimer.step(FP.elapsed);
		}
	}
	
	public void toggleEnabled() {
		collidable = !collidable;
		visible = collidable;
	}
}
