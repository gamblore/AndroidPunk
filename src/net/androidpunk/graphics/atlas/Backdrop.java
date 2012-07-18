package net.androidpunk.graphics.atlas;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Point;
import net.androidpunk.FP;
import net.androidpunk.android.OpenGLSystem;
import net.androidpunk.graphics.opengl.SubTexture;

public class Backdrop extends AtlasGraphic {

	private boolean mRepeatX, mRepeatY;
	
	private FloatBuffer mVertexBuffer = AtlasGraphic.getDirectFloatBuffer(8);
	private FloatBuffer mTextureBuffer = AtlasGraphic.getDirectFloatBuffer(8);
	
	/**
	 * Constructor. Repeats horizontally and vertically.
	 * @param	texture		Source texture.
	 */
	public Backdrop(SubTexture subTexture)  {
		this(subTexture, true, true);
	}
	
	public Backdrop(SubTexture subTexture, boolean repeatX, boolean repeatY) {
		super(subTexture);
		mRepeatX = repeatX;
		mRepeatY = repeatY;
		
		AtlasGraphic.setGeometryBuffer(mVertexBuffer, 0, 0, subTexture.getWidth(), subTexture.getHeight());
		AtlasGraphic.setTextureBuffer(mTextureBuffer, subTexture);
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
			
			while (yy  < FP.screen.getHeight()) {
				while (xx < FP.screen.getWidth()) {
					gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
					
					if (!mRepeatX) {
						//TERMINATE
						xx = FP.screen.getWidth();
					} else {
						xx += mSubTexture.getWidth();
						gl.glTranslatef(mSubTexture.getWidth(), 0, 0);
					}
				}
				if (!mRepeatY) {
					//TERMINATE
					yy = FP.screen.getHeight();
				} else {
					yy += mSubTexture.getHeight();
					gl.glTranslatef(mPoint.x - xx, mSubTexture.getHeight(), 0);
					xx = mPoint.x;
				}
			}
		}
		gl.glPopMatrix();
		
		unsetBuffers(gl);
	}
}
