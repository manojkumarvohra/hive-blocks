package com.blocks.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class If implements ElementExecutionOrderComparable {

	int executionOrder;

	String condition;
	List<If> Ifs = new ArrayList<If>();
	List<Export> exports = new ArrayList<Export>();
	List<Query> queries = new ArrayList<Query>();

	public void addExports(Export export) {
		exports.add(export);
	}

	public void addIfs(If fi) {
		Ifs.add(fi);
	}

	public void addQueries(Query query) {
		queries.add(query);
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public List<If> getIfs() {
		return Ifs;
	}

	public List<Export> getExports() {
		return exports;
	}

	public List<Query> getQueries() {
		return queries;
	}

	public int getExecutionOrder() {
		return executionOrder;
	}

	public void setExecutionOrder(int order) {
		this.executionOrder = order;
	}

	public int getElementExecutionOrder() {
		return executionOrder;
	}

	public int compareTo(ElementExecutionOrderComparable o) {
		int comparisonResult = this.getElementExecutionOrder() - o.getElementExecutionOrder();

		if (comparisonResult == 0) {
			throw new RuntimeException(
					"INVALID BLOCK CONFIGURATION: Two IFs can't have same execution order within same parent");
		}

		return comparisonResult;
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
