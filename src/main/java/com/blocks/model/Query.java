package com.blocks.model;

public class Query implements ElementExecutionOrderComparable{
	
	int executionOrder;
	String queryFile;
	
	public String getQueryFile() {
		return queryFile;
	}

	public void setQueryFile(String queryFile) {
		this.queryFile = queryFile;
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
		
		if(comparisonResult == 0){
			throw new RuntimeException(
					"INVALID BLOCK CONFIGURATION: Two Queries can't have same execution order within same parent");
		}
		
		return comparisonResult;
	}
}
