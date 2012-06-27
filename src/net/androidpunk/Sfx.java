package net.androidpunk;

import java.util.Vector;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;
import net.androidpunk.flashcompat.OnCompleteCallback;

/**
 * Sound effect object used to play embedded sounds.
 */
public class Sfx {
	
	private static final String TAG = "Sfx";
	
	/**
	 * Optional callback function for when the sound finishes playing.
	 */
	public OnCompleteCallback complete;
	
	private static float GLOBAL_VOLUME = 1.0f;
	private static float GLOBAL_PAN = 0.0f;
	
	public static final SoundPool SOUND_POOL = new SoundPool(64, AudioManager.STREAM_MUSIC, 0);
	public static final Vector<Sfx> SOUNDS = new Vector<Sfx>();
	
	private int mSoundId;
	private int mStreamId;
	
	// Sound infromation.
	private float mVolume = 1.0f;
	private float mPan = 0.0f;
	private boolean mLooping = false;
	private boolean mPlayed = false;
	
	public static void setMasterVolume(float volume) {
		GLOBAL_VOLUME = volume;
		for(Sfx s : SOUNDS) {
			// reset using the new global volume
			s.setVolume(s.getVolume());
		}
	}
	
	public static void setMasterPan(float pan) {
		GLOBAL_PAN = pan;
		for(Sfx s : SOUNDS) {
			// reset using the new global pan
			s.setPan(s.getPan());
		}
	}
	
	/**
	 * Creates a sound effect from an raw resource. Store a reference to
	 * this object so that you can play the sound using play() or loop().
	 * @param	source		The embedded sound resource to use.
	 */
	public Sfx(int resId) {
		this(resId, null);
	}
	
	/**
	 * Creates a sound effect from an raw resource. Store a reference to
	 * this object so that you can play the sound using play() or loop().
	 * @param	source		The embedded sound resource to use.
	 * @param	complete	Optional callback function for when the sound finishes playing.
	 */
	public Sfx(int resId, OnCompleteCallback completeFunction) {
		complete = completeFunction;
		mSoundId = SOUND_POOL.load(FP.context, resId, 1);
		if (mSoundId > 0) {
			SOUNDS.add(this);
		}
	}
	
	/**
	 * Creates a sound effect from an asset source. Store a reference to
	 * this object so that you can play the sound using play() or loop().
	 * @param	source		The embedded sound class to use.
	 */
	public Sfx(AssetFileDescriptor assetFd) {
		this(assetFd, null);
	}
	
	/**
	 * Creates a sound effect from an asset source. Store a reference to
	 * this object so that you can play the sound using play() or loop().
	 * @param	source		The embedded sound class to use.
	 * @param	complete	Optional callback function for when the sound finishes playing.
	 */
	public Sfx(AssetFileDescriptor assetFd, OnCompleteCallback completeFunction) {
		complete = completeFunction;
		mSoundId = SOUND_POOL.load(assetFd, 1);
		if (mSoundId > 0) {
			SOUNDS.add(this);
		}
	}
	
	/**
	 * Creates a sound effect from an file path. Store a reference to
	 * this object so that you can play the sound using play() or loop().
	 * @param	source		The embedded sound class to use.
	 */
	public Sfx(String path) {
		this(path, null);
	}
	
	/**
	 * Creates a sound effect from an file path. Store a reference to
	 * this object so that you can play the sound using play() or loop().
	 * @param	source		The embedded sound class to use.
	 * @param	complete	Optional callback function for when the sound finishes playing.
	 */
	public Sfx(String path, OnCompleteCallback completeFunction) {
		complete = completeFunction;
		mSoundId = SOUND_POOL.load(path, 1);
		if (mSoundId > 0) {
			SOUNDS.add(this);
		}
	}
	
	private void play(float vol, float pan, int loop) {
		float left, right;
		
		mVolume = vol;
		mPan = pan;
		
		left = leftPan(vol, pan);
		right = rightPan(vol, pan);
		
		SOUND_POOL.stop(mStreamId);
		mStreamId = SOUND_POOL.play(mSoundId, left, right, 0, loop, 1.0f);
		
		mPlayed = true;
	}
	
