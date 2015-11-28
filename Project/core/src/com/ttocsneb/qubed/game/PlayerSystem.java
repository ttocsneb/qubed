package com.ttocsneb.qubed.game;

import box2dLight.PointLight;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.ttocsneb.qubed.screen.GameScreen;

/**
 * All logic for the Player.
 * 
 * @author TtocsNeb
 *
 */
public class PlayerSystem extends EntitySystem {

	// WARNING: I have not updated the comments for this class, enter at your
	// own risk!

	private static final float COOLDOWN = 0.5f;
	private static final float DELAY = 0.5f;
	private static final float BULLETSIZE = 0.1f;

	private float direction; // Wanted rotation
	private float rotation; // Current rotation

	private float size;
	private float health;

	private GameScreen game;

	private float coolDown = 0;

	private float delay = DELAY;

	private boolean touched = true;

	private Vector2 a, b, c, d, e;

	private Body body;
	private Fixture fixture;

	private PointLight light;

	public PlayerSystem(GameScreen gs) {
		game = gs;

		a = new Vector2();
		b = new Vector2();
		c = new Vector2();
		d = new Vector2();
		e = new Vector2();

		initBody(gs.world);
	}

	/**
	 * Initialize the body for the main character.
	 * 
	 * @param world
	 *            the Box2D world to create the body in.
	 */
	private void initBody(World world) {
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.KinematicBody;
		bdef.position.set(0, 0);
		body = world.createBody(bdef);

		fixture = null;
		updateShape();
	}

	private void updateShape() {
		updateShape(size);
	}

	/**
	 * Update the body's shape.
	 */
	private void updateShape(float size) {

		// Remove the fixture if it exists.
		if (fixture != null && fixture.getBody() != null) {
			body.destroyFixture(fixture);
			fixture = null;
		}

		if (size < 0.001f) return;

		FixtureDef fdef = new FixtureDef();

		fdef.friction = 0.7f;
		fdef.density = 1f;
		fdef.restitution = 0.2f;

		PolygonShape shape = new PolygonShape();

		// Set the points of the shape.
		a.set(0, size / 2f);
		b.set(a.x * MathUtils.cosDeg(240) - (a.y * MathUtils.sinDeg(240)), a.x
				* MathUtils.sinDeg(240) + (a.y * MathUtils.cosDeg(240)));
		c.set(a.x * MathUtils.cosDeg(120) - (a.y * MathUtils.sinDeg(120)), a.x
				* MathUtils.sinDeg(120) + (a.y * MathUtils.cosDeg(120)));

		// Gdx.app.debug("PlayerSystem", "A: " + a + "; B: " + b + "; C: " + c);

		float[] vert = new float[] {
				a.x, a.y, b.x, b.y, c.x, c.y
		};

		shape.set(vert);

		fdef.shape = shape;

		// Create the fixture, and add it to the
		fixture = body.createFixture(fdef);

		shape.dispose();
	}

	@Override
	public void addedToEngine(Engine engine) {
		health = 1;
	}

	private Vector2 point(Vector2 a, Vector2 b, float percent) {

		return new Vector2(a.x + ((b.x - a.x) * percent), a.y
				+ ((b.y - a.y) * percent));

	}

