package net.androidpunk;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.androidpunk.Tween.OnCompleteCallback;
import net.androidpunk.Tween.OnEaseCallback;
import net.androidpunk.debug.Console;
import net.androidpunk.flashcompat.SoundTransform;
import net.androidpunk.flashcompat.Sprite;
import net.androidpunk.tweens.misc.MultiVarTween;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

public class FP {

	private static final String TAG = "FP";
	
    /**
     * The FlashPunk major version.
     */
    public static final String VERSION = "1.6";
    
    /**
     * Width of the game.
     */
    public static int width;
    
    /**
     * Height of the game.
     */
    public static int height;
    
    /**
     * Half width of the game.
     */
    public static float halfWidth;
    
    /**
     * Half height of the game.
     */
    public static float halfHeight;
    
    /**
     * If the game is running at a fixed framerate.
     */
    public static boolean fixed;
    
    /**
     * If times should be given in frames (as opposed to seconds).
     * Default is true in fixed timestep mode and false in variable timestep mode.
     */
    public static boolean timeInFrames;
    
    /**
     * The framerate assigned to the stage.
     */
    public static float frameRate;
    
    /**
     * The framerate assigned to the stage.
     */
    public static float assignedFrameRate;
    
    /**
     * Time elapsed since the last frame (in seconds).
     */
    public static float elapsed;
    
    /**
     * Timescale applied to FP.elapsed.
     */
    public static float rate = 1;
    
    /**
     * The Screen object, use to transform or offset the Screen.
     */
    public static Screen screen;
    
    
    /**
     * The current screen buffer, drawn to in the render loop.
     */
    public static Bitmap buffer;
    
    /**
     * A rectangle representing the size of the screen.
     */
    public static Rect bounds;
    
    /**
     * Point used to determine drawing offset in the render loop.
     */
    public static Point camera;
    
    /**
     * Global Tweener for tweening values across multiple worlds.
     */
    public static Tweener tweener = new Tweener();
    
    /**
     * If the game currently has input focus or not. Note: may not be correct initially.
     */
    public static boolean focused = true;
    
    private static final float HSV[] = new float[3];
    
    // World information.
    protected static World mWorld;
    protected static World mGoto;

    // Console information.
    protected static Console mConsole;

    // Time information.
    protected static long mTime;
    public static long updateTime;
    public static long renderTime;
    public static long gameTime;
    public static long javaTime;

    // Pseudo-random number generation (the seed is set in Engine's constructor).
    private static long mSeed;
    private static long mGetSeed;

    // Volume control.
    private static float mVolume = 1.0f;
    private static float mPan = 0.0f;
    private static SoundTransform mSoundTransform = new SoundTransform();

    // Used for rad-to-deg and deg-to-rad conversion.
    public static final float DEG = (float)(-180.0 / Math.PI);
    public static final float RAD = (float)(Math.PI / -180.0);

    // Global Flash objects.
    public static Engine engine;

    // Global objects used for rendering, collision, etc.
    public static final  Point point = new Point();
    public static final Point point2 = new Point();
    public static final Point zero = new Point();
    public static final Rect rect = new Rect();
    public static final Matrix matrix = new Matrix();
    public static Sprite sprite = new Sprite();
    public static final Paint paint = new Paint();
    public static final Canvas canvas = new Canvas();
    public static Entity entity;
    
    public static Resources resources;
        
    public static final float MATRIX_VALUES[] = new float[9];
    
    /**
     * Resize the screen.
     * @param width     New width.
     * @param height    New height.
     */
    public static void resize(int width, int height) {
        FP.width = width;
        FP.height = height;
        FP.halfWidth = width/2;
        FP.halfHeight = height/2;
        FP.bounds.right = width;
        FP.bounds.bottom = height;
        FP.screen.resize();
    }
    
    public static World getWorld() {
        return mWorld;
    }
    
