package com.ttocsneb.qubed;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.ttocsneb.qubed.screen.AbstractGameScreen;
import com.ttocsneb.qubed.screen.DirectedGame;
import com.ttocsneb.qubed.screen.MenuScreen;
import com.ttocsneb.qubed.screen.transitions.ScreenTransition;
import com.ttocsneb.qubed.screen.transitions.ScreenTransitionFade;
import com.ttocsneb.qubed.util.Global;

/**
 * The Main entry Point for QUBED
 * 
 * @author TtocsNeb
 *
 */
public class Main extends DirectedGame {
	String Version;
	
	public Main(String version) {
		Version = version;
	}
	
	@Override
	public void create() {
		// Set the log level.
		//Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		Global.version = Version;
		Gdx.app.debug("Main", Global.version);
		

		// Don't crash for missing variables in shaderPrograms.
		ShaderProgram.pedantic = false;

		Global.init();
		
		// Load Settings
		Global.Config.load();
		
		
		
		// Init the assets.
		Global.assets.init(new AssetManager());
		
		//"Temporarily" skip the loading screen as it takes a negligible amount of time.
		Global.assets.finishLoading();
		ScreenTransition fade = ScreenTransitionFade.init(0.5f);
		setScreen(new MenuScreen(this), fade);

		//start the loading screen
		//setScreen(new LoadScreen(this));

	}
	
	@Override
	public void pause() {
		super.pause();
		Gdx.app.debug("Main", "PAUSE!!");
	}
	
	@Override
	public void resume() {
		super.pause();
		Gdx.app.debug("Main", "UNPAUSE!!!");
	}

	/**
	 * The loading screen, displayed at the start of the game.
	 * @author TtocsNeb
	 *
	 */
	@SuppressWarnings("unused")//This is not used, but may be used later in development if loading time takes to long.
	private class LoadScreen extends AbstractGameScreen {

		private float prog;
		private float time;
		
		private boolean loaded;

		private DirectedGame game;

		private OrthographicCamera cam;

		public LoadScreen(DirectedGame game) {
			super(game);
			this.game = game;
		}

		@Override
		public void show() {
			cam = new OrthographicCamera();
			cam.setToOrtho(false, 1080, 1920);
		}

		@Override
		public void render(float delta) {
			// Clear the screen.
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT
					| GL20.GL_DEPTH_BUFFER_BIT
					| (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV
							: 0));

			//Get the loading progress.
			float tmp = Global.assets.getProgress();

			//linearly interpolate the progress, to make it look pretty.
			prog = Math.min(tmp, Global.lerp(time += 1 / 20f, 0, 1));

			//Draw the background/loading bar.
			Global.shape.setProjectionMatrix(cam.combined);
			Global.shape.begin(ShapeType.Filled);
			
			Global.shape.setColor(Color.BLACK);
			Global.shape.rect(0, 0, 1080, 1920);

			Global.shape.setColor(Color.WHITE);
			Global.shape.rect(270, 940, 540 * prog, 40);

			Global.shape.end();
			
			//Draw the loading bar outline.

			Global.shape.begin(ShapeType.Line);
			Global.shape.rect(270, 940, 540, 40);
			Global.shape.end();

			//When the assets load, start the game.
			if (!loaded && Global.assets.isLoaded() && prog >= 1.0) {
				loaded = true;
				// Start playing music if turned on.
				Global.assets.sounds.music.setLooping(true);
				if (!Global.Config.MUTE) {
					Global.assets.sounds.music.play();
				}

				// Transition to the MenuScreen.
				ScreenTransition fade = ScreenTransitionFade.init(0.5f);
				setScreen(new MenuScreen(game), fade);
				return;
			} else {
				Gdx.app.debug(
						"Main",
						"Prog: " + prog);
			}
		}

		@Override
		public void resize(int width, int height) {

		}

		@Override
		public void pause() {

		}

		@Override
		public void hide() {

		}

		@Override
		public InputProcessor getInputProcessor() {
			return null;
		}

	}

	@Override
	public void dispose() {
		super.dispose();
		Global.dispose();
		Gdx.app.debug("Main", "Destroyed!!!!!!!@$3");
	}
}
