package ru.footmade.dummymagic.entities;

public class Unit {
	public String name;
	public UnitAction currentAction;
	
	public Unit(UnitAction action) {
		name = action.unitName;
		currentAction = action;
	}
}
