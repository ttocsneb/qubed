package com.ttocsneb.qubed.game.powerups;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.ttocsneb.qubed.screen.GameScreen;

/**
 * The powerup system controls all powerups.
 * 
 * @author TtocsNeb
 *
 */
public class PowerupSystem extends EntitySystem {

	private GameScreen game;

	private Array<Powerup> objects;

	public PowerupSystem(GameScreen game) {
		this.game = game;
		objects = new Array<Powerup>();
	}

	/**
	 * Add a powerup to the game.
	 * 
	 * @param powerup
	 */
	public void addPowerup(Powerup powerup) {
		objects.add(powerup);
	}

	@Override
	public void update(float deltaTime) {
		game.batch.setColor(Color.WHITE);
		TextureRegion region;

		// Go through each powerup, and process it.
		for (Powerup powerup : objects) {

			// Run the powerup, if it is active.
			if (powerup.isActive()) {
				// start the powerup.
				if (!powerup.hasStarted()) powerup.begin();

				powerup.refresh(deltaTime);

				
			}
			
			// stop the powerup, and remove it from existance.
			if (powerup.isFinished()) {
				powerup.stop();
				objects.removeValue(powerup, true);
				powerup = null;
				continue;
			}

			// Draw the powerup icon if the parent object hasn't died yet.
			if (powerup.object != null) {
				region = powerup.getTexture();

				game.batch.draw(
						region.getTexture(),
						powerup.object.getPosition().x
								+ powerup.object.getCenter().x - 0.5f,
						powerup.object.getPosition().y
								+ powerup.object.getCenter().y - 0.5f, 0.5f,
						0.5f, 1, 1, powerup.object.getSize(),
						powerup.object.getSize(), powerup.object.getRotation(),
						region.getRegionX(), region.getRegionY(),
						region.getRegionWidth(), region.getRegionHeight(),
						false, false);
			}

		}
	}

}
