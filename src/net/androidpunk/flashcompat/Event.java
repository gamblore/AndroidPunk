package net.androidpunk.flashcompat;

public class Event {
	public static abstract class OnEventListener {
		public int type;
		public OnEventListener(int eventType) {
			this.type = eventType;
		}
		public abstract void event();
	}
	
	/**
	 * When the surface is ready to draw.
	 */
	public static final int ADDED_TO_STAGE = 1;
	/**
	 * Screen has refreshed.
	 */
	public static final int ENTER_FRAME = 2;
	/**
	 * When a timer fires.
	 */
	public static final int TIMER = 4;
}
