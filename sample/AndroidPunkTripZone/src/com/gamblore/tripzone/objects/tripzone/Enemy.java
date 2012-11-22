package com.gamblore.tripzone.objects.tripzone;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.Positionable;
import net.androidpunk.graphics.atlas.SpriteMap;
import net.androidpunk.graphics.opengl.SubTexture;
import net.androidpunk.utils.TaskTimer;
import android.util.Log;

import com.gamblore.tripzone.Main;

public class Enemy extends Entity {
	public static final String TYPE_ENEMY = "Enemy";
	
	private TaskTimer mTimer;
	
	private Entity mTimerTarget;
	
	public Enemy(int x, int y) {
		super(x, y);
		
		SubTexture enemy = Main.mAtlas.getSubTexture("EnemyBall");
		SpriteMap sm = new SpriteMap(enemy, enemy.getWidth()/2, enemy.getHeight());
		sm.add("bubble", FP.frames(0, 1), 15);
		
		sm.play("bubble");
		
		setGraphic(sm);
		
		setLayer(3);
		setHitbox(enemy.getWidth()/2, enemy.getHeight());
		
		setType(TYPE_ENEMY);
		mTimerTarget = this;
		mTimer = new TaskTimer(1.5f, new TaskTimer.OnTimeup() {
			@Override
			public void run() {
				Log.d("Enemy", "Updating position to " + Main.player.toString());
				
				FP.stepTowards(mTimerTarget, Main.player.x, Main.player.y, 25);
			}
		});
	}

	@Override
	public void update() {
		if (collideWith(Main.player, x, y) != null) {
			Main.player.killme();
		}
		mTimer.step(FP.elapsed);
		
	}
	
	
}
