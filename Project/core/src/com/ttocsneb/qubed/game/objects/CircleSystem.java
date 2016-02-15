package com.ttocsneb.qubed.game.objects;

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
import com.ttocsneb.qubed.game.objects.components.BulletComponent;
import com.ttocsneb.qubed.game.objects.components.CircleComponent;
import com.ttocsneb.qubed.screen.GameScreen;
import com.ttocsneb.qubed.util.Assets;

/**
 * All logic for Circles.
 * 
 * @author Ben
 *
 */
public class CircleSystem extends EntitySystem implements ContactListener {

	private ImmutableArray<Entity> entities;
	private ComponentMapper<CircleComponent> cc = ComponentMapper
			.getFor(CircleComponent.class);

	private GameScreen game;
	private Engine engine;

	public ParticleEffectPool circleEffect;

	public CircleSystem(GameScreen gs) {
		game = gs;
		circleEffect = new ParticleEffectPool(
				Assets.instance.particles.circleExp, 1, 5);

	}

	@Override
	@SuppressWarnings("unchecked")
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(CircleComponent.class)
				.get());
		this.engine = engine;
	}

	/**
	 * Update the fixture shape to the object. (This includes resizing.)
	 * 
	 * @param cc
	 *            The circle to update
	 * @param size
	 *            The size of the circle.
	 */
	private void updateShape(CircleComponent cc, float size) {
		//Remove any existing shapes.
		if (cc.fixture != null && cc.fixture.getBody() != null) {
			cc.body.destroyFixture(cc.fixture);
			cc.fixture = null;
		}

		//Don't try to create a shape, that shouldn't exist.
		if (size < 0.001f) {
			return;
		}

		//Make a fixture
		FixtureDef fdef = new FixtureDef();

		fdef.density = 0.1f;
		fdef.friction = 0.2f;
		fdef.restitution = 0.5f;

		//Create the shape.
		CircleShape shape = new CircleShape();
		shape.setRadius(size / 2f);
		shape.setPosition(new Vector2(0, 0));

		fdef.shape = shape;

		//Create the fixture.
		cc.fixture = cc.body.createFixture(fdef);

		//dispose of the shape.
		shape.dispose();

	}

	@Override
	public void update(float delta) {
		// Go through all circles.
		for (Entity entity : entities) {
			CircleComponent circle = cc.get(entity);

			// kill the circle if it deserves to DIE.
			if (circle.die) {
				circle.scale -= delta * 4;
				updateShape(circle, circle.scale);

				// Remove the body.
				if (circle.scale <= 0) {
					//remove the powerup if it has not already activated.
					if(circle.powerup != null && !circle.powerup.hasStarted()) {
						circle.powerup.remove();
					}
					
					engine.removeEntity(entity);
					game.world.destroyBody(circle.body);
					continue;
				}
			} else {
				// update the circles position; Note: I don't know why we use
				// this code.
				circle.position.x = circle.body.getPosition().x;
				circle.position.y = circle.body.getPosition().y;
			}

			// Set the color to draw the circle.
			game.shape.setColor(circle.color);

			// Draw the circle
			game.shape.circle(circle.position.x, circle.position.y, 0.5f * circle.scale, 25);

			// If the circle goes out of bounds, kill it.
			if (Math.pow(circle.position.x, 2) + Math.pow(circle.position.y, 2) >= 9f) {
				circle.die = true;
			}

		}
	}

	/**
	 * Add a Circle to the System.
	 * 
	 * <pre>
	 * <b>
	 * Warning: Not using this function without initializing a Body will result
	 *          in the game crashing.
	 * </b>
	 * </pre>
	 * 
	 * @param cc
	 */
	public void addCircle(CircleComponent cc) {
		// Create a new Circle entity.
		Entity e = new Entity();
		e.add(cc);
		engine.addEntity(e);

		// Make a body for the circle.
		BodyDef bdef = new BodyDef();

		bdef.type = BodyType.DynamicBody;
		bdef.position.set(cc.position.x, cc.position.y);

		//Create the body.
		cc.body = game.world.createBody(bdef);
		cc.body.setUserData(cc);

		//Set its velocity.
		cc.body.setLinearVelocity(cc.velocity * MathUtils.cosDeg(cc.direction),
				cc.velocity * MathUtils.sinDeg(cc.direction));
		cc.body.setAngularVelocity((MathUtils.randomBoolean() ? -1 : 1)
				* MathUtils.random(180, 360) * MathUtils.degreesToRadians);

		//Set the shape.
		updateShape(cc, cc.scale);
	}

	@Override
	public void beginContact(Component object, Object object2) {
		CircleComponent cc = (CircleComponent) object;

		// Kill the circle if it touches a bullet.
		if (object2 instanceof BulletComponent) {
			((BulletComponent) object2).die = true;
			
			if(cc.powerup != null) {
				cc.powerup.activate();
			}
			
			cc.die = true;
			PooledEffect effect = circleEffect.obtain();
			effect.setPosition(cc.position.x, cc.position.y);
			effect.getEmitters().get(0).getTint().setColors(new float[] {
					cc.color.r, cc.color.g, cc.color.b
			});
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
