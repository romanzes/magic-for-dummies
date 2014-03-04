package ru.footmade.dummymagic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.Gdx;

public class Script {
	public static final int PERSON_MOVE_TIME = 700;
	public static final int DRAW_CHAR_TIME = 30;
	public static String COMMAND_PREFIX = "_";
	
	private Scene[] scenes;
	public int currentScene;
	
	public long tick;
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
	}
	
	public String getCurrentText() {
		return scenes[currentScene].text;
	}
	
	public String getCurrentBackground() {
		return scenes[currentScene].background;
	}
	
	public List<Button> getCurrentButtons() {
		return scenes[currentScene].buttons;
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
		currentScene = index;
		tick = System.currentTimeMillis();
		textRendered = false;
		unitsRendered = false;
		updateUnits();
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
	
	private class Scene {
		private String text;
		private String background;
		private List<Button> buttons = new ArrayList<Button>();
		private List<UnitAction> unitActions = new ArrayList<UnitAction>();
		private String label, jumpLabel;
		
		public Scene(String raw) {
			StringBuilder textBuilder = new StringBuilder();
			String strings[] = raw.split("(\\r\\n?)|(\\n\\r?)");
			for (String string : strings) {
				if (string.startsWith(COMMAND_PREFIX)) {
					processCommand(string.substring(COMMAND_PREFIX.length()).trim());
				} else {
					textBuilder.append(string).append("\n");
				}
			}
			text = textBuilder.toString();
		}
		
		private void processCommand(String command) {
			String[] commandData = parseCommand(command);
			String commandName = commandData[0];
			if (commandName.equals("bg")) {
				background = commandData[1];
			} else if (commandName.equals("btn")) {
				String btnText = commandData[1];
				String btnLabel = commandData[2];
				buttons.add(new Button(btnText, btnLabel));
			} else if (commandName.equals("label")) {
				label = commandData[1];
			} else if (commandName.equals("jump")) {
				jumpLabel = commandData[1];
			} else if (commandName.equals("unit")) {
				String unitName = commandData[1];
				if (commandData.length > 2) {
					String placeDescription = commandData[2];
					if (commandData.length > 3) {
						String stateDescription = commandData[3];
						unitActions.add(new UnitAction(unitName, placeDescription, stateDescription));
					} else {
						unitActions.add(new UnitAction(unitName, placeDescription));
					}
				} else {
					unitActions.add(new UnitAction(unitName));
				}
			}
		}
	}
	
	private static final Pattern commandPattern = Pattern.compile("(\\w*)\\s*\\(([^()]*)\\)");
	
	private static String[] parseCommand(String command) {
		Matcher matcher = commandPattern.matcher(command);
		if (!matcher.matches())
			throw new IllegalArgumentException("Bad command syntax");
		String commandName = matcher.group(1).trim();
		String[] args = matcher.group(2).split(",");
		String[] result = new String[args.length + 1];
		result[0] = commandName;
		for (int i = 0; i < args.length; i++) {
			result[i + 1] = args[i].trim();
		}
		return result;
	}
	
	public class Button {
		public String text;
		public String label;
		
		public Button(String text, String label) {
			this.text = text;
			this.label = label;
		}
	}
	
	public class Unit {
		public String name;
		public UnitAction currentAction;
		
		public Unit(UnitAction action) {
			name = action.unitName;
			currentAction = action;
		}
	}
}
