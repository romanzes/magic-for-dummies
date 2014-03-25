package ru.footmade.dummymagic;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ColoredText extends Actor {
	public static final Color DEFAULT_COLOR = Color.WHITE;
	
	private BitmapFontCache cache;
	private Color defaultColor = DEFAULT_COLOR;
	
	private float xPos, yPos;

	public ColoredText(BitmapFont font) {
		cache = new BitmapFontCache(font);
	}
	
	private static final Pattern colorPattern = Pattern.compile("\\{\\s*color\\s*=\\s*#([a-f\\d]{6,8})\\s*\\}([^{]*)\\{\\s*/color\\s*\\}",
			Pattern.DOTALL);
	
	private String markedText;
	
	public void loadMarkedText(String text) {
		xPos = 0;
		yPos = 0;
		markedText = text;
		cache.clear();
		Matcher matcher = colorPattern.matcher(text);
		int pos = 0;
		while (matcher.find()) {
			addText(text.substring(pos, matcher.start()), defaultColor);
			String colorDef = matcher.group(1);
			int color = Integer.parseInt(colorDef, 16);
			float a = ((0xff000000 & color) >>> 24) / 255f;
			float r = ((0xff0000 & color) >>> 16) / 255f;
			float g = ((0xff00 & color) >>> 8) / 255f;
			float b = (0xff & color) / 255f;
			if (colorDef.length() == 6)
				a = 0xff;
			addText(matcher.group(2), new Color(r, g, b, a));
			pos = matcher.end();
		}
		addText(text.substring(pos), defaultColor);
	}
	
	public void setDefaultColor(Color color) {
		defaultColor = color;
	}

	private void addText(String str, Color color) {
		cache.setColor(color);
		List<String> parts = smartSplit(str, "\\n");
		for (int i = 0; i < parts.size() - 1; i++) {
			cache.addText(parts.get(i) + " ", xPos, yPos);
			xPos = 0;
			yPos -= cache.getFont().getLineHeight();
		}
		String part = parts.get(parts.size() - 1);
		cache.addText(part, xPos, yPos);
		xPos += cache.getFont().getBounds(part).width;
	}
	
	private List<String> smartSplit(String str, String regex) {
		Matcher matcher = Pattern.compile(regex).matcher(str);
		List<String> result = new ArrayList<String>();
		int pos = 0;
		while (matcher.find()) {
			result.add(str.substring(pos, matcher.start()));
			pos = matcher.end();
		}
		result.add(str.substring(pos));
		return result;
	}
	
	private int textLimit;
	
	public void setTextLimit(int limit) {
		textLimit = limit;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (markedText != null) {
			cache.setPosition(getX(), getY() + getHeight());
			cache.draw(batch, 0, textLimit);
		}
	}
}
