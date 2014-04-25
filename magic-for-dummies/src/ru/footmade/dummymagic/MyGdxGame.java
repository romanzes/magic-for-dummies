package ru.footmade.dummymagic;

import com.badlogic.gdx.ApplicationListener;

public class MyGdxGame implements ApplicationListener {
	private Script script;
	private Renderer renderer;
	private AudioPlayer soundPlayer;
	
	@Override
	public void create() {
		script = new Script();
		renderer = new Renderer(script);
		soundPlayer = new AudioPlayer(script);
		script.start();
	}

	@Override
	public void dispose() {
		renderer.dispose();
		soundPlayer.dispose();
		CommonResources.getInstance().dispose();
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
