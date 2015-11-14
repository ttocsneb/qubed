package com.ttocsneb.qubed.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

public class Global {

	public static final float VIEWPORT_WIDTH = 10f;
	public static final float VIEWPORT_HEIGHT = 5.625f;
	
	public static final float VIEWPORT_SCALE = 128f;
	
	public static final float VIEWPORT_GUI_WIDTH = 1920;
	public static final float VIEWPROT_GUI_HEIGHT = 1080;
	
	public static final String TEXTURE_ATLAS = "textures/textures.atlas";
	
	private static final String SKIN_UI = "skins/uiskin.json";
	
	public static final Stage stage = new Stage(new ScalingViewport(Scaling.stretch, 1920, 1080));
	public static final Skin skin = new Skin(Gdx.files.internal(Global.SKIN_UI), Assets.instance.atlases.uiskin);
	
	/**
	 * Contains the configurations for the application.
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