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
import com.ttocsneb.qubed.game.BulletSystem;
import com.ttocsneb.qubed.game.CircleComponent;
import com.ttocsneb.qubed.game.CircleSystem;
import com.ttocsneb.qubed.game.CubeComponent;
import com.ttocsneb.qubed.game.CubeSystem;
import com.ttocsneb.qubed.game.PlayerSystem;
import com.ttocsneb.qubed.game.contact.ContactManager;
import com.ttocsneb.qubed.screen.transitions.ScreenTransition;
import com.ttocsneb.qubed.screen.transitions.ScreenTransitionSlide;
import com.ttocsneb.qubed.util.Assets;

public class GameScreen extends AbstractGameScreen implements InputProcessor {

	public static final float WIDTH = 4;
	public static final float HEIGHT = 7.11111f;
	
	private Engine engine;
	
	public CubeSystem cube;
	public CircleSystem circle;
	public PlayerSystem player;
	public BulletSystem bullet;
	
	ContactManager contactManager;
	
	private OrthographicCamera cam;
	private OrthographicCamera hud;
	
	public SpriteBatch batch;
	public ShapeRenderer shape;
	
	public World world;
	public Box2DDebugRenderer worldRenderer;
	
	public RayHandler lights;
	

	
	private GlyphLayout rotate;
	
	private BitmapFont font;
	
	private float orientation;
	private float rotation;
	private boolean debug;
	
	public GameScreen(DirectedGame game) {
		super(game);
	}

	@Override
	public void show() {
		Box2D.init();
		world = new World(new Vector2(0, 0), true);
		worldRenderer = new Box2DDebugRenderer();

		RayHandler.setGammaCorrection(true);
		lights = new RayHandler(world);
		lights.setShadows(true);
		lights.setAmbientLight(0.9f);
		//lights.setLightMapRendering(false);
		//new PointLight(lights, 512, new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1), MathUtils.random(1 ,6), MathUtils.random(-3f, 3f), MathUtils.random(-3f, 3f));
		//new PointLight(lights, 512, Color.RED, 6, 0, -1);
		new DirectionalLight(lights, 1024, new Color(1, 1, 1, 0.1f), -45);
		
		
		initEngine();
		
		initCamera();
		
		
		font = Assets.instance.fonts.med;
		rotate = new GlyphLayout();
		
		
		Gdx.input.setCatchBackKey(true);
		
	}
	
	private void initEngine() {
		
		
		//Create the engine
		engine = new Engine();
		
		//Activate the Engine systems.
		player = new PlayerSystem(this);
		

		bullet = new BulletSystem(this);
		
		cube = new CubeSystem(this);
		engine.addSystem(cube);
		
		circle = new CircleSystem(this);
		engine.addSystem(circle);
		

		engine.addSystem(player);
		
		engine.addSystem(bullet);
		
		
		contactManager = new ContactManager(circle, bullet, cube);
		world.setContactListener(contactManager);
		
	}
	
	private void initCamera() {

		//Create the Game World Camera
		cam = new OrthographicCamera();
		cam.setToOrtho(false, WIDTH, HEIGHT);
		cam.position.set(0, 0, cam.position.z);
		
		//Create the Ui Camera
		hud = new OrthographicCamera();
		hud.setToOrtho(false, 1080, 1920);
		hud.position.set(1080/2, 1920/2, hud.position.z);
		
		//Create the renderers.
		batch = new SpriteBatch();
		shape = new ShapeRenderer();
		
	}
	
	private Color selectColor() {
		switch(MathUtils.random(3)) {
		case 0: return new Color(250/255f, 128/255f, 40/255f, 1);
		case 1: return new Color(218/255f, 67/255f, 36/255f, 1);
		case 2: return new Color(49/255f, 136/255f, 183/255f, 1);
		case 3: return new Color(63/255f, 193/255f, 91/255f, 1);
		default: return new Color(Color.BLACK);
		}
	}