	private float leftPan(float vol, float pan) {
		return pan > 0 ? (1.0f - pan) * (vol * GLOBAL_VOLUME) : vol * GLOBAL_VOLUME;
	}
	
	private float rightPan(float vol, float pan) {
		return pan > 0 ? (1.0f - pan) * (vol * GLOBAL_VOLUME) : vol * GLOBAL_VOLUME;
	}
	
	/**
	 * Plays the sound once at max volume
	 */
	public void play() {
		play(1.0f, 0);
	}
	
	/**
	 * Plays the sound once with even panning.
	 * @param	vol		Volume factor, a value from 0 to 1.
	 */
	public void play(float vol) {
		play(vol, 0);
	}
	
	/**
	 * Plays the sound once.
	 * @param	vol		Volume factor, a value from 0 to 1.
	 * @param	pan		Panning factor, a value from -1 to 1.
	 */
	public void play(float vol, float pan) {
		play(vol, pan, 0);
	}
	
	/**
	 * Plays the sound looping at max volume. Will loop continuously until you call stop(), play(), or loop() again.
	 * @param	vol		Volume factor, a value from 0 to 1.
	 */
	public void loop() {
		loop(1.0f, 0);
	}
	
	/**
	 * Plays the sound looping. Will loop continuously until you call stop(), play(), or loop() again.
	 * @param	vol		Volume factor, a value from 0 to 1.
	 */
	public void loop(float vol) {
		loop(vol, 0);
	}
	
	/**
	 * Plays the sound looping. Will loop continuously until you call stop(), play(), or loop() again.
	 * @param	vol		Volume factor, a value from 0 to 1.
	 * @param	pan		Panning factor, a value from -1 to 1.
	 */
	public void loop(float vol, float pan) {
		play(vol, pan, -1);
		mLooping = true;
	}
	
	/**
	 * Will set the sound to stop looping after this last playout.
	 */
	public void stopLooping() {
		SOUND_POOL.setLoop(mStreamId, 0);
		mLooping = false;
	}
	
	/**
	 * Stops the sound if it is currently playing.
	 * @return
	 */
	public boolean stop() {
		SOUND_POOL.pause(mStreamId);
		mLooping = false;
		return true;
	}
	
	/**
	 * Resumes the sound from the position stop() was called on it.
	 */
	public void resume() {
		SOUND_POOL.resume(mStreamId);
	}
	
	/**
	 * Get the volume factor (a value from 0 to 1) of the sound during playback.
	 */
	public float getVolume() { return mVolume; }
	
	/**
	 * Alter the volume factor (a value from 0 to 1) of the sound during playback.
	 */
	public void setVolume(float value) {
		if (value < 0) 
			value = 0;
		if (mVolume == value) 
			return;
		mVolume = value;
		SOUND_POOL.setVolume(mStreamId, leftPan(mVolume, mPan), rightPan(mVolume, mPan));
	}
	
	/**
	 * Get the panning factor (a value from -1 to 1) of the sound during playback.
	 */
	public float getPan() { return mPan; }
	
	/**
	 * Alter the panning factor (a value from -1 to 1) of the sound during playback.
	 */
	public void setPan(float value) {
		if (value < -1) 
			value = -1;
		if (value > 1) 
			value = 1;
		if (mPan == value) 
			return;
		mPan = value;
		SOUND_POOL.setVolume(mStreamId, leftPan(mVolume, mPan), rightPan(mVolume, mPan));
	}
	
	/**
	 * If the sound has been played.
	 */
	public boolean getPlayed() { return mPlayed; }
	
	/**
	 * If the sound is currently playing. Only knows if it is looping.
	 */
	public boolean getPlaying() { return mLooping && mPlayed; }
	
	public void release() {
		SOUND_POOL.unload(mStreamId);
		SOUNDS.remove(this);
	}
}
