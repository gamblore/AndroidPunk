package net.androidpunk.scripts.javascript;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.androidpunk.FP;
import net.androidpunk.scripts.APScript;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import android.util.Log;

public class JavaScript extends APScript {
	
	private static final String TAG = "APJavaScript";
	
	private Context mContext;
	private Scriptable mGlobalScope;
	
	public JavaScript(InputStream is) throws IOException {
		super(is);
	}
	public JavaScript(InputStream is, String scriptName) throws IOException {
		super(is, scriptName);
	}
	
	public void loadScript(InputStream is, String scriptName) throws IOException {
		mContext = Context.enter();
		mContext.setLanguageVersion(Context.VERSION_1_2);
		mContext.setOptimizationLevel(-1); // Have to turn off compilation
		mGlobalScope = mContext.initStandardObjects();
		((ScriptableObject)mGlobalScope).defineFunctionProperties(new String [] { "print" }, JavaScript.class, ScriptableObject.DONTENUM);
		mGlobalScope.put("FP", mGlobalScope, new FP());
		mContext.evaluateReader(mGlobalScope, new InputStreamReader(is), scriptName, 1, null);
	}

	@Override
	/**
	 * Note: this in JavaScript global scope.
	 */
	public Object callFunction(String funcName, Object... args) {
		return ScriptableObject.callMethod(mGlobalScope, funcName, args);
		//Function fn = (Function) mGlobalScope.get(funcName, mGlobalScope);
		//return fn.call(mContext, mGlobalScope, null, args);
	}
	
	public static void print(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		StringBuilder b = new StringBuilder();
		for (int i=0; i < args.length; i++) {
			if (i > 0) {
				b.append(' ');
			}
			
			// Convert the arbitrary JavaScript value into a string form.
			b.append(Context.toString(args[i]));
		}
	    Log.d(TAG, b.toString());
	}
}
