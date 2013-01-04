package com.gamblore.androidpunk;

import net.androidpunk.FP;
import net.androidpunk.android.PunkActivity;
import android.os.Bundle;

public class AndroidPunkTestActivity extends PunkActivity {
	static {
        // This is how big the screen is targeted for, it will resize the display
        // to meet the device's size.
        PunkActivity.static_width = 800; 
        PunkActivity.static_height = 480;
        
        // Set this to a class that extends Engine
        PunkActivity.engine_class = Main.class; 
        
        // If you want fixed time between frames set these. Value is in frames per second. 
        //FP.fixed = true;
        //FP.assignedFrameRate = 30;
        
        // Turn on exceptions for opengl errors. Slows down render.
        //FP.debugOpenGL = true;
        
        //FP.debug = true;
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}