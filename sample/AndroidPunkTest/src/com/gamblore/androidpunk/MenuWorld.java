package com.gamblore.androidpunk;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.FP.TweenOptions;
import net.androidpunk.World;
import net.androidpunk.android.PunkActivity;
import net.androidpunk.flashcompat.OnCompleteCallback;
import net.androidpunk.graphics.atlas.AtlasText;
import net.androidpunk.graphics.atlas.Backdrop;
import net.androidpunk.graphics.atlas.GraphicList;
import net.androidpunk.graphics.atlas.Image;
import net.androidpunk.graphics.atlas.SpriteMap;
import net.androidpunk.graphics.opengl.SubTexture;
import net.androidpunk.masks.Hitbox;
import net.androidpunk.masks.MaskList;
import net.androidpunk.tweens.misc.AngleTween;
import net.androidpunk.tweens.misc.ColorTween;
import net.androidpunk.tweens.misc.VarTween;
import net.androidpunk.tweens.motion.QuadMotion;
import net.androidpunk.utils.Data;
import net.androidpunk.utils.Ease;
import net.androidpunk.utils.Input;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

public class MenuWorld extends World {

	private static final String TAG = "MenuWorld";
	
	private static final String ANIM_WALKING = "walking";
	
	private Entity mDisplay = new Entity();
	private Entity mSecondDisplay = new Entity();
	
	private Entity mSoundEntity = new Entity();
	private Entity mCreditsEntity = new Entity();

	
	private Image logo;
	private SpriteMap ogmo;
	private AtlasText startText;
	
	private SpriteMap sound;
	private Image credits;
	
	private AtlasText newGame, continueGame;
	
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
		
		// Ask to close.
		FP.activity.setOnBackCallback(PunkActivity.DEFAULT_ON_BACK);
		
		int level = Data.getData().getInt(Main.DATA_CURRENT_LEVEL, 1);
		Log.d(TAG, "Level: " +level);
		Backdrop bd = Main.getLevelBackdrop(level);
		bd.setColor(0xb0ffffff);
		
		Backdrop bd2 = new Backdrop(Main.mAtlas.getSubTexture("jumper_clouds"), true, false);
		
		logo = new Image(Main.mAtlas.getSubTexture("logo_text"));
		logo.x = FP.screen.getWidth()/2 - logo.getWidth()/2;
		logo.y = FP.screen.getHeight()/4;
		switch(((level-1)/10)+1) {
		case 2:
			logo.setColor(0xff0000ff);
			break;
		case 1:
		default:
			logo.setColor(0xffff0000);
		}
		
		SubTexture ogmoBm = Main.mAtlas.getSubTexture("ogmo");
		ogmo = new SpriteMap(ogmoBm, (int) ogmoBm.getWidth()/6, (int) ogmoBm.getHeight());
		ogmo.add(ANIM_WALKING, FP.frames(0, 5), 20);
		ogmo.setFrame(0);
		
		startText = new AtlasText("Tap to Start", 30, Main.mTypeface);
		startText.x = FP.screen.getWidth()/2 - startText.getWidth()/2;
		startText.y = 3*FP.screen.getHeight()/4;
		
		GraphicList gl = new GraphicList(bd, bd2, logo, ogmo);
		mDisplay.setLayer(99);
		mDisplay.setGraphic(gl);
		
