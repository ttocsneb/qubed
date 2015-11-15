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
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
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
	
	/**
	 * Update the fixture shape to the object.  (This includes resizing.)
	 * @param cc The circle to update
	 * @param size The size of the circle.
	 */
	private void updateShape(CircleComponent cc, float size) {
		if(cc.fixture != null && cc.fixture.getBody() != null) {
			cc.body.destroyFixture(cc.fixture);
		}
		
		if(size <= 0) {
			return;
		}
		
		FixtureDef fdef = new FixtureDef();
		
		fdef.density = 0.1f;
		fdef.friction = 0.2f;
		fdef.restitution = 0.5f;
		
		CircleShape shape = new CircleShape();
		shape.setRadius(size/2f);
		shape.setPosition(new Vector2(0, 0));
		
		fdef.shape = shape;
		
		cc.fixture = cc.body.createFixture(fdef);
		
		shape.dispose();
		
	}
	
	@Override
	public void update(float delta) {
		for(int i=0; i<entities.size(); i++) {
			Entity entity = entities.get(i);
			CircleComponent circle = cc.get(entity);
			
			
			if(circle.die) {
				//circle.x = lerp(delta*4, circle.x, 0);
				//circle.y = lerp(delta*4, circle.y, 0);
				//circle.body.setTransform(circle.x, circle.y, 0);
				circle.scale -= delta*4;
				updateShape(circle, circle.scale);
				
				if(circle.scale <= 0) {
					engine.removeEntity(entity);
					game.world.destroyBody(circle.body);
					continue;
				}
			} else {
				circle.x = circle.body.getPosition().x;
				circle.y = circle.body.getPosition().y;
			}
			
			//size = circle.scale * (1-distance/3f);
			

			game.shape.setColor(circle.color);
			
			game.shape.circle(circle.x, circle.y, 0.5f*circle.scale, 25);
			
			if(Math.pow(circle.x, 2) + Math.pow(circle.y, 2) >= 9f) {
				circle.die = true;
			}
			
		}
	}
	

	@SuppressWarnings("unused")
	private float lerp(float t, float a, float b) {
		return (a + t*(b-a));
	}
	
	/**
	 * Add a Circle to the System.  
	 * 
	 * @Note Not using this function without initializing a Body will result in the game crashing.
	 * 
	 * @param cc
	 */
	public void addCircle(CircleComponent cc) {
		Entity e = new Entity();
		e.add(cc);
		engine.addEntity(e);
		
		BodyDef bdef = new BodyDef();
		
		bdef.type = BodyType.DynamicBody;
		bdef.position.set(cc.x, cc.y);
		
		cc.body = game.world.createBody(bdef);
		
		cc.body.setLinearVelocity(cc.velocity * MathUtils.cosDeg(cc.direction), cc.velocity * MathUtils.sinDeg(cc.direction));
		
		updateShape(cc, cc.scale);
	}
	
}


