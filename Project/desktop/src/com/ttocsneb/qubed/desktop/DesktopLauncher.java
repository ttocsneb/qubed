package com.ttocsneb.qubed.desktop;

import java.io.File;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.ttocsneb.qubed.Main;

public class DesktopLauncher {
	
	private static final boolean pack = true, debug = false;
	private static final String textureDir = "textureRaw";
	
	public static void main (String[] arg) {
		
		//check if we should re-pack the textures, and if the directory is valid.
		if(pack && new File(textureDir).isDirectory() == true) {
			
			//Go through the items of the texture folder
			for(File file : new File(textureDir).listFiles()) {
				//check if the file is a directory
				if(file.isDirectory()) {
					//Pack the textures in the current directory
					Settings settings = new Settings();
					settings.debug = debug;
					TexturePacker.process(settings, file.getPath(),
							"../android/assets/textures",
							file.getPath().substring(textureDir.length()+1));
				}
			}
			
		}
		
		//Start the application.
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 394;
		config.height = 700;
		new LwjglApplication(new Main(), config);
	}
}
