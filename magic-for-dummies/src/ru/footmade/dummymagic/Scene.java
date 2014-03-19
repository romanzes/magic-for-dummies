package ru.footmade.dummymagic;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scene {
	public static String COMMAND_PREFIX = "_";
	
	public static final int BG_EFFECT_NONE = 0;
	public static final int BG_EFFECT_DISSOLVE = 1;
	public static final int BG_EFFECT_FADE = 2;
	
	public String text;
	public String background;
	public int backgroundEffect;
	public List<Button> buttons = new ArrayList<Button>();
	public List<UnitAction> unitActions = new ArrayList<UnitAction>();
	public String label, jumpLabel;
	
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
			if (commandData.length > 2) {
				String bgEffectName = commandData[2];
				if (bgEffectName.equals("dissolve"))
					backgroundEffect = BG_EFFECT_DISSOLVE;
				else if (bgEffectName.equals("fade"))
					backgroundEffect = BG_EFFECT_FADE;
			} else
				backgroundEffect = BG_EFFECT_NONE;
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
}
