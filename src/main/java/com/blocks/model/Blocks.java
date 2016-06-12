package com.blocks.model;

import java.util.LinkedList;

public class Blocks {

	String id;
	String basePath;
	LinkedList<Block> blocks = new LinkedList<Block>();

	public void addBlocks(Block block) {
		blocks.add(block);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public LinkedList<Block> getBlocks() {
		return blocks;
	}
}
