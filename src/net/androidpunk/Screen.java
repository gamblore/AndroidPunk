package net.androidpunk;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;

import net.androidpunk.flashcompat.Sprite;

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
    
    public float getScale() {
        return mScale;
    }
    
    public void setScale(float scale) {
        mScale = scale;
    }
    
    public float getScaleX() {
        return mScaleX;
    }
    
    public void setScaleX(float scale) {
        mScaleX = scale;
    }
    
    public float getScaleY() {
        return mScaleY;
    }
    
    public void setScaleY(float scale) {
        mScaleY = scale;
    }
}
