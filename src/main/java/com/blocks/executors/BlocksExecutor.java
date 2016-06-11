package com.blocks.executors;

import com.blocks.dao.DBConfiguration;
import com.blocks.model.Block;
import com.blocks.model.BlockExecutionOrderComparable;
import com.blocks.model.Blocks;

public class BlocksExecutor {

	public void execute(Blocks blocks, DBConfiguration dbConfiguration) {

		if (blocks == null) {
			return;
		}
		
		System.out.println("Starting execution of blocks:" + blocks.getName());
		
		for (BlockExecutionOrderComparable element : blocks.getSortedElements()) {
			Block block = (Block) element;
			block.setBasePath(blocks.getBasePath());
			new BlockExecutor().execute(block, dbConfiguration);
		}
	}
}
