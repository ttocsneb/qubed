package com.ttocsneb.qubed.game;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.ttocsneb.qubed.screen.GameScreen;

public class CircleSystem extends EntitySystem {
	
	private ImmutableArray<Entity> entities;
	private ComponentMapper<CircleComponent> cc = ComponentMapper.getFor(CircleComponent.class);
	
	private GameScreen game;
	private Engine engine;
	
	
	public CircleSystem(GameScreen gs) {
		game = gs;
		
		
	}
	
	@Override
	@SuppressWarnings("unchecked") 
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(CircleComponent.class).get());
		this.engine = engine;
	}
	
	@Override
	public void update(float delta) {
		float size, distance;
		
		for(int i=0; i<entities.size(); i++) {
			Entity entity = entities.get(i);
			CircleComponent circle = cc.get(entity);
			
			distance = (float)Math.sqrt(Math.pow(circle.x, 2) +Math.pow(circle.y, 2));
			
			if(circle.die) {
				circle.x = lerp(delta*4, circle.x, 0);
				circle.y = lerp(delta*4, circle.y, 0);
				circle.scale -= delta*4;
				if(circle.scale <= 0) {
					engine.removeEntity(entity);
				}
			} else {
				circle.x += circle.velocity * MathUtils.cosDeg(circle.direction) * delta * (1-distance/3f);
				circle.y += circle.velocity * MathUtils.sinDeg(circle.direction) * delta * (1-distance/3f);
			}
			
			size = circle.scale * (1-distance/3f);

			game.shape.setColor(circle.color);
			
			game.shape.circle(circle.x, circle.y, 0.5f*size, 25);
			
		}
	}
	

	private float lerp(float t, float a, float b) {
		return (a + t*(b-a));
	}
	
	
	
}


