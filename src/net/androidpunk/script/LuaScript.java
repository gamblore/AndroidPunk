package net.androidpunk.script;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.androidpunk.FP;

import com.naef.jnlua.LuaState;

public class LuaScript {

	private final LuaState mState = new LuaState();
	
	/**
	 * Load script from assets
	 * @param path path in assets
	 */
	public LuaScript(String path) throws IOException {
		File theFile = new File("../foo/bar/baz.txt");
		InputStream is = FP.context.getAssets().open(path);
		
		load(is, theFile.getName());

	}
	
	/**
	 * Load script from raw resource
	 * @param rawRes id of resource
	 */
	public LuaScript(int rawRes) throws IOException {
		InputStream is = FP.context.getResources().openRawResource(rawRes);
		
		load(is, FP.context.getResources().getResourceName(rawRes));
	}
	
	/**
	 * Load a unnamed script
	 * @param is stream to read script from
	 */
	public LuaScript(InputStream is) throws IOException {
		load(is, "unamed");
	}
	
	/**
	 * Load a script with name
	 * @param is stream to read script from
	 * @param name name of the script (for errors)
	 */
	public LuaScript(InputStream is, String name) throws IOException {
		load(is, name);
	}
	
	public LuaScript(String code, String name) {
		mState.load(code, name);
				
		// Evaluate
		mState.call(0, 0);
	}
	
	private void load(InputStream is, String name) throws IOException {

		mState.load(is, name, "bt");
				
		// Evaluate
		mState.call(0, 0);

	}
	
	/**
	 * 
	 * @return
	 */
	public LuaState getState() {
		return mState;
	}
	
	/**
	 * Calls <funcName>(Object o) so you have access to the object on the other side.
	 * Must 
	 * 
	 * function <funcName>(o)
	 *     return 0
	 * end
	 * @param o An object to talk to on the other side.
	 * @return An integer return code
	 */
	public int callFunction(String funcName, Object o) {
		mState.getGlobal(funcName);
		mState.pushJavaObject(o);
		mState.call(1, 1);
		
		int returnCode = mState.toInteger(1);
		mState.pop(1);
		
		return returnCode;
	}
	
	/**
	 * Calls main(Object o) so you have access to the object on the other side.
	 * Must 
	 * 
	 * function main(o)
	 *     return 0
	 * end
	 * @param o An object to talk to on the other side.
	 * @return An integer return code
	 */
	public int callMain(Object o) {
		return callFunction("main", o);
		
	}
}
