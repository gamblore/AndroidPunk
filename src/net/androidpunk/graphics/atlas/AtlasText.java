package net.androidpunk.graphics.atlas;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.FP;
import net.androidpunk.android.OpenGLSystem;
import net.androidpunk.graphics.opengl.Atlas;
import net.androidpunk.graphics.opengl.TextAtlas;
import net.androidpunk.graphics.opengl.Texture;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.Log;

public class AtlasText extends AtlasGraphic {
	
	private static final String TAG  = "AtlasText";
	
	private String mText = "sff2afs2";
	
	private static final Map<Typeface, Map<Integer, TextAtlas>> mTextAtlasCollection = new HashMap<Typeface, Map<Integer, TextAtlas>>();
	
	private TextAtlas mTextAtlas;
	private Texture mTexture;
	
	private FloatBuffer mGeometryBuffer, mTextureBuffer;
	private ShortBuffer mIndexBuffer;
	private int mIndexCount;
	
	private int mFontSize;
	
	
	public AtlasText(String s, int fontSize) {
		this(s, fontSize, FP.typeface);
	}
	
	public AtlasText(String s, int fontSize, Typeface typeface) {
		super(findOrCreateAtlas(fontSize, typeface));

		mFontSize = fontSize;
		mTextAtlas = mTextAtlasCollection.get(typeface).get(fontSize);
		mTexture = mTextAtlas.getAtlas();
		
		setText(s);
	}
	
	public AtlasText(String s, TextAtlas atlas) {
		super(atlas.getAtlas());
		
		mFontSize = atlas.getFontSize();
		mTextAtlas = atlas;
		mTexture = mTextAtlas.getAtlas();
		setText(s);
		
	}
	
	private static Atlas findOrCreateAtlas(int fontSize, Typeface typeface) {
		Atlas builtAtlas;
		
		if (typeface == null) {
			typeface = Typeface.DEFAULT;
		}
		
		// Check if we have used this Typeface before
		Map<Integer, TextAtlas> typefaceMap = mTextAtlasCollection.get(typeface);
		if (typefaceMap != null) {
			
			// Old Typeface
			TextAtlas existingAtlas = typefaceMap.get(fontSize);
			if (existingAtlas == null) {
				
				// New fontsize
				Log.d(TAG, "New fontsize " + fontSize);
				TextAtlas newAtlas = new TextAtlas(fontSize, typeface);
				typefaceMap.put(fontSize, newAtlas);
				builtAtlas = newAtlas.getAtlas();
			} else {
				
				// Existing TextAtlas
				Log.d(TAG, "Existing Atlas" + fontSize);
				builtAtlas = existingAtlas.getAtlas();
			}
		} else {
			
			//New Typeface
			Log.d(TAG, "New Typeface" + fontSize);
			Map<Integer, TextAtlas> sizeMap = new HashMap<Integer, TextAtlas>();
			TextAtlas newAtlas = new TextAtlas(fontSize, typeface);
			sizeMap.put(fontSize, newAtlas);
			mTextAtlasCollection.put(typeface, sizeMap);
			builtAtlas = newAtlas.getAtlas();
		}
		return builtAtlas;
	}

	public int getSize() {
		return mFontSize;
	}
	
	public void setSize(int fontSize) {
		if (fontSize == mFontSize) {
			return;
		}
		
		Typeface tf = mTextAtlas.getTypeface();
		
		Atlas a = findOrCreateAtlas(fontSize, tf);
		setAtlas(a);
		
		mTextAtlas = mTextAtlasCollection.get(tf).get(fontSize);
		mTexture = mTextAtlas.getAtlas();
		
		String oldText = mText;
		mText = "";
		mFontSize = fontSize;
		setText(oldText);
	}
	
	public void setText(String s) {
		if (mText.equals(s)) {
			return;
		}
		
		mText = s;
		
		mGeometryBuffer = mTextAtlas.getStringGeometryBuffer(mText);
		mTextureBuffer = mTextAtlas.getStringTextureBuffer(mText);
		
		mIndexBuffer = mTextAtlas.setBuffers(mText, mGeometryBuffer, mTextureBuffer);
		mIndexCount = mText.length() * 6;
	}
	
	public String getText() {
		return mText;
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
    	gl.glVertexPointer(2, GL10.GL_FLOAT, 0, mGeometryBuffer);
    	
    	gl.glPushMatrix();
    	{
    		setMatrix(gl);
    		gl.glDrawElements(GL10.GL_TRIANGLES, mIndexCount, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
    	}
    	gl.glPopMatrix();
	}
	
	public int getWidth() {
		return mTextAtlas.getWidth(mText);
	}
	
	public int getHeight() {
		return mTextAtlas.getHeight(mText);
	}

	
}
