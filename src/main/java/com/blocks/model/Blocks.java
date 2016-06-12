package com.blocks.model;

import java.util.LinkedList;

public class Blocks {

	String name;
	String basePath;
	LinkedList<Block> blocks = new LinkedList<Block>();

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

	public LinkedList<Block> getBlocks() {
		return blocks;
	}
}
