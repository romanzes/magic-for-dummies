package ru.footmade.dummymagic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;

public class Script {
	public static final int BACKGROUND_CHANGE_TIME = 1000;
	public static final int PERSON_MOVE_TIME = 700;
	public static final int DRAW_CHAR_TIME = 30;
	
	private Scene[] scenes;
	public int currentScene;
	public int prevScene;
	
	public long tick;
	public boolean backgroundRendered;
	public boolean textRendered;
	public boolean unitsRendered;
	
	public Map<String, Unit> units = new HashMap<String, Unit>();
	
	public Script() {
		String rawScript = Gdx.files.internal("vn.vn").readString("UTF-8");
		String[] rawScenes = rawScript.split("((\\r\\n?)|(\\n\\r?))\\1");
		scenes = new Scene[rawScenes.length];
		for (int i = 0; i < rawScenes.length; i++) {
			scenes[i] = new Scene(rawScenes[i]);
		}
	}
	
	public void start() {
		setScene(0);
	}
	
	public void update() {
		if (backgroundRendered) {
			if (unitsRendered) {
				if (getCurrentText().length() == 0)
					next();
			} else {
				boolean hasAnimations = false;
				for (Unit unit : units.values()) {
					if (unit.currentAction.placeStart != unit.currentAction.placeEnd
							|| unit.currentAction.effect != UnitAction.EFFECT_NONE)
						hasAnimations = true;
				}
				if (hasAnimations) {
					if (System.currentTimeMillis() - tick > PERSON_MOVE_TIME)
						unitsRendered = true;
				} else
					unitsRendered = true;
				if (unitsRendered) {
					tick = System.currentTimeMillis();
					List<String> removeCandidates = new ArrayList<String>();
					for (Unit unit : units.values()) {
						if (unit.currentAction.state == UnitAction.STATE_HIDE)
							removeCandidates.add(unit.name);
						else {
							unit.currentAction.placeStart = unit.currentAction.placeEnd;
							unit.currentAction.effect = UnitAction.EFFECT_NONE;
						}
					}
					for (String name : removeCandidates) {
						units.remove(name);
					}
				}
			}
		} else {
			Scene scene = scenes[currentScene];
			if (scene.backgroundEffect == Scene.BG_EFFECT_NONE) {
				backgroundRendered = true;
				tick = System.currentTimeMillis();
			} else {
				if (System.currentTimeMillis() - tick > BACKGROUND_CHANGE_TIME) {
					backgroundRendered = true;
					tick = System.currentTimeMillis();
				}
			}
		}
	}
	
	public Scene getCurrentScene() {
		return scenes[currentScene];
	}
	
	public Scene getPreviousScene() {
		return scenes[prevScene];
	}
	
	public String getCurrentText() {
		return getCurrentScene().text;
	}
	
	public List<Button> getCurrentButtons() {
		return getCurrentScene().buttons;
	}
	
	private void goLabel(String label) {
		for (int i = 0; i < scenes.length; i++) {
			if (label.equals(scenes[i].label)) {
				setScene(i);
				break;
			}
		}
	}
	
	public void next() {
		if (getCurrentButtons().size() == 0) {
			if (textRendered) {
				String jumpLabel = scenes[currentScene].jumpLabel;
				if (jumpLabel != null) {
					goLabel(jumpLabel);
				} else if (currentScene < scenes.length - 1) {
					setScene(currentScene + 1);
				}
			} else
				textRendered = true;
		}
	}
	
	public void choose(int buttonIndex) {
		goLabel(getCurrentButtons().get(buttonIndex).label);
	}
	
	private void setScene(int index) {
		prevScene = currentScene;
		currentScene = index;
		tick = System.currentTimeMillis();
		backgroundRendered = false;
		unitsRendered = false;
		textRendered = false;
		updateBackground();
		updateUnits();
		update();
	}
	
	private void updateBackground() {
		if (scenes[currentScene].background == null)
			scenes[currentScene].background = scenes[prevScene].background;
	}
	
	private void updateUnits() {
		List<UnitAction> unitActions = scenes[currentScene].unitActions;
		for (UnitAction action : unitActions) {
			Unit existing = units.get(action.unitName);
			if (existing == null) {
				existing = new Unit(action);
				units.put(existing.name, existing);
			} else {
				if (action.placeStart == action.placeEnd)
					action.placeStart = existing.currentAction.placeEnd;
				existing.currentAction = action;
			}
		}
	}
	
	public String getTextToDraw() {
		if (!unitsRendered)
			return null;
		String text = getCurrentText();
		if (!textRendered) {
			long timePassed = System.currentTimeMillis() - tick;
			long charsCount = timePassed / DRAW_CHAR_TIME;
			if (charsCount >= text.length()) {
				textRendered = true;
			} else {
				text = text.substring(0, (int) charsCount);
			}
		}
		return text;
	}
}
