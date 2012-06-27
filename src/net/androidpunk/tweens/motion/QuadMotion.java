package net.androidpunk.tweens.motion;

import net.androidpunk.FP;
import net.androidpunk.flashcompat.OnCompleteCallback;
import net.androidpunk.flashcompat.OnEaseCallback;
import android.graphics.Point;

/**
 * Determines motion along a quadratic curve.
 */
public class QuadMotion extends Motion {
	
	// Curve information.
	private float mFromX = 0;
	private float mFromY = 0;
	private float mToX = 0;
	private float mToY = 0;
	private float mControlX = 0;
	private float mControlY = 0;
	private float mDistance = -1;

	/**
	 * Constructor.
	 */
	public QuadMotion() {
		super(0, null, 0, null);
	}
	
	/**
	 * Constructor.
	 * @param	complete	Optional completion callback.
	 */
	public QuadMotion(OnCompleteCallback completeFunction) {
		super(0, completeFunction, 0, null);
	}
	
	/**
	 * Constructor.
	 * @param	complete	Optional completion callback.
	 * @param	type		Tween type.
	 */
	public QuadMotion(OnCompleteCallback completeFunction, int type) {
		super(0, completeFunction, type, null);
	}
	
	/**
	 * Starts moving along the curve.
	 * @param	fromX		X start.
	 * @param	fromY		Y start.
	 * @param	controlX	X control, used to determine the curve.
	 * @param	controlY	Y control, used to determine the curve.
	 * @param	toX			X finish.
	 * @param	toY			Y finish.
	 * @param	duration	Duration of the movement.
	 */
	public void setMotion(float fromX, float fromY, float controlX, float controlY, float toX, float toY, float duration) {
		setMotion(fromX, fromY, controlX, controlY, toX, toY, duration, null);
	}
	
	/**
	 * Starts moving along the curve.
	 * @param	fromX		X start.
	 * @param	fromY		Y start.
	 * @param	controlX	X control, used to determine the curve.
	 * @param	controlY	Y control, used to determine the curve.
	 * @param	toX			X finish.
	 * @param	toY			Y finish.
	 * @param	duration	Duration of the movement.
	 * @param	ease		Optional easer function.
	 */
	public void setMotion(float fromX, float fromY, float controlX, float controlY, float toX, float toY, float duration, OnEaseCallback ease) {
		mDistance = -1;
		x = mFromX = fromX;
		y = mFromY = fromY;
		mControlX = controlX;
		mControlY = controlY;
		mToX = toX;
		mToY = toY;
		mTarget = duration;
		mEase = ease;
		start();
	}
	
	/**
	 * Starts moving along the curve at the speed.
	 * @param	fromX		X start.
	 * @param	fromY		Y start.
	 * @param	controlX	X control, used to determine the curve.
	 * @param	controlY	Y control, used to determine the curve.
	 * @param	toX			X finish.
	 * @param	toY			Y finish.
	 * @param	speed		Speed of the movement.
	 */
	public void setMotionSpeed(float fromX, float fromY, float controlX, float controlY, float toX, float toY, float speed) {
		setMotionSpeed(fromX, fromY, controlX, controlY, toX, toY, speed, null);
	}
	
	/**
	 * Starts moving along the curve at the speed.
	 * @param	fromX		X start.
	 * @param	fromY		Y start.
	 * @param	controlX	X control, used to determine the curve.
	 * @param	controlY	Y control, used to determine the curve.
	 * @param	toX			X finish.
	 * @param	toY			Y finish.
	 * @param	speed		Speed of the movement.
	 * @param	ease		Optional easer function.
	 */
	public void setMotionSpeed(float fromX, float fromY, float controlX, float controlY, float toX, float toY, float speed, OnEaseCallback ease) {
		mDistance = -1;
		x = mFromX = fromX;
		y = mFromY = fromY;
		mControlX = controlX;
		mControlY = controlY;
		mToX = toX;
		mToY = toY;
		mTarget = getDistance() / speed;
		mEase = ease;
		start();
	}
	
	/** @private Updates the Tween. */
	@Override
	public void update() {
		super.update();
		x = mFromX * (1 - mT) * (1 - mT) + mControlX * 2 * (1 - mT) * mT + mToX * mT * mT;
		y = mFromY * (1 - mT) * (1 - mT) + mControlY * 2 * (1 - mT) * mT + mToY * mT * mT;
	}
	
	/**
	 * The distance of the entire curve.
	 */
	public float getDistance()
	{
		if (mDistance >= 0) 
			return mDistance;
		Point a = FP.point;
		Point b = FP.point2;
		a.x = (int) (x - 2 * mControlX + mToX);
		a.y = (int) (y - 2 * mControlY + mToY);
		b.x = (int) (2 * mControlX - 2 * x);
		b.y = (int) (2 * mControlY - 2 * y);
		float A, B, C, ABC, A2, A32, C2, BA;
		A = 4 * (a.x * a.x + a.y * a.y);
		B = 4 * (a.x * b.x + a.y * b.y);
		C = b.x * b.x + b.y * b.y;
		ABC = (float)(2 * Math.sqrt(A + B + C));
		A2 = (float)Math.sqrt(A);
		A32 = 2 * A * A2;
		C2 = (float)(2 * Math.sqrt(C));
		BA = B / A2;
		return (float) ((A32 * ABC + A2 * B * (ABC - C2) + (4 * C * A - B * B) * Math.log((2 * A2 + BA + ABC) / (BA + C2))) / (4 * A32));
	}
}
