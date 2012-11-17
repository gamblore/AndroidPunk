package com.gamblore.androidpunk.test.scripts;

import java.io.IOException;
import java.io.InputStream;

import net.androidpunk.FP;
import net.androidpunk.World;
import net.androidpunk.scripts.APScript;
import net.androidpunk.scripts.javascript.JavaScript;

public class ScriptWorld extends World {

	private APScript mScript;
				
	public ScriptWorld() {
		try {
			InputStream is = FP.context.getAssets().open("scripts/test.js");
			mScript = new JavaScript(is);
			mScript.callFunction("main", this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
