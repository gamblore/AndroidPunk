package net.androidpunk.graphics.atlas;

import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.FP;
import net.androidpunk.android.OpenGLSystem;
import net.androidpunk.flashcompat.OnEaseCallback;
import net.androidpunk.graphics.opengl.SubTexture;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

/**
 * Particle emitter used for emitting and rendering particle sprites.
 * Good rendering performance with large amounts of particles.
 */
public class Emitter extends AtlasGraphic {

	private static final String TAG = "Emitter";
	
	// Particle infromation.
	private Map<String, ParticleType> mTypes = new HashMap<String, ParticleType>();
	private Particle mParticle;
	private Particle mCache;
	private int mParticleCount;

	// Source information.
	private int mWidth;
	private int mHeight;
	private int mFrameWidth;
	private int mFrameHeight;
	private int mFrameCount;
	
	private final float matrix[] = new float[20];
	
	// Drawing information.
	private Point mP = new Point();
	private ColorFilter mTint = new ColorFilter();
	private static final double SIN = Math.PI / 2;
	private static final Canvas mCanvas = new Canvas();
	
	
	/**
	 * Constructor. Sets the source image to use for newly added particle types.
	 * @param	source			Source image.
	 */
	public Emitter(SubTexture source) {
		this(source, source.getWidth(), source.getHeight());
	}
	
	/**
	 * Constructor. Sets the source image to use for newly added particle types.
	 * @param	source			Source image.
	 * @param	frameWidth		Frame width.
	 * @param	frameHeight		Frame height.
	 */
	public Emitter(SubTexture source, int frameWidth, int frameHeight)  {
		super(source);
		setSource(source, frameWidth, frameHeight);
		active = true;
	}
	
	
	public void setSource(SubTexture source) {
		setSource(source, 0, 0);
	}
	
	/**
	 * Changes the source image to use for newly added particle types.
	 * @param	source			Source image.
	 * @param	frameWidth		Frame width.
	 * @param	frameHeight		Frame height.
	 */
	public void setSource(SubTexture source, int frameWidth, int frameHeight) {
		mSubTexture = source;
		
		mWidth = mSubTexture.getWidth();
		mHeight = mSubTexture.getHeight();
		
		mFrameWidth = frameWidth != 0 ? frameWidth : mWidth;
		mFrameHeight = frameHeight != 0 ? frameHeight : mHeight;
		mFrameCount = (int)(mWidth / mFrameWidth) * (int)(mHeight / mFrameHeight);
	}
	
	@Override 
	public void update() {
		// quit if there are no particles
		if (mParticle == null) 
			return;

		// particle info
		float e = FP.fixed ? 1 : FP.elapsed;
		Particle p = mParticle;
		Particle n;
		float t;
		
		// loop through the particles
		while (p != null) {
			// update time scale
			p.mTime += e;
			t = p.mTime / p.mDuration;

			// remove on time-out
			if (p.mTime >= p.mDuration) {
				if (p.mNext != null) 
					p.mNext.mPrev = p.mPrev;
				if (p.mPrev != null) 
					p.mPrev.mNext = p.mNext;
				else 
					mParticle = p.mNext;
				n = p.mNext;
				p.mNext = mCache;
				p.mPrev = null;
				mCache = p;
				p = n;
				mParticleCount--;
				continue;
			}

			// get next particle
			p = p.mNext;
		}
	}
	
