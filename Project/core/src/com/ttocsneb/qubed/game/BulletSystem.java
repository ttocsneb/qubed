package com.ttocsneb.qubed.game;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.ttocsneb.qubed.screen.GameScreen;

public class BulletSystem extends EntitySystem {
	
	private ImmutableArray<Entity> entities;
	
	private ComponentMapper<BulletComponent> bc = ComponentMapper.getFor(BulletComponent.class);

	private GameScreen game;
	
	private Vector2 a, b, c;
	
	public BulletSystem(GameScreen gs) {
		game = gs;
	}
	
	private Engine engine;
	
	@Override
	@SuppressWarnings("unchecked")
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(BulletComponent.class).get());
		this.engine = engine;
		a = new Vector2();
		b = new Vector2();
		c = new Vector2();
	}
	
	@Override
	public void update(float delta) {
		
		
		
		for(int i=0; i<entities.size(); i++) {
			Entity entity = entities.get(i);
			BulletComponent bullet = bc.get(entity);
			
			bullet.x = bullet.body.getPosition().x;
			bullet.y = bullet.body.getPosition().y;
			
			a.set(bullet.scale/2f*MathUtils.sinDeg(bullet.rotation), bullet.scale/2f*MathUtils.cosDeg(bullet.rotation));
			b.set(a.x*MathUtils.cosDeg(240)-(a.y*MathUtils.sinDeg(240)), a.x*MathUtils.sinDeg(240)+(a.y*MathUtils.cosDeg(240)));
			c.set(a.x*MathUtils.cosDeg(120)-(a.y*MathUtils.sinDeg(120)), a.x*MathUtils.sinDeg(120)+(a.y*MathUtils.cosDeg(120)));
			
			game.shape.triangle(
					bullet.x + a.x, bullet.y + a.y, 
					bullet.x + b.x, bullet.y + b.y, 
					bullet.x + c.x, bullet.y + c.y);
		}
	}
	
	public void addBullet(BulletComponent bc) {
		Entity e = new Entity();
		e.add(bc);
		engine.addEntity(e);
		
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.DynamicBody;
		bdef.position.set(bc.x, bc.y);
		bdef.angle = (360-bc.rotation) * MathUtils.degreesToRadians;
		
		bc.body = game.world.createBody(bdef);
		
		FixtureDef fdef = new FixtureDef();
		
		fdef.density = 1;
		fdef.friction = 0.7f;
		fdef.restitution = 0.2f;
		
		PolygonShape shape = new PolygonShape();
		
		a.set(bc.scale/2f*MathUtils.sinDeg(bc.rotation), bc.scale/2f*MathUtils.cosDeg(bc.rotation));
		b.set(a.x*MathUtils.cosDeg(240)-(a.y*MathUtils.sinDeg(240)), a.x*MathUtils.sinDeg(240)+(a.y*MathUtils.cosDeg(240)));
		c.set(a.x*MathUtils.cosDeg(120)-(a.y*MathUtils.sinDeg(120)), a.x*MathUtils.sinDeg(120)+(a.y*MathUtils.cosDeg(120)));
		
		shape.set(new float[]{
				a.x, a.y,
				b.x, b.y,
				c.x, c.y
		});
		
		fdef.shape = shape;
		
		bc.body.createFixture(fdef);
		
		bc.body.setLinearVelocity(new Vector2(bc.velx, bc.vely));
		
	}
	
}
