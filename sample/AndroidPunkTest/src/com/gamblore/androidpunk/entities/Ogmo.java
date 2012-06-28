package com.gamblore.androidpunk.entities;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.Tween;
import net.androidpunk.graphics.SpriteMap;
import net.androidpunk.utils.Input;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.view.VelocityTracker;

import com.gamblore.androidpunk.Main;
import com.gamblore.androidpunk.R;

public class Ogmo extends Entity {

	private static final String TAG = "Ogmo";
	
	private Tween tween;
	
	private PointF mVelocity = new PointF();
	
	private SpriteMap mMap;
	
	private boolean mCanJump = false;
	
	//private static final String ANIM_STANDING = "standing";
	private static final String ANIM_WALKING = "walking";
	
	public Ogmo(int x, int y) {
		super(x, y);
		
		Bitmap ogmo = FP.getBitmap(R.drawable.ogmo);
		mMap = new SpriteMap(ogmo, (int) ogmo.getWidth()/6, (int) ogmo.getHeight());
		//mMap.add(ANIM_STANDING, new int[] {0}, 0);
		mMap.add(ANIM_WALKING, FP.frames(0, 5), 20);
		setStanding();
		//mMap.play(ANIM_STANDING);
		setGraphic(mMap);
		//setGraphic(new Image(FP.getBitmap(R.drawable.ogmo), new Rect(45,0,90,45)));
		
		setHitbox((int) ogmo.getWidth()/6, (int) ogmo.getHeight());
	}

	@Override
	public void update() {
		float deltax = 0, deltay = 0;
		mVelocity.y = mVelocity.y > 200 ? 200 : mVelocity.y + 1000 * FP.elapsed;
		if (Input.mouseDown) {
			
			Point points[] = Input.getTouches();
			if (Input.getTouchesCount() > 1 && mCanJump) {
				mVelocity.y = -500;
				mCanJump = false;
			}
			Point p = points[0];
			if (p.x + FP.camera.x > x ) {
				mVelocity.x = 200;
			} else if (p.x + FP.camera.x < x) {
				mVelocity.x = -200;
			}
		}
		
		mCanJump = false;
		
		deltax = (int)(mVelocity.x * FP.elapsed);
		deltay = (int)(mVelocity.y * FP.elapsed);
		//Log.d(TAG, String.format("delta %.2f %.2f", deltax, deltay));
		
		if (collide("level", (int) (x + deltax), y) != null) {
			mCanJump = true;
			mVelocity.x = 0;
			if (!Main.mBonk.getPlaying())
				Main.mBonk.loop(1);
		} else { 
			Main.mBonk.stopLooping();
			x += deltax;
		}
		// TODO make this better.
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
			setStanding();
		} else {
			mMap.play(ANIM_WALKING);
			if (mVelocity.x > 0) {
				mMap.setFlipped(false);
			} else if (mVelocity.x < 0) {
				mMap.setFlipped(true);
			}
		}
		//Log.d(TAG, String.format("Velocity %.2f %.2f", mVelocity.x, mVelocity.y));
	}
	
	private void setStanding() {
		if (mMap.getFlipped()) {
			mMap.setFrame(5);
		} else {
			mMap.setFrame(0);
		}
	}
	
}
