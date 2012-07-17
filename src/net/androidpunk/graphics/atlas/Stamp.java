package net.androidpunk.graphics.atlas;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.FP;
import net.androidpunk.android.OpenGLSystem;
import net.androidpunk.graphics.opengl.SubTexture;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

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
	}
	
	/** @private Renders the Graphic. */
	@Override 
	public void render(GL10 gl, Point point, Point camera) {
		mPoint.x = (int)(point.x + x - camera.x * scrollX);
		mPoint.y = (int)(point.y + y - camera.y * scrollY);
		
		setGeometryBuffer(QUAD_FLOAT_BUFFER_1, mPoint.x, mPoint.y, mSubTexture.getWidth(), mSubTexture.getHeight());
		setBuffers(gl, QUAD_FLOAT_BUFFER_1, mTextureBuffer);
		
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		
		unsetBuffers(gl);
	}
	
	/**
	 * Source BitmapData image.
	 */
	public void setSource(SubTexture value) {
		mSubTexture = value;
	}
}
