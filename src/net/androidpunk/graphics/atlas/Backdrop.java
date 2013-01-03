package net.androidpunk.graphics.atlas;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.FP;
import net.androidpunk.R;
import net.androidpunk.graphics.opengl.Shader;
import net.androidpunk.graphics.opengl.SubTexture;
import android.graphics.Point;
import android.graphics.Rect;
import android.opengl.GLES20;

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
		
		//mProgram = Shader.getProgram(R.raw.shader_g_repeating_texture, R.raw.shader_f_repeating_texture);
		useShaders(R.raw.shader_g_repeating_texture, R.raw.shader_f_repeating_texture);
		
		AtlasGraphic.setGeometryBuffer(mVertexBuffer, 0, 0, subTexture.getWidth(), subTexture.getHeight());
		AtlasGraphic.setTextureBuffer(mTextureBuffer, subTexture);
		
	}

	@Override
	public void render(GL10 gl, Point point, Point camera) {
		
		if (!getAtlas().isLoaded()) {
			return;
		}
		mPoint.x = (int)(point.x + x - camera.x * scrollX);
		mPoint.y = (int)(point.y + y - camera.y * scrollY);

		int xx = mPoint.x;
		int yy = mPoint.y;
		
		if (mRepeatX) {
			mPoint.x = 0;
		}
		if (mRepeatY) {
			mPoint.y = 0;
		}
		super.render(gl, point, camera);
		
		Rect subTextureBounds = mSubTexture.getBounds();
		
		float atlasWidth = mSubTexture.getTexture().getWidth();
		float atlasHeight = mSubTexture.getTexture().getHeight();
		
		int offsetHandle = GLES20.glGetUniformLocation(mProgram, "uOffset");
		
		
		int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "Position");
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
		
		int mTextureHandle = GLES20.glGetAttribLocation(mProgram, "TexCoord");
		GLES20.glEnableVertexAttribArray(mTextureHandle);
		GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
		
		int topLeftHandle = GLES20.glGetUniformLocation(mProgram, "uTopLeft");
		//Log.d(TAG, String.format("%.3f, %.3f", subTextureBounds.left / atlasWidth, subTextureBounds.top / atlasHeight));
		GLES20.glUniform2f(topLeftHandle, subTextureBounds.left / atlasWidth, subTextureBounds.top / atlasHeight);
		
		int repeatHandle = GLES20.glGetUniformLocation(mProgram, "uRepeat");
		if (mRepeatX && mRepeatY) {
			GLES20.glUniform2f(offsetHandle, (xx % mSubTexture.getWidth()) / atlasWidth, (yy % mSubTexture.getWidth()) / atlasHeight);
			GLES20.glUniform2f(repeatHandle, FP.width/subTextureBounds.width(), FP.height/subTextureBounds.height());
		} else if (mRepeatX && !mRepeatY) {
			GLES20.glUniform2f(offsetHandle, -(xx % mSubTexture.getWidth()) / atlasWidth, 0);
			GLES20.glUniform2f(repeatHandle, FP.width/subTextureBounds.width(), 1);
		} else if (!mRepeatX && mRepeatY) {
			GLES20.glUniform2f(offsetHandle, 0, (yy % mSubTexture.getWidth()) / atlasHeight);
			GLES20.glUniform2f(repeatHandle, 1, FP.height/subTextureBounds.height());
		} else {
			GLES20.glUniform2f(offsetHandle, 0, 0);
			GLES20.glUniform2f(repeatHandle, 1, 1);
		}
		
		int frameSizeHandle = GLES20.glGetUniformLocation(mProgram, "uFrameSize");
		GLES20.glUniform2f(frameSizeHandle, subTextureBounds.width()/atlasWidth, subTextureBounds.height()/atlasHeight);
		
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
		
		GLES20.glDisableVertexAttribArray(mPositionHandle);
		GLES20.glDisableVertexAttribArray(mTextureHandle);
		/*
		setBuffers(gl, mVertexBuffer, mTextureBuffer);
		
		gl.glPushMatrix(); 
		{
			//gl.glTranslatef(mPoint.x, mPoint.y, 0);
			setMatrix();
			
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
		*/
	}
}
