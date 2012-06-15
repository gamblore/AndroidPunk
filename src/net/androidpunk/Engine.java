package net.androidpunk;

import java.util.Vector;

import net.androidpunk.flashcompat.Event;
import net.androidpunk.flashcompat.Event.OnEventListener;
import net.androidpunk.flashcompat.Timer;
import net.androidpunk.utils.Draw;
import net.androidpunk.utils.Input;
import android.graphics.Rect;

public class Engine {
	
	/**
	 * If the game should stop updating/rendering.
	 */
	public boolean paused = false;

	/**
	 * Cap on the elapsed time (default at 30 FPS). Raise this to allow for lower framerates (eg. 1 / 10).
	 */
	public float maxElapsed = 0.0333f;

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
	private float mLast;
	private Timer mTimer;
	private float mRate;
	private float mSkip;
	private float mPrev;

	// Debug timing information.
	private long mUpdateTime;
	private long mRenderTime;
	private long mGameTime;
	private long mSystemTime;
	
	// FrameRate tracking.
	private long mFrameLast = 0;
	private long mFrameListSum = 0;
	private Vector<Long> mFrameList = new Vector<Long>();
	
	public static final Vector<Timer> TIMERS = new Vector<Timer>(); 
	
	public static final Vector<OnEventListener> mEventListeners = new Vector<OnEventListener>();
	
	public static void fire(int event) {
		if (event == Event.TIMER) {
			for (OnEventListener l : mEventListeners) {
				if (l.type == Event.TIMER) {
					l.event();
				}
			}
		} else if (event == Event.ADDED_TO_STAGE) {
			for (OnEventListener l : mEventListeners) {
				if (l.type == Event.ADDED_TO_STAGE) {
					l.event();
				}
			}
		} else if (event == Event.ENTER_FRAME) {
			for (OnEventListener l : mEventListeners) {
				if (l.type == Event.ENTER_FRAME) {
					l.event();
				}
			}
		}
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
			if (FP.fixed)
			{
				// fixed framerate
				mSkip = mRate * maxFrameSkip;
				mLast = mPrev = System.currentTimeMillis();
				mTimer = new Timer(tickRate);
				addEventListener(onTimer);
				mTimer.start();
			}
			else
			{
				// nonfixed framerate
				mLast = System.currentTimeMillis();
				addEventListener(onEnterFrame);
			}
		}
	};
	
	private OnEventListener onTimer = new OnEventListener(Event.TIMER) {
		@Override
		public void event() {
			// update timer
			mTime = System.currentTimeMillis();
			mDelta += (mTime - mLast);
			mLast = mTime;

			// quit if a frame hasn't passed
			if (mDelta < mRate) 
				return;

			// update timer
			mGameTime = mTime;
			FP.javaTime = mTime - FP.javaTime;

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
				FP.elapsed = (mTime - mPrev) / 1000;
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
				mTime = System.currentTimeMillis();
				FP.updateTime = mTime - FP.updateTime;
			}

			// update timer
			mRenderTime = mTime;

			// render loop
			if (!paused) 
				render();

			// update timer
			mTime = FP.javaTime = System.currentTimeMillis();
			FP.renderTime = mTime - FP.renderTime;
			FP.gameTime =  mTime - FP.gameTime;
		}
	};
	
	private OnEventListener onEnterFrame = new OnEventListener(Event.ENTER_FRAME) {
		@Override
		public void event() {
			// update timer
			mTime = mGameTime = System.currentTimeMillis();
			FP.javaTime = mTime - FP.javaTime;
			mUpdateTime = mTime;
			FP.elapsed = (mTime - mLast) / 1000;
			if (FP.elapsed > maxElapsed) 
				FP.elapsed = maxElapsed;
			FP.elapsed *= FP.rate;
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
			mTime = mRenderTime = System.currentTimeMillis();
			FP.updateTime = mTime - mUpdateTime;

			// render loop
			if (!paused) render();

			// update timer
			mTime = FP.javaTime = System.currentTimeMillis();
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
		FP.mTime = System.currentTimeMillis();

		// on-stage event listener
		addEventListener(onStage);
	}
	
	public void addEventListener(OnEventListener listener) {
		mEventListeners.add(listener);
	}
	
	public void removeEventListener(OnEventListener listener) {
		mEventListeners.remove(listener);
	}
	
	/** @private Switch Worlds if they've changed. */
	private void checkWorld() {
		if (FP.mGoto == null) 
			return;
		World world = FP.getWorld();
		world.end();
		world.updateLists();

		if (world != null && world.autoClear && world.mTween != null) 
			world.clearTweens();
		
		FP.setWorld(FP.mGoto);
		FP.mGoto = null;
		FP.camera = world.camera;
		world.updateLists();
		world.begin();
		world.updateLists();
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
		// timing stuff
		long t = System.currentTimeMillis();
		if (mFrameLast != 0) 
			mFrameLast = t;

		// render loop
		FP.screen.swap();
		Draw.resetTarget();
		FP.screen.refresh();
		World world = FP.getWorld();
		if (world.visible)
			world.render();
		FP.screen.redraw();

		// more timing stuff
		t = System.currentTimeMillis();
		long time = t - mFrameLast;
		mFrameListSum += time;
		mFrameList.add(time);
		if (mFrameList.size() > 10) {
			mFrameListSum -= mFrameList.get(0);
			mFrameList.remove(0);
		}
			
		FP.frameRate = 1000 / (mFrameListSum / mFrameList.size());
		mFrameLast = t;
	}
}
