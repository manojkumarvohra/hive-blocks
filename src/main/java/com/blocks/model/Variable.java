package com.blocks.model;

import java.util.ArrayList;
import java.util.List;

public class Variable {
	String name;
	String type;
	String value;
	String format;

	public static List<String> acceptableTypes = new ArrayList<String>();
	
	static{
		acceptableTypes.add("numeric");
		acceptableTypes.add("string");
		acceptableTypes.add("char");
		acceptableTypes.add("timestamp");
		acceptableTypes.add("date");
		acceptableTypes.add("literal");
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}
