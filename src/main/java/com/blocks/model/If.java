package com.blocks.model;

import java.util.LinkedList;

public class If implements Element {

	String condition;
	LinkedList<Element> elements = new LinkedList<Element>();
	String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addElements(Element element) {
		elements.add(element);
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public LinkedList<Element> getElements() {
		return elements != null ? elements : new LinkedList<Element>();
	}
}
