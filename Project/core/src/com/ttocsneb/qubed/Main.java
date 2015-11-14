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


public class Main extends DirectedGame {
	
	
	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		ShaderProgram.pedantic = false;
		
		Global.Config.load();

		Assets.instance.init(new AssetManager());
		
		Assets.instance.sounds.music.setLooping(true);
		
		if(!Global.Config.MUTE) {
			Assets.instance.sounds.music.play();
		}
		
		ScreenTransition fade = ScreenTransitionFade.init(0.5f); 
		
		setScreen(new MenuScreen(this), fade);
		
	}
	
	
}
