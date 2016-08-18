package com.ttocsneb.qubed.game.powerups;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ttocsneb.qubed.game.objects.GameObject;

/**
 * The powerup Object.
 * 
 * @author TtocsNeb
 *
 */
public abstract class Powerup {

	private TextureRegion icon;

	private boolean active, start, die;

	private float time;
	private final float TIME;

	private GameObject object;
	
	public final PowerupSystem system;

	/**
	 * Create a new powerup.
	 * 
	 * @param object
	 * @param system the PowerUpSystem this belongs to.
	 * @param icon
	 * @param time
	 */
	public Powerup( PowerupSystem system, TextureRegion icon, float time) {
		this.icon = icon;

		this.system = system;
		
		this.time = time;
		TIME = time;

	}
	
	

	/**
	 * @return the texture to draw the powerup.
	 */
	TextureRegion getTexture() {
		return icon;
	}

	/**
	 * @return if the powerup is finished.
	 */
	boolean isFinished() {
		return die || time <= 0;
	}

	/**
	 * Activate the powerup.
	 */
	public void activate() {
		//activate the powerup
		if(system.activate(this)) {
			active = true;
		}
	}

	/**
	 * Remove the powerup, without activating it.
	 */
	public void remove() {
		die = true;
	}

	/**
	 * @return true if the powerup is active.
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @return true if the powerup has began.
	 */
	public boolean hasStarted() {
		return start;
	}

	/**
	 * @return Get the amount of time the powerup will run.
	 */
	public float getTimeLeft() {
		return time;
	}

	/**
	 * @return the progress of the powerup.
	 */
	public float getProgress() {
		return time / TIME;
	}

	/**
	 * start the Powerup.
	 */
	void begin() {
		if (start == false) start();
		start = true;
	}

	/**
	 * update the powerup. (when active)
	 * 
	 * @param delta
	 */
	void refresh(float delta) {
		time -= delta;
		if (start == true) update(delta);
	}

	/**
	 * Stop the powerup.
	 */
	void stop() {
		active = false;
		if (start == true) end();
	}

	/**
	 * Start the Powerup.
	 */
	public abstract void start();

	/**
	 * Update the powerup
	 * 
	 * <pre>
	 * <b>Note: this is only called when the powerup is activated.</b>
	 * </pre>
	 * 
	 * @param delta
	 */
	public abstract void update(float delta);

	/**
	 * End the Powerup.
	 */
	public abstract void end();

	/**
	 * Get the color that represents this powerup.
	 * @return Color
	 */
	public abstract Color getColor();

	
	public GameObject getObject() {
		return object;
	}
	
	public void setObject(GameObject object) {
		this.object = object;
	}

}
