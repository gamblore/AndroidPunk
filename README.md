AndroidPunk
===========

An Android port of the open-source game engine [FlashPunk](http://flashpunk.net/).

How-To
=====
This How-To pulls from example classes located in the sample folder. 

Create an Engine class, World class, etc.

    public class Main extends Engine {
    
        private static final String TAG = "Game";
        
        public Main(int width, int height, float frameRate, boolean fixed) {
            super(width, height, frameRate, fixed);
            FP.setWorld(new OgmoEditorWorld(R.raw.intro_1));
        }
    }

Then create a class that extends PunkActivity and set the following to whatever you want.

    public class MyAndroidPunkActivity extends PunkActivity {
        static {
            // This is how big the screen is targeted for, it will resize the display
            // to meet the device's size.
            PunkActivity.static_width = 800; 
            PunkActivity.static_height = 480;
            
            // Set this to a class that extends Engine that will be created when the screen is ready.
            PunkActivity.engine_class = Main.class; 
            
            // If you want fixed time between frames set these. Value is in frames per second. 
            //FP.fixed = true;
            //FP.assignedFrameRate = 30
        }
    }
    
Legal
============
This project uses Rihno for JavaScript. You can find it more information on it [here](https://developer.mozilla.org/en-US/docs/Rhino).