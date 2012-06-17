package net.androidpunk;

import net.androidpunk.flashcompat.Sprite;
import net.androidpunk.utils.Input;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.MotionEvent;

public class Screen {

    // Screen information.
    private Sprite mSprite = new Sprite();
    private Bitmap[] mBitmap = new Bitmap[2];
    private int mCurrent = 0;
    private Matrix mMatrix = new Matrix();
    
    private int mX, mY, mWidth, mHeight, mOriginX, mOriginY;
    private float mScaleX = 1;
    private float mScaleY = 1;
    private float mScale = 0;
    private float mAngle = 0;
    private int mColor = 0xff202020;
    
    private MotionEvent mInput;
    
    private int[] mXInput = new int[2];
    private int[] mYInput = new int[2];
    
    public Screen() {
		// create screen buffers
		mBitmap[0] = Bitmap.createBitmap(FP.width, FP.height, Config.ARGB_8888);
		mBitmap[1] = mBitmap[0].copy(Config.ARGB_8888, true);
		
		FP.buffer = mBitmap[0];
		mWidth = FP.width;
		mHeight = FP.height;
		update();
	}
    
    /**
     * Initialise buffers to current screen size.
     */
    public void resize() {
              
        // create screen buffers
        mBitmap[0] = Bitmap.createBitmap(FP.width, FP.height, Config.ARGB_8888);
        mBitmap[1] = Bitmap.createBitmap(FP.width, FP.height, Config.ARGB_8888);
        
        Canvas c = new Canvas();
        for (int i = 0; i < mBitmap.length; i++) {
            c.setBitmap(mBitmap[i]);
            c.drawColor(mColor);
        }
        
        //TODO FIGURE OUT WHAT SPRITE DOES?
        //mSprite.addChild(mBitmap[0]).visible = true;
        //mSprite.addChild(mBitmap[1]).visible = false;
        
        FP.buffer = mBitmap[0];
        mWidth = FP.width;
        mHeight = FP.height;
        mCurrent = 0;
    }
    
    /**
	 * Swaps screen buffers.
	 */
	public void swap() {
		mCurrent = 1 - mCurrent;
		FP.buffer = mBitmap[mCurrent];
	}
	
	/**
	 * Refreshes the screen.
	 */
	public void refresh() {
		// refreshes the screen
		Canvas c = new Canvas(FP.buffer);
		Paint p = new Paint();
		p.setStyle(Style.FILL);
		p.setColor(mColor);
		c.drawRect(FP.bounds, p);
	}
	
	/**
	 * Redraws the screen.
	 */
	public void redraw() {
		// TODO refresh the buffers
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
		mSprite.transform.matrix = mMatrix;
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
	
	protected void setMotionEvent(MotionEvent me) {
		if (me.getActionMasked() == MotionEvent.ACTION_DOWN || me.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
			Input.mouseDown = true;
			Input.mouseUp = false;
			Input.mousePressed = true;
		}
		if (me.getActionMasked() == MotionEvent.ACTION_UP) {
			Input.mouseDown = false;
			Input.mouseUp = true;
			Input.mouseReleased = true;
		}
	}
	
	/**
	 * Captures the current screen as an Image object.
	 * @return	A new Image object.
	 */
	public Bitmap capture() {
		return mBitmap[mCurrent].copy(Config.ARGB_8888, false);
	}
	
}
