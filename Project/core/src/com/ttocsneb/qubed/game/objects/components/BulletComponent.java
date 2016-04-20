package com.ttocsneb.qubed.game.objects.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.ttocsneb.qubed.game.objects.GameObject;

/**
 * Component for bullets.
 * 
 * @author TtocsNeb
 *
 */
public class BulletComponent implements Component, GameObject {

	public Body body;

	public boolean die;
	
	public Vector2 position;

	public float velx;
	public float vely;
	public float rotation;

	public float scale = 1;
	
	public BulletComponent() {
		position = new Vector2();
	}
	
	
	@Override
	public Vector2 getPosition() {
		return position;
	}

	@Override
	public Vector2 getCenter() {
		return new Vector2();
	}

	@Override
	public float getRotation() {
		return rotation;
	}

	@Override
	public float getSize() {
		return scale;
	}

}
