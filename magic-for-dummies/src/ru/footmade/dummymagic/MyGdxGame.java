package ru.footmade.dummymagic;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;

public class MyGdxGame implements ApplicationListener {
	private Script script;
	private Renderer renderer;
	private AudioPlayer soundPlayer;
	
	@Override
	public void create() {
		script = new Script();
		renderer = new Renderer(script);
		Gdx.input.setInputProcessor(renderer.stage);
		soundPlayer = new AudioPlayer(script);
		script.start();
	}

	@Override
	public void dispose() {
		renderer.dispose();
		soundPlayer.dispose();
	}

	@Override
	public void render() {
		script.update();
		renderer.render();
		soundPlayer.update();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
