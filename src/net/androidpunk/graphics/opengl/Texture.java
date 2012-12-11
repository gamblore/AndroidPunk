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
import android.opengl.GLES20;
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
		if (mLoaded) {
			release();
		}
		load();
	}
	
	/**
	 * Set the texture bitmap and load it.
	 * @param texturePath Bitmap image.
	 */
	public void setTextureBitmap(Bitmap texture) {
		mSource = texture;

		if (mLoaded) {
			release();
		}
		load();
	}
	
	public class TextureLoadRunnable extends OpenGLSystem.OpenGLRunnable {
		private Bitmap mSource;
		
		public TextureLoadRunnable(Bitmap bm) {
			mSource = bm;
		}

		@Override
		public void run(GL10 gl) {
			if (mLoaded) {
				Log.e(TAG, "Texture already loaded");
				Thread.dumpStack();
				return;
			}
			if (createTexture(gl, mSource)) {
				if (mTexturePath != null) {
					mSource.recycle();
					mSource = null;
				}
			} else {
				//Re-run when you get the context back.
				OpenGLSystem.postRunnable(this);
			}
		}
		
	}
	/**
	 * Thread-safe creation the texture.
	 * @param gl the GL context.
	 */
	public void load() {
		mRect.set(0, 0, mSource.getWidth(), mSource.getHeight());
		TextureLoadRunnable runnable = new TextureLoadRunnable(mSource);
		OpenGLSystem.postRunnable(runnable);

	}
	
	/**
	 * Thread-safe release of the texture.
	 */
	public void release() {
		OpenGLSystem.postRunnable(new OpenGLRunnable() {
			@Override
			public void run(GL10 gl) {
				releaseTexture(gl);
			}
		});
	}
	
	public void reload() {
		release();
		if (mTexturePath != null) {
			setTextureBitmap(mTexturePath);
		} else {
			load();
		}
	}
	
	/**
	 * Move the texture to VRAM. Bitmap must be power of two dimensions.
	 * @param gl the GL context.
	 * @param bm The bitmap to put it. 
	 */
	private boolean createTexture(GL10 gl, Bitmap bm) {
		GLES20.glEnable(GL10.GL_TEXTURE_2D);
				
		int textures[] = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		
		mTextureName = textures[0];
		
		// Select this OpenGL texture
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureName);
		//Log.d(TAG, "Texture is bound to " + mTextureName);
		if (mTextureName == 0) { 
			return false;
		}
		// Set the texture parameters
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE); 
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		
		// Upload the texture to texture memory
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mSource, 0);
		mLoaded = true;
		return mLoaded;
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
		GLES20.glDeleteTextures(1, textures, 0);
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
