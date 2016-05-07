package com.ttocsneb.qubed.game.powerups;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.ttocsneb.qubed.game.objects.GameObject;
import com.ttocsneb.qubed.screen.GameScreen;
import com.ttocsneb.qubed.util.Assets;
import com.ttocsneb.qubed.util.Global;

public class SlowPowerup extends Powerup {

	private float time;

	private float amplitude;
	private float start;

	private float lerpTime;
	private float alphaTime;

	private GameScreen gameScreen;

	public SlowPowerup(GameObject object, float amplitude, float time,
			GameScreen gamescreen) {
		super(object, Assets.instance.textures.slowPowerup, time*amplitude);
		this.time = time*amplitude;
		gameScreen = gamescreen;
		this.amplitude = amplitude;

		lerpTime = Math.min(0.25f, time / 8f);
	}

	@Override
	public void start() {
		start = gameScreen.speed;
	}

	@Override
	public void update(float delta) {
		time -= delta;
		if (time >= lerpTime && gameScreen.speed != amplitude) {
			alphaTime += delta;
			float alpha = Interpolation.sine.apply(Math.min(1, alphaTime
					/ lerpTime));
			gameScreen.speed = Global.lerp(alpha, start, amplitude);
			if (gameScreen.speed == amplitude)
				alphaTime = 0;
		} else if (time <= lerpTime) {
			alphaTime += delta;
			float alpha = Interpolation.sine.apply(Math.min(1, alphaTime/lerpTime));
			gameScreen.speed = Global.lerp(alpha, amplitude, 1);
		}
	}

	@Override
	public void end() {
		gameScreen.speed = 1;
	}

	@Override
	public Color getColor() {
		return Color.PURPLE;
	}

}
