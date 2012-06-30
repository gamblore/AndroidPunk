package net.androidpunk.android;

import java.lang.reflect.InvocationTargetException;

import net.androidpunk.Engine;
import net.androidpunk.FP;
import net.androidpunk.R;
import net.androidpunk.Sfx;
import net.androidpunk.flashcompat.Event;
import net.androidpunk.flashcompat.Timer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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
	
	private AudioManager mAudioManager;
	private int mOldVolume = 0;
	private int mVolume = 100; 
	
	public static abstract class OnBackCallback {
		public abstract boolean onBack();
	}
	
	public final OnBackCallback DEFAULT_ON_BACK = new OnBackCallback() {
		
		@Override
		public boolean onBack() {
			AlertDialog.Builder builder = new AlertDialog.Builder(FP.context);
			
			builder.setTitle(R.string.exit_dialog_title);
			builder.setMessage(R.string.exit_dialog_message);
			
			OnClickListener ocl = new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					if (which == DialogInterface.BUTTON_POSITIVE) {
						finish();
					}
				}
			};
			builder.setPositiveButton(R.string.yes, ocl);
			builder.setNegativeButton(R.string.no, ocl);
			builder.create().show();
			return true;
		}
	}; 
	
	private OnBackCallback mOnBackCallback = DEFAULT_ON_BACK;
	
	private OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {
		private boolean mDucking = false;
		
		public void onAudioFocusChange(int focusChange) {
			if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
				// Other app requested focus for a bit.
			} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
				// Other app requested focus but said it is cool if we keep it quiet.
				mDucking = true;
				mOldVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
				mVolume = (int)(mOldVolume * 0.125);
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolume, 0);
	        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
	        	// We got audio focus now.
	        	if(mDucking) {
	        		mDucking = false;
	        		mVolume = mOldVolume;
	        	}
	        	 
	        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
	        	// We are out.
	        	mAudioManager.abandonAudioFocus(afChangeListener);
	        }
	    }
	};
	
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
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
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
		mAudioManager.abandonAudioFocus(afChangeListener);

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
		// Request audio focus for playback
		int result = mAudioManager.requestAudioFocus(afChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);
		if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			Log.e(TAG, "Failed to request focus. No sound.");
		}
	}
	
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Sfx.SOUND_POOL.release();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.d(TAG, String.format("Resize to %dx%d", width, height));
		mScreenRect.set(0, 0, width, height);
		Engine.fire(Event.ADDED_TO_STAGE);
		mStarted = true;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			mEngine = engine_class.getConstructor(Integer.TYPE, Integer.TYPE, Float.TYPE, Boolean.TYPE).newInstance(static_width, static_height, FP.assignedFrameRate, FP.fixed);
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
	
	/**
	 * Callback to execute on back button presses.
	 * @param callback  The callback. If null it uses the default back behaviour.
	 */
	public void setOnBackCallback(OnBackCallback callback) {
		if (callback == null) {
			mOnBackCallback = DEFAULT_ON_BACK;
		} else {
			mOnBackCallback = callback;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (mOnBackCallback != null) {
				return mOnBackCallback.onBack();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	
	
}
