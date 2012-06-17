package net.androidpunk.utils;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.Graphic;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.TypedValue;

public class Draw {

	/**
	 * The blending mode used by Draw functions. This will not
	 * apply to Draw.line(), but will apply to Draw.linePlus().
	 */
	public static final Paint blend = new Paint();
	private static final Paint resetPaint = new Paint();
	
	private static Bitmap mTarget;
	private static Point mCamera;
	private static Canvas mCanvas = FP.canvas; 
	private static Rect mRect = FP.rect;
	
	public static void setTarget(Bitmap target) {
		setTarget(target, null, null);
	}
	
	public static void setTarget(Bitmap target, Point camera) {
		setTarget(target, camera, null);
	}
	
	/**
	 * Sets the drawing target for Draw functions.
	 * @param	target		The buffer to draw to.
	 * @param	camera		The camera offset (use null for none).
	 * @param	blend		The blend mode to use.
	 */
	public static void setTarget(Bitmap target, Point camera, Paint blend) {
		mTarget = target;
		mCamera = (camera != null) ? camera : FP.zero;
		if (blend != null) {
			Draw.blend.set(blend);
		}
	}
	
	/**
	 * Resets the drawing target to the default. The same as calling Draw.setTarget(FP.buffer, FP.camera).
	 */
	public static void resetTarget() {
		mTarget = FP.buffer;
		mCamera = FP.camera;
		Draw.blend.set(resetPaint);
	}
	
	
	public static void line(int x1, int y1, int x2, int y2) {
		line(x1,y1,x2,y2,0xffffffff);
	}
	/**
	 * Draws a pixelated, non-antialiased line.
	 * @param	x1		Starting x position.
	 * @param	y1		Starting y position.
	 * @param	x2		Ending x position.
	 * @param	y2		Ending y position.
	 * @param	color	Color of the line.
	 */
	public static void line(int x1, int y1, int x2, int y2, int color) {
		x1 -= mCamera.x;
		y1 -= mCamera.y;
		x2 -= mCamera.x;
		y2 -= mCamera.y;
		
		Bitmap screen = mTarget;
		float X = Math.abs(x2 - x1);
		float Y = Math.abs(y2 - y1);
		int xx, yy;
		// draw a single pixel
		if (X == 0) {
			if (Y == 0) {
				screen.setPixel(x1, y1, color);
				return;
			}
			// draw a straight vertical line
			yy = y2 > y1 ? 1 : -1;
			while (y1 != y2) {
				screen.setPixel(x1, y1, color);
				y1 += yy;
			}
			screen.setPixel(x2, y2, color);
			return;
		}

		if (Y == 0) {
			// draw a straight horizontal line
			xx = x2 > x1 ? 1 : -1;
			while (x1 != x2) {
				screen.setPixel(x1, y1, color);
				x1 += xx;
			}
			screen.setPixel(x2, y2, color);
			return;
		}

		xx = x2 > x1 ? 1 : -1;
		yy = y2 > y1 ? 1 : -1;
		float c = 0;
		float slope;

		if (X > Y) {
			slope = Y / X;
			c = .5f;
			while (x1 != x2) {
				screen.setPixel(x1, y1, color);
				x1 += xx;
				c += slope;
				if (c >= 1)
				{
					y1 += yy;
					c -= 1;
				}
			}
			screen.setPixel(x2, y2, color);
		} else {
			slope = X / Y;
			c = .5f;
			while (y1 != y2) {
				screen.setPixel(x1, y1, color);
				y1 += yy;
				c += slope;
				if (c >= 1) {
					x1 += xx;
					c -= 1;
				}
			}
			screen.setPixel(x2, y2, color);
		}
	}
	
	public static void linePlus(int x1, int y1, int x2, int y2) {
		linePlus(x1, y1, x2, y2, 0xff000000, 1, 1);
	}
	
	public static void linePlus(int x1, int y1, int x2, int y2, int color) {
		linePlus(x1, y1, x2, y2, color, 1, 1);
	}
	
