package com.gamblore.androidpunk;

import java.io.File;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.World;
import net.androidpunk.graphics.Text;
import net.androidpunk.graphics.atlas.Backdrop;
import net.androidpunk.graphics.atlas.GraphicList;
import net.androidpunk.graphics.atlas.TileMap;
import net.androidpunk.graphics.opengl.SubTexture;
import net.androidpunk.masks.Grid;
import net.androidpunk.utils.Data;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

import com.gamblore.androidpunk.entities.Bird;
import com.gamblore.androidpunk.entities.Exit;
import com.gamblore.androidpunk.entities.Lightning;
import com.gamblore.androidpunk.entities.Monster;
import com.gamblore.androidpunk.entities.Ogmo;
import com.gamblore.androidpunk.entities.PlayerStart;
import com.gamblore.androidpunk.entities.TreeSpikes;
import com.gamblore.androidpunk.entities.Volcano;

public class OgmoEditorWorld extends World {

	private static final String TAG = "OgmoEditorWorld";
	
	private static final String LEVEL_FOLDER = "levels";
	private int mCurrentLevel = 1;
	private int mNumLevels;
	
	private Entity mLevel = new Entity();
	private Ogmo mOgmo = null;
	private PlayerStart mPlayerStart = null;
	private Exit mExit = null;
	
	private int mWidth = 0, mHeight = 0;
	
	private Entity mBackdropEntity;
	
	public OgmoEditorWorld(int level) {
		
		mCurrentLevel = level;
		String levels[] = FP.getAssetList(LEVEL_FOLDER);
		mNumLevels = levels.length;
		String theLevel = String.format("%d_", level);
		//Log.d(TAG, String.format("%d levels looking for level %s", mNumLevels, theLevel));
		for (String l : levels) {
			Log.d(TAG, l);
			if (l.startsWith(theLevel)) {
				parseOgmoEditorLevel(LEVEL_FOLDER + File.separator + l);
				break;
			}
		}
		
		mBackdropEntity = new Entity();
		Backdrop bd = Main.getLevelBackdrop(mCurrentLevel);
		
		Backdrop clouds = new Backdrop(Main.mAtlas.getSubTexture("jumper_clouds"), true, false);
		bd.scale = 2.0f;
		bd.scrollX = 0.55f;
		bd.scrollY = 0.55f;
		bd.setColor(0xff808080);
		clouds.x = -FP.rand(800);
		clouds.scrollX = 0.75f;
		clouds.setColor(0xffb0b0b0);
		clouds.y = FP.screen.getHeight()/3 + (FP.rand(FP.screen.getHeight()/4)-FP.screen.getHeight()/8);
		mBackdropEntity.setGraphic(new GraphicList(bd, clouds));
		mBackdropEntity.setLayer(200);
		add(mBackdropEntity);
	}
	
