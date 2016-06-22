package com.ttocsneb.qubed.screen;

import box2dLight.DirectionalLight;
import box2dLight.RayHandler;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.ttocsneb.qubed.game.contact.ContactManager;
import com.ttocsneb.qubed.game.objects.BulletSystem;
import com.ttocsneb.qubed.game.objects.CircleSystem;
import com.ttocsneb.qubed.game.objects.CubeSystem;
import com.ttocsneb.qubed.game.objects.ParticleSystem;
import com.ttocsneb.qubed.game.objects.PlayerSystem;
import com.ttocsneb.qubed.game.powerups.PowerupSystem;
import com.ttocsneb.qubed.game.spawn.SpawnManager;
import com.ttocsneb.qubed.game.spawn.json.SpawnObject;
import com.ttocsneb.qubed.game.spawn.json.SpawnPattern;
import com.ttocsneb.qubed.screen.transitions.ScreenTransition;
import com.ttocsneb.qubed.screen.transitions.ScreenTransitionSlide;
import com.ttocsneb.qubed.util.Assets;
import com.ttocsneb.qubed.util.Global;

/**
 * 
 * The game Screen holds all of the objects for the game.
 * 
 * @author TtocsNeb
 *
 */
public class GameScreen extends AbstractGameScreen implements InputProcessor {

	// This is the length from one corner of the viewport to the other which is
	// used for background rendering.
	private static float ViewPortSize = (float) Math.sqrt(Math.pow(
			Global.VIEWPORT_GUI_HEIGHT, 2) * 2);

	private Engine engine;

	// Game Systems, where all of the logic lie
	public CubeSystem cube;
	public CircleSystem circle;
	public PlayerSystem player;
	public BulletSystem bullet;
	public ParticleSystem particle;
	public PowerupSystem powerup;

	// The contact manager simplifies Box2D contact events.
	ContactManager contactManager;

	// The cameras used for rendering the game.
	private OrthographicCamera cam;
	private OrthographicCamera hud;

	// The Renderers for the game.
	public SpriteBatch batch;
	public ShapeRenderer shape;

	private Color background;

	// The Box2D world.
	public World world;
	public Box2DDebugRenderer worldRenderer;

	// The lighting system.
	public RayHandler lights;

	// The Game's orientation variables
	private float orientation;
	private float rotation;
	// Debug variables.
	private boolean debug;

	private BitmapFont font;
	private BitmapFont smol;
	private GlyphLayout gameOver;
	private float gameOverProg;

	private int score;
	private boolean swithc;
	private int scoreDisplay;
	private GlyphLayout scoreLabel;

	private SpawnManager spawner;

	private SpawnPattern pattern;

	private float difficulty;

	// //////// PowerUp Variables ////////////

	public float speed;
	public float rotationSpeed;

	/**
	 * Create a new Game Screen
	 * 
	 * @param game
	 */
	public GameScreen(DirectedGame game) {
		super(game);
	}

	@Override
	public void show() {
		speed = 1f;
		rotationSpeed = 1f;

		died = false;

		// Initialize Box2D.
		Box2D.init();
		world = new World(new Vector2(0, 0), true);
		worldRenderer = new Box2DDebugRenderer();

		// Initialize Light Engine.
		RayHandler.setGammaCorrection(true);
		lights = new RayHandler(world);
		lights.setShadows(true);
		lights.setAmbientLight(0.9f);
		new DirectionalLight(lights, 1024, new Color(1, 1, 1, 0.1f), -45);

		background = new Color(0, 0, 0, 1);

		initEngine();

		initCamera();

		// Pattern();

		spawner = new SpawnManager(this);
		spawner.startPattern(Assets.instance.patterns.all[MathUtils
				.random(Assets.instance.patterns.all.length - 1)]);

		font = Assets.instance.fonts.huge;
		smol = Assets.instance.fonts.large;
		gameOver = new GlyphLayout(font, "GAME OVER",
				new Color(Global.RED).mul(0.9f, 0.9f, 0.9f, 1), 1080,
				Align.center, true);
		scoreLabel = new GlyphLayout(Assets.instance.fonts.large, "0",
				Color.WHITE, 0, Align.left, false);

		difficulty = 1;

		// Don't allow the back button(on Android) to close the game.
		Gdx.input.setCatchBackKey(true);

	}

