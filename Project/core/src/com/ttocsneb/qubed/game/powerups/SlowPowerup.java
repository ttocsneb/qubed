package com.ttocsneb.qubed.game.powerups;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.ttocsneb.qubed.game.objects.GameObject;
import com.ttocsneb.qubed.screen.GameScreen;
import com.ttocsneb.qubed.util.Assets;
import com.ttocsneb.qubed.util.Global;

public class SlowPowerup extends Powerup {

	private static final float SPEED = 0.5f;
	
	private float time;

	private float start;

	private float lerpTime;
	private float alphaTime;

	private GameScreen gameScreen;

	/**
	 * Creates a new Slomo Powerup
	 * @param object the GameObject the powerup belongs to
	 * @param time The time the powerup will be active.
	 * @param gamescreen
	 */
	public SlowPowerup(GameObject object, float time,
			GameScreen gamescreen) {
		super(object, Assets.instance.textures.slowPowerup, time*SPEED);
		this.time = time*SPEED;
		gameScreen = gamescreen;

		lerpTime = Math.min(0.25f, time / 8f);
	}

	@Override
	public void start() {
		start = gameScreen.speed;
		
		if(!Global.Config.MUTE) {
			Assets.instance.sounds.slowMusic.setPosition(Assets.instance.sounds.music.getPosition()*2);
			Assets.instance.sounds.music.pause();
			Assets.instance.sounds.slowMusic.play();
		}
	}

	@Override
	public void update(float delta) {
		time -= delta;
		if (time >= lerpTime && gameScreen.speed != SPEED) {
			alphaTime += delta;
			float alpha = Interpolation.sine.apply(Math.min(1, alphaTime
					/ lerpTime));
			gameScreen.speed = Global.lerp(alpha, start, SPEED);
			if (gameScreen.speed == SPEED)
				alphaTime = 0;
		} else if (time <= lerpTime) {
			alphaTime += delta;
			float alpha = Interpolation.sine.apply(Math.min(1, alphaTime/lerpTime));
			gameScreen.speed = Global.lerp(alpha, SPEED, 1);
		}
	}

	@Override
	public void end() {
		gameScreen.speed = 1;
		if(!Global.Config.MUTE) {
			Assets.instance.sounds.music.setPosition(Assets.instance.sounds.slowMusic.getPosition()/2);
			Assets.instance.sounds.slowMusic.pause();
			Assets.instance.sounds.music.play();
		}
	}

	@Override
	public Color getColor() {
		return Color.PURPLE;
	}

}
