package com.blocks.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Block implements BlockExecutionOrderComparable {

	int blockId;
	String basePath;

	Map<String, Object> variableMap = new HashMap<String, Object>();
	Map<String, String> variableTypeMap = new HashMap<String, String>();
	List<Variable> variables = new ArrayList<Variable>();
	List<Export> exports = new ArrayList<Export>();
	List<If> Ifs = new ArrayList<If>();
	List<Query> queries = new ArrayList<Query>();

	public void addVariables(Variable variable) {
		variables.add(variable);
	}

	public void addExports(Export export) {
		exports.add(export);
	}

	public void addIfs(If fi) {
		Ifs.add(fi);
	}

	public void addQueries(Query query) {
		queries.add(query);
	}

	public int getBlockId() {
		return blockId;
	}

	public void setBlockId(int blockId) {
		this.blockId = blockId;
	}

	public List<Variable> getVariables() {
		return variables != null ? variables : new ArrayList<Variable>();
	}

	public List<Export> getExports() {
		return exports;
	}

	public List<If> getIfs() {
		return Ifs;
	}

	public List<Query> getQueries() {
		return queries;
	}

	public int getBlockOrder() {
		return blockId;
	}

	public int compareTo(BlockExecutionOrderComparable o) {
		int comparisonResult = this.getBlockOrder() - o.getBlockOrder();

		if (comparisonResult == 0) {
			throw new RuntimeException("INVALID BLOCK CONFIGURATION: Two blocks can't have same block id");
		}
		return comparisonResult;
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

	public List<ElementExecutionOrderComparable> getSortedElements() {

		List<ElementExecutionOrderComparable> elements = new ArrayList<ElementExecutionOrderComparable>();

		elements.addAll(exports != null ? exports : new ArrayList<ElementExecutionOrderComparable>());
		elements.addAll(Ifs != null ? Ifs : new ArrayList<ElementExecutionOrderComparable>());
		elements.addAll(queries != null ? queries : new ArrayList<ElementExecutionOrderComparable>());

		Collections.sort(elements);

		return elements;
	}

}
