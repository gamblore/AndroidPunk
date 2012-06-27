package net.androidpunk.tweens.sound;

import net.androidpunk.FP;
import net.androidpunk.Tween;
import net.androidpunk.flashcompat.OnCompleteCallback;
import net.androidpunk.flashcompat.OnEaseCallback;

/**
 * Global volume fader.
 */
public class Fader extends Tween {

	// Fader information.
	private float mStart;
	private float mRange;
	
	/**
	 * Constructor.
	 */
	public Fader() {
		this(null, 0);
	}
	
	/**
	 * Constructor.
	 * @param	complete	Optional completion callback.
	 */
	public Fader(OnCompleteCallback completeFunction) {
		this(completeFunction, 0);
	}
	
	/**
	 * Constructor.
	 * @param	complete	Optional completion callback.
	 * @param	type		Tween type.
	 */
	public Fader(OnCompleteCallback completeFunction, int type) {
		super(0, type, completeFunction);
	}
	
	/**
	 * Fades FP.volume to the target volume linearly.
	 * @param	volume		The volume to fade to.
	 * @param	duration	Duration of the fade.
	 */
	public void fadeTo(float volume, float duration) {
		fadeTo(volume, duration, null);
	}
	
	/**
	 * Fades FP.volume to the target volume.
	 * @param	volume		The volume to fade to.
	 * @param	duration	Duration of the fade.
	 * @param	ease		Optional easer function.
	 */
	public void fadeTo(float volume, float duration, OnEaseCallback ease) {
		if (volume < 0) 
			volume = 0;
		mStart = FP.getVolume();
		mRange = volume - mStart;
		mTarget = duration;
		mEase = ease;
		start();
	}
	
	/** @private Updates the Tween. */
	@Override
	public void update() {
		super.update();
		FP.setVolume(mStart + mRange * mT);
	}
}
