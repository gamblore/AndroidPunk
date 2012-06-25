package com.gamblore.androidpunk;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.FP.TweenOptions;
import net.androidpunk.World;
import net.androidpunk.graphics.Text;
import net.androidpunk.graphics.TileMap;
import net.androidpunk.masks.Grid;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

public class MyWorld extends World {

	private static final String TAG = "MyWorld";
	
	private Entity mCollision = new Entity();
	
	public MyWorld() {
		
		parseOgmoEditorLevel(R.raw.level1);
		
		add(new MyEntity());
		
		
		Text.size = 35;
		Text t = new Text("OGMO", 0, 0);
		t.setColor(0xffffffff);
		
		Entity e = new Entity(FP.screen.getWidth()/2, -t.getHeight(), t);
		e.x -= t.getWidth()/2;
		FP.tween(e, FP.tweenmap("y", FP.screen.getHeight()/2), 3.0f);
		TweenOptions options = new TweenOptions(ONESHOT, null, null, this);
		FP.tween(t, FP.tweenmap("scale", 1.5f), 1.0f, options);
		add(e);
	}

	
	private void parseOgmoEditorLevel(int resId) {
		Document doc = FP.getXML(resId);
		Node level = doc.getFirstChild();
		Log.d(TAG, "Head by " + level.getNodeName());
		Node n = level.getFirstChild();
		Log.d(TAG, "First child" + n.getNodeName());
		do {
			if (n.getNodeType() == Document.TEXT_NODE) {
				continue;
			}
			
			if ("Collisions".equals(n.getNodeName())) {
				String collision = n.getFirstChild().getTextContent();
				
				Grid g = new Grid(FP.screen.getWidth(), FP.screen.getHeight(), 32, 32);
				g.loadFromString(collision, "", "\n");
				mCollision.setMask(g);
				
			} else if ("Tiles".equals(n.getNodeName())) {
				String tiles = n.getFirstChild().getTextContent();
				//Log.d(TAG, tiles);
				
				
				TileMap tileMap = new TileMap(FP.getBitmap(R.drawable.grass_tiles), FP.screen.getWidth(), FP.screen.getHeight(), 32, 32);
				tileMap.loadFromString(tiles);
				Log.d(TAG, tileMap.saveToString());
				mCollision.setGraphic(tileMap);
				
			} else if ("Entities".equals(n.getNodeName())) {
				
			}
			Log.d(TAG, n.getNodeName());
		} while ((n = n.getNextSibling()) != null);
		
		add(mCollision);
	}
}
