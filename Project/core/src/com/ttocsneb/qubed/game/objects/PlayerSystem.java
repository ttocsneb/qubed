package com.ttocsneb.qubed.game.objects;

import box2dLight.PointLight;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
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
import com.ttocsneb.qubed.game.contact.ContactListener;
import com.ttocsneb.qubed.game.objects.components.BulletComponent;
import com.ttocsneb.qubed.game.objects.components.CircleComponent;
import com.ttocsneb.qubed.game.objects.components.CubeComponent;
import com.ttocsneb.qubed.screen.GameScreen;
import com.ttocsneb.qubed.util.Global;

/**
 * All logic for the Player.
 * 
 * @author TtocsNeb
 *
 */
public class PlayerSystem extends EntitySystem implements ContactListener {

	private ParticleEffectPool triangleEffect;

	private static final float COOLDOWN = 0.33f;
	private static final float BULLETSIZE = 0.1f;
	private static final float REGENRATE = 0.05f;

	private Color color;
	private Color toColor;

	public float RegenMultiplier = 1f;

	private GameScreen game;

	/** The Wanted rotation */
	private float torotation; // Wanted rotation
	/** The current rotation */
	private float rotation; // Current rotation
	/** The size of the player */
	private float size;
	/** The desired size of the player */
	private float health;
	/** Time it takes to regenerate the missing corner from shooting */
	private float coolDownA, coolDownB, coolDownC;

	private Vector2 a, b, c, d, e, f, g, h, i;
	private Body body;
	private Fixture fixture;

	/**
	 * The point of the triangle which is shot. <br>
	 * <br>
	 * The first 3 bits are for each point.
	 */
	private byte shootpoint = 0;

	private PointLight light;

	public PlayerSystem(GameScreen gs) {
		game = gs;

		triangleEffect = new ParticleEffectPool(
				Global.assets.particles.triangleExp, 1, 5);

		// initiate the vectors.
		a = new Vector2();
		b = new Vector2();
		c = new Vector2();
		d = new Vector2();
		e = new Vector2();
		f = new Vector2();
		g = new Vector2();
		h = new Vector2();
		i = new Vector2();

		initBody(gs.world);

		color = new Color(Color.LIGHT_GRAY.mul(Color.CYAN));
		toColor = new Color(Color.LIGHT_GRAY.mul(Color.CYAN));
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
		body.setUserData(new PlayerComponent());

		size = 0.1f;

		fixture = null;
		updateShape();
	}

	/**
	 * This is a dummy class used for the physics engine
	 * 
	 * @author TtocsNeb
	 *
	 */
	private class PlayerComponent implements Component {
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

		if (size < 0.001f)
			return;

		FixtureDef fdef = new FixtureDef();

		fdef.friction = 0.7f;
		fdef.density = 1f;
		fdef.restitution = 0.2f;

		fdef.filter.categoryBits = 2;
		
		PolygonShape shape = new PolygonShape();

		// Set the points of the shape.
		a.set(0, size / 2f);
		b.set(a.x * MathUtils.cosDeg(240) - (a.y * MathUtils.sinDeg(240)), a.x
				* MathUtils.sinDeg(240) + (a.y * MathUtils.cosDeg(240)));
		c.set(a.x * MathUtils.cosDeg(120) - (a.y * MathUtils.sinDeg(120)), a.x
				* MathUtils.sinDeg(120) + (a.y * MathUtils.cosDeg(120)));

		// Gdx.app.debug("PlayerSystem", "A: " + a + "; B: " + b + "; C: " + c);

		float[] vert = new float[] { a.x, a.y, b.x, b.y, c.x, c.y };

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

	/**
	 * @return <b>true</b> if the player has died.
	 */
	public boolean died() {
		return health == 0;
	}

	private Vector2 point(Vector2 a, Vector2 b, float percent) {

		return new Vector2(a.x + ((b.x - a.x) * percent), a.y
				+ ((b.y - a.y) * percent));

	}

