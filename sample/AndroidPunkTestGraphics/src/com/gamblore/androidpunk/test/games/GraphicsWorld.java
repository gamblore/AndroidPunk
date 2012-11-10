package com.gamblore.androidpunk.test.games;

import java.io.IOException;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.Graphic;
import net.androidpunk.World;
import net.androidpunk.graphics.atlas.AtlasText;
import net.androidpunk.graphics.atlas.Backdrop;
import net.androidpunk.graphics.atlas.Emitter;
import net.androidpunk.graphics.atlas.GraphicList;
import net.androidpunk.graphics.atlas.Image;
import net.androidpunk.graphics.atlas.SpriteMap;
import net.androidpunk.graphics.atlas.Stamp;
import net.androidpunk.graphics.atlas.TileMap;
import net.androidpunk.graphics.atlas.TiledImage;
import net.androidpunk.graphics.atlas.TiledSpriteMap;
import net.androidpunk.graphics.opengl.SubTexture;
import net.androidpunk.graphics.opengl.TextAtlas;
import net.androidpunk.graphics.opengl.shapes.Shape;
import net.androidpunk.script.LuaScript;
import net.androidpunk.utils.Input;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

public class GraphicsWorld extends World {

	private static final String TAG = "GraphicsWorld";
	
	private Backdrop mBackdrop;
	//private CanvasGraphic mCanvasGraphic;
	private Emitter mEmitter;
	private Image mImage;
	private SpriteMap mSpriteMap;
	private Stamp mStamp;
	private AtlasText mText;
	private TiledImage mTiledImage1, mTiledImage2;
	private TiledSpriteMap mTiledSpriteMap;
	private TileMap mTileMap;
	
	private int counter = 0;
	private Entity mEntities[] = new Entity[20];
	private int mCurrentEntity = 0;
	
	private LuaScript mLuaScript;
	
