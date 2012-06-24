package net.androidpunk.flashcompat;

import net.androidpunk.Engine;

public class Timer {

	private long mElapsed = 0;
	private long mStart;
	private boolean mRunning;
	private long mTickRate;
	
	private static Thread TIMER_THREAD;
	
	public Timer(long tickRate) {
		mTickRate = tickRate;
		Engine.TIMERS.add(this);
	}
	
	public void start() {
		mStart = System.currentTimeMillis();
		mRunning = true;
		if (TIMER_THREAD == null || !TIMER_THREAD.isAlive()) {
			TIMER_THREAD = new Thread(new Runnable() {
				
				public void run() {
					while (mRunning) {
						update();
						try {
							Thread.sleep(4);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});
			TIMER_THREAD.start();
		}
	}
	
	public void stop() {
		mRunning = false;
	}
	
	public void update() {
		mElapsed = System.currentTimeMillis() - mStart;
		if (mElapsed > mTickRate) {
			Engine.fire(Event.TIMER);
			mStart = System.currentTimeMillis();
		}
	}
}
