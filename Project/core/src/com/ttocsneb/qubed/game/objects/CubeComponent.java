package com.ttocsneb.qubed.game.objects;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.ttocsneb.qubed.game.powerups.HealthPowerup;

/**
 * Component for Cubes.
 * 
 * @author Ben
 *
 */
public class CubeComponent implements Component, GameObject {

	public Vector2 position;

	public Body body;
	public Fixture fixture;

	public float direction;
	public float velocity;

	public float progress;
	public float rotation;

	public float scale = 1;

	public Color color;

	public boolean die;
	public boolean killed;

	public HealthPowerup powerup;

	public CubeComponent() {
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
