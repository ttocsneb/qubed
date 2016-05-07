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
import com.ttocsneb.qubed.game.powerups.HealthPowerup;
import com.ttocsneb.qubed.game.powerups.SlowPowerup;
import com.ttocsneb.qubed.game.spawn.Spawn;
import com.ttocsneb.qubed.screen.GameScreen;
import com.ttocsneb.qubed.util.Assets;
import com.ttocsneb.qubed.util.Global;

/**
 * All logic for Circles.
 * 
 * @author Ben
 *
 */
public class CircleSystem extends EntitySystem implements ContactListener, Spawn {

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
		// Remove any existing shapes.
		if (cc.fixture != null && cc.fixture.getBody() != null) {
			cc.body.destroyFixture(cc.fixture);
			cc.fixture = null;
		}

		// Don't try to create a shape, that shouldn't exist.
		if (size < 0.001f) {
			return;
		}

		// Make a fixture
		FixtureDef fdef = new FixtureDef();

		fdef.density = 0.1f;
		fdef.friction = 0.2f;
		fdef.restitution = 0.5f;

		// Create the shape.
		CircleShape shape = new CircleShape();
		shape.setRadius(size / 2f);
		shape.setPosition(new Vector2(0, 0));

		fdef.shape = shape;

		// Create the fixture.
		cc.fixture = cc.body.createFixture(fdef);

		// dispose of the shape.
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
					// remove the powerup if it has not already activated.
					if (circle.powerup != null && !circle.powerup.hasStarted()) {
						circle.powerup.remove();
					}

					if (circle.killed)
						game.addScore((int) (circle.getSize() * 100f) + 10);

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
			game.shape.circle(circle.position.x, circle.position.y,
					0.5f * circle.scale, 25);

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

		// Create the body.
		cc.body = game.world.createBody(bdef);
		cc.body.setUserData(cc);

		// Set its velocity.
		cc.body.setLinearVelocity(cc.velocity * MathUtils.cosDeg(cc.direction),
				cc.velocity * MathUtils.sinDeg(cc.direction));
		cc.body.setAngularVelocity((MathUtils.randomBoolean() ? -1 : 1)
				* MathUtils.random(180, 360) * MathUtils.degreesToRadians);

		// Set the shape.
		updateShape(cc, cc.scale);
	}

	@Override
	public void beginContact(Component object, Object object2) {
		CircleComponent cc = (CircleComponent) object;

		// Kill the circle if it touches a bullet.
		if (object2 instanceof BulletComponent) {

			((BulletComponent) object2).die = true;

			if (cc.powerup != null) {
				cc.powerup.activate();
			}

			cc.killed = true;

			cc.die = true;
			PooledEffect effect = circleEffect.obtain();
			effect.setPosition(cc.position.x, cc.position.y);
			effect.getEmitters()
					.get(0)
					.getTint()
					.setColors(
							new float[] { cc.color.r, cc.color.g, cc.color.b });
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

	@Override
	public void spawn(int position, int direction, float velocity,
			float scale) {
		CircleComponent circComp = new CircleComponent();
		circComp.position.set(2.9f * MathUtils.cosDeg(position), 2.9f * MathUtils.sinDeg(position));
		circComp.direction = direction;
		circComp.velocity = velocity;
		circComp.scale = scale;
		circComp.color = Global.selectColor();
		// Add a health boos powerup 3/10 of the time.
		if (MathUtils.randomBoolean(0.30f)) {
			circComp.powerup = new HealthPowerup(circComp, MathUtils.random(
					0.5f, 2f), game.player);
			game.powerup.addPowerup(circComp.powerup);
		} else if(MathUtils.randomBoolean(0.143f)) {
			//The actual probability is 10% because (1-30%) * (14.3%) = 10%
			circComp.powerup = new SlowPowerup(circComp, Math.min(0.9f, (MathUtils.random(0.25f, 0.75f)/circComp.scale)), MathUtils.random(1f, 7f), game);
			game.powerup.addPowerup(circComp.powerup);
		}
		addCircle(circComp);		
	}

}
