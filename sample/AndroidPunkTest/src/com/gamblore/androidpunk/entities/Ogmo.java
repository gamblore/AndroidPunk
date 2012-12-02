package com.gamblore.androidpunk.entities;

import java.util.Vector;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.graphics.atlas.SpriteMap;
import net.androidpunk.graphics.atlas.TileMap;
import net.androidpunk.graphics.opengl.SubTexture;
import net.androidpunk.utils.Input;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.KeyEvent;

import com.gamblore.androidpunk.Main;
import com.gamblore.androidpunk.OgmoEditorWorld;

public class Ogmo extends Entity {

	private static final String TAG = "Ogmo";
	
	public static final String TYPE_PLAYER = "player";
	
	private static final int X_SPEED = 200;
	private static final int MAX_FALL_SPEED = 200;
	private static final int JUMP_SPEED = -500;
	
	private boolean mMoveLeft = false;
	private boolean mMoveRight = false;
	private boolean mMoveJump = false;
	
	//private static final String ANIM_STANDING = "standing";
	public static final String ANIM_WALKING = "walking";
	
	private PointF mVelocity = new PointF();
	
	private SpriteMap mMap;
	
	private boolean mDead = false;
	private boolean mCanJump = false;
	
	private static final Vector<String> CollidableTypes = new Vector<String>();
	
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
		
		setType(TYPE_PLAYER);
		
		CollidableTypes.clear();
		CollidableTypes.add("level");
	}
	
	private void updateMoveBooleans() {
		mMoveJump = mMoveLeft = mMoveRight = false;
		if (Input.mouseDown) {
			Point points[] = Input.getTouches();
			
			if (points[0].y < FP.height/4) {
				mMoveJump = true;
				return;
			}
			
			if (Input.getTouchesCount() > 1 || Input.checkKey(KeyEvent.KEYCODE_SPACE)) {
				mMoveJump = true;	
			}
			
			if (points[0].x > FP.screen.getWidth()/2) {
				mMoveRight = true;
			} else {
				mMoveLeft = true;
			}
		}
	}
	
	@Override
	public void update() {
		updateMoveBooleans();
		float deltax = 0, deltay = 0;
		
		mVelocity.y = mVelocity.y > MAX_FALL_SPEED ? MAX_FALL_SPEED : mVelocity.y + (1000 * FP.elapsed);
		if (mMoveJump && mCanJump) {
			Main.mJump.play();
			y -= 1; // break locks to moving platforms.
			mVelocity.y = JUMP_SPEED;
			mCanJump = false;
		}
		if (mMoveLeft) {
			mVelocity.x = -X_SPEED;
		}
		if (mMoveRight) {
			mVelocity.x = X_SPEED;
		}
		
		mCanJump = false;
		
		deltax = (int)(mVelocity.x * FP.elapsed);
		deltay = (int)(mVelocity.y * FP.elapsed);
		
		float previousXVelocity = mVelocity.x;
		
		// Check for moving platforms
		Entity e;
		Entity platformVertical = collide(Platform.TYPE, x, (int) (y + deltay));
		
		if ((e = collide(Platform.TYPE, (int) (x + deltax), y)) != null && platformVertical == null) {
			if (previousXVelocity < 0) {
				x = e.x + e.width + 1;
			} else {
				x = e.x - width - 1;
			}
			if (!Main.mBonk.getPlaying())
				Main.mBonk.loop(1);
		} else if ((e = collideTypes(CollidableTypes, (int) (x + deltax), y)) != null) {
			
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
		
		if (deltay >= 0 && (e = collide(Platform.TYPE, x, (int) (y + deltay))) != null) { // On top or falling through
			mCanJump = true;
			mVelocity.y = 0;
			y = e.y - height;
		} else if ((e = collide(Platform.TYPE, x, (int) (y + deltay))) != null) { // Spiked
			setDead();	
		} else if (collideTypes(CollidableTypes, x, (int) (y + deltay)) != null) { // on ground or hit roof
			if (mVelocity.y >= 0) {
				mCanJump = true;
			} else {
				Main.mBonk.play();
			}
			mVelocity.y = 0;
		} else { // falling
			y += deltay;
		}
		
		super.update();
		
		// Decay x
		mVelocity.x *= 0.75;
		
		if (Math.abs(mVelocity.x) < 1) {
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
	
	public void setDead() {
		Main.mDeath.play();
		OgmoEditorWorld.restart = true;
	}
	
}
