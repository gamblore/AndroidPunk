package com.gamblore.androidpunk;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.World;
import net.androidpunk.flashcompat.OnCompleteCallback;
import net.androidpunk.graphics.Backdrop;
import net.androidpunk.graphics.GraphicList;
import net.androidpunk.graphics.Image;
import net.androidpunk.graphics.SpriteMap;
import net.androidpunk.graphics.Text;
import net.androidpunk.tweens.misc.AngleTween;
import net.androidpunk.tweens.misc.ColorTween;
import net.androidpunk.tweens.misc.VarTween;
import net.androidpunk.tweens.motion.QuadMotion;
import net.androidpunk.utils.Data;
import net.androidpunk.utils.Input;
import android.graphics.Bitmap;
import android.graphics.Color;

public class MenuWorld extends World {

	private static final String ANIM_WALKING = "walking";
	
	private Entity mDisplay = new Entity();
	private Image logo;
	private SpriteMap ogmo;
	private Text startText;
	
	private OnCompleteCallback mAngleFlipperCallback = new OnCompleteCallback() {
		
		@Override
		public void completed() {
			if (mAngleTween.angle > 90) {
				mAngleTween.tween(mAngleTween.angle, 60, .5f);
			} else {
				mAngleTween.tween(mAngleTween.angle, 120, .5f);
			}
		}
	};
	
	private QuadMotion mQuadMotion = new QuadMotion();
	private AngleTween mAngleTween = new AngleTween(mAngleFlipperCallback, ONESHOT);
	private ColorTween mTextTween = new ColorTween(null, LOOPING);
	
	public MenuWorld() {
		
		Backdrop bd = new Backdrop(FP.getBitmap(R.drawable.jumper_background));
		Backdrop bd2 = new Backdrop(FP.getBitmap(R.drawable.jumper_clouds));
		
		logo = new Image(FP.getBitmap(R.drawable.jumper_mobile));
		logo.x = FP.screen.getWidth()/2 - logo.getWidth()/2;
		logo.y = FP.screen.getHeight()/4;
		
		Bitmap ogmoBm = FP.getBitmap(R.drawable.ogmo);
		ogmo = new SpriteMap(ogmoBm, (int) ogmoBm.getWidth()/6, (int) ogmoBm.getHeight());
		ogmo.add(ANIM_WALKING, FP.frames(0, 5), 20);
		
		Text.size = 36;
		startText = new Text("Tap to Start", 0,0);
		startText.x = FP.screen.getWidth()/2 - startText.getWidth()/2;
		startText.y = 3*FP.screen.getHeight()/4;
		
		GraphicList gl = new GraphicList(bd, bd2, logo, ogmo);
		mDisplay.setLayer(99);
		mDisplay.setGraphic(gl);
		
		mQuadMotion.setMotion(-(int) ogmoBm.getWidth()/6, 3*FP.screen.getHeight()/4,
				FP.screen.getWidth(), FP.screen.getHeight(),
				FP.screen.getWidth()/2 + logo.getWidth()/2, logo.y, 
				2.0f);
		
		VarTween vt = new VarTween();
		vt.tween(ogmo, "scale", 2.0f, 2.0f);
		vt.complete = new OnCompleteCallback() {
			
			@Override
			public void completed() {
				((GraphicList)mDisplay.getGraphic()).add(startText);
				mTextTween.tween(0.50f, 0xffffffff, 0x66ffffff);
				mTextTween.complete = new OnCompleteCallback() {
					
					@Override
					public void completed() {
						if (Color.alpha(mTextTween.color) < 255) {
							mTextTween.tween(0.50f, mTextTween.color, 0xffffffff);
						} else {
							mTextTween.tween(0.505f, mTextTween.color, 0x66ffffff);
						}
					}
				};
				addTween(mTextTween);
			}
		};
		
		mAngleTween.tween(0, 60, 2f);
		addTween(mQuadMotion);
		addTween(vt);
		addTween(mAngleTween);
		
		add(mDisplay);
		
		//add(new Button(FP.screen.getWidth()/4, FP.screen.getHeight()/2, FP.screen.getWidth()/2, 40, "START"));
	}

	@Override
	public void update() {
		super.update();
		
		ogmo.x = mQuadMotion.x;
		ogmo.y = mQuadMotion.y;
		
		ogmo.angle = mAngleTween.angle;
		
		startText.setColor(mTextTween.color);
		
		if (mTextTween.active && Input.mousePressed) {
			//FP.setWorld(new OgmoEditorWorld(8));
			FP.setWorld(new OgmoEditorWorld(Data.getData().getInt(Main.DATA_CURRENT_LEVEL, 1)));
		}
	}
	
}
