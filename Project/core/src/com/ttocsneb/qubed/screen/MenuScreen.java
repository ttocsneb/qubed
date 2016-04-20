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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.ttocsneb.qubed.screen.transitions.ScreenTransition;
import com.ttocsneb.qubed.screen.transitions.ScreenTransitionSlide;
import com.ttocsneb.qubed.util.Assets;
import com.ttocsneb.qubed.util.Global;

/**
 * The Menu Screen.
 * 
 * @author TtocsNeb
 *
 */
public class MenuScreen extends AbstractGameScreen implements GestureListener {

	private static final float StageX = 360;
	private static final float StageY = 640;
	
	
	// Renderer
	private Stage stage;
	private SpriteBatch batch;
	private ShapeRenderer shape;

	// Font stuff.
	private BitmapFont font;
	private GlyphLayout title;

	// Camera
	private OrthographicCamera cam;

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

		stage = new Stage(new ScalingViewport(Scaling.stretch, StageX, StageY), Global.batch);
		initStage();
		batch = Global.batch;
		cam = new OrthographicCamera();
		cam.setToOrtho(false, 1080, 1920);
		
		// Load fonts.
		font = Assets.instance.fonts.huge;
		font.setColor(Color.DARK_GRAY);

		// Create the Title
		title = new GlyphLayout(font, "QUBED");

		// Init the renderers.
		shape = Global.shape;

		// Initiate arrow animation variables
		Gdx.app.debug(
				"MenuScreen",
				Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer) ? "This device has an accelerometer"
						: "This device does not have an accelerometer");
	}
	
	private void initStage() {
		TextButton b = new TextButton("PLAY", Assets.instance.skin);
		b.setPosition(StageX/2-b.getWidth()/2, StageY/2);
		
		stage.addActor(b);
	}

	@Override
	public void render(float delta) {
		// Clear the screen.
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT
				| GL20.GL_DEPTH_BUFFER_BIT
				| (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV
						: 0));

		stage.act(delta);

		//Draw the background
		shape.setProjectionMatrix(cam.combined);
		shape.begin(ShapeType.Filled);
		shape.setColor(Color.WHITE);
		shape.rect(cam.position.x - cam.viewportWidth / 2, cam.position.y
				- cam.viewportHeight / 2, cam.viewportWidth, cam.viewportHeight);
		shape.end();
		
		//Draw the title
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
			font.draw(batch, title, 540-title.width/2/*(1920/2)*/, 1680/*(1920*7/8)*/);
		batch.end();
		
		stage.draw();
		
	}

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
		return stage;
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

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {

		// scale the screen position to game resolution (1080x1920)
		Vector3 v3 = cam.unproject(new Vector3(deltaX, deltaY, 0));
		Vector2 v2 = new Vector2(v3.x, v3.y);

		// Add the delta to the screen's position.
		this.x -= v2.x;

		// if the screen has moved enough, start the game.
		if (this.x > 135) {
			this.x = 1080;
			ScreenTransition transition = ScreenTransitionSlide.init(0.125f,
					ScreenTransitionSlide.LEFT, true, Interpolation.pow2);
			game.setScreen(new GameScreen(game), transition);
		}

		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {

		// Reset the pan.. I think.
		if (this.x >= 1080) {
			this.x = 1080;
		} else {
			this.x = 0;
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
