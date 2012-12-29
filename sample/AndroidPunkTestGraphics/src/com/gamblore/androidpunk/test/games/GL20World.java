package com.gamblore.androidpunk.test.games;

import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.World;
import net.androidpunk.graphics.atlas.Image;
import net.androidpunk.graphics.atlas.SpriteMap;
import net.androidpunk.graphics.opengl.SubTexture;
import net.androidpunk.graphics.opengl.shapes.Shape;
import android.graphics.Point;
import android.opengl.GLES20;

public class GL20World extends World {

	public class VignetteFilter extends Shape {
		public VignetteFilter() {
			useShaders(R.raw.shader_g_flat, R.raw.vignette_fragment_shader);
			
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
		bg.setColor(0xffF2F5A9);
		
		add(new Entity(0, 0, bg));
		
		add(new Entity(250, 250, rect));		
		
		Entity filter = new Entity(0, 0, new VignetteFilter());
		filter.setLayer(-1000);
		add(filter);
		
		add(new Entity(300, 250, new Image(Main.getAtlas().getSubTexture("menu_newgame"))));
		
		SubTexture ogmoSt = Main.getAtlas().getSubTexture("ogmo");
		SpriteMap sm = new SpriteMap(ogmoSt, ogmoSt.getWidth()/6, ogmoSt.getHeight());
		sm.add("run", FP.frames(0,5), 10);
		sm.play("run");
		
		add(new Entity(300, 218, sm));
	}

	
	
}


