package com.blocks.model;

public class Print implements Element {
	
	String text;
	Parent parent;
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public void setParent(Parent parent) {
		this.parent = parent;		
	}

	@Override
	public Parent getParent() {
		return parent;
	}
}
