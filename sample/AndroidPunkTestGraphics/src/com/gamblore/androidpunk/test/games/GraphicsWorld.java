package com.gamblore.androidpunk.test.games;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.World;
import net.androidpunk.graphics.Backdrop;
import net.androidpunk.graphics.CanvasGraphic;
import net.androidpunk.graphics.Emitter;
import net.androidpunk.graphics.GraphicList;
import net.androidpunk.graphics.Image;
import net.androidpunk.graphics.SpriteMap;
import net.androidpunk.graphics.Stamp;
import net.androidpunk.graphics.Text;
import net.androidpunk.graphics.TileMap;
import net.androidpunk.graphics.TiledImage;
import net.androidpunk.graphics.TiledSpriteMap;
import net.androidpunk.utils.Input;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

public class GraphicsWorld extends World {

	private static final String TAG = "GraphicsWorld";
	
	private Backdrop mBackdrop;
	private CanvasGraphic mCanvasGraphic;
	private Emitter mEmitter;
	private Image mImage;
	private SpriteMap mSpriteMap;
	private Stamp mStamp;
	private Text mText;
	private TiledImage mTiledImage1, mTiledImage2;
	private TiledSpriteMap mTiledSpriteMap;
	private TileMap mTileMap;
	
	
	private Entity mEntities[] = new Entity[10];
	private int mCurrentEntity = 0;
	
	public GraphicsWorld() {
		super();
		for (int i = 0; i < 10; i++) {
			mEntities[i] = new Entity();
		}
		
		Bitmap spritemap = FP.getBitmap(R.drawable.ogmo);
		
		//Backdrop
		mBackdrop = new Backdrop(FP.getBitmap(R.drawable.jumper_background));
		mBackdrop.scrollX = 0.25f;
		
		SpriteMap spriteMap = new SpriteMap(spritemap, (int) spritemap.getWidth()/6, (int) spritemap.getHeight());
		spriteMap.add("walking", FP.frames(0, 5), 20);
		spriteMap.play("walking");
		
		mEntities[0].setGraphic(new GraphicList(mBackdrop, spriteMap));
		
		add(mEntities[0]);
		
		//CanvasGraphic
		mCanvasGraphic = new CanvasGraphic(FP.width, FP.height);
		FP.rect.set(50,50,100,100);
		mCanvasGraphic.fill(FP.rect, 0xffff0000);
		
		mEntities[1].setGraphic(mCanvasGraphic);
		
		//Emitter
		SpriteMap spriteMap2 = new SpriteMap(spritemap, (int) spritemap.getWidth()/6, (int) spritemap.getHeight());
		spriteMap2.add("walking", FP.frames(0, 5), 20);
		spriteMap2.play("walking");
		
		mEmitter = new Emitter(FP.getBitmap(R.drawable.particle));
		mEmitter.newType("test");
		mEmitter.setAlpha("test", 0xff, 0x22);
		mEmitter.setColor("test", 0xffff0000, 0xff888800);
		mEmitter.setMotion("test", 315, 50, 3.0f, 45, 25, 1f);
		
		mEntities[2].setGraphic(new GraphicList(spriteMap2, mEmitter));
		
		//Image
		mImage = new Image(FP.getBitmap(R.drawable.jumper_clouds));
		
		mEntities[3].setGraphic(mImage);
		
		//SpriteMap
		
		mSpriteMap = new SpriteMap(spritemap, (int) spritemap.getWidth()/6, (int) spritemap.getHeight());
		mSpriteMap.add("walking", FP.frames(0, 5), 20);
		mSpriteMap.play("walking");
		
		mEntities[4].setGraphic(mSpriteMap);
		
		//Stamp
		mStamp = new Stamp(FP.getBitmap(R.drawable.jumper_mobile));
		
		mEntities[5].setGraphic(mStamp);
		
		//Text
		Text.size = 34;
		mText = new Text("Hi there!", 0, 0);
		mEntities[6].setGraphic(mText);
		
		//TiledImage
		Bitmap cement = FP.getBitmap(R.drawable.grey_cement);
		Bitmap grass = FP.getBitmap(R.drawable.grass);
		mTiledImage1 = new TiledImage(cement, cement.getWidth()*5 , cement.getHeight()*3);
		Rect clipRect = new Rect();
		clipRect.set(grass.getWidth()/6, grass.getHeight()/3, 2*grass.getWidth()/6, 2*grass.getHeight()/3);
		mTiledImage2 = new TiledImage(grass, clipRect.width()*5 , clipRect.height()*3, clipRect);
		mTiledImage2.x = cement.getWidth()*6;
		
		mEntities[7].setGraphic(new GraphicList(mTiledImage1, mTiledImage2));
		
		//TiledSpriteMap
		Bitmap lightning = FP.getBitmap(R.drawable.lightning);
		mTiledSpriteMap = new TiledSpriteMap(lightning, lightning.getWidth()/5, lightning.getHeight(), 5*lightning.getWidth()/5, lightning.getHeight());
		mTiledSpriteMap.add("test", FP.frames(0, 4), 15);
		mTiledSpriteMap.play("test");
		
		mEntities[8].setGraphic(mTiledSpriteMap);
		
		//TileMap
		Bitmap desert = FP.getBitmap(R.drawable.desert);
		mTileMap = new TileMap(desert, 5*desert.getWidth()/6, 5*desert.getHeight()/3, desert.getWidth()/6, desert.getHeight()/3);
		mTileMap.loadFromString("0,1,1,1,2-6,7,5,7,8-12,13,13,13,14", ",", "-");
		
		mEntities[9].setGraphic(mTileMap);
	}


	public void next() {
		remove(mEntities[mCurrentEntity]);
		mCurrentEntity = (mCurrentEntity + 1) % 10;
		add(mEntities[mCurrentEntity]);
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
			((GraphicList)mEntities[0].getGraphic()).getChildren().get(1).x+=2;
		case 2:
			if (mEmitter.getParticleCount() < 3) {
				mEmitter.emit("test", 25, 25);
			}
			break;
		default:
			
		}
		if (Input.mousePressed) {
			
			next();
			Log.d(TAG, "Next " +mCurrentEntity);
		}
	}
	
	
	
}
