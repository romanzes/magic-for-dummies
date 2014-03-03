package ru.footmade.dummymagic;

import java.util.HashMap;
import java.util.Map;

import ru.footmade.dummymagic.Script.Place;
import ru.footmade.dummymagic.Script.Unit;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class UnitsView extends Actor {
	private Script script;
	
	private TextureAtlas atlas;
	private Map<String, Sprite> unitCache = new HashMap<String, Sprite>();
	
	public UnitsView(TextureAtlas atlas, Script script) {
		this.atlas = atlas;
		this.script = script;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		for (Unit unit : script.units.values()) {
			Sprite unitSprite = unitCache.get(unit.name);
			if (unitSprite == null) {
				AtlasRegion region = atlas.findRegion("pers/" + unit.name);
				region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
				unitSprite = new Sprite(region);
				float ratio = getHeight() / unitSprite.getHeight();
				unitSprite.setSize(unitSprite.getWidth() * ratio, unitSprite.getHeight() * ratio);
				unitCache.put(unit.name, unitSprite);
			}
			float x, y;
			Vector2 placeEnd = placeToCoords(unitSprite, unit.currentAction.placeEnd);
			if (script.unitsRendered) {
				x = placeEnd.x;
				y = placeEnd.y;
			} else {
				float progress = ((float) (System.currentTimeMillis() - script.tick)) / Script.PERSON_MOVE_TIME;
				Vector2 placeStart = placeToCoords(unitSprite, unit.currentAction.placeStart);
				x = placeStart.x + (placeEnd.x - placeStart.x) * progress;
				y = placeStart.y + (placeEnd.y - placeStart.y) * progress;
			}
			unitSprite.setPosition(x, y);
			unitSprite.draw(batch, parentAlpha);
		}
	}
	
	private Vector2 placeToCoords(Sprite sprite, Script.Place place) {
		float x, y;
		switch (place.horizontal) {
		case Place.PLACE_OUTER_LEFT:
			x = -sprite.getWidth();
			break;
		case Place.PLACE_LEFT:
			x = Math.max(0, getWidth() / 6 - sprite.getWidth() / 2);
			break;
		case Place.PLACE_RIGHT:
			x = Math.min(getWidth() - sprite.getWidth(), getWidth() * 5 / 6 - sprite.getWidth() / 2);
			break;
		case Place.PLACE_OUTER_RIGHT:
			x = getWidth();
			break;
		case Place.PLACE_CENTER:
		default:
			x = (getWidth() - sprite.getWidth()) / 2;
			break;
		}
		switch (place.vertical) {
		case Place.PLACE_OUTER_BOTTOM:
			y = -sprite.getHeight();
			break;
		case Place.PLACE_BOTTOM:
			y = Math.max(0, getHeight() / 6 - sprite.getHeight() / 2);
			break;
		case Place.PLACE_TOP:
			y = Math.min(getHeight() - sprite.getHeight(), getHeight() * 5 / 6 - sprite.getHeight() / 2);
			break;
		case Place.PLACE_OUTER_TOP:
			y = getHeight();
			break;
		case Place.PLACE_CENTER:
		default:
			y = (getHeight() - sprite.getHeight()) / 2;
			break;
		}
		return new Vector2(x, y);
	}
}
