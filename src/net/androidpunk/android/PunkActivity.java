package net.androidpunk.android;

import java.lang.reflect.InvocationTargetException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

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
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

public class PunkActivity extends Activity implements OnTouchListener {
	
	private static final String TAG = "PunkActivity";
	
	public static int static_width = 800;
	public static int static_height = 480;
	public static Class<? extends Engine> engine_class = Engine.class;
	
	private GLSurfaceView mSurfaceView;
	private static APRenderer mRenderer;
	//private SurfaceHolder mSurfaceHolder;
		
	private Engine mEngine;
	private Thread mGameThread;
	//private Thread mRenderThread;
	
	private static final Rect mScreenRect = new Rect();
	private static Path mDebugPath;
	private static Bitmap mDebugBitmap;
	private static Paint mDebugPaint;
	private static Canvas mDebugCanvas;
	
	private boolean mStarted = false;
	private boolean mRunning = false;
	
	private AudioManager mAudioManager;
	private int mOldVolume = 0;
	private int mVolume = 100;
	
	public static abstract class OnBackCallback {
		public abstract boolean onBack();
	}
	
	public class APRenderer implements GLSurfaceView.Renderer {
		private float mScaleX, mScaleY;
		private OpenGLSystem mOpenGLSystem = new OpenGLSystem(); 
		
		
		public void onDrawFrame(GL10 gl) {
			OpenGLSystem.setGL(gl);
			
			// process queue runnables for a max of 8ms.
			OpenGLSystem.processQueue(8);
			
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			// Iterate loop and draw them.
			//TODO remove FP.backbuffer once moved to OpenGL
			if (mEngine != null && FP.backBuffer != null) {
				mEngine.render();
			}
			OpenGLSystem.setGL(null);
			
		}

		public void onSurfaceChanged(GL10 gl, int width, int height) {
			Log.d(TAG, "Surface Size Change: " + width + ", " + height);
			mScreenRect.set(0, 0, width, height);
			Engine.fire(Event.ADDED_TO_STAGE);
			
	        //mWidth = w;0
	        //mHeight = h;
	        // ensure the same aspect ratio as the game
	        float scaleX = (float)width / static_width;
	        float scaleY =  (float)height / static_height;
	        
	        final int viewportWidth = (int)(mScreenRect.width() * scaleX);
	        final int viewportHeight = (int)(mScreenRect.height() * scaleY);
	        
	        gl.glViewport(0, 0, viewportWidth, viewportHeight);
	        
	        mScaleX = scaleX;
	        mScaleY = scaleY;
	        
	        gl.glMatrixMode(GL10.GL_MODELVIEW);
	        gl.glLoadIdentity();
	        
	        gl.glMatrixMode(GL10.GL_PROJECTION);
	        gl.glLoadIdentity();
	        
	        gl.glOrthof(0, viewportWidth, viewportHeight, 0, -1, 1);
	        mStarted = true;
	        
	        //This should give it a bit of time to setup anything created during initial surface load.
	        OpenGLSystem.setGL(gl);
			OpenGLSystem.processQueue(32);
			OpenGLSystem.setGL(null);
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			/*
	         * Some one-time OpenGL initialization can be made here probably based
	         * on features of this particular context
	         */
			gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
	        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

	        gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
	        gl.glShadeModel(GL10.GL_FLAT);
	       
	        gl.glEnable(GL10.GL_TEXTURE_2D);
	        /*
	         * By default, OpenGL enables features that improve quality but reduce
	         * performance. One might want to tweak that especially on software
	         * renderer.
	         */
	        gl.glDisable(GL10.GL_CULL_FACE);
	        gl.glDisable(GL10.GL_DITHER);
	        gl.glDisable(GL10.GL_LIGHTING);
	        gl.glDisable(GL10.GL_DEPTH_TEST);
	        gl.glDisable(GL10.GL_FOG);

	        gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
	        
	        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	        gl.glEnable(GL10.GL_BLEND);

	        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	       
	        String extensions = gl.glGetString(GL10.GL_EXTENSIONS); 
	        String version = gl.glGetString(GL10.GL_VERSION);
	        String renderer = gl.glGetString(GL10.GL_RENDERER);
	        boolean isSoftwareRenderer = renderer.contains("PixelFlinger");
	        boolean isOpenGL10 = version.contains("1.0");
	        boolean supportsDrawTexture = extensions.contains("draw_texture");
	        
	        // VBOs are standard in GLES1.1
	        // No use using VBOs when software renderering, esp. since older versions of the software renderer
	        // had a crash bug related to freeing VBOs.
	        boolean supportsVBOs = !isSoftwareRenderer && (!isOpenGL10 || extensions.contains("vertex_buffer_object"));
	        
	        FP.supportsDrawTexture = supportsDrawTexture;
	        FP.supportsVBOs = supportsVBOs;
	        
	        Log.d(TAG, "Graphics Support" + version + " (" + renderer + "): " +(supportsDrawTexture ?  "draw texture," : "") + (supportsVBOs ? "vbos" : ""));
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
		
	}
	
