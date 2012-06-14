package com.anstrat.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Options {

	public final static boolean DEBUG_SHOW_OWNER = false;
	public static boolean soundOn = false;
	public static Preferences prefs;
	public static float speedFactor = 1f;
	public static boolean showFps = false;
	
	public static void loadPreferences(){
		System.out.println("Loading preferences.");
		
		prefs = Gdx.app.getPreferences("settings");
		if(prefs.contains("sound")){
			System.out.println("\tSound: "+prefs.getBoolean("sound"));
			soundOn = prefs.getBoolean("sound");
			if(soundOn)
				Main.getInstance().menuMusic.play();
		}
		prefs.clear();
	}
	
	public static void savePreferences(){
		System.out.println("Saving preferences.");
		
		prefs.putBoolean("sound", soundOn);
		prefs.flush();
	}
}
