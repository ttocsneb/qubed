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
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.ttocsneb.qubed.game.contact.ContactListener;
import com.ttocsneb.qubed.game.objects.components.BulletComponent;
import com.ttocsneb.qubed.game.objects.components.CubeComponent;
import com.ttocsneb.qubed.game.powerups.HealthPowerup;
import com.ttocsneb.qubed.game.powerups.SlowPowerup;
import com.ttocsneb.qubed.screen.GameScreen;
import com.ttocsneb.qubed.util.Assets;
import com.ttocsneb.qubed.util.Global;

/**
 * All logic for Cubes.
 * 
 * @author TtocsNeb
 *
 */
public class CubeSystem extends EntitySystem implements ContactListener, com.ttocsneb.qubed.game.spawn.Spawn {

	private ImmutableArray<Entity> entities;

	private ComponentMapper<CubeComponent> cc = ComponentMapper
			.getFor(CubeComponent.class);

	private GameScreen game;
	private Engine engine;

	public ParticleEffectPool squareEffect;

	public CubeSystem(GameScreen gs) {
		game = gs;

		squareEffect = new ParticleEffectPool(
				Assets.instance.particles.squareExp, 1, 5);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(CubeComponent.class).get());
		this.engine = engine;

	}

	@Override
	public void update(float delta) {

		// Go through all cubes.
		for (Entity entity : entities) {
			CubeComponent cube = cc.get(entity);

			// Kill the cube if it is unworthy of living.
			if (cube.die) {
				cube.scale -= delta * 4;
				updateShape(cube);

				// Dispose of the dead body.
				if (cube.scale <= 0) {
					// remove the powerup if it has not already activated.
					if (cube.powerup != null && !cube.powerup.hasStarted()) {
						cube.powerup.remove();
					}

					if (cube.killed)
						game.addScore((int) (cube.getSize() * 100f) + 10);

					game.world.destroyBody(cube.body);
					engine.removeEntity(entity);
					continue;
				}
			} else {
				// I'm still not sure why we need to update the positioning
				// system.
				cube.position.x = cube.body.getPosition().x;
				cube.position.y = cube.body.getPosition().y;
			}

			// set the rotation, why can't we use the body directly?
			cube.rotation = (cube.body.getAngle() * MathUtils.radiansToDegrees);

			// Set the color to draw the cube.
			game.shape.setColor(cube.color);

			// Draw the Cube.
			game.shape.rect(cube.position.x - cube.scale / 2f, cube.position.y
					- cube.scale / 2f, cube.scale / 2f, cube.scale / 2f,
					cube.scale, cube.scale, 1, 1, cube.rotation);

			// Kill the cube if it broke the barrier rule.
			if (Math.pow(cube.position.x, 2) + Math.pow(cube.position.y, 2) >= 9f) {
				cube.die = true;
			}

		}
	}

	/**
	 * Update a cube's shape.
	 * 
	 * @param cc
	 */
	private void updateShape(CubeComponent cc) {

		// Remove any existing shapes.
		if (cc.fixture != null && cc.fixture.getBody() != null) {
			cc.body.destroyFixture(cc.fixture);
			cc.fixture = null;
		}

		// don't even think about making a non-existing shape. It's almost as
		// bad as dividing by zero.
		if (cc.scale < 0.001f) {
			return;
		}

		// Make a fixture
		FixtureDef fdef = new FixtureDef();

		fdef.density = 0.1f;
		fdef.friction = 0.2f;
		fdef.restitution = 0.5f;

		// Create the shape.
		PolygonShape shape = new PolygonShape();
		shape.set(new float[] { -cc.scale / 2f, -cc.scale / 2f, -cc.scale / 2f,
				cc.scale / 2f, cc.scale / 2f, -cc.scale / 2f, cc.scale / 2f,
				cc.scale / 2f });
		fdef.shape = shape;

		// Create the fixture.
		cc.fixture = cc.body.createFixture(fdef);

		// dispose of the bad shape.
		shape.dispose();
	}

	/**
	 * Add a Cube to the System.
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
	public void addCube(CubeComponent cc) {
		// Create a new Cube entity.
		Entity e = new Entity();
		e.add(cc);
		engine.addEntity(e);

		// make a body.
		BodyDef bdef = new BodyDef();

		bdef.type = BodyType.DynamicBody;
		bdef.position.set(cc.position.x, cc.position.y);

		// Create the body.
		cc.body = game.world.createBody(bdef);
		cc.body.setUserData(cc);

		// Set it's velocity.
		cc.body.setLinearVelocity(cc.velocity * MathUtils.cosDeg(cc.direction),
				cc.velocity * MathUtils.sinDeg(cc.direction));
		cc.body.setAngularVelocity((MathUtils.randomBoolean() ? -1 : 1)
				* MathUtils.random(180, 360) * MathUtils.degreesToRadians);

		// give it a shape.
		updateShape(cc);
	}
	
	@Override
	public void spawn(int position, int direction, float velocity, float scale) {
		CubeComponent cubeComp = new CubeComponent();
		cubeComp.position.set(2.9f * MathUtils.cosDeg(position), 2.9f * MathUtils.sinDeg(position));
		cubeComp.direction = direction;
		cubeComp.velocity = velocity;
		cubeComp.scale = scale;
		cubeComp.color = Global.selectColor();
		// Add a health boos powerup 3/10 of the time.
		if (MathUtils.randomBoolean(0.30f)) {
			cubeComp.powerup = new HealthPowerup(cubeComp, MathUtils.random(
					0.5f, 2f), game.player);
			game.powerup.addPowerup(cubeComp.powerup);
		} else if(MathUtils.randomBoolean(0.143f)) {
			//The actual probability is 10% because (1-30%) * (14.3%) = 10%
			cubeComp.powerup = new SlowPowerup(cubeComp, Math.min(0.9f, (MathUtils.random(0.25f, 0.9f)/cubeComp.scale)), MathUtils.random(1f, 7f), game);
			game.powerup.addPowerup(cubeComp.powerup);
		}

		addCube(cubeComp);
	}

	@SuppressWarnings("unused")
	private float lerp(float t, float a, float b) {
		return (a + t * (b - a));
	}

	@Override
	public Class<?> getComponentType() {
		return CubeComponent.class;
	}

	@Override
	public void beginContact(Component object, Object object2) {
		CubeComponent cc = (CubeComponent) object;

		// Kill the cube if it even grazes a bullet.
		if (object2 instanceof BulletComponent) {

			((BulletComponent) object2).die = true;
			cc.killed = true;
			cc.die = true;

			if (cc.powerup != null) {
				cc.powerup.activate();
			}

			PooledEffect effect = squareEffect.obtain();
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


}
