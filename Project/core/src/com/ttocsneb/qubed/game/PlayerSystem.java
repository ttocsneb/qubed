package com.ttocsneb.qubed.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.ttocsneb.qubed.screen.GameScreen;

public class PlayerSystem extends EntitySystem {
	
	private static final float COOLDOWN = 0.5f;
	private static final float DELAY = 0.5f;
	private static final float BULLETSIZE = 0.1f;
	
	private float direction; //Wanted rotation
	private float rotation; //Current rotation
	
	private float size;
	private float health;
	
	private Engine engine;
	
	private GameScreen game;
	
	private float coolDown = 0; 
	
	private float delay = DELAY;
	
	private boolean touched = true;
	
	private Vector2	a, b, c, d, e;
	
	public PlayerSystem(GameScreen gs) {
		game = gs;
		
		a = new Vector2();
		b = new Vector2();
		c = new Vector2();
		d = new Vector2();
		e = new Vector2();
		
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.engine = engine;
		health = 1;
	}
	
	private Vector2 point(Vector2 a, Vector2 b, float percent) {
		
		return new Vector2(
				a.x  + ((b.x-a.x)*percent),
				a.y + ((b.y-a.y)*percent)
		);
		
	}

	
	@Override
	public void update(float delta) {
		
		//////////////////////////////////////////////////////////////
		//
		//	Mechanics
		//
		/////////////////////////////////////////////////////////////
		
		//Shoot
		if(size > 0.1 && coolDown <= 0 && Gdx.input.isTouched() && !touched) {
			touched = true;
			coolDown = COOLDOWN;
			delay = DELAY;
			Entity bullet = new Entity();
			BulletComponent c = new BulletComponent();
			c.x = a.x;
			c.y = a.y;
			
			c.rotation = rotation;
			
			c.velx = MathUtils.sinDeg(rotation);
			c.vely = MathUtils.cosDeg(rotation);
			c.scale = BULLETSIZE;
			
			bullet.add(c);
			engine.addEntity(bullet);
		} else if(!Gdx.input.isTouched() && touched) {
			touched = false;
		}
		
		//Health
		if(size != health) {
			size = lerp(delta*5, size, health);
			
			if(Math.abs(size-health) < 0.01f) {
				size = health;
			}
		}
		
		//////////////////////////////////////////////////////////////
		//
		//	Draw the Player
		//
		/////////////////////////////////////////////////////////////
		
		rotation = nlerp(delta*7, rotation, direction);

		game.shape.setColor(Color.LIGHT_GRAY.mul(Color.CYAN));
		
		
		if(coolDown <= 0) {

			//Draw the Triangle
			a.set(size/2f*MathUtils.sinDeg(rotation), size/2f*MathUtils.cosDeg(rotation));
			b.set(a.x*MathUtils.cosDeg(240)-(a.y*MathUtils.sinDeg(240)), a.x*MathUtils.sinDeg(240)+(a.y*MathUtils.cosDeg(240)));
			c.set(a.x*MathUtils.cosDeg(120)-(a.y*MathUtils.sinDeg(120)), a.x*MathUtils.sinDeg(120)+(a.y*MathUtils.cosDeg(120)));
			
			game.shape.triangle(a.x, a.y,
					c.x, c.y,
					b.x, b.y);
		}else {
			
			//calculate the modifications to the triangle
			float progress = 0;
			
			//wait until the delay has passed before making modifications.
			if(delay > 0) {
				delay = Math.max(delay - delta, 0);
			} else {
				//Get the percentage of the transition.
				coolDown = Math.max(coolDown - delta, 0);
				
				//If the the animation has finished, draw the triangle 
				if(coolDown == 0) {
					size -= BULLETSIZE;
					health -= BULLETSIZE;
					
					//Calculate the points of the triangle.
					a.set(size/2f*MathUtils.sinDeg(rotation), size/2f*MathUtils.cosDeg(rotation));
					b.set(a.x*MathUtils.cosDeg(240)-(a.y*MathUtils.sinDeg(240)), a.x*MathUtils.sinDeg(240)+(a.y*MathUtils.cosDeg(240)));
					c.set(a.x*MathUtils.cosDeg(120)-(a.y*MathUtils.sinDeg(120)), a.x*MathUtils.sinDeg(120)+(a.y*MathUtils.cosDeg(120)));
					
					//Draw the triangle.
					game.shape.triangle(a.x, a.y,
							c.x, c.y,
							b.x, b.y);
					
					//Stop the method 
					return;
				}

				//Calculate the progress of the transition, and interpolate it to make it smooth.
				progress = 1-coolDown/COOLDOWN;
				progress = Interpolation.pow2Out.apply(progress);
				progress *= BULLETSIZE;
				
				
			}

			//Calculate the points of the triangle.
			a.set((size/2f - progress/2f)*MathUtils.sinDeg(rotation), (size/2f - progress/2f)*MathUtils.cosDeg(rotation));
			b.set(a.x*MathUtils.cosDeg(240)-(a.y*MathUtils.sinDeg(240)), a.x*MathUtils.sinDeg(240)+(a.y*MathUtils.cosDeg(240)));
			c.set(a.x*MathUtils.cosDeg(120)-(a.y*MathUtils.sinDeg(120)), a.x*MathUtils.sinDeg(120)+(a.y*MathUtils.cosDeg(120)));
			e.set(point(a, c, (BULLETSIZE-progress)/size));
			d.set(point(a, b, (BULLETSIZE-progress)/size));
			
			
			
			//Draw the Triangle without the top.
			game.shape.triangle(e.x, e.y,
					d.x, d.y,
					b.x, b.y);

			game.shape.triangle(e.x, e.y,
					c.x, c.y,
					b.x, b.y);
			
		}
		
	}
	
	public void damage(float health) {
		this.health = Math.max(0, this.health-health);
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
	 * @param t Alpha
	 * @param a Start Rotation (Deg)
	 * @param b End Rotation (Deg)
	 * @return Nlerp (Deg)
	 */
	private float nlerp(float t, float a, float b) {
		
		//Convert a and b to vectors
		Vector2 A = new Vector2(MathUtils.cosDeg(a), MathUtils.sinDeg(a));
		Vector2 B = new Vector2(MathUtils.cosDeg(b), MathUtils.sinDeg(b));
		A = A.lerp(B, t).nor();
		
		//Convert the final vector into degrees, and return it.
		return MathUtils.atan2(A.y, A.x)*MathUtils.radiansToDegrees;
	}
	
	private float lerp(float t, float a, float b) {
		return (a + t*(b-a));
	}

	public float getSize() {
		return size;
	}
	
}
