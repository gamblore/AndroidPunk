package net.androidpunk.android;

import java.lang.reflect.InvocationTargetException;

import net.androidpunk.Engine;
import net.androidpunk.FP;
import net.androidpunk.R;
import net.androidpunk.Screen;
import net.androidpunk.flashcompat.Event;
import net.androidpunk.flashcompat.Timer;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

public class PunkActivity extends Activity implements Callback, OnTouchListener {
	
	private static final String TAG = "PunkActivity";
	
	public static int static_width = 800;
	public static int static_height = 480;
	public static Class<? extends Engine> engine_class = Engine.class;
	
	private static final String WAKE_TAG = "PunkAcitivty";
	
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
		
	private Engine mEngine;
	private Thread mGameThread;
	
	private static final Rect mScreenRect = new Rect();
	
	private boolean mStarted = false;
	private boolean mRunning = false;
	
	protected class EngineRunner extends Thread {
		@Override
		public void run() {
			while(mRunning) {
				step();
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FP.resources = getResources();
		FP.context = this;
		setContentView(R.layout.main);
		
		mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
		mSurfaceView.setOnTouchListener(this);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
	private void step() {
		if (mStarted) {
			Engine.fire(Event.ENTER_FRAME);
			Engine.checkEvents();
			if (mSurfaceHolder.isCreating())
				return;
			Canvas c = mSurfaceHolder.lockCanvas();
			if (c != null) {
				if(FP.buffer != null) {
					c.clipRect(mScreenRect);
					c.drawBitmap(FP.buffer, FP.bounds, mScreenRect, null);
					/*
					Log.d(TAG, String.format("Drawing %dx%d bounded by %s %s and clipped %s",
							FP.buffer.getWidth(), FP.buffer.getHeight(),
							FP.bounds.toShortString(), mScreenRect.toShortString(),
							c.getClipBounds().toShortString()));
					*/

				}
				mSurfaceHolder.unlockCanvasAndPost(c);
			}
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mRunning = false;
		for (Timer t : Engine.TIMERS) {
			t.stop();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mRunning = true;
		mGameThread = new EngineRunner();
		mGameThread.start();
		for (Timer t : Engine.TIMERS) {
			t.start();
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.d(TAG, String.format("Resize to %dx%d", width, height));
		mScreenRect.set(0, 0, width, height);
		Engine.fire(Event.ADDED_TO_STAGE);
		mStarted = true;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			mEngine = engine_class.getConstructor(Integer.TYPE, Integer.TYPE, Float.TYPE, Boolean.TYPE).newInstance(static_width, static_height, 60, FP.fixed);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		mRunning = false;
		mStarted = false;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public boolean onTouch(View v, MotionEvent event) {
		if (mStarted)
			FP.screen.setMotionEvent(event);
		return true;
	}

	
}
