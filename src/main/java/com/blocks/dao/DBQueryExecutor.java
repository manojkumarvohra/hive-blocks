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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;

import com.blocks.model.Parent;
import com.blocks.util.Evaluator;

public class DBQueryExecutor {

	private static Logger logger = Logger.getLogger(DBQueryExecutor.class);
	private BasicFormatterImpl formatter = new BasicFormatterImpl();
	private static final String INVALID_DB_DRIVER_MESSAGE_PATTERN = "Database driver class %s not found\n";
	private static final String QUERY_EXECUTION_ERROR_MESSAGE_PATERN = "Error while executing query on database... \n\t %s \n";
	private static final String CONNECTION_FAILURE_MESSAGE_PATTERN = "Unable to establish connection to database... %s \n";

	public boolean checkConditionOnDatabase(String element, String condition, Parent parent, String immediateParentId,
			DBConfiguration dbConfiguration) {

		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		String conditionQuery = "SELECT " + condition + " FROM default.dual LIMIT 1";
		String translatedQuery = substituteVariables(parent, immediateParentId, conditionQuery);

		System.out.println("ORIGINAL QUERY");
		System.out.println("--------------");
		System.out.println(formatter.format(conditionQuery));
		System.out.println("\nTRANSLATED QUERY");
		System.out.println("-----------------");
		System.out.println(formatter.format(translatedQuery));

		boolean retValue = false;

		try {

			resultSet = executeQuery(dbConfiguration, translatedQuery, connection, statement, resultSet, 1);

			if (resultSet.next()) {
				retValue = resultSet.getBoolean(1);
			}

			System.out.println("....................................");
			System.out.println(element + ":" + immediateParentId + " RESULT=" + retValue);
			System.out.println("....................................");

		} catch (Throwable t) {
			System.out.println("ERROR EXECUTING " + element + "CONDITION: " + immediateParentId);
			logger.error("ERROR EXECUTING " + element + " CONDITION: " + immediateParentId);
			t.printStackTrace();
			System.exit(1);
		} finally {
			cleanUpConnection(connection, statement, resultSet);
		}

		return retValue;
	}

	public boolean checkConditionOnEngine(String element, String condition, Parent parent, String immediateParentId,
			DBConfiguration dbConfiguration) {

		String translatedCondition = transformBetweenExpression(condition);
		translatedCondition = substituteOperators(translatedCondition);
		translatedCondition = substituteVariables(parent, immediateParentId, translatedCondition);
		Boolean result = Evaluator.evaluateToBoolean(translatedCondition);

		System.out.println("....................................");
		System.out.println(
				element + ":" + immediateParentId + "\nCONDITION:[" + translatedCondition + "]\nRESULT=" + result);
		System.out.println("....................................");

		return result;
	}

	private String transformBetweenExpression(String condition) {

		String translatedCondition = condition;

		Pattern betweenReplacementPatternEnclosed = Pattern
				.compile("\\s(\\S+)\\s+[bB][eE][tT][wW][eE][eE][nN]\\s+(\\S+)\\s+[aA][nN][dD]\\s+(\\S+)\\s");

		StringBuffer stringBuffer = new StringBuffer();
		Matcher betweenPatterMatcherEnclosed = betweenReplacementPatternEnclosed.matcher(translatedCondition);
		while (betweenPatterMatcherEnclosed.find()) {
			String operand1 = betweenPatterMatcherEnclosed.group(1);
			String operand2 = betweenPatterMatcherEnclosed.group(2);
			String operand3 = betweenPatterMatcherEnclosed.group(3);

			String replacementStringEnclosed = " " + operand1 + " >= " + operand2 + " and " + operand1 + " <= "
					+ operand3 + " ";

			betweenPatterMatcherEnclosed.appendReplacement(stringBuffer, replacementStringEnclosed);
		}
		betweenPatterMatcherEnclosed.appendTail(stringBuffer);
		translatedCondition = stringBuffer.toString();

		return translatedCondition;
	}

	private String substituteOperators(String condition) {

		String translatedCondition = condition;

		Pattern andReplacementPattern = Pattern.compile("(\\s+[aA][nN][dD]\\s+)");
		Pattern orReplacementPattern = Pattern.compile("(\\s+[oO][rR]\\s+)");

		StringBuffer stringBuffer = new StringBuffer();
		Matcher operatorMatcher = andReplacementPattern.matcher(translatedCondition);

		while (operatorMatcher.find()) {
			String replacementString = " && ";
			operatorMatcher.appendReplacement(stringBuffer, replacementString);

		}
		operatorMatcher.appendTail(stringBuffer);
		translatedCondition = stringBuffer.toString();

		stringBuffer = new StringBuffer();
		operatorMatcher = orReplacementPattern.matcher(translatedCondition);

		while (operatorMatcher.find()) {
			String replacementString = " || ";
			operatorMatcher.appendReplacement(stringBuffer, replacementString);

		}
		operatorMatcher.appendTail(stringBuffer);
		translatedCondition = stringBuffer.toString();

		return translatedCondition;
	}

