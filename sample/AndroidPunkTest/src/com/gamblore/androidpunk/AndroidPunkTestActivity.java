package com.gamblore.androidpunk;

import net.androidpunk.FP;
import net.androidpunk.android.PunkActivity;
import android.os.Bundle;

public class AndroidPunkTestActivity extends PunkActivity {
	static {
		PunkActivity.static_width = 800;
		PunkActivity.static_height = 480;
		PunkActivity.engine_class = Main.class;
		//FP.fixed = true;
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}