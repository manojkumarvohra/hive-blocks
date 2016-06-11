package com.blocks.executors;

import java.util.Map;

import com.blocks.dao.DBConfiguration;
import com.blocks.dao.DBQueryExecutor;
import com.blocks.model.Block;
import com.blocks.model.Export;

public class ExportExecutor {

	private DBQueryExecutor dbQueryExecutor = null;

	public void execute(Export export, Block parentBlock, String immediateParentId, DBConfiguration dbConfiguration) {

		System.out.println("\n--------------------------------------------------------");
		System.out.println("Executing Export[" + export.getExecutionOrder() + "] IN " + immediateParentId + "\n");
		System.out.println("--------------------------------------------------------\n");

		String queryFile = export.getQueryFile();

		if (queryFile == null || queryFile.isEmpty()) {
			throw new RuntimeException("Error: Null or Empty query file name specified for " + immediateParentId
					+ "Export[" + export.getExecutionOrder() + "]\n");
		}

		immediateParentId = immediateParentId + "EXPORT[" + export.getElementExecutionOrder() + "]";

		dbQueryExecutor = new DBQueryExecutor();

		String queryFilePath = parentBlock.getBasePath() + queryFile;

		exportValuesToVariables(queryFilePath, parentBlock.getVariableMap(), parentBlock.getVariableTypeMap(),
				immediateParentId, dbConfiguration);
	}

	private void exportValuesToVariables(String queryFilePath, Map<String, Object> variableMap,
			Map<String, String> variableTypeMap, String immediateParentId, DBConfiguration dbConfiguration) {
		dbQueryExecutor.exportValuesToVariables(queryFilePath, variableMap, variableTypeMap, immediateParentId,
				dbConfiguration);
	}
}
