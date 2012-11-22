package com.gamblore.tripzone.objects.tripzone;

import com.gamblore.tripzone.Main;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.graphics.atlas.SpriteMap;
import net.androidpunk.graphics.opengl.SubTexture;

public class Spawner extends Entity {

	public Spawner(int x, int y) {
		super(x, y);
		
		SubTexture spawner = Main.mAtlas.getSubTexture("Spawner");
		SpriteMap sm = new SpriteMap(spawner, spawner.getWidth()/3, spawner.getHeight());
		sm.add("bubble", FP.frames(0, 2), 15);
		
		sm.play("bubble");
		
		setGraphic(sm);
		setLayer(5);
	}
}
