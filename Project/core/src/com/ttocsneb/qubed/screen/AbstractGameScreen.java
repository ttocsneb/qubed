package com.ttocsneb.qubed.screen;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.ttocsneb.qubed.util.Global;

public abstract class AbstractGameScreen implements Screen {
	protected DirectedGame game;
	
	public AbstractGameScreen(DirectedGame game) {
		this.game = game;
	}
	
	@Override
	public abstract void show();
	@Override
	public abstract void render(float delta);
	@Override
	public abstract void resize(int width, int height);
	@Override
	public abstract void pause();
	@Override
	public abstract void hide();

	@Override
	public void resume() {
		if(!Global.assets.isLoaded())
			Global.assets.init(new AssetManager());
	}

	@Override
	public void dispose() {
		Global.assets.dispose();
	}
	
	/**
	 * Get the Input processor.
	 * @return Input Processor
	 */
	public abstract InputProcessor getInputProcessor();
	
}

