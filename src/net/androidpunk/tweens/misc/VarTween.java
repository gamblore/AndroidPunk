package net.androidpunk.tweens.misc;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import net.androidpunk.Tween;
import net.androidpunk.flashcompat.OnCompleteCallback;
import net.androidpunk.flashcompat.OnEaseCallback;
import android.util.Log;

/**
 * Tweens a numeric public property of an Object.
 */
public class VarTween extends Tween {
	
	private static final String TAG = "VarTween";
	
	private static final int TYPE_CHAR = 0;
	private static final int TYPE_SHORT = 1;
	private static final int TYPE_INT = 2;
	private static final int TYPE_FLOAT = 3;
	private static final int TYPE_DOUBLE = 4;
	
	// Tween information.
	private Object mObject;
	private String mProperty;
	private Field mField;
	private int mType;
	private float mStart;
	private float mRange;
	
	/**
	 * Constructor.
	 * @param	complete	Optional completion callback.
	 * @param	type		Tween type.
	 */
	public VarTween() {
		this(null, 0);
	}
	/**
	 * Constructor.
	 * @param	complete	Optional completion callback.
	 * @param	type		Tween type.
	 */
	public VarTween(OnCompleteCallback completeFunction) {
		this(completeFunction, 0);
	}
	/**
	 * Constructor.
	 * @param	complete	Optional completion callback.
	 * @param	type		Tween type.
	 */
	public VarTween(OnCompleteCallback completeFunction, int type) {
		super(0, type, completeFunction);
	}
	
	/**
	 * Tweens a numeric public property.
	 * @param	object		The object containing the property.
	 * @param	property	The name of the property (eg. "x").
	 * @param	to			Value to tween to.
	 * @param	duration	Duration of the tween.
	 * @param	ease		Optional easer function.
	 */
	public void tween(Object object, String property, float to, float duration, OnEaseCallback ease) {
		mObject = object;
		mProperty = property;
		mEase = ease;
		try {
			mField = object.getClass().getField(mProperty);
		} catch (NoSuchFieldException e) {
			Log.e(TAG, String.format("The Object %s does not have the property\"%s\", or it is not accessible.", object.toString(), property));
			return;
		}
		
		Type t = mField.getType();
		float startValue;
		try {
			if (Integer.TYPE.equals(t)) {
				startValue = (float)mField.getInt(object);
				mType = TYPE_INT;
			} else if (Float.TYPE.equals(t)) {
				startValue = (float)mField.getFloat(object);
				mType = TYPE_FLOAT;
			} else if (Double.TYPE.equals(t)) {
				startValue = (float)mField.getDouble(object);
				mType = TYPE_DOUBLE;
			} else if (Character.TYPE.equals(t)) {
				startValue = (float)mField.getChar(mObject);
				mType = TYPE_CHAR;
			} else if (Short.TYPE.equals(t)) {
				startValue = (float)mField.getShort(mObject);
				mType = TYPE_SHORT;
			} else {
				Log.e(TAG, String.format("The property \"%s\" is not numeric.", property));
				return;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			Log.e(TAG, String.format("The Object %s does not have the property\"%s\"", object.toString(), property));
			return;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			Log.e(TAG, String.format("The Object %s property\"%s\" is not accessible.", object.toString(), property));
			return;
		}
		mStart = startValue;
		mRange = to - mStart;
		mTarget = duration;
		start();
	}
	
	/** @private Updates the Tween. */
	@Override 
	public void update() {
		super.update();
		float value = mStart + mRange * mT;
		try {
			if (mType == TYPE_INT) {
				mField.setInt(mObject, (int)value);
			} else if (mType == TYPE_FLOAT) {
				mField.setFloat(mObject, value);
			} else if (mType == TYPE_DOUBLE) {
				mField.setDouble(mObject, (double)value);
			} else if (mType == TYPE_CHAR) {
				mField.setChar(mObject, (char)value);
			} else if (mType == TYPE_SHORT) {
				mField.setShort(mObject, (short)value);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			Log.e(TAG, String.format("The Object %s does not have the property\"%s\"", mObject.toString(), mProperty));
			return;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			Log.e(TAG, String.format("The Object %s property\"%s\" is not accessible.", mObject.toString(), mProperty));
			return;
		}
	}
}
