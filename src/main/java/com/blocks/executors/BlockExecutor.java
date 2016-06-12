package com.blocks.executors;

import java.util.HashMap;
import java.util.Map;

import com.blocks.dao.DBConfiguration;
import com.blocks.model.Block;
import com.blocks.model.Element;
import com.blocks.model.Export;
import com.blocks.model.If;
import com.blocks.model.Query;
import com.blocks.model.Variable;

public class BlockExecutor {

	public void execute(Block block, DBConfiguration dbConfiguration) {

		System.out.println("\n--------------------------------------------------------");
		System.out.println("Executing Block[" + block.getName() + "]\n");
		System.out.println("--------------------------------------------------------\n");
		
		Map<String, Object> variableMap = new HashMap<String, Object>();
		Map<String, String> variableTypeMap = new HashMap<String, String>();
		for (Variable variable : block.getVariables()) {
			variableMap.put(variable.getName(), null);
			variableTypeMap.put(variable.getName(), variable.getType());
		}
		
		block.setVariableMap(variableMap);
		block.setVariableTypeMap(variableTypeMap);

		String immediateParentId = "BLOCK[" + block.getName() + "]";

		IFExecutor ifExecutor = new IFExecutor();
		ExportExecutor exportExecutor = new ExportExecutor();
		QueryExecutor queryExecutor = new QueryExecutor();

		for (Element blockElement : block.getElements()) {
			if (blockElement instanceof If) {
				ifExecutor.execute((If) blockElement, block, immediateParentId, dbConfiguration);
			} else if (blockElement instanceof Export) {
				exportExecutor.execute((Export) blockElement, block, immediateParentId, dbConfiguration);
			} else if (blockElement instanceof Query) {
				queryExecutor.execute((Query) blockElement, block, immediateParentId, dbConfiguration);
			}
		}
	}

}
