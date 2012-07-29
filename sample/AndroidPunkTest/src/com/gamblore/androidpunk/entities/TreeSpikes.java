package com.gamblore.androidpunk.entities;

import net.androidpunk.graphics.atlas.TiledImage;
import net.androidpunk.graphics.opengl.SubTexture;

import com.gamblore.androidpunk.Main;

public class TreeSpikes extends StaticDanger {
	
	private TiledImage mMap;
	//private TiledSpriteMap mMap;
	
	public TreeSpikes(int x, int y, int width, int height, int angle) {
		super(x, y, width, height, angle);
		
		SubTexture spikes = Main.mAtlas.getSubTexture("tree_spikes");

		mMap = new TiledImage(spikes, width, height);
		//mMap = new TiledSpriteMap(spikes, spikes.getWidth(), spikes.getHeight(), width, height);
		
		setGraphic(mMap);
	}
}
