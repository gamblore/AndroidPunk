package net.androidpunk.graphics.opengl;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.FP;
import net.androidpunk.android.OpenGLSystem;
import net.androidpunk.android.OpenGLSystem.OpenGLRunnable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * An OpenGL Texture class Must be power of 2 sizes.
 * @author ametcalf
 *
 */
public class Texture {

	private static final String TAG = "Texture";
	
	/**
	 * Use this to bind to the texture.
	 */
	public int mTextureName;
	
	/**
	 * Use this to adjust the texture colors.
	 */
	public final ColorFilter mColorFilter = new ColorFilter();
	
	// The clip plane.
	private final Rect mRect = new Rect();
	
	// The path to the texture.
	private String mTexturePath;
	
	// Temporary bitmap to load it into texture VRAM.
	private Bitmap mSource;
	
	// Has the texture been loaded.
	private boolean mLoaded = false;
	
	
	public static final int nextHigher2(int v) {
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
	
	/**
	 * Do nothing 
	 */
	public Texture() {
		
	}
	
	/**
	 * Create a texture from the following asset path. Will load if created in GL Thread
	 * @param texturePath the path in assets/ where to find the image.
	 */
	public Texture(String texturePath) {
		setTextureBitmap(texturePath);
		
		load();
	}
	
	/**
	 * Create a texture from an already in memory bitmap. This will recycle the bitmap.
	 * @param source The bitmap to load in.
	 */
	public Texture(Bitmap source) {
		mSource = source;
		load();
	}
	
	/**
	 * Set the texture bitmap to an asset and load it.
	 * @param texturePath Asset path to the image.
	 */
	public void setTextureBitmap(String texturePath) {
		mTexturePath = texturePath;
		InputStream is = FP.getAsset(mTexturePath);
		mSource = BitmapFactory.decodeStream(is);
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		load();
	}
	
	/**
	 * Set the texture bitmap and load it.
	 * @param texturePath Asset path to the image.
	 */
	public void setTextureBitmap(Bitmap texture) {
		mSource = texture;

		load();
	}
	
	/**
	 * Thread-safe creation the texture.
	 * @param gl the GL context.
	 */
	public void load() {
		if (mLoaded) {
			Log.e(TAG, "Texture already loaded");
			Thread.dumpStack();
			return;
		}
		mRect.set(0, 0, mSource.getWidth(), mSource.getHeight());
		GL10 agl = OpenGLSystem.getGL();
		if (agl != null) {
			createTexture(agl, mSource);
			mSource.recycle();
			mSource = null;
		} else {
			OpenGLSystem.postRunnable(new OpenGLRunnable() {
				@Override
				public void run(GL10 gl) {
					createTexture(gl, mSource);
					mSource.recycle();
					mSource = null;
				}
			});
		}
	}
	
	/**
	 * Thread-safe release of the texture.
	 */
	public void release() {
		GL10 agl = OpenGLSystem.getGL();
		if (agl != null) {
			releaseTexture(agl);
		} else {
			OpenGLSystem.postRunnable(new OpenGLRunnable() {
				@Override
				public void run(GL10 gl) {
					releaseTexture(gl);
				}
			});
		}
	}
	
	/**
	 * Move the texture to VRAM. Bitmap must be power of two dimensions.
	 * @param gl the GL context.
	 * @param bm The bitmap to put it. 
	 */
	private void createTexture(GL10 gl, Bitmap bm) {
		gl.glEnable(GL10.GL_TEXTURE_2D);
				
		int textures[] = new int[1];
		gl.glGenTextures(1, textures, 0);
		
		mTextureName = textures[0];
		
		// Select this OpenGL texture
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureName);
		Log.d(TAG, "Texture is bound to " + mTextureName);
		// Set the texture parameters
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE); 
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
		
		
		// Allocate room for the texture
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mSource, 0);
		mLoaded = true;
	}
	
	/**
	 * Free the texture memory assosiated with this Texture.
	 * @param gl the GL context.
	 */
	private void releaseTexture(GL10 gl) {
		if (!mLoaded) {
			Log.e(TAG, "Texture wasn't loaded");
			Thread.dumpStack();
			return;
		}
		int textures[] = new int[1];
		textures[0] = mTextureName;
		gl.glDeleteTextures(1, textures, 0);
		mLoaded = false;
	}
	
	/**
	 * Is true when the texture is ready to be drawn.
	 * @return true if it is in the texture memory. 
	 */
	public boolean isLoaded() {
		return mLoaded;
	}
	
	/**
	 * Get the width of the texture.
	 * @return Texture width in pixels.
	 */
	public int getWidth() {
		return mRect.width();
	}
	
	/**
	 * Get the height of the texture.
	 * @return Texture height in pixels.
	 */
	public int getHeight() {
		return mRect.height();
	}
}
