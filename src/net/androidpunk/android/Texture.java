package net.androidpunk.android;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.opengl.GLUtils;
import android.util.Log;

public class Texture {

	private static final String TAG = "Texture";
	
	/**
	 * Name to use in glBindTexture;
	 */
	protected int name = -1;
	
	/**
	 * The opacity of the texture
	 */
	public float alpha = 1.0f;
	
	/**
	 * The tint of the texture.
	 */
	public float red = 1.0f;
	
	/**
	 * The tint of the texture.
	 */
	public float green = 1.0f;
	
	/**
	 * The tint of the texture.
	 */
	public float blue = 1.0f;
	
	/**
	 * Does this texture clamp or repeat.
	 */
	private boolean mRepeat;
	
	/**
	 * Set these to display a grid.
	 */
	public int displayWidth, displayHeight;
	/**
	 * Set these to offset the texture.
	 */
	public int offsetX = 0, offsetY = 0;
	
	private int imageWidth, imageHeight, texWidth, texHeight;
	
	public Texture() {
		
	}
	
	public static void checkGLError(GL10 gl) {
		
	}
	
	public static int nextHigher2(int v) {
		if (v == 0)
			return 1;
		v--;
		v |= v >> 1;
		v |= v >> 2;
		v |= v >> 4;
		v |= v >> 8;
		v |= v >> 16;
		return ++v;
	}
	
	public void createTexture(GL10 gl, Bitmap bm) {
		createTexture(gl, bm, false);
	}
	
	public void createTexture(GL10 gl, Bitmap bm, boolean repeat) {
		mRepeat = repeat;
		gl.glEnable(GL10.GL_TEXTURE_2D);
		displayWidth = imageWidth = bm.getWidth();
		displayHeight = imageHeight = bm.getHeight();
		texWidth = Texture.nextHigher2(imageWidth);
		texHeight = Texture.nextHigher2(imageHeight);
		
		Bitmap po2Bitmap = Bitmap.createScaledBitmap(bm, (int) texWidth, (int) texHeight, true); 
		
		int textures[] = new int[1];
		gl.glGenTextures(1, textures, 0); OpenGLSystem.checkGLError(gl);
		
		name = textures[0];
		// Select this OpenGL texture
		gl.glBindTexture(GL10.GL_TEXTURE_2D, name); OpenGLSystem.checkGLError(gl);
		Log.d(TAG, "Texture is " + texWidth + " by " + texHeight + " binded to " + name);
		// Set the texture parameters
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		if (repeat) {
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
		} else {
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE); 
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
		}
		
		// Allocate room for the texture
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, po2Bitmap, 0);
		
		po2Bitmap.recycle();
	}
	
	public void setTexture(GL10 gl) {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, name); 
	}
	
	public void setTexCoords(float[] texCoords) {
		if (mRepeat) {
			texCoords[0] = (float)offsetX/texWidth; texCoords[1] = (float)offsetY/texHeight;
			texCoords[2] = (float)offsetX/texWidth+((float)displayWidth/texWidth); texCoords[3] = (float)offsetY/texHeight;
			texCoords[4] = (float)offsetX/texWidth; texCoords[5] = (float)offsetY/texHeight+((float)displayHeight/texHeight);
			texCoords[6] = (float)offsetX/texWidth+((float)displayWidth/texWidth); texCoords[7] = (float)offsetY/texHeight+((float)displayHeight/texHeight);
		} else {
			texCoords[0] = (float)offsetX/texWidth; texCoords[1] = (float)offsetY/texHeight;
			texCoords[2] = (float)offsetX/texWidth+1.0f; texCoords[3] = (float)offsetY/texHeight;
			texCoords[4] = (float)offsetX/texWidth; texCoords[5] = (float)offsetY/texHeight+1.0f;
			texCoords[6] = (float)offsetX/texWidth+1.0f; texCoords[7] = (float)offsetY/texHeight+1.0f;
		}
		
	}
	
	public void releaseTexture(GL10 gl) {
		int textures[] = new int[1];
		textures[0] = name;
		gl.glDeleteTextures(1, textures, 0);
	}
}
