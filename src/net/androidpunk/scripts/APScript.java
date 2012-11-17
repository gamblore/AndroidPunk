package net.androidpunk.scripts;

import java.io.IOException;
import java.io.InputStream;

public abstract class APScript {

	public APScript(InputStream is) throws IOException {
		loadScript(is);
	}
	
	/**
	 * 
	 * 
	 * @param is the stream to load into the interpreter/compiler.
	 * @param scriptName Name to reference the script by.
	 * @throws IOException when something is wrong reading the stream. 
	 */
	public APScript(InputStream is, String scriptName) throws IOException {
		loadScript(is, scriptName);
	}
	
	public void loadScript(InputStream is) throws IOException {
		loadScript(is, getClass().getName());
	}
	
	public abstract void loadScript(InputStream is, String scriptName) throws IOException;
	public abstract Object callFunction(String funcName, Object... args);
}