	public String substituteVariables(Parent parent, String immediateParentId, String conditionQuery) {

		String translatedQuery = conditionQuery;

		while (parent != null) {

			Map<String, Object> variableExportedValuesMap = parent.getVariableExportedValuesMap();
			Map<String, Object> variableAssignedValuesMap = parent.getVariableAssignedValuesMap();
			Map<String, String> variableTypeMap = parent.getVariableTypeMap();

			for (String key : variableExportedValuesMap.keySet()) {

				String matchingKey = ":" + key;

				if (translatedQuery.contains(matchingKey)) {

					Object valueObj = variableExportedValuesMap.get(key);

					if (valueObj == null) {

						valueObj = variableAssignedValuesMap.get(key);

						if (valueObj == null) {
							continue;
						}
					}

					String valueToBeReplaced = valueObj.toString();
					String varType = variableTypeMap.get(key);

					if (varType.equalsIgnoreCase("string") || varType.equalsIgnoreCase("char")
							|| varType.equalsIgnoreCase("date") || varType.equalsIgnoreCase("timestamp")) {

						valueToBeReplaced = "'" + valueToBeReplaced + "'";
					}

					Pattern variableReplacementPattern = Pattern.compile(matchingKey);

					StringBuffer stringBuffer = new StringBuffer();
					Matcher variablePatterMatcher = variableReplacementPattern.matcher(translatedQuery);

					while (variablePatterMatcher.find()) {
						variablePatterMatcher.appendReplacement(stringBuffer, valueToBeReplaced);
					}
					variablePatterMatcher.appendTail(stringBuffer);
					translatedQuery = stringBuffer.toString();
				}
			}

			parent = parent.getImmediateParent();
		}

		return translatedQuery;
	}

	public void exportValuesToVariables(String queryFilePath, Parent parent, String immediateParentId,
			DBConfiguration dbConfiguration) {

		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		List<String> exportQueries = null;

		try {

			exportQueries = getQueriesFromQueryFile(queryFilePath, true);

			if (!exportQueries.isEmpty()) {

				String exportQuery = exportQueries.get(0);

				String translatedQuery = substituteVariables(parent, immediateParentId, exportQuery);

				System.out.println("ORIGINAL EXPORT QUERY");
				System.out.println("---------------------");
				System.out.println(formatter.format(exportQuery));
				System.out.println("\nTRANSLATED EXPORT QUERY");
				System.out.println("------------------------");
				System.out.println(formatter.format(translatedQuery));

				resultSet = executeQuery(dbConfiguration, translatedQuery, connection, statement, resultSet, 1);

				if (resultSet.next()) {
					exportResultSetToVariables(resultSet, parent);
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

	public void exportResultSetToVariables(ResultSet resultSet, Parent parent) throws SQLException {

		ResultSetMetaData metadata = resultSet.getMetaData();
		int columnCount = metadata.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {

			String varname = metadata.getColumnLabel(i);
			Object value = resultSet.getObject(i);

			if (varname.contains(".")) {
				varname = varname.split("\\.")[1];
			}

			Map<String, Object> variableExportedValuesMap = parent.getVariableExportedValuesMap();

			for (String key : variableExportedValuesMap.keySet()) {
				if (key.equalsIgnoreCase(varname)) {
					variableExportedValuesMap.put(key, value);
					break;
				}
			}
		}
	}

	public void executeQuery(String queryFilePath, Parent parent, String immediateParentId,
			DBConfiguration dbConfiguration) {
		Connection connection = null;
		Statement statement = null;
		List<String> queries = null;

		try {

			queries = getQueriesFromQueryFile(queryFilePath, false);

			for (String query : queries) {

				String translatedQuery = substituteVariables(parent, immediateParentId, query);

				System.out.println("ORIGINAL QUERY");
				System.out.println("--------------");
				System.out.println(formatter.format(query));
				System.out.println("\nTRANSLATED QUERY");
				System.out.println("-----------------");
				System.out.println(formatter.format(translatedQuery));

				executeUpdate(dbConfiguration, translatedQuery, connection, statement);
			}

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

	public ResultSet executeQueryAndFetchResults(String queryFilePath, Parent parent, String immediateParentId,
			DBConfiguration dbConfiguration, Connection connection, Statement statement, ResultSet resultSet) {

		try {

			List<String> queries = getQueriesFromQueryFile(queryFilePath, true);

			if (!queries.isEmpty()) {

				String query = queries.get(0);
				String translatedQuery = substituteVariables(parent, immediateParentId, query);

				System.out.println("ORIGINAL QUERY");
				System.out.println("--------------");
				System.out.println(formatter.format(query));
				System.out.println("\nTRANSLATED QUERY");
				System.out.println("-----------------");
				System.out.println(formatter.format(translatedQuery));

				resultSet = executeQuery(dbConfiguration, translatedQuery, connection, statement, resultSet, 100);
			}

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
		}
		return resultSet;
	}

	private List<String> getQueriesFromQueryFile(String queryFilePath, boolean getOnlyFirstQueryFromFile)
			throws IOException {

		LinkedList<String> queries = new LinkedList<String>();

		FileInputStream fis = new FileInputStream(new File(queryFilePath));
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		StringBuffer stringBuffer = new StringBuffer();
		String line = null;
		while ((line = br.readLine()) != null) {
			if (line.endsWith(";") && !line.endsWith("\\;")) {
				stringBuffer.append(" ");
				stringBuffer.append(line.substring(0, line.indexOf(";")));
				stringBuffer.append(" ");

				String query = stringBuffer.toString().trim();

				if (!query.isEmpty()) {
					queries.add(query);
				}

				if (getOnlyFirstQueryFromFile && queries.size() == 1) {
					break;
				}

				stringBuffer = new StringBuffer();
			} else {
				stringBuffer.append(" ");
				stringBuffer.append(line);
				stringBuffer.append(" ");
			}

		}
		br.close();

		String bufferContent = stringBuffer.toString().trim();

		if (!bufferContent.isEmpty()) {
			queries.add(bufferContent);
		}

		return queries;
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

	public void cleanUpConnection(Connection connection, Statement statement, ResultSet resultSet) {

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