	public void shoot(int pointer) {
		BulletComponent c = new BulletComponent();

		c.scale = BULLETSIZE;
		c.color.set(color);

		// decide which point to shoot from.
		if (coolDownA <= 0
				&& inTriange(game.getPointer(pointer), new Vector2(0, 0),
						new Vector2(2203 * MathUtils.cosDeg(150 - rotation),
								2203 * MathUtils.sinDeg(150 - rotation)),
						new Vector2(2203 * MathUtils.cosDeg(30 - rotation),
								2203 * MathUtils.sinDeg(30 - rotation)))) {
			Gdx.app.debug("PlayerSystem:Update:Shoot",
					"Normal shoot orientation.");
			c.position.x = this.a.x;
			c.position.y = this.a.y;

			c.rotation = rotation;

			c.velx = MathUtils.sinDeg(rotation);
			c.vely = MathUtils.cosDeg(rotation);

			/*
			 * light = new PointLight(game.lights, 512, new Color(1, 1, 1,
			 * 0.25f).mul(color), 1, a.x + 0.05f MathUtils.sinDeg(rotation), a.y
			 * + 0.05f MathUtils.cosDeg(rotation));
			 */

			game.bullet.addBullet(c);
			coolDownA = COOLDOWN;

			shootpoint = (byte) (shootpoint | 1);
		} else if (coolDownB <= 0
				&& inTriange(game.getPointer(pointer), new Vector2(0, 0),
						new Vector2(2203 * MathUtils.cosDeg(-90 - rotation),
								2203 * MathUtils.sinDeg(-90 - rotation)),
						new Vector2(2203 * MathUtils.cosDeg(30 - rotation),
								2203 * MathUtils.sinDeg(30 - rotation)))) {
			Gdx.app.debug("PlayerSystem:Update:Shoot",
					"Right shoot orientation.");
			c.position.x = this.b.x;
			c.position.y = this.b.y;

			c.rotation = rotation + 120;

			c.velx = MathUtils.sinDeg(rotation + 120);
			c.vely = MathUtils.cosDeg(rotation + 120);

			/*
			 * light = new PointLight(game.lights, 512, new Color(1, 1, 1,
			 * 0.25f).mul(color), 1, b.x + 0.05f MathUtils.sinDeg(rotation), b.y
			 * + 0.05f MathUtils.cosDeg(rotation));
			 */

			game.bullet.addBullet(c);
			coolDownB = COOLDOWN;

			shootpoint = (byte) (shootpoint | 2);
		} else if (coolDownC <= 0
				&& inTriange(game.getPointer(pointer), new Vector2(0, 0),
						new Vector2(2203 * MathUtils.cosDeg(150 - rotation),
								2203 * MathUtils.sinDeg(150 - rotation)),
						new Vector2(2203 * MathUtils.cosDeg(-90 - rotation),
								2203 * MathUtils.sinDeg(-90 - rotation)))) {
			Gdx.app.debug("PlayerSystem:Update:Shoot",
					"Left shoot orientation.");
			c.position.x = this.c.x;
			c.position.y = this.c.y;

			c.rotation = rotation - 120;

			c.velx = MathUtils.sinDeg(rotation - 120);
			c.vely = MathUtils.cosDeg(rotation - 120);

			/*
			 * light = new PointLight(game.lights, 512, new Color(1, 1, 1,
			 * 0.25f).mul(color), 1, this.c.x + 0.05f
			 * MathUtils.sinDeg(rotation), this.c.y + 0.05f
			 * MathUtils.cosDeg(rotation));
			 */

			game.bullet.addBullet(c);
			coolDownC = COOLDOWN;

			shootpoint = (byte) (shootpoint | 4);
		} else {
			return;
		}

		Gdx.input.vibrate(new long[] { 0, 10, 10, 10, 10, 10, 10 }, -1);

	}

