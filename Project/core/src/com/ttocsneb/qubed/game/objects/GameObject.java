package com.ttocsneb.qubed.game.objects;

import com.badlogic.gdx.math.Vector2;

/**
 * This is for physical objects in the game.
 * 
 * @author TtocsNeb
 *
 */
public interface GameObject {

	/**
	 * @return the position of the object
	 */
	public Vector2 getPosition();

	/**
	 * @return the center of the object
	 * 
	 *         <pre>
	 * <b>Note: This is relative to the position.</b>
	 * </pre>
	 */
	public Vector2 getCenter();

	/**
	 * @return the rotation of the object.
	 */
	public float getRotation();

	/**
	 * @return the size of the object.
	 */
	public float getSize();

}
