package net.androidpunk.tweens.motion;

import java.util.Vector;

import net.androidpunk.FP;
import net.androidpunk.flashcompat.OnCompleteCallback;
import net.androidpunk.flashcompat.OnEaseCallback;
import android.graphics.Point;
import android.util.Log;

/**
 * A series of points which will determine a path from the
 * beginning point to the end poing using quadratic curves.
 */
public class QuadPath extends Motion {

	private static final String TAG = "QuadPath";
	
	// Path information.
	private Vector<Point> mPoints = new Vector<Point>();
	private float mDistance = 0;
	private float mSpeed = 0;
	private int mIndex = 0;

	// Curve information.
	private boolean mUpdateCurve = true;
	private Vector<Point> mCurve = new Vector<Point>();
	private Vector<Float> mCurveD = new Vector<Float>();
	private Vector<Float> mCurveT = new Vector<Float>();

	// Curve points.
	private Point mA;
	private Point mB;
	private Point mC;

	/**
	 * Constructor.
	 * @param	complete	Optional completion callback.
	 * @param	type		Tween type.
	 */
	public QuadPath() {
		this(null, 0);
	}
	
	/**
	 * Constructor.
	 * @param	complete	Optional completion callback.
	 * @param	type		Tween type.
	 */
	public QuadPath(OnCompleteCallback completeFunction) {
		this(completeFunction, 0);
	}
	
	/**
	 * Constructor.
	 * @param	complete	Optional completion callback.
	 * @param	type		Tween type.
	 */
	public QuadPath(OnCompleteCallback completeFunction, int type) {
		super(0, completeFunction, type, null);
		mCurveT.add(0f);
	}
	
	/**
	 * Starts moving along the path.
	 * @param	duration	Duration of the movement.
	 * @param	ease		Optional easer function.
	 */
	public void setMotion(float duration) {
		setMotion(duration, null);
	}
	
	/**
	 * Starts moving along the path.
	 * @param	duration	Duration of the movement.
	 * @param	ease		Optional easer function.
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
	 * @param	ease		Optional easer function.
	 */
	public void setMotionSpeed(float speed) {
		setMotionSpeed(speed, null);
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
		mUpdateCurve = true;
		mPoints.add(new Point(x, y));
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
		if (mIndex < mCurve.size() - 1) {
			while (mT > mCurveT.get(mIndex + 1)) 
				mIndex ++;
		}
		float td = mCurveT.get(mIndex);
		float tt = mCurveT.get(mIndex + 1) - td;
		td = (mT - td) / tt;
		mA = mCurve.get(mIndex);
		mB = mPoints.get(mIndex + 1);
		mC = mCurve.get(mIndex + 1);
		x = mA.x * (1 - td) * (1 - td) + mB.x * 2 * (1 - td) * td + mC.x * td * td;
		y = mA.y * (1 - td) * (1 - td) + mB.y * 2 * (1 - td) * td + mC.y * td * td;
	}
	
	/** @private Updates the path, preparing the curve. */
	private void updatePath() {
		if (mPoints.size() < 3)
			Log.e(TAG, "A QuadPath must have at least 3 points to operate.");
		if (!mUpdateCurve) 
			return;
		mUpdateCurve = false;

		// produce the curve points
		Point p, c;
		Point l = mPoints.get(1);
		int i;
		for (i = 2; i < mPoints.size(); i++) {
			p = mPoints.get(i);
			if (mCurve.size() > i - 1) 
				c = mCurve.get(i - 1);
			else {
				c = new Point();
				mCurve.add(c);
			}
			if (i < mPoints.size() - 1) {
				c.x = l.x + (p.x - l.x) / 2;
				c.y = l.y + (p.y - l.y) / 2;
			}
			else {
				c.x = p.x;
				c.y = p.y;
			}
			l = p;
		}

		// find the total distance of the path
		mDistance = 0;
		mCurveD.clear();
		for (i = 0; i < mCurve.size() - 1; i++) {
			float value = curveLength(mCurve.get(i), mPoints.get(i + 1), mCurve.get(i + 1));
			mCurveD.add(value);
			mDistance += value;
		}

		// find t for each point on the curve
		float d = 0;
		mCurveT.clear();
		for (i = 1; i < mCurve.size()-1; i++) {
			d += mCurveD.get(i);
			float value = d / mDistance;
			
			mCurveT.add(value);
		}
		mCurveT.add(1f);
	}
	
	/**
	 * Amount of points on the path.
	 */
	public int getPointCount() { return mPoints.size(); }
	
	/** @private Calculates the lenght of the curve. */
	private float curveLength(Point start, Point control, Point finish) {
		Point a = FP.point;
		Point b = FP.point2;
		a.x = start.x - 2 * control.x + finish.x;
		a.y = start.y - 2 * control.y + finish.y;
		b.x = 2 * control.x - 2 * start.x;
		b.y = 2 * control.y - 2 * start.y;
		
		float A, B, C, ABC, A2, A32, C2, BA;
		A = 4 * (a.x * a.x + a.y * a.y);
		B = 4 * (a.x * b.x + a.y * b.y);
		C = b.x * b.x + b.y * b.y;
		ABC = (float) (2 * Math.sqrt(A + B + C));
		A2 = (float) Math.sqrt(A);
		A32 = 2 * A * A2;
		C2 = (float) (2 * Math.sqrt(C));
		BA = B / A2;
		
		return (float) ((A32 * ABC + A2 * B * (ABC - C2) + (4 * C * A - B * B) * Math.log((2 * A2 + BA + ABC) / (BA + C2))) / (4 * A32));
	}
	
}