	@Override
	public void update(float delta) {
		// Compensate the slow downs from slowmotion powerups.
		delta /= game.speed;

		if (size < 1) {
			damage(-(REGENRATE * RegenMultiplier * delta));
		}

		body.setTransform(body.getPosition(), (360 - rotation)
				* MathUtils.degreesToRadians);

		// ////////////////////////////////////////////////////////////
		//
		// Mechanics
		//
		// ///////////////////////////////////////////////////////////

		if (game.debug) {
			game.shape.setColor(Color.BLACK);
			game.shape.rectLine(new Vector2(0, 0),
					new Vector2(2203 * MathUtils.cosDeg(150 - rotation),
							2203 * MathUtils.sinDeg(150 - rotation)), 0.01f);
			game.shape.rectLine(new Vector2(0, 0),
					new Vector2(2203 * MathUtils.cosDeg(30 - rotation),
							2203 * MathUtils.sinDeg(30 - rotation)), 0.01f);
			game.shape.rectLine(new Vector2(0, 0),
					new Vector2(2203 * MathUtils.cosDeg(-90 - rotation),
							2203 * MathUtils.sinDeg(-90 - rotation)), 0.01f);
		}

		// Shoot if the player has tapped the screen.
		if (size - BULLETSIZE >= BULLETSIZE && Gdx.input.isTouched()) {

			for (int i = 0; i < 3; i++) {
				if (Gdx.input.isTouched(i))
					shoot(i);
			}

		}

		if (light != null) {
			light.setDistance(light.getDistance() - delta * 5);

			if (light.getDistance() <= 0.1f) {
				light.remove();
				light = null;
			}
		}

		// Health
		// Shrink to nothing when the player can no longer shoot.
		if (health < BULLETSIZE) {
			health = 0;
		}
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

		if (size > 0) {
			if (size - BULLETSIZE < BULLETSIZE) {
				toColor.set(Global.RED);
			} else if (size < BULLETSIZE * 3) {
				toColor.set(Global.ORANGE);
			} else {
				toColor.set(Color.LIGHT_GRAY.mul(Color.CYAN));
			}

			color.lerp(toColor, delta / 0.1f);

			game.shape.setColor(color);

			rotation = nlerp(delta * 7, rotation, torotation);

			if (coolDownA <= 0 && coolDownB <= 0 && coolDownC <= 0) {

				float tsize = size / 2f;

				// Draw the Triangle
				a.set(tsize * MathUtils.sinDeg(rotation),
						tsize * MathUtils.cosDeg(rotation));
				b.set(tsize * MathUtils.sinDeg(rotation + 120), tsize
						* MathUtils.cosDeg(rotation + 120));
				c.set(tsize * MathUtils.sinDeg(rotation - 120), tsize
						* MathUtils.cosDeg(rotation - 120));

				game.shape.triangle(a.x, a.y, c.x, c.y, b.x, b.y);
			} else {

				// other points.

				// calculate the modifications to the triangle
				float progressA = 0;
				float progressB = 0;
				float progressC = 0;

				float tmpA = coolDownA, tmpB = coolDownB, tmpC = coolDownC;

				// Get the percentage of the transition.
				coolDownA = Math.max(coolDownA - delta, 0);
				coolDownB = Math.max(coolDownB - delta, 0);
				coolDownC = Math.max(coolDownC - delta, 0);

				if (coolDownA == 0 && tmpA != 0) {
					shootpoint = (byte) (shootpoint & 6);
					size -= BULLETSIZE;
					health -= BULLETSIZE;
				}
				if (coolDownB == 0 && tmpB != 0) {
					shootpoint = (byte) (shootpoint & 5);
					size -= BULLETSIZE;
					health -= BULLETSIZE;
				}
				if (coolDownC == 0 && tmpC != 0) {
					shootpoint = (byte) (shootpoint & 3);
					size -= BULLETSIZE;
					health -= BULLETSIZE;
				}

				// If the the animation has finished, draw the triangle
				if (shootpoint == 0) {

					// Calculate the points of the triangle.

					float tsize = size / 2f;

					a.set(tsize * MathUtils.sinDeg(rotation),
							tsize * MathUtils.cosDeg(rotation));
					b.set(tsize * MathUtils.sinDeg(rotation + 120), tsize
							* MathUtils.cosDeg(rotation + 120));
					c.set(tsize * MathUtils.sinDeg(rotation - 120), tsize
							* MathUtils.cosDeg(rotation - 120));

					// Draw the triangle.
					game.shape.triangle(a.x, a.y, c.x, c.y, b.x, b.y);

					// Stop the method
					return;
				}

				// Calculate the progress of the transition, and interpolate
				// it
				// to make it smooth.
				if ((shootpoint & 1) != 0) {
					progressA = 1 - coolDownA / COOLDOWN;
					progressA = Interpolation.fade.apply(progressA);
					progressA *= BULLETSIZE;
				}

				if ((shootpoint & 2) != 0) {
					progressB = 1 - coolDownB / COOLDOWN;
					progressB = Interpolation.fade.apply(progressB);
					progressB *= BULLETSIZE;
				}

				if ((shootpoint & 4) != 0) {
					progressC = 1 - coolDownC / COOLDOWN;
					progressC = Interpolation.fade.apply(progressC);
					progressC *= BULLETSIZE;
				}

				// Calculate the points of the triangle.
				float tsize = size / 2f - progressA / 2f - progressB / 2f
						- progressC / 2f;

				a.set(tsize * MathUtils.sinDeg(rotation),
						tsize * MathUtils.cosDeg(rotation));
				b.set(tsize * MathUtils.sinDeg(rotation + 120), tsize
						* MathUtils.cosDeg(rotation + 120));
				c.set(tsize * MathUtils.sinDeg(rotation - 120), tsize
						* MathUtils.cosDeg(rotation - 120));

				// Create a float array with the appropriate size to fit the
				// vertices used to draw the polygon.
				float[] verts = new float[((shootpoint & 1) != 0 ? 4 : 2)
						+ ((shootpoint & 2) != 0 ? 4 : 2)
						+ ((shootpoint & 4) != 0 ? 4 : 2)];
				int x = 0;

				Gdx.app.debug("PlayerSystem", "A: " + (shootpoint & 1) + "; B: "
						+ (shootpoint & 2) + "; C: " + (shootpoint & 4));

				// Top Notch
				if ((shootpoint & 1) != 0) {
					e.set(point(a, c, (BULLETSIZE - progressA) / size));
					d.set(point(a, b, (BULLETSIZE - progressA) / size));
					verts[x++] = e.x;
					verts[x++] = e.y;
					verts[x++] = d.x;
					verts[x++] = d.y;
				} else {
					verts[x++] = a.x;
					verts[x++] = a.y;
				}

				// Bottom Right Notch
				if ((shootpoint & 2) != 0) {
					f.set(point(b, a, (BULLETSIZE - progressB) / size));
					g.set(point(b, c, (BULLETSIZE - progressB) / size));
					verts[x++] = f.x;
					verts[x++] = f.y;
					verts[x++] = g.x;
					verts[x++] = g.y;
				} else {
					verts[x++] = b.x;
					verts[x++] = b.y;
				}

				// BottomLeft Notch
				if ((shootpoint & 4) != 0) {
					h.set(point(c, b, (BULLETSIZE - progressC) / size));
					i.set(point(c, a, (BULLETSIZE - progressC) / size));
					verts[x++] = h.x;
					verts[x++] = h.y;
					verts[x++] = i.x;
					verts[x++] = i.y;
				} else {
					verts[x++] = c.x;
					verts[x++] = c.y;
				}

				Global.fillPoly(verts);

				// Resize the box2D triangle
				updateShape(size - progressA - progressB - progressC);
			}
		}

	}

