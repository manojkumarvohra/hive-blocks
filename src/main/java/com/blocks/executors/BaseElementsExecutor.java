package com.blocks.executors;

import java.util.HashMap;
import java.util.Map;

import com.blocks.dao.DBConfiguration;
import com.blocks.model.Element;
import com.blocks.model.Else;
import com.blocks.model.ElseIf;
import com.blocks.model.Export;
import com.blocks.model.For;
import com.blocks.model.If;
import com.blocks.model.Parent;
import com.blocks.model.Print;
import com.blocks.model.Query;
import com.blocks.model.Variable;

public abstract class BaseElementsExecutor {

	protected void prepareVariablesMap(Parent parent) {
		Map<String, Object> variableExportedValuesMap = new HashMap<String, Object>();
		Map<String, Object> variableAssignedValuesMap = new HashMap<String, Object>();
		Map<String, String> variableTypeMap = new HashMap<String, String>();
		for (Variable variable : parent.getVariables()) {

			String name = variable.getName();

			if (name == null) {
				throw new RuntimeException("ERROR:Missing variable name");
			} else if (name.startsWith(":")) {
				throw new RuntimeException("ERROR:Invalid variable name [" + name + "] can't start with :");
			}

			variableExportedValuesMap.put(variable.getName(), null);
			variableAssignedValuesMap.put(variable.getName(), variable.getValue());
			variableTypeMap.put(variable.getName(), variable.getType());
		}

		parent.setVariableExportedValuesMap(variableExportedValuesMap);
		parent.setVariableAssignedValuesMap(variableAssignedValuesMap);
		parent.setVariableTypeMap(variableTypeMap);
	}

	protected void executeSubElements(Parent parent, String basePath, String immediateParentId,
			DBConfiguration dbConfiguration) {
		IfExecutor ifExecutor = new IfExecutor();
		ElseIfExecutor elseIfExecutor = new ElseIfExecutor();
		ElseExecutor elseExecutor = new ElseExecutor();
		ExportExecutor exportExecutor = new ExportExecutor();
		QueryExecutor queryExecutor = new QueryExecutor();
		PrintExecutor printExecutor = new PrintExecutor();
		ForLoopExecutor forLoopExecutor = new ForLoopExecutor();

		Boolean previous_if_executed = null;
		Element lastElementExecuted = null;

		for (Element ifElement : parent.getElements()) {

			ifElement.setParent(parent);

			if (ifElement instanceof If) {
				previous_if_executed = ifExecutor.execute((If) ifElement, basePath, immediateParentId, dbConfiguration);
				lastElementExecuted = ifElement;
			} else if (ifElement instanceof ElseIf) {

				if (lastElementExecuted instanceof If || lastElementExecuted instanceof ElseIf) {
					if (!previous_if_executed) {
						previous_if_executed = elseIfExecutor.execute((ElseIf) ifElement, basePath, immediateParentId,
								dbConfiguration);
					}
				} else {
					throw new RuntimeException(
							"INVALID BLOCKS CONFIGURATION: ELSEIF spotted without a preceeding IF OR ELSEIF with in["
									+ immediateParentId + "\n");
				}

				lastElementExecuted = ifElement;

			} else if (ifElement instanceof Else) {

				if (lastElementExecuted instanceof If || lastElementExecuted instanceof ElseIf) {
					if (!previous_if_executed) {
						elseExecutor.execute((Else) ifElement, basePath, immediateParentId, dbConfiguration);
						previous_if_executed = null;
					}
				} else {
					throw new RuntimeException(
							"INVALID BLOCKS CONFIGURATION: ELSE spotted without a preceeding IF OR ELSEIF with in["
									+ immediateParentId + "\n");
				}

				lastElementExecuted = ifElement;

			} else if (ifElement instanceof Export) {
				exportExecutor.execute((Export) ifElement, basePath, immediateParentId, dbConfiguration);
				lastElementExecuted = ifElement;
			} else if (ifElement instanceof Query) {
				queryExecutor.execute((Query) ifElement, basePath, immediateParentId, dbConfiguration);
				lastElementExecuted = ifElement;
			} else if (ifElement instanceof Print) {
				printExecutor.execute((Print) ifElement, basePath, immediateParentId, dbConfiguration);
				lastElementExecuted = ifElement;
			} else if (ifElement instanceof For) {
				forLoopExecutor.execute((For) ifElement, basePath, immediateParentId, dbConfiguration);
				lastElementExecuted = ifElement;
			}
		}
	}
}
