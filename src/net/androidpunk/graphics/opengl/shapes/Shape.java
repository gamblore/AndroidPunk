package net.androidpunk.graphics.opengl.shapes;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.FP;
import net.androidpunk.graphics.opengl.GLGraphic;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.util.Log;

public class Shape extends GLGraphic {
	
	private static final String TAG = "Shape";
	
	protected FloatBuffer mVertexBuffer;
	protected FloatBuffer mVertexColorBuffer;
	protected int mVertices = 0;
	
	private int mWidth = 0;
	private int mHeight = 0;
	
	protected Shape() {
		super();
	}
	
	protected static void setRect(float x, float y, float w, float h, float v[]) {
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
	 * Create a shape based on the three Point objects given.
	 * @param points
	 * @return A shape Graphic
	 */
	public static Shape triangle(Point[] points) {
		if (points.length < 3 * 2) {
			Log.e(TAG, "Not enough points for triangle");
			return null;
		}
		
		int idx = 0;
		float[] verticies = new float[3*2];
		for (int i = 0; i < 3; i++) {
			verticies[idx++] = points[i].x;
			verticies[idx++] = points[i].y;
		}
		
		return triangle(verticies);
	}
	
	/**
	 * Create a shape based on the three Point given.
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param x3
	 * @param y3
	 * @return A shape Graphic
	 */
	public static Shape triangle(float x1, float y1, float x2, float y2, float x3, float y3) {
		int idx = 0;
		float[] verticies = new float[3*2];
		
		verticies[idx++] = x1;
		verticies[idx++] = y1;
		
		verticies[idx++] = x2;
		verticies[idx++] = y2;
		
		verticies[idx++] = x3;
		verticies[idx++] = y3;
		
		return triangle(verticies);
	}
	
	/**
	 * Create a shape based on the vertices given.
	 * @param verticies
	 * @return A shape Graphic
	 */
	public static Shape triangle(float[] verticies) {
		Shape s = new Shape();
		
		s.setVertices(verticies);
		
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
	 * Sets the color of the vertices. Float format 4 per vertex.
	 * @param v
	 */
	public void setColorVertices(float v[]) {
		mVertexColorBuffer = GLGraphic.getDirectFloatBuffer(v.length);
		mVertexColorBuffer.put(v).position(0);
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
	 * Get the number of vertices in the buffer.
	 * @return 
	 */
	public int getVertexCount() {
		return mVertices / 2;
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

		if (mProgram < 0) {
			return;
		}
		
		if (mVertices == 0) {
			return;
		}
		int mColorHandle = GLES20.glGetAttribLocation(mProgram, "Color");
		if (mVertexColorBuffer != null) {
			GLES20.glEnableVertexAttribArray(mColorHandle);
			GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 0, mVertexColorBuffer);
		}
		
		int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "Position");
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
		
		int mUseTexture = GLES20.glGetUniformLocation(mProgram, "uHasTextureAttribute");
		GLES20.glUniform1i(mUseTexture, 0);
		
		
		//GLES20.glPushMatrix(); 
		{
			setMatrix();
			GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, mVertices/2);
		}
		//GLES20.glPopMatrix();
		
		if (mVertexColorBuffer != null) {
			GLES20.glDisableVertexAttribArray(mColorHandle);
		}
		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}
}
