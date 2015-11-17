package com.ttocsneb.qubed.game.contact;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;

/**
 * The Contact Manager is a {@link com.badlogic.gdx.physics.box2d.ContactListener} 
 * Which will notify the {@link com.ttocsneb.qubed.game.contact.ContactListener} 
 * of any contacts in which it claims.
 * @author TtocsNeb
 *
 */
public class ContactManager implements com.badlogic.gdx.physics.box2d.ContactListener {

	private final ContactListener[] contactListeners;
	
	private Array<Contact> beginContact;
	private Array<Contact> endContact;
	
	/**
	 * Create a new Contact Manager.
	 * @param contact the listeners to notify.
	 */
	public ContactManager(ContactListener... contact) {
		contactListeners = contact;
		beginContact = new Array<Contact>();
		endContact = new Array<Contact>();
	}
	
	/**
	 * This will notify the listeners when their component has begun/ended contact.
	 */
	public void update() {
		
		Component a;
		Component b;
		
		//Notify listeners for begin Contact.
		//go through every contact
		for(Contact c : beginContact) {
			a = (Component) c.getFixtureA().getBody().getUserData();
			b = (Component) c.getFixtureB().getBody().getUserData();
			//Go through every listener 
			for(ContactListener cl : contactListeners) {
				//Check if the contact was made by the selected listener, and notify it.
				if(a != null && a.getClass().equals(cl.getComponentType())) {
					cl.beginContact(a, b);
					continue;
				} else if(b != null && b.getClass().equals(cl.getComponentType())) {
					cl.beginContact(b, a);
					continue;
				}
			}
		}
		
		//Notify listeners for end Contact.
		//go through every contact
		for(Contact c : beginContact) {
			a = (Component) c.getFixtureA().getBody().getUserData();
			b = (Component) c.getFixtureB().getBody().getUserData();
			//Go through every listener 
			for(ContactListener cl : contactListeners) {
				//Check if the contact was made by the selected listener, and notify it.
				if(a != null && a.getClass().equals(cl.getComponentType())) {
					cl.endContact(a, b);
					continue;
				} else if(b != null && b.getClass().equals(cl.getComponentType())) {
					cl.endContact(b, a);
					continue;
				}
			}
		}
		
		//Clear the contact storage for the next routine
		beginContact.clear();
		endContact.clear();
	}
	
	@Override
	public void beginContact(Contact contact) {

		Object a = contact.getFixtureA().getBody().getUserData();
		Object b = contact.getFixtureB().getBody().getUserData();
		
		//Go through each listener.
		for(ContactListener cl : contactListeners) {
			//Check if the contact belongs to the selected listener.
			if(a != null && a.getClass().equals(cl.getComponentType())) {
				beginContact.add(contact);
				return;
			} else if(b != null && b.getClass().equals(cl.getComponentType())) {
				beginContact.add(contact);
				return;
			}
		}
		
		
	}

	@Override
	public void endContact(Contact contact) {
		Object a = contact.getFixtureA().getBody().getUserData();
		Object b = contact.getFixtureB().getBody().getUserData();

		//Go through each listener.
		for(ContactListener cl : contactListeners) {
			//Check if the contact belongs to the selected listener.
			if(a != null && a.getClass().equals(cl.getComponentType())) {
				endContact.add(contact);
				return;
			} else if(b != null && b.getClass().equals(cl.getComponentType())) {
				endContact.add(contact);
				return;
			}
		}
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		
	}

}
