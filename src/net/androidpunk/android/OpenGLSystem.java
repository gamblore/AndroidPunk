package net.androidpunk.android;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.graphics.opengl.Texture;
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
    /*
    public static final void drawTexture(GL10 gl, int x, int y, int w, int h, Texture texture) {
    	if (!texture.isDrawable()) {
    		return;
    	}
    	gl.glEnable(GL10.GL_TEXTURE_2D);
    	
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.name);
    		
      	vertexArray[0] = x; vertexArray[1] = y;
    	vertexArray[2] = x + w; vertexArray[3] = y;
    	vertexArray[4] = x; vertexArray[5] = y + h;
    	vertexArray[6] = x + w; vertexArray[7] = y + h;
    	vertexBuffer.put(vertexArray).position(0);
    	
    	if (texture.isRepeating()) {
    		texture.setTexCoords(textureArray, w, h);
    	} else {
    		texture.setTexCoords(textureArray);
    	}
    	textureBuffer.put(textureArray).position(0);

    	if (texture.name == -1) {
    		gl.glDisable(GL10.GL_TEXTURE_2D);
    	} else {
    		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
    	}
    	gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    	gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);
    	
    	gl.glColor4f(texture.red, texture.green, texture.blue, texture.alpha);
    	
    	gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
    	gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }
    */
    public void reset() {
        
    }
    
    /*
    private static String getGlErrStr(int error) {
    	switch (error) {
    	case GL10.GL_NO_ERROR:           return "GL_NO_ERROR";
    	case GL10.GL_INVALID_ENUM:       return "GL_INVALID_ENUM";
    	case GL10.GL_INVALID_OPERATION:  return "GL_INVALID_OPERATION";
    	case GL10.GL_STACK_OVERFLOW:     return "GL_STACK_OVERFLOW";
    	case GL10.GL_STACK_UNDERFLOW:    return "GL_STACK_UNDERFLOW";
    	case GL10.GL_OUT_OF_MEMORY:      return "GL_OUT_OF_MEMORY";
    	default:                         return String.format("Unknown GL error code 0x%x", error);
    	}
    }
    
    public static void checkGLError(GL10 gl) {
    	int error = gl.glGetError();
    	if (error != GL10.GL_NO_ERROR) {
    		Log.w(TAG, String.format("GL error: %s", getGlErrStr(error)));
    		Thread.dumpStack();
    	}
    }
    */
}