    /**
     * The currently active World object. When you set this, the World is flagged
     * to switch, but won't actually do so until the end of the current frame.
     */
    public static void setWorld(World world) {
        if (mWorld != null && mWorld.equals(world))
            return;
        mGoto = world;
    }
    
    /**
     * Sets the camera position.
     * @param   x   X position.
     * @param   y   Y position.
     */
    public static void setCamera(int x, int y) {
        camera.x = x;
        camera.y = y;
    }
    
    /**
     * Resets the camera position.
     */
    public static void resetCamera() {
        camera.x = camera.y = 0;
    }
    
    /**
     * Global volume factor for all sounds, a value from 0 to 1.
     */
    public static void setVolume(float value)
    {
        if (value < 0) 
            value = 0;
        if (mVolume == value) 
            return;
        mSoundTransform.volume = mVolume = value;
    }
    
    /**
     * Global panning factor for all sounds, a value from -1 to 1.
     */
    public static float getPan() {
        return mPan; 
    }
    
    public static void setPan(float value)
    {
        if (value < -1) 
            value = -1;
        if (value > 1) 
            value = 1;
        if (mPan == value)
            return;
        
        mSoundTransform.pan = mPan = value;
    }
    
    /**
     * Randomly chooses and returns one of the provided values.
     * @param   objs        The Objects you want to randomly choose from. Can be ints, Numbers, Points, etc.
     * @return  A randomly chosen one of the provided parameters.
     */
    public static Object choose(Object... objs) {
        Random r = new Random();
        
        return objs[r.nextInt(objs.length)];
    }
    
    /**
     * Finds the sign of the provided value.
     * @param   value       The Number to evaluate.
     * @return  1 if value > 0, -1 if value < 0, and 0 when value == 0.
     */
    public static int sign(float value)
    {
        return value < 0 ? -1 : (value > 0 ? 1 : 0);
    }
    
    public static int sign(int value)
    {
        return value < 0 ? -1 : (value > 0 ? 1 : 0);
    }
    
    /**
     * Approaches the value towards the target, by the specified amount, without overshooting the target.
     * @param   value   The starting value.
     * @param   target  The target that you want value to approach.
     * @param   amount  How much you want the value to approach target by.
     * @return  The new value.
     */
    public static float approach(float value, float target, float amount) {
        return value < target ? (target < value + amount ? target : value + amount) : (target > value - amount ? target : value - amount);
    }

    /**
     * Linear interpolation between two values.
     * @param   a       First value.
     * @param   b       Second value.
     * @param   t       Interpolation factor.
     * @return  When t=0, returns a. When t=1, returns b. When t=0.5, will return halfway between a and b. Etc.
     */
    public static float  lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
    
    /**
     * Linear interpolation between two colors.
     * @param   fromColor       First color.
     * @param   toColor         Second color.
     * @param   t               Interpolation value. Clamped to the range [0, 1].
     * return   RGB component-interpolated color value.
     */
    public static int colorLerp(int fromColor, int toColor, float t) {
        if (t <= 0) { return fromColor; }
        if (t >= 1) { return toColor; }
        int a = Color.alpha(fromColor);
        int r = Color.red(fromColor);
        int g = Color.green(fromColor);
        int b = Color.blue(fromColor);
        
        int dA = Color.alpha(toColor) - a;
        int dR = Color.red(toColor) - r;
        int dG = Color.green(toColor) - g;
        int dB = Color.blue(toColor) -b;

        a += dA * t;
        r += dR * t;
        g += dG * t;
        b += dB * t;
        return Color.argb(a, r, g, b);
    }
    
    /**
     * Steps the object towards a point.
     * @param   object      Object to move (must have an x and y property).
     * @param   x           X position to step towards.
     * @param   y           Y position to step towards.
     * @param   distance    The distance to step (will not overshoot target).
     */
    public static void stepTowards(Positionable object, int  x, int y, float distance) {
        point.x = x - object.x;
        point.y = y - object.y;
        double len = PointF.length(point.x, point.y);
        if (len <= distance)
        {
            object.x = x;
            object.y = y;
            return;
        }
        point.x /= len;
        point.y /= len;
        object.x += point.x;
        object.y += point.y;
    }
    
