package net.androidpunk;

import java.util.Vector;

import net.androidpunk.utils.Input;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;

public class Screen {

	private static final String TAG = "Screen";
	
    // Screen information.
    private Bitmap mBitmap;
    private int mCurrent = 0;
    private Matrix mMatrix = new Matrix();
    
    private Canvas mCanvas = FP.canvas;
    private Paint mPaint = FP.paint;
    
    private int mX, mY, mWidth, mHeight, mOriginX, mOriginY;
    private float mScaleX = 1;
    private float mScaleY = 1;
    private float mScale = 0;
    private float mAngle = 0;
    private int mColor = 0xff202020;
    
    private static MotionEvent mInput = null;
    
    private int[] mXInput = new int[2];
    private int[] mYInput = new int[2];
    
	private final Point mPoints[] = new Point[] { new Point(), new Point(), new Point(), new Point(), new Point() };
	
	private final Vector<Integer> mPointIndices = new Vector<Integer>();
    
    public Screen() {
		// create screen buffers
		mBitmap = Bitmap.createBitmap(FP.width, FP.height, Config.ARGB_8888);
		//mBitmap[1] = mBitmap[0].copy(Config.ARGB_8888, true);
		
		FP.buffer = mBitmap;
		mWidth = FP.width;
		mHeight = FP.height;
		update();
		
	}
    
    /**
     * Initialise buffers to current screen size.
     */
    public void resize() {
        mBitmap.recycle(); 
        // create screen buffers
        mBitmap = Bitmap.createBitmap(FP.width, FP.height, Config.ARGB_8888);
        //mBitmap[1] = mBitmap[0].copy(Config.ARGB_8888, true);
        
        Log.d(TAG, String.format("Screen created %dx%d", mBitmap.getWidth(), mBitmap.getHeight()));

        mCanvas.setBitmap(mBitmap);
        mCanvas.drawColor(mColor);
        
        //TODO FIGURE OUT WHAT SPRITE DOES?
        //mSprite.addChild(mBitmap[0]).visible = true;
        //mSprite.addChild(mBitmap[1]).visible = false;
        
        FP.buffer = mBitmap;
        mWidth = FP.width;
        mHeight = FP.height;
        mCurrent = 0;
    }
	
	/**
	 * Refreshes the screen.
	 */
	public void refresh() {
		// refreshes the screen
		FP.buffer.eraseColor(mColor);
	}
	
	/** @private Re-applies transformation matrix. */
	public void update() {
		float values[] = new float[9];
		mMatrix.getValues(values);
		values[3] = values[2] = 0;
		values[0] = mScaleX * mScale;
		values[4] = mScaleY * mScale;
		values[2] = -mOriginX * values[0];
		values[5] = -mOriginY * values[4];
		mMatrix.setValues(values);
		if (mAngle != 0) {
			mMatrix.postRotate(mAngle);
		}
		mMatrix.getValues(values);
		values[2] += mOriginX * mScaleX * mScale + mX;
		values[5] += mOriginY * mScaleY * mScale + mY;
		mMatrix.setValues(values);
	}
	
	/**
	 * Refresh color of the screen.
	 */
	public int getColor() {
		return mColor;
	}
	
	/**
	 * Refresh color of the screen.
	 */
	public void setColor(int color) {
		mColor = color;
	}
	
	/**
	 * X offset of the screen.
	 */
	public int getX() {
		return mX;
	}
	
	/**
	 * X offset of the screen.
	 */
	public void setX(int x) {
		if (mX == x) 
			return;
		mX = x;
		update();
	}
	
	/**
	 * Y offset of the screen.
	 */
	public int getY() {
		return mY;
	}
	/**
	 * Y offset of the screen.
	 */
	public void setY(int y) {
		if (mY == y) 
			return;
		mY = y;
		update();
	}
	
	/**
	 * X origin of transformations.
	 */
	public int getOriginX() {
		return mOriginX;
	}
	/**
	 * X origin of transformations.
	 */
	public void setOriginX(int originX) {
		if (mOriginX == originX) 
			return;
		mOriginX = originX;
		update();
	}
	
