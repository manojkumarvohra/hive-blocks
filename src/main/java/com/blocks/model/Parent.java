package com.blocks.model;

import java.util.LinkedList;
import java.util.Map;

public interface Parent {

	LinkedList<Variable> getVariables();

	LinkedList<Element> getElements();

	Parent getImmediateParent();

	Map<String, String> getVariableTypeMap();

	Map<String, Object> getVariableExportedValuesMap();

	Map<String, Object> getVariableAssignedValuesMap();

	void setVariableExportedValuesMap(Map<String, Object> variableExportedValuesMap);

	void setVariableAssignedValuesMap(Map<String, Object> variableAssignedValuesMap);

	void setVariableTypeMap(Map<String, String> variableTypeMap);
}
