package com.ttocsneb.qubed.game;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public class CubeComponent implements Component {

	public float x = 0;
	public float y = 0;

	public Body body;
	public Fixture fixture;
	
	public float direction;
	public float velocity;
	
	public float progress;
	public float rotation;
	
	public float scale = 1;
	
	public Color color;
	
	public boolean die;
}
