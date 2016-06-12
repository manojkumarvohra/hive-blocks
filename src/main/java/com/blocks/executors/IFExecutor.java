package com.blocks.executors;

import java.util.Map;

import com.blocks.dao.DBConfiguration;
import com.blocks.dao.DBQueryExecutor;
import com.blocks.model.Block;
import com.blocks.model.Element;
import com.blocks.model.Export;
import com.blocks.model.If;
import com.blocks.model.Query;

public class IFExecutor {

	private DBQueryExecutor dbQueryExecutor = null;

	public void execute(If fi, Block parentBlock, String immediateParentId, DBConfiguration dbConfiguration) {

		System.out.println("\n--------------------------------------------------------");
		System.out.println("Executing IF[" + fi.getName() + "] IN " + immediateParentId + "\n");
		System.out.println("--------------------------------------------------------\n");

		String condition = fi.getCondition();

		if (condition == null || condition.isEmpty()) {
			throw new RuntimeException("Error: Null or Empty condition specified for Block ID["
					+ parentBlock.getName() + "] IF Element[" + fi.getName() + "]\n");
		}

		dbQueryExecutor = new DBQueryExecutor();

		immediateParentId = immediateParentId + "IF[" + fi.getName() + "]";

		boolean proceedWithIfBlock = checkIfCondition(condition, parentBlock.getVariableMap(),
				parentBlock.getVariableTypeMap(), immediateParentId, dbConfiguration);

		if (proceedWithIfBlock) {

			IFExecutor ifExecutor = new IFExecutor();
			ExportExecutor exportExecutor = new ExportExecutor();
			QueryExecutor queryExecutor = new QueryExecutor();

			for (Element ifElement : fi.getElements()) {
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
