package net.androidpunk.tweens.misc;

import net.androidpunk.Tween;
import net.androidpunk.flashcompat.OnCompleteCallback;
import net.androidpunk.flashcompat.OnEaseCallback;

/**
 * Tweens a numeric value.
 */
public class NumTween extends Tween {

	// Tween information.
	private float mStart;
	private float mRange;

	/**
	 * The current value.
	 */
	public float value = 0;

	/**
	 * Constructor.
	 */
	public  NumTween() {
		this(null, 0);
	}
	/**
	 * Constructor.
	 * @param	complete	Optional completion callback.
	 */
	public  NumTween(OnCompleteCallback completeFunction) {
		this(completeFunction, 0);
	}
	/**
	 * Constructor.
	 * @param	complete	Optional completion callback.
	 * @param	type		Tween type.
	 */
	public  NumTween(OnCompleteCallback completeFunction, int type) {
		super(0, type, completeFunction);
	}
	
	/**
	 * Tweens the value from one value to another linearly.
	 * @param	fromValue		Start value.
	 * @param	toValue			End value.
	 * @param	duration		Duration of the tween.
	 */
	public void tween(float fromValue, float toValue, float duration) {
		tween(fromValue, toValue, duration, null);
	}
	
	/**
	 * Tweens the value from one value to another.
	 * @param	fromValue		Start value.
	 * @param	toValue			End value.
	 * @param	duration		Duration of the tween.
	 * @param	ease			Optional easer function.
	 */
	public void tween(float fromValue, float toValue, float duration, OnEaseCallback ease) {
		mStart = value = fromValue;
		mRange = toValue - value;
		mTarget = duration;
		mEase = ease;
		start();
	}
	
	/** @private Updates the Tween. */
	@Override
	public void update() {
		super.update();
		value = mStart + mRange * mT;
	}
}