	/**
	 * This is used to create new spawn patterns.
	 */
	@SuppressWarnings("unused")
	private void Pattern() {
		pattern = new SpawnPattern();

		pattern.repeatMin = 1;
		pattern.repeatMax = 1;
		pattern.objects = new SpawnObject[1];
		SpawnObject a = pattern.objects[0] = new SpawnObject();
		a.delayMax = 1.5f;
		a.delayMin = 1;
		a.offsetDiff = true;
		a.offsetDiffScale = 1.1f;
		a.offsetMax = (int) (170 / a.offsetDiffScale);
		a.offsetMin = (int) (150 / a.offsetDiffScale);
		a.repeatMin = 3;
		a.repeatMax = 6;
		a.sizeDiff = false;
		a.sizeMax = .75f;
		a.sizeMin = 0.5f;
		a.speedDiff = true;
		a.speedDiffScale = 0.1f;
		a.speedMin = 0.5f / a.speedDiffScale;
		a.speedMax = 1f / a.speedDiffScale;

		Json json = new Json();
		Gdx.files.local("../android/assets/pattern/oppositeOther.json")
				.writeString(json.prettyPrint(pattern), false);
	}

	/**
	 * Initialize the Ashley Game Engine.
	 */
	private void initEngine() {

		engine = new Engine();

		player = new PlayerSystem(this);

		bullet = new BulletSystem(this);

		cube = new CubeSystem(this);
		engine.addSystem(cube);

		circle = new CircleSystem(this);
		engine.addSystem(circle);

		engine.addSystem(player);

		engine.addSystem(bullet);

		particle = new ParticleSystem(this);

		powerup = new PowerupSystem(this);

		contactManager = new ContactManager(circle, bullet, cube, player);
		world.setContactListener(contactManager);

	}

	/**
	 * Initialize the camera.
	 */
	private void initCamera() {

		// Create the Game World Camera
		cam = new OrthographicCamera();
		cam.setToOrtho(false, Global.VIEWPORT_WIDTH, Global.VIEWPORT_GUI_HEIGHT);
		cam.position.set(0, 0, cam.position.z);

		// Create the Ui Camera
		hud = new OrthographicCamera();
		hud.setToOrtho(false, 1080, 1920);

		// Create the renderers.
		batch = Global.batch;
		shape = Global.shape;

	}

	private boolean died;

	@Override
	public void render(float delta) {
		delta = delta * speed;

		// Clear the screen.
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT
				| GL20.GL_DEPTH_BUFFER_BIT
				| (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV
						: 0));

		if (Gdx.app.getType() != ApplicationType.Desktop) {
			// Rotate the screen using the accelerometer.
			// * 60 * delta while redundant allows slow motion to effect the
			// orientation speed
			orientation = Math.max(
					Math.min((Gdx.input.getAccelerometerX() * 60 * delta)
							* rotationSpeed, 5), -5);
		} else {
			// Rotate the screen using buttons if on desktop.
			orientation = Global.lerp((delta * 2) * rotationSpeed, orientation,
					MathUtils.clamp((Gdx.input.isKeyPressed(Keys.A) ? 5
							: Gdx.input.isKeyPressed(Keys.D) ? -5
									: -orientation), -5, 5));
		}

		if (player.died()) {
			died = true;
		}

		if (!died && Math.abs(orientation) > 0.5) {
			// Rotate the screen.
			cam.rotate(-orientation);
			rotation -= orientation;
			if (rotation >= 360) {
				rotation -= 360;
			} else if (rotation < 0) {
				rotation += 360;
			}
		}

		// Rotate the player.
		player.setRotation(rotation);

		// Spawn objects.
		spawner.update(delta, difficulty);
		if (spawner.isPatternComplete()) {
			spawner.startPattern(Assets.instance.patterns.all[MathUtils
					.random(Assets.instance.patterns.all.length - 1)],
					MathUtils.random(5));
			difficulty += 0.25f;
		}

		// Update the world.
		world.step(delta, 6, 2);
		contactManager.update();

		// Draw the game.
		cam.update();

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shape.setProjectionMatrix(cam.combined);
		shape.begin(ShapeType.Filled);
		// ////////////////////////////SHAPE RENDERER//////////////////////

		// Smoothly interpolate the current background color to the desired
		// color.

		// Get the powerup colors.
		Color tmp = powerup.getColor();
		// Set the color step to 0.0666 (Full change in 0.25 seconds).
		final float stp = 0.0666f;

