package net.androidpunk.utils;

public class TaskTimer {

	public static abstract class OnTimeup implements Runnable {
		public abstract void run();
	}
	
	private OnTimeup mTimeupFunction;
	private float mTimer;
	private float mEndTimer;
	
	public TaskTimer(float time, OnTimeup timeupFunction) {
		mEndTimer = time;
		mTimer = 0f;
		mTimeupFunction = timeupFunction;
	}
	
	public void step(float seconds) {
		mTimer += seconds;
		if (mTimer > mEndTimer) {
			mTimer -= mEndTimer;
			mTimeupFunction.run();
		}
	}
}
