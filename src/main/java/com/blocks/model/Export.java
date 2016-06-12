package com.blocks.model;

public class Export implements Element {

	String queryFile;
	String id;
	Parent parent;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getQueryFile() {
		return queryFile;
	}

	public void setQueryFile(String queryFile) {
		this.queryFile = queryFile;
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
