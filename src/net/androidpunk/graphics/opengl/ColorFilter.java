package net.androidpunk.graphics.opengl;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;

public class ColorFilter {

	public float red;
	public float green;
	public float blue;
	public float alpha;
	
	private int mColor = 0xffffffff;
	
	public ColorFilter() {
		red = 1.0f;
		green = 1.0f;
		blue = 1.0f;
		alpha = 1.0f;
	}
	
	public void setColor(int color) {
		mColor = color;
		red = Color.red(mColor) / 255f;
		green = Color.green(mColor) / 255f;
		blue = Color.blue(mColor) / 255f;
		alpha = Color.alpha(mColor) / 255f;
	}
	
	public void applyColorFilter(GL10 gl) {
		gl.glColor4f(red, green, blue, alpha);
	}
}
