package com.ttocsneb.qubed.game.contact;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
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
	
	private Array<Cont> beginContact;
	private Array<Cont> endContact;
	
	/**
	 * Create a new Contact Manager.
	 * @param contact the listeners to notify.
	 */
	public ContactManager(ContactListener... contact) {
		contactListeners = contact;
		beginContact = new Array<Cont>();
		endContact = new Array<Cont>();
	}
	
	private class Cont {
		private final Body body1;
		private final Body body2;
		
		private Cont(Body b1, Body b2) {
			body1 = b1;
			body2 = b2;
		}
	}
	
	/**
	 * This will notify the listeners when their component has begun/ended contact.
	 */
	public void update() {
		
		Object a;
		Object b;
		
		//Notify listeners for begin Contact.
		//go through every contact
		for(Cont c : beginContact) {
			a = c.body1.getUserData();
			b = c.body2.getUserData();
			//Go through every listener 
			for(ContactListener cl : contactListeners) {
				//Check if the contact was made by the selected listener, and notify it.
				if(a != null && a.getClass().equals(cl.getComponentType())) {
					cl.beginContact((Component) a, b);
				} else if(b != null && b.getClass().equals(cl.getComponentType())) {
					cl.beginContact((Component) b, a);
				}
			}
		}
		
		//Notify listeners for end Contact.
		//go through every contact
		for(Cont c : endContact) {
			a = c.body1.getUserData();
			b = c.body2.getUserData();
			//Go through every listener 
			for(ContactListener cl : contactListeners) {
				//Check if the contact was made by the selected listener, and notify it.
				if(a != null && a.getClass().equals(cl.getComponentType())) {
					cl.endContact((Component) a, b);
				} else if(b != null && b.getClass().equals(cl.getComponentType())) {
					cl.endContact((Component) b, a);
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
				beginContact.add(new Cont(contact.getFixtureA().getBody(), contact.getFixtureB().getBody()));
				return;
			} else if(b != null && b.getClass().equals(cl.getComponentType())) {
				beginContact.add(new Cont(contact.getFixtureA().getBody(), contact.getFixtureB().getBody()));
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
				beginContact.add(new Cont(contact.getFixtureA().getBody(), contact.getFixtureB().getBody()));
				return;
			} else if(b != null && b.getClass().equals(cl.getComponentType())) {
				beginContact.add(new Cont(contact.getFixtureA().getBody(), contact.getFixtureB().getBody()));
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