	/**
	 * Y origin of transformations.
	 */
	public int getOriginY() {
		return mOriginY;
	}
	/**
	 * Y origin of transformations.
	 */
	public void setOriginY(int originY) {
		if (mOriginY == originY) 
			return;
		mOriginY = originY;
		update();
	}
	
	/**
	 * Scale factor of the screen. Final scale is scaleX * scale by scaleY * scale, so
	 * you can use this factor to scale the screen both horizontally and vertically.
	 */
    public float getScale() {
        return mScale;
    }
    
    /**
	 * Scale factor of the screen. Final scale is scaleX * scale by scaleY * scale, so
	 * you can use this factor to scale the screen both horizontally and vertically.
	 */
    public void setScale(float scale) {
        mScale = scale;
    }
    
    /**
	 * X scale of the screen.
	 */
    public float getScaleX() {
        return mScaleX;
    }
    
    /**
	 * X scale of the screen.
	 */
    public void setScaleX(float scale) {
        mScaleX = scale;
    }
    
    /**
	 * Y scale of the screen.
	 */
    public float getScaleY() {
        return mScaleY;
    }
    
    /**
	 * Y scale of the screen.
	 */
    public void setScaleY(float scale) {
        mScaleY = scale;
    }
    
    /**
	 * Rotation of the screen, in degrees.
	 */
	public float getAngle() {
		return mAngle * FP.DEG; 
	}
	
	public void setAngle(float angle)
	{
		if (mAngle == angle * FP.RAD)
			return;
		mAngle = angle * FP.RAD;
		update();
	}
	
	/**
	 * Width of the screen.
	 */
	public int getWidth() {
		return mWidth; 
	}

	/**
	 * Height of the screen.
	 */
	public int getHeight() {
		return mHeight; 
	}
	
	public int[] getTouchX() {
		for (int i = 0; i < mInput.getPointerCount() && i < 2; i++) {
			mXInput[i] = (int)mInput.getX(i);
		}
		return mXInput;
	}
	
	public int[] getTouchY() {
		for (int i = 0; i < mInput.getPointerCount() && i < 2; i++) {
			mYInput[i] = (int)mInput.getY(i);
		}
		return mYInput;
	}
	
	public int getTouchesCount() {
		return (mInput != null) ? Math.min(mPointIndices.size(), 5) : 0; 
	}
	
	public Point[] getTouches() {
		if (mInput != null) {
			int index = 0;
			
			for (Integer id : mPointIndices) {
				for(int i = 0; i < mInput.getPointerCount() && index < mPoints.length; i++) {
					if (mInput.getPointerId(i) == id) {
						mPoints[index].x = (int)mInput.getX(i);
						mPoints[index].y = (int)mInput.getY(i);
						index++;
						break;
					}
				}
			}
			/*
			for (int i = 0; i < mInput.getPointerCount() && i < 5; i++) {
				mPoints[i].x = (int)mInput.getX(i);
				mPoints[i].y = (int)mInput.getY(i);
			}
			*/
		}
		return mPoints;
	}
	
	public void setMotionEvent(MotionEvent me) {
		if (me.getActionMasked() == MotionEvent.ACTION_DOWN) {
			mPointIndices.add(me.getPointerId(0));
			Input.mouseDown = true;
			Input.mouseUp = false;
			Input.mousePressed = true;
		}
		if ( me.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
			int id = me.getPointerId(me.getActionIndex());
			//Log.d(TAG, "Adding " + id);
			mPointIndices.add(id);
		}
		if ( me.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {
			int id = me.getPointerId(me.getActionIndex());
			//Log.d(TAG, "Removing " + id);
			mPointIndices.remove((Integer)id);
		}
		
		mInput = me;
		if (me.getActionMasked() == MotionEvent.ACTION_UP) {
			mPointIndices.clear();
			Input.mouseDown = false;
			Input.mouseUp = true;
			Input.mouseReleased = true;
			mInput = null;
		}
	}
	
	/**
	 * Captures the current screen as an Image object.
	 * @return	A new Image object.
	 */
	public Bitmap capture() {
		return mBitmap.copy(Config.ARGB_8888, false);
	}
	
}
