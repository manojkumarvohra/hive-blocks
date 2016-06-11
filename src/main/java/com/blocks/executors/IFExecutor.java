package com.blocks.executors;

import java.util.Map;

import com.blocks.dao.DBConfiguration;
import com.blocks.dao.DBQueryExecutor;
import com.blocks.model.Block;
import com.blocks.model.ElementExecutionOrderComparable;
import com.blocks.model.Export;
import com.blocks.model.If;
import com.blocks.model.Query;

public class IFExecutor {

	private DBQueryExecutor dbQueryExecutor = null;

	public void execute(If fi, Block parentBlock, String immediateParentId, DBConfiguration dbConfiguration) {

		System.out.println("\n--------------------------------------------------------");
		System.out.println("Executing IF[" + fi.getExecutionOrder() + "] IN " + immediateParentId + "\n");
		System.out.println("--------------------------------------------------------\n");

		String condition = fi.getCondition();

		if (condition == null || condition.isEmpty()) {
			throw new RuntimeException("Error: Null or Empty condition specified for Block ID["
					+ parentBlock.getBlockId() + "] IF Element[" + fi.getExecutionOrder() + "]\n");
		}

		dbQueryExecutor = new DBQueryExecutor();

		immediateParentId = immediateParentId + "IF[" + fi.getElementExecutionOrder() + "]";

		boolean proceedWithIfBlock = checkIfCondition(condition, parentBlock.getVariableMap(),
				parentBlock.getVariableTypeMap(), immediateParentId, dbConfiguration);

		if (proceedWithIfBlock) {

			IFExecutor ifExecutor = new IFExecutor();
			ExportExecutor exportExecutor = new ExportExecutor();
			QueryExecutor queryExecutor = new QueryExecutor();

			for (ElementExecutionOrderComparable ifElement : fi.getSortedElements()) {
				if (ifElement instanceof If) {
					ifExecutor.execute((If) ifElement, parentBlock, immediateParentId, dbConfiguration);
				} else if (ifElement instanceof Export) {
					exportExecutor.execute((Export) ifElement, parentBlock, immediateParentId, dbConfiguration);
				} else if (ifElement instanceof Query) {
					queryExecutor.execute((Query) ifElement, parentBlock, immediateParentId, dbConfiguration);
				}
			}
		}
	}

	private boolean checkIfCondition(String condition, Map<String, Object> variableMap,
			Map<String, String> variableTypeMap, String immediateParentId, DBConfiguration dbConfiguration) {
		return dbQueryExecutor.checkIfCondition(condition, variableMap, variableTypeMap, immediateParentId,
				dbConfiguration);
	}
}
