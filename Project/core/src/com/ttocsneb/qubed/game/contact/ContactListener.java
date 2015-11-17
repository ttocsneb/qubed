package com.ttocsneb.qubed.game.contact;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * 
 * 	Listener for the Contact Manager.  This will listen for interactions your claimed bodies make with other bodies.
 * 
 * 	Set {@link Body#setUserData(Object)} with an object you own to listen for that body. 
 * 
 * 	To claim an object, return the object in {@link #getComponentType()}
 * 
 * @author TtocsNeb
 *
 */
public interface ContactListener{
	
	/**
	 * @return Claimed Class. 
	 */
	public Class<?> getComponentType();
	
	/**
	 * Called when an owned object makes contact with another object
	 * @param object The Component that the class owns
	 * @param object2 The Component the body made contact with.
	 */
	public void beginContact(Component object, Object object2);
	
	/**
	 * Called when an owned object ends contact with another object
	 * @param object The Component that the class owns
	 * @param object2 The Component the body ended contact with.
	 */
	public void endContact(Component object, Object object2);
	
}