		// step each color value of the background to the powerup color.
		if (Math.abs(background.r - tmp.r) <= stp)
			background.r = tmp.r;
		else
			background.r += background.r > tmp.r ? -stp : stp;

		if (Math.abs(background.g - tmp.g) <= stp)
			background.g = tmp.g;
		else
			background.g += background.g > tmp.g ? -stp : stp;

		if (Math.abs(background.b - tmp.b) <= stp)
			background.b = tmp.b;
		else
			background.b += background.b > tmp.b ? -stp : stp;

		// Set the background color, and draw it.
		shape.setColor(background);
		shape.rect(-ViewPortSize / 2, -ViewPortSize / 2, ViewPortSize,
				ViewPortSize);

		shape.setColor(Color.WHITE);
		shape.circle(0, 0, 3, 100);

		player.setDifficulty(difficulty);

		engine.update(delta);

		// ////////////////////////////SHAPE RENDERER//////////////////////
		shape.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);

		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		// ///////////////////////////GAME BATCH///////////////////////////

		particle.update(delta);
		powerup.update(delta);

		// ///////////////////////////GAME BATCH///////////////////////////
		batch.end();

		// Update the lighting system.
		lights.setCombinedMatrix(cam);
		lights.updateAndRender();

		// Draw the world debug, if in debug mode.
		if (debug == true)
			worldRenderer.render(world, cam.combined);

		if (died) {

			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			shape.setProjectionMatrix(hud.combined);
			shape.begin(ShapeType.Filled);
			shape.setColor(0, 0, 0, gameOverProg * 0.5f);
			shape.rect(0, 0, 1080, 1920);
			shape.end();
		}

		// Draw the GUI.
		hud.update();
		batch.setProjectionMatrix(hud.combined);
		batch.begin();
		// //////////////////////////HUD BATCH/////////////////////////////

		// Check if the player has died.
		if (died) {
			// If the player has died, draw the game Over display.
			gameOverProg = Math.min(gameOverProg + delta, 1);
			float prog = Interpolation.sineOut.apply(gameOverProg);

			font.draw(batch, gameOver, 0, 1920
					- (960 + gameOver.height * 0.25f) * prog + gameOver.height);

			if (score >= Global.Config.HIGHSCORE) {
				Global.Config.HIGHSCORE = score;
				Global.Config.save();
				scoreLabel.setText(smol, score + "", Global.GREEN, 0,
						Align.left, false);
			} else {
				scoreLabel.setText(smol, score + "", Color.WHITE, 0,
						Align.left, false);
			}

			smol.draw(batch, scoreLabel, 540 - scoreLabel.width / 2, 1920
					- (960 - scoreLabel.height * 2) * prog);

		} else {
			// Draw the score in the top left corner of the screen when the game
			// is active.
			if (swithc)
				scoreDisplay += scoreDisplay - score > 0 ? -1 : scoreDisplay
						- score < 0 ? 1 : 0;
			swithc = !swithc;
			scoreLabel.setText(smol, scoreDisplay + "");
			smol.draw(batch, scoreLabel, 10, 1910);
		}

		// //////////////////////////HUD BATCH/////////////////////////////
		batch.end();

	}

	public Vector2 getPointer() {
		Vector3 v3 = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		return new Vector2(v3.x, v3.y);
	}
	
	/**
	 * Add to the current score.
	 * 
	 * @param score
	 */
	public void addScore(int score) {
		this.score += score;
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void hide() {
		world.dispose();
		worldRenderer.dispose();
		particle.dispose();
	}

	@Override
	public InputProcessor getInputProcessor() {
		return this;
	}

	// //////////////////////////////////////////
	// Input Processor
	// //////////////////////////////////////////

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.BACK || keycode == Keys.Q) {
			// Change screens to the menu when the back button is pressed.
			ScreenTransition transition = ScreenTransitionSlide.init(0.5f,
					ScreenTransitionSlide.RIGHT, false, Interpolation.pow2);
			game.setScreen(new MenuScreen(game), transition);
			if(!Global.Config.MUTE && Assets.instance.sounds.slowMusic.isPlaying()) {
				Assets.instance.sounds.slowMusic.pause();
				Assets.instance.sounds.music.setPosition(Assets.instance.sounds.slowMusic.getPosition()/2f);
				Assets.instance.sounds.music.play();
			}
		}

		// Debug operations on Desktop.
		if (Gdx.app.getType() == ApplicationType.Desktop) {
			if (keycode == Keys.E) {
				debug = !debug;
			}
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}
