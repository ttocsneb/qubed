package com.ttocsneb.qubed.game.spawn;

import java.util.HashMap;

import com.badlogic.gdx.math.MathUtils;
import com.ttocsneb.qubed.game.spawn.json.SpawnObject;
import com.ttocsneb.qubed.game.spawn.json.SpawnPattern;
import com.ttocsneb.qubed.screen.GameScreen;

public class SpawnManager {
	public static final String CUBE = "cube";
	public static final String CIRCLE = "circle"; 
	

	private HashMap<String, Spawn> objects;

	private SpawnPattern active;
	private int step;
	private int subStep;

	private int position;

	private float delay;

	private int subRepeat;
	private int repeat;
	private int repeatStep;

	private boolean done;

	public SpawnManager(GameScreen gs) {
		objects = new HashMap<String, Spawn>();
		objects.put(CUBE, gs.cube);
		objects.put(CIRCLE, gs.circle);
	}

	public boolean isPatternComplete() {
		return done;
	}

	/**
	 * Spawn objects according to the active {@link SpawnPattern}.
	 * 
	 * @param delta
	 */
	public void update(float delta, float difficulty) {
		// Update the time left before spawning an object.
		delay -= delta;

		// Spawn an object if the sequence is not done, and the delay is
		// finished.
		if (active != null && done == false && delay < 0) {
			SpawnObject obj = active.objects[step];

			// Adjust the position the object will spawn at.
			position += MathUtils.random(obj.offsetMin * (obj.offsetDiff ? difficulty*obj.offsetDiffScale : 1),
					obj.offsetMax  * (obj.offsetDiff ? difficulty*obj.offsetDiffScale : 1));

			if (!objects.containsKey(obj.object)) {
				// If there is no object type set, spawn a random type.
				((Spawn) objects.values().toArray()[MathUtils.random(objects
						.size() - 1)]).spawn(
						position,
						(int) (position
								+ 180
								- MathUtils.random(
										obj.angleMin / (obj.angleDiff ? difficulty*obj.angleDiffScale : 1),
										obj.angleMax / (obj.angleDiff ? difficulty*obj.angleDiffScale : 1))),
						MathUtils.random(obj.speedMin * (obj.speedDiff ? difficulty*obj.speedDiffScale : 1),
								obj.speedMax * (obj.speedDiff ? difficulty*obj.speedDiffScale : 1)), MathUtils
								.random(obj.sizeMin * (obj.sizeDiff ? difficulty*obj.sizeDiffScale : 1),
										obj.sizeMax * (obj.sizeDiff ? difficulty*obj.sizeDiffScale : 1)));
			} else {
				// Spawn the object.
				objects.get(obj.object).spawn(
						position,
						(int) (position
								+ 180
								- MathUtils.random(
										obj.angleMin / (obj.angleDiff ? difficulty*obj.angleDiffScale : 1),
										obj.angleMax / (obj.angleDiff ? difficulty*obj.angleDiffScale : 1))),
						MathUtils.random(obj.speedMin * (obj.speedDiff ? difficulty*obj.speedDiffScale : 1),
								obj.speedMax * (obj.speedDiff ? difficulty*obj.speedDiffScale : 1)),
						MathUtils.random(obj.sizeMin * (obj.sizeDiff ? difficulty*obj.sizeDiffScale : 1),
								obj.sizeMax * (obj.sizeDiff ? difficulty*obj.sizeDiffScale : 1)));
			}

			// add to subStep each time an object has spawned.
			// When subStep is greater than subRepeat (The max repetitions for
			// this object) move to the next object.
			subStep++;
			if (subStep >= subRepeat) {
				step++;

				// restart the sequence when there are no more objects.
				if (step >= active.objects.length) {
					step = 0;

					
					
					// stop the spawn pattern when it has repeated the desiered
					// amount of times.
					if (repeatStep >= repeat) {
						
						done = true;
					}
					repeatStep++;
				}

				subRepeat = MathUtils.random(active.objects[step].repeatMin,
						active.objects[step].repeatMax);
			}
			delay = MathUtils.random(active.objects[step].delayMin,
					active.objects[step].delayMax);

		}

	}

	/**
	 * Start spawning from the given pattern.
	 * 
	 * @param pattern
	 * @param delay
	 *            time before spawning starts (seconds).
	 */
	public void startPattern(SpawnPattern pattern, float delay) {
		// Reset the variables.
		done = false;
		step = 0;
		subStep = 0;
		repeatStep = 0;

		position = MathUtils.random(0, 360);
		this.delay = delay;
		subRepeat = MathUtils.random(pattern.objects[0].repeatMin,
				pattern.objects[0].repeatMax);
		repeat = MathUtils.random(pattern.repeatMin, pattern.repeatMax);
		active = pattern;
	}

	/**
	 * Start spawning from the given pattern.
	 * 
	 * @param pattern
	 */
	public void startPattern(SpawnPattern pattern) {
		startPattern(pattern, 0);
	}

}
