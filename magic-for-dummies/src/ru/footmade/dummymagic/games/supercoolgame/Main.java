package ru.footmade.dummymagic.games.supercoolgame;

import ru.footmade.dummymagic.CommonResources;
import ru.footmade.dummymagic.games.MiniGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends MiniGame {
	private float scrW, scrH;
	private BitmapFont font;
	private SpriteBatch batch;

	@Override
	public void start(String argument) {
		scrW = Gdx.graphics.getWidth();
		scrH = Gdx.graphics.getHeight();
		CommonResources resources = CommonResources.getInstance();
		batch = new SpriteBatch();
		batch.setProjectionMatrix(resources.getCamera().combined);
		font = resources.getFont(50);
	}

	@Override
	public void render() {
		if (Gdx.input.justTouched()) {
			if (Gdx.input.getX() < scrW / 2)
				exit(0);
			else
				exit(1);
		}
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		
		String title = "Super Cool Game!";
		font.setColor(Color.BLUE);
		font.draw(batch, title, (scrW - font.getBounds(title).width) / 2, scrH - font.getLineHeight());
		font.setColor(Color.GREEN);
		font.drawMultiLine(batch, "Press\nhere\nto win", 0, scrH / 2, scrW / 2, HAlignment.CENTER);
		font.setColor(Color.RED);
		font.drawMultiLine(batch, "Press\nhere\nto lose", scrW / 2, scrH / 2, scrW / 2, HAlignment.CENTER);
		
		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		font.dispose();
	}
}
