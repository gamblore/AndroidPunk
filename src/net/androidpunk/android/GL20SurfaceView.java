package net.androidpunk.android;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class GL20SurfaceView extends GLSurfaceView {

	public GL20SurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// Create an OpenGL ES 2.0 context
		setEGLContextClientVersion(2);
	}

}