	public GraphicsWorld() {
		super();
		
		try {
			mLuaScript = new LuaScript("scripts/test_script.lua");
		} catch (IOException e) {
			e.printStackTrace();
			FP.activity.finish();
		}
		
		for (int i = 0; i < 11; i++) {
			mEntities[i] = new Entity();
		}

		SubTexture spritemap = Main.getAtlas().getSubTexture("ogmo");
		
		//Backdrop
		Graphic mBackdrop = new AwesomeBackdrop();
		//mBackdrop = new Backdrop(Main.getAtlas().getSubTexture("jumper_background"));
		//mBackdrop.scrollX = 0.25f;
		
		SpriteMap spriteMap = new SpriteMap(spritemap, (int) spritemap.getWidth()/6, (int) spritemap.getHeight());
		spriteMap.y = FP.height - spritemap.getHeight();
		spriteMap.add("walking", FP.frames(0, 5), 20);
		spriteMap.play("walking");
		
		mEntities[0].setGraphic(new GraphicList(mBackdrop, spriteMap));
		
		/*
		//CanvasGraphic
		mCanvasGraphic = new CanvasGraphic(FP.width, FP.height);
		FP.rect.set(50,50,100,100);
		mCanvasGraphic.fill(FP.rect, 0xffff0000);
		
		mEntities[1].setGraphic(mCanvasGraphic);
		*/
		
		//Emitter
		SpriteMap spriteMap2 = new SpriteMap(spritemap, (int) spritemap.getWidth()/6, (int) spritemap.getHeight());
		spriteMap2.add("walking", FP.frames(0, 5), 20);
		spriteMap2.play("walking");
		
		mEmitter = new Emitter(Main.getAtlas().getSubTexture("particle"));
		mEmitter.newType("test");
		mEmitter.setAlpha("test", 0xff, 0x22);
		mEmitter.setColor("test", 0xffff0000, 0xff888800);
		mEmitter.setMotion("test", 315, 50, 3.0f, 45, 25, 1f);
		
		mEntities[2].setGraphic(new GraphicList(spriteMap2, mEmitter));
		
		//Image
		mImage = new Image(Main.getAtlas().getSubTexture("jumper_clouds"));
		
		mEntities[3].setGraphic(mImage);
		
		//SpriteMap
		
		mSpriteMap = new SpriteMap(spritemap, (int) spritemap.getWidth()/6, (int) spritemap.getHeight());
		mSpriteMap.add("walking", FP.frames(0, 5), 20);
		mSpriteMap.play("walking");
		
		mEntities[4].setGraphic(mSpriteMap);
		
		//Stamp
		mStamp = new Stamp(Main.getAtlas().getSubTexture("jumper_mobile"));
		
		mEntities[5].setGraphic(mStamp);
		
		
		//Text
		Typeface tf = TextAtlas.getFontFromRes(R.raw.font_fixed_bold);
		//TextAtlas textAtlas = new TextAtlas(32, tf);
		mText = new AtlasText("Hi there!", 33, tf);
		//mText = new AtlasText("Hi there!", textAtlas);
		//mText = new Text("Hi there!", 32, tf);
		//mText.setColor(0xffffffff);
		mEntities[6].setGraphic(mText);
		
		
		//TiledImage
		SubTexture cement = Main.getAtlas().getSubTexture("grey_cement");
		SubTexture grass = Main.getAtlas().getSubTexture("grass");
		mTiledImage1 = new TiledImage(cement, cement.getWidth()*5 , cement.getHeight()*3);
		Rect clipRect = new Rect();
		clipRect.set(grass.getWidth()/6, grass.getHeight()/3, 2*grass.getWidth()/6, 2*grass.getHeight()/3);
		mTiledImage2 = new TiledImage(grass, clipRect.width()*5 , clipRect.height()*3, clipRect);
		mTiledImage2.x = cement.getWidth()*6;
		
		mEntities[7].setGraphic(new GraphicList(mTiledImage1, mTiledImage2));
		
		
		
		//TiledSpriteMap
		SubTexture lightning = Main.getAtlas().getSubTexture("lightning");
		mTiledSpriteMap = new TiledSpriteMap(lightning, lightning.getWidth()/5, lightning.getHeight(), 5*lightning.getWidth()/5, lightning.getHeight());
		mTiledSpriteMap.add("test", FP.frames(0, 4), 15);
		mTiledSpriteMap.play("test");
		mTiledSpriteMap.x = FP.screen.getWidth()/2;
		mTiledSpriteMap.y = FP.screen.getHeight()/2;
		mTiledSpriteMap.scale = 0.75f;
		
		mEntities[8].setGraphic(mTiledSpriteMap);
		
		
		//TileMap
		SubTexture desert = Main.getAtlas().getSubTexture("desert");
		mTileMap = new TileMap(desert, 5*desert.getWidth()/6, 3*desert.getHeight()/3, desert.getWidth()/6, desert.getHeight()/3);
		mTileMap.loadFromString("0,1,1,1,2-6,7,5,7,8-12,13,13,13,14", ",", "-");
		
		mEntities[9].setGraphic(mTileMap);
		
		//Shapes!
		AtlasText shapeText = new AtlasText("SHAPES!", 18, tf);
		shapeText.x = FP.width/2 - shapeText.getWidth()/2;
		shapeText.y = FP.height/2 - 9;
		
		Shape line = Shape.line(25, 25, 115, 115, 4);
		line.setColor(0xffff0000);
		
		Shape circle = Shape.circle(100, 100, 15);
		circle.setColor(0xff00ff00);
		
		Shape rect2 = Shape.rect(0, 0, 30, 30);
		rect2.setColor(0xff0000ff);
		rect2.x = 85;
		rect2.y = 85;
		
		Shape rect = Shape.rect(0, 0, 150, 25);
		rect.x = 100;
		rect.setColor(0xff0000ff);
		
		mEntities[10].setGraphic(new GraphicList(line, rect2, circle, rect, shapeText));
		
		
		add(mEntities[mCurrentEntity]);
	}


	public void next() {
		if (mEntities[mCurrentEntity] != null) {
			remove(mEntities[mCurrentEntity]);
		}
		mCurrentEntity = (mCurrentEntity + 1) % 11;
		if (mEntities[mCurrentEntity] != null) {
			add(mEntities[mCurrentEntity]);
		}
		FP.camera.x = 0;
	}
	
	@Override
	public void update() {
		super.update();
		
		switch(mCurrentEntity) {
		case 0:
			FP.camera.x++;
			if (FP.camera.x > FP.screen.getWidth()) {
				FP.camera.x = 0;
				((GraphicList)mEntities[0].getGraphic()).getChildren().get(1).x = 0;
			}
			if (mLuaScript != null) {
				mLuaScript.callMain(((GraphicList)mEntities[0].getGraphic()).getChildren().get(1));
			}
			//((GraphicList)mEntities[0].getGraphic()).getChildren().get(1).x+=2;
		case 2:
			if (mEmitter.getParticleCount() < 3) {
				mEmitter.emit("test", 25, 25);
			}
			break;
		case 3:
			if (counter > 50) {
				counter = 0;
				mImage.scaleX *= -1;
			}
			
			counter++;
			break;
		
		case 6:
			if (counter > 50) {
				counter = 0;
				mText.setSize(mText.getSize()+1);
			}
			
			counter++;
			break;
		case 8:
			if (counter > 50) {
				counter = 0;
				mTiledSpriteMap.angle += 90;
				if (mTiledSpriteMap.angle == 360) {
					mTiledSpriteMap.angle = 0;
				}
			}
			counter++;
			break;
		default:
			
		}
		if (Input.mousePressed) {
			next();
			Log.d(TAG, "Next " +mCurrentEntity);
		}
	}
	
	
	
}
