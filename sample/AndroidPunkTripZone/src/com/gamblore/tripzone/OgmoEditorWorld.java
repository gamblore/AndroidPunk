package com.gamblore.tripzone;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.World;
import net.androidpunk.graphics.atlas.TileMap;
import net.androidpunk.graphics.opengl.SubTexture;
import net.androidpunk.masks.Grid;
import net.androidpunk.utils.Data;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.graphics.Rect;
import android.util.Log;

import com.gamblore.tripzone.objects.Physics;
import com.gamblore.tripzone.objects.Player;
import com.gamblore.tripzone.objects.tripzone.Enemy;
import com.gamblore.tripzone.objects.tripzone.Exit;
import com.gamblore.tripzone.objects.tripzone.Spawner;
import com.gamblore.tripzone.objects.tripzone.TripGuy;

public class OgmoEditorWorld extends World {

	private static final String TAG = "OgmoEditorWorld";
	
	public static final String TYPE_DANGER = "danger";
	
	public static final String LEVEL_FOLDER = "levels";
	
	private int mCurrentLevel = 1;
	private int mNumLevels;
	
	private int mWidth = 0, mHeight = 0;
	
	private Entity mLevel = new Entity();
	
	public static abstract class XMLEntityConstructor {
		public abstract Entity make(World w, Node n);
	}
	
	private static Map<String, XMLEntityConstructor> mXMLEntityConstructors = new TreeMap<String, XMLEntityConstructor>();
	
	static {
		mXMLEntityConstructors.put("Guy", new XMLEntityConstructor() {

			@Override
			public Player make(World w, Node n) {
				NamedNodeMap atts = n.getAttributes();
				int x = Integer.parseInt(atts.getNamedItem("x").getNodeValue());
				int y = Integer.parseInt(atts.getNamedItem("y").getNodeValue());
				
				TripGuy tg = new TripGuy(x, y);
				w.add(Main.view = new View(tg, new Rect(0,0, FP.width, FP.height), 10));
				return tg;
			}
		});
		
		mXMLEntityConstructors.put("Spawner", new XMLEntityConstructor() {

			@Override
			public Spawner make(World w, Node n) {
				NamedNodeMap atts = n.getAttributes();
				int x = Integer.parseInt(atts.getNamedItem("x").getNodeValue());
				int y = Integer.parseInt(atts.getNamedItem("y").getNodeValue());
				
				Spawner s = new Spawner(x, y);
				return s;
			}
		});
		
		mXMLEntityConstructors.put("Enemy", new XMLEntityConstructor() {

			@Override
			public Enemy make(World w, Node n) {
				NamedNodeMap atts = n.getAttributes();
				int x = Integer.parseInt(atts.getNamedItem("x").getNodeValue());
				int y = Integer.parseInt(atts.getNamedItem("y").getNodeValue());
				
				Enemy e = new Enemy(x, y);
				
				return e;
			}
		});
		
		mXMLEntityConstructors.put("Exit", new XMLEntityConstructor() {

			@Override
			public Exit make(World w, Node n) {
				NamedNodeMap atts = n.getAttributes();
				int x = Integer.parseInt(atts.getNamedItem("x").getNodeValue());
				int y = Integer.parseInt(atts.getNamedItem("y").getNodeValue());
				
				Exit e = new Exit(x, y);
				return e;
			}
		});
	}
	
	public OgmoEditorWorld(int level) {
		mCurrentLevel = level;
		Data.getData().edit().putInt(Main.DATA_CURRENT_LEVEL, mCurrentLevel).commit();
		String levels[] = FP.getAssetList(LEVEL_FOLDER);
		mNumLevels = levels.length;
		String theLevel = String.format("%d_", level);
		Log.d(TAG, String.format("%d levels looking for level %s", mNumLevels, theLevel));
		for (String l : levels) {
			Log.d(TAG, l);
			if (l.startsWith(theLevel)) {
				parseOgmoEditorLevel(LEVEL_FOLDER + File.separator + l);
				break;
			}
		}

	}
	
	private void parseOgmoEditorLevel(String assetFilename) {
		Log.d(TAG, "Parsing " + assetFilename);
		Document doc = FP.getXML(assetFilename);
		parseOgmoEditorLevel(doc);
	}
	
	private void parseOgmoEditorLevel(int resId) {
		Document doc = FP.getXML(resId);
		parseOgmoEditorLevel(doc);
	}
	
	private void parseOgmoEditorLevel(Document doc) {
		if (doc == null) {
			return;
		}
		NamedNodeMap levelatts = doc.getFirstChild().getAttributes();
		mWidth = Integer.parseInt(levelatts.getNamedItem("width").getNodeValue());
		mHeight = Integer.parseInt(levelatts.getNamedItem("height").getNodeValue());
		Log.d(TAG, String.format("Level is %dx%d",mWidth,mHeight));
		mLevel.setHitbox(mWidth, mHeight);
		
		NodeList cameras = doc.getElementsByTagName("camera");
		if (cameras.getLength() > 0) {
			Node cameraElement = cameras.item(0);
			NamedNodeMap catts = cameraElement.getAttributes();
			int x = Integer.parseInt(catts.getNamedItem("x").getNodeValue());
			int y = Integer.parseInt(catts.getNamedItem("y").getNodeValue());
			Log.d(TAG, String.format("camera at %d,%d",x,y) );
			
			camera.set(x, y); 
		} else {
			camera.set(0, 0);
		}
		
		NodeList tiles = doc.getElementsByTagName("Tiles");
		for (int i = 0; i < tiles.getLength(); i++) {
			Node n = tiles.item(i);
			
			int resWidth, resHeight;
			SubTexture st;
			
			st = Main.mAtlas.getSubTexture("Block");
			resWidth = st.getWidth()/2;
			resHeight = st.getHeight();
			
			Node child = n.getFirstChild();
			if (child.getNodeType() == Document.TEXT_NODE) {
				String tilescsv = child.getTextContent();
				TileMap tileMap = new TileMap(st, mWidth, mHeight, resWidth, resHeight);
				tileMap.loadFromString(tilescsv);
				mLevel.setGraphic(tileMap);
			}
		}
		NodeList grid = doc.getElementsByTagName("Grid");
		for (int i = 0; i < grid.getLength(); i++) {
			Node n = grid.item(i);
			Node child = n.getFirstChild();
			if (child.getNodeType() == Document.TEXT_NODE) {
				String gridBitString = child.getTextContent();
				Grid g = new Grid(mWidth, mHeight, 32, 32);
				mLevel.setType(Physics.TYPE_SOLID);
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
				if (mXMLEntityConstructors.containsKey(n.getNodeName())) {
					add(mXMLEntityConstructors.get(n.getNodeName()).make(this, n));
				} 
			}
		}
	}
	
	public int getWidth() {
		return mWidth;
	}
	
	public int getHeight() {
		return mHeight;
	}

	@Override
	public void update() {
		super.update();
		
		if (Main.restart) {
			Data.getData().edit().putInt(Main.DATA_DEATHS, Data.getData().getInt(Main.DATA_DEATHS, 0) + 1).commit();
			Main.restart = false;
			FP.setWorld(new OgmoEditorWorld(mCurrentLevel));
		}
		
		if (Main.finished) {
			Main.finished = false;
			if (Data.getData().getInt(Main.DATA_CURRENT_LEVEL, 1)+1 > FP.getAssetList(OgmoEditorWorld.LEVEL_FOLDER).length) {
				FP.setWorld(new OgmoEditorWorld(1));
			} else {
				FP.setWorld(new OgmoEditorWorld(mCurrentLevel + 1));
			}
		}
	}
	
}
