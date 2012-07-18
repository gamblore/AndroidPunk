package com.gamblore.androidpunk.entities;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.graphics.atlas.SpriteMap;
import net.androidpunk.graphics.atlas.TileMap;
import net.androidpunk.graphics.opengl.SubTexture;
import net.androidpunk.utils.Input;
import android.graphics.Point;
import android.graphics.PointF;

import com.gamblore.androidpunk.Main;

public class Ogmo extends Entity {

	private static final String TAG = "Ogmo";
	
	private static final int X_SPEED = 200;
	private static final int MAX_FALL_SPEED = 200;
	private static final int JUMP_SPEED = -500;
	
	//private static final String ANIM_STANDING = "standing";
	private static final String ANIM_WALKING = "walking";
	
	private PointF mVelocity = new PointF();
	
	private SpriteMap mMap;
	
	private boolean mCanJump = false;
	
	
	
	public Ogmo(int x, int y) {
		super(x, y);
		
		SubTexture ogmo = Main.mAtlas.getSubTexture("ogmo");
		mMap = new SpriteMap(ogmo, (int) ogmo.getWidth()/6, (int) ogmo.getHeight());
		//mMap.add(ANIM_STANDING, new int[] {0}, 0);
		mMap.add(ANIM_WALKING, FP.frames(0, 5), 20);
		mMap.setFrame(0);
		
		//mMap.play(ANIM_STANDING);
		setGraphic(mMap);
		//setGraphic(new Image(FP.getBitmap(R.drawable.ogmo), new Rect(45,0,90,45)));
		
		setHitbox((int) ogmo.getWidth()/6, (int) ogmo.getHeight());
	}

	@Override
	public void update() {
		float deltax = 0, deltay = 0;
		
		mVelocity.y = mVelocity.y > MAX_FALL_SPEED ? MAX_FALL_SPEED : mVelocity.y + (1000 * FP.elapsed);
		if (Input.mouseDown) {
			
			Point points[] = Input.getTouches();
			if (Input.getTouchesCount() > 1 && mCanJump) {
				Main.mJump.play();
				mVelocity.y = JUMP_SPEED;
				mCanJump = false;
			}
			Point p = points[0];
			if (p.x + FP.camera.x >= getRight() ) {
					mVelocity.x = X_SPEED;
			} else if (p.x + FP.camera.x <= getLeft()) {
					mVelocity.x = -X_SPEED;
			}
			
			//mVelocity.x = Math.max(Math.min(mVelocity.x, 200), -200);
			
		}
		
		mCanJump = false;
		
		deltax = (int)(mVelocity.x * FP.elapsed);
		deltay = (int)(mVelocity.y * FP.elapsed);
		//Log.d(TAG, String.format("delta %.2f %.2f", deltax, deltay));
		Entity e;
		float previousXVelocity = mVelocity.x;
		if ((e = collide("level", (int) (x + deltax), y)) != null) {
			//mCanJump = true;
			
			if (mVelocity.y > 0) {
				mVelocity.y *= 0.90;
			}
			int width = ((TileMap)e.getGraphic()).getTileWidth();
			mVelocity.x = 0;
			if (previousXVelocity < 0) {
				x = ((x + (int)deltax) / width) * width + width;
			} else {
				x = ((x + (int)deltax) / width) * width;
			}
			if (!Main.mBonk.getPlaying())
				Main.mBonk.loop(1);
		} else { 
			Main.mBonk.stopLooping();
			x += deltax;
		}
		if (collide("level", x, (int) (y + deltay)) != null) {
			if (mVelocity.y >= 0) {
				mCanJump = true;
			} else {
				Main.mBonk.play();
			}
			mVelocity.y = 0;
		} else {
			y += deltay;
		}
		
		super.update();
		
		// Decay x
		mVelocity.x *= 0.75;
		
		if (Math.abs(deltax) < 1) {
			mVelocity.x = 0;
			mMap.setFrame(0);
		} else {
			mMap.play(ANIM_WALKING);
			if (mVelocity.x > 0) {
				mMap.scaleX = Math.abs(mMap.scaleX);
			} else if (mVelocity.x < 0) {
				mMap.scaleX = -Math.abs(mMap.scaleX);
			}
		}
		//Log.d(TAG, String.format("Velocity %.2f %.2f", mVelocity.x, mVelocity.y));
	}
	
}
