package ru.footmade.dummymagic;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MyGdxGame implements ApplicationListener {
	private Script script;
	private Renderer renderer;
	
	@Override
	public void create() {
		script = new Script();
		renderer = new Renderer(script);
		Gdx.input.setInputProcessor(renderer.stage);
		renderer.textFrame.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				script.next();
			}
		});
		script.start();
	}

	@Override
	public void dispose() {
		renderer.dispose();
	}

	@Override
	public void render() {
		renderer.render();
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
