package net.androidpunk.tweens.sound;

import net.androidpunk.Sfx;
import net.androidpunk.Tween;
import net.androidpunk.flashcompat.OnCompleteCallback;
import net.androidpunk.flashcompat.OnEaseCallback;

public class SfxFader extends Tween {
	
	// Fader information.
	private Sfx mSfx;
	private float mStart;
	private float mRange;
	private Sfx mCrossSfx;
	private float mCrossRange;
	private OnCompleteCallback mComplete;
	

	/** @private When the tween completes. */
	private final OnCompleteCallback mFinish = new OnCompleteCallback() {

		@Override
		public void completed() {
			if (mCrossSfx != null) {
				if (mSfx != null)
					mSfx.stop();
				mSfx = mCrossSfx;
				mCrossSfx.release();
				mCrossSfx = null;
			}
			if (mComplete != null) 
				mComplete.completed();
		}
	};

	/**
	 * Constructor.
	 * @param	sfx			The Sfx object to alter.
	 */
	public SfxFader(Sfx sfx) {
		this(sfx, null, 0);
	}
	
	/**
	 * Constructor.
	 * @param	sfx			The Sfx object to alter.
	 * @param	complete	Optional completion callback.
	 */
	public SfxFader(Sfx sfx, OnCompleteCallback completeFunction) {
		this(sfx, completeFunction, 0);
	}
	
	/**
	 * Constructor.
	 * @param	sfx			The Sfx object to alter.
	 * @param	complete	Optional completion callback.
	 * @param	type		Tween type.
	 */
	public SfxFader(Sfx sfx, OnCompleteCallback completeFunction, int type) {
		super(0, type, null);
		complete = mFinish;
		mComplete = completeFunction;
		mSfx = sfx;
	}
	
	/**
	 * Fades the Sfx to the target volume linearly.
	 * @param	volume		The volume to fade to.
	 * @param	duration	Duration of the fade.
	 */
	public void fadeTo(float volume, float duration) {
		fadeTo(volume, duration, null);
	}
	
	/**
	 * Fades the Sfx to the target volume.
	 * @param	volume		The volume to fade to.
	 * @param	duration	Duration of the fade.
	 * @param	ease		Optional easer function.
	 */
	public void fadeTo(float volume, float duration, OnEaseCallback ease) {
		if (volume < 0)
			volume = 0;
		mStart = mSfx.getVolume();
		mRange = volume - mStart;
		mTarget = duration;
		mEase = ease;
		start();
	}
	
	/**
	 * Fades out the Sfx, while also playing and fading in a replacement Sfx linearly to full volume.
	 * @param	play		The Sfx to play and fade in.
	 * @param	loop		If the new Sfx should loop.
	 * @param	duration	Duration of the crossfade.
	 */
	public void crossFade(Sfx play, boolean loop, float duration) {
		crossFade(play, loop, duration, 1.0f, null);
	}
	
	/**
	 * Fades out the Sfx, while also playing and fading in a replacement Sfx linearly.
	 * @param	play		The Sfx to play and fade in.
	 * @param	loop		If the new Sfx should loop.
	 * @param	duration	Duration of the crossfade.
	 * @param	volume		The volume to fade in the new Sfx to.
	 */
	public void crossFade(Sfx play, boolean loop, float duration, float volume) {
		crossFade(play, loop, duration, volume, null);
	}
	
	/**
	 * Fades out the Sfx, while also playing and fading in a replacement Sfx.
	 * @param	play		The Sfx to play and fade in.
	 * @param	loop		If the new Sfx should loop.
	 * @param	duration	Duration of the crossfade.
	 * @param	volume		The volume to fade in the new Sfx to.
	 * @param	ease		Optional easer function.
	 */
	public void crossFade(Sfx play, boolean loop, float duration, float volume, OnEaseCallback ease) {
		mCrossSfx = play;
		mCrossRange = volume;
		mStart = mSfx.getVolume();
		mRange = -mStart;
		mTarget = duration;
		mEase = ease;
		if (loop) 
			mCrossSfx.loop(0);
		else 
			mCrossSfx.play(0);
		start();
	}
	
	/** @private Updates the Tween. */
	@Override 
	public void update() {
		super.update();
		if (mSfx != null) 
			mSfx.setVolume(mStart + mRange * mT);
		if (mCrossSfx != null) 
			mCrossSfx.setVolume(mCrossRange * mT);
	}
	
	/**
	 * The current Sfx this object is effecting.
	 */
	public Sfx getSfx() { return mSfx; }
}
