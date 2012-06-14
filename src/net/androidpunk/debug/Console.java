package net.androidpunk.debug;

import android.util.Log;

public class Console {
	private static final String TAG = "Console";
	
	/**
	 * Logs data to the console.
	 * @param	...data		The data parameters to log, can be variables, objects, etc. Parameters will be separated by a space (" ").
	 */
	public void log(Object... data) {
		String s;
		if (data.length > 1)
		{
			s = "";
			int i = 0;
			while (i < data.length)
			{
				if (i > 0) 
					s += " ";
				s += data[i++].toString();
			}
		}
		else 
			s = data[0].toString();
		
		if (s.indexOf("\n") >= 0) {
			String[] a = s.split("\n");
			for (String string : a) {
				Log.d(TAG, string);
			}
		}
	}
}
