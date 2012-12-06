package net.androidpunk.android;

import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.Engine;
import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.Graphic;
import net.androidpunk.R;
import net.androidpunk.Sfx;
import net.androidpunk.flashcompat.Event;
import net.androidpunk.flashcompat.Timer;
import net.androidpunk.graphics.Text;
import net.androidpunk.graphics.atlas.AtlasText;
import net.androidpunk.graphics.atlas.GraphicList;
import net.androidpunk.graphics.opengl.Atlas;
import net.androidpunk.graphics.opengl.TextAtlas;
import net.androidpunk.utils.Input;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
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
		
	public static final Object mUpdateLock = new Object();
	
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
		long mRenderTime;
		private OpenGLSystem mOpenGLSystem = new OpenGLSystem(); 
		
		// For debugging purposes
		private int mDebugUpdateCount;
		private GraphicList mDebug;
		private AtlasText mFPS, mUpdate, mRender;
		
		public APRenderer() {
			if (FP.debug) {
				Paint p = new Paint();
				int fpsWidth = (int)p.measureText("FPS:   0");
				mFPS = new AtlasText("FPS:  0", 0);
				mUpdate = new AtlasText("update:  0ms", 0);
				mUpdate.x = fpsWidth + FP.dip(5);
				mRender = new AtlasText("render:  0ms", 0);
				mRender.x = mUpdate.x;
				mRender.y = -p.ascent() + p.descent() + FP.dip(2);
				
				mDebug = new GraphicList(mFPS, mUpdate, mRender);
				mDebugUpdateCount = 0;
			}
		}
		
		public void onDrawFrame(GL10 gl) {
			OpenGLSystem.setGL(gl);
			
			mRenderTime = SystemClock.uptimeMillis();
			// process queue runnables for a max of 8ms.
			synchronized (mUpdateLock) {
				
				gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	
				OpenGLSystem.processQueue(14);
				
				// Iterate loop and draw them.
				if (mEngine != null) {
					mEngine.render();
				}
				if (FP.debug) {
					if (mDebugUpdateCount % 30 == 0) {
						mFPS.setText(String.format("FPS: %3.0f", Math.min(FP.frameRate, 60)));
						mUpdate.setText(String.format("update: %2dms", FP.updateTime));
						mRender.setText(String.format("render: %2dms", FP.renderTime));
						mDebugUpdateCount = 0;
					}
					mDebugUpdateCount++;
					FP.point.set(0,0);
					OpenGLSystem.processQueue(4);
					mDebug.render(gl, FP.point, FP.point);
				}
			}
			
			FP.renderTime = SystemClock.uptimeMillis() - mRenderTime;
			OpenGLSystem.setGL(null);
			
		}

		public void onSurfaceChanged(GL10 gl, int width, int height) {
			Log.d(TAG, "Surface Size Change: " + width + ", " + height);
			mScreenRect.set(0, 0, width, height);
			
			FP.displayWidth = width;
			FP.displayHeight = height;
			
			Engine.fire(Event.ADDED_TO_STAGE);
			
	        //mWidth = w;0
	        //mHeight = h;
	        // ensure the same aspect ratio as the game
	        float scaleX = (float)width / static_width;
	        float scaleY =  (float)height / static_height;
	        
	        FP.halfWidth = static_width/2;
	        FP.halfHeight = static_height/2;
	        
	        final int viewportWidth = (int)(mScreenRect.width());
	        final int viewportHeight = (int)(mScreenRect.height());
	        
	        gl.glViewport(0, 0, viewportWidth, viewportHeight);
	        gl.glScissor(0, 0, viewportWidth, viewportHeight);
	        
	        mScaleX = scaleX;
	        mScaleY = scaleY;
	        
	        gl.glMatrixMode(GL10.GL_MODELVIEW);
	        gl.glLoadIdentity();
	        
	        gl.glMatrixMode(GL10.GL_PROJECTION);
	        gl.glLoadIdentity();
	        
	        gl.glOrthof(0, viewportWidth, viewportHeight, 0, -1, 1);
	        gl.glScalef(mScaleX, mScaleY, 0.0f);
	        mStarted = true;
	        
	        //This should give it a bit of time to setup anything created during initial surface load.
	        OpenGLSystem.setGL(gl);
			OpenGLSystem.processQueue();
			OpenGLSystem.setGL(null);
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			/*
	         * Some one-time OpenGL initialization can be made here probably based
	         * on features of this particular context
	         */
			
	        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

	        gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
	        gl.glShadeModel(GL10.GL_FLAT);
	       
	        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	        
	        gl.glEnable(GL10.GL_TEXTURE_2D);
	        gl.glEnable(GL10.GL_SCISSOR_TEST);

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
	        if (mEngine == null) {
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
		
	}
	
	public static final OnBackCallback DEFAULT_ON_BACK = new OnBackCallback() {
		
		@Override
		public boolean onBack() {
			
			if (FP.engine != null)
				FP.engine.paused = true;
			AlertDialog.Builder builder = new AlertDialog.Builder(FP.context);
			
			builder.setTitle(R.string.exit_dialog_title);
			builder.setMessage(R.string.exit_dialog_message);
			
			OnClickListener ocl = new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					if (which == DialogInterface.BUTTON_POSITIVE) {
						FP.activity.finish();
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
			Log.d(TAG, "Exiting Engine");
		}
		
		private void step() {
			if (mStarted) {
				long now = SystemClock.uptimeMillis();
				Engine.fire(Event.ENTER_FRAME);
				synchronized (mUpdateLock) {
					Engine.checkEvents();
				}
				mSurfaceView.requestRender();
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		FP.activity = this;
		FP.resources = getResources();
		FP.context = this;
		setContentView(R.layout.main);
		
		mSurfaceView = (GLSurfaceView) findViewById(R.id.surface_view);
		mSurfaceView.setOnTouchListener(this);
		mRenderer = new APRenderer();
		mSurfaceView.setRenderer(mRenderer);
		mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		if (FP.debugOpenGL) {
			mSurfaceView.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR);
		}
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
		FP.typeface = TextAtlas.getFontFromRes(R.raw.novamono);
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
		// A real resume to game state.
		if (mEngine != null) {
			// Reload the atlas
			int count = Atlas.ATLAS.size();
			for (int i = 0; i < count; i++) {
				Atlas.ATLAS.get(i).reload();
			}
			// TODO make bitmap font so I can load that atlas instead of scowring the entities for the Text to reload.
			Vector<Entity> v = new Vector<Entity>();
			FP.getWorld().getAll(v);
			count = v.size();
			for (int i = 0; i < count; i++) {
				Entity e = v.get(i);
				Graphic g = e.getGraphic();
				if (g instanceof Text) {
					g.reload();
				} else if (g instanceof GraphicList) {
					GraphicList list = (GraphicList)g;
					Vector<Graphic> glist = list.getChildren();
					int glistCount = glist.size();
					for (int k = 0; k < glistCount; k++ ) {
						Graphic listGraphic = glist.get(k);
						if (listGraphic instanceof Text) {
							listGraphic.reload();
						}
					}
				}
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (FP.debug) {
			FP.getConsole().shutdown();
		}
		Sfx.releaseAll();
		FP.clearCachedBitmaps();
		Engine.clearEventListeners();
		
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
			FP.onBack = DEFAULT_ON_BACK;
		} else {
			FP.onBack = callback;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (FP.onBack != null) {
				return FP.onBack.onBack();
			}
		}
		Input.onKeyChange(keyCode, event);
		return super.onKeyDown(keyCode, event);
	}
	
}
