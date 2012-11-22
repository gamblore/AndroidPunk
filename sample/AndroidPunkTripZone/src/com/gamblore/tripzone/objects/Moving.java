package com.gamblore.tripzone.objects;

public class Moving extends Physics {

	public static final String[] CARRY = new String[] { TYPE_SOLID, Player.TYPE_PLAYER };
	public boolean direction;
	public int movement;
	
	public Moving(int x, int y, int movementSpeed, boolean moveRight) {
		super(x, y);
		direction = moveRight;
		movement = movementSpeed;
	}

	@Override
	public void update() {
		speed.x = direction ? movement : -movement;
		
		for (int i = 0; i < CARRY.length; i++) {
			moveontop(CARRY[i], speed.x);
		}
		
		motion();
		
		if (speed.x == 0) {
			switchDirection();
		}
	}
	
	public void switchDirection() {
		direction = !direction;
	}
	
	
}