	/** @private Renders the particles. */
	@Override 
	public void render(GL10 gl, Point point, Point camera) {
		super.render(gl, point, camera);
		if (!getAtlas().isLoaded()) {
			return;
		}
		
		// quit if there are no particles
		if (mParticle == null)
			return;
		
		// get rendering position
		mPoint.x = (int)(point.x + x - camera.x * scrollX);
		mPoint.y = (int)(point.y + y - camera.y * scrollY);

		// particle info
		float t, td;
		Particle p = mParticle;
		ParticleType type;
		Rect rect;
		Paint paint = FP.paint;

		// loop through the particles
		while (p != null) {
			// get time scale
			t = p.mTime / p.mDuration;

			// get particle type
			type = p.mType;
			rect = type.mFrame;

			// get position
			td = (type.mEase == null) ? t : type.mEase.ease(t);
			mP.x = (int)(mPoint.x + p.mX + p.mMoveX * td);
			mP.y = (int)(mPoint.y + p.mY + p.mMoveY * td);
			
			gl.glPushMatrix(); 
			{
				setGeometryBuffer(QUAD_FLOAT_BUFFER_1, mP.x, mP.y, rect.width(), rect.height());
				if (type.mFrames != null) {
					setTextureBuffer(QUAD_FLOAT_BUFFER_2, mSubTexture, type.mFrames[(int)(td * type.mFrameCount)], rect.width(), rect.height());
				} else {
					setTextureBuffer(QUAD_FLOAT_BUFFER_2, mSubTexture, 0, rect.width(), rect.height());
				}
				
				setBuffers(gl, QUAD_FLOAT_BUFFER_1, QUAD_FLOAT_BUFFER_2);
				
				
				// draw particle

				// get color
				td = (type.mColorEase == null) ? t : type.mColorEase.ease(t);
					
				float red = (type.mRed + type.mRedRange * td) / 255.0f;
				float green = (type.mGreen + type.mGreenRange * td) / 255.0f;
				float blue = (type.mBlue + type.mBlueRange * td) / 255.0f;
				float alpha = (type.mAlpha + type.mAlphaRange * ((type.mAlphaEase == null) ? t : type.mAlphaEase.ease(t))) / 255.0f;
				gl.glColor4f(red, green, blue, alpha);
				gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
			}
			gl.glPopMatrix();
			
			unsetBuffers(gl);
			

			// get next particle
			p = p.mNext;
		}
	}
	
	/**
	 * Creates a new Particle type for this Emitter.
	 * @param	name		Name of the particle type.
	 * @return	A new ParticleType object.
	 */
	public ParticleType newType(String name) {
		return newType(name, null);
	}
	
	/**
	 * Creates a new Particle type for this Emitter.
	 * @param	name		Name of the particle type.
	 * @param	frames		Array of frame indices for the particles to animate.
	 * @return	A new ParticleType object.
	 */
	public ParticleType newType(String name, int [] frames) {
		if (mTypes.containsKey(name)) {
			Log.e(TAG, "Cannot add multiple particle types of the same name");
			return mTypes.get(name);
		}
		ParticleType p = new ParticleType(name, frames, mSubTexture, mFrameWidth, mFrameHeight);
		mTypes.put(name, p);
		return p; 
	}
	
	
	/**
	 * Defines the motion range for a particle type.
	 * @param	name			The particle type.
	 * @param	angle			Launch Direction.
	 * @param	distance		Distance to travel.
	 * @param	duration		Particle duration.
	 * @return	This ParticleType object.
	 */
	public ParticleType setMotion(String name, float angle, float distance, float duration) {
		return setMotion(name, angle, distance, duration, 0, 0, 0, null);
	}
	/**
	 * Defines the motion range for a particle type.
	 * @param	name			The particle type.
	 * @param	angle			Launch Direction.
	 * @param	distance		Distance to travel.
	 * @param	duration		Particle duration.
	 * @param	angleRange		Random amount to add to the particle's direction.
	 * @param	distanceRange	Random amount to add to the particle's distance.
	 * @param	durationRange	Random amount to add to the particle's duration.
	 * @return	This ParticleType object.
	 */
	public ParticleType setMotion(String name, float angle, float distance, float duration, float angleRange, float distanceRange, float durationRange) {
		return setMotion(name, angle, distance, duration, angleRange, distanceRange, durationRange, null);
	}
	/**
	 * Defines the motion range for a particle type.
	 * @param	name			The particle type.
	 * @param	angle			Launch Direction.
	 * @param	distance		Distance to travel.
	 * @param	duration		Particle duration.
	 * @param	angleRange		Random amount to add to the particle's direction.
	 * @param	distanceRange	Random amount to add to the particle's distance.
	 * @param	durationRange	Random amount to add to the particle's duration.
	 * @param	ease			easer function.
	 * @return	This ParticleType object.
	 */
	public ParticleType setMotion(String name, float angle, float distance, float duration, float angleRange, float distanceRange, float durationRange, OnEaseCallback ease) {
		ParticleType p = mTypes.get(name);
		if (p != null) {
			p.setMotion(angle, distance, duration, angleRange, distanceRange, durationRange, ease);
		}
		return p;
	}
	
