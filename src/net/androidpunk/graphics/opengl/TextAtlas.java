package net.androidpunk.graphics.opengl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import net.androidpunk.FP;
import net.androidpunk.graphics.atlas.AtlasGraphic;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

public class TextAtlas {

	private static final String TAG = "TextAtlas";
	
	private static final int SPACER = 1;
	
	private final Paint mPaint = new Paint();
	
	private Atlas mAtlas;
	private Bitmap mBitmap;
	
	private Typeface mTypeface;
	private int mFontSize;
	
	private final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ !@#$%^&*()-+_=,./;'[]\\<>?:\"{}|`~";
	
	public TextAtlas(int fontSize) {
		this(fontSize, null);
	}
	
	public TextAtlas(int fontSize, Typeface tf) {
		mTypeface = tf;
		mFontSize = fontSize;
		
		mPaint.setColor(Color.WHITE);
		mPaint.setTextSize(fontSize);
		if (mTypeface != null) {
			mPaint.setTypeface(mTypeface);
		}
		
		float totalWidth = mPaint.measureText(CHARACTERS);
		float rows = totalWidth / 2048;
		int characterHeight = (int)(-mPaint.ascent() + mPaint.descent());
		float totalHeight = characterHeight * (rows+1);
		if (totalHeight > 2048) {
			Log.e(TAG, "TOO BIG FOR TEXTURE. FAILED TO CREATE");
			return;
		}
		
		mBitmap = Bitmap.createBitmap(1024, Texture.nextHigher2((int)totalHeight), Config.ARGB_8888);
		Canvas c = new Canvas(mBitmap);
		Log.d(TAG, String.format("Font bitmap of size %d is %dx%d", mFontSize, 1024, mBitmap.getHeight()));
		mAtlas = new Atlas();
		
		int x = 0;
		int y = 0;
		Rect r = new Rect();
		for (int i = 0; i < CHARACTERS.length(); i++) {
			String character = CHARACTERS.substring(i, i+1);
			int characterWidth = (int)mPaint.measureText(character);
			
			if (x + characterWidth > mBitmap.getWidth()) {
				x = 0;
				y += characterHeight + SPACER;
			}
			
			c.drawText(character, x, y - mPaint.ascent(), mPaint);
			
			
			r.set(x, y, x + characterWidth, y + characterHeight);
			mAtlas.addSubTexture(character, r);
			
			x += characterWidth + SPACER;
		}
		
		mAtlas.setTextureBitmap(mBitmap);
		
		/*
		try {
			mBitmap.compress(CompressFormat.PNG, 85, FP.context.openFileOutput("font.png", Context.MODE_WORLD_READABLE));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		*/
	}
	
	public int getFontSize() {
		return mFontSize;
	}
	
	public Typeface getTypeface() {
		return mTypeface;
	}
	
	public FloatBuffer getStringGeometryBuffer(String s) {
		FloatBuffer mVertexBuffer = AtlasGraphic.getDirectFloatBuffer(s.length() * 8);
		return mVertexBuffer;
	}
	public FloatBuffer getStringTextureBuffer(String s) {
		FloatBuffer mTextureBuffer = AtlasGraphic.getDirectFloatBuffer(s.length() * 8);
		return mTextureBuffer;
	}
	
	public ShortBuffer setBuffers(String s, FloatBuffer vertex, FloatBuffer texture) {
		
		ShortBuffer indexBuffer = AtlasGraphic.getDirectShortBuffer(s.length() * 6);
		
		indexBuffer.position(0);
		vertex.position(0);
		texture.position(0);
		
		int x = 0;
		int y = 0;
		
		int textureWidth = mAtlas.getWidth();
		int textureHeight = mAtlas.getHeight();
		
		for (int i = 0; i < s.length(); i++) {
			String character = s.substring(i, i+1);
			
			if ("\n".equals(character)) {
				y += -mPaint.ascent() + mPaint.descent();
				continue;
			} else if ("\t".equals(character)) {
				x += mPaint.measureText("\t");
			}
			SubTexture subTexture = mAtlas.getSubTexture(character);
			Rect r = subTexture.getBounds();
			
			//Log.d(TAG, String.format("drawing '%s' at %d,%d to %d,%d", character, x, y, x + r.width(), y + r.height()));
			
			vertex.put(x).put(y);
			vertex.put(x+r.width()).put(y);
			vertex.put(x).put(y+r.height());
			vertex.put(x+r.width()).put(y+r.height());
			
			//Log.d(TAG, "Character texture at " + r.toShortString());
			
			texture.put((float)r.left/textureWidth).put((float)r.top/textureHeight);
			texture.put((float)(r.left + r.width())/textureWidth).put((float)r.top/textureHeight);
			texture.put((float)r.left/textureWidth).put((float)(r.top + r.height())/textureHeight);
			texture.put((float)(r.left + r.width())/textureWidth).put((float)(r.top + r.height())/textureHeight);
			
			indexBuffer.put((short)(i*4));
			indexBuffer.put((short)((i*4)+1));
			indexBuffer.put((short)((i*4)+2));
			
			indexBuffer.put((short)((i*4)+1));
			indexBuffer.put((short)((i*4)+3));
			indexBuffer.put((short)((i*4)+2));
			
			x += r.width();
		}
		indexBuffer.position(0);
		vertex.position(0);
		texture.position(0);
		return indexBuffer;
	}
	
	public Atlas getAtlas() {
		return mAtlas;
	}
	
	public int getWidth(String s) {
		return (int)mPaint.measureText(s);
	}
	
	public int getHeight(String s) {
		String[] split = s.split("\n");
		return (int)(-mPaint.ascent() + mPaint.descent()) * split.length;
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
