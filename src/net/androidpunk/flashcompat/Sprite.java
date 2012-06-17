package net.androidpunk.flashcompat;

import android.graphics.Canvas;
import android.graphics.Matrix;

public class Sprite {

	public static class Transform {
		public Matrix matrix;
	}
	//public final Canvas graphics = new Canvas();
	public Transform transform = new Transform();
}