	/**
	 * Sets the alpha range of the particle type.
	 * @param	name		The particle type.
	 * @param	start		The starting alpha.
	 * @return	This ParticleType object.
	 */
	public ParticleType setAlpha(String name, int start) {
		return setAlpha(name, start, 0, null);
	}
	
	/**
	 * Sets the alpha range of the particle type.
	 * @param	name		The particle type.
	 * @param	start		The starting alpha.
	 * @param	finish		The finish alpha.
	 * @return	This ParticleType object.
	 */
	public ParticleType setAlpha(String name, int start, int finish ) {
		return setAlpha(name, start, finish, null);
	}
	/**
	 * Sets the alpha range of the particle type.
	 * @param	name		The particle type.
	 * @param	start		The starting alpha.
	 * @param	finish		The finish alpha.
	 * @param	ease		easer function.
	 * @return	This ParticleType object.
	 */
	public ParticleType setAlpha(String name, int start, int finish, OnEaseCallback ease) {
		ParticleType p = mTypes.get(name);
		if (p != null) {
			p.setAlpha(start, finish, ease);
		}
		return p;
	}
	
	/**
	 * Sets the color range of the particle type.
	 * @param	name		The particle type.
	 * @param	start		The starting color.
	 * @return	This ParticleType object.
	 */
	public ParticleType setColor(String name, int start) {
		return setColor(name, start, 0, null);
	}
	
	/**
	 * Sets the color range of the particle type.
	 * @param	name		The particle type.
	 * @param	start		The starting color.
	 * @param	finish		The finish color.
	 * @return	This ParticleType object.
	 */
	public ParticleType setColor(String name, int start, int finish) {
		return setColor(name, start, finish, null);
	}
	
	/**
	 * Sets the color range of the particle type.
	 * @param	name		The particle type.
	 * @param	start		The starting color.
	 * @param	finish		The finish color.
	 * @param	ease		easer function.
	 * @return	This ParticleType object.
	 */
	public ParticleType setColor(String name, int start, int finish, OnEaseCallback ease) {
		ParticleType p = mTypes.get(name);
		if (p != null) {
			p.setColor(start, finish, ease);
		}
		return p;
	}
	
	/**
	 * Emits a particle.
	 * @param	name		Particle type to emit.
	 * @param	x			X point to emit from.
	 * @param	y			Y point to emit from.
	 * @return
	 */
	public Particle emit(String name, int x, int y) {
		Particle p;
		ParticleType type = mTypes.get(name);
		if (type == null) {
			Log.e(TAG, "Particle type \"" + name + "\" does not exist.");
			return null;
		}
		
		if (mCache != null) {
			p = mCache;
			mCache = p.mNext;
		}
		else
			p = new Particle();
		p.mNext = mParticle;
		p.mPrev = null;
		if (p.mNext != null)
			p.mNext.mPrev = p;

		p.mType = type;
		p.mTime = 0;
		p.mDuration = (float)(type.mDuration + type.mDurationRange * FP.random());
		double a = type.mAngle + type.mAngleRange * FP.random();
		double d = type.mDistance + type.mDistanceRange * FP.random();

		p.mMoveX = (float)(Math.cos(a) * d);
		p.mMoveY = (float)(Math.sin(a) * d);
		p.mX = x;
		p.mY = y;
		mParticleCount++;
		return (mParticle = p);
	}
	
	/**
	 * Amount of currently existing particles.
	 */
	public int getParticleCount() { return mParticleCount; }
}