	private boolean inTriange(Vector2 p, Vector2 p0, Vector2 p1, Vector2 p2) {
		float a = 0.5f * (-p1.y * p2.x + p0.y * (-p1.x + p2.x) + p0.x
				* (p1.y - p2.y) + p1.x * p2.y);

		float sign = a < 0 ? -1 : 1;

		float s = (p0.y * p2.x - p0.x * p2.y + (p2.y - p0.y) * p.x + (p0.x - p2.x)
				* p.y)
				* sign;
		float t = (p0.x * p1.y - p0.y * p1.x + (p0.y - p1.y) * p.x + (p1.x - p0.x)
				* p.y)
				* sign;

		return s > 0 && t > 0 && (s + t) < 2 * a * sign;
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
		torotation = rotation;
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

	// /////////////// ContactListener ///////////////////////

	@Override
	public Class<?> getComponentType() {
		return PlayerComponent.class;
	}

	@Override
	public void beginContact(Component object, Object object2) {

		if (object2 instanceof CircleComponent) {
			CircleComponent circ = (CircleComponent) object2;
			circ.die = true;

			long[] tmp = new long[9];
			tmp[0] = 0;
			for (int i = 1; i < tmp.length; i += 2) {
				tmp[i] = (long) (25 / game.speed);
				tmp[i + 1] = (long) (5 / game.speed);
			}
			Gdx.input.vibrate(tmp, -1);

			PooledEffect effect = triangleEffect.obtain();
			effect.setPosition(0, 0);
			effect.getEmitters().get(0).getScale().setHigh(size / 2f);
			effect.getEmitters().get(0).getTint()
					.setColors(new float[] { color.r, color.g, color.b });
			game.particle.addEffect(effect);
			health -= circ.scale / 5f;

			PooledEffect effect1 = game.circle.circleEffect.obtain();
			effect1.setPosition(circ.position.x, circ.position.y);
			effect1.getEmitters()
					.get(0)
					.getTint()
					.setColors(
							new float[] { circ.color.r, circ.color.g,
									circ.color.b });
			effect1.getEmitters().get(0).getScale().setHigh(circ.scale);
			game.particle.addEffect(effect1);

		}

		if (object2 instanceof CubeComponent) {
			CubeComponent cube = (CubeComponent) object2;
			cube.die = true;

			long[] tmp = new long[9];
			tmp[0] = 0;
			for (int i = 1; i < tmp.length; i += 2) {
				tmp[i] = (long) (25 / game.speed);
				tmp[i + 1] = (long) (5 / game.speed);
			}
			Gdx.input.vibrate(tmp, -1);

			PooledEffect effect = triangleEffect.obtain();
			effect.setPosition(0, 0);
			effect.getEmitters().get(0).getScale().setHigh(size / 2f);
			effect.getEmitters().get(0).getTint()
					.setColors(new float[] { color.r, color.g, color.b });
			game.particle.addEffect(effect);
			health -= cube.scale / 5f;

			PooledEffect effect1 = game.cube.squareEffect.obtain();
			effect1.setPosition(cube.position.x, cube.position.y);
			effect1.getEmitters()
					.get(0)
					.getTint()
					.setColors(
							new float[] { cube.color.r, cube.color.g,
									cube.color.b });
			effect1.getEmitters().get(0).getScale().setHigh(cube.scale);
			game.particle.addEffect(effect1);

		}
	}

	@Override
	public void endContact(Component object, Object object2) {

	}

}
