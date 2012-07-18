package net.androidpunk.graphics.atlas;

import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.FP;
import net.androidpunk.graphics.opengl.SubTexture;
import android.graphics.Point;
import android.graphics.Rect;

public class TiledImage extends Image {

	public static final String TAG = "Image";
	
	private int mWidth;
	private int mHeight;
	
	private int mOffsetX = 0;
	private int mOffsetY = 0;
	
	public TiledImage(SubTexture subTexture, int width, int height) {
		this(subTexture, width, height, null);
	}
	
	public TiledImage(SubTexture subTexture, int width, int height, Rect clipRect) {
		super(subTexture, clipRect);
		
		mWidth = width;
		mHeight = height;
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
		super.render(gl, point, camera);
		if (!getAtlas().isLoaded()) {
			return;
		}
		mPoint.x = (int)(point.x + x - camera.x * scrollX);
		mPoint.y = (int)(point.y + y - camera.y * scrollY);

		int xx = mPoint.x;
		int yy = mPoint.y;
		
		setBuffers(gl, mVertexBuffer, mTextureBuffer);
		
		gl.glPushMatrix(); 
		{
			gl.glTranslatef(mPoint.x, mPoint.y, 0);
			
			while (yy  < mPoint.y + mHeight) {
				while (xx < mPoint.x + mWidth) {
					gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
					
					xx += mClipRect.width();
					gl.glTranslatef(mClipRect.width(), 0, 0);
					
				}
				
				yy += mClipRect.height();
				gl.glTranslatef(mPoint.x - xx, mClipRect.height(), 0);
				xx = mPoint.x;
			}
		}
		gl.glPopMatrix();
	}

}
