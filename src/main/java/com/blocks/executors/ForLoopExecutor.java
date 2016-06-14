package com.blocks.executors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.blocks.dao.DBConfiguration;
import com.blocks.dao.DBQueryExecutor;
import com.blocks.model.Element;
import com.blocks.model.Export;
import com.blocks.model.For;
import com.blocks.model.If;
import com.blocks.model.Parent;
import com.blocks.model.Print;
import com.blocks.model.Query;
import com.blocks.model.Variable;

public class ForLoopExecutor {

	private DBQueryExecutor dbQueryExecutor = null;

	public void execute(For loop, String basePath, String immediateParentId, DBConfiguration dbConfiguration) {

		System.out.println("\n--------------------------------------------------------");
		System.out.println("Executing FOR[" + loop.getId() + "] IN " + immediateParentId);
		System.out.println("--------------------------------------------------------\n");

		String queryFile = loop.getQueryFile();
		String condition = null;

		if (queryFile == null || queryFile.isEmpty()) {

			condition = loop.getCondition();

			if (condition == null || condition.isEmpty()) {
				throw new RuntimeException("Error: None of query file or condition specified for " + immediateParentId
						+ "FOR[" + loop.getId() + "]\n");
			}
		}

		prepareVariablesMap(loop);

		dbQueryExecutor = new DBQueryExecutor();

		immediateParentId = immediateParentId + "FOR[" + loop.getId() + "]";

		if (queryFile != null) {
			String queryFilePath = basePath + queryFile;
			iterateOverQueryResults(queryFilePath, loop, basePath, immediateParentId, dbConfiguration);
		}
	}

	private void iterateOverQueryResults(String queryFilePath, For loop, String basePath, String immediateParentId,
			DBConfiguration dbConfiguration) {

		IfExecutor ifExecutor = new IfExecutor();
		ExportExecutor exportExecutor = new ExportExecutor();
		QueryExecutor queryExecutor = new QueryExecutor();
		PrintExecutor printExecutor = new PrintExecutor();
		ForLoopExecutor forLoopExecutor = new ForLoopExecutor();
		
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {

			resultSet = executeQueryAndFetchResults(queryFilePath, loop.getParent(), immediateParentId, dbConfiguration,
					connection, statement, resultSet);

			while (resultSet.next()) {

				exportCurrentRecordToLoopVariables(loop, resultSet);

				for (Element loopElement : loop.getElements()) {

					loopElement.setParent(loop);

					if (loopElement instanceof If) {
						ifExecutor.execute((If) loopElement, basePath, immediateParentId, dbConfiguration);
					} else if (loopElement instanceof Export) {
						exportExecutor.execute((Export) loopElement, basePath, immediateParentId, dbConfiguration);
					} else if (loopElement instanceof Query) {
						queryExecutor.execute((Query) loopElement, basePath, immediateParentId, dbConfiguration);
					}else if (loopElement instanceof Print) {
						printExecutor.execute((Print) loopElement, basePath, immediateParentId, dbConfiguration);
					}else if (loopElement instanceof For) {
						forLoopExecutor.execute((For) loopElement, basePath, immediateParentId, dbConfiguration);
					}
					
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new RuntimeException("Error: Error executing FOR[ " + immediateParentId + "]\n");
		} finally {
			dbQueryExecutor.cleanUpConnection(connection, statement, resultSet);
		}

	}

	private void exportCurrentRecordToLoopVariables(For loop, ResultSet resultSet) throws SQLException {
		dbQueryExecutor.exportResultSetToVariables(resultSet, loop);
	}

	private ResultSet executeQueryAndFetchResults(String queryFilePath, Parent parent, String immediateParentId,
			DBConfiguration dbConfiguration, Connection connection, Statement statement, ResultSet resultSet) {
		return dbQueryExecutor.executeQueryAndFetchResults(queryFilePath, parent, immediateParentId, dbConfiguration,
				connection, statement, resultSet);
	}

	private void prepareVariablesMap(For loop) {
		Map<String, Object> variableExportedValuesMap = new HashMap<String, Object>();
		Map<String, Object> variableAssignedValuesMap = new HashMap<String, Object>();
		Map<String, String> variableTypeMap = new HashMap<String, String>();
		for (Variable variable : loop.getVariables()) {
			variableExportedValuesMap.put(variable.getName(), null);
			variableAssignedValuesMap.put(variable.getName(), variable.getValue());
			variableTypeMap.put(variable.getName(), variable.getType());
		}

		loop.setVariableExportedValuesMap(variableExportedValuesMap);
		loop.setVariableAssignedValuesMap(variableAssignedValuesMap);
		loop.setVariableTypeMap(variableTypeMap);
	}
}