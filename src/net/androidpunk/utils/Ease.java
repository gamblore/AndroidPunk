package net.androidpunk.utils;

import net.androidpunk.Tween.OnEaseCallback;

public class Ease {

	
	// Easing constants.
		private static final float PI = (float) Math.PI;
		private static final float PI2 = (float) Math.PI / 2;
		private static final float EL = 2 * PI / .45f;
		private static final float B1 = 1 / 2.75f;
		private static final float B2 = 2 / 2.75f;
		private static final float B3 = 1.5f / 2.75f;
		private static final float B4 = 2.5f / 2.75f;
		private static final float B5 = 2.25f / 2.75f;
		private static final float B6 = 2.625f / 2.75f;
		
	public static final OnEaseCallback quadIn = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			return t*t;
		}
	};
	
	public static final OnEaseCallback quadOut = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			return -t *(t-2);
		}
	};
	
	public static final OnEaseCallback quadInOut = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			return t <= .5 ? t * t * 2 : 1 - (--t) * t * 2;
		}
	};
	
	public static final OnEaseCallback cubeIn = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			return t * t * t;
		}
	};
	
	public static final OnEaseCallback cubeOut = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			return 1 + (--t) * t * t;
		}
	};
	
	public static final OnEaseCallback cubeInOut = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			return t <= .5 ? t * t * t * 4 : 1 + (--t) * t * t * 4;
		}
	};
	
	public static final OnEaseCallback quartIn = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			return t * t * t * t;
		}
	};
	
	public static final OnEaseCallback quartOut = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			return 1 - (t-=1) * t * t * t;
		}
	};
	
	public static final OnEaseCallback quartInOut = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			return t <= .5 ? t * t * t * t * 8 : (1 - (t = t * 2 - 2) * t * t * t) / 2 + .5f;
		}
	};
	
	public static final OnEaseCallback quintIn = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			return t * t * t * t * t;
		}
	};
	
	public static final OnEaseCallback quintOut = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			return (t = t - 1) * t * t * t * t + 1;
		}
	};
	
	public static final OnEaseCallback quintInOut = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			return ((t *= 2) < 1) ? (t * t * t * t * t) / 2 : ((t -= 2) * t * t * t * t + 2) / 2;
		}
	};
	
	public static final OnEaseCallback sineIn = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			return (float)(-Math.cos(PI2 * t) + 1);
		}
	};
	
	public static final OnEaseCallback sineOut = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			return (float)Math.sin(PI2 * t);
		}
	};
	
	public static final OnEaseCallback sineInOut = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			return (float)(-Math.cos(PI * t) / 2 + .5f);
		}
	};
	
	public static final OnEaseCallback bounceIn = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			t = 1 - t;
			if (t < B1) return 1 - 7.5625f * t * t;
			if (t < B2) return 1 - (7.5625f * (t - B3) * (t - B3) + .75f);
			if (t < B4) return 1 - (7.5625f * (t - B5) * (t - B5) + .9375f);
			return 1 - (7.5625f * (t - B6) * (t - B6) + .984375f);
		}
	};
	
	public static final OnEaseCallback bounceOut = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			if (t < B1) return 7.5625f * t * t;
			if (t < B2) return 7.5625f * (t - B3) * (t - B3) + .75f;
			if (t < B4) return 7.5625f * (t - B5) * (t - B5) + .9375f;
			return 7.5625f * (t - B6) * (t - B6) + .984375f;
		}
	};
	
	public static final OnEaseCallback bounceInOut = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			if (t < .5f)
			{
				t = 1 - t * 2;
				if (t < B1) return (1 - 7.5625f * t * t) / 2;
				if (t < B2) return (1 - (7.5625f * (t - B3) * (t - B3) + .75f)) / 2;
				if (t < B4) return (1 - (7.5625f * (t - B5) * (t - B5) + .9375f)) / 2;
				return (1 - (7.5625f * (t - B6) * (t - B6) + .984375f)) / 2;
			}
			t = t * 2 - 1;
			if (t < B1) return (7.5625f * t * t) / 2 + .5f;
			if (t < B2) return (7.5625f * (t - B3) * (t - B3) + .75f) / 2 + .5f;
			if (t < B4) return (7.5625f * (t - B5) * (t - B5) + .9375f) / 2 + .5f;
			return (7.5625f * (t - B6) * (t - B6) + .984375f) / 2 + .5f;
		}
	};
	
	public static final OnEaseCallback circIn = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			return (float)(-(Math.sqrt(1 - t * t) - 1));
		}
	};
	
	public static final OnEaseCallback circOut = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			return (float)Math.sqrt(1 - (t - 1) * (t - 1));
		}
	};
	
	public static final OnEaseCallback circInOut = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			return (float)(t <= .5f ? (Math.sqrt(1 - t * t * 4) - 1) / -2 : (Math.sqrt(1 - (t * 2 - 2) * (t * 2 - 2)) + 1) / 2);
		}
	};
	
	public static final OnEaseCallback expoIn = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			return (float)Math.pow(2, 10 * (t - 1));
		}
	};
	
	public static final OnEaseCallback expoOut = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			return (float)-Math.pow(2, -10 * t) + 1;
		}
	};
	
	public static final OnEaseCallback expoInOut = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			return (float)(t < .5 ? Math.pow(2, 10 * (t * 2 - 1)) / 2 : (-Math.pow(2, -10 * (t * 2 - 1)) + 2) / 2);
		}
	};
	
	public static final OnEaseCallback backIn = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			return t * t * (2.70158f * t - 1.70158f);
		}
	};
	
	public static final OnEaseCallback backOut = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			return 1 - (--t) * (t) * (-2.70158f * t - 1.70158f);
		}
	};
	
	public static final OnEaseCallback backInOut = new OnEaseCallback() {
		@Override
		public float ease(float t) {
			t *= 2;
			if (t < 1) 
				return t * t * (2.70158f * t - 1.70158f) / 2;
			t--;
			return (1 - (--t) * (t) * (-2.70158f * t - 1.70158f)) / 2 + .5f;
		}
	};
}
