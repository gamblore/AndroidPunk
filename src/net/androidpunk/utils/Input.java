package net.androidpunk.utils;

import java.util.HashMap;
import java.util.Map;

import net.androidpunk.FP;
import android.graphics.Point;
import android.view.KeyEvent;

public class Input {

	/**
	 * Map from KeyCode to Action (ACTION_DOWN, ACTION_UP)
	 */
	public static final Map<Integer, Integer> KEY_STATE = new HashMap<Integer, Integer>();
	
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
	
	/**
	 * Gets the number of pointers on the screen.
	 * @return The number of points
	 */
	public static int getTouchesCount() {
		return FP.screen.getTouchesCount();
	}
	
	/**
	 * Gets the points of contact with the screen.
	 * @return An array of points use getTouchesCount() to find the size;
	 */
	public static Point[] getTouches() {
		return FP.screen.getTouches();
	}
	
	/**
	 * Store the state of the inputs.
	 * @param keyCode
	 * @param event
	 */
	public static void onKeyChange(int keyCode, KeyEvent event) {
		switch(event.getAction()) {
		case KeyEvent.ACTION_DOWN:
			KEY_STATE.put(keyCode, 1);
			break;
		case KeyEvent.ACTION_UP:
			KEY_STATE.put(keyCode, 0);
			break;
		case KeyEvent.ACTION_MULTIPLE:
			String keys = event.getCharacters();
			for (int i = 0; i < keys.length(); i++) {
				KEY_STATE.put((int)keys.charAt(i), 1);
			}
			break;
		}
	}
	
	public static boolean checkKey(int keyCode) {
		return KEY_STATE.containsKey(keyCode) && KEY_STATE.get(keyCode) == 1;
	}
	
	/** @private Called by Engine to update the input. */
	public static void update() {
		if (mousePressed) 
			mousePressed = false;
		if (mouseReleased) 
			mouseReleased = false;
	}
}
