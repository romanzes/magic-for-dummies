package ru.footmade.dummymagic.ui;

import ru.footmade.dummymagic.Script;
import ru.footmade.dummymagic.entities.Scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class BackgroundView extends Actor {
	private Script script;
	private TextureAtlas atlas;
	
	private String prevBackground;
	private Sprite oldBackground, newBackground;
	private Sprite fade;
	
	public BackgroundView(TextureAtlas atlas, Script script) {
		this.atlas = atlas;
		this.script = script;
	}
	
	@Override
	protected void sizeChanged() {
		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.BLACK);
		pixmap.fill();
		fade = new Sprite(new Texture(pixmap));
		pixmap.dispose();
		fade.setPosition(0, 0);
		fade.setSize(getWidth(), getHeight());
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (prevBackground != script.getCurrentScene().background) {
			prevBackground = script.getCurrentScene().background;
			newBackground = new Sprite(atlas.findRegion("bg/" + script.getCurrentScene().background));
			newBackground.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			stretchBackground(newBackground);
			
			if (script.getCurrentScene().backgroundEffect != Scene.BG_EFFECT_NONE) {
				oldBackground = new Sprite(atlas.findRegion("bg/" + script.getPreviousScene().background));
				oldBackground.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
				stretchBackground(oldBackground);
			}
		}
		
		if (script.backgroundRendered) {
			newBackground.draw(batch, parentAlpha);
		} else {
			float ratio = ((float) (System.currentTimeMillis() - script.tick)) / Script.BACKGROUND_CHANGE_TIME;
			if (ratio > 1) ratio = 1;
			switch (script.getCurrentScene().backgroundEffect) {
			case Scene.BG_EFFECT_DISSOLVE:
				oldBackground.draw(batch, parentAlpha);
				newBackground.draw(batch, parentAlpha * ratio);
				break;
			case Scene.BG_EFFECT_FADE:
				if (ratio < 0.5) {
					oldBackground.draw(batch, parentAlpha);
					ratio *= 2;
				} else {
					newBackground.draw(batch, parentAlpha);
					ratio = (1 - ratio) * 2;
				}
				fade.draw(batch, parentAlpha * ratio);
				break;
			}
		}
	}
	
	private void stretchBackground(Sprite background) {
		float newWidth = getWidth();
		float newHeight = background.getHeight() * getWidth() / background.getWidth();
		if (newHeight < getHeight()) {
			newHeight = getHeight();
			newWidth = background.getWidth() * getHeight() / background.getHeight();
		}
		background.setSize(newWidth, newHeight);
		background.setPosition((getWidth() - newWidth) / 2, (getHeight() - newHeight) / 2);
	}
}
