package com.gamblore.tripzone.objects.tripzone;

import net.androidpunk.FP;
import net.androidpunk.graphics.atlas.SpriteMap;
import net.androidpunk.graphics.opengl.SubTexture;

import com.gamblore.tripzone.Main;
import com.gamblore.tripzone.objects.Player;

public class TripGuy extends Player {

	public TripGuy(int x, int y) {
		super(x, y);
		
		setLayer(2);
	}

	@Override
	public void setupGraphic() {
		SubTexture guy = Main.mAtlas.getSubTexture("Guy");
		SpriteMap sm = new SpriteMap(guy, guy.getWidth()/4, guy.getHeight());
		sm.add(ANIM_STAND, FP.frames(0, 0));
		sm.add(ANIM_WALK, FP.frames(0, 3), 10);
		sm.add(ANIM_JUMP, FP.frames(2, 2));
		sm.add(ANIM_SLIDE, FP.frames(1, 1));
		
		sm.play(ANIM_STAND);
		
		setGraphic(sm);
		setHitbox(5, guy.getHeight(), -4, -1);
	}

}
