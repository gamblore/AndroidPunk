package net.androidpunk.flashcompat;

import net.androidpunk.Engine;

public class Timer {

	private long mElapsed = 0;
	private long mStart;
	private boolean mRunning;
	private long mTickRate;
	
	public Timer(long tickRate) {
		mTickRate = tickRate;
		Engine.TIMERS.add(this);
	}
	
	public void start() {
		mStart = System.currentTimeMillis();
		mRunning = true;
	}
	
	public void update() {
		if (!mRunning) 
			return;
		mElapsed = System.currentTimeMillis() - mStart;
		if (mElapsed > mTickRate)
			Engine.fire(Event.TIMER);
			mStart = System.currentTimeMillis();
	}
}
