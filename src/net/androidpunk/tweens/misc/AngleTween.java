package net.androidpunk.tweens.misc;

import net.androidpunk.FP;
import net.androidpunk.Tween;
import net.androidpunk.flashcompat.OnCompleteCallback;
import net.androidpunk.flashcompat.OnEaseCallback;

/**
 * Tweens from one angle to another.
 */
public class AngleTween extends Tween {
	
	// Tween information.
	private float mStart;
	private float mRange;
	
	/**
	 * The current value.
	 */
	public float angle = 0;
	
	/**
	 * Constructor.
	 * @param	complete	Optional completion callback.
	 * @param	type		Tween type.
	 */
	public AngleTween() {
		super(0, 0, null);
	}
	/**
	 * Constructor.
	 * @param	complete	Optional completion callback.
	 * @param	type		Tween type.
	 */
	public AngleTween(OnCompleteCallback completeFunction) {
		super(0, 0, completeFunction);
	}
	
	/**
	 * Constructor.
	 * @param	complete	Optional completion callback.
	 * @param	type		Tween type.
	 */
	public AngleTween(OnCompleteCallback completeFunction, int type) {
		super(0, type, completeFunction);
	}
	
	/**
	 * Tweens the value from one angle to another.
	 * @param	fromAngle		Start angle.
	 * @param	toAngle			End angle.
	 * @param	duration		Duration of the tween.
	 * @param	ease			Optional easer function.
	 */
	public void tween(float fromAngle, float toAngle, float duration) {
		tween(fromAngle, toAngle, duration, null);
	}
	/**
	 * Tweens the value from one angle to another.
	 * @param	fromAngle		Start angle.
	 * @param	toAngle			End angle.
	 * @param	duration		Duration of the tween.
	 * @param	ease			Optional easer function.
	 */
	public void tween(float fromAngle, float toAngle, float duration, OnEaseCallback ease) {
		mStart = angle = fromAngle;
		float d = toAngle - angle;
		float a = Math.abs(d);
		if (a > 181)
			mRange = (360 - a) * (d > 0 ? -1 : 1);
		else if (a < 179) 
			mRange = d;
		else
			mRange = (Integer)FP.choose((Integer)(180), (Integer)(-180));
		mTarget = duration;
		mEase = ease;
		start();
	}
	
	/** @private Updates the Tween. */
	@Override 
	public void update() {
		super.update();
		angle = (mStart + mRange * mT) % 360;
		if (angle < 0) 
			angle += 360;
	}
}
