package com.ttocsneb.qubed.game;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;

public class BulletComponent implements Component {

	public Body body;
	
	public float x = 0;
	public float y = 0;

	public float velx;
	public float vely;
	public float rotation;
	
	public float scale = 1;
	
}
