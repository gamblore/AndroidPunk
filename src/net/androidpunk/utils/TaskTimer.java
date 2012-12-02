package net.androidpunk.utils;

public class TaskTimer {

	public static abstract class OnTimeup implements Runnable {
		public abstract void run();
	}
	
	private OnTimeup mTimeupFunction;
	private float mTimer;
	private float mEndTimer;
	
	/**
	 * A timer that you can step to run a function.
	 * @param timerLength The length you want to loop on.
	 * @param timeupFunction The function you want to run.
	 */
	public TaskTimer(float timerLength, OnTimeup timeupFunction) {
		this(timerLength, timeupFunction, 0f);
	}
	
	/**
	 * A timer that can step to run a function. Started at an offset.
	 * @param timerLength
	 * @param timeupFunction
	 * @param offset
	 */
	public TaskTimer(float timerLength, OnTimeup timeupFunction, float offset) {
		mEndTimer = timerLength;
		mTimer = offset;
		mTimeupFunction = timeupFunction;
	}
	/**
	 * Steps the timer object by a set amount of seconds. If it triggers the function it will call it.
	 * @param seconds The number of seconds to step.
	 */
	public void step(float seconds) {
		mTimer += seconds;
		if (mTimer > mEndTimer) {
			mTimer -= mEndTimer;
			mTimeupFunction.run();
		}
	}
}
