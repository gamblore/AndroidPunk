package com.gamblore.androidpunk;

import java.util.Vector;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.World;
import net.androidpunk.android.PunkActivity.OnBackCallback;
import net.androidpunk.flashcompat.OnCompleteCallback;
import net.androidpunk.graphics.atlas.AtlasGraphic;
import net.androidpunk.graphics.atlas.AtlasText;
import net.androidpunk.graphics.atlas.GraphicList;
import net.androidpunk.graphics.atlas.SpriteMap;
import net.androidpunk.graphics.opengl.SubTexture;
import net.androidpunk.tweens.misc.AngleTween;
import net.androidpunk.tweens.misc.ColorTween;
import net.androidpunk.tweens.motion.LinearPath;
import net.androidpunk.utils.Input;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;

import com.gamblore.androidpunk.entities.Exit;
import com.gamblore.androidpunk.entities.Ogmo;

public class StoryWorld extends World {

	private static final String TAG = "StoryWorld";
	
	private final float TIME_PER_LINE = 1.0f; 
	private int mStoryStringResourceId;
	private String mStoryString;
	private World mNextWorld;
	
	private String mSections[];
	private Vector<AtlasGraphic> mGraphics = new Vector<AtlasGraphic>();
	
	private int mCurrentLine = -1;
	
	private float mTimeUntilUpdate = 0;
	
	private ColorTween mColorTween = new ColorTween(null, PERSIST);
	private LinearPath mLP;
	private AngleTween mAT;
	
	private Entity mOgmo;
	private Exit mExit;
	
	private final OnBackCallback mGameOnBack = new OnBackCallback() {
		
		@Override
		public boolean onBack() {
			if (FP.engine != null)
				FP.engine.paused = true;
			AlertDialog.Builder builder = new AlertDialog.Builder(FP.context);
			
			builder.setTitle(R.string.return_to_menu_title);
			builder.setMessage(R.string.return_to_menu_message);
			
			OnClickListener ocl = new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					if (which == DialogInterface.BUTTON_POSITIVE) {
						FP.setWorld(new MenuWorld());
					}
					if (FP.engine != null)
						FP.engine.paused = false;
				}
			};
			builder.setPositiveButton(R.string.yes, ocl);
			builder.setNegativeButton(R.string.no, ocl);
			builder.create().show();
			return true;
		}
	};
	
	public StoryWorld(int stringResourceId, World nextWorld) {
		super();
		mNextWorld = nextWorld;
		FP.activity.setOnBackCallback(mGameOnBack);
		
		mStoryStringResourceId = stringResourceId;
		mStoryString = FP.context.getString(stringResourceId);
		
		mSections = mStoryString.split("\\n");
		
		Entity e = new Entity();
		GraphicList gl = new GraphicList();
		
		for (int i = 0; i < mSections.length; i++) {
			if ("".equals(mSections[i])) {
				continue;
			}
			AtlasText line = new AtlasText(mSections[i], 20, Main.mTypeface);
			line.visible = false;
			line.x = 2;
			line.y = i * 22;
			line.setColor(0);
			
			mGraphics.add(line);
			gl.add(line);
		}
		
		e.setGraphic(gl);
		
		add(e);
		addTween(mColorTween);
		
		mOgmo = new Entity(0, 480-32);
		SubTexture ogmo = Main.mAtlas.getSubTexture("ogmo");
		SpriteMap map = new SpriteMap(ogmo, (int) ogmo.getWidth()/6, (int) ogmo.getHeight());
		map.add(Ogmo.ANIM_WALKING, FP.frames(0, 5), 20);
		map.setFrame(0);
		map.play(Ogmo.ANIM_WALKING);
		mOgmo.setGraphic(map);
		mOgmo.setLayer(-1);
		
		mExit = new Exit(FP.width-64, FP.height-64);
		
		add(mOgmo);
		add(mExit);
		
		mLP = new LinearPath(new OnCompleteCallback() {
			@Override
			public void completed() {
				mAT = new AngleTween(null, LOOPING);
				addTween(mAT);
				mAT.tween(0, 180f, 1);
			}
		}, ONESHOT);
		addTween(mLP);
		
		mLP.addPoint(mOgmo.x, mOgmo.y);
		mLP.addPoint(mExit.x, mOgmo.y);
		mLP.setMotion(4f);
		
		((SpriteMap)mOgmo.getGraphic()).play(Ogmo.ANIM_WALKING);
	}

	@Override
	public void update() {
		super.update();
		mTimeUntilUpdate -= FP.elapsed;
		
		if (mLP != null && mLP.active) {
			mOgmo.x = (int) mLP.x;
			mOgmo.y = (int) mLP.y;
		}
		if (mAT != null && mAT.active) {
			((SpriteMap)mOgmo.getGraphic()).angle += FP.elapsed * 360/1.0f;
			FP.stepTowards(mOgmo, mExit.x+16, mExit.y+16, 1);
			((SpriteMap)mOgmo.getGraphic()).scale -= FP.elapsed * .5/1.0f;
		}
		if (Input.mousePressed) {
			if (mCurrentLine != mGraphics.size()-1) {
				mTimeUntilUpdate = -1;
			}
		}
		
		if (mTimeUntilUpdate < 0) {
			mCurrentLine++;
			if (mCurrentLine < mGraphics.size()) {
				Log.d(TAG, "Displaying " + mCurrentLine + " " + mSections[mCurrentLine]);
			}
			if (mCurrentLine == mGraphics.size()) {
				Log.d(TAG, "Moving to next world.");
				FP.setWorld(mNextWorld);
				return;
			} else if (mCurrentLine == mGraphics.size()-1) {
				mTimeUntilUpdate = 3.0f;
			} else {
				mTimeUntilUpdate = TIME_PER_LINE;
			}
			AtlasGraphic line = mGraphics.get(mCurrentLine);
			line.visible = true;
			mColorTween.tween(TIME_PER_LINE, 0x33ffffff, 0xffffffff);
		}
		
		if (mCurrentLine > 0) {
			mGraphics.get(mCurrentLine-1).setColor(0xffffffff);
		}
		if (mCurrentLine < mGraphics.size()) {
			mGraphics.get(mCurrentLine).setColor(mColorTween.color);
		}
	}
	
}
