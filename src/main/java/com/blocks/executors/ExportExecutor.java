package com.blocks.executors;

import com.blocks.dao.DBConfiguration;
import com.blocks.dao.DBQueryExecutor;
import com.blocks.model.Export;
import com.blocks.model.Parent;

public class ExportExecutor {

	private DBQueryExecutor dbQueryExecutor = null;

	public void execute(Export export, String basePath, String immediateParentId, DBConfiguration dbConfiguration) {

		System.out.println("\n--------------------------------------------------------");
		System.out.println("Executing Export[" + export.getId() + "] IN " + immediateParentId + "\n");
		System.out.println("--------------------------------------------------------\n");

		String queryFile = export.getQueryFile();

		if (queryFile == null || queryFile.isEmpty()) {
			throw new RuntimeException("Error: Null or Empty query file name specified for " + immediateParentId
					+ "Export[" + export.getId() + "]\n");
		}

		immediateParentId = immediateParentId + "EXPORT[" + export.getId() + "]";

		dbQueryExecutor = new DBQueryExecutor();

		String queryFilePath = basePath + queryFile;

		exportValuesToVariables(queryFilePath, export.getParent(), immediateParentId, dbConfiguration);
	}

	private void exportValuesToVariables(String queryFilePath, Parent parent, String immediateParentId,
			DBConfiguration dbConfiguration) {
		dbQueryExecutor.exportValuesToVariables(queryFilePath, parent, immediateParentId, dbConfiguration);
	}
}
