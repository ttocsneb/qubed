package com.ttocsneb.qubed.game;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

public class CubeComponent implements Component {

	public float x = 0;
	public float y = 0;
	
	public float direction;
	public float velocity;
	
	public float progress;
	public boolean rotation;
	
	public float scale = 1;
	
	public Color color;
	
	public boolean die;
}
