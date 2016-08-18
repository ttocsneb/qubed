package com.ttocsneb.qubed.game.powerups;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
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
	
	private Powerup active;

	public PowerupSystem(GameScreen game) {
		this.game = game;
		objects = new Array<Powerup>();
	}

	/**
	 * Get the color of the active powerups.
	 * @return Color
	 */
	public Color getColor() {
		
		return (active != null && active.isActive()) ? active.getColor() : Color.BLACK;
	}
	
	/**
	 * Get the active Texture icon.
	 * @return texture
	 */
	public TextureRegion getTexture() {
		return (active != null && active.isActive()) ? active.getTexture() : null;
	}
	
	/**
	 * Add a powerup to the game.
	 * 
	 * @param powerup
	 * @return true if successful
	 */
	public boolean addPowerup(Powerup powerup) {
		if(powerup.getObject() == null) return false;
		Gdx.app.debug("PowerupSystem", "Added powerup! size is now " + objects.size);
		objects.add(powerup);
		return true;
	}

	@Override
	public void update(float deltaTime) {
		game.batch.setColor(Color.WHITE);
		TextureRegion region;
		
		//check if the active powerup is active
		if(active != null && active.isActive()) {
			//start the powerup if it hasn't already started yet.
			if(!active.hasStarted()) active.begin();
			//update the powerup.
			active.refresh(deltaTime);
			
			region = active.getTexture();
			
		}

		// Go through each powerup, and process it.
		for (Powerup powerup : objects) {
			
			// stop the powerup, and remove it from existance.
			if (powerup.isFinished()) {
				powerup.stop();
				objects.removeValue(powerup, true);
				powerup = null;
				Gdx.app.debug("PowerupSystem", "Removed powerup! Size is now " + objects.size);
				continue;
			}

			// Draw the powerup icon if the parent object hasn't died yet.
			if (powerup.getObject() != null) {
				region = powerup.getTexture();

				game.batch.draw(
						region.getTexture(),
						powerup.getObject().getPosition().x
								+ powerup.getObject().getCenter().x - 0.5f,
						powerup.getObject().getPosition().y
								+ powerup.getObject().getCenter().y - 0.5f, 0.5f,
						0.5f, 1, 1, powerup.getObject().getSize(),
						powerup.getObject().getSize(), powerup.getObject().getRotation(),
						region.getRegionX(), region.getRegionY(),
						region.getRegionWidth(), region.getRegionHeight(),
						false, false);
			}

		}
	}
	
	/**
	 * Activate a powerup
	 * 
	 * <br><br><b>Note</b>: this should only be called by {@link Powerup}.
	 * 
	 * @param powerup the powerup to activate
	 * @return whether the powerup has been activated.
	 */
	public boolean activate(Powerup powerup) {
		if(active != null && active.isActive())
			return false;
		active = powerup;
		return true;
	}

}
