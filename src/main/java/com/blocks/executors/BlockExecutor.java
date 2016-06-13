package com.blocks.executors;

import java.util.HashMap;
import java.util.Map;

import com.blocks.dao.DBConfiguration;
import com.blocks.model.Block;
import com.blocks.model.Element;
import com.blocks.model.Export;
import com.blocks.model.For;
import com.blocks.model.If;
import com.blocks.model.Print;
import com.blocks.model.Query;
import com.blocks.model.Variable;

public class BlockExecutor {

	public void execute(Block block, String basePath, DBConfiguration dbConfiguration) {

		System.out.println("\n--------------------------------------------------------");
		System.out.println("Executing Block[" + block.getId() + "]\n");
		System.out.println("--------------------------------------------------------\n");

		prepareVariablesMap(block);

		String immediateParentId = "BLOCK[" + block.getId() + "]";

		IFExecutor ifExecutor = new IFExecutor();
		ExportExecutor exportExecutor = new ExportExecutor();
		QueryExecutor queryExecutor = new QueryExecutor();
		PrintExecutor printExecutor = new PrintExecutor();
		ForLoopExecutor forLoopExecutor = new ForLoopExecutor();

		for (Element blockElement : block.getElements()) {

			blockElement.setParent(block);

			if (blockElement instanceof If) {
				ifExecutor.execute((If) blockElement, basePath, immediateParentId, dbConfiguration);
			} else if (blockElement instanceof Export) {
				exportExecutor.execute((Export) blockElement, basePath, immediateParentId, dbConfiguration);
			} else if (blockElement instanceof Query) {
				queryExecutor.execute((Query) blockElement, basePath, immediateParentId, dbConfiguration);
			} else if (blockElement instanceof Print) {
				printExecutor.execute((Print) blockElement, basePath, immediateParentId, dbConfiguration);
			} else if (blockElement instanceof For) {
				forLoopExecutor.execute((For) blockElement, basePath, immediateParentId, dbConfiguration);
			}
		}
	}

	private void prepareVariablesMap(Block block) {
		Map<String, Object> variableExportedValuesMap = new HashMap<String, Object>();
		Map<String, Object> variableAssignedValuesMap = new HashMap<String, Object>();
		Map<String, String> variableTypeMap = new HashMap<String, String>();
		for (Variable variable : block.getVariables()) {
			variableExportedValuesMap.put(variable.getName(), null);
			variableAssignedValuesMap.put(variable.getName(), variable.getValue());
			variableTypeMap.put(variable.getName(), variable.getType());
		}

		block.setVariableExportedValuesMap(variableExportedValuesMap);
		block.setVariableAssignedValuesMap(variableAssignedValuesMap);
		block.setVariableTypeMap(variableTypeMap);
	}

}
