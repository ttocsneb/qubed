package com.ttocsneb.qubed.game.powerups;

import com.ttocsneb.qubed.game.objects.GameObject;
import com.ttocsneb.qubed.game.objects.PlayerSystem;
import com.ttocsneb.qubed.util.Assets;


public class HealthPowerup extends Powerup {

	private PlayerSystem player;
	
	public HealthPowerup(GameObject object, float time, PlayerSystem player) {
		super(object, Assets.instance.textures.healthPowerup, time);
		
		this.player = player;
	}

	@Override
	public void start() {
		
	}

	@Override
	public void update(float delta) {
		player.damage(-0.5f*delta);
	}

	@Override
	public void end() {
		
	}

}
