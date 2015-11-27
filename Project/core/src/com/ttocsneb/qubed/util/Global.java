package com.ttocsneb.qubed.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Global variables accessible by all classes.
 * 
 * @author TtocsNeb
 *
 */
public class Global {

	public static final float VIEWPORT_WIDTH = 4;
	public static final float VIEWPORT_GUI_HEIGHT = 7.111111f;

	public static final float VIEWPORT_GUI_WIDTH = 1080;
	public static final float VIEWPROT_GUI_HEIGHT = 1920;

	public static final String TEXTURE_ATLAS = "textures/textures.atlas";

	/**
	 * Contains the configurations for the application.
	 * 
	 * @author TtocsNeb
	 *
	 */
	public static class Config {

		public static Preferences prefs = Gdx.app.getPreferences("Cube");

		private static final String MUTE_ID = "mute";
		public static boolean MUTE = false;

		/**
		 * Load the configuration.
		 */
		public static void load() {
			MUTE = prefs.getBoolean(MUTE_ID, false);
		}

		public static void save() {
			prefs.putBoolean(MUTE_ID, MUTE);
			prefs.flush();
		}
	}

	public static boolean start = false;

}