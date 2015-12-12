package com.ttocsneb.qubed.game.powerups;

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

	public final GameObject object;

	/**
	 * Create a new powerup.
	 * 
	 * @param object
	 * @param icon
	 * @param time
	 */
	public Powerup(GameObject object, TextureRegion icon, float time) {
		this.icon = icon;

		this.time = time;
		TIME = time;

		this.object = object;
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
		active = true;
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

}
