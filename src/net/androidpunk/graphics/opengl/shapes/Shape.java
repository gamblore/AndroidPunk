package net.androidpunk.graphics.opengl.shapes;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.FP;
import net.androidpunk.graphics.opengl.GLGraphic;

import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;

public class Shape extends GLGraphic {

	protected FloatBuffer mVertexBuffer;
	protected FloatBuffer mVertexColorBuffer;
	protected int mVertices = 0;
	
	private int mWidth = 0;
	private int mHeight = 0;
	
	public Shape() {
		
	}
	
	private static void setRect(float x, float y, float w, float h, float v[]) {
		v[0] = x;
		v[1] = y;
		
		v[2] = x+w;
		v[3] = y;
		
		v[4] = x;
		v[5] = y+h;
		
		v[6] = x+w;
		v[7] = y+h;
	}
	
	/**
	 * get a graphic that represents a rectangle. You can tranform it afterwords with angle variable.
	 * @param p the top left cornor.
	 * @param width width in pixels
	 * @param height height in pixels
	 * @return a shape Graphic
	 */
	public static Shape rect(Point p, float width, float height) {
		return rect(p.x, p.y, width, height);
	}
	
	/**
	 * get a graphic that represents a rectangle. You can tranform it afterwords with angle variable.
	 * @param x left
	 * @param y top
	 * @param width width in pixels
	 * @param height height in pixels
	 * @return a shape Graphic
	 */
	public static Shape rect(int x, int y, float width, float height) {
		Shape s = new Shape();
		float v[] = new float[8];
		
		setRect(x, y, width, height, v);
		
		s.setVertices(v);
		
		return s;
	}
	
	/**
	 * Returns a line graphic of length
	 * @param length
	 * @return A shape Graphic
	 */
	public static Shape line(float length) {
		return line(length, 1.0f);
	}
	
	/**
	 * Returns a line graphic of length with a thickness
	 * @param length
	 * @param width
	 * @return A shape Graphic
	 */
	public static Shape line(float length, float width) {
		return line(0, 0, (int)length, 0, width);
	}
	
	/**
	 * A line from point to point with a thickness
	 * @param p1
	 * @param p2
	 * @param width
	 * @return A shape Graphic
	 */
	public static Shape line(Point p1, Point p2, float width) {
		return line(p1.x, p1.y, p2.x, p2.y, width);
	}
	
	/**
	 * A line from point to point with a thickness
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param width
	 * @return A shape Graphic
	 */
	public static Shape line(int x1, int y1, int x2, int y2, float width) {
		Shape s = new Shape();
		float v[] = new float[8];
		
		int xdiff = x2 - x1;
		int ydiff = y2 - y1;
		
		float length = PointF.length(xdiff, ydiff);
		
		setRect(0, -width/2, length, width, v);
		
		Matrix m = new Matrix();
		
		m.postRotate(-(float)(FP.DEG * Math.atan2(ydiff, xdiff)));
		m.postTranslate(x1, y1);
		
		m.mapPoints(v);
		
		s.setVertices(v);
		return s;
	}
	
	public static Shape circle(int cx, int cy, float radius) {
		return CircleShape.circle(cx, cy, radius);
	}
	
	/**
	 * Set custom vertices will be set as a triangle strip. You can override this method to do more other rendering types.
	 * @param v vertices to set into the native buffer for drawing.
	 */
	public void setVertices(float v[]) {
		float minX, maxX, minY, maxY;
		
		minX = maxX = v[0];
		minY = maxY = v[1];
		
		for (int i = 1; i < v.length/2; i ++) {
			minX = Math.min(minX, v[i*2]);
			minX = Math.max(maxX, v[i*2]);
			
			minY = Math.min(minY, v[i*2+1]);
			maxY = Math.max(maxY, v[i*2+1]);
		}
		
		mVertexBuffer = GLGraphic.getDirectFloatBuffer(v.length);
		mVertexBuffer.put(v);
		mVertexBuffer.position(0);
		mVertices = v.length;
		
		mWidth = (int)(maxX - minX);
		mHeight = (int)(maxY - minY);
	}
	
	/**
	 * Get the width of the vertices put in.
	 * @return
	 */
	public int getWidth() {
		return mWidth;
	}
	
	/**
	 * Get the height of the vertices put in.
	 * @return
	 */
	public int getHeight() {
		return mHeight;
	}
	
	protected void superRender(GL10 gl, Point point, Point camera) {
		super.render(gl, point, camera);
	}
	
	@Override
	public void render(GL10 gl, Point point, Point camera) {
		super.render(gl, point, camera);

		if (mVertices == 0) {
			return;
		}
		
		if (mVertexColorBuffer != null) {
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, mVertexColorBuffer);

		}
		GLGraphic.setBuffers(gl, mVertexBuffer, null);
		
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glPushMatrix(); 
		{
			setMatrix(gl);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, mVertices/2);
		}
		gl.glPopMatrix();
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		if (mVertexColorBuffer != null) {
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		}
	}
}
