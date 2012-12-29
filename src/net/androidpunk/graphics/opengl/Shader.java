package net.androidpunk.graphics.opengl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.androidpunk.FP;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

public class Shader {
	
	private static final String TAG = "Shader";
	
	public static final Map<Shader, Integer> SHADERS = new HashMap<Shader, Integer>();
	
	public static final Shader SHADER = new Shader();
	
	private int mGeometryRes, mFragmentRes;
	
	public Shader() {
		mGeometryRes = mFragmentRes = -1;
	}
	
	public Shader(int geometryRes, int fragementRes) {
		set(geometryRes, fragementRes);
	}
	
	public void set(int geometryRes, int fragementRes) {
		mGeometryRes = geometryRes;
		mFragmentRes = fragementRes;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Shader) {
			Shader s = (Shader) o;
			return s.mFragmentRes == mFragmentRes && s.mGeometryRes == mGeometryRes;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return (mFragmentRes << 16) + mGeometryRes;
	}
	
	private static String convertStreamToString(InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
	private static int loadShader(int type, String code) {
		int shader = GLES20.glCreateShader(type);

	    // add the source code to the shader and compile it
	    GLES20.glShaderSource(shader, code);
	    GLES20.glCompileShader(shader);
	    
	    int status[] = new int[1];
	    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);
	    
	    if (status[0] == GLES20.GL_FALSE) {
	    	Log.d(TAG, "Compile Status: " + status[0]);
	    	Log.d(TAG, GLES20.glGetShaderInfoLog(shader));
	    }

	    return shader;
	}
	
	/**
	 * This is run in a GL thread to return a program if it doesn't exist.
	 * @param geometryRes the geometry shader resource
	 * @param fragmentRes the fragment shader resource
	 * @return the compiled or cached program descriptor. 
	 */
	public static int getProgram(int geometryRes, int fragmentRes) {
		SHADER.set(geometryRes, fragmentRes);
		
		if (SHADERS.containsKey(SHADER)) {
			return SHADERS.get(SHADER);
		}
		
		Resources res = FP.context.getResources();
		Log.d(TAG, String.format("Compiling geometry: \"%s\" fragment: \"%s\"", res.getResourceEntryName(geometryRes), res.getResourceEntryName(fragmentRes)));
		
		int geometry = loadShader(GLES20.GL_VERTEX_SHADER, convertStreamToString(FP.context.getResources().openRawResource(geometryRes)));
		int fragment = loadShader(GLES20.GL_FRAGMENT_SHADER, convertStreamToString(FP.context.getResources().openRawResource(fragmentRes)));
		
		int program = GLES20.glCreateProgram();
		GLES20.glAttachShader(program, geometry);
		GLES20.glAttachShader(program, fragment);
		GLES20.glLinkProgram(program);
		
		int status[] = new int[1];
		GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0);
		
		
		if (status[0] == GLES20.GL_TRUE) {
			Shader store = new Shader(geometryRes, fragmentRes);
			SHADERS.put(store, program);
			return program;
		} else {
			Log.d(TAG, "Link Status: " + status[0]);
			Log.d(TAG, GLES20.glGetProgramInfoLog(program));
		}
		return -1;
		
	}
}
