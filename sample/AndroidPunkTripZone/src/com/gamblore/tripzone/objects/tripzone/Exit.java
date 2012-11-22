package com.gamblore.tripzone.objects.tripzone;

import com.gamblore.tripzone.Main;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.graphics.atlas.SpriteMap;
import net.androidpunk.graphics.opengl.SubTexture;

public class Exit extends Entity {

	public Exit(int x, int y) {
		super(x, y);
		
		SubTexture exit = Main.mAtlas.getSubTexture("Exit");
		SpriteMap sm = new SpriteMap(exit, exit.getWidth()/4, exit.getHeight());
		sm.add("play", FP.frames(0, 3), 10);
		sm.play("play");
		
		setGraphic(sm);
		setHitbox(exit.getWidth()/4, exit.getHeight());
	}

	@Override
	public void update() {
		if (collideWith(Main.player, x, y) != null) {
			Main.finished = true;
		}
	}
	
	
}
