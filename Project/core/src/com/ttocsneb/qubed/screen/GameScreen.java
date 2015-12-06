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
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Align;
import com.ttocsneb.qubed.game.BulletSystem;
import com.ttocsneb.qubed.game.CircleComponent;
import com.ttocsneb.qubed.game.CircleSystem;
import com.ttocsneb.qubed.game.CubeComponent;
import com.ttocsneb.qubed.game.CubeSystem;
import com.ttocsneb.qubed.game.ParticleSystem;
import com.ttocsneb.qubed.game.PlayerSystem;
import com.ttocsneb.qubed.game.contact.ContactManager;
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

	private Engine engine;

	// Game Systems, where all of the logic lie
	public CubeSystem cube;
	public CircleSystem circle;
	public PlayerSystem player;
	public BulletSystem bullet;
	public ParticleSystem particle;

	// The contact manager simplifies Box2D contact events.
	ContactManager contactManager;

	// The cameras used for rendering the game.
	private OrthographicCamera cam;
	private OrthographicCamera hud;

	// The Renderers for the game.
	public SpriteBatch batch;
	public ShapeRenderer shape;

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
	private GlyphLayout gameOver;
	private float gameOverProg;

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

		initEngine();

		initCamera();

		font = Assets.instance.fonts.huge;
		gameOver = new GlyphLayout(font, "GAME OVER", Color.RED, 1080,
				Align.center, true);

		// Don't allow the back button(on Android) to close the game.
		Gdx.input.setCatchBackKey(true);

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

	/**
	 * @return a random color.
	 */
	private Color selectColor() {
		switch (MathUtils.random(3)) {
		case 0:
			return new Color(250 / 255f, 128 / 255f, 40 / 255f, 1);
		case 1:
			return new Color(218 / 255f, 67 / 255f, 36 / 255f, 1);
		case 2:
			return new Color(49 / 255f, 136 / 255f, 183 / 255f, 1);
		case 3:
			return new Color(63 / 255f, 193 / 255f, 91 / 255f, 1);
		default:
			return new Color(Color.BLACK);
		}
	}

	@Override
	public void render(float delta) {
		// Clear the screen.
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT
				| GL20.GL_DEPTH_BUFFER_BIT
				| (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV
						: 0));

		
		if (Gdx.app.getType() != ApplicationType.Desktop) {
			// Rotate the screen using buttons if on desktop.
			orientation = Math.max(Math.min(Gdx.input.getAccelerometerX(), 5),
					-5);
		} else {
			// Rotate the screen using the accelerometer.
			orientation = Global.lerp(delta * 2, orientation, MathUtils.clamp(
					(Gdx.input.isKeyPressed(Keys.A) ? 5 : Gdx.input
							.isKeyPressed(Keys.D) ? -5 : -orientation), -5, 5));
		}

		if (!player.died() && Math.abs(orientation) > 0.5) {
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
		if (MathUtils.random(100) == MathUtils.random(100)) {
			if (MathUtils.randomBoolean()) {
				spawnCube();
			} else {
				spawnCircle();
			}
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

		shape.setColor(Color.BLACK);
		shape.rect(0, 0, Global.VIEWPORT_WIDTH, Global.VIEWPORT_GUI_HEIGHT);

		shape.setColor(Color.WHITE);
		shape.circle(0, 0, 3, 100);

		engine.update(delta);

		// ////////////////////////////SHAPE RENDERER//////////////////////
		shape.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);

		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		// ///////////////////////////GAME BATCH///////////////////////////
		particle.update(delta);

		// ///////////////////////////GAME BATCH///////////////////////////
		batch.end();

		// Update the lighting system.
		lights.setCombinedMatrix(cam);
		lights.updateAndRender();

		// Draw the world debug, if in debug mode.
		if (debug == true) worldRenderer.render(world, cam.combined);

		// Draw the GUI.
		hud.update();
		batch.setProjectionMatrix(hud.combined);
		batch.begin();
		// //////////////////////////HUD BATCH/////////////////////////////

		// Check if the player has died.
		if (player.died()) {
			// If the player has died, draw the game Over display.
			gameOverProg = Math.min(gameOverProg + delta, 1);
			float prog = Interpolation.sineOut.apply(gameOverProg);

			font.draw(batch, gameOver, 0, 1920-(960 + gameOver.height*0.25f)*prog + gameOver.height);
		}

		// //////////////////////////HUD BATCH/////////////////////////////
		batch.end();
		
		if(player.died()) {

			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			shape.setProjectionMatrix(hud.combined);
			shape.begin(ShapeType.Filled);
			shape.setColor(1, 0, 0, gameOverProg*0.5f);
			shape.rect(0, 0, 1080, 1920);
			shape.end();
		}

	}

	/**
	 * Spawn a cube.
	 */
	private void spawnCube() {
		CubeComponent cubeComp = new CubeComponent();
		int rot = MathUtils.random(360);
		cubeComp.x = 2.9f * MathUtils.cosDeg(rot);
		cubeComp.y = 2.9f * MathUtils.sinDeg(rot);
		cubeComp.direction = MathUtils.randomBoolean(.6f) ? (rot - 180 < 0 ? rot + 180
				: rot - 180)
				: (MathUtils.random(
						(rot - 180 < 0 ? rot + 180 : rot - 180) - 45,
						(rot - 180 < 0 ? rot + 180 : rot - 180) + 45));
		cubeComp.velocity = MathUtils.random(0.5f, 1);
		cubeComp.scale = MathUtils.random(0.5f, 0.9f);
		cubeComp.color = selectColor();

		cube.addCube(cubeComp);

	}

	/**
	 * Spawn a Circle.
	 */
	private void spawnCircle() {
		CircleComponent circComp = new CircleComponent();
		int rot = MathUtils.random(360);
		circComp.x = 2.9f * MathUtils.cosDeg(rot);
		circComp.y = 2.9f * MathUtils.sinDeg(rot);
		circComp.direction = MathUtils.randomBoolean(.6f) ? (rot - 180 < 0 ? rot + 180
				: rot - 180)
				: (MathUtils.random(
						(rot - 180 < 0 ? rot + 180 : rot - 180) - 45,
						(rot - 180 < 0 ? rot + 180 : rot - 180) + 45));
		circComp.velocity = MathUtils.random(0.5f, 1);
		circComp.scale = MathUtils.random(0.25f, 0.75f);
		circComp.color = selectColor();
		circle.addCircle(circComp);
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
		if (keycode == Keys.BACK) {
			// Change screens to the menu when the back button is pressed.
			ScreenTransition transition = ScreenTransitionSlide.init(0.5f,
					ScreenTransitionSlide.RIGHT, false, Interpolation.pow2);
			game.setScreen(new MenuScreen(game), transition);
		}

		// Debug operations on Desktop.
		if (Gdx.app.getType() == ApplicationType.Desktop) {
			if (keycode == Keys.E) {
				debug = !debug;
			} else if (keycode == Keys.Q) {
				ScreenTransition transition = ScreenTransitionSlide.init(0.5f,
						ScreenTransitionSlide.RIGHT, false, Interpolation.pow2);
				game.setScreen(new MenuScreen(game), transition);
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
