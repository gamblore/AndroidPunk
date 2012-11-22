package com.gamblore.tripzone.objects;

import java.util.Vector;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.World;
import net.androidpunk.flashcompat.OnCompleteCallback;
import net.androidpunk.graphics.opengl.shapes.Shape;
import net.androidpunk.tweens.misc.ColorTween;

public class Transition extends Entity {
	
	private World mGotoWorld;
	private Shape mGraphic = Shape.rect(0, 0, FP.width, FP.height);
	private ColorTween mColorTween;
	public Transition(World gotoWorld) {
		mGotoWorld = gotoWorld;
		
		mGraphic.setColor(0x00000000);
		setGraphic(mGraphic);
		
		mColorTween = new ColorTween(new OnCompleteCallback() {
			@Override
			public void completed() {
				FP.setWorld(mGotoWorld);
				
			}
		}, ONESHOT);
		
		addTween(mColorTween, false);
	}
	
	public void start(float time) {
		Vector<Entity> entities = new Vector<Entity>();
		FP.getWorld().getAll(entities);
		
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).active = false;
		}
		
		mColorTween.tween(time, 0x00000000, 0xff000000);
	}
}