    /**
     * Anchors the object to a position.
     * @param   object      The object to anchor.
     * @param   anchor      The anchor object.
     * @param   distance    The max distance object can be anchored to the anchor.
     */
    public static void anchorTo(Positionable object, Positionable anchor, float distance)
    {
        point.x = object.x - anchor.x;
        point.y = object.y - anchor.y;
        double len = PointF.length(point.x, point.y);
        if (len > distance) {
            point.x /= len;
            point.y /= len;
        }
        object.x = anchor.x + point.x;
        object.y = anchor.y + point.y;
    }
    
    /**
     * Finds the angle (in degrees) from point 1 to point 2.
     * @param   x1      The first x-position.
     * @param   y1      The first y-position.
     * @param   x2      The second x-position.
     * @param   y2      The second y-position.
     * @return  The angle from (x1, y1) to (x2, y2).
     */
    public static double angle(int x1, int y1, int x2, int y2) {
        return angle((float)x1,(float)y1,(float)x2,(float)y2);
    }
    
    public static double angle(float x1, float y1, float x2, float y2) {
        double a = Math.atan2(y2 - y1, x2 - x1) * DEG;
        return a < 0 ? a + 360 : a;
    }
    
    /**
     * Sets the x/y values of the provided object to a vector of the specified angle and length.
     * @param   object      The object whose x/y properties should be set.
     * @param   angle       The angle of the vector, in degrees.
     * @param   length      The distance to the vector from (0, 0).
     * @param   x           X offset.
     * @param   y           Y offset.
     */
    public static void angleXY(Positionable object, double angle) {
        angleXY(object, angle,1,0,0);
    }
    public static void angleXY(Positionable object, double angle, double length) {
        angleXY(object, angle, length, 0, 0);
    }
    public static void angleXY(Positionable object, double angle, double length, int x) {
        angleXY(object, angle, length, x, 0);
    }
    public static void angleXY(Positionable object, double angle, double length, int x, int y) {
        angle *= RAD;
        object.x = (int)(Math.cos(angle) * length + x);
        object.y = (int)(Math.sin(angle) * length + y);
    }
    
    /**
     * Rotates the object around the anchor by the specified amount.
     * @param   object      Object to rotate around the anchor.
     * @param   anchor      Anchor to rotate around.
     * @param   angle       The amount of degrees to rotate by.
     */
    
    public static void rotateAround(Positionable object, Positionable anchor, double angle) {
        rotateAround(object, anchor, angle, true);
    }
    
    public static void rotateAround(Positionable object, Positionable anchor, double angle, boolean relative) {
        if (relative) 
            angle += FP.angle(anchor.x, anchor.y, object.x, object.y);
        FP.angleXY(object, angle, FP.distance(anchor.x, anchor.y, object.x, object.y), anchor.x, anchor.y);
    }
    
    /**
     * Gets the difference of two angles, wrapped around to the range -180 to 180.
     * @param   a   First angle in degrees.
     * @param   b   Second angle in degrees.
     * @return  Difference in angles, wrapped around to the range -180 to 180.
     */
    public static double angleDiff(double a, double b) {
        double diff = b- a;

        while (diff > 180) { diff -= 360; }
        while (diff <= -180) { diff += 360; }

        return diff;
    }
    
    
    /**
     * Find the distance from (0, 0).
     * @param   x1      The first x-position.
     * @param   y1      The first y-position.
     * @return  The distance.
     */
    public static double distance(int x1, int y1) {
        return distance(x1,y1,0,0);
    }
    
    /**
     * Find the distance between two points.
     * @param   x1      The first x-position.
     * @param   y1      The first y-position.
     * @param   x2      The second x-position.
     * @param   y2      The second y-position.
     * @return  The distance.
     */
    public static double distance(int x1, int y1, int x2, int y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }
    
