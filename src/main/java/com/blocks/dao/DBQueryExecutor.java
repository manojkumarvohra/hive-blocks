package com.blocks.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class DBQueryExecutor {

	private static Logger logger = Logger.getLogger(DBQueryExecutor.class);

	private static final String INVALID_DB_DRIVER_MESSAGE_PATTERN = "Database driver class %s not found\n";
	private static final String QUERY_EXECUTION_ERROR_MESSAGE_PATERN = "Error while executing query on database... \n\t %s \n";
	private static final String CONNECTION_FAILURE_MESSAGE_PATTERN = "Unable to establish connection to database... %s \n";

	public boolean checkIfCondition(String condition, Map<String, Object> variableMap,
			Map<String, String> variableTypeMap, String immediateParentId, DBConfiguration dbConfiguration) {

		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		String conditionQuery = "SELECT " + condition + " FROM default.dual LIMIT 1";
		String translatedQuery = substituteVariables(variableMap, variableTypeMap, immediateParentId, conditionQuery);

		System.out.println("\n--------------------------------------------------------------------------------\n");
		System.out.println("IF ORIGINAL QUERY=" + conditionQuery);
		System.out.println("\nIF TRANSLATED QUERY=" + translatedQuery);
		System.out.println("\n--------------------------------------------------------------------------------\n");

		boolean retValue = false;

		try {

			resultSet = executeQuery(dbConfiguration, translatedQuery, connection, statement, resultSet, 1);

			if (resultSet.next()) {
				retValue = resultSet.getBoolean(1);
			}

			System.out.println("\n....................................\n");
			System.out.println("IF:" + immediateParentId + " RESULT=" + retValue);
			System.out.println("\n....................................\n");

		} catch (Throwable t) {
			System.out.println("ERROR EXECUTING IF CONDITION: " + immediateParentId);
			logger.error("ERROR EXECUTING IF CONDITION: " + immediateParentId);
			t.printStackTrace();
			System.exit(1);
		} finally {
			cleanUpConnection(connection, statement, resultSet);
		}

		return retValue;
	}

	private String substituteVariables(Map<String, Object> variableMap, Map<String, String> variableTypeMap,
			String immediateParentId, String conditionQuery) {

		String translatedQuery = conditionQuery;

		for (String key : variableMap.keySet()) {

			String matchingKey = ":" + key;

			if (conditionQuery.contains(matchingKey)) {

				Object valueObj = variableMap.get(key);

				if (valueObj == null) {
					throw new RuntimeException("INVALID BLOCK CONFIGURATION: Variable[" + matchingKey
							+ "]required to be substituted in: " + immediateParentId + " is not exported.");
				}

				String valueToBeReplaced = valueObj.toString();
				String varType = variableTypeMap.get(key);

				if (varType.equalsIgnoreCase("string") || varType.equalsIgnoreCase("varchar")
						|| varType.equalsIgnoreCase("char") || varType.equalsIgnoreCase("date")
						|| varType.equalsIgnoreCase("timestamp")) {

					valueToBeReplaced = "'" + valueToBeReplaced + "'";
				}

				Pattern variableReplacementPattern = Pattern.compile(matchingKey);

				StringBuffer stringBuffer = new StringBuffer();
				Matcher variablePatterMatcher = variableReplacementPattern.matcher(conditionQuery);

				while (variablePatterMatcher.find()) {
					variablePatterMatcher.appendReplacement(stringBuffer, valueToBeReplaced);
				}
				variablePatterMatcher.appendTail(stringBuffer);
				translatedQuery = stringBuffer.toString();
			}
		}
		return translatedQuery;
	}

	public void exportValuesToVariables(String queryFilePath, Map<String, Object> variableMap,
			Map<String, String> variableTypeMap, String immediateParentId, DBConfiguration dbConfiguration) {

		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		String exportQuery = null;

		try {

			exportQuery = getQueryFromQueryFile(queryFilePath, immediateParentId);
			String translatedQuery = substituteVariables(variableMap, variableTypeMap, immediateParentId, exportQuery);

			System.out.println("\n--------------------------------------------------------------------------------\n");
			System.out.println("EXPORT ORIGINAL QUERY=" + exportQuery);
			System.out.println("\nEXPORT TRANSLATED QUERY=" + translatedQuery + "\n");
			System.out.println("\n--------------------------------------------------------------------------------\n");

			resultSet = executeQuery(dbConfiguration, translatedQuery, connection, statement, resultSet, 1);

			if (resultSet.next()) {
				ResultSetMetaData metadata = resultSet.getMetaData();
				int columnCount = metadata.getColumnCount();
				for (int i = 1; i <= columnCount; i++) {

					String varname = metadata.getColumnLabel(i);
					Object value = resultSet.getObject(i);

					if (varname.contains(".")) {
						varname = varname.split("\\.")[1];
					}

					for (String key : variableMap.keySet()) {
						if (key.equalsIgnoreCase(varname)) {
							variableMap.put(key, value);
							break;
						}
					}

				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("ERROR EXECUTING EXPORT: " + immediateParentId);
			logger.error("ERROR EXECUTING EXPORT: " + immediateParentId);
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("ERROR EXECUTING EXPORT: " + immediateParentId);
			logger.error("ERROR EXECUTING EXPORT: " + immediateParentId);
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR EXECUTING EXPORT: " + immediateParentId);
			logger.error("ERROR EXECUTING EXPORT: " + immediateParentId);
			System.exit(1);
		} finally {
			cleanUpConnection(connection, statement, resultSet);
		}

	}

	public void executeQuery(String queryFilePath, Map<String, Object> variableMap, Map<String, String> variableTypeMap,
			String immediateParentId, DBConfiguration dbConfiguration) {
		Connection connection = null;
		Statement statement = null;
		String query = null;

		try {

			query = getQueryFromQueryFile(queryFilePath, immediateParentId);
			String translatedQuery = substituteVariables(variableMap, variableTypeMap, immediateParentId, query);

			System.out.println("\n--------------------------------------------------------------------------------\n");
			System.out.println("ORIGINAL QUERY=" + query + "\n");
			System.out.println("\nTRANSLATED QUERY=" + translatedQuery + "\n");
			System.out.println("\n--------------------------------------------------------------------------------\n");

			executeUpdate(dbConfiguration, translatedQuery, connection, statement);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("ERROR EXECUTING QUERY: " + immediateParentId);
			logger.error("ERROR EXECUTING QUERY: " + immediateParentId);
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("ERROR EXECUTING QUERY: " + immediateParentId);
			logger.error("ERROR EXECUTING QUERY: " + immediateParentId);
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR EXECUTING QUERY: " + immediateParentId);
			logger.error("ERROR EXECUTING QUERY: " + immediateParentId);
			System.exit(1);
		} finally {
			cleanUpConnection(connection, statement, null);
		}

	}

	private String getQueryFromQueryFile(String queryFilePath, String immediateParentId) throws IOException {

		FileInputStream fis = null;
		BufferedReader br = null;
		String query = null;

		fis = new FileInputStream(new File(queryFilePath));
		br = new BufferedReader(new InputStreamReader(fis));
		StringBuffer stringBuffer = new StringBuffer();

		String line = null;
		while ((line = br.readLine()) != null) {
			stringBuffer.append(" ");
			stringBuffer.append(line);
			stringBuffer.append(" ");
		}
		br.close();

		query = stringBuffer.toString().trim();
		if (query.endsWith(";")) {
			query = query.substring(0, query.length() - 1);
		}

		return query;
	}

	public static void executeUpdate(DBConfiguration dbConfiguration, String query, Connection connection,
			Statement statement) throws ClassNotFoundException, SQLException {

		String jdbcDriver = dbConfiguration.getJdbcDriver();
		String dbUrl = dbConfiguration.getDbUrl();
		String dbUserid = dbConfiguration.getDbUserid();
		String dbPassword = dbConfiguration.getDbPassword();

		try {
			Class.forName(jdbcDriver);
		} catch (ClassNotFoundException cnfe) {
			System.out.println(String.format(INVALID_DB_DRIVER_MESSAGE_PATTERN, jdbcDriver) + cnfe);
			throw cnfe;
		}

		try {
			if (connection == null) {
				connection = DriverManager.getConnection(dbUrl, dbUserid, dbPassword);
			}
			if (statement == null) {
				statement = connection.createStatement();
			}
		} catch (SQLException sqle) {
			System.out.println(String.format(CONNECTION_FAILURE_MESSAGE_PATTERN, dbUrl) + sqle);
			throw sqle;
		}

		try {
			int result = statement.executeUpdate(query);
			System.out.println(result + " rows updated");
		} catch (SQLException sqle) {
			System.out.println(String.format(QUERY_EXECUTION_ERROR_MESSAGE_PATERN, query) + sqle);
			throw sqle;
		}
	}

	public static ResultSet executeQuery(DBConfiguration dbConfiguration, String query, Connection connection,
			Statement statement, ResultSet resultSet, Integer fetchSize) throws ClassNotFoundException, SQLException {

		String jdbcDriver = dbConfiguration.getJdbcDriver();
		String dbUrl = dbConfiguration.getDbUrl();
		String dbUserid = dbConfiguration.getDbUserid();
		String dbPassword = dbConfiguration.getDbPassword();

		try {
			Class.forName(jdbcDriver);
		} catch (ClassNotFoundException cnfe) {
			System.out.println(String.format(INVALID_DB_DRIVER_MESSAGE_PATTERN, jdbcDriver) + cnfe);
			throw cnfe;
		}

		try {
			if (connection == null) {
				connection = DriverManager.getConnection(dbUrl, dbUserid, dbPassword);
			}
			if (statement == null) {
				statement = connection.createStatement();
			}
			if (fetchSize != null) {
				statement.setFetchSize(fetchSize);
			}
		} catch (SQLException sqle) {
			System.out.println(String.format(CONNECTION_FAILURE_MESSAGE_PATTERN, dbUrl) + sqle);
			throw sqle;
		}

		try {
			resultSet = statement.executeQuery(query);
		} catch (SQLException sqle) {
			System.out.println(String.format(QUERY_EXECUTION_ERROR_MESSAGE_PATERN, query) + sqle);
			throw sqle;
		}

		return resultSet;
	}

	private void cleanUpConnection(Connection connection, Statement statement, ResultSet resultSet) {

		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException sqle) {
				System.out.println("Unable to close ResultSet\n" + sqle);
			}
		}

		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException sqle) {
				System.out.println("Unable to close Statement\n" + sqle);
			}
		}

		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException sqle) {
				System.out.println("Unable to close Connection\n" + sqle);
			}
		}
	}
}
