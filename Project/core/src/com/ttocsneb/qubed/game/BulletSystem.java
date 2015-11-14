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

public class BulletSystem extends EntitySystem {
	
	private ImmutableArray<Entity> entities;
	
	private ComponentMapper<BulletComponent> bc = ComponentMapper.getFor(BulletComponent.class);

	private GameScreen game;
	
	public BulletSystem(GameScreen gs) {
		game = gs;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(BulletComponent.class).get());
		
	}
	
	@Override
	public void update(float delta) {
		
		Vector2 a = new Vector2(), b = new Vector2(), c = new Vector2();
		
		for(int i=0; i<entities.size(); i++) {
			Entity entity = entities.get(i);
			BulletComponent bullet = bc.get(entity);
			
			bullet.x += bullet.velx * delta;
			bullet.y += bullet.vely * delta;
			
			a.set(bullet.scale/2f*MathUtils.sinDeg(bullet.rotation), bullet.scale/2f*MathUtils.cosDeg(bullet.rotation));
			b.set(a.x*MathUtils.cosDeg(240)-(a.y*MathUtils.sinDeg(240)), a.x*MathUtils.sinDeg(240)+(a.y*MathUtils.cosDeg(240)));
			c.set(a.x*MathUtils.cosDeg(120)-(a.y*MathUtils.sinDeg(120)), a.x*MathUtils.sinDeg(120)+(a.y*MathUtils.cosDeg(120)));
			
			game.shape.triangle(
					bullet.x + a.x, bullet.y + a.y, 
					bullet.x + b.x, bullet.y + b.y, 
					bullet.x + c.x, bullet.y + c.y);
		}
	}
	
}
