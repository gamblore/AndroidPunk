package net.androidpunk.graphics;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.FP;
import net.androidpunk.Graphic;
import net.androidpunk.android.OpenGLSystem;
import net.androidpunk.graphics.atlas.AtlasGraphic;
import net.androidpunk.graphics.opengl.Texture;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

public class Text extends Graphic {

	private static final String TAG = "Text";
	
	/**
	 * Rotation of the image, in degrees.
	 */
	public float angle = 0;

	/**
	 * Scale of the image, effects both x and y scale.
	 */
	public float scale = 1;

	/**
	 * X scale of the image.
	 */
	public float scaleX = 1;

	/**
	 * Y scale of the image.
	 */
	public float scaleY = 1;

	/**
	 * X origin of the image, determines transformation point.
	 */
	public int originX;

	/**
	 * Y origin of the image, determines transformation point.
	 */
	public int originY;
	
	private int mTextSize;
	private String mText;
	private Typeface mTypeface;
	private Texture mTexture = new Texture();
	
	private final Rect mRect = new Rect();
	
	private int mColor = 0xffffffff;
	
	private static final Canvas mCanvas = new Canvas();
	private static final Paint mPaint = new Paint();
	
	private FloatBuffer mVertexBuffer = AtlasGraphic.getDirectFloatBuffer(8);
	private FloatBuffer mTextureBuffer = AtlasGraphic.getDirectFloatBuffer(8);
	
	public Text(String text) {
		this(text, 20, null);
	}
	
	public Text(String text, int textSize) {
		this(text, textSize, null);
	}
	public Text(String text, int textSize, Typeface tf) {
		super();
		mText = text;
		mTextSize = textSize;
		mTypeface = tf;
		
		updateTexture();
	}
	
	public void setTypeFace(Typeface tf) {
		mTypeface = tf;
		
		updateTexture();
	}
	
	public void setText(String text) {
		if (text.equals(mText)) {
			return;
		}
		mText = text;
		
		updateTexture();
	}

	private void updateTexture() {
		if (mTexture.isLoaded()) {
			// Will allow us to load on this texture again.
			mTexture.release();
		}
		mPaint.reset();
		if (mTypeface != null) {
			mPaint.setTypeface(mTypeface);
		}
		mPaint.setTextSize(mTextSize);
		
		mPaint.setColor(0xffffffff);
		mPaint.setStyle(Style.FILL);
		
		int width = (int)mPaint.measureText(mText);
		int height = (int)(-mPaint.ascent() + mPaint.descent());
		Bitmap newBuffer = Bitmap.createBitmap(Texture.nextHigher2(width), Texture.nextHigher2(height), Config.ARGB_8888);
		mCanvas.setBitmap(newBuffer);
		mCanvas.drawText(mText, 0, -mPaint.ascent(), mPaint);
		
		mRect.set(0, 0, width, height);
		
		mTexture.setTextureBitmap(newBuffer);
		
		AtlasGraphic.setGeometryBuffer(mVertexBuffer, mRect);
		AtlasGraphic.setTextureBuffer(mTextureBuffer, mTexture, mRect);
	}
	
	@Override 
	public void reload() {
		updateTexture();
	}
	
	@Override
	public void render(GL10 gl, Point point, Point camera) {
		super.render(gl, point, camera);
		if (!mTexture.isLoaded()) {
			return;
		}
		
		mTexture.mColorFilter.setColor(mColor);
		mTexture.mColorFilter.applyColorFilter(gl);
		OpenGLSystem.setTexture(gl, mTexture);
		
		mPoint.x = (int)(point.x + x - camera.x * scrollX);
		mPoint.y = (int)(point.y + y - camera.y * scrollY);
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
    	gl.glVertexPointer(2, GL10.GL_FLOAT, 0, mVertexBuffer);
    	
    	gl.glPushMatrix();
    	{
    		setMatrix(gl);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
    	}
    	gl.glPopMatrix();
	}
	
	protected void setMatrix(GL10 gl) {
		
		float sX = scaleX * scale;
		float sY = scaleY * scale;
		
		
		gl.glTranslatef(originX + mPoint.x, originY + mPoint.y, 0f);
		
		if (angle != 0) {
			gl.glRotatef(angle, 0, 0, 1.0f);
		}
		gl.glScalef(sX, sY, 1.0f);
		gl.glTranslatef(-originX, -originY, 0.0f);
	}
	
	/**
	 * The tinted color of the Image. Use 0xFFFFFFFF to draw the Image normally.
	 */
	public int getColor() { 
		return mColor; 
	}
	
	/**
	 * The tinted color of the Image. Use 0xFFFFFFFF to draw the Image normally.
	 */
	public void setColor(int value) {
		mColor = value;
	}
	
	public int getWidth() {
		return mRect.width();
	}

	public int getHeight() {
		return mRect.height();
	}
	
	public static final Typeface getFontFromRes(int resource)
	{ 
	    Typeface tf = null;
	    InputStream is = null;
	    try {
	        is = FP.context.getResources().openRawResource(resource);
	    }
	    catch(NotFoundException e) {
	        Log.e(TAG, "Could not find font in resources!");
	    }

	    String outPath = FP.context.getCacheDir() + "/tmp.raw";

	    try
	    {
	        byte[] buffer = new byte[is.available()];
	        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outPath));

	        int l = 0;
	        while((l = is.read(buffer)) > 0)
	            bos.write(buffer, 0, l);

	        bos.close();

	        tf = Typeface.createFromFile(outPath);

	        // clean up
	        new File(outPath).delete();
	    }
	    catch (IOException e)
	    {
	        Log.e(TAG, "Error reading in font!");
	        return null;
	    }

	    Log.d(TAG, "Successfully loaded font.");

	    return tf;      
	}
}
