package net.androidpunk.utils;

import net.androidpunk.FP;

public class Input {

	/**
	 * If the mouse button is down.
	 */
	public static boolean mouseDown = false;

	/**
	 * If the mouse button is up.
	 */
	public static boolean mouseUp = true;

	/**
	 * If the mouse button was pressed this frame.
	 */
	public static boolean mousePressed = false;

	/**
	 * If the mouse button was released this frame.
	 */
	public static boolean mouseReleased = false;
	
	/**
	 * X positions of up to two fingers on the screen.
	 */
	public static int[] getMouseX() {
		return FP.screen.getTouchX();
	}

	/**
	 * Y position of up to two fingers on the screen.
	 */
	public static int[] getMouseY() {
		return FP.screen.getTouchY();
	}
	
	/** @private Called by Engine to update the input. */
	public static void update() {
		if (mousePressed) 
			mousePressed = false;
		if (mouseReleased) 
			mouseReleased = false;
	}
}
