package com.gamblore.androidpunk;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.FP.TweenOptions;
import net.androidpunk.World;
import net.androidpunk.flashcompat.OnCompleteCallback;
import net.androidpunk.graphics.Backdrop;
import net.androidpunk.graphics.GraphicList;
import net.androidpunk.graphics.Image;
import net.androidpunk.graphics.SpriteMap;
import net.androidpunk.graphics.Text;
import net.androidpunk.masks.Hitbox;
import net.androidpunk.masks.MaskList;
import net.androidpunk.tweens.misc.AngleTween;
import net.androidpunk.tweens.misc.ColorTween;
import net.androidpunk.tweens.misc.VarTween;
import net.androidpunk.tweens.motion.QuadMotion;
import net.androidpunk.utils.Data;
import net.androidpunk.utils.Ease;
import net.androidpunk.utils.Input;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

public class MenuWorld extends World {

	private static final String TAG = "MenuWorld";
	
	private static final String ANIM_WALKING = "walking";
	
	private Entity mDisplay = new Entity();
	private Entity mSecondDisplay = new Entity();
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
		
		// Secondary options
		mSecondDisplay.x = FP.screen.getWidth();
		
		Image menuNewGame = new Image(FP.getBitmap(R.drawable.menu_newgame));
		menuNewGame.relative = true;
		menuNewGame.x = FP.screen.getWidth()/4 - menuNewGame.getWidth()/2;
		menuNewGame.y = 2*FP.screen.getHeight()/3;
		Hitbox newHitbox = new Hitbox(menuNewGame.getWidth(), menuNewGame.getHeight(), (int)menuNewGame.x, (int)menuNewGame.y);
		
		Image menuContinue = new Image(FP.getBitmap(R.drawable.menu_continue));
		menuContinue.relative = true;
		menuContinue.x = 3*FP.screen.getWidth()/4 - menuContinue.getWidth()/2;
		menuContinue.y = menuNewGame.y;
		Hitbox contHitbox = new Hitbox(menuContinue.getWidth(), menuContinue.getHeight(), (int)menuContinue.x, (int)menuContinue.y);
		
		mSecondDisplay.setMask(new MaskList(newHitbox, contHitbox));
		mSecondDisplay.setGraphic(new GraphicList(menuNewGame, menuContinue));
		add(mSecondDisplay);
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
			if (Data.getData().contains(Main.DATA_CURRENT_LEVEL)) {
				FP.tween(mSecondDisplay, FP.tweenmap("x", 0), 1.5f, new TweenOptions(ONESHOT, null, Ease.quadIn, this));
				mTextTween.active = false;
				startText.visible = false;
				
				
			} else {
				//FP.setWorld(new OgmoEditorWorld(8));
				FP.setWorld(new OgmoEditorWorld(Data.getData().getInt(Main.DATA_CURRENT_LEVEL, 1)));
			}
		}
		
		// In secondary menu
		if (mSecondDisplay.x < 5 && Input.mousePressed) {
			Point p = FP.screen.getTouches()[0];
			Log.d(TAG, String.format("Touch at %d %d", p.x, p.y));
			if (mSecondDisplay.collidePoint(mSecondDisplay.x, mSecondDisplay.y, p.x, p.y)) {
				if (p.x < FP.screen.getWidth()/2) {
					// new game
					Data.getData().edit().remove(Main.DATA_CURRENT_LEVEL).commit();
					FP.setWorld(new OgmoEditorWorld(1));
				} else {
					//continue
					FP.setWorld(new OgmoEditorWorld(Data.getData().getInt(Main.DATA_CURRENT_LEVEL, 1)));
				}
			}
			
		}
	}
	
}
