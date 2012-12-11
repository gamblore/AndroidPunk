package net.androidpunk.graphics.atlas;


import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.android.OpenGLSystem;
import net.androidpunk.graphics.opengl.Atlas;
import net.androidpunk.graphics.opengl.GLGraphic;
import net.androidpunk.graphics.opengl.SubTexture;
import net.androidpunk.graphics.opengl.Texture;
import android.graphics.Point;

public class AtlasGraphic extends GLGraphic {
	
	private static final String TAG = "AtlasGraphic";
	
	private Texture mAtlas;
	
	protected SubTexture mSubTexture;

	public AtlasGraphic(Atlas atlas) {
		mAtlas = atlas;
	}
	
	public AtlasGraphic(SubTexture subTexture) {
		mSubTexture = subTexture;
		mAtlas = subTexture.getTexture();
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
		applyColor(gl);
		OpenGLSystem.setTexture(gl, getAtlas());

	}
	
	/*
	protected void setMatrix(GL10 gl) {
		// Translate to origin
		// scale the sprite
		// rotate the sprite
		// translate to position + origin * scale.
		float sX = scaleX * scale;
		float sY = scaleY * scale;
		
		gl.glTranslatef((originX * Math.abs(sX)) + mPoint.x, (originY * Math.abs(sY)) + mPoint.y, 0f);
		
		if (angle != 0) {
			gl.glRotatef(angle, 0, 0, 1.0f);
		}
		
		gl.glScalef(sX, sY, 1.0f);
		gl.glTranslatef(-originX, -originY, 0.0f);
		
	}
	*/
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