	private float lerp(float t, float a, float b) {
		return (a + t*(b-a));
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | 
				(Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));

		if(Gdx.app.getType() != ApplicationType.Desktop) {
			orientation = Math.max(Math.min(Gdx.input.getAccelerometerX(), 5), -5);
		} else {
			orientation = lerp(delta*2, orientation, MathUtils.clamp((Gdx.input.isKeyPressed(Keys.A) ? 5 : Gdx.input.isKeyPressed(Keys.D) ? -5 : -orientation), -5, 5));
		}
		if(Math.abs(orientation) > 0.5) { 
			cam.rotate(orientation);
			rotation += orientation;
			if(rotation >= 360) {
				rotation -= 360;
			} else if(rotation < 0) {
				rotation += 360;
			}
		}
		
		player.setRotation(rotation);
		
		rotate.setText(font, "X: " + MathUtils.round(orientation*10)/10f);
		
		if(MathUtils.random(100) == MathUtils.random(100)) {
			if(MathUtils.randomBoolean()) {
				spawnCube();
			} else {
				spawnCircle();
			}
		}
		
		
		cam.update();
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shape.setProjectionMatrix(cam.combined);
		shape.begin(ShapeType.Filled);
			
			shape.setColor(Color.BLACK);
			shape.rect(0, 0, WIDTH, HEIGHT);
		
			shape.setColor(Color.WHITE);
			shape.circle(0, 0, 3, 100);
			


			engine.update(delta);
			
		shape.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);

		lights.setCombinedMatrix(cam);
		lights.updateAndRender();
		
		if(debug == true)
		worldRenderer.render(world, cam.combined);
		
		
		hud.update();
		batch.setProjectionMatrix(hud.combined);
		batch.begin();
			/*font.setColor(Color.BLACK);
			font.draw(batch, rotate, 1080/2-rotate.width/2, 1920/4+rotate.height/2);*/

			//batch.draw(lights.getLightMapTexture(), 0, 0);
		batch.end();
		
		cam.update();
		
		world.step(delta, 6, 2);
		contactManager.update();
		
		
	}
	
	private void spawnCube() {
		CubeComponent cubeComp = new CubeComponent();
		int rot = MathUtils.random(360);
		cubeComp.x = 2.9f * MathUtils.cosDeg(rot);
		cubeComp.y = 2.9f * MathUtils.sinDeg(rot);
		cubeComp.direction = rot-180 < 0 ? rot+180 : rot-180;
		cubeComp.velocity = MathUtils.random(0.5f, 2);
		cubeComp.scale = MathUtils.random(0.5f, 0.9f);
		cubeComp.color = selectColor();
		
		cube.addCube(cubeComp);
		
	}
	
	private void spawnCircle() {
		CircleComponent circComp = new CircleComponent();
		int rot = MathUtils.random(360);
		circComp.x = 2.9f * MathUtils.cosDeg(rot);
		circComp.y = 2.9f * MathUtils.sinDeg(rot);
		circComp.direction = rot-180 < 0 ? rot+180 : rot-180;
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
		batch.dispose();
		shape.dispose();
	}

	@Override
	public InputProcessor getInputProcessor() {
		return this;
	}
	
	
	
	////////////////////////////////////////////
	//	Input Processor
	////////////////////////////////////////////

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.BACK) {
			ScreenTransition transition = ScreenTransitionSlide.init(
					0.5f, ScreenTransitionSlide.RIGHT, false, Interpolation.pow2);
			game.setScreen(new MenuScreen(game), transition);
		} 
		
		if(Gdx.app.getType() == ApplicationType.Desktop) {
			if(keycode == Keys.E) {
				debug = !debug;
			} else if(keycode == Keys.Q) {
				ScreenTransition transition = ScreenTransitionSlide.init(
						0.5f, ScreenTransitionSlide.RIGHT, false, Interpolation.pow2);
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
