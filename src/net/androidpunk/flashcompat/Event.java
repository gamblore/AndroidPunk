package net.androidpunk.flashcompat;

public class Event {
	public static abstract class OnEventListener {
		public int type;
		public OnEventListener(int eventType) {
			this.type = eventType;
		}
		public abstract void event();
	}
	
	public static final int ADDED_TO_STAGE = 1;
	public static final int ENTER_FRAME = 2;
	public static final int TIMER = 3;
}