		mQuadMotion.setMotion(-(int) ogmoBm.getWidth()/6, 3*FP.screen.getHeight()/4,
				FP.screen.getWidth(), FP.screen.getHeight(),
				FP.screen.getWidth()/2 + logo.getWidth()/2 + ogmoBm.getWidth()/6, logo.y, 
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
		mSecondDisplay.y = FP.screen.getHeight()/2;
		
		newGame = new AtlasText("New Game", 26, Main.mTypeface);
		newGame.relative = true;
		newGame.x = FP.screen.getWidth()/4 - newGame.getWidth()/2;
		newGame.y = 2*FP.screen.getHeight()/3;
		Hitbox newHitbox = new Hitbox((int)(newGame.getWidth()+FP.dip(10)), (int)(newGame.getHeight()+FP.dip(10)), (int)(newGame.x-FP.dip(5)), (int)(newGame.y-FP.dip(5)));
		
		continueGame = new AtlasText("Continue", 26, Main.mTypeface);
		continueGame.relative = true;
		continueGame.x = 3*FP.screen.getWidth()/4 - continueGame.getWidth()/2;
		continueGame.y = newGame.y;
		Hitbox contHitbox = new Hitbox((int)(continueGame.getWidth()+FP.dip(10)), (int)(continueGame.getHeight()+FP.dip(10)), (int)(continueGame.x-FP.dip(5)), (int)(continueGame.y-FP.dip(5)));
		
		mSecondDisplay.setMask(new MaskList(newHitbox, contHitbox));
		mSecondDisplay.setGraphic(new GraphicList(newGame, continueGame));
		
		add(mSecondDisplay);
		//add(new Button(FP.screen.getWidth()/4, FP.screen.getHeight()/2, FP.screen.getWidth()/2, 40, "START"));
		
		// Setup the mute button
		SubTexture soundTexture = Main.mAtlas.getSubTexture("sound");
		sound = new SpriteMap(soundTexture, soundTexture.getWidth()/2, soundTexture.getHeight());
		sound.scale = 2.0f;
		boolean muted = Main.isMute();
		Log.d(TAG, "Muted "+ muted);
		sound.setFrame(muted ? 1 : 0);
		sound.setColor(0xff000000);
		//Main.setMute(muted);
		mSoundEntity.x = FP.screen.getWidth() - (soundTexture.getWidth()/2 *2);
		mSoundEntity.setGraphic(sound);
		mSoundEntity.setHitbox((int)(soundTexture.getWidth()/2* sound.scale), (int)(soundTexture.getHeight() * sound.scale));
		mSoundEntity.setType("muter");
		add(mSoundEntity);
		
		SubTexture creditsTexture = Main.mAtlas.getSubTexture("credits");
		credits = new Image(creditsTexture);
		credits.scale = 2.0f;
		credits.setColor(0xff000000);
		mCreditsEntity.x = mSoundEntity.x - (mSoundEntity.width + 8);
		mCreditsEntity.setGraphic(credits);
		mCreditsEntity.setHitbox(creditsTexture.getWidth(), creditsTexture.getHeight());
		mCreditsEntity.setType("credits");
		add(mCreditsEntity);
	}

	@Override
	public void update() {
		super.update();
		
		ogmo.x = mQuadMotion.x;
		ogmo.y = mQuadMotion.y;
		
		ogmo.angle = mAngleTween.angle;
		
		startText.setColor(mTextTween.color);
		if (Input.mousePressed) {
			Point touch = FP.screen.getTouches()[0];
			boolean hitTarget = false;
			Entity e = collidePoint("muter", touch.x, touch.y);
			if (e != null) {
				boolean muted = Main.isMute();
				Main.setMute(!muted);
				sound.setFrame(!muted ? 1 : 0);
				hitTarget = true;
				
			}
			e = collidePoint("credits", touch.x, touch.y);
			if (e != null) {
				FP.setWorld(new StoryWorld(R.string.credits, new MenuWorld()));
				hitTarget = true;
			}
			
			if (!hitTarget && mTextTween.active) {
				if (Data.getData().contains(Main.DATA_CURRENT_LEVEL)) {
					FP.tween(mSecondDisplay, FP.tweenmap("y", 0), 1.0f, new TweenOptions(ONESHOT, null, Ease.quadIn, this));
					mTextTween.active = false;
					startText.visible = false;
					
					
				} else {
					//FP.setWorld(new OgmoEditorWorld(8));
					FP.setWorld(new OgmoEditorWorld(Data.getData().getInt(Main.DATA_CURRENT_LEVEL, 1)));
				}
			}
			
			// In secondary menu
			else if (mSecondDisplay.y < 50) {
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
						//FP.setWorld(new OgmoEditorWorld(23));
					}
					//Main.mBGM.loop(0.2f);
				}
			}
		}
	}
}
