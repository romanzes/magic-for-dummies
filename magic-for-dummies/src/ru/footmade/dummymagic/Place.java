package ru.footmade.dummymagic;

public class Place {
	public static final int PLACE_CENTER = 0;
	public static final int PLACE_OUTER_LEFT = 0x1;
	public static final int PLACE_LEFT = 0x2;
	public static final int PLACE_RIGHT = 0x4;
	public static final int PLACE_OUTER_RIGHT = 0x8;
	public static final int PLACE_OUTER_BOTTOM = 0x10;
	public static final int PLACE_BOTTOM = 0x20;
	public static final int PLACE_TOP = 0x40;
	public static final int PLACE_OUTER_TOP = 0x80;
	
	public int vertical, horizontal;
	
	public Place(String description) {
		horizontal = PLACE_CENTER;
		vertical = PLACE_BOTTOM;
		String[] parts = description.split("\\|");
		for (String part : parts) {
			if (part.equals("outer_left")) {
				horizontal = PLACE_OUTER_LEFT;
			} else if (part.equals("left")) {
				horizontal = PLACE_LEFT;
			} else if (part.equals("right")) {
				horizontal = PLACE_RIGHT;
			} else if (part.equals("outer_right")) {
				horizontal = PLACE_OUTER_RIGHT;
			} else if (part.equals("outer_top")) {
				vertical = PLACE_OUTER_TOP;
			} else if (part.equals("top")) {
				vertical = PLACE_TOP;
			} else if (part.equals("bottom")) {
				vertical = PLACE_BOTTOM;
			} else if (part.equals("outer_bottom")) {
				vertical = PLACE_OUTER_BOTTOM;
			}
		}
	}
}
