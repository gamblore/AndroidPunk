package net.androidpunk.tweens.misc;

import android.graphics.Color;
import net.androidpunk.Tween;
import net.androidpunk.flashcompat.OnCompleteCallback;
import net.androidpunk.flashcompat.OnEaseCallback;

/**
 * Tweens a color's red, green, and blue properties
 * independently. Can also tween an alpha value.
 */
public class ColorTween extends Tween {

	public int color;

	// Color information.
	private int mA;
	private int mR;
	private int mG;
	private int mB;
	private float mStartA, mStartR, mStartG, mStartB;
	private float mRangeA, mRangeR, mRangeG, mRangeB;
	
	/**
	 * Constructor.
	 * @param	complete	Optional completion callback.
	 * @param	type		Tween type.
	 */
	public ColorTween(OnCompleteCallback completeFunction, int type) {
		super(0, type, completeFunction);
	}
	
	/**
	 * Tweens the color to a new color and an alpha to a new alpha linearly.
	 * @param	duration		Duration of the tween.
	 * @param	fromColor		Start color.
	 * @param	toColor			End color.
	 */
	public void tween(float duration, int fromColor, int toColor) {
		tween(duration, fromColor, toColor, null);
	}
	
	/**
	 * Tweens the color to a new color (including alpha).
	 * @param	duration		Duration of the tween.
	 * @param	fromColor		Start color.
	 * @param	toColor			End color.
	 * @param	ease			Optional easer function.
	 */
	public void tween(float duration, int fromColor, int toColor, OnEaseCallback ease) {
		color = fromColor;
		mA = Color.alpha(fromColor);
		mR = Color.red(fromColor);
		mG = Color.green(fromColor);
		mB = Color.blue(fromColor);
		
		mStartA = mA / 255.0f;
		mStartR = mR / 255.0f;
		mStartG = mG / 255.0f;
		mStartB = mB / 255.0f;
		
		mRangeA = (Color.alpha(toColor) / 255.0f) - mStartA;
		mRangeR = (Color.red(toColor) / 255.0f) - mStartR;
		mRangeG = (Color.green(toColor) / 255.0f) - mStartG;
		mRangeB = (Color.blue(toColor) / 255.0f) - mStartB;
		
		mTarget = duration;
		mEase = ease;
		start();
	}
	
	/** @private Updates the Tween. */
	@Override 
	public void update() {
		super.update();
		
		mA = (int)((mStartA + mRangeA * mT) * 255);
		mR = (int)((mStartR + mRangeR * mT) * 255);
		mG = (int)((mStartG + mRangeG * mT) * 255);
		mB = (int)((mStartB + mRangeB * mT) * 255);
		
		color = Color.argb(mA, mR, mG, mB);
	}
	
	
	/**
	 * Alpha value of the current color, from 0 to 255.
	 */
	public int getAlpha() { return mA; }
	
	/**
	 * Red value of the current color, from 0 to 255.
	 */
	public int getRed() { return mR; }

	/**
	 * Green value of the current color, from 0 to 255.
	 */
	public int getGreen() { return mG; }

	/**
	 * Blue value of the current color, from 0 to 255.
	 */
	public int getBlue() { return mB; }
}
