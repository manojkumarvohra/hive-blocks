package com.blocks.executors;

import java.util.Map;

import com.blocks.dao.DBConfiguration;
import com.blocks.dao.DBQueryExecutor;
import com.blocks.model.Block;
import com.blocks.model.Query;

public class QueryExecutor {

	private DBQueryExecutor dbQueryExecutor = null;

	public void execute(Query query, Block parentBlock, String immediateParentId, DBConfiguration dbConfiguration) {

		System.out.println("\n--------------------------------------------------------");
		System.out.println("Executing QUERY[" + query.getExecutionOrder() + "] IN " + immediateParentId + "\n");
		System.out.println("--------------------------------------------------------\n");

		String queryFile = query.getQueryFile();

		if (queryFile == null || queryFile.isEmpty()) {
			throw new RuntimeException("Error: Null or Empty query file name specified for " + immediateParentId
					+ "QUERY[" + query.getExecutionOrder() + "]\n");
		}

		dbQueryExecutor = new DBQueryExecutor();

		String queryFilePath = parentBlock.getBasePath() + queryFile;

		executeQuery(queryFilePath, parentBlock.getVariableMap(), parentBlock.getVariableTypeMap(), immediateParentId,
				dbConfiguration);
	}

	private void executeQuery(String queryFilePath, Map<String, Object> variableMap,
			Map<String, String> variableTypeMap, String immediateParentId, DBConfiguration dbConfiguration) {
		dbQueryExecutor.executeQuery(queryFilePath, variableMap, variableTypeMap, immediateParentId, dbConfiguration);
	}
}
