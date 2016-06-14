package com.blocks.executors;

import com.blocks.dao.DBConfiguration;
import com.blocks.model.Block;

public class BlockExecutor extends BaseElementsExecutor{

	public void execute(Block block, String basePath, DBConfiguration dbConfiguration) {

		System.out.println("\n--------------------------------------------------------");
		System.out.println("Executing Block[" + block.getId() + "]");
		System.out.println("--------------------------------------------------------\n");

		prepareVariablesMap(block);

		String immediateParentId = "BLOCK[" + block.getId() + "]";

		executeSubElements(block, basePath, immediateParentId, dbConfiguration);
	}
}