    /**
     * Find the distance between two rectangles. Will return 0 if the rectangles overlap.
     * @param   x1      The x-position of the first rect.
     * @param   y1      The y-position of the first rect.
     * @param   w1      The width of the first rect.
     * @param   h1      The height of the first rect.
     * @param   x2      The x-position of the second rect.
     * @param   y2      The y-position of the second rect.
     * @param   w2      The width of the second rect.
     * @param   h2      The height of the second rect.
     * @return  The distance.
     */
    public static double distanceRects(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
        if (x1 < x2 + w2 && x2 < x1 + w1)
        {
            if (y1 < y2 + h2 && y2 < y1 + h1) 
                return 0;
            if (y1 > y2) 
                return y1 - (y2 + h2);
            return y2 - (y1 + h1);
        }
        if (y1 < y2 + h2 && y2 < y1 + h1)
        {
            if (x1 > x2) 
                return x1 - (x2 + w2);
            return x2 - (x1 + w1);
        }
        if (x1 > x2)
        {
            if (y1 > y2) 
                return distance(x1, y1, (x2 + w2), (y2 + h2));
            return distance(x1, y1 + h1, x2 + w2, y2);
        }
        if (y1 > y2) 
            return distance(x1 + w1, y1, x2, y2 + h2);
        return distance(x1 + w1, y1 + h1, x2, y2);
    }
    
    /**
     * Find the distance between a point and a rectangle. Returns 0 if the point is within the rectangle.
     * @param   px      The x-position of the point.
     * @param   py      The y-position of the point.
     * @param   rx      The x-position of the rect.
     * @param   ry      The y-position of the rect.
     * @param   rw      The width of the rect.
     * @param   rh      The height of the rect.
     * @return  The distance.
     */
    public static double distanceRectPoint(int px, int py, int rx, int ry, int rw, int rh) {
        if (px >= rx && px <= rx + rw)
        {
            if (py >= ry && py <= ry + rh) 
                return 0;
            if (py > ry) 
                return py - (ry + rh);
            return ry - py;
        }
        if (py >= ry && py <= ry + rh)
        {
            if (px > rx) 
                return px - (rx + rw);
            return rx - px;
        }
        if (px > rx)
        {
            if (py > ry) 
                return distance(px, py, rx + rw, ry + rh);
            return distance(px, py, rx + rw, ry);
        }
        if (py > ry) 
            return distance(px, py, rx, ry + rh);
        return distance(px, py, rx, ry);
    }
    
    /**
     * Clamps the value within the minimum and maximum values.
     * @param   value       The Number to evaluate.
     * @param   min         The minimum range.
     * @param   max         The maximum range.
     * @return  The clamped value.
     */
    public static int clamp(int value, int min, int max)
    {
        if (max > min)
        {
            value = value < max ? value : max;
            return value > min ? value : min;
        }
        value = value < min ? value : min;
        return value > max ? value : max;
    }
    
    /**
     * Clamps the object inside the rectangle.
     * @param   object      The object to clamp (must have an x and y property).
     * @param   x           Rectangle's x.
     * @param   y           Rectangle's y.
     * @param   width       Rectangle's width.
     * @param   height      Rectangle's height.
     */
    public static void clampInRect(Positionable object, int x, int y, int width, int height) {
        clampInRect(object, x, y, width, height, 0);
    }
    
    /**
     * Clamps the object inside the rectangle.
     * @param   object      The object to clamp (must have an x and y property).
     * @param   x           Rectangle's x.
     * @param   y           Rectangle's y.
     * @param   width       Rectangle's width.
     * @param   height      Rectangle's height.
     * @param   padding     Padding on the Rectangle.
     */
    public static void clampInRect(Positionable object, int x, int y, int width, int height, int padding) {
        object.x = clamp(object.x, x + padding, x + width - padding);
        object.y = clamp(object.y, y + padding, y + height - padding);
    }
    
