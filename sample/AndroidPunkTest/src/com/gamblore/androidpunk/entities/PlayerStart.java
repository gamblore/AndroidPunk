package com.gamblore.androidpunk.entities;

import net.androidpunk.Entity;
import net.androidpunk.FP;

import com.gamblore.androidpunk.OgmoEditorWorld;

public class PlayerStart extends Entity {

	
	public PlayerStart(int x, int y) {
		super(x,y);
	}
	
	public Ogmo spawn() {
		Ogmo o = new Ogmo(x, y);
		return o;
	}

	@Override
	public void update() {
		if (OgmoEditorWorld.mOgmo == null) {
			OgmoEditorWorld.mOgmo = spawn();
			getWorld().add(OgmoEditorWorld.mOgmo);
		}
	}
}
