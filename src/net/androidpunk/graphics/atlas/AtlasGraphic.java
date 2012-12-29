package net.androidpunk.graphics.atlas;


import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.R;
import net.androidpunk.android.OpenGLSystem;
import net.androidpunk.graphics.opengl.Atlas;
import net.androidpunk.graphics.opengl.GLGraphic;
import net.androidpunk.graphics.opengl.Shader;
import net.androidpunk.graphics.opengl.SubTexture;
import net.androidpunk.graphics.opengl.Texture;
import android.graphics.Point;
import android.opengl.GLES20;

public class AtlasGraphic extends GLGraphic {
	
	private static final String TAG = "AtlasGraphic";
	
	private Texture mAtlas;
	
	protected SubTexture mSubTexture;

	public AtlasGraphic(Atlas atlas) {
		mAtlas = atlas;
		mProgram = Shader.getProgram(R.raw.shader_g_texture, R.raw.shader_f_texture);
	}
	
	public AtlasGraphic(SubTexture subTexture) {
		mSubTexture = subTexture;
		mAtlas = subTexture.getTexture();
		mProgram = Shader.getProgram(R.raw.shader_g_texture, R.raw.shader_f_texture);
	}
	
	public void setAtlas(Atlas atlas) {
		mAtlas = atlas;
	}
	
	public Texture getAtlas() {
		return mAtlas;
	}

	/**
	 * Sets the color filter and loads the texture if it is not already loaded.
	 */
	public void render(GL10 gl, Point point, Point camera) {
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		OpenGLSystem.setTexture(mProgram, getAtlas());
		super.render(gl, point, camera);
	}
	
	/**
	 * Get the width of the graphic.
	 * @return width
	 */
	public int getWidth() {
		return mSubTexture.getWidth();
	}
	
	/**
	 * Get the height of the graphic.
	 * @return height
	 */
	public int getHeight() {
		return mSubTexture.getHeight();
	}
	
	/**
	 * Centers the Image's originX/Y to its center.
	 */
	public void centerOrigin() {
		originX = mSubTexture.getWidth() / 2;
		originY = mSubTexture.getHeight() / 2;
	}
}
