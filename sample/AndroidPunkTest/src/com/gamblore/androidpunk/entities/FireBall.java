package com.gamblore.androidpunk.entities;

import com.gamblore.androidpunk.Main;
import com.gamblore.androidpunk.OgmoEditorWorld;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.graphics.atlas.Emitter;
import net.androidpunk.graphics.atlas.GraphicList;
import net.androidpunk.graphics.atlas.SpriteMap;
import net.androidpunk.graphics.opengl.SubTexture;
import android.graphics.Point;

public class FireBall extends Entity {

	private static final String TAG = "FireBall";
	
	//Pixels per second.
	private final int FIREBALL_SPEED = 100;
	private static final String ANIM_LOOP = "loop";
	
	private float mMaxLifetime;
	private float mLifetime;
	
	private int mAngle;
	private SpriteMap mMap;
	private Emitter mEmiterMap;
	private Point mVelocity = new Point();
	OgmoEditorWorld mWorld;
	
	public FireBall(Volcano v) {
		this(v, 0.0f);
	}

	public FireBall(Volcano v, float lifetime) {
		super();
		
		mLifetime = 0.0f;
		mMaxLifetime = lifetime;
		
		SubTexture fireball = Main.mAtlas.getSubTexture("fireball");
		SubTexture fireball_spark = Main.mAtlas.getSubTexture("fireball_spark");
		mMap = new SpriteMap(fireball, fireball.getWidth()/3, fireball.getHeight());
		mMap.add(ANIM_LOOP, FP.frames(0, 2), 5);
		mMap.play(ANIM_LOOP);
		
		mEmiterMap = new Emitter(fireball_spark, fireball_spark.getWidth()/3, fireball_spark.getHeight());
		mEmiterMap.x = fireball.getWidth()/4;
		mEmiterMap.y = fireball.getHeight()/4;
		mEmiterMap.newType("spark", FP.frames(0, 2));
		mEmiterMap.setAlpha("spark", 0xff, 0x33);
		
		setGraphic(new GraphicList(mMap, mEmiterMap));
		mAngle = v.getAngle();
		switch(mAngle) {
		case 0:
			x = (int)(v.x + v.width/4);
			y = (int)(v.y + v.height - mMap.getFrameHeight());
			mVelocity.set(0, -FIREBALL_SPEED);
			//mEmiterMap.setMotion("spark", 270-25, 50, 1.0f, 50, 30, .25f);
			break;
		case 90:
			x = v.x;
			y = (int)(v.y + v.height/4);
			mVelocity.set(FIREBALL_SPEED, 0);
			//mEmiterMap.setMotion("spark", 180-25, 50, 1.0f, 50, 30, .25f);
			break;
		case 180:
			x = (int)(v.x + v.width/4);
			y = v.y;
			mVelocity.set(0, FIREBALL_SPEED);
			//mEmiterMap.setMotion("spark", 90-25, 50, 1.0f, 50, 30, .25f);
			break;
		case 270:
			x = (int)(v.x + v.width - mMap.getFrameWidth());
			y = (int)(v.y + v.height/4);
			mVelocity.set(-FIREBALL_SPEED, 0);
			//mEmiterMap.setMotion("spark", 0-25, 50, 1.0f, 50, 30, .25f);
			break;
		}
		
		mEmiterMap.setMotion("spark", 270-25-mAngle, 50, 1.0f, 50, 30, .25f);
		mMap.angle = mAngle;
		setHitbox(mMap.getWidth()/3, mMap.getHeight());
		
		setType(OgmoEditorWorld.TYPE_DANGER);
		
	}

	
	@Override
	public void render() {
		FP.rect.set(FP.camera.x, FP.camera.y, FP.camera.x+FP.width, FP.camera.y+FP.height);
		if (FP.rect.contains(x, y))
			super.render();
	}

	@Override
	public void update() {
		super.update();
		FP.rect.set(FP.camera.x, FP.camera.y, FP.camera.x+FP.width, FP.camera.y+FP.height);
		
		if (mWorld == null) {
			mWorld = (OgmoEditorWorld)getWorld();
		}
		if (FP.rect.contains(x, y) && 
				mEmiterMap.getParticleCount() < 5 && FP.random() < .10) {
			mEmiterMap.emit("spark", 0, 0);
		}
		if (mMaxLifetime != 0.0f) {
			mLifetime += FP.elapsed;
			if (mLifetime > mMaxLifetime) {
				mWorld.remove(this);
				collidable = false;
				return;
			}
		}
		
		x += mVelocity.x * FP.elapsed + 0.5f;
		y += mVelocity.y * FP.elapsed + 0.5f;
		
		if (collide("level", x, y) != null) {
			mWorld.remove(this);
			collidable = false;
		} else if (x < 0 || y < 0 || x > mWorld.getWidth() || y > mWorld.getHeight()) {
			mWorld.remove(this);
			collidable = false;
		}
	}
	
	
}
