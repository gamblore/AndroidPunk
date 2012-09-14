package net.androidpunk.graphics.opengl.shapes;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.graphics.opengl.GLGraphic;

import android.graphics.Point;

public class CircleShape extends Shape {
	
	private int mWidth = 0;
	private int mHeight = 0;
	
	public CircleShape() {
		
	}
	
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
		
		if (mVertices == 0) {
			return;
		}
		
		GLGraphic.setBuffers(gl, mVertexBuffer, null);
		
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glPushMatrix(); 
		{
			setMatrix(gl);
			gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, mVertices/2);
		}
		gl.glPopMatrix();
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}
}
