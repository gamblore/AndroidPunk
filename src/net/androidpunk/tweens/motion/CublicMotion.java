package net.androidpunk.tweens.motion;

import net.androidpunk.flashcompat.OnCompleteCallback;
import net.androidpunk.flashcompat.OnEaseCallback;

/**
 * Determines motion along a cubic curve.
 */
public class CublicMotion extends Motion {
	
	// Curve information.
	private float mFromX = 0;
	private float mFromY = 0;
	private float mToX = 0;
	private float mToY = 0;
	private float mAX = 0;
	private float mAY = 0;
	private float mBX = 0;
	private float mBY = 0;
	private float mTTT;
	private float mTT;

	/**
	 * Constructor.
	 */
	public CublicMotion() {
		super(0, null, 0, null);
	}
	
	/**
	 * Constructor.
	 * @param	complete	Optional completion callback.
	 */
	public CublicMotion(OnCompleteCallback completeFunction) {
		super(0, completeFunction, 0, null);
	}
	
	/**
	 * Constructor.
	 * @param	complete	Optional completion callback.
	 * @param	type		Tween type.
	 */
	public CublicMotion(OnCompleteCallback completeFunction, int type) {
		super(0, completeFunction, type, null);
	}
	
	
	/**
	 * Starts moving along the curve.
	 * @param	fromX		X start.
	 * @param	fromY		Y start.
	 * @param	aX			First control x.
	 * @param	aY			First control y.
	 * @param	bX			Second control x.
	 * @param	bY			Second control y.
	 * @param	toX			X finish.
	 * @param	toY			Y finish.
	 * @param	duration	Duration of the movement.
	 */
	public void setMotion(float fromX, float fromY, float aX, float aY, float bX, float bY, float toX, float toY, float duration) {
		setMotion(fromX, fromY, aX, aY, bX, bY, toX, toY, duration, null);
	}
	/**
	 * Starts moving along the curve.
	 * @param	fromX		X start.
	 * @param	fromY		Y start.
	 * @param	aX			First control x.
	 * @param	aY			First control y.
	 * @param	bX			Second control x.
	 * @param	bY			Second control y.
	 * @param	toX			X finish.
	 * @param	toY			Y finish.
	 * @param	duration	Duration of the movement.
	 * @param	ease		Optional easer function.
	 */
	public void setMotion(float fromX, float fromY, float aX, float aY, float bX, float bY, float toX, float toY, float duration, OnEaseCallback ease) {
		x = mFromX = fromX;
		y = mFromY = fromY;
		mAX = aX;
		mAY = aY;
		mBX = bX;
		mBY = bY;
		mToX = toX;
		mToY = toY;
		mTarget = duration;
		mEase = ease;
		start();
	}
	
	/** @private Updates the Tween. */
	@Override
	public void update() {
		super.update();
		x = mT * mT * mT * (mToX + 3 * (mAX - mBX) - mFromX) + 3 * mT * mT * (mFromX - 2 * mAX + mBX) + 3 * mT * (mAX - mFromX) + mFromX;
		y = mT * mT * mT * (mToY + 3 * (mAY - mBY) - mFromY) + 3 * mT * mT * (mFromY - 2 * mAY + mBY) + 3 * mT * (mAY - mFromY) + mFromY;
	}
	
}