	private void parseOgmoEditorLevel(String assetFilename) {
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
			String tileset = n.getAttributes().getNamedItem("tileset").getNodeValue();
			int res;
			int resWidth, resHeight;
			SubTexture st;
			
			if ("grass".equals(tileset)) {
				st = Main.mAtlas.getSubTexture("grass");
				resWidth = st.getWidth()/6;
				resHeight = st.getHeight()/3;
			} else if ("desert".equals(tileset)) {
				st = Main.mAtlas.getSubTexture("desert");
				resWidth = st.getWidth()/6;
				resHeight = st.getHeight()/3;
			} else if ("city".equals(tileset)) {
				st = Main.mAtlas.getSubTexture("city");
				resWidth = st.getWidth()/6;
				resHeight = st.getHeight()/3;
			/*	
			} else if ("grass_box_tiles".equals(tileset)) {
				bm = FP.getBitmap(R.drawable.grass_box_tiles);
				resWidth = bm.getWidth()/3;
				resHeight = bm.getHeight()/3;
			*/
			} else {
				st = Main.mAtlas.getSubTexture("grey_cement");
				resWidth = st.getWidth();
				resHeight = st.getHeight();
			}
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
				} else if ("Text".equals(n.getNodeName())) {
					NamedNodeMap atts = n.getAttributes();
					int x = Integer.parseInt(atts.getNamedItem("x").getNodeValue());
					int y = Integer.parseInt(atts.getNamedItem("y").getNodeValue());
					String text = atts.getNamedItem("text").getNodeValue();
					
					Log.d(TAG, String.format("New text %s at %d, %d", text, x, y));
					
					Entity e = new Entity(x, y);
					e.setLayer(100);
					Text t = new Text(text, 20, Main.mTypeface);
					
					e.setGraphic(t);
					add(e);
				} else if ("Lightning".equals(n.getNodeName())) {
					NamedNodeMap atts = n.getAttributes();
					int x = Integer.parseInt(atts.getNamedItem("x").getNodeValue());
					int y = Integer.parseInt(atts.getNamedItem("y").getNodeValue());
					int width = Integer.parseInt(atts.getNamedItem("width").getNodeValue());
					int height = Integer.parseInt(atts.getNamedItem("height").getNodeValue());
					int angle = Integer.parseInt(atts.getNamedItem("angle").getNodeValue());
					
					Lightning l = new Lightning(x, y, width, height, angle);
					
					add(l);
				} else if ("TreeSpikes".equals(n.getNodeName())) {
					NamedNodeMap atts = n.getAttributes();
					int x = Integer.parseInt(atts.getNamedItem("x").getNodeValue());
					int y = Integer.parseInt(atts.getNamedItem("y").getNodeValue());
					int width = Integer.parseInt(atts.getNamedItem("width").getNodeValue());
					int height = Integer.parseInt(atts.getNamedItem("height").getNodeValue());
					int angle = Integer.parseInt(atts.getNamedItem("angle").getNodeValue());
					
					TreeSpikes t = new TreeSpikes(x, y, width, height, angle);
					
					add(t);
				} else if ("Volcano".equals(n.getNodeName())) {
					NamedNodeMap atts = n.getAttributes();
					int x = Integer.parseInt(atts.getNamedItem("x").getNodeValue());
					int y = Integer.parseInt(atts.getNamedItem("y").getNodeValue());
					int angle = Integer.parseInt(atts.getNamedItem("angle").getNodeValue());
					
					Volcano v = new Volcano(x, y, angle);
					
					add(v);
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
					if (enemyPoints.getLength() > 0) {
						m.start();
					}
					
					add(m);
				} else if ("Bird".equals(n.getNodeName())) {
					NamedNodeMap atts = n.getAttributes();
					int x = Integer.parseInt(atts.getNamedItem("x").getNodeValue());
					int y = Integer.parseInt(atts.getNamedItem("y").getNodeValue());
					Log.d(TAG, String.format("New bird at %d,%d",x,y) );
					
					Bird b = new Bird(x, y);
					
					NodeList enemyPoints = n.getChildNodes();
					for (int j = 0; j < enemyPoints.getLength(); j++) {
						Node node = enemyPoints.item(j);
						if ("node".equals(node.getNodeName())) {
							NamedNodeMap natts = node.getAttributes();
							int nx = Integer.parseInt(natts.getNamedItem("x").getNodeValue());
							int ny = Integer.parseInt(natts.getNamedItem("y").getNodeValue());
							b.addPoint(nx, ny);
							Log.d(TAG, String.format("Path to %d %d", nx, ny));
						}
					}
					if (enemyPoints.getLength() > 0) {
						b.addPoint(x, y);
						b.start();
					}
					add(b);
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
		
		if (mOgmo == null) {
			Log.d(TAG, "Spwaning Ogmo");
			mOgmo = mPlayerStart.spawn();
			add(mOgmo);
		}
		
		if (mOgmo.collideWith(mExit, mOgmo.x, mOgmo.y) != null) {
			Log.d(TAG, "Level Compelete");
			if (mCurrentLevel + 1 > mNumLevels) {
				Data.getData().edit().remove(Main.DATA_CURRENT_LEVEL).commit();
				FP.setWorld(new MenuWorld());
			} else {
				FP.setWorld(new OgmoEditorWorld(mCurrentLevel + 1));
				Data.getData().edit().putInt(Main.DATA_CURRENT_LEVEL, mCurrentLevel + 1).commit();
			}
			remove(mOgmo);
			mOgmo = null;
		}
		if (mOgmo != null) {
			boolean restart = false;
			int width = FP.screen.getWidth();
			int height = FP.screen.getHeight();
			// move the camera if you can.
			if (mOgmo.x > camera.x + 2 * width / 3 ) {
				int newLeft = mOgmo.x - 2 * width / 3 ;
				camera.x = Math.min(mLevel.width - width, newLeft);
			} else if (mOgmo.x < camera.x + 1 * width / 3) {
				int newLeft = mOgmo.x - 1 * width/3;
				camera.x = Math.max(0, newLeft);
			}
			
			if (mOgmo.y > camera.y + 2 * height / 3) {
				int newTop = mOgmo.y - 2 * height / 3;
				camera.y = Math.min(mLevel.height - height, newTop);
			} else if (mOgmo.y < camera.y + 1 * height/3) {
				int newTop = mOgmo.y - 1 * height / 3;
				camera.y = Math.max(0, newTop);
			}
			if (mOgmo.y > mLevel.height) {
				restart = true;
			} else if (mOgmo.collide("danger", mOgmo.x, mOgmo.y) != null) {
				Main.mDeath.play();
				restart = true;
			}

			if (restart) {
				mOgmo = null;
				FP.setWorld(new OgmoEditorWorld(mCurrentLevel));
			}
		}
	}
	
}
