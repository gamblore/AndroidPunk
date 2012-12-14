package net.androidpunk.graphics.opengl.shapes;

import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.graphics.opengl.GLGraphic;
import android.graphics.Point;
import android.opengl.GLES20;

public class CircleShape extends Shape {
	
	private int mWidth = 0;
	private int mHeight = 0;
		
	// http://slabode.exofire.net/circle_draw.shtml
	private static void setCircle(float cx, float cy, float r, float vertices[]) {
		
		final int sections = (vertices.length)/2 - 2; // One for the center and one for the last point.
		
		double theta = Math.PI * 2 / (float)(sections); 
		
		float tangetial_factor = (float) Math.tan(theta);//calculate the tangential factor 
		float radial_factor = (float) Math.cos(theta);//calculate the radial factor 
		
		float x = r;//we start at angle = 0 
		float y = 0; 
	    
		int idx = 0;
		
		vertices[idx++] = cx;
		vertices[idx++] = cy;
		
		for(int i = 0; i < sections; i++) { 
			vertices[idx++] = x + cx;
			vertices[idx++] = y + cy;
	        
			//calculate the tangential vector 
			//remember, the radial vector is (x, y) 
			//to get the tangential vector we flip those coordinates and negate one of them 

			float tx = -y; 
			float ty = x; 
	        
			//add the tangential vector 

			x += tx * tangetial_factor; 
			y += ty * tangetial_factor; 
	        
			//correct using the radial factor 

			x *= radial_factor; 
			y *= radial_factor; 
		}  
		vertices[idx++] = vertices[2];
		vertices[idx++] = vertices[3];
	}
	
	public static Shape circle(int cx, int cy, float radius) {
		CircleShape cs = new CircleShape();
		int sections = (int) ((2*radius*Math.PI)/5);
		
		sections = Math.max(sections, 30);
		
		float v[] = new float[sections*2 + 4];
		
		setCircle(cx, cy, radius, v);
		
		cs.setVertices(v);
		
		return cs;
	}
	
	@Override
	public void render(GL10 gl, Point point, Point camera) {
		superRender(gl, point, camera);
		
		if (mProgram == -1) {
			return;
		}
		
		if (mVertices == 0) {
			return;
		}
		
		int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "Position");
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
		
		int mUseTexture = GLES20.glGetUniformLocation(mProgram, "uHasTextureAttribute");
		GLES20.glUniform1i(mUseTexture, 0);
		
		{
			setMatrix();
			GLES20.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, mVertices/2);
		}
		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}
}
