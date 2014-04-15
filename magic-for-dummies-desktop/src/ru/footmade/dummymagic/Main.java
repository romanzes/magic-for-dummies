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
		
		new LwjglApplication(new MyGdxGame(), cfg);
	}
}
