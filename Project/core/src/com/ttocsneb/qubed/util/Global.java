package com.ttocsneb.qubed.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.MathUtils;

/**
 * Global variables accessible by all classes.
 * 
 * @author TtocsNeb
 *
 */
public class Global  {

	public static final float VIEWPORT_WIDTH = 4;
	public static final float VIEWPORT_GUI_HEIGHT = 7.111111f;
	
	public static final String TEXTURE_ATLAS = "textures/textures.atlas";

	// These objects cannot be final as when the app is reloaded, 
	// after being closed by the back button, the objects are
	// lost and need to be reinitialized.
	public static SpriteBatch batch = new SpriteBatch();
	public static ShapeRenderer shape = new ShapeRenderer();
	
	public static final Color RED = new Color(218/255f, 67/255f, 32/255f, 1);
	public static final Color ORANGE = new Color(250/255f, 128/255f, 40/255f, 1);
	public static final Color BLUE = new Color(49/255f, 136/255f, 183/255f, 1);
	public static final Color GREEN = new Color(63/255f, 193/255f, 91/255f, 1);
	
	public static Assets assets = new Assets();
	
	/**
	 * initialize special objects that are lost after the app is reloaded.
	 */
	public static void init() {
		if(batch == null) batch = new SpriteBatch();
		if(shape == null) shape = new ShapeRenderer();
		if(assets == null) assets = new Assets();
	}
	
	private static EarClippingTriangulator ear = new EarClippingTriangulator();
	
	/**
	 * Fill a polygon into the {@link #shape} {@link ShapeRenderer}.
	 * @param verts pairs of vertices
	 * @return true if the polygon was successfully drawn.
	 */
	public static boolean fillPoly(float...verts) {
		//return false when the shapeRenderer is not active, or the verticies are not even.
		if(!shape.isDrawing() && (verts.length & 1) == 1)
			return false;

		//Find the indices to draw triangles without holes
		short[] v = ear.computeTriangles(verts, 0, verts.length).items;

		for(int i=0; i<v.length; i+=3) {
			//The size of the indice array may be bigger than the data inside.  exit the loop if there is no more data.
			if(v[i]==v[i+1] && v[i]==v[i+2]) {
				//Gdx.app.log("Global", "Finish");
				break;
			}
			//Gdx.app.log("Global", "v: " + verts[v[i]*2] + "," + verts[v[i]*2+1] + "," + verts[v[i+1]*2] + "," + verts[v[i+1]*2+1] + "," + verts[v[i+2]*2] + "," + verts[v[i+2]*2+1]);
			
			
			//Draw the triangle
			shape.triangle(verts[v[i]*2], verts[v[i]*2+1],
					verts[v[i+1]*2], verts[v[i+1]*2+1],
					verts[v[i+2]*2], verts[v[i+2]*2+1]);
			
			v[i] = 0;
			v[i+1] = 0;
			v[i+2] = 0;
		}
		
		
		return true;
	}
	
	/**
	 * Performs a linear interpolation
	 * 
	 * @param alpha
	 * @param start
	 * @param end
	 * @return
	 */
	public static float lerp(float alpha, float start, float end) {
		return (start + alpha * (end - start));
	}

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
		
		private static final String HIGHSCORE_ID = "high score";
		public static int HIGHSCORE = 0;

		/**
		 * Load the configuration.
		 */
		public static void load() {
			MUTE = prefs.getBoolean(MUTE_ID, false);
			HIGHSCORE = prefs.getInteger(HIGHSCORE_ID, 0);
		}

		public static void save() {
			prefs.putBoolean(MUTE_ID, MUTE);
			prefs.putInteger(HIGHSCORE_ID, HIGHSCORE);
			prefs.flush();
		}
	}

	public static boolean start = false;
	public static String version;

	public static void dispose() {
		batch.dispose();
		shape.dispose();
		assets.dispose();
		
		batch = null;
		shape = null;
		assets = null;
		
	}

	public static Color selectColor() {
		switch (MathUtils.random(3)) {
		case 0:
			return RED;
		case 1:
			return ORANGE;
		case 2:
			return BLUE;
		case 3:
			return GREEN;
		default:
			return new Color(Color.BLACK);
		}
	}

}