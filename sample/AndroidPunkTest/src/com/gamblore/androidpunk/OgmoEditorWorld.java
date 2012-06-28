package com.gamblore.androidpunk;

import net.androidpunk.Engine;
import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.World;
import net.androidpunk.graphics.TileMap;
import net.androidpunk.masks.Grid;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.gamblore.androidpunk.entities.Exit;
import com.gamblore.androidpunk.entities.Monster;
import com.gamblore.androidpunk.entities.Ogmo;
import com.gamblore.androidpunk.entities.PlayerStart;

import android.util.Log;

public class OgmoEditorWorld extends World {

	private static final String TAG = "MyWorld";
	
	private int mCurrentLevelRes = 0;
	
	private Entity mLevel = new Entity();
	private Ogmo mOgmo = null;
	private PlayerStart mPlayerStart = null;
	private Exit mExit = null;
	
	public OgmoEditorWorld(int resId) {
		parseOgmoEditorLevel(resId);
		mCurrentLevelRes = resId;
	}

	private void parseOgmoEditorLevel(int resId) {
		Document doc = FP.getXML(resId);
		
		NamedNodeMap levelatts = doc.getFirstChild().getAttributes();
		int lWidth = Integer.parseInt(levelatts.getNamedItem("width").getNodeValue());
		int lHeight = Integer.parseInt(levelatts.getNamedItem("height").getNodeValue());
		mLevel.setHitbox(lWidth, lHeight);
		
		NodeList tiles = doc.getElementsByTagName("Tiles");
		if (tiles.getLength() > 0) {
			Node n = tiles.item(0);
			Node child = n.getFirstChild();
			if (child.getNodeType() == Document.TEXT_NODE) {
				String tilescsv = child.getTextContent();
				TileMap tileMap = new TileMap(FP.getBitmap(R.drawable.grass_tiles), FP.screen.getWidth(), FP.screen.getHeight(), 32, 32);
				tileMap.loadFromString(tilescsv);
				mLevel.setGraphic(tileMap);
			}
		}
		NodeList grid = doc.getElementsByTagName("Grid");
		if (grid.getLength() > 0) {
			Node n = grid.item(0);
			Node child = n.getFirstChild();
			if (child.getNodeType() == Document.TEXT_NODE) {
				String gridBitString = child.getTextContent();
				Grid g = new Grid(FP.screen.getWidth(), FP.screen.getHeight(), 32, 32);
				mLevel.setType("level");
				g.loadFromString(gridBitString, "", "\n");
				mLevel.setMask(g);
			}
		}
		add(mLevel);
		
		NodeList objectsList = doc.getElementsByTagName("Objects");
		if (objectsList.getLength() > 0) {
			NodeList objects = objectsList.item(0).getChildNodes();
			for (int i = 0; i < objects.getLength(); i++) {
				Node n = objects.item(i);
				//Log.d(TAG, String.format("tag '%s' in Objects", n.getNodeName()));
				if ("PlayerStart".equals(n.getNodeName())) {
					NamedNodeMap atts = n.getAttributes();
					int x = Integer.parseInt(atts.getNamedItem("x").getNodeValue());
					int y = Integer.parseInt(atts.getNamedItem("y").getNodeValue());
					Log.d(TAG, String.format("New playerstart at %d,%d",x,y) );
					mPlayerStart = new PlayerStart(x, y);
					add(mPlayerStart);
				} else if ("Exit".equals(n.getNodeName())) {
					NamedNodeMap atts = n.getAttributes();
					int x = Integer.parseInt(atts.getNamedItem("x").getNodeValue());
					int y = Integer.parseInt(atts.getNamedItem("y").getNodeValue());
					Log.d(TAG, String.format("New exit at %d,%d",x,y) );
					mExit = new Exit(x, y);
					add(mExit);
				} else if ("Enemy".equals(n.getNodeName())) {
					NamedNodeMap atts = n.getAttributes();
					int x = Integer.parseInt(atts.getNamedItem("x").getNodeValue());
					int y = Integer.parseInt(atts.getNamedItem("y").getNodeValue());
					float speed = 100.0f;
					try { 
						speed = (float)Integer.parseInt(atts.getNamedItem("speed").getNodeValue());
					} catch (Exception e) {}
					Log.d(TAG, String.format("New enemy at %d,%d",x,y) );
					
					Monster m = new Monster(x, y);
					m.setSpeed(speed);
					
					
					NodeList enemyPoints = n.getChildNodes();
					for (int j = 0; j < enemyPoints.getLength(); j++) {
						Node node = enemyPoints.item(j);
						if ("node".equals(node.getNodeName())) {
							NamedNodeMap natts = node.getAttributes();
							int nx = Integer.parseInt(natts.getNamedItem("x").getNodeValue());
							int ny = Integer.parseInt(natts.getNamedItem("y").getNodeValue());
							m.addPoint(nx, ny);
							Log.d(TAG, String.format("Path to %d %d", nx, ny));
						}
					}
					m.start();
					add(m);
				}
				
			}
		}
	}

	@Override
	public void update() {
		super.update();
		
		if (mOgmo == null) {
			Log.d(TAG, "Spwaning Ogmo");
			mOgmo = mPlayerStart.spawn();
			add(mOgmo);
		}
		
		if (mOgmo.collideWith(mExit, mOgmo.x, mOgmo.y) != null) {
			if (mCurrentLevelRes == R.raw.intro_1) {
				FP.setWorld(new OgmoEditorWorld(R.raw.jumping_2));
			} else if (mCurrentLevelRes == R.raw.jumping_2) {
				FP.setWorld(new OgmoEditorWorld(R.raw.enemy_3));
			} else if (mCurrentLevelRes == R.raw.enemy_3) {
				FP.setWorld(new OgmoEditorWorld(R.raw.intro_1));
			}
			remove(mOgmo);
			mOgmo = null;
		}
		if (mOgmo != null) {
			boolean restart = false;
			
			if (mOgmo.y > FP.screen.getHeight()) {
				restart = true;
			} else if (mOgmo.collide("danger", mOgmo.x, mOgmo.y) != null){
				restart = true;
			}

			if (restart) {
				mOgmo = null;
				FP.setWorld(new OgmoEditorWorld(mCurrentLevelRes));
			}
		}
	}
	
}
