package com.ttocsneb.qubed.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Disposable;

public class Assets implements Disposable, AssetErrorListener {
	private static final String TAG = Assets.class.getName();
	
	public static final Assets instance = new Assets();
	
	private AssetManager assetManager;
	private boolean loaded;
	
	public AssetTextures textures;
	public AssetSounds sounds;
	public AssetFonts fonts;
	public AssetAtlas atlases;
	
	private static final String[] ATLASES = {"skins/uiskin.atlas"};
	public class AssetAtlas {
		
		public final TextureAtlas uiskin;
		public final TextureAtlas textures;
		
		public AssetAtlas(AssetManager am) {
			textures = am.get(Global.TEXTURE_ATLAS);
			
			uiskin = am.get(ATLASES[0]);
		}
		
	}
	
	public class AssetTextures {
		
		public final AtlasRegion background;
		
		public final AtlasRegion speaker;
		public final AtlasRegion speakerOff;

		public AssetTextures(TextureAtlas atlas) {
			background = atlas.findRegion("Background");
			
			speaker = atlas.findRegion("speaker_on");
			speakerOff = atlas.findRegion("speaker_off");
		}
	}
	
	public class AssetSounds {
		
		//public final Sound load;
		public final Music music;
		
		
		public AssetSounds(AssetManager am) {
			//load = am.get("sounds/Load.wav", Sound.class);
			music = am.get("music/Blip Stream.mp3", Music.class);
		}
		
	}
	
	public class AssetFonts implements Disposable {
		
		private static final String arial = "font/arialHeavy.ttf";
		
		public final BitmapFont small;
		public final BitmapFont med;
		public final BitmapFont large;
		public final BitmapFont huge;
		
		public AssetFonts() {
			FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(arial));
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
			
			small.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			med.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			large.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			huge.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}

		@Override
		public void dispose() {
			small.dispose();
			med.dispose();
			large.dispose();
			huge.dispose();
		}
	}
	
	public void init(AssetManager assetManager) {
		Gdx.app.debug("Assets", "Init Assets!");
		
		this.assetManager = assetManager;
		loaded = false;
		//set asset manager error handler.
		assetManager.setErrorListener(this);
		//load texture atlas
		assetManager.load(Global.TEXTURE_ATLAS, TextureAtlas.class);
	
		for(String s : ATLASES) {
			assetManager.load(s, TextureAtlas.class);
		}
	
		assetManager.load("music/Blip Stream.mp3", Music.class);
	
		//start loading assets and wait until finished.
	
		
		assetManager.finishLoading();
		
		
		Gdx.app.debug(TAG, "# of assets loadied: " + assetManager.getAssetNames().size);
		for(String a : assetManager.getAssetNames())
			Gdx.app.debug(TAG, "asset: " + a);
		TextureAtlas atlas = assetManager.get(Global.TEXTURE_ATLAS);
		
		//enable texture filtering for pixel smoothing.
		for(Texture t : atlas.getTextures()) {
			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
		
		for(String s : ATLASES) {
			TextureAtlas a = assetManager.get(s);
			
			for(Texture t :  a.getTextures()) {
				t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			}
		}
		
		//create game resource objects
		sounds = new AssetSounds(assetManager);
		textures = new AssetTextures(atlas);
		atlases = new AssetAtlas(assetManager);
		
	

		fonts = new AssetFonts();
		loaded = true;
	}
	
	public boolean isLoaded() {
		return loaded;
	}
	
	@Override
	public void error(@SuppressWarnings("rawtypes") AssetDescriptor asset, Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset '" + asset.fileName + "'", throwable);
		Gdx.app.exit();
	}

	@Override
	public void dispose() {
		assetManager.dispose();
		fonts.dispose();
		loaded = false;
	}

}
