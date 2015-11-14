package com.ttocsneb.qubed.game;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.ttocsneb.qubed.screen.GameScreen;

public class CubeSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;

	private ComponentMapper<CubeComponent> cc = ComponentMapper.getFor(CubeComponent.class);
	
	private GameScreen game;
	private Engine engine;
	
	private PlayerSystem player;
	private PlayerSystem.Triangle playerMesh;
	
	private BulletSystem bullet;
	
	private float interpolation;
	
	private Vector2 a, b, c, d;
	
	public CubeSystem(GameScreen gs, PlayerSystem ps, BulletSystem bs) {
		game = gs;
		interpolation = 0;
		player = ps;
		bullet = bs;
		
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
		float size, distance;
		
		playerMesh = player.getTriangle();
		
		for(int i=0; i<entities.size(); i++) {
			Entity entity = entities.get(i);
			CubeComponent cube = cc.get(entity);
			
			distance = (float)Math.sqrt(Math.pow(cube.x, 2) + Math.pow(cube.y, 2));
			
			if(cube.die) {
				cube.x = lerp(delta*4, cube.x, 0);
				cube.y = lerp(delta*4, cube.y, 0);
				cube.scale -= delta*4;
				if(cube.scale <= 0)
					engine.removeEntity(entity);
			} else {
				cube.x += cube.velocity * MathUtils.cosDeg(cube.direction) * delta * (1-distance/3f);
				cube.y += cube.velocity * MathUtils.sinDeg(cube.direction) * delta * (1-distance/3f);
			}
			
			size = cube.scale * (1-distance/3f);
			
			
			if(cube.rotation) {
				cube.progress += delta*4;
			
				if(cube.progress >= 1) {
					cube.progress = 0;
				}
			} else {
				cube.progress -= delta*4;
			
				if(cube.progress <= 0) {
					cube.progress = 1;
				}
			}
			
			interpolation = Interpolation.sine.apply(cube.progress);

			game.shape.setColor(cube.color);
			
			//As the shape renderer does not support filled polygons, Draw two triangles to form the custom square.
			
			a.set(cube.x-0.5f*size+interpolation*size, cube.y-0.5f*size);
			b.set(cube.x+0.5f*size, cube.y-0.5f*size+interpolation*size);
			c.set(cube.x+0.5f*size-interpolation*size, cube.y+0.5f*size);
			d.set(cube.x-0.5f*size, cube.y+0.5f*size-interpolation*size);
			
			game.shape.triangle(
					a.x, a.y,
					b.x, b.y,
					c.x, c.y);
			
			game.shape.triangle(
					c.x, c.y,
					d.x, d.y,
					a.x, a.y);

			if((pointinPlayer(a) || pointinPlayer(b) || pointinPlayer(c) || pointinPlayer(d) || distance <= player.getSize()/2f) && !cube.die){
				cube.die = true;
				player.damage(cube.scale*0.5f);
			}
			
			
		}
	}
	
	private float sign(Vector2 p1, Vector2 p2, Vector2 c) {
		return ((p2.x - p1.x)*(c.y-p1.y) - (p2.y - p1.y)*(c.x - p1.x));
	}
	
	private boolean pointinPlayer(Vector2 point) {
		
		boolean b1 = sign(playerMesh.a, playerMesh.b, point) < 0f;
		boolean b2 = sign(playerMesh.b, playerMesh.c, point) < 0f;
		boolean b3 = sign(playerMesh.c, playerMesh.a, point) < 0f;
		
		return ((b1 == b2) && (b2 == b3));
		
	}
	
	private float lerp(float t, float a, float b) {
		return (a + t*(b-a));
	}
	
}
