package com.blocks.executors;

import com.blocks.dao.DBConfiguration;
import com.blocks.dao.DBQueryExecutor;
import com.blocks.model.ElseIf;
import com.blocks.model.Parent;

public class ElseIfExecutor extends BaseElementsExecutor {

	private DBQueryExecutor dbQueryExecutor = null;

	public Boolean execute(ElseIf elseif, String basePath, String immediateParentId, DBConfiguration dbConfiguration) {

		System.out.println("\n--------------------------------------------------------");
		System.out.println("Executing ELSEIF[" + elseif.getId() + "] IN " + immediateParentId);
		System.out.println("--------------------------------------------------------\n");

		String condition = elseif.getCondition();

		if (condition == null || condition.isEmpty()) {
			throw new RuntimeException("Error: Null or Empty condition specified for ELSEIF[" + elseif.getId() + "] IN "
					+ immediateParentId + "\n");
		}

		prepareVariablesMap(elseif);

		dbQueryExecutor = new DBQueryExecutor();

		immediateParentId = immediateParentId + "ELSEIF[" + elseif.getId() + "]";

		boolean conditionResult = checkIfCondition(condition, elseif.getParent(), immediateParentId, dbConfiguration);

		if (conditionResult) {
			executeSubElements(elseif, basePath, immediateParentId, dbConfiguration);
		}

		return conditionResult;
	}

	private boolean checkIfCondition(String condition, Parent parent, String immediateParentId,
			DBConfiguration dbConfiguration) {
		return dbQueryExecutor.checkCondition("ELSEIF", condition, parent, immediateParentId, dbConfiguration);
	}
}