	@Override
	public void update(float delta) {
		/*
		 * If possible, use real physics to translate the rotation of the
		 * player's body. For now, we are setting the rotation directly
		 * (breaking physics). This is a temporary fix.
		 * 
		 * float nextAngle = body.getAngle() + body.getAngularVelocity()*delta;
		 * float totalRotation = (MathUtils.degreesToRadians*rotation) -
		 * nextAngle; while(totalRotation < -180 * MathUtils.degreesToRadians)
		 * totalRotation += 360*MathUtils.degreesToRadians; while(totalRotation
		 * > 180 * MathUtils.degreesToRadians) totalRotation -=
		 * 360*MathUtils.degreesToRadians; float desiredAngularVelocity =
		 * totalRotation / delta; float impulse = body.getInertia() *
		 * desiredAngularVelocity; body.applyAngularImpulse(impulse, true);
		 */
		body.setTransform(body.getPosition(), (360 - rotation)
				* MathUtils.degreesToRadians);

		// Gdx.app.debug("PlayerSystem", "Rotation: " + rotation + "; BodyRot: "
		// + body.getAngle() * MathUtils.radiansToDegrees);

		// ////////////////////////////////////////////////////////////
		//
		// Mechanics
		//
		// ///////////////////////////////////////////////////////////

		if (size < 0.1f) {
			health = 1;
		}

		// Shoot
		if (size > 0.1 && coolDown <= 0 && Gdx.input.isTouched() && !touched) {
			touched = true;
			coolDown = COOLDOWN;
			delay = DELAY;
			BulletComponent c = new BulletComponent();
			c.x = a.x;
			c.y = a.y;

			c.rotation = rotation;

			c.velx = MathUtils.sinDeg(rotation);
			c.vely = MathUtils.cosDeg(rotation);
			c.scale = BULLETSIZE;

			light = new PointLight(game.lights, 512,
					new Color(1, 1, 1, 0.f).mul(Color.CYAN), 1, a.x + 0.05f
							* MathUtils.sinDeg(rotation), a.y + 0.05f
							* MathUtils.cosDeg(rotation));

			game.bullet.addBullet(c);
		} else if (!Gdx.input.isTouched() && touched) {
			touched = false;
		}

		if (light != null) {
			light.setDistance(light.getDistance() - delta * 5);

			if (light.getDistance() <= 0.1f) {
				light.remove();
				light = null;
			}
		}

		// Health
		if (size != health) {
			size = lerp(delta * 5, size, health);

			if (Math.abs(size - health) < 0.01f) {
				size = health;
			}

			updateShape();
		}

		// ////////////////////////////////////////////////////////////
		//
		// Draw the Player
		//
		// ///////////////////////////////////////////////////////////

		rotation = nlerp(delta * 7, rotation, direction);

		game.shape.setColor(Color.LIGHT_GRAY.mul(Color.CYAN));

		if (coolDown <= 0) {

			// Draw the Triangle
			a.set(size / 2f * MathUtils.sinDeg(rotation),
					size / 2f * MathUtils.cosDeg(rotation));
			b.set(a.x * MathUtils.cosDeg(240) - (a.y * MathUtils.sinDeg(240)),
					a.x * MathUtils.sinDeg(240) + (a.y * MathUtils.cosDeg(240)));
			c.set(a.x * MathUtils.cosDeg(120) - (a.y * MathUtils.sinDeg(120)),
					a.x * MathUtils.sinDeg(120) + (a.y * MathUtils.cosDeg(120)));

			game.shape.triangle(a.x, a.y, c.x, c.y, b.x, b.y);
		} else {

			// calculate the modifications to the triangle
			float progress = 0;

			// wait until the delay has passed before making modifications.
			if (delay > 0) {
				delay = Math.max(delay - delta, 0);
			} else {
				// Get the percentage of the transition.
				coolDown = Math.max(coolDown - delta, 0);

				// If the the animation has finished, draw the triangle
				if (coolDown == 0) {
					size -= BULLETSIZE;
					health -= BULLETSIZE;

					// Calculate the points of the triangle.
					a.set(size / 2f * MathUtils.sinDeg(rotation), size / 2f
							* MathUtils.cosDeg(rotation));
					b.set(a.x * MathUtils.cosDeg(240)
							- (a.y * MathUtils.sinDeg(240)),
							a.x * MathUtils.sinDeg(240)
									+ (a.y * MathUtils.cosDeg(240)));
					c.set(a.x * MathUtils.cosDeg(120)
							- (a.y * MathUtils.sinDeg(120)),
							a.x * MathUtils.sinDeg(120)
									+ (a.y * MathUtils.cosDeg(120)));

					// Draw the triangle.
					game.shape.triangle(a.x, a.y, c.x, c.y, b.x, b.y);

					// Stop the method
					return;
				}

				// Calculate the progress of the transition, and interpolate it
				// to make it smooth.
				progress = 1 - coolDown / COOLDOWN;
				progress = Interpolation.pow2Out.apply(progress);
				progress *= BULLETSIZE;

			}

			// Calculate the points of the triangle.
			a.set((size / 2f - progress / 2f) * MathUtils.sinDeg(rotation),
					(size / 2f - progress / 2f) * MathUtils.cosDeg(rotation));
			b.set(a.x * MathUtils.cosDeg(240) - (a.y * MathUtils.sinDeg(240)),
					a.x * MathUtils.sinDeg(240) + (a.y * MathUtils.cosDeg(240)));
			c.set(a.x * MathUtils.cosDeg(120) - (a.y * MathUtils.sinDeg(120)),
					a.x * MathUtils.sinDeg(120) + (a.y * MathUtils.cosDeg(120)));
			e.set(point(a, c, (BULLETSIZE - progress) / size));
			d.set(point(a, b, (BULLETSIZE - progress) / size));

			// Draw the Triangle without the top.
			game.shape.triangle(e.x, e.y, d.x, d.y, b.x, b.y);

			game.shape.triangle(e.x, e.y, c.x, c.y, b.x, b.y);

			// Resize the box2D triangle
			updateShape(size - progress);
		}

	}

	public void damage(float health) {
		this.health = Math.max(0, this.health - health);
	}

	public class Triangle {

		public Vector2 a;
		public Vector2 b;
		public Vector2 c;

		public Triangle(Vector2 a, Vector2 b, Vector2 c) {
			this.a = new Vector2(a);
			this.b = new Vector2(b);
			this.c = new Vector2(c);
		}
	}

	public Triangle getTriangle() {
		return new Triangle(a, b, c);
	}

	public void setRotation(float rotation) {
		direction = rotation;
	}

	/**
	 * Nlerp a to b
	 * 
	 * @param t
	 *            Alpha
	 * @param a
	 *            Start Rotation (Deg)
	 * @param b
	 *            End Rotation (Deg)
	 * @return Nlerp (Deg)
	 */
	private float nlerp(float t, float a, float b) {

		// Convert a and b to vectors
		Vector2 A = new Vector2(MathUtils.cosDeg(a), MathUtils.sinDeg(a));
		Vector2 B = new Vector2(MathUtils.cosDeg(b), MathUtils.sinDeg(b));
		A = A.lerp(B, t).nor();

		// Convert the final vector into degrees, and return it.
		return MathUtils.atan2(A.y, A.x) * MathUtils.radiansToDegrees;
	}

	private float lerp(float t, float a, float b) {
		return (a + t * (b - a));
	}

	public float getSize() {
		return size;
	}

}
