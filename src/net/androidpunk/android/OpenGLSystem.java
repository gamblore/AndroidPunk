package net.androidpunk.android;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.graphics.opengl.Texture;
import android.opengl.GLES20;
import android.os.SystemClock;
import android.util.Log;

public class OpenGLSystem {
	
	private static final String TAG = "OpenGLSystem";
	
	private static FloatBuffer textureBuffer;
	private static FloatBuffer vertexBuffer;
	
	private static final float vertexArray[] = new float[8];
	private static final float textureArray[] = new float[8];
	
	private static int mCurrentTexture = -1;
	private static final Queue<OpenGLRunnable> mQueue = new LinkedList<OpenGLRunnable>();
	
	private static GL10 mGL;
    
    public OpenGLSystem() {
        this(null);
    }

    public OpenGLSystem(GL10 gl) {
        mGL = gl;
        textureBuffer = ByteBuffer.allocateDirect(vertexArray.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer = ByteBuffer.allocateDirect(textureArray.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }
    
    public static abstract class OpenGLRunnable {
    	public abstract void run(GL10 gl);
    }
    
    
    /**
     * Pop runnables off the queue until time is up.
     * @param ms the amount of time to process queue elements for.
     */
    public static void processQueue(long ms) {
    	OpenGLRunnable r;
    	synchronized (mQueue) {
    		long start = SystemClock.uptimeMillis();
        	long now = start;
	    	while(true) {
	    		now = SystemClock.uptimeMillis();
	    		if (now > start + ms) {
	    			// Time is up for this frame.
	    			Log.d(TAG, "Times up " + mQueue.size() + " left.");
	    			return;
	    		}
	    		r = mQueue.poll();
	    		if (r == null) {
	    			// No more elements in the queue.
	    			return;
	    		}
	    		r.run(mGL);
	    	}
    	}
    }
    
    /**
     * Pop runnables off the queue until it is empty.
     */
    public static void processQueue() {
    	OpenGLRunnable r;
    	synchronized (mQueue) {
	    	while(true) {
	    		r = mQueue.poll();
	    		if (r == null) {
	    			// No more elements in the queue.
	    			return;
	    		}
	    		r.run(mGL);
	    	}
    	}
    }
    
    
    public static void postRunnable(OpenGLRunnable r) {
    	synchronized (mQueue) {
    		mQueue.add(r);
		}
    }
    
    public static final void setGL(GL10 gl) {
        mGL = gl;
    }
    
    public static final GL10 getGL() {
        return mGL;
    }
    
    public static int getTextureName() {
    	return mCurrentTexture;
    }
    
    public static void setTexture(GL10 gl, Texture texture) {
    	mCurrentTexture = texture.mTextureName;
    	gl.glEnable(GL10.GL_TEXTURE_2D);
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, mCurrentTexture);
    }
    
    public static void setTexture(int program, Texture texture) {
    	mCurrentTexture = texture.mTextureName;
    	
    	int mUseTexture = GLES20.glGetUniformLocation(program, "uHasTextureAttribute");
		GLES20.glUniform1i(mUseTexture, 1);
		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mCurrentTexture);
    }

    public void reset() {
        
    }
    
}
