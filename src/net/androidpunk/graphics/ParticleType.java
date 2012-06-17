package net.androidpunk.graphics;

import net.androidpunk.FP;
import net.androidpunk.Tween.OnEaseCallback;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Rect;

/**
 * Template used to define a particle type used by the Emitter class. Instead
 * of creating this object yourself, fetch one with Emitter's add() function.
 */
public class ParticleType {
	// Particle information.
	protected String mName;
	protected Bitmap mSource;
	protected int mWidth;
	protected Rect mFrame;
	protected int[] mFrames;
	protected int mFrameCount;

	// Motion information.
	protected float mAngle;
	protected float mAngleRange;
	protected float mDistance;
	protected float mDistanceRange;
	protected float mDuration;
	protected float mDurationRange;
	protected OnEaseCallback mEase;

	// Alpha information.
	protected int mAlpha = 255;
	protected int mAlphaRange = 0;
	protected OnEaseCallback mAlphaEase;

	// Color information.
	protected int mRed = 255;
	protected int mRedRange = 0;
	protected int mGreen = 255;
	protected int mGreenRange = 0;
	protected int mBlue = 255;
	protected int mBlueRange = 0;
	protected OnEaseCallback mColorEase;

	// Buffer information.
	protected Bitmap mBuffer;
	protected Rect mBufferRect;
	
	/**
	 * Constructor.
	 * @param	name			Name of the particle type.
	 * @param	frames			Array of frame indices to animate through.
	 * @param	source			Source image.
	 * @param	frameWidth		Frame width.
	 * @param	frameHeight		Frame height.
	 * @param	frameCount		Frame count.
	 */
	public ParticleType(String name, int[] frames, Bitmap source, int frameWidth, int frameHeight) {
		mName = name;
		mSource = source;
		mWidth = source.getWidth();
		mFrame = new Rect(0, 0, frameWidth, frameHeight);
		mFrames = frames;
		mFrameCount = frames.length;
	}
	
	
	/**
	 * Defines the motion range for this particle type.
	 * @param	angle			Launch Direction.
	 * @param	distance		Distance to travel.
	 * @param	duration		Particle duration.
	 * @return	This ParticleType object.
	 */
	public ParticleType setMotion(float angle, float distance, float duration) {
		return setMotion(angle, distance, duration, 0, 0, 0, null);
	}
	
	/**
	 * Defines the motion range for this particle type.
	 * @param	angle			Launch Direction.
	 * @param	distance		Distance to travel.
	 * @param	duration		Particle duration.
	 * @param	angleRange		Random amount to add to the particle's direction.
	 * @param	distanceRange	Random amount to add to the particle's distance.
	 * @param	durationRange	Random amount to add to the particle's duration.
	 * @return	This ParticleType object.
	 */
	public ParticleType setMotion(float angle, float distance, float duration, float angleRange, float distanceRange, float durationRange) {
		return setMotion(angle, distance, duration, angleRange, distanceRange, durationRange, null);
	}
	
	/**
	 * Defines the motion range for this particle type.
	 * @param	angle			Launch Direction.
	 * @param	distance		Distance to travel.
	 * @param	duration		Particle duration.
	 * @param	angleRange		Random amount to add to the particle's direction.
	 * @param	distanceRange	Random amount to add to the particle's distance.
	 * @param	durationRange	Random amount to add to the particle's duration.
	 * @param	ease			easer function.
	 * @return	This ParticleType object.
	 */
	public ParticleType setMotion(float angle, float distance, float duration, float angleRange, float distanceRange, float durationRange, OnEaseCallback ease) {
		mAngle = angle * FP.RAD;
		mDistance = distance;
		mDuration = duration;
		mAngleRange = angleRange * FP.RAD;
		mDistanceRange = distanceRange;
		mDurationRange = durationRange;
		mEase = ease;
		return this;
	}
	
	
	/**
	 * Defines the motion range for this particle type based on the vector.
	 * @param	x				X distance to move.
	 * @param	y				Y distance to move.
	 * @param	duration		Particle duration.
	 * @return	This ParticleType object.
	 */
	public ParticleType setMotionVector(float x, float y, float duration) {
		return setMotionVector(x, y, duration, 0, null);
	}
	
	/**
	 * Defines the motion range for this particle type based on the vector.
	 * @param	x				X distance to move.
	 * @param	y				Y distance to move.
	 * @param	duration		Particle duration.
	 * @param	durationRange	Random amount to add to the particle's duration.
	 * @return	This ParticleType object.
	 */
	public ParticleType setMotionVector(float x, float y, float duration, float durationRange) {
		return setMotionVector(x, y, duration, durationRange, null);
	}
	
	/**
	 * Defines the motion range for this particle type based on the vector.
	 * @param	x				X distance to move.
	 * @param	y				Y distance to move.
	 * @param	duration		Particle duration.
	 * @param	durationRange	Random amount to add to the particle's duration.
	 * @param	ease			easer function.
	 * @return	This ParticleType object.
	 */
	public ParticleType setMotionVector(float x, float y, float duration, float durationRange, OnEaseCallback ease) {
		mAngle = (float)Math.atan2(y, x);
		mAngleRange = 0;
		mDuration = duration;
		mDurationRange = durationRange;
		mEase = ease;
		return this;
	}
	
	/**
	 * Sets the alpha range of this particle type ending at zero
	 * @param	start		The starting alpha.
	 * @return	This ParticleType object.
	 */
	public ParticleType setAlpha(int start) {
		return setAlpha(start, 0, null);
	}
	
	/**
	 * Sets the alpha range of this particle type.
	 * @param	start		The starting alpha.
	 * @param	finish		The finish alpha.
	 * @return	This ParticleType object.
	 */
	public ParticleType setAlpha(int start, int finish) {
		return setAlpha(start, finish, null);
	}
	
	/**
	 * Sets the alpha range of this particle type.
	 * @param	start		The starting alpha.
	 * @param	finish		The finish alpha.
	 * @param	ease		easer function.
	 * @return	This ParticleType object.
	 */
	public ParticleType setAlpha(int start, int finish, OnEaseCallback ease) {
		start = start < 0 ? 0 : (start > 255 ? 255 : start);
		finish = finish < 0 ? 0 : (finish > 255 ? 255 : finish);
		mAlpha = start;
		mAlphaRange = finish - start;
		mAlphaEase = ease;
		createBuffer();
		return this;
	}
	
	/**
	 * Sets the color range of this particle type.
	 * @param	start		The starting color.
	 * @param	finish		The finish color.
	 * @return	This ParticleType object.
	 */
	public ParticleType setColor(int start, int finish) {
		return setColor(start, finish, null);
	}
	
	/**
	 * Sets the color range of this particle type.
	 * @param	start		The starting color.
	 * @param	finish		The finish color.
	 * @param	ease		easer function.
	 * @return	This ParticleType object.
	 */
	public ParticleType setColor(int start, int finish, OnEaseCallback ease) {
		mRed = Color.red(start);
		mGreen = Color.green(start);
		mBlue = Color.blue(start);
		mRedRange = Color.red(finish) - mRed;
		mGreenRange = Color.green(finish) - mGreen;
		mBlueRange = Color.blue(finish) - mBlue;
		mColorEase = ease;
		createBuffer();
		return this;
	}
	
	/** @private Creates the buffer if it doesn't exist. */
	private void createBuffer() {
		if (mBuffer != null) 
			return;
		mBuffer = Bitmap.createBitmap(mFrame.width(), mFrame.height(), Config.ARGB_8888);
		mBufferRect.set(0, 0, mBuffer.getWidth(), mBuffer.getHeight());
	}
}
