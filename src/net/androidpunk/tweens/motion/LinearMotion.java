package net.androidpunk.tweens.motion;

import net.androidpunk.flashcompat.OnCompleteCallback;
import net.androidpunk.flashcompat.OnEaseCallback;

/**
 * Determines motion along a line, from one point to another.
 */
public class LinearMotion extends Motion {
	
	// Line information.
	private float mFromX = 0;
	private float mFromY = 0;
	private float mMoveX = 0;
	private float mMoveY = 0;
	private float mDistance = -1;
	
	/**
	 * Constructor.
	 */
	public LinearMotion() {
		super(0, null, 0, null);
	}
	
	/**
	 * Constructor.
	 * @param	complete	Optional completion callback.
	 */
	public LinearMotion(OnCompleteCallback completeFunction) {
		super(0, completeFunction, 0, null);
	}
	
	/**
	 * Constructor.
	 * @param	complete	Optional completion callback.
	 * @param	type		Tween type.
	 */
	public LinearMotion(OnCompleteCallback completeFunction, int type) {
		super(0, completeFunction, type, null);
	}
	
	/**
	 * Starts moving along a line.
	 * @param	fromX		X start.
	 * @param	fromY		Y start.
	 * @param	toX			X finish.
	 * @param	toY			Y finish.
	 * @param	duration	Duration of the movement.
	 */
	public void setMotion(float fromX, float fromY, float toX, float toY, float duration) {
		setMotion(fromX, fromY, toX, toY, duration, null);
	}
	
	/**
	 * Starts moving along a line.
	 * @param	fromX		X start.
	 * @param	fromY		Y start.
	 * @param	toX			X finish.
	 * @param	toY			Y finish.
	 * @param	duration	Duration of the movement.
	 * @param	ease		Optional easer function.
	 */
	public void setMotion(float fromX, float fromY, float toX, float toY, float duration, OnEaseCallback ease) {
		mDistance = -1;
		x = mFromX = fromX;
		y = mFromY = fromY;
		mMoveX = toX - fromX;
		mMoveY = toY - fromY;
		mTarget = duration;
		mEase = ease;
		start();
	}
	
	/**
	 * Starts moving along a line at the speed.
	 * @param	fromX		X start.
	 * @param	fromY		Y start.
	 * @param	toX			X finish.
	 * @param	toY			Y finish.
	 * @param	speed		Speed of the movement.
	 */
	public void setMotionSpeed(float fromX, float fromY, float toX, float toY, float speed) {
		setMotionSpeed(fromX, fromY, toX, toY, speed, null);
	}
	
	/**
	 * Starts moving along a line at the speed.
	 * @param	fromX		X start.
	 * @param	fromY		Y start.
	 * @param	toX			X finish.
	 * @param	toY			Y finish.
	 * @param	speed		Speed of the movement.
	 * @param	ease		Optional easer function.
	 */
	public void setMotionSpeed(float fromX, float fromY, float toX, float toY, float speed, OnEaseCallback ease) {
		mDistance = -1;
		x = mFromX = fromX;
		y = mFromY = fromY;
		mMoveX = toX - fromX;
		mMoveY = toY - fromY;
		mTarget = getDistance() / speed;
		mEase = ease;
		start();
	}
	
	/** @private Updates the Tween. */
	@Override 
	public void update() {
		super.update();
		x = mFromX + mMoveX * mT;
		y = mFromY + mMoveY * mT;
	}
	
	/**
	 * Length of the current line of movement.
	 */
	public float getDistance() {
		if (mDistance >= 0)
			return mDistance;
		return (mDistance = (float)Math.sqrt(mMoveX * mMoveX + mMoveY * mMoveY));
	}
}
