package net.androidpunk.graphics;

/**
 * Template used by Spritemap to define animations. Don't create
 * these yourself, instead you can fetch them with Spritemap's add().
 */
public class Anim {

	protected SpriteMap mParent;
	protected String mName;
	protected int[] mFrames;
	protected int mFrameRate;
	protected int mFrameCount;
	protected boolean mLoop;

	public Anim(String name, int[] frames) {
		
	}
	public Anim(String name, int[] frames, int frameRate) {
		
	}
	
	public Anim(String name, int[] frames, int frameRate, boolean loop) {
		mName = name;
		mFrames = frames;
		mFrameRate = frameRate;
		mLoop = loop;
		mFrameCount = frames.length;
	}
	
	/**
	 * Plays the animation without resetting.
	 */
	public void play() {
		play(false);
	}
	/**
	 * Plays the animation.
	 * @param	reset		If the animation should force-restart if it is already playing.
	 */
	public void play(boolean reset) {
		mParent.play(mName, reset);
	}
	
	/**
	 * Name of the animation.
	 */
	public String geName() { return mName; }

	/**
	 * Array of frame indices to animate.
	 */
	public int[] getFrames() { return mFrames; }

	/**
	 * Animation speed.
	 */
	public int getFrameRate() { return mFrameRate; }

	/**
	 * Amount of frames in the animation.
	 */
	public int getFrameCount() { return mFrameCount; }

	/**
	 * If the animation loops.
	 */
	public boolean getLoop() { return mLoop; }
}
