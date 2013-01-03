package net.androidpunk.graphics.atlas;

import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.FP;
import net.androidpunk.R;
import net.androidpunk.android.OpenGLSystem;
import net.androidpunk.graphics.opengl.Shader;
import net.androidpunk.graphics.opengl.SubTexture;
import android.graphics.Point;
import android.graphics.Rect;
import android.opengl.GLES20;

public class TiledSpriteMap extends SpriteMap {

	private static final String TAG = "TiledSpriteMap";
	
	private int mImageWidth;
	private int mImageHeight;
	private int mOffsetX = 0;
	private int mOffsetY = 0;
			
	/**
	 * Constructs the tiled spritemap.
	 * @param	source			Source image.
	 */
	public TiledSpriteMap(SubTexture source) {
		this(source, 0, 0, 0, 0, null);
	}
	
	/**
	 * Constructs the tiled spritemap.
	 * @param	source			Source image.
	 * @param	frameWidth		Frame width.
	 * @param	frameHeight		Frame height.
	 */
	public TiledSpriteMap(SubTexture source, int frameWidth, int frameHeight) {
		this(source, frameWidth, frameHeight, 0, 0, null);
	}
	
	/**
	 * Constructs the tiled spritemap.
	 * @param	source			Source image.
	 * @param	frameWidth		Frame width.
	 * @param	frameHeight		Frame height.	
	 * @param	width			Width of the block to render.
	 * @param	height			Height of the block to render.
	 */
	public TiledSpriteMap(SubTexture source, int frameWidth, int frameHeight, int width, int height) {
		this(source, frameWidth, frameHeight, width, height, null);
	}
	
	/**
	 * Constructs the tiled spritemap.
	 * @param	source			Source image.
	 * @param	frameWidth		Frame width.
	 * @param	frameHeight		Frame height.	
	 * @param	width			Width of the block to render.
	 * @param	height			Height of the block to render.
	 * @param	callback		callback function for animation end.
	 */
	public TiledSpriteMap(SubTexture source, int frameWidth, int frameHeight, int width, int height, OnAnimationEndCallback callback) {
		super(source, frameWidth, frameHeight, callback);
		mImageWidth = width;
		mImageHeight = height;
		
		//mProgram = Shader.getProgram(R.raw.shader_g_repeating_texture, R.raw.shader_f_repeating_texture);
		useShaders(R.raw.shader_g_repeating_texture, R.raw.shader_f_repeating_texture);
		
		AtlasGraphic.setGeometryBuffer(mVertexBuffer, 0, 0, mImageWidth, mImageHeight);
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
		
		Rect subTextureBounds = FP.rect;
		mSubTexture.getFrame(subTextureBounds, mFrame, mFrameWidth, mFrameHeight);
		
		//int atlasSizeHandle = GLES20.glGetUniformLocation(mProgram, "uAtlasSize");
		//GLES20.glUniform2f(atlasSizeHandle, getAtlas().getWidth(), getAtlas().getHeight());
		float atlasWidth = mSubTexture.getTexture().getWidth();
		float atlasHeight = mSubTexture.getTexture().getHeight();
		
		int topLeftHandle = GLES20.glGetUniformLocation(mProgram, "uTopLeft");
		//Log.d(TAG, String.format("%.3f, %.3f", subTextureBounds.left / atlasWidth, subTextureBounds.top / atlasHeight));
		GLES20.glUniform2f(topLeftHandle, subTextureBounds.left / atlasWidth, subTextureBounds.top / atlasHeight);
		
		int repeatHandle = GLES20.glGetUniformLocation(mProgram, "uRepeat");
		int repeatX = mImageWidth/subTextureBounds.width();
		int repeatY = mImageHeight/subTextureBounds.height();
		GLES20.glUniform2f(repeatHandle, repeatX, repeatY);
		
		int frameSizeHandle = GLES20.glGetUniformLocation(mProgram, "uFrameSize");
		GLES20.glUniform2f(frameSizeHandle, subTextureBounds.width()/atlasWidth, subTextureBounds.height()/atlasHeight);
		
		super.render(gl, point, camera);
	}
	

}
