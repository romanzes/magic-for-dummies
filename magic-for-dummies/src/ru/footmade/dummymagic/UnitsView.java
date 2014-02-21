package ru.footmade.dummymagic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.footmade.dummymagic.Script.Unit;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class UnitsView extends Actor {
	private List<Unit> units;
	
	private TextureAtlas atlas;
	private Map<String, Sprite> unitCache = new HashMap<String, Sprite>();
	
	public UnitsView(TextureAtlas atlas, List<Unit> units) {
		this.atlas = atlas;
		this.units = units;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		for (Unit unit : units) {
			Sprite unitSprite = unitCache.get(unit.name);
			if (unitSprite == null) {
				AtlasRegion region = atlas.findRegion("pers/" + unit.name);
				region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
				unitSprite = new Sprite(region);
				unitSprite.setSize(unitSprite.getWidth() * getHeight() / unitSprite.getHeight(), getHeight());
				unitSprite.setPosition((getWidth() - unitSprite.getWidth()) / 2, 0);
				unitCache.put(unit.name, unitSprite);
			}
			unitSprite.draw(batch, parentAlpha);
		}
	}
}
