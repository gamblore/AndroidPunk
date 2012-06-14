package net.androidpunk;

import android.util.Log;

public class Tweener {
	
	private static final String TAG = "Tweener";
	
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
	 * If the Tweener should clear on removal. For Entities, this is when they are
	 * removed from a World, and for World this is when the active World is switched.
	 */
	public boolean autoClear = false;
	
	private Tween mTween;
	
	/**
	 * Updates the Tween container.
	 */
	public void update() {

	}
	
	/**
	 * Adds a new Tween.
	 * @param	t			The Tween to add.
	 * @return	The added Tween.
	 */
	public Tween addTween(Tween t) {
		return addTween(t, false);
	}
	
	/**
	 * Adds a new Tween.
	 * @param	t			The Tween to add.
	 * @param	start		If the Tween should call start() immediately.
	 * @return	The added Tween.
	 */
	public Tween addTween(Tween t, boolean start) {
		if (t.mParent != null) { 
			Log.w(TAG, "Cannot add a Tween object more than once.");
			return t;
		}
		t.mParent = this;
		t.mNext = mTween;
		
		if (mTween != null) 
			mTween.mPrev = t;
		mTween = t;
		if (start) 
			mTween.start();
		return t;
	}
	
	/**
	 * Removes a Tween.
	 * @param	t		The Tween to remove.
	 * @return	The removed Tween.
	 */
	public Tween removeTween(Tween t) {
		if (t.mParent != this) { 
			Log.w(TAG, "Core object does not contain Tween.");
			return t;
		}
		if (t.mNext != null) 
			t.mNext.mPrev = t.mPrev;
		if (t.mPrev != null) 
			t.mPrev.mNext = t.mNext;
		else 
			mTween = t.mNext;
		t.mNext = t.mPrev = null;
		t.mParent = null;
		t.active = false;
		
		return t;
	}
	
	/**
	 * Removes all Tweens.
	 */
	public void clearTweens() {
		Tween t = mTween;
		Tween n;
		while (t != null) {
			n = t.mNext;
			removeTween(t);
			t = n;
		}
	}
	
	/** 
	 * Updates all contained tweens.
	 */
	public void updateTweens() {
		Tween t = mTween;
		while (t != null) {
			if (t.active) {
				t.update();
				if (t.mFinish)
					t.finish();
			}
			t = t.mNext;
		}
	}
}
