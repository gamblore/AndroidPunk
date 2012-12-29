package net.androidpunk.graphics.atlas;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.graphics.opengl.SubTexture;
import android.graphics.Point;
import android.graphics.Rect;
import android.opengl.GLES20;

public class Image extends AtlasGraphic {

	protected Rect mClipRect = new Rect();
	
	protected FloatBuffer mVertexBuffer = AtlasGraphic.getDirectFloatBuffer(8);
	protected FloatBuffer mTextureBuffer = AtlasGraphic.getDirectFloatBuffer(8);
	
	public Image(SubTexture subTexture) {
		this(subTexture, null);
	}
	
	public Image(SubTexture subTexture, Rect clipRect) {
		super(subTexture);
		
		if (clipRect != null){
			
			if (clipRect.width() == 0)
				clipRect.right = subTexture.getWidth();
			if (clipRect.height() == 0)
				clipRect.bottom = subTexture.getHeight();
			subTexture.GetAbsoluteClipRect(mClipRect, clipRect);
		} else {
			mClipRect.set(subTexture.getBounds());
		}
		
		AtlasGraphic.setGeometryBuffer(mVertexBuffer, 0, 0, mClipRect.width(), mClipRect.height());
		AtlasGraphic.setTextureBuffer(mTextureBuffer, subTexture.getTexture(), mClipRect);
		
		originX = mClipRect.width()/2;
		originY = mClipRect.height()/2;
	}

	@Override
	public void render(GL10 gl, Point point, Point camera) {
		super.render(gl, point, camera);
		if (!getAtlas().isLoaded()) {
			return;
		}
		//mPoint.x = (int)(point.x + x - camera.x * scrollX);
		//mPoint.y = (int)(point.y + y - camera.y * scrollY);
		
		int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "Position");
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
		
		int mTextureHandle = GLES20.glGetAttribLocation(mProgram, "TexCoord");
		GLES20.glEnableVertexAttribArray(mTextureHandle);
		GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
		
		
		//setBuffers(gl, mVertexBuffer, mTextureBuffer);
		
		//gl.glPushMatrix(); 
		{
			setMatrix();
			GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
		}
		//gl.glPopMatrix();
		
		GLES20.glDisableVertexAttribArray(mPositionHandle);
		GLES20.glDisableVertexAttribArray(mTextureHandle);
	}

	@Override
	public int getWidth() {
		return mClipRect.width();
	}

	@Override
	public int getHeight() {
		return mClipRect.height();
	}
	
	
}
