package com.blocks.executors;

import com.blocks.dao.DBConfiguration;
import com.blocks.dao.DBQueryExecutor;
import com.blocks.model.Parent;
import com.blocks.model.Query;

public class QueryExecutor {

	private DBQueryExecutor dbQueryExecutor = null;

	public void execute(Query query, String basePath, String immediateParentId, DBConfiguration dbConfiguration) {

		System.out.println("\n--------------------------------------------------------");
		System.out.println("Executing QUERY[" + query.getId() + "] IN " + immediateParentId + "\n");
		System.out.println("--------------------------------------------------------\n");

		String queryFile = query.getQueryFile();

		if (queryFile == null || queryFile.isEmpty()) {
			throw new RuntimeException("Error: Null or Empty query file name specified for " + immediateParentId
					+ "QUERY[" + query.getId() + "]\n");
		}

		dbQueryExecutor = new DBQueryExecutor();

		String queryFilePath = basePath + queryFile;

		executeQuery(queryFilePath, query.getParent(), immediateParentId, dbConfiguration);
	}

	private void executeQuery(String queryFilePath, Parent parent, String immediateParentId, DBConfiguration dbConfiguration) {
		dbQueryExecutor.executeQuery(queryFilePath, parent, immediateParentId, dbConfiguration);
	}
}
