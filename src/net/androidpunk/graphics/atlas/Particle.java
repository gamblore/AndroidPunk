package net.androidpunk.graphics.atlas;

/**
 * Used by the Emitter class to track an existing Particle.
 */
public class Particle {
	// Particle information.
	protected ParticleType mType;
	protected float mTime;
	protected float mDuration;

	// Motion information.
	protected float mX;
	protected float mY;
	protected float mMoveX;
	protected float mMoveY;
	
	// List information.
	protected Particle mPrev;
	protected Particle mNext;
}
