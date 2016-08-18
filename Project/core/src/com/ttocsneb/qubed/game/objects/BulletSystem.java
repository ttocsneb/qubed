package com.ttocsneb.qubed.game.objects;

import com.badlogic.ashley.core.Component;
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
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.ttocsneb.qubed.game.contact.ContactListener;
import com.ttocsneb.qubed.game.objects.components.BulletComponent;
import com.ttocsneb.qubed.screen.GameScreen;

/**
 * All logic for Bullets.
 * 
 * @author TtocsNeb
 *
 */
public class BulletSystem extends EntitySystem implements ContactListener {

	private ImmutableArray<Entity> entities;

	private ComponentMapper<BulletComponent> bc = ComponentMapper
			.getFor(BulletComponent.class);

	private GameScreen game;

	private Vector2 a, b, c;

	public BulletSystem(GameScreen gs) {
		game = gs;
	}

	private Engine engine;

	@Override
	@SuppressWarnings("unchecked")
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(BulletComponent.class)
				.get());
		this.engine = engine;
		a = new Vector2();
		b = new Vector2();
		c = new Vector2();
	}

	@Override
	public void update(float delta) {
		//compensate for speed related powerups.
		delta /= game.speed;
		
		// Loop through every bullet
		for (Entity entity : entities) {
			BulletComponent bullet = bc.get(entity);
			
			game.shape.setColor(bullet.color);
			
			// Update the bullet position. Note: I'm not sure why we do this.
			bullet.position.x = bullet.body.getPosition().x;
			bullet.position.y = bullet.body.getPosition().y;

			// Kill the bullet if it is suppost to die.
			if (bullet.die) {
				bullet.scale -= delta;
				if (bullet.scale <= 0) {
					engine.removeEntity(entity);
					game.world.destroyBody(bullet.body);
					continue;
				}
			}

			a.set(bullet.scale
					/ 2f
					* MathUtils.sin(360 * MathUtils.degreesToRadians
							- bullet.body.getAngle()),
					bullet.scale
							/ 2f
							* MathUtils.cos(360 * MathUtils.degreesToRadians
									- bullet.body.getAngle()));
			b.set(a.x * MathUtils.cosDeg(240) - (a.y * MathUtils.sinDeg(240)),
					a.x * MathUtils.sinDeg(240) + (a.y * MathUtils.cosDeg(240)));
			c.set(a.x * MathUtils.cosDeg(120) - (a.y * MathUtils.sinDeg(120)),
					a.x * MathUtils.sinDeg(120) + (a.y * MathUtils.cosDeg(120)));

			// Draw the bullet.
			game.shape.triangle(bullet.position.x + a.x, bullet.position.y + a.y, bullet.position.x + b.x,
					bullet.position.y + b.y, bullet.position.x + c.x, bullet.position.y + c.y);

			// Die when the bullet leaves the world.
			if (Math.pow(bullet.position.x, 2) + Math.pow(bullet.position.y, 2) >= 8.41f) {
				bullet.die = true;
			}
		}
	}

	/**
	 * Add a bullet to the System.
	 * 
	 * @param bc
	 */
	public void addBullet(BulletComponent bc) {
		// Create a new Bullet Entity.
		Entity e = new Entity();
		e.add(bc);
		engine.addEntity(e);

		// Make the Body for the bullet.
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.DynamicBody;
		bdef.position.set(bc.position.x, bc.position.y);
		bdef.angle = (360 - bc.rotation) * MathUtils.degreesToRadians;

		// Create the Body.
		bc.body = game.world.createBody(bdef);
		bc.body.setUserData(bc);

		// Make a Fixture for the bullet.
		FixtureDef fdef = new FixtureDef();

		fdef.density = 100;
		fdef.friction = 0.7f;
		fdef.restitution = 0.2f;

		// Create the shape.
		PolygonShape shape = new PolygonShape();

		a.set(bc.scale
				/ 2f
				* MathUtils.sin(360 * MathUtils.degreesToRadians
						- bc.body.getAngle()),
				bc.scale
						/ 2f
						* MathUtils.cos(360 * MathUtils.degreesToRadians
								- bc.body.getAngle()));
		b.set(a.x * MathUtils.cosDeg(240) - (a.y * MathUtils.sinDeg(240)), a.x
				* MathUtils.sinDeg(240) + (a.y * MathUtils.cosDeg(240)));
		c.set(a.x * MathUtils.cosDeg(120) - (a.y * MathUtils.sinDeg(120)), a.x
				* MathUtils.sinDeg(120) + (a.y * MathUtils.cosDeg(120)));

		shape.set(new float[] {
				a.x, a.y, b.x, b.y, c.x, c.y
		});

		fdef.shape = shape;

		// Create the Fixture.
		bc.body.createFixture(fdef);

		// Set the velocity of the bullet.
		bc.body.setLinearVelocity(new Vector2(bc.velx/game.speed, bc.vely/game.speed));

	}

	@Override
	public Class<?> getComponentType() {
		return BulletComponent.class;
	}

	@Override
	public void beginContact(Component object, Object object2) {

	}

	@Override
	public void endContact(Component object, Object object2) {

	}

}
