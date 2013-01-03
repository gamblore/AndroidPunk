package net.androidpunk.graphics.atlas;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.graphics.opengl.SubTexture;
import android.graphics.Point;
import android.opengl.GLES20;

/**
 * A simple non-transformed, non-animated graphic.
 */
public class Stamp extends AtlasGraphic {

	// Stamp information.
	
	private FloatBuffer mTextureBuffer = getDirectFloatBuffer(8);

	public Stamp(SubTexture subTexture) {
		this(subTexture, 0, 0);
	}
	
	/**
	 * Constructor.
	 * @param	subTexture		Source image.
	 * @param	x			X offset.
	 * @param	y			Y offset.
	 */
	public Stamp(SubTexture subTexture, int x, int y) {
		super(subTexture);
		// set the origin
		this.x = x;
		this.y = y;

		AtlasGraphic.setTextureBuffer(mTextureBuffer, getAtlas(), subTexture.getBounds());
		
		originX = subTexture.getBounds().width()/2;
		originY = subTexture.getBounds().height()/2;
	}
	
	/** @private Renders the Graphic. */
	@Override 
	public void render(GL10 gl, Point point, Point camera) {
		super.render(gl, point, camera);
		if (!getAtlas().isLoaded()) {
			return;
		}
		
		setGeometryBuffer(QUAD_FLOAT_BUFFER_1, mPoint.x, mPoint.y, mSubTexture.getWidth(), mSubTexture.getHeight());
		
		//int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "Position");
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 0, QUAD_FLOAT_BUFFER_1);
		
		//int mTextureHandle = GLES20.glGetAttribLocation(mProgram, "TexCoord");
		GLES20.glEnableVertexAttribArray(mTextureHandle);
		GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
		
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
		
		GLES20.glDisableVertexAttribArray(mPositionHandle);
		GLES20.glDisableVertexAttribArray(mTextureHandle);
		
	}
	
	/**
	 * Source BitmapData image.
	 */
	public void setSource(SubTexture value) {
		mSubTexture = value;
	}
}
