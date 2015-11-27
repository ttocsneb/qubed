package com.ttocsneb.qubed.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ttocsneb.qubed.android.R;

/**
 * The splash screen for the Android Version.
 * 
 * @author TtocsNeb
 *
 */
public class SplashScreen extends Activity {

	private static int SPLASH_TIME_OUT = 2750;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		new Handler().postDelayed(new Runnable() {

			/**
			 * Show the splash screen for 'SPLASH_TIME_OUT' amount of time, then
			 * start the main program.
			 */
			@Override
			public void run() {
				// Start the main activity
				Intent i = new Intent(SplashScreen.this, AndroidLauncher.class);
				startActivity(i);
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

				// Close this activity.
				finish();
			}

		}, SPLASH_TIME_OUT);
	}

}
