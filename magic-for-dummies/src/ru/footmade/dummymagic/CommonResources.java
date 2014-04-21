package ru.footmade.dummymagic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Disposable;

public class CommonResources implements Disposable {
	private static CommonResources _instance;
	
	private TextureAtlas atlas;
	private FreeTypeFontGenerator fontGenerator;
	
	private static final String RUSSIAN_CHARS = "�������������������������������������Ũ��������������������������";
	
	public static CommonResources getInstance() {
		if (_instance == null) {
			_instance = new CommonResources();
		}
		return _instance;
	}
	
	public CommonResources() {
		atlas = new TextureAtlas(Gdx.files.internal("img/pack.atlas"));
		fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fnt/mtcorsva.ttf"));
	}
	
	public BitmapFont getFont(int size) {
		FreeTypeFontParameter fontParam = new FreeTypeFontParameter();
		fontParam.size = size;
		fontParam.minFilter = TextureFilter.Linear;
		fontParam.magFilter = TextureFilter.Linear;
		fontParam.characters = fontParam.characters + RUSSIAN_CHARS;
		return fontGenerator.generateFont(fontParam);
	}
	
	public TextureAtlas getTextureAtlas() {
		return atlas;
	}

	@Override
	public void dispose() {
		atlas.dispose();
		fontGenerator.dispose();
	}
}