	public final OnBackCallback DEFAULT_ON_BACK = new OnBackCallback() {
		
		@Override
		public boolean onBack() {
			if (mEngine != null)
				mEngine.paused = true;
			AlertDialog.Builder builder = new AlertDialog.Builder(FP.context);
			
			builder.setTitle(R.string.exit_dialog_title);
			builder.setMessage(R.string.exit_dialog_message);
			
			OnClickListener ocl = new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					if (which == DialogInterface.BUTTON_POSITIVE) {
						finish();
					}
					if (mEngine != null)
						mEngine.paused = false;
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
		
		private void step() {
			if (mStarted) {
				long now = SystemClock.uptimeMillis();
				Engine.fire(Event.ENTER_FRAME);
				Engine.checkEvents();
				long delta = SystemClock.uptimeMillis() - now;
				if (delta < 16) {
					try {
						Thread.sleep(16-delta);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	/*
	protected class RenderRunner extends Thread {
		int mDebugCounter = 0; 
		
		@Override
		public void run() {
			while(mRunning) {
				if (!mStarted || mSurfaceHolder.isCreating() || FP.backBuffer == null) {
					try {
						//Log.d(TAG, "RenderThread Sleeing some");
						Thread.sleep(16);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}
				
				Canvas c = mSurfaceHolder.lockCanvas();
				if (c != null) {
					if(FP.backBuffer != null) {
						synchronized (FP.backBuffer) {
							c.drawBitmap(FP.backBuffer, FP.bounds, mScreenRect, null);
						}
					}
					if (FP.debug) {
						if (mDebugCounter++ > 10) {
							mDebugBitmap.eraseColor(0);
							Canvas debugC = mDebugCanvas;
							
							mDebugCounter = 0;
							Paint p = mDebugPaint;
							p.reset();
							
							p.setStyle(Style.FILL);
							p.setAntiAlias(true);
							p.setColor(0x80000000);
							
							debugC.drawPath(mDebugPath, p);
							
							
							// Draw the timers.
							p.setColor(0xffffffff);
							p.setAntiAlias(false);
							
							//Row 1
							p.setTextSize(30);
							String fps = String.format("FPS: %3d", (int)FP.frameRate);
							debugC.drawText(fps, 0, -p.ascent(), p);
							int step1 = (int) p.measureText(fps);
							
							//Row 2
							p.setTextSize(20);
							int step2 = step1 + (int) p.measureText("Update: 000ms");
							debugC.drawText(String.format("Update: %3dms", FP.updateTime), step1 + FP.dip(2), -p.ascent(), p);
							debugC.drawText(String.format("Render: %3dms", FP.renderTime), step1 + FP.dip(2), FP.dip(20)-p.ascent(), p);
						
							//Row 3
							debugC.drawText(String.format("Game: %3dms", FP.gameTime), step2 + FP.dip(4), -p.ascent(), p);
							debugC.drawText(String.format("Java: %3dms", FP.javaTime), step2 + FP.dip(4), FP.dip(20)-p.ascent(), p);
						}
						c.drawBitmap(mDebugBitmap, 0, 0, null);
					}
					mSurfaceHolder.unlockCanvasAndPost(c);
				}
				
			}
		}
	}
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FP.resources = getResources();
		FP.context = this;
		setContentView(R.layout.main);
		
		mSurfaceView = (GLSurfaceView) findViewById(R.id.surface_view);
		mSurfaceView.setOnTouchListener(this);
		mRenderer = new APRenderer();
		mSurfaceView.setRenderer(mRenderer);
		mSurfaceView.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mSurfaceView.onPause();
		
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
		if (mGameThread == null || !mGameThread.isAlive()) {
			mGameThread = new EngineRunner();
			mGameThread.start();
		}
		/*
		if (mRenderThread == null || !mRenderThread.isAlive()) {
			mRenderThread = new RenderRunner();
			mRenderThread.start();
		}
		*/
		mSurfaceView.onResume();
		
		
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
		FP.clearCachedBitmaps();
	}
/*
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.d(TAG, String.format("Resize to %dx%d", width, height));
		mScreenRect.set(0, 0, width, height);
		Engine.fire(Event.ADDED_TO_STAGE);
		mStarted = true;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			mEngine = engine_class.getConstructor(Integer.TYPE, Integer.TYPE, Float.TYPE, Boolean.TYPE).newInstance(static_width, static_height, FP.assignedFrameRate, FP.fixed);
			if (FP.debug) {
				mDebugPath = new Path();
				mDebugPath.moveTo(0, 0);
				mDebugPath.lineTo(2*FP.screen.getWidth()/3, 0);
				mDebugPath.cubicTo(2*FP.screen.getWidth()/3, FP.dip(25), 2*FP.screen.getWidth()/3-FP.dip(25), FP.dip(50), 2*FP.screen.getWidth()/3-FP.dip(50), FP.dip(50));
				mDebugPath.lineTo(0, FP.dip(50));
				mDebugPath.close();
				mDebugPaint = new Paint();
				mDebugBitmap = Bitmap.createBitmap(2*FP.screen.getWidth()/3, (int)FP.dip(50), Config.ARGB_8888);
				mDebugCanvas = new Canvas(mDebugBitmap);
			}
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
*/
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public boolean onTouch(View v, MotionEvent event) {
		if (mStarted) {
			FP.screen.setMotionEvent(event);
			/*
			try {
				Thread.sleep(4);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			*/
		}
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
