package ru.footmade.dummymagic;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.Gdx;

public class Script {
	private static final int DRAW_CHAR_TIME = 30;
	public static String COMMAND_PREFIX = "_";
	
	private Scene[] scenes;
	public int currentScene;
	
	private long tick;
	private boolean textRendered;
	
	public Script() {
		String rawScript = Gdx.files.internal("vn.vn").readString("UTF-8");
		String[] rawScenes = rawScript.split("((\\r\\n?)|(\\n\\r?))\\1");
		scenes = new Scene[rawScenes.length];
		for (int i = 0; i < rawScenes.length; i++) {
			scenes[i] = new Scene(rawScenes[i]);
		}
	}
	
	public void start() {
		tick = System.currentTimeMillis();
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
	}
	
	public String getTextToDraw() {
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
}
