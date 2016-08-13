package com.ttocsneb.qubed.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.ttocsneb.qubed.screen.transitions.ScreenTransition;
import com.ttocsneb.qubed.screen.transitions.ScreenTransitionSlide;
import com.ttocsneb.qubed.util.Global;

/**
 * The Menu Screen.
 * 
 * @author TtocsNeb
 *
 */
public class MenuScreen extends AbstractGameScreen implements GestureListener {

	// Renderer
	private SpriteBatch batch;
	private ShapeRenderer shape;

	// Font stuff.
	private BitmapFont font;
	private GlyphLayout title;
	private GlyphLayout highscore;
	private GlyphLayout version;
	private GlyphLayout bug;
	
	private boolean touchedBug;

	// Camera
	private OrthographicCamera cam;

	// Sound stuffs.
	private TextureRegion speaker;
	private TextureRegion speakerOff;
	private boolean touchedVol;

	// Arrow Animation Variables.
	private float animInterp;
	private int animProgress;
	private boolean animDir;

	private DragAnim drag;

	// Camera position.
	private float x;

	/**
	 * Create a new MenuScreen
	 * 
	 * @param game
	 */
	public MenuScreen(DirectedGame game) {
		super(game);
	}

	@Override
	public void show() {

		// Load assets.
		speaker = Global.assets.textures.speaker;
		speakerOff = Global.assets.textures.speakerOff;

		// Load fonts.
		font = Global.assets.fonts.huge;
		font.setColor(Color.BLACK);

		// Create the Title
		title = new GlyphLayout(font, "QUBED");
		
		version = new GlyphLayout(Global.assets.fonts.small, "v." +Global.version, Color.BLACK, 0, Align.topLeft, false);
		
		bug = new GlyphLayout(Global.assets.fonts.med, "Report a bug", new Color(51/255f, 177/255f, 1, 1), 0, Align.center, false);

		highscore = new GlyphLayout(Global.assets.fonts.large,
				Global.Config.HIGHSCORE + "", Color.BLACK, 0, Align.center,
				false);

		// Init the Camera.
		cam = new OrthographicCamera();
		cam.setToOrtho(false, 1080, 1920);

		// Init the renderers.
		batch = Global.batch;
		shape = Global.shape;

		// Initiate arrow animation variables
		animProgress = 0;
		animInterp = 0;
		animDir = true;

		drag = new DragAnim();
		tapTimer = 15;

		//Allow the application to close when the back button(on Android) is pressed.
		Gdx.input.setCatchBackKey(false);
		
		Gdx.app.debug(
				"MenuScreen",
				Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer) ? "This device has an accelerometer"
						: "This device does not have an accelerometer");
	}

	@Override
	public void render(float delta) {

		tapTimer -= delta;
		// If the screen hasn't been tapped yet, and the timer is finished,
		// animate the finger.
		if (tapTimer < 0 && !tapped) {
			drag.start();
			tapTimer = 15;
		}

		// Clear the screen.
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT
				| GL20.GL_DEPTH_BUFFER_BIT
				| (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV
						: 0));
		Gdx.gl.glClearColor(0, 0, 0, 1);

		// lerp the screen back to 0 when the screen is not being panned by the
		// user.
		if (panStart == 0) {
			x = Global.lerp(delta * 10, x, 0);
		} else if (panStart == 2) {
			x = Global.lerp(delta * 10, x, 1080);
		}

		// Move the Camera.
		cam.position.set(MathUtils.clamp(x + 540, 540, 1080 + 540),
				cam.position.y, cam.position.z);

		// Progress the animation Interpolation
		animInterp += (animDir ? -1 : 1) * delta;
		// Switch the direction of interpolation when the limit is reached.
		if (animInterp >= 1 || animInterp <= 0) {
			animDir = !animDir;
			animInterp = MathUtils.clamp(animInterp, 0, 1);
		}
		// Apply interpolation to the animation progress variable to be used for
		// renderering.
		animProgress = MathUtils
				.round(Interpolation.pow2.apply(animInterp) * 64);

		cam.update();
		shape.setProjectionMatrix(cam.combined);
		shape.begin(ShapeType.Filled);
		// ///////////////////////Shape Renderer////////////////////////////////

		// Draw the White Background
		shape.setColor(Color.WHITE);
		shape.rect(0, 0, 1080, 1920);

		// Draw the arrow to the screen.
		shape.setColor(Color.BLACK);
		shape.triangle(885 + animProgress,
				1920 / 2 - 64 - (animProgress / 64f * 32), 885 + animProgress,
				1920 / 2 + 64 + (animProgress / 64f * 32),
				885 + animProgress + 128, 1920 / 2);

		// ///////////////////////Shape Renderer////////////////////////////////
		shape.end();

		if (Gdx.input.isTouched()) {
			// Convert screen pixels to camera pixels.
			Vector3 v3 = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input
					.getY(), 0));
			// Detect Touch Events.
			if (!touchedVol && v3.x > 0 && v3.x < speaker.getRegionWidth()
					&& v3.y > 1920 - speaker.getRegionHeight() && v3.y < 1920) {
				touchedVol = true;
			} else if (touchedVol
					&& !(v3.x > 0 && v3.x < speaker.getRegionWidth()
							&& v3.y > 1920 - speaker.getRegionHeight() && v3.y < 1920))
				touchedVol = false;
			
			//Detect when the bug link is clicked
			if(!touchedBug && v3.x > 1080/2-bug.width/2 && v3.x < 1080/2+bug.width/2 
					&& v3.y > 1915-bug.height && v3.y < 1915) {
				touchedBug = true;
				bug.setText(Global.assets.fonts.med, "Report a bug", new Color(0, 119/255f, 213/255f, 1), 0, Align.center, false);
			}
			else if(touchedBug && !(v3.x > 1080/2-bug.width/2 && v3.x < 1080/2+bug.width/2 
					&& v3.y > 1915-bug.height && v3.y < 1915)) {
				touchedBug = false;
				bug.setText(Global.assets.fonts.med, "Report a bug", new Color(51/255f, 177/255f, 1, 1), 0, Align.center, false);
			}
			
		} else if (touchedVol) {
			// Activate/deactivate the music.
			touchedVol = false;
			Global.Config.MUTE = !Global.Config.MUTE;
			if (Global.Config.MUTE)
				Global.assets.sounds.music.pause();
			else
				Global.assets.sounds.music.play();
			Global.Config.save();
		} else if(touchedBug) {
			//open the browser when the bug link is clicked
			touchedBug = false;
			Gdx.net.openURI("http://ttocsneb.com/qubed-bug");
			bug.setText(Global.assets.fonts.med, "Report a bug", new Color(51/255f, 177/255f, 1, 1), 0, Align.center, false);
		}

		cam.update();
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		// ///////////////////////Batch Renderer////////////////////////////////

		// Draw the title to the screen.
		font.draw(batch, title, 1080 / 2 - title.width / 2, 1920 * 3 / 4f);
		Global.assets.fonts.large.draw(batch, highscore, 540,
				highscore.height + 10);
		
		Global.assets.fonts.small.draw(batch, version, 5, version.height+5);
		
		Global.assets.fonts.med.draw(batch, bug, 1080/2, 1915);

		Global.assets.fonts.med.setColor(Color.BLACK);

		// Draw the speaker.
		TextureRegion region = Global.Config.MUTE ? speakerOff : speaker;
		batch.draw(region.getTexture(), 0, 1920 - region.getRegionHeight(), 0,
				0, region.getRegionWidth(), region.getRegionHeight(), 1, 1, 0,
				region.getRegionX(), region.getRegionY(),
				region.getRegionWidth(), region.getRegionHeight(), false, false);

		drag.update(delta);

		// ///////////////////////Batch Renderer////////////////////////////////
		batch.end();

	}

	/**
	 * Animates a finger moving across the screen.
	 * 
	 * @author TtocsNeb
	 *
	 */
	private class DragAnim {

		private TextureRegion finger;

		private int step;
		private float time;
		private float timer;
		private Vector2 position;
		private float angle;

		private float strtRot;
		private float endRot;

		private Vector2 strtPos;
		private Vector2 endPos;

		private float calpha;
		private float strtAlpha;
		private float endAlpha;

		private Interpolation interp;

		private boolean done;

		private DragAnim() {
			finger = Global.assets.textures.finger;
			position = new Vector2();
			strtPos = new Vector2();
			endPos = new Vector2();
			done = true;
		}

		private void update(float delta) {
			// Only update when the animation is active.
			if (!done) {
				// Find the progress/interpolation of the current step.
				time += delta;
				float prog = Math.min(time / timer, 1);
				float alpha = interp.apply(prog);

				// Interpolate the angle, position, and alpha.
				angle = Global.lerp(alpha, strtRot, endRot);
				position.set(Global.lerp(alpha, strtPos.x, endPos.x),
						Global.lerp(alpha, strtPos.y, endPos.y));
				calpha = Global.lerp(alpha, strtAlpha, endAlpha);

				// Draw the finger.
				batch.setColor(1, 1, 1, calpha);
				batch.draw(finger.getTexture(), position.x, position.y,
						finger.getRegionWidth(), finger.getRegionHeight(),
						finger.getRegionWidth() * 2,
						finger.getRegionHeight() * 2, 1, 1, angle,
						finger.getRegionX(), finger.getRegionY(),
						finger.getRegionWidth(), finger.getRegionHeight(),
						false, false);
				batch.setColor(Color.WHITE);

				// if the current step is finished move to the next one.
				if (prog >= 1f) {
					step++;
					switch (step) {
					case 1:
						// Begin moving across the screen.
						strtRot = endRot;
						strtPos.set(endPos);
						endPos.set(416, 960);
						timer = 0.5625f;
						time = 0;
						interp = Interpolation.pow2In;
						break;
					case 2:
						// Move to the end of the screen, and fade away.
						strtRot = endRot;
						endRot -= 30;
						strtPos.set(endPos);
						endPos.set(0, 1000);
						timer = 0.5f;
						time = 0;
						interp = Interpolation.pow2Out;
						strtAlpha = endAlpha;
						endAlpha = 0;
						break;
					case 3:
						// Stop the animation.
						done = true;
					}
				}
			}
		}

		private void start() {
			// Step 0
			// Move the finger down to simulate a press.
			strtPos.set(775, 1200);
			endPos.set(810, 960);
			strtAlpha = 1;
			endAlpha = 1;
			timer = 0.25f;
			time = 0;
			strtRot = -70;
			endRot = -135;
			step = 0;
			interp = Interpolation.sineIn;
			done = false;
		}

	}

	private boolean tapped;
	private float tapTimer;

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void hide() {

	}

	@Override
	public InputProcessor getInputProcessor() {
		return new GestureDetector(this);
	}

	// //////////////////////////////////////////////
	// Gesture Listener
	// //////////////////////////////////////////////

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		// If the screen hasn't been tapped yet, stop the timer, and animate the
		// finger.
		if (!tapped) {
			tapTimer = 0;
			tapped = true;
		}
		// If the timer has finished run the finger animation.
		if (tapTimer <= 0) {
			drag.start();
			tapTimer = 7;
		}
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {

		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return false;
	}

	int panStart;

	float panPos;

	boolean canPlay;

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {

		// Set the pan position when the pan begins.
		if (panStart != 1) {
			panPos = x / (Gdx.graphics.getWidth() / 1080f);
			panStart = 1;
		}

		// Set can play to true if the pan speed is fast enough, or the screen
		// is more than half way across.
		canPlay = (-x / (Gdx.graphics.getWidth() / 1080f)) + panPos > 540
				|| (deltaX / (Gdx.graphics.getWidth() / 1080f))
						/ Gdx.graphics.getDeltaTime() < -350;

		// Set the x position of the screen.
		this.x = -x / (Gdx.graphics.getWidth() / 1080f) + panPos;

		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		// reset the pan Start
		panStart = 0;

		// if can play is true, start the game.
		if (canPlay) {
			panStart = 2;
			ScreenTransition transition = ScreenTransitionSlide.init(0.125f,
					ScreenTransitionSlide.LEFT, true, Interpolation.pow2);
			game.setScreen(new GameScreen(game), transition);
		}

		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		return false;
	}

}
