package net.androidpunk;

import java.util.Vector;

import net.androidpunk.flashcompat.Event;
import net.androidpunk.flashcompat.Event.OnEventListener;
import net.androidpunk.flashcompat.Timer;
import net.androidpunk.utils.Draw;
import net.androidpunk.utils.Input;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Log;

public class Engine {
	
	private static final String TAG = "Engine";
	
	/**
	 * If the game should stop updating/rendering.
	 */
	public boolean paused = false;

	/**
	 * Cap on the elapsed time (default at 60 FPS). Raise this to allow for lower framerates (eg. 1 / 30).
	 */
	public float maxElapsed = 1/60f;

	/**
	 * The max amount of frames that can be skipped in fixed framerate mode.
	 */
	public int maxFrameSkip = 5;

	/**
	 * The amount of milliseconds between ticks in fixed framerate mode.
	 */
	public int tickRate = 4;
	
	private float mDelta = 0;
	private long mTime;
	private long mLast;
	private long mPrev;
	private Timer mTimer;
	private float mRate;
	private float mSkip;
	

	// Debug timing information.
	private long mUpdateTime;
	private long mRenderTime;
	private long mGameTime;
	private long mJavaTime;
	
	// FrameRate tracking.
	private long mFrameLast = 0;
	private long mFrameListSum = 0;
	private Vector<Long> mFrameList = new Vector<Long>();
	
	public static final Vector<Timer> TIMERS = new Vector<Timer>();
	
	
	public static int mPendingEvents = 0;
	
	public static int mEventListenersCount = 0;
	public static final OnEventListener mEventListeners[] = new OnEventListener[10];
	
	
	public static void checkEvents() {
		synchronized (mEventListeners) {
			if ((mPendingEvents & Event.TIMER) > 0) {
				for (int i = 0; i < mEventListenersCount; i++) {
					if (mEventListeners[i].type == Event.TIMER) {
						mEventListeners[i].event();
					}
				}
				mPendingEvents ^= Event.TIMER;
			} else if ((mPendingEvents & Event.ADDED_TO_STAGE) > 0) {
				for (int i = 0; i < mEventListenersCount; i++) {
					if (mEventListeners[i].type == Event.ADDED_TO_STAGE) {
						mEventListeners[i].event();
					}
				}
				mPendingEvents ^= Event.ADDED_TO_STAGE;
			} else if ((mPendingEvents & Event.ENTER_FRAME) > 0) {
				for (int i = 0; i < mEventListenersCount; i++) {
					if (mEventListeners[i].type == Event.ENTER_FRAME) {
						mEventListeners[i].event();
					}
				}
				mPendingEvents ^= Event.ENTER_FRAME;
			}
		}
	}
	
	public static void fire(int event) {
		mPendingEvents |= event;
	}
	
	private OnEventListener onStage = new OnEventListener(Event.ADDED_TO_STAGE) {
		@Override
		public void event() {
			// remove event listener
			removeEventListener(onStage);

			// switch worlds
			if (FP.mGoto != null) 
				checkWorld();

			// game start
			init();

			// start game loop
			mRate = 1000 / FP.assignedFrameRate;
			if (FP.fixed) {
				// fixed framerate
				mSkip = mRate * maxFrameSkip;
				mLast = mPrev = SystemClock.uptimeMillis();
				mTimer = new Timer(tickRate);
				addEventListener(onTimer);
				mTimer.start();
			} else {
				// nonfixed framerate
				mLast = SystemClock.uptimeMillis();
				addEventListener(onEnterFrame);
			}
			
			FP.screen.resize();
		}
	};
	
	private OnEventListener onTimer = new OnEventListener(Event.TIMER) {
		@Override
		public void event() {
			// update timer
			mTime = SystemClock.uptimeMillis();
			mDelta += (mTime - mLast);
			//Log.d(TAG, String.format("Delta %f millis this frame %d to hit rate %f", mDelta, (mTime - mLast), mRate));
			mLast = mTime;

			// quit if a frame hasn't passed
			if (mDelta < mRate) 
				return;

			// update timer
			mGameTime = mTime;
			FP.javaTime = mTime - mJavaTime;

			// update console
			//if (FP._console) 
			//	FP._console.update();

			// update loop
			if (mDelta > mSkip)
				mDelta = mSkip;
			while (mDelta > mRate) {
				// update timer
				mUpdateTime = mTime;
				mDelta -= mRate;
				FP.elapsed = (mTime - mPrev) / 1000.0f;
				if (FP.elapsed > maxElapsed) 
					FP.elapsed = maxElapsed;
				FP.elapsed *= FP.rate;
				mPrev = mTime;

				// update loop
				if (!paused) 
					update();

				// update input
				Input.update();

				// update timer
				mTime = SystemClock.uptimeMillis();
				FP.updateTime = mTime - mUpdateTime;
			}

			// update timer
			mRenderTime = mTime;

			// render loop
			// Handled in GL Thread
			//if (!paused) 
			//	render();
			
			// update timer
			mTime = mJavaTime = SystemClock.uptimeMillis();
			FP.renderTime = mTime - mRenderTime;
			FP.gameTime =  mTime - mGameTime;
		}
	};
	
