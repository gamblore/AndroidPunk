package net.androidpunk.utils;

import net.androidpunk.FP;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Static helper class used for saving and loading data from shared preferences.
 */
public class Data {
	
	public static final String DEFAULT_PREFS = "AndroidPunk";
	
	/**
	 * Get a shared preference object that you can edit and read from.
	 * @return A shared preferences object using the default pref name.
	 */
	public static SharedPreferences getData() {
		return getData(DEFAULT_PREFS);
	}
	
	/**
	 * Get a shared preference object that you can edit and read from.
	 * @param preferenceFilename the name of the shared preference to read.
	 * @returnA shared preferences object using the default preferenceFilename.
	 */
	public static SharedPreferences getData(String preferenceFilename) {
		return FP.context.getSharedPreferences(preferenceFilename, Context.MODE_PRIVATE);
	}
}
