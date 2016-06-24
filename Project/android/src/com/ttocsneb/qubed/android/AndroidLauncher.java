package com.ttocsneb.qubed.android;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.ttocsneb.qubed.Main;

/**
 * The main launcher for android
 * 
 * @author TtocsNeb
 *
 */
public class AndroidLauncher extends AndroidApplication {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		config.useAccelerometer = true;
		config.useCompass = false;

		config.numSamples = 2;

		initialize(new Main(getString(R.string.Version)), config);

		// Hide any virtual buttons if the OS is KITKAT or above.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			hideVirtualButtons();
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (hasFocus) {
			// Hide any virtual buttons if the OS is KITKAT or above.
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				hideVirtualButtons();
			}
		}
	}

	/**
	 * Hide the virtual buttons.
	 */
	@TargetApi(19)
	private void hideVirtualButtons() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
						| View.SYSTEM_UI_FLAG_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
	}
}
