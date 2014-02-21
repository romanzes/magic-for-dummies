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
				unitCache.put(unit.name, unitSprite);
			}
			float x;
			switch (unit.place) {
			case Unit.PLACE_LEFT:
				x = getWidth() / 6;
				break;
			case Unit.PLACE_RIGHT:
				x = getWidth() * 5 / 6;
				break;
			case Unit.PLACE_CENTER:
			default:
				x = getWidth() / 2;
				break;
			}
			x -= unitSprite.getWidth() / 2;
			if (x < 0) x = 0;
			if (x > getWidth() - unitSprite.getWidth()) x = getWidth() - unitSprite.getWidth();
			unitSprite.setPosition(x, 0);
			unitSprite.draw(batch, parentAlpha);
		}
	}
}
