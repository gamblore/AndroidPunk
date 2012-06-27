package net.androidpunk.tweens.motion;

import java.util.Vector;

import net.androidpunk.flashcompat.OnCompleteCallback;
import net.androidpunk.flashcompat.OnEaseCallback;
import android.graphics.Point;
import android.util.Log;

/**
 * Determines linear motion along a set of points.
 */
public class LinearPath extends Motion {

	private static final String TAG = "LinearPath";
	
	// Path information.
	private Vector<Point> mPoints = new Vector<Point>();
	private Vector<Float> mPointD = new Vector<Float>();
	private Vector<Float> mPointT = new Vector<Float>();
	private float mDistance = 0;
	private float mSpeed = 0;
	private int mIndex = 0;
	
	// Line information.
	private Point mLast;
	private Point mPrev;
	private Point mNext;

	/**
	 * Constructor.
	 * @param	complete	Optional completion callback.
	 * @param	type		Tween type.
	 */
	public LinearPath() {
		this(null, 0);
	}
	
	/**
	 * Constructor.
	 * @param	complete	Optional completion callback.
	 * @param	type		Tween type.
	 */
	public LinearPath(OnCompleteCallback completeFunction) {
		this(completeFunction, 0);
	}
	
	/**
	 * Constructor.
	 * @param	complete	Optional completion callback.
	 * @param	type		Tween type.
	 */
	public LinearPath(OnCompleteCallback completeFunction, int type) {
		super(0, completeFunction, type, null);
		mPointD.add(0f);
		mPointT.add(0f);
	}
	
	
	/**
	 * Starts moving along the path.
	 * @param	duration		Duration of the movement.
	 */
	public void setMotion(float duration) {
		setMotion(duration, null);
	}
	
	/**
	 * Starts moving along the path.
	 * @param	duration		Duration of the movement.
	 * @param	ease			Optional easer function.
	 */
	public void setMotion(float duration, OnEaseCallback ease) {
		updatePath();
		mTarget = duration;
		mSpeed = mDistance / duration;
		mEase = ease;
		start();
	}
	
	
	/**
	 * Starts moving along the path at the speed.
	 * @param	speed		Speed of the movement.
	 */
	public void setMotionSpeed(float speed) {
		setMotion(speed, null);
	}
	
	/**
	 * Starts moving along the path at the speed.
	 * @param	speed		Speed of the movement.
	 * @param	ease		Optional easer function.
	 */
	public void setMotionSpeed(float speed, OnEaseCallback ease) {
		updatePath();
		mTarget = mDistance / speed;
		mSpeed = speed;
		mEase = ease;
		start();
	}
	
	/**
	 * Adds the point to the path.
	 * @param	x		X position.
	 * @param	y		Y position.
	 */
	public void addPoint(int x, int y) {
		if (mLast != null) {
			mDistance += Math.sqrt((x - mLast.x) * (x - mLast.x) + (y - mLast.y) * (y - mLast.y));
			mPointD.add(mDistance);
		}
		mPoints.add(mLast = new Point(x, y));
	}
	
	/**
	 * Gets the first point on the path.
	 * @return	The Point object.
	 */
	public Point getPoint() {
		return getPoint(0);
	}
	/**
	 * Gets a point on the path.
	 * @param	index		Index of the point.
	 * @return	The Point object.
	 */
	public Point getPoint(int index) {
		return mPoints.get(index % mPoints.size());
	}
	
	/** @private Starts the Tween. */
	@Override
	public void start() {
		mIndex = 0;
		super.start();
	}
	
	/** @private Updates the Tween. */
	@Override
	public void update() {
		super.update();
		if (mIndex < mPoints.size() - 1) {
			while (mT > mPointT.get(mIndex + 1)) 
				mIndex++;
		}
		
		float td = mPointT.get(mIndex);
		float tt = mPointT.get(mIndex + 1) - td;
		td = (mT - td) / tt;
		mPrev = mPoints.get(mIndex);
		mNext = mPoints.get(mIndex + 1);
		x = mPrev.x + (mNext.x - mPrev.x) * td;
		y = mPrev.y + (mNext.y - mPrev.y) * td;
	}
	
	/** @private Updates the path, preparing it for motion. */
	private void updatePath() {
		if (mPoints.size() < 2)
			Log.e(TAG, "A LinearPath must have at least 2 points to operate.");
		if (mPointD.size() == mPointT.size()) 
			return;
		// evaluate t for each point
		for (int i = 0; i < mPoints.size(); i++) {
			float set = mPointD.get(i) / mDistance;
			mPointT.set(i, set);
		}
	}
	
	/**
	 * The full length of the path.
	 */
	public float getDistance() { return mDistance; }

	/**
	 * How many points are on the path.
	 */
	public int getPointCount() { return mPoints.size(); }
}
