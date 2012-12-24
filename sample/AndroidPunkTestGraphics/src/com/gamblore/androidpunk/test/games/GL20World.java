package com.gamblore.androidpunk.test.games;

import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.World;
import net.androidpunk.graphics.opengl.shapes.Shape;
import android.graphics.Point;
import android.opengl.GLES20;

public class GL20World extends World {

	public class VignetteFilter extends Shape {
		public VignetteFilter() {
			useShaders(R.raw.default_geometry_shader, R.raw.vignette_fragment_shader);
			
			float v[] = new float[8];
			
			setRect(0, 0, FP.width, FP.height, v);
			
			setVertices(v);
			
		}

		@Override
		public void render(GL10 gl, Point point, Point camera) {
			GLES20.glUseProgram(mProgram);
			GLES20.glEnable(GLES20.GL_BLEND);
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
			int resolutionHandler = GLES20.glGetUniformLocation(mProgram, "resolution");
			GLES20.glUniform2f(resolutionHandler, FP.displayWidth, FP.displayHeight);
			
			super.render(gl, point, camera);
		}
		
		
	}
	@Override
	public void begin() {
		Shape rect = Shape.rect(0,0,50,50);
		
		rect.setColor(0xffffffff);
		
		Shape bg = Shape.rect(0, 0, FP.width, FP.height);
		bg.setColor(0xffff0000);
		
		add(new Entity(0, 0, bg));
		
		add(new Entity(250, 250, rect));		

		add(new Entity(0, 0, new VignetteFilter()));
	}

	
	
}


