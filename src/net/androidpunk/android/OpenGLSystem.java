package net.androidpunk.android;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import android.util.Log;

public class OpenGLSystem {
	
	private static final String TAG = "OpenGLSystem";
	
	private static FloatBuffer textureBuffer;
	private static FloatBuffer vertexBuffer;
	
	private static final float vertexArray[] = new float[8];
	private static final float textureArray[] = new float[8];
	
	private static GL10 sGL;
    
    public OpenGLSystem() {
        this(null);
    }

    public OpenGLSystem(GL10 gl) {
        sGL = gl;
        textureBuffer = ByteBuffer.allocateDirect(vertexArray.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();;
        vertexBuffer = ByteBuffer.allocateDirect(textureArray.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();;
    }

    public static final void setGL(GL10 gl) {
        sGL = gl;
    }
    
    public static final GL10 getGL() {
        return sGL;
    }
    
    public static final void drawTexture(GL10 gl, int x, int y, int w, int h, Texture texture) {
    	gl.glEnable(GL10.GL_TEXTURE_2D);
    	
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.name);
    		
      	vertexArray[0] = x; vertexArray[1] = y;
    	vertexArray[2] = x + w; vertexArray[3] = y;
    	vertexArray[4] = x; vertexArray[5] = y + h;
    	vertexArray[6] = x + w; vertexArray[7] = y + h;
    	vertexBuffer.put(vertexArray).position(0);
    	
    	texture.setTexCoords(textureArray);
    	textureBuffer.put(textureArray).position(0);
    	
    	gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    	gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
    	gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    	gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);
    	
    	gl.glColor4f(texture.red, texture.green, texture.blue, texture.alpha);
    	
    	gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
    	gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }
    
    public void reset() {
        
    }
    
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
}
