package net.androidpunk.tweens.motion;

import net.androidpunk.Tween;
import net.androidpunk.flashcompat.OnCompleteCallback;
import net.androidpunk.flashcompat.OnEaseCallback;

/**
 * Base class for motion Tweens.
 */
public class Motion extends Tween {
	
	/**
	 * Current x position of the Tween.
	 */
	public float x = 0;
	
	/**
	 * Current y position of the Tween.
	 */
	public float y = 0;
	
	/**
	 * Constructor.
	 * @param	duration	Duration of the Tween.
	 */
	public Motion(float duration) {
		this(duration, null, 0, null);
	}
	
	/**
	 * Constructor.
	 * @param	duration	Duration of the Tween.
	 * @param	complete	Optional completion callback.
	 */
	public Motion(float duration, OnCompleteCallback completeFunction) {
		this(duration, completeFunction, 0, null);
	}
	
	/**
	 * Constructor.
	 * @param	duration	Duration of the Tween.
	 * @param	complete	Optional completion callback.
	 * @param	type		Tween type.
	 */
	public Motion(float duration, OnCompleteCallback completeFunction, int type) {
		this(duration, completeFunction, type, null);
	}
	
	/**
	 * Constructor.
	 * @param	duration	Duration of the Tween.
	 * @param	complete	Optional completion callback.
	 * @param	type		Tween type.
	 * @param	ease		Optional easer function.
	 */
	public Motion(float duration, OnCompleteCallback completeFunction, int type, OnEaseCallback ease) {
		super(duration, type, completeFunction, ease);
	}
}
