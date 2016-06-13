package com.blocks.executors;

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

public class IFExecutor {

	private DBQueryExecutor dbQueryExecutor = null;

	public void execute(If fi, String basePath, String immediateParentId, DBConfiguration dbConfiguration) {

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

		boolean proceedWithIfBlock = checkIfCondition(condition, fi.getParent(), immediateParentId, dbConfiguration);

		if (proceedWithIfBlock) {

			IFExecutor ifExecutor = new IFExecutor();
			ExportExecutor exportExecutor = new ExportExecutor();
			QueryExecutor queryExecutor = new QueryExecutor();
			PrintExecutor printExecutor = new PrintExecutor();
			ForLoopExecutor forLoopExecutor = new ForLoopExecutor();

			for (Element ifElement : fi.getElements()) {

				ifElement.setParent(fi);

				if (ifElement instanceof If) {
					ifExecutor.execute((If) ifElement, basePath, immediateParentId, dbConfiguration);
				} else if (ifElement instanceof Export) {
					exportExecutor.execute((Export) ifElement, basePath, immediateParentId, dbConfiguration);
				} else if (ifElement instanceof Query) {
					queryExecutor.execute((Query) ifElement, basePath, immediateParentId, dbConfiguration);
				} else if (ifElement instanceof Print) {
					printExecutor.execute((Print) ifElement, basePath, immediateParentId, dbConfiguration);
				} else if (ifElement instanceof For) {
					forLoopExecutor.execute((For) ifElement, basePath, immediateParentId, dbConfiguration);
				}
			}
		}
	}

	private void prepareVariablesMap(If fi) {
		Map<String, Object> variableExportedValuesMap = new HashMap<String, Object>();
		Map<String, Object> variableAssignedValuesMap = new HashMap<String, Object>();
		Map<String, String> variableTypeMap = new HashMap<String, String>();
		for (Variable variable : fi.getVariables()) {
			variableExportedValuesMap.put(variable.getName(), null);
			variableAssignedValuesMap.put(variable.getName(), variable.getValue());
			variableTypeMap.put(variable.getName(), variable.getType());
		}

		fi.setVariableExportedValuesMap(variableExportedValuesMap);
		fi.setVariableAssignedValuesMap(variableAssignedValuesMap);
		fi.setVariableTypeMap(variableTypeMap);
	}

	private boolean checkIfCondition(String condition, Parent parent, String immediateParentId,
			DBConfiguration dbConfiguration) {
		return dbQueryExecutor.checkIfCondition(condition, parent, immediateParentId, dbConfiguration);
	}
}
