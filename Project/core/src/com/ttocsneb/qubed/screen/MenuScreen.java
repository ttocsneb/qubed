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
import com.ttocsneb.qubed.screen.transitions.ScreenTransition;
import com.ttocsneb.qubed.screen.transitions.ScreenTransitionSlide;
import com.ttocsneb.qubed.util.Assets;
import com.ttocsneb.qubed.util.Global;

public class MenuScreen extends AbstractGameScreen implements GestureListener {
	
	SpriteBatch batch;
	ShapeRenderer shape;
	
	BitmapFont font;
	GlyphLayout title;
	
	OrthographicCamera cam;
	
	TextureRegion speaker;
	TextureRegion speakerOff;
	boolean touched;
	
	//Arrow Animation Variables.
	float animInterp;
	int animProgress;
	boolean animDir;
	
	float x;

	public MenuScreen(DirectedGame game) {
		super(game);
	}

	@Override
	public void show() {

		
		
		speaker = Assets.instance.textures.speaker;
		speakerOff = Assets.instance.textures.speakerOff;
		
		font = Assets.instance.fonts.huge;
		font.setColor(Color.BLACK);
		
		title = new GlyphLayout(font, "QUBED");
		
		cam = new OrthographicCamera();
		cam.setToOrtho(false, 1080, 1920);
		
		batch = new SpriteBatch();
		//batch.setShader(shader);
		shape = new ShapeRenderer();
		

		
		//Initiate arrow animation variables
		animProgress = 0;
		animInterp = 0;
		animDir = true;
		
		Gdx.app.debug("MenuScreen", Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer) ? "This device has an accelerometer" : "This device does not have an accelerometer");
	}

	@Override
	public void render(float delta) {
		
		Gdx.app.debug("MenuScreen", "Playing: " + (Assets.instance.sounds.music.isPlaying() ? "True" : "False"));
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | 
				(Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
		
		
		cam.position.set(cam.position.lerp(new Vector3(MathUtils.clamp(x+540, 540, 1080+540), cam.position.y, cam.position.z), delta*10));
		
		//Progress the animation Interpolation
		animInterp += (animDir ? -1 : 1) * delta;
		//Switch the direction of interpolation when the limit is reached.
		if(animInterp >= 1 || animInterp <= 0) {
			animDir = !animDir;
			animInterp = MathUtils.clamp(animInterp, 0, 1);
		}
		//Apply interpolation to the animation progress variable to be used for renderering.
		animProgress = MathUtils.round(Interpolation.pow2.apply(animInterp) * 64);
		
		cam.update();
		shape.setProjectionMatrix(cam.combined);
		shape.begin(ShapeType.Filled);
		
			//Draw the White Background
			shape.setColor(Color.WHITE);
			shape.rect(0, 0, 2160, 1920);

			
			//Draw the arrow to the screen.
			shape.setColor(Color.BLACK);
			shape.triangle(885 + animProgress, 1920/2-64 - (animProgress/64f * 32), 
					885 + animProgress, 1920/2+64 + (animProgress/64f * 32), 
					885 + animProgress+128, 1920/2);
		shape.end();
		
		if(Gdx.input.isTouched()) {
			Vector3 v3 = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
			if(!touched && v3.x > 0  && v3.x < speaker.getRegionWidth()) {
				touched = true;
			} else if(touched && !(v3.x > 0  && v3.x < speaker.getRegionWidth()))
				touched = false;
		} else if(touched) {
			touched = false;
			Global.Config.MUTE = !Global.Config.MUTE;
			if(Global.Config.MUTE)
				Assets.instance.sounds.music.pause();
			else
				Assets.instance.sounds.music.play();
			Global.Config.save();
		}
		
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		
		
			//Draw the title to the screen.
			font.draw(batch, title, 1080/2-title.width/2, 1920*3/4f);
		
			Assets.instance.fonts.med.setColor(Color.BLACK);
			
			TextureRegion region = Global.Config.MUTE ? speakerOff : speaker;
			
			batch.draw(region.getTexture(), 0, 1920-region.getRegionHeight(), 0, 0, region.getRegionWidth(),
					region.getRegionHeight(), 1, 1, 0, region.getRegionX(), region.getRegionY(),
					region.getRegionWidth(), region.getRegionHeight(), false, false);
			
		batch.end();
		
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
		return new GestureDetector(this);
	}
	
	////////////////////////////////////////////////
	// Gesture Listener
	////////////////////////////////////////////////

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
		
		//scale the screen position to game resolution (1080x1920)
		Vector3 v3 = cam.unproject(new Vector3(deltaX, deltaY, 0));
		Vector2 v2 = new Vector2(v3.x, v3.y);
		
		//Add the delta to the screen's position.
		this.x -= v2.x;
		
		//if the screen has moved enough, start the game.
		if(this.x > 135) {
			this.x = 1080;
			ScreenTransition transition = ScreenTransitionSlide.init(
					0.125f, ScreenTransitionSlide.LEFT, true, Interpolation.pow2);
			game.setScreen(new GameScreen(game), transition);
		}
		
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		
		if(this.x >= 1080 ) {
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