	public static void linePlus(int x1, int y1, int x2, int y2, int color, int alpha) {
		linePlus(x1, y1, x2, y2, color, alpha, 1);
	}
	
	/**
	 * Draws a smooth, antialiased line with optional alpha and thickness.
	 * @param	x1		Starting x position.
	 * @param	y1		Starting y position.
	 * @param	x2		Ending x position.
	 * @param	y2		Ending y position.
	 * @param	color	Color of the line.
	 * @param	alpha	Alpha of the line.
	 * @param	thick	The thickness of the line.
	 */
	public static void linePlus(int x1, int y1, int x2, int y2, int color, int alpha, float thick) {
		
		Paint p = FP.paint;
		p.setStyle(Style.STROKE);
		p.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, thick, FP.resources.getDisplayMetrics()));
		p.setColor(color);
		p.setAlpha((int)(alpha));
		p.setAntiAlias(true);
		
		mCanvas.drawLine(x1 - mCamera.x, y1 - mCamera.y, x2 - mCamera.x, y2 - mCamera.y, p);
	}
	
	/**
	 * Draws a filled white rectangle.
	 * @param	x			X position of the rectangle.
	 * @param	y			Y position of the rectangle.
	 * @param	width		Width of the rectangle.
	 * @param	height		Height of the rectangle.
	 */
	public static void rect(int x, int y, int width, int height) {
		rect(x, y, width, height, 0xffffffff, 255);
	}
	
	/**
	 * Draws a filled rectangle.
	 * @param	x			X position of the rectangle.
	 * @param	y			Y position of the rectangle.
	 * @param	width		Width of the rectangle.
	 * @param	height		Height of the rectangle.
	 * @param	color		Color of the rectangle.
	 */
	public static void rect(int x, int y, int width, int height, int color) {
		rect(x, y, width, height, color, 255);
	}
	
	
	/**
	 * Draws a filled rectangle.
	 * @param	x			X position of the rectangle.
	 * @param	y			Y position of the rectangle.
	 * @param	width		Width of the rectangle.
	 * @param	height		Height of the rectangle.
	 * @param	color		Color of the rectangle.
	 * @param	alpha		Alpha of the rectangle.
	 */
	public static void rect(int x, int y, int width, int height, int color, int alpha) {
		Paint p = FP.paint;
		p.setStyle(Style.FILL);
		p.setColor(color);
		p.setAlpha(alpha);
		p.setAntiAlias(true);
		mRect.left = x - mCamera.x;
		mRect.top = y - mCamera.y;
		mRect.right = mRect.left + width;
		mRect.bottom = mRect.top + height;
		mCanvas.drawRect(mRect, p);
	}
	
	
	public static void circle(int x, int y, int radius) {
		circle(x,y,radius,0xffffffff);
	}
	/**
	 * Draws a non-filled, pixelated circle.
	 * @param	x			Center x position.
	 * @param	y			Center y position.
	 * @param	radius		Radius of the circle.
	 * @param	color		Color of the circle.
	 */
	public static void circle(int x, int y, int radius, int color) {
		x -= mCamera.x;
		y -= mCamera.y;
		int f = 1-radius;
		int fx = 1;
		int fy = -2 * radius;
		int xx = 0;
		int yy = radius;
		
		mTarget.setPixel(x, y + radius, color);
		mTarget.setPixel(x, y - radius, color);
		mTarget.setPixel(x + radius, y, color);
		mTarget.setPixel(x - radius, y, color);
		
		while (xx < yy)
		{
			if (f >= 0) 
			{
				yy --;
				fy += 2;
				f += fy;
			}
			xx ++;
			fx += 2;
			f += fx;    
			mTarget.setPixel(x + xx, y + yy, color);
			mTarget.setPixel(x - xx, y + yy, color);
			mTarget.setPixel(x + xx, y - yy, color);
			mTarget.setPixel(x - xx, y - yy, color);
			mTarget.setPixel(x + yy, y + xx, color);
			mTarget.setPixel(x - yy, y + xx, color);
			mTarget.setPixel(x + yy, y - xx, color);
			mTarget.setPixel(x - yy, y - xx, color);
		}
	}
	
	public static void circlePlus(int x, int y, float radius) {
		circlePlus(x,y,radius, 0xffffffff, 255, true, 1);
	}
	public static void circlePlus(int x, int y, float radius, int color) {
		circlePlus(x,y,radius, color, 255, true, 1);
	}
	public static void circlePlus(int x, int y, float radius, int color, int alpha) {
		circlePlus(x,y,radius, color, alpha, true, 1);
	}
	public static void circlePlus(int x, int y, float radius, int color, int alpha, boolean fill) {
		circlePlus(x,y,radius, color, alpha, true, 1);
	}
	
	/**
	 * Draws a circle to the screen.
	 * @param	x			X position of the circle's center.
	 * @param	y			Y position of the circle's center.
	 * @param	radius		Radius of the circle.
	 * @param	color		Color of the circle.
	 * @param	alpha		Alpha of the circle.
	 * @param	fill		If the circle should be filled with the color (true) or just an outline (false).
	 * @param	thick		How thick the outline should be (only applicable when fill = false).
	 */
	public static void circlePlus(int x, int y, float radius, int color, int alpha, boolean fill, float thick) {
		
		Paint p = FP.paint;
		if (fill)
			p.setStyle(Style.FILL);
		else 
			p.setStyle(Style.STROKE);
		p.setColor(color);
		p.setAlpha(alpha);
		p.setAntiAlias(true);
		
		mCanvas.drawCircle(x - mCamera.x, y - mCamera.y, radius, p);
	}
	
	public static void hitbox(Entity e) {
		hitbox(e,true,0xffffffff, 255);
	}
	public static void hitbox(Entity e, boolean outline) {
		hitbox(e,outline, 0xffffffff, 255);
	}
	public static void hitbox(Entity e, boolean outline, int color) {
		hitbox(e,outline, color, 255);
	}
	/**
	 * Draws the Entity's hitbox.
	 * @param	e			The Entity whose hitbox is to be drawn.
	 * @param	outline		If just the hitbox's outline should be drawn.
	 * @param	color		Color of the hitbox.
	 * @param	alpha		Alpha of the hitbox.
	 */
	public static void hitbox(Entity e, boolean outline, int color, int alpha) {
		Paint p = FP.paint;
		if (outline)
			p.setStyle(Style.STROKE);
		else 
			p.setStyle(Style.FILL);
		
		p.setColor(color);
		p.setAlpha(alpha);
		p.setAntiAlias(true);
		mRect.left = e.x - e.originX - mCamera.x;
		mRect.top = e.y - e.originY - mCamera.y;
		mRect.right = mRect.left + e.width;
		mRect.bottom = mRect.top + e.height;
		mCanvas.drawRect(mRect, p);
	}
	
	public static void graphic(Graphic g) {
		graphic(g,0,0);
	}
	/**
	 * Draws a graphic object.
	 * @param	g		The Graphic to draw.
	 * @param	x		X position.
	 * @param	y		Y position.
	 */
	public static void graphic(Graphic g, int x, int y) {
		if (g.visible) {
			if (g.relative) {
				FP.point.x = x;
				FP.point.y = y;
			}
			else 
				FP.point.x = FP.point.y = 0;
			FP.point2.x = FP.camera.x;
			FP.point2.y = FP.camera.y;
		}
	}
	
	public static void entity(Entity e) {
		entity(e, 0, 0, false);
	}
		
	public static void entity(Entity e, int x, int y) {
		entity(e, x, y, false);
	}
	
	/**
	 * Draws an Entity object.
	 * @param	e					The Entity to draw.
	 * @param	x					X position.
	 * @param	y					Y position.
	 * @param	addEntityPosition	Adds the Entity's x and y position to the target position.
	 */
	public static void entity(Entity e, int x, int y, boolean addEntityPosition) {
		Graphic g = e.getGraphic();
		if (e.visible && g != null) {
			if (addEntityPosition)
				graphic(g, x + e.x, y + e.y);
			else 
				graphic(g, x, y);
		}
	}
}
