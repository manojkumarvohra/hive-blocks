package com.blocks.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Else implements Element, Parent {

	String id;
	Parent parent;

	LinkedList<Element> elements = new LinkedList<Element>();
	Map<String, Object> variableExportedValuesMap = new HashMap<String, Object>();
	Map<String, Object> variableAssignedValuesMap = new HashMap<String, Object>();
	Map<String, String> variableTypeMap = new HashMap<String, String>();
	LinkedList<Variable> variables = new LinkedList<Variable>();

	public void addVariables(Variable variable) {
		variables.add(variable);
	}

	public void addElements(Element element) {
		elements.add(element);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void setParent(Parent parent) {
		this.parent = parent;
	}

	@Override
	public Parent getParent() {
		return parent;
	}

	@Override
	public Parent getImmediateParent() {
		return parent;
	}

	@Override
	public void setVariableExportedValuesMap(Map<String, Object> variableMap) {
		this.variableExportedValuesMap = variableMap;
	}

	@Override
	public void setVariableTypeMap(Map<String, String> variableTypeMap) {
		this.variableTypeMap = variableTypeMap;
	}

	@Override
	public void setVariableAssignedValuesMap(Map<String, Object> variableAssignedValuesMap) {
		this.variableAssignedValuesMap = variableAssignedValuesMap;
	}

	@Override
	public Map<String, Object> getVariableExportedValuesMap() {
		return variableExportedValuesMap != null ? variableExportedValuesMap : new HashMap<String, Object>();
	}

	@Override
	public Map<String, Object> getVariableAssignedValuesMap() {
		return variableAssignedValuesMap != null ? variableAssignedValuesMap : new HashMap<String, Object>();
	}

	@Override
	public Map<String, String> getVariableTypeMap() {
		return variableTypeMap != null ? variableTypeMap : new HashMap<String, String>();
	}

	@Override
	public LinkedList<Variable> getVariables() {
		return variables != null ? variables : new LinkedList<Variable>();
	}

	@Override
	public LinkedList<Element> getElements() {
		return elements != null ? elements : new LinkedList<Element>();
	}
}