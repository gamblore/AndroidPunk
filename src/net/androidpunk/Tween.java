package net.androidpunk;

public class Tween {
	
	public abstract class OnCompleteCallback {
		public abstract void completed();
	}
	
	public abstract class OnEaseCallback {
		public abstract float ease(float t);
	}
	/**
	 * Persistent Tween type, will stop when it finishes.
	 */
	public static final int PERSIST = 0;

	/**
	 * Looping Tween type, will restart immediately when it finishes.
	 */
	public static final int LOOPING = 1;

	/**
	 * Oneshot Tween type, will stop and remove itself from its core container when it finishes.
	 */
	public static final int ONESHOT = 2;

	/**
	 * If the tween should update.
	 */
	public boolean active;

	/**
	 * Tween completion callback.
	 */
	public OnCompleteCallback complete;
	
	// Tween information.
	private int mType;
	protected OnEaseCallback mEase;
	protected float mT = 0;
	
	// Timing information.
	protected float mTime;
	protected float mTarget;

	// List information.
	protected boolean mFinish;
	protected Tweener mParent;
	protected Tween mPrev;
	protected Tween mNext;
	
	/**
	 * Constructor. Specify basic information about the Tween.
	 * @param	duration		Duration of the tween (in seconds or frames).
	 * @param	type			Tween type, one of Tween.PERSIST (default), Tween.LOOPING, or Tween.ONESHOT.
	 * @param	complete		Optional callback for when the Tween completes.
	 * @param	ease			Optional easer function to apply to the Tweened value.
	 */
	public Tween(float duration) {
		this(duration, 0, null, null);
	}
	
	public Tween(float duration, int type) {
		this(duration, type, null, null);
	}
	
	public Tween(float duration, int type, OnCompleteCallback completeFunction) {
		this(duration, type, completeFunction, null);
	}
	
	public Tween(float duration, int type, OnCompleteCallback completeFunction, OnEaseCallback easeFunction) {
		mTarget = duration;
		mType = type;
		complete = completeFunction;
		mEase = easeFunction;
	}
	
	/**
	 * Updates the Tween, called by World.
	 */
	public void update() {
		mTime += FP.fixed ? 1 : FP.elapsed;
		
		mT = mTime / mTarget;
		if (mEase != null && mT > 0 && mT < 1) 
			mT = mEase.ease(mT);
		if (mTime >= mTarget) {
			mT = 1;
			mFinish = true;
		}
	}
	
	/**
	 * Starts the Tween, or restarts it if it's currently running.
	 */
	public void start() {
		mTime = 0;
		if (mTarget == 0) {
			active = false;
			return;
		}
		active = true;
	}
	
	/** @private Called when the Tween completes. */
	protected void finish() {
		switch (mType) {
			case PERSIST:
				mTime = mTarget;
				active = false;
				break;
			case LOOPING:
				mTime %= mTarget;
				mT = mTime / mTarget;
				if (mEase != null && mT > 0 && mT < 1) 
					mT = mEase.ease(mT);
				start();
				break;
			case ONESHOT:
				mTime = mTarget;
				active = false;
				mParent.removeTween(this);
				break;
		}
		mFinish = false;
		if (complete != null) 
			complete.completed();
	}
	
	public float getPercent() {
		return mTime / mTarget;
	}
	
	public void setPercent(float value) {
		mTime = mTarget * value;
	}
	
	public float getScale() {
		return mT;
	}
}
