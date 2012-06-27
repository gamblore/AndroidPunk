package net.androidpunk.tweens.misc;

import net.androidpunk.Tween;
import net.androidpunk.flashcompat.OnCompleteCallback;

/**
 * A simple alarm, useful for timed events, etc.
 */
public class Alarm extends Tween {

	/**
	 * Constructor.
	 * @param	duration	Duration of the alarm.
	 */
	public Alarm(float duration) {
		super(duration, 0, null, null);
	}
	
	/**
	 * Constructor.
	 * @param	duration	Duration of the alarm.
	 * @param	complete	Optional completion callback.
	 */
	public Alarm(float duration, OnCompleteCallback competeFunction) {
		super(duration, 0, competeFunction, null);
	}
	/**
	 * Constructor.
	 * @param	duration	Duration of the alarm.
	 * @param	complete	Optional completion callback.
	 * @param	type		Tween type.
	 */
	public Alarm(float duration, OnCompleteCallback competeFunction, int type) {
		super(duration, type, competeFunction, null);
	}
	
	/**
	 * How much time has passed since reset.
	 */
	public float getElapsed() { return mTime; }
	
	/**
	 * Current alarm duration.
	 */
	public float getDuration() { return mTarget; }
	
	/**
	 * Time remaining on the alarm.
	 */
	public float getRemaining() { return mTarget - mTime; }
}