    /**
     * Transfers a value from one scale to another scale. For example, scale(.5, 0, 1, 10, 20) == 15, and scale(3, 0, 5, 100, 0) == 40.
     * @param   value       The value on the first scale.
     * @param   min         The minimum range of the first scale.
     * @param   max         The maximum range of the first scale.
     * @param   min2        The minimum range of the second scale.
     * @param   max2        The maximum range of the second scale.
     * @return  The scaled value.
     */
    public static double scale(double value, double min, double max, double min2, double max2) {
        return min2 + ((value - min) / (max - min)) * (max2 - min2);
    }
    
    /**
     * Transfers a value from one scale to another scale, but clamps the return value within the second scale.
     * @param   value       The value on the first scale.
     * @param   min         The minimum range of the first scale.
     * @param   max         The maximum range of the first scale.
     * @param   min2        The minimum range of the second scale.
     * @param   max2        The maximum range of the second scale.
     * @return  The scaled and clamped value.
     */
    public static double scaleClamp(double value, double min, double max, double min2, double max2) {
        value = min2 + ((value - min) / (max - min)) * (max2 - min2);
        if (max2 > min2)
        {
            value = value < max2 ? value : max2;
            return value > min2 ? value : min2;
        }
        value = value < min2 ? value : min2;
        return value > max2 ? value : max2;
    }
    
    /**
     * The random seed used by FP's random functions.
     */
    public static long getRandomSeed() {
        return mGetSeed;
    }
    
    public static void setRandomSeed(int value) {
        mSeed = FP.clamp(value,1, Integer.MAX_VALUE);
        mGetSeed = mSeed;
    }
    
    /**
     * Randomizes the random seed using Java's Math.random() function.
     */
    public static void randomizeSeed() {
        setRandomSeed((int)(Integer.MAX_VALUE * Math.random()));
    }
    
    /**
     * A pseudo-random Number produced using FP's random seed, where 0 <= Number < 1.
     */
    public static double random() {
        mSeed = (mSeed * 16807) % Integer.MAX_VALUE;
        return (double)mSeed / Integer.MAX_VALUE;
    }
    
    /**
     * Returns a pseudo-random uint.
     * @param   amount      The returned int will always be 0 <= uint < amount.
     * @return  The uint.
     */
    public static int rand(int amount) {
        mSeed = (mSeed * 16807) % Integer.MAX_VALUE;
        return (int)(((double)mSeed / Integer.MAX_VALUE) * amount);
    }
    
