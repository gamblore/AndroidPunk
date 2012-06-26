package com.gamblore.androidpunk.entities;

import net.androidpunk.Entity;

public class PlayerStart extends Entity {

	
	public PlayerStart(int x, int y) {
		super(x,y);
	}
	
	public Ogmo spawn() {
		Ogmo o = new Ogmo(x, y);
		return o;
	}
}
