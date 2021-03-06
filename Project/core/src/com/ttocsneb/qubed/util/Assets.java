package com.ttocsneb.qubed.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.ttocsneb.qubed.game.spawn.json.SpawnPattern;

/**
 * Contains all of the assets for the game.
 * 
 * @author TtocsNeb
 *
 */
public class Assets implements Disposable, AssetErrorListener {

	private static final String TAG = Assets.class.getName();

	private AssetManager assetManager;
	private boolean loaded;

	public AssetTextures textures;
	public AssetSounds sounds;
	public AssetFonts fonts;
	public AssetAtlas atlases;
	public AssetParticles particles;
	public AssetPatterns patterns;

	private static final String[] ATLASES = {
		"skins/uiskin.atlas"
	};

	/**
	 * Contains all atlases.
	 * 
	 * @author TtocsNeb
	 *
	 */
	public class AssetAtlas {

		public final TextureAtlas uiskin;
		public final TextureAtlas textures;

		public AssetAtlas(AssetManager am) {
			textures = am.get(Global.TEXTURE_ATLAS);

			uiskin = am.get(ATLASES[0]);
		}

	}

	/**
	 * Contains all Textures
	 * 
	 * @author TtocsNeb
	 *
	 */
	public class AssetTextures {


		public final AtlasRegion speaker;
		public final AtlasRegion speakerOff;

		public final AtlasRegion healthPowerup;
		public final AtlasRegion slowPowerup;
		
		public final AtlasRegion finger;

		public AssetTextures(TextureAtlas atlas) {

			speaker = atlas.findRegion("menu/speaker_on");
			speakerOff = atlas.findRegion("menu/speaker_off");

			healthPowerup = atlas.findRegion("game/healthPwr");
			slowPowerup = atlas.findRegion("game/healthSlw");
			
			finger = atlas.findRegion("menu/finger");
		}
	}

	/**
	 * Contains all Particle effects.
	 * 
	 * @author TtocsNeb
	 *
	 */
	public class AssetParticles {

		public final ParticleEffect triangleExp;
		public final ParticleEffect squareExp;
		public final ParticleEffect circleExp;

		public AssetParticles(TextureAtlas atlas) {
			triangleExp = new ParticleEffect();
			triangleExp.load(
					Gdx.files.internal("particles/explosionTriangle.p"), atlas);

			squareExp = new ParticleEffect();
			squareExp.load(Gdx.files.internal("particles/explosionSquare.p"),
					atlas);

			circleExp = new ParticleEffect();
			circleExp.load(Gdx.files.internal("particles/explosionCircle.p"),
					atlas);
		}
	}

	/**
	 * Contains all Sounds.
	 * 
	 * @author TtocsNeb
	 *
	 */
	public class AssetSounds {
		
		public final Music music;
		
		public final Music slowMusic;

		public AssetSounds(AssetManager am) {
			music = am.get("music/Voice Over Under.mp3", Music.class);
			
			slowMusic = am.get("music/Voice Over Under - slo.mp3", Music.class);
		}

	}
	
	public class AssetPatterns {
		public final SpawnPattern[] all;
		
		public final SpawnPattern opposite;
		public final SpawnPattern oppositeThing;
		public final SpawnPattern repeatLeft;
		public final SpawnPattern repeatRight;
		
		public AssetPatterns() {
			Json json = new Json();
			
			all = new SpawnPattern[4];
			
			opposite = all[0] = json.fromJson(SpawnPattern.class, Gdx.files.internal("pattern/opposite.json"));
			repeatLeft = all[1] = json.fromJson(SpawnPattern.class, Gdx.files.internal("pattern/repeat.json"));
			repeatRight = all[2] = json.fromJson(SpawnPattern.class, Gdx.files.internal("pattern/repeatRight.json"));
			oppositeThing = all[3] = json.fromJson(SpawnPattern.class, Gdx.files.internal("pattern/oppositeOther.json"));
		}
	}

	/**
	 * Contains all fonts.
	 * 
	 * @author TtocsNeb
	 *
	 */
	public class AssetFonts implements Disposable {

		private static final String arial = "font/arialHeavy.ttf";

		public final BitmapFont small;
		public final BitmapFont med;
		public final BitmapFont large;
		public final BitmapFont huge;

		public AssetFonts() {
			FreeTypeFontGenerator gen = new FreeTypeFontGenerator(
					Gdx.files.internal(arial));
			FreeTypeFontParameter par = new FreeTypeFontParameter();

			par.size = 32;
			small = gen.generateFont(par);

			par.size = 64;
			med = gen.generateFont(par);

			par.size = 128;
			large = gen.generateFont(par);

			par.size = 256;
			huge = gen.generateFont(par);

			gen.dispose();

			small.getRegion().getTexture()
					.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			med.getRegion().getTexture()
					.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			large.getRegion().getTexture()
					.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			huge.getRegion().getTexture()
					.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}

		@Override
		public void dispose() {
			small.dispose();
			med.dispose();
			large.dispose();
			huge.dispose();
		}
	}

	/**
	 * Initialize assets
	 * 
	 * @param assetManager
	 */
	public void init(AssetManager assetManager) {
		Gdx.app.debug("Assets", "Init Assets!");

		this.assetManager = assetManager;
		loaded = false;
		// set asset manager error handler.
		assetManager.setErrorListener(this);
		// load texture atlas
		assetManager.load(Global.TEXTURE_ATLAS, TextureAtlas.class);

		for (String s : ATLASES) {
			assetManager.load(s, TextureAtlas.class);
		}

		assetManager.load("music/Voice Over Under.mp3", Music.class);
		assetManager.load("music/Voice Over Under - slo.mp3", Music.class);

	}

	/**
	 * Check if loaded.
	 * 
	 * @return
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * Waits until all assets are loaded.
	 */
	public void finishLoading() {
		assetManager.finishLoading();
		while (getProgress() != 1);
	}

	/**
	 * Get the progress in percent of completion.
	 * 
	 * <pre>
	 * <b>Note: no assets are loaded until returns 1.0</b>
	 * </pre>
	 * 
	 * @return the progress in percent of completion.
	 */
	public float getProgress() {
		// return 100% if the assets are loaded
		if (loaded) return 1;

		assetManager.update();

		// return the progress if not yet loaded.
		if (assetManager.getProgress() < 1) return assetManager.getProgress();

		loaded = true;

		Gdx.app.debug(TAG,
				"# of assets loadied: " + assetManager.getAssetNames().size);
		for (String a : assetManager.getAssetNames())
			Gdx.app.debug(TAG, "asset: " + a);
		TextureAtlas atlas = assetManager.get(Global.TEXTURE_ATLAS);

		// enable texture filtering for pixel smoothing.
		for (Texture t : atlas.getTextures()) {
			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}

		for (String s : ATLASES) {
			TextureAtlas a = assetManager.get(s);

			for (Texture t : a.getTextures()) {
				t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			}
		}

		// create game resource objects
		sounds = new AssetSounds(assetManager);
		textures = new AssetTextures(atlas);
		particles = new AssetParticles(atlas);
		atlases = new AssetAtlas(assetManager);
		patterns = new AssetPatterns();

		fonts = new AssetFonts();
		loaded = true;
		return 1;
	}

	@Override
	public void error(@SuppressWarnings("rawtypes") AssetDescriptor asset,
			Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset '" + asset.fileName + "'",
				throwable);
		Gdx.app.exit();
	}

	@Override
	public void dispose() {
		Gdx.app.debug("Assets", "Destroyed");
		assetManager.dispose();
		fonts.dispose();
		loaded = false;
		
	}

}