    /**
	 * Returns the next item after current in the list of options.
	 * @param	current		The currently selected item (must be one of the options).
	 * @param	options		An array of all the items to cycle through.
	 * @param	loop		If true, will jump to the first item after the last item is reached.
	 * @return	The next item in the list.
	 */
	public static Object next(Object current, Object[] options, boolean loop) {
		
		for (int i = 0; i < options.length; i++) {
			if (options[i].equals(current)) {
				if (loop) {
					return options[i+1 % options.length];
				} else {
					return options[Math.max(i + 1, options.length - 1)];
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the item previous to the current in the list of options.
	 * @param	current		The currently selected item (must be one of the options).
	 * @param	options		An array of all the items to cycle through.
	 * @param	loop		If true, will jump to the last item after the first is reached.
	 * @return	The previous item in the list.
	 */
	public static Object prev(Object current, Object[] options, boolean loop) {
		
		for (int i = 0; i < options.length; i++) {
			if (options[i].equals(current)) {
				if (loop) {
					return options[(i-1 + options.length) % options.length];
				} else {
					return options[Math.max(i - 1, 0)];
				}
			}
		}
		return null;
	}
	
	/**
	 * Swaps the current item between a and b. Useful for quick state/string/value swapping.
	 * @param	current		The currently selected item.
	 * @param	a			Item a.
	 * @param	b			Item b.
	 * @return	Returns a if current is b, and b if current is a.
	 */
	public static Object swap(Object current, Object a, Object b) {
		return (current == a) ? b : a;
	}

	
	/**
	 * Creates a color value by combining the chosen RGB values.
	 * @param	R		The red value of the color, from 0 to 255.
	 * @param	G		The green value of the color, from 0 to 255.
	 * @param	B		The blue value of the color, from 0 to 255.
	 * @return	The color uint.
	 */
	public static int getColorRGB(int r, int g, int b) {
		return Color.rgb(r, g, b);
	}
	
	/**
	 * Creates a color value with the chosen HSV values.
	 * @param	h		The hue of the color (from 0 to 1).
	 * @param	s		The saturation of the color (from 0 to 1).
	 * @param	v		The value of the color (from 0 to 1).
	 * @return	The color uint.
	 */
	public static int getColorHSV(float h, float s, float v) {
		
		HSV[0] = h;
		HSV[1] = s;
		HSV[2] = v;
		return Color.HSVToColor(HSV);
	}
	
	/**
	 * Finds the red factor of a color.
	 * @param	color		The color to evaluate.
	 * @return	A uint from 0 to 255.
	 */
	public static int getRed(int color) {
		return Color.red(color);
	}
	
	/**
	 * Finds the green factor of a color.
	 * @param	color		The color to evaluate.
	 * @return	A uint from 0 to 255.
	 */
	public static int getGreen(int color) {
		return Color.green(color);
	}
	
	/**
	 * Finds the blue factor of a color.
	 * @param	color		The color to evaluate.
	 * @return	A uint from 0 to 255.
	 */
	public static int getBlue(int color) {
		return Color.blue(color);
	}
	
	/**
	 * Sets a time flag.
	 * @return	Time elapsed (in milliseconds) since the last time flag was set.
	 */
	public static long timeFlag() {
		long t = System.currentTimeMillis();
		long e = t - mTime;
		mTime = t;
		return e;
	}
	
	/**
	 * The global Console object.
	 */
	public static Console getConsole() {
		if (mConsole == null) 
			mConsole = new Console();
		return mConsole;
	}
	
	/**
	 * Logs data to the console.
	 * @param	...data		The data parameters to log, can be variables, objects, etc. Parameters will be separated by a space (" ").
	 */
	public static void log(Object... data) {
		if (mConsole != null) {
			if (data.length > 1) {
				int i = 0;
				String s = "";
				while (i < data.length)
				{
					if (i > 0)
						s += " ";
					s += data[i++].toString();
				}
				mConsole.log(s);
			} else {
				mConsole.log(data[0].toString());
			}
		}
	}
	
	/**
	 * Loads the file as an XML object.
	 * @param	file		The embedded file to load.
	 * @return	An XML object representing the file.
	 */
	public static Document getXML(Context c, int resId) {
		InputStream is = c.getResources().openRawResource(resId);
		DocumentBuilderFactory builderfactory = DocumentBuilderFactory.newInstance();
		Document d;
		try {
			d = builderfactory.newDocumentBuilder().parse(is);
			return d;
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static MultiVarTween tween(Object object, Map<String, Number> values, float duration) {
		return tween(object, values, duration, null);
	}
	/**
	 * Tweens numeric public properties of an Object. Shorthand for creating a MultiVarTween tween, starting it and adding it to a Tweener.
	 * @param	object		The object containing the properties to tween.
	 * @param	values		An object containing key/value pairs of properties and target values.
	 * @param	duration	Duration of the tween.
	 * @param	options		An object containing key/value pairs of the following optional parameters:
	 * 						type		Tween type.
	 * 						complete	Optional completion callback function.
	 * 						ease		Optional easer function.
	 * 						tweener		The Tweener to add this Tween to.
	 * @return	The added MultiVarTween object.
	 * 
	 * Example: FP.tween(object, { x: 500, y: 350 }, 2.0, { ease: easeFunction, complete: onComplete } );
	 */
	
	public static Map<String, Number> tweenmap(String k1, Number v1) {
		return tweenmap(k1, v1, null, null, null, null, null, null, null, null);
	}
	
	public static Map<String, Number> tweenmap(String k1, Number v1, String k2, Number v2) {
		return tweenmap(k1,v1,k2,v2, null, null, null, null, null, null);
	}
	
	public static Map<String, Number> tweenmap(String k1, Number v1, String k2, Number v2,  String k3, Number v3) {
		return tweenmap(k1,v1,k2,v2,k3,v3, null, null, null, null);
	}
	
	public static Map<String, Number> tweenmap(String k1, Number v1, String k2, Number v2, String k3, Number v3,  String k4, Number v4) {
		return tweenmap(k1,v1,k2,v2,k3,v3,k4,v4, null, null);
	}
	
	public static Map<String, Number> tweenmap(String k1, Number v1, String k2, Number v2,
											String k3, Number v3, String k4, Number v4,
											String k5, Number v5) {
		Map<String, Number> theMap = new HashMap<String, Number>();
		
		if (k5 != null) 
			theMap.put(k5, v5);
		if (k4 != null) 
			theMap.put(k4, v4);
		if (k3 != null) 
			theMap.put(k3, v3);
		if (k2 != null) 
			theMap.put(k2, v2);
		if (k1 != null) 
			theMap.put(k1, v1);
		
		return theMap;
	}
	
	public static class TweenOptions {
		int type;
		OnCompleteCallback complete;
		OnEaseCallback ease;
		Tweener tweener;
		
		public TweenOptions(int theType, OnCompleteCallback theComplete, OnEaseCallback theEase, Tweener theTweener) {
			type = theType;
			complete = theComplete;
			ease = theEase;
			tweener = theTweener;
		}
	}
	
	public static MultiVarTween tween(Object object, Map<String, Number> values, float duration, TweenOptions options) {
		int type = Tween.ONESHOT;
		OnCompleteCallback complete = null;
		OnEaseCallback ease = null;
		Tweener tweener = FP.getWorld();
		
		if (object instanceof Tweener)  
			tweener = (Tweener)object;
		if (options != null){
			type = options.type;
			complete = options.complete;
			ease = options.ease;
			tweener = options.tweener;
		}
		MultiVarTween tween = new MultiVarTween(complete,type);
		tween.tween(object, values, duration, ease);
		tweener.addTween(tween);
		return tween;
	} 
	
	public static int[] frames(int from, int to) {
		return frames(from, to, 0);
	}
	
	/**
	 * Gets an array of frame indices.
	 * @param	from	Starting frame.
	 * @param	to		Ending frame.
	 * @param	skip	Skip amount every frame (eg. use 1 for every 2nd frame).
	 */
	public static int[] frames(int from, int to, int skip) {
		int index = 0;
		skip ++;
		int a[] = new int[(int)(Math.abs(to - from) / skip)];
		if (from < to) {
			while (from <= to) {
				a[index++] = from;
				from += skip;
			}
		} else {
			while (from >= to) {
				a[index++] = from;
				from -= skip;
			}
		}
		return a;
	}
	
	/**
	 * Shuffles the elements in the array.
	 * @param	a		The Object to shuffle (an Array or Vector).
	 */
	public static void shuffle(Object[] a) {
		int i = a.length;
		int j;
		Object t;
		
		while (--i > 0) {
			t = a[i];
			a[i] = a[j = FP.rand(i + 1)];
			a[j] = t;
		}
	}
	
	/**
	 * Shuffles the elements in the array.
	 * @param	a		The Object to shuffle (an Array or Vector).
	 */
	public static void shuffle(Vector<Object> a) {
		int i = a.size();
		int j;
		Object t;
		
		while (--i > 0) {
			t = a.get(i);
			a.add(i, a.get(j = FP.rand(i + 1)));
			a.add(j, t);
		}
	}
	
	public static Bitmap getBitmap(int resId) {
		return ((BitmapDrawable)FP.resources.getDrawable(resId)).getBitmap();
	}
}
