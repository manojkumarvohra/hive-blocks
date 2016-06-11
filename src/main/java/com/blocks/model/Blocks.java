package com.blocks.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Blocks {

	String name;
	String basePath;
	List<Block> blocks	= new ArrayList<Block>();
	
	public void addBlocks(Block block) {
		blocks.add(block);
	}	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public List<BlockExecutionOrderComparable> getSortedElements(){
		
		List<BlockExecutionOrderComparable> elements = new ArrayList<BlockExecutionOrderComparable>();
		
		elements.addAll(blocks != null ? blocks : new ArrayList<BlockExecutionOrderComparable>());
	
		Collections.sort(elements);
		
		return elements;
		
	}
}
