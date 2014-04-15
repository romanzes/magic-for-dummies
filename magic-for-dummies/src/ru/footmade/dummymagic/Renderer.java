package ru.footmade.dummymagic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Disposable;

public class Renderer implements Disposable {
	private static final float SCREEN_HEIGHT = 1000;
	private static final float FRAME_HEIGHT = 340;
	private static final float FRAME_PADDING = 30;
	private static final int FONT_HEIGHT = 60;
	private static final float CHOICE_WIDTH = 0.7f;
	
	private static final String RUSSIAN_CHARS = "àáâãäå¸æçèéêëìíîïðñòóôõö÷øùúûüýþÿÀÁÂÃÄÅ¨ÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞß";
	
	private Script script;

	private float scrW, scrH;
	
	private OrthographicCamera camera;
	private TextureAtlas atlas;
	private BitmapFont font;
	
	public Stage stage;
	public Container textFrame;
	public ColoredText text;
	public Table choicesList;
	private TextButtonStyle choiceStyle;
	private BackgroundView backgroundView;
	private UnitsView unitsView;
	
	public Renderer(Script script) {
		this.script = script;
		
		scrH = SCREEN_HEIGHT;
		scrW = Gdx.graphics.getWidth() * scrH / Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(scrW, scrH);
		camera.translate(scrW / 2, scrH / 2);
		camera.update();
		
		atlas = new TextureAtlas(Gdx.files.internal("img/pack.atlas"));
		
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fnt/mtcorsva.ttf"));
		FreeTypeFontParameter fontParam = new FreeTypeFontParameter();
		fontParam.size = FONT_HEIGHT;
		fontParam.minFilter = TextureFilter.Linear;
		fontParam.magFilter = TextureFilter.Linear;
		fontParam.characters = fontParam.characters + RUSSIAN_CHARS;
		font = generator.generateFont(fontParam);
		generator.dispose();
		
		stage = new Stage(scrW, scrH);
		
		backgroundView = new BackgroundView(atlas, script);
		backgroundView.setSize(scrW, scrH);
		stage.addActor(backgroundView);
		
		unitsView = new UnitsView(atlas, script);
		unitsView.setSize(scrW, scrH);
		stage.addActor(unitsView);
		
		text = new ColoredText(font);
		
		textFrame = new Container(text);
		textFrame.fill();
		NinePatch frame = atlas.createPatch("gui/frame");
		frame.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		textFrame.setBackground(new NinePatchDrawable(frame));
		textFrame.setPosition(FRAME_PADDING, FRAME_PADDING);
		textFrame.setSize(scrW - FRAME_PADDING * 2, FRAME_HEIGHT);
		stage.addActor(textFrame);
		textFrame.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Renderer.this.script.next();
			}
		});
		
		Drawable listItem = new NinePatchDrawable(atlas.createPatch("gui/list_item"));
		Drawable listItemPressed = new NinePatchDrawable(atlas.createPatch("gui/list_item_pressed"));
		choiceStyle = new TextButtonStyle(listItem, listItemPressed, listItemPressed, font);
		choiceStyle.fontColor = Color.BLUE;
		choiceStyle.downFontColor = Color.WHITE;
		
		choicesList = new Table();
		choicesList.setVisible(false);
		stage.addActor(choicesList);
	}
	
	private void refreshText() {
		textFrame.setVisible(script.getCurrentText().length() > 0 && script.unitsRendered);
		if (script.currentScene != oldScene) {
			text.loadMarkedText(script.getCurrentText());
		}
		text.setTextLimit(script.getCharCountToDraw());
	}
	
	private void refreshChoices() {
		if (script.currentScene != oldScene) {
			java.util.List<Button> buttons = script.getCurrentButtons();
			choicesList.setVisible(buttons.size() > 0);
			if (buttons.size() > 0) {
				choicesList.clearChildren();
				for (int i = 0; i < buttons.size(); i++) {
					TextButton button = new TextButton(buttons.get(i).text, choiceStyle);
					final int buttonIndex = i;
					button.addListener(new ClickListener() {
						@Override
						public void clicked(InputEvent event, float x, float y) {
							script.choose(buttonIndex);
						}
					});
					choicesList.add(button).size(scrW * CHOICE_WIDTH, button.getHeight());
					choicesList.row();
				}
				choicesList.setSize(scrW, choicesList.getPrefHeight());
				choicesList.setPosition(0, (scrH - choicesList.getHeight()) / 2);
			}
		}
	}
	
	private int oldScene = -1;
	
	public void render() {
		refreshText();
		refreshChoices();
		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        
        oldScene = script.currentScene;
	}

	@Override
	public void dispose() {
		atlas.dispose();
		font.dispose();
		stage.dispose();
	}
}
