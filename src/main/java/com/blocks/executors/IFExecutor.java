package com.blocks.executors;

import com.blocks.dao.DBConfiguration;
import com.blocks.dao.DBQueryExecutor;
import com.blocks.model.If;
import com.blocks.model.Parent;

public class IfExecutor extends BaseElementsExecutor {

	private DBQueryExecutor dbQueryExecutor = null;

	public Boolean execute(If fi, String basePath, String immediateParentId, DBConfiguration dbConfiguration) {

		System.out.println("\n--------------------------------------------------------");
		System.out.println("Executing IF[" + fi.getId() + "] IN " + immediateParentId + "\n");
		System.out.println("--------------------------------------------------------\n");

		String condition = fi.getCondition();

		if (condition == null || condition.isEmpty()) {
			throw new RuntimeException("Error: Null or Empty condition specified for IF[" + fi.getId() + "] IN "
					+ immediateParentId + "\n");
		}

		prepareVariablesMap(fi);

		dbQueryExecutor = new DBQueryExecutor();

		immediateParentId = immediateParentId + "IF[" + fi.getId() + "]";

		boolean conditionResult = checkIfCondition(condition, fi.getParent(), immediateParentId, dbConfiguration);

		if (conditionResult) {
			executeSubElements(fi, basePath, immediateParentId, dbConfiguration);
		}

		return conditionResult;
	}

	private boolean checkIfCondition(String condition, Parent parent, String immediateParentId,
			DBConfiguration dbConfiguration) {
		return dbQueryExecutor.checkCondition("IF", condition, parent, immediateParentId, dbConfiguration);
	}
}
