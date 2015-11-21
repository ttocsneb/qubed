package com.ttocsneb.qubed.game;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.ttocsneb.qubed.screen.GameScreen;

public class ParticleSystem extends EntitySystem implements Disposable {

	
	Array<PooledEffect> effects;
	GameScreen game;
	
	public ParticleSystem(GameScreen game) {
		effects = new Array<PooledEffect>();
		this.game = game;
	}

	public void addEffect(PooledEffect effect) {
		effects.add(effect);
	}
	
	@Override
	public void update(float delta) {
		for(PooledEffect e : effects) {
			e.draw(game.batch, delta);
			if(e.isComplete()) {
				effects.removeValue(e, true);
				e.free();
			}
		}
	}
	
	@Override
	public void dispose() {
		for(PooledEffect e : effects)
			e.free();
		effects.clear();
	}
	
}
