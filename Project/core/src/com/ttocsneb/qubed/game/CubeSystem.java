package com.ttocsneb.qubed.game;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.ttocsneb.qubed.game.contact.ContactListener;
import com.ttocsneb.qubed.screen.GameScreen;

public class CubeSystem extends EntitySystem implements ContactListener{
    private ImmutableArray<Entity> entities;

	private ComponentMapper<CubeComponent> cc = ComponentMapper.getFor(CubeComponent.class);
	
	private GameScreen game;
	private Engine engine;
	
	private float interpolation;
	
	private Vector2 a, b, c, d;
	
	public CubeSystem(GameScreen gs) {
		game = gs;
		interpolation = 0;
		
		a = new Vector2();
		b = new Vector2();
		c = new Vector2();
		d = new Vector2();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(CubeComponent.class).get());
		this.engine = engine;
	}
	
	@Override
	public void update(float delta) {
		//float size, distance;
		
		for(int i=0; i<entities.size(); i++) {
			Entity entity = entities.get(i);
			CubeComponent cube = cc.get(entity);
			
			//distance = (float)Math.sqrt(Math.pow(cube.x, 2) + Math.pow(cube.y, 2));
			
			if(cube.die) {
				//cube.x = lerp(delta*4, cube.x, 0);
				//cube.y = lerp(delta*4, cube.y, 0);
				cube.scale -= delta*4;
				updateShape(cube, cube.scale);
				if(cube.scale <= 0) {
					game.world.destroyBody(cube.body);
					engine.removeEntity(entity);
					continue;
				}
			} else {
				cube.x = cube.body.getPosition().x;
				cube.y = cube.body.getPosition().y;
			}
			
			//size = cube.scale * (1-distance/3f);
			
			
			/*if(cube.rotation) {
				cube.progress += delta*4;
			
				if(cube.progress >= 1) {
					cube.progress = 0;
				}
			} else {
				cube.progress -= delta*4;
			
				if(cube.progress <= 0) {
					cube.progress = 1;
				}
			}*/
			
			cube.rotation = (cube.body.getAngle() * MathUtils.radiansToDegrees); 
			
			//interpolation = Interpolation.sine.apply(cube.progress);

			game.shape.setColor(cube.color);
			
			//As the shape renderer does not support filled polygons, Draw two triangles to form the custom square.
			
			/*a.set(cube.x-cube.scale/2f, cube.y-cube.scale/2f);
			b.set(cube.x+cube.scale/2f, cube.y-cube.scale/2f);
			c.set(cube.x+cube.scale/2f, cube.y+cube.scale/2f);
			d.set(cube.x-cube.scale/2f, cube.y+cube.scale/2f);*/
			
			game.shape.rect(cube.x-cube.scale/2f, cube.y-cube.scale/2f, cube.scale/2f, cube.scale/2f, cube.scale,
					cube.scale, 1, 1, cube.rotation);
			
			/*game.shape.triangle(
					a.x, a.y,
					b.x, b.y,
					c.x, c.y);
			
			game.shape.triangle(
					c.x, c.y,
					d.x, d.y,
					a.x, a.y);*/

			if(Math.pow(cube.x, 2) + Math.pow(cube.y, 2) >= 9f) {
				cube.die = true;
			}
			
		}
	}
	
	private void updateShape(CubeComponent cc, float size) {
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
		
		PolygonShape shape = new PolygonShape();
		shape.set(new float[] {-cc.scale/2f, -cc.scale/2f,
				-cc.scale/2f, cc.scale/2f,
				cc.scale/2f, -cc.scale/2f,
				cc.scale/2f, cc.scale/2f
		});
		fdef.shape = shape;
		
		cc.fixture = cc.body.createFixture(fdef);
		
		shape.dispose();
	}

	/**
	 * Add a Cube to the System.
	 * 
	 * @Warning Not using this function without initializing a Body will result in the game crashing.
	 * 
	 * @param cc
	 */
	public void addCube(CubeComponent cc) {
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
	
	private float lerp(float t, float a, float b) {
		return (a + t*(b-a));
	}

	@Override
	public Class<?> getComponentType() {
		return CubeComponent.class;
	}

	@Override
	public void beginContact(Component object, Object object2) {
		Gdx.app.debug("CubeSystem", "Begin Contact");
	}

	@Override
	public void endContact(Component object, Object object2) {
		Gdx.app.debug("CubeSystem", "End Contact");
		
	}

	
}
