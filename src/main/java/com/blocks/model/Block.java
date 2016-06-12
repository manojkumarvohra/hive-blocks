package com.blocks.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Block {

	String name;
	String basePath;

	Map<String, Object> variableMap = new HashMap<String, Object>();
	Map<String, String> variableTypeMap = new HashMap<String, String>();
	LinkedList<Variable> variables = new LinkedList<Variable>();
	LinkedList<Element> elements = new LinkedList<Element>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addVariables(Variable variable) {
		variables.add(variable);
	}

	public void addElements(Element element) {
		elements.add(element);
	}

	public LinkedList<Variable> getVariables() {
		return variables != null ? variables : new LinkedList<Variable>();
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public Map<String, Object> getVariableMap() {
		return variableMap != null ? variableMap : new HashMap<String, Object>();
	}

	public void setVariableMap(Map<String, Object> variableMap) {
		this.variableMap = variableMap;
	}

	public void setVariableTypeMap(Map<String, String> variableTypeMap) {
		this.variableTypeMap = variableTypeMap;
	}

	public Map<String, String> getVariableTypeMap() {
		return variableTypeMap != null ? variableTypeMap : new HashMap<String, String>();
	}

	public LinkedList<Element> getElements() {
		return elements != null ? elements : new LinkedList<Element>();
	}
}
