package net.androidpunk.graphics.atlas;

import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.R;
import net.androidpunk.graphics.opengl.Shader;
import net.androidpunk.graphics.opengl.SubTexture;
import android.graphics.Point;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.util.Log;

public class TiledImage extends Image {

	public static final String TAG = "TiledImage";
	
	private int mWidth;
	private int mHeight;
	
	private int mOffsetX = 0;
	private int mOffsetY = 0;
	
	public TiledImage(SubTexture subTexture, int width, int height) {
		this(subTexture, width, height, null);
	}
	
	public TiledImage(SubTexture subTexture, int width, int height, Rect clipRect) {
		super(subTexture, clipRect);
		
		mProgram = Shader.getProgram(R.raw.shader_g_repeating_texture, R.raw.shader_f_repeating_texture);
		
		mWidth = width;
		mHeight = height;
		
		originX = 0;
		originY = 0;
		
		// Shader is doing the repeating.
		AtlasGraphic.setGeometryBuffer(mVertexBuffer, 0, 0, mWidth, mHeight);
	}
	
	/**
	 * The x-offset of the texture.
	 */
	public int getOffsetX() { return mOffsetX; }
	public void setOffsetX(int value) {
		if (mOffsetX == value) 
			return;
		mOffsetX = value;
	}
	
	/**
	 * The y-offset of the texture.
	 */
	public int getOffsetY() { return mOffsetY; }
	public void setOffsetY(int value) {
		if (mOffsetY == value) 
			return;
		mOffsetY = value;
	}
	
	/**
	 * Sets the texture offset.
	 * @param	x		The x-offset.
	 * @param	y		The y-offset.
	 */
	public void setOffset(int x, int y) {
		if (mOffsetX == x && mOffsetY == y) 
			return;
		mOffsetX = x;
		mOffsetY = y;
	}
	

	@Override
	public void render(GL10 gl, Point point, Point camera) {
		GLES20.glUseProgram(mProgram);
		Rect subTextureBounds = mSubTexture.getBounds();
		
		//int atlasSizeHandle = GLES20.glGetUniformLocation(mProgram, "uAtlasSize");
		//GLES20.glUniform2f(atlasSizeHandle, getAtlas().getWidth(), getAtlas().getHeight());
		float atlasWidth = mSubTexture.getTexture().getWidth();
		float atlasHeight = mSubTexture.getTexture().getHeight();
		
		int topLeftHandle = GLES20.glGetUniformLocation(mProgram, "uTopLeft");
		//Log.d(TAG, String.format("%.3f, %.3f", subTextureBounds.left / atlasWidth, subTextureBounds.top / atlasHeight));
		GLES20.glUniform2f(topLeftHandle, subTextureBounds.left / atlasWidth, subTextureBounds.top / atlasHeight);
		
		int repeatHandle = GLES20.glGetUniformLocation(mProgram, "uRepeat");
		GLES20.glUniform2f(repeatHandle, mWidth/subTextureBounds.width(), mHeight/subTextureBounds.height());
		
		int frameSizeHandle = GLES20.glGetUniformLocation(mProgram, "uFrameSize");
		GLES20.glUniform2f(frameSizeHandle, subTextureBounds.width()/atlasWidth, subTextureBounds.height()/atlasHeight);
		
		super.render(gl, point, camera);
	}
}
