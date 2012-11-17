package net.androidpunk.scripts.javascript;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.androidpunk.scripts.APScript;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

public class JavaScript extends APScript {
	
	private Context mContext;
	private Scriptable mGlobalScope;
	
	public JavaScript(InputStream is) throws IOException {
		super(is);
	}
	
	public void loadScript(InputStream is, String scriptName) throws IOException {
		mContext = Context.enter();
		mContext.setLanguageVersion(Context.VERSION_1_2);
		mContext.setOptimizationLevel(-1); // Have to turn off compilation
		mGlobalScope = mContext.initStandardObjects();
		
		mContext.evaluateReader(mGlobalScope, new InputStreamReader(is), scriptName, 1, null);
	}

	@Override
	/**
	 * No this in JavaScript global scope.
	 */
	public Object callFunction(String funcName, Object... args) {
		
		Function fn = (Function) mGlobalScope.get(funcName, mGlobalScope);
		return fn.call(mContext, mGlobalScope, null, args);
		
	}
	
	
}
