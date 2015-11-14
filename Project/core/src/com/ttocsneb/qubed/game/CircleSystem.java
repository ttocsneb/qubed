package com.ttocsneb.qubed.game;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.ttocsneb.qubed.screen.GameScreen;

public class CircleSystem extends EntitySystem {
	
	private ImmutableArray<Entity> entities;
	private ComponentMapper<CircleComponent> cc = ComponentMapper.getFor(CircleComponent.class);
	
	private ImmutableArray<Entity> bullets;
	private ComponentMapper<BulletComponent> bc = ComponentMapper.getFor(BulletComponent.class);
	
	private GameScreen game;
	private Engine engine;
	
	private PlayerSystem player;
	private PlayerSystem.Triangle playerMesh;
	
	public CircleSystem(GameScreen gs, PlayerSystem ps) {
		game = gs;
		player = ps;
		
		
	}
	
	@Override
	@SuppressWarnings("unchecked") 
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(CircleComponent.class).get());
		bullets = engine.getEntitiesFor(Family.all(BulletComponent.class).get());
		this.engine = engine;
	}
	
	@Override
	public void update(float delta) {
		float size, distance;
		
		playerMesh = player.getTriangle();
		
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
			
			if((pointinPlayer(new Vector2(circle.x + 0.5f*size * MathUtils.cosDeg(circle.direction), circle.y + 0.5f*size * MathUtils.sinDeg(circle.direction) )) || distance <= player.getSize()/2f) && !circle.die) {
				circle.die = true;
				player.damage(circle.scale*0.5f);
			}
			
		}
	}
	
	private float sign(Vector2 p1, Vector2 p2, Vector2 c) {
		return ((p2.x - p1.x)*(c.y-p1.y) - (p2.y - p1.y)*(c.x - p1.x));
	}
	
	private boolean pointinPlayer(Vector2 point) {
		
		boolean b1 = sign(playerMesh.a, playerMesh.b, point) < 0f;
		boolean b2 = sign(playerMesh.b, playerMesh.c, point) < 0f;
		boolean b3 = sign(playerMesh.c, playerMesh.a, point) < 0f;
		
		return ((b1 == b2) && (b2 == b3));
		
	}

	private float lerp(float t, float a, float b) {
		return (a + t*(b-a));
	}
	
	
	
}


