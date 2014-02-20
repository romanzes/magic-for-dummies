package ru.footmade.dummymagic;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "magic-for-dummies";
		cfg.useGL20 = true;
		cfg.width = 1280;
		cfg.height = 768;
		
		/*Settings settings = new Settings();
		settings.pot = false;
		settings.maxWidth = 2048;
		TexturePacker2.process(settings, "../images", "../magic-for-dummies-android/assets/img", "pack");*/
		
		new LwjglApplication(new MyGdxGame(), cfg);
	}
}
