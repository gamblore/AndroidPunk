package com.gamblore.androidpunk.test.games;

import net.androidpunk.Entity;
import net.androidpunk.World;
import net.androidpunk.graphics.opengl.shapes.Shape;

public class GL20World extends World {

	@Override
	public void begin() {
		Shape rect = Shape.rect(0,0,50,50);
		
		rect.setColor(0xffffffff);
		
		add(new Entity(0, 0, rect));
	}

	
	
}


