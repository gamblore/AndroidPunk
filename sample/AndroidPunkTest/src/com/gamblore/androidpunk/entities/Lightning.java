package com.gamblore.androidpunk.entities;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.Graphic;
import net.androidpunk.graphics.SpriteMap;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;

import com.gamblore.androidpunk.R;

public class Lightning extends Entity {
	
	private static final String TAG = "Lightning";
	
	private SpriteMap mMap;
	private static final String ANIM_SHOCK = "shock";
	
	private Point mRenderPoints[];
	
	public Lightning(int x, int y, int width, int height, boolean flipped) {
		super(x, y);
		Bitmap lightning = FP.getBitmap(R.drawable.lightning);
		
		int gridWidth = lightning.getWidth()/5;
		mMap = new SpriteMap(lightning, gridWidth, (int) lightning.getHeight());
		//mMap.add(ANIM_STANDING, new int[] {0}, 0);
		mMap.add(ANIM_SHOCK, FP.frames(0, 4), 15);
		mMap.play(ANIM_SHOCK);
		setGraphic(mMap);
		
		if (width != gridWidth && height != lightning.getHeight()) {
			Log.e(TAG, "Cannot resize in both directions");
			return;
		}
		
		setHitbox(width, height);
		
		// Find out how many graphics to draw.
		int graphics = 0;
		boolean horiz;
		if ( width > gridWidth ) {
			horiz = true;
			graphics = width / gridWidth;
			if (flipped) 
				mMap.angle = 180;
		} else if (height > gridWidth) {
			horiz = false;
			graphics = height / gridWidth;
			if (flipped) 
				mMap.angle = 270;
			else 
				mMap.angle = 90;
		} else {
			return;
		}
		
		mRenderPoints = new Point[graphics];
		if (horiz && !flipped) {
			for (int i = 0; i < mRenderPoints.length; i++) {
				mRenderPoints[i] = new Point(x + (i * gridWidth), y);
			}
		} else if (!horiz && flipped) {
			for (int i = 0; i < mRenderPoints.length; i++) {
				mRenderPoints[i] = new Point(x , y + (i * gridWidth));
			}
		} else if (horiz && flipped) {
			for (int i = 0; i < mRenderPoints.length; i++) {
				mRenderPoints[i] = new Point(x + (i * gridWidth), y);
			}
		}else if (!horiz && !flipped) {
			for (int i = 0; i < mRenderPoints.length; i++) {
				mRenderPoints[i] = new Point(x , y + (i * gridWidth));
			}
		}
		
		setType("danger");
	}

	@Override
	public void render() {
		for(Point p : mRenderPoints) {
			mMap.render(renderTarget != null ? renderTarget : FP.buffer, p, FP.camera);
		}
	}
}
