package net.androidpunk.tweens.motion;

import net.androidpunk.FP;
import net.androidpunk.flashcompat.OnCompleteCallback;
import net.androidpunk.flashcompat.OnEaseCallback;

/**
 * Determines a circular motion.
 */
public class CircularMotion extends Motion {
	
	// Circle information.
	private float mCenterX = 0;
	private float mCenterY = 0;
	private float mRadius = 0;
	private float mAngle = 0;
	private float mAngleStart = 0;
	private float mAngleFinish = 0;
	private static final float CIRC = (float)Math.PI * 2;
	
	/**
	 * Constructor.
	 */
	public CircularMotion() {
		super(0, null, 0, null);
	}
	
	/**
	 * Constructor.
	 * @param	complete	Optional completion callback.
	 */
	public CircularMotion(OnCompleteCallback completeFunction) {
		super(0, completeFunction, 0, null);
	}
	
	/**
	 * Constructor.
	 * @param	complete	Optional completion callback.
	 * @param	type		Tween type.
	 */
	public CircularMotion(OnCompleteCallback completeFunction, int type) {
		super(0, completeFunction, type, null);
	}
	
	/**
	 * Starts moving along a circle.
	 * @param	centerX		X position of the circle's center.
	 * @param	centerY		Y position of the circle's center.
	 * @param	radius		Radius of the circle.
	 * @param	angle		Starting position on the circle.
	 * @param	clockwise	If the motion is clockwise.
	 * @param	duration	Duration of the movement.
	 */
	public void setMotion(float centerX, float centerY, float radius, float angle, boolean clockwise, float duration) {
		setMotion(centerX, centerY, radius, angle, clockwise, duration, null);
	}
	
	/**
	 * Starts moving along a circle.
	 * @param	centerX		X position of the circle's center.
	 * @param	centerY		Y position of the circle's center.
	 * @param	radius		Radius of the circle.
	 * @param	angle		Starting position on the circle.
	 * @param	clockwise	If the motion is clockwise.
	 * @param	duration	Duration of the movement.
	 * @param	ease		Optional easer function.
	 */
	public void setMotion(float centerX, float centerY, float radius, float angle, boolean clockwise, float duration, OnEaseCallback ease) {
		mCenterX = centerX;
		mCenterY = centerY;
		mRadius = radius;
		mAngle = mAngleStart = angle * FP.RAD;
		mAngleFinish = CIRC * (clockwise ? 1 : -1);
		mTarget = duration;
		mEase = ease;
		start();
	}
	
	/**
	 * Starts moving along a circle at the speed.
	 * @param	centerX		X position of the circle's center.
	 * @param	centerY		Y position of the circle's center.
	 * @param	radius		Radius of the circle.
	 * @param	angle		Starting position on the circle.
	 * @param	clockwise	If the motion is clockwise.
	 * @param	speed		Speed of the movement.
	 */
	public void setMotionSpeed(float centerX, float centerY, float radius, float angle, boolean clockwise, float speed) {
		setMotionSpeed(centerX, centerY, radius, angle, clockwise, speed, null);
	}
	
	/**
	 * Starts moving along a circle at the speed.
	 * @param	centerX		X position of the circle's center.
	 * @param	centerY		Y position of the circle's center.
	 * @param	radius		Radius of the circle.
	 * @param	angle		Starting position on the circle.
	 * @param	clockwise	If the motion is clockwise.
	 * @param	speed		Speed of the movement.
	 * @param	ease		Optional easer function.
	 */
	public void setMotionSpeed(float centerX, float centerY, float radius, float angle, boolean clockwise, float speed, OnEaseCallback ease) {
		mCenterX = centerX;
		mCenterY = centerY;
		mRadius = radius;
		mAngle = mAngleStart = angle * FP.RAD;
		mAngleFinish = CIRC * (clockwise ? 1 : -1);
		mTarget = (mRadius * CIRC) / speed;
		mEase = ease;
		start();
	}
	
	/** @private Updates the Tween. */
	@Override 
	public void update() {
		super.update();
		mAngle = mAngleStart + mAngleFinish * mT;
		x = (float)(mCenterX + Math.cos(mAngle) * mRadius);
		y = (float)(mCenterY + Math.sin(mAngle) * mRadius);
	}
	
	/**
	 * The current position on the circle.
	 */
	public float getAngle() { return mAngle; }

	/**
	 * The circumference of the current circle motion.
	 */
	public float getCircumference() { return mRadius * CIRC; }
}