	private OnEventListener onEnterFrame = new OnEventListener(Event.ENTER_FRAME) {
		@Override
		public void event() {
			// update timer
			mTime = mGameTime = SystemClock.uptimeMillis();
			FP.javaTime = mTime - mJavaTime;
			mUpdateTime = mTime;
			FP.elapsed = (mTime - mLast) / 1000.0f;
			if (FP.elapsed > maxElapsed) 
				FP.elapsed = maxElapsed;
			FP.elapsed *= FP.rate;
			
			//Log.d(TAG, String.format("Elapsed %f millis delta millis %d to hit rate %f", FP.elapsed, (mTime - mLast), mRate));
			mLast = mTime;

			// update console
			//if (FP.mConsole != null) 
			//	FP.mConsole.update();
			 
			// update loop
			if (!paused) 
				update();

			// update input
			Input.update();

			// update timer
			mTime = mRenderTime = SystemClock.uptimeMillis();
			FP.updateTime = mTime - mUpdateTime;

			// render loop
			// Handled in GL Thread
			//if (!paused) 
			//	render();
			
			// update timer
			mTime = mJavaTime = SystemClock.uptimeMillis();
			FP.renderTime = mTime - mRenderTime;
			FP.gameTime = mTime - mGameTime;
		}
	};

	public Engine(int width, int height) {
		this(width,height, 60, false);
	}
	/**
	 * Constructor. Defines startup information about your game.
	 * @param	width			The width of your game.
	 * @param	height			The height of your game.
	 * @param	frameRate		The game framerate, in frames per second.
	 * @param	fixed			If a fixed-framerate should be used.
	 */
	public Engine(int width, int height, float frameRate, boolean fixed) {
		// global game properties
		FP.width = width;
		FP.height = height;
		FP.assignedFrameRate = frameRate;
		FP.fixed = fixed;

		// global game objects
		FP.engine = this;
		FP.screen = new Screen();
		FP.bounds = new Rect(0, 0, width, height);
		FP.setWorld(new World());

		// miscellanious startup stuff
		if (FP.getRandomSeed() == 0) 
			FP.randomizeSeed();
		FP.entity = new Entity();
		FP.mTime = SystemClock.uptimeMillis();

		// on-stage event listener
		addEventListener(onStage);
	}
	
	public void addEventListener(OnEventListener listener) {
		if (mEventListenersCount < 10) {
			mEventListeners[mEventListenersCount++] = listener;
		} else {
			Log.e(TAG, "Could not add a new event listener too many listeners");
		}
	}
	
	public void removeEventListener(OnEventListener listener) {
		boolean found = false;
		for (int i = 0; i < mEventListenersCount; i++) {
			if (found) {
				if (i+1 < 9) {
					mEventListeners[i] = mEventListeners[i+1];
				} else {
					mEventListeners[i] = null;
				}
			}
			if (listener == mEventListeners[i]) {
				found = true;
				mEventListeners[i] = mEventListeners[i+1];
			}
		}
		if (found) {
			mEventListenersCount--;
		}
	}
	
	/** @private Switch Worlds if they've changed. */
	private void checkWorld() {
		if (FP.mGoto == null) 
			return;
		World world = FP.getWorld();
		if(world != null) { 
			world.end();
			world.removeAll();
			world.updateLists();
			if (world.autoClear && world.mTween != null)
				world.clearTweens();
		}
		
		FP.mWorld = FP.mGoto;
		if (world != null) {
			world = null;
			System.gc();
			System.gc();
		}
		FP.mGoto = null;
		FP.camera = FP.mWorld.camera;
		FP.mWorld.updateLists();
		FP.mWorld.begin();
		FP.mWorld.updateLists();
		
		FP.mWorld.active = true;
	}
	
	/**
	 * Override this, called after Engine has been added to the stage.
	 */
	public void init() {

	}
	
	/**
	 * Updates the game, updating the World and Entities.
	 */
	public void update() {
		if (FP.getWorld().active) {
			if (FP.getWorld().mTween != null) 
				FP.getWorld().updateTweens();
			FP.getWorld().update();
		}
		FP.getWorld().updateLists();
		if (FP.mGoto != null)
			checkWorld();
	}
	
	/**
	 * Renders the game, rendering the World and Entities.
	 */
	public void render() {
		if (FP.getWorld() == null) {
			return;
		}
		// timing stuff
		long t = SystemClock.uptimeMillis();
		if (mFrameLast != 0) 
			mFrameLast = t;

		// render loop
		FP.screen.swap();
		Draw.resetTarget();
		//FP.screen.refresh();
		World world = FP.getWorld();
		if (world.visible)
			world.render();

		// more timing stuff
		t = SystemClock.uptimeMillis();
		long time = t - mFrameLast;
		mFrameListSum += time;
		mFrameList.add(time);
		if (mFrameList.size() > 10) {
			mFrameListSum -= mFrameList.get(0);
			mFrameList.remove(0);
		}
		if (mFrameListSum > 0 ) 
			FP.frameRate = 1000.0f / (mFrameListSum / mFrameList.size());
		
		mFrameLast = t;
	}
}
