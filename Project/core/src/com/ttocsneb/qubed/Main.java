package com.ttocsneb.qubed;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.ttocsneb.qubed.screen.DirectedGame;
import com.ttocsneb.qubed.screen.MenuScreen;
import com.ttocsneb.qubed.screen.transitions.ScreenTransition;
import com.ttocsneb.qubed.screen.transitions.ScreenTransitionFade;
import com.ttocsneb.qubed.util.Assets;
import com.ttocsneb.qubed.util.Global;

/**
 * The Main entry Point for QUBED
 * 
 * @author TtocsNeb
 *
 */
public class Main extends DirectedGame {

	@Override
	public void create() {
		// Set the log level.
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		// Don't crash for missing variables in shaderPrograms.
		ShaderProgram.pedantic = false;

		// Load Settings
		Global.Config.load();

		// Init the assets.
		Assets.instance.init(new AssetManager());

		// Start playing music if turned on.
		Assets.instance.sounds.music.setLooping(true);
		if (!Global.Config.MUTE) {
			Assets.instance.sounds.music.play();
		}

		// Transition to the MenuScreen.
		ScreenTransition fade = ScreenTransitionFade.init(0.5f);
		setScreen(new MenuScreen(this), fade);

	}

}
