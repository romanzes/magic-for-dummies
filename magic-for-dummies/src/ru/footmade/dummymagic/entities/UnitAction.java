package ru.footmade.dummymagic.entities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UnitAction {
	public static final int STATE_HIDE = 0;
	public static final int STATE_SHOW = 1;
	
	public static final int EFFECT_NONE = 0;
	public static final int EFFECT_DISSOLVE = 1;
	
	public String unitName;
	public Place placeStart, placeEnd;
	public int state;
	public int effect;
	
	public UnitAction(String unitName) {
		this.unitName = unitName;
		placeStart = new Place();
		placeEnd = placeStart;
		state = STATE_SHOW;
		effect = EFFECT_NONE;
	}
	
	public UnitAction(String unitName, String placeDescription) {
		this.unitName = unitName;
		parseAnimation(placeDescription);
		state = STATE_SHOW;
		effect = EFFECT_NONE;
	}
	
	public UnitAction(String unitName, String placeDescription, String stateDescription) {
		this.unitName = unitName;
		parseAnimation(placeDescription);
		parseState(stateDescription);
	}
	
	private static final Pattern placePattern = Pattern.compile("(([\\w|]*)\\s+to\\s+)?([\\w|]*)");
	
	private void parseAnimation(String description) {
		if (description == null) {
			placeStart = new Place();
			placeEnd = placeStart;
		} else {
			Matcher matcher = placePattern.matcher(description);
			if (matcher.matches()) {
				placeEnd = new Place(matcher.group(3));
				String placeStartInfo = matcher.group(2);
				if (placeStartInfo != null)
					placeStart = new Place(placeStartInfo);
				else
					placeStart = placeEnd;
			}
		}
	}
	
	private static final Pattern statePattern = Pattern.compile("([\\w|]*)(\\s+with\\s+([\\w|]*))?");
	
	private void parseState(String description) {
		Matcher matcher = statePattern.matcher(description);
		if (matcher.matches()) {
			String stateInfo = matcher.group(1);
			String effectInfo = matcher.group(3);
			state = STATE_SHOW;
			if (stateInfo.equals("hide")) {
				state = STATE_HIDE;
			}
			if (effectInfo == null) {
				effect = EFFECT_NONE;
			} else if (effectInfo.equals("dissolve")) {
				effect = EFFECT_DISSOLVE;
			}
		}
	}
}
