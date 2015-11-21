package com.ttocsneb.qubed.game;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.ttocsneb.qubed.game.contact.ContactListener;
import com.ttocsneb.qubed.screen.GameScreen;
import com.ttocsneb.qubed.util.Assets;

public class CircleSystem extends EntitySystem implements ContactListener {
	
	private ImmutableArray<Entity> entities;
	private ComponentMapper<CircleComponent> cc = ComponentMapper.getFor(CircleComponent.class);
	
	private GameScreen game;
	private Engine engine;
	
	private ParticleEffectPool circleEffect;
	
	public CircleSystem(GameScreen gs) {
		game = gs;
		circleEffect = new ParticleEffectPool(Assets.instance.particles.circleExp, 1, 5);
		
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
	 * @Warning Not using this function without initializing a Body will result in the game crashing.
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
		cc.body.setUserData(cc);
		
		cc.body.setLinearVelocity(cc.velocity * MathUtils.cosDeg(cc.direction), cc.velocity * MathUtils.sinDeg(cc.direction));
		
		updateShape(cc, cc.scale);
	}


	

	@Override
	public void beginContact(Component object, Object object2) {
		CircleComponent cc = (CircleComponent) object;
		
		//Die if the Circle comes into contact with a bullet.
		if(object2 instanceof BulletComponent) {
			((BulletComponent) object2).die = true;
			cc.die = true;
			PooledEffect effect = circleEffect.obtain();
			effect.setPosition(cc.x, cc.y);
			effect.getEmitters().get(0).getTint().setColors(new float[]{cc.color.r, cc.color.g, cc.color.b});
			effect.getEmitters().get(0).getScale().setHigh(cc.scale);
			game.particle.addEffect(effect);
		}
	}
	
	@Override
	public void endContact(Component object, Object object2) {
		
	}

	@Override
	public Class<?> getComponentType() {
		return CircleComponent.class;
	}
	
}


