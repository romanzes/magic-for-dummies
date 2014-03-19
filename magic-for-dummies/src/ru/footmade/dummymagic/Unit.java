package ru.footmade.dummymagic;

public class Unit {
	public String name;
	public UnitAction currentAction;
	
	public Unit(UnitAction action) {
		name = action.unitName;
		currentAction = action;
	}
}
