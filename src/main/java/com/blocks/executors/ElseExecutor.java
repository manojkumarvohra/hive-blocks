package com.blocks.executors;

import com.blocks.dao.DBConfiguration;
import com.blocks.model.Else;

public class ElseExecutor extends BaseElementsExecutor {

	public void execute(Else elsE, String basePath, String immediateParentId, DBConfiguration dbConfiguration) {

		System.out.println("\n--------------------------------------------------------");
		System.out.println("Executing ELSE[" + elsE.getId() + "] IN " + immediateParentId);
		System.out.println("--------------------------------------------------------\n");

		prepareVariablesMap(elsE);

		immediateParentId = immediateParentId + "ELSE[" + elsE.getId() + "]";

		executeSubElements(elsE, basePath, immediateParentId, dbConfiguration);
	}
}
