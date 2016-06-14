package com.blocks.util;

import java.util.List;

import com.blocks.model.Block;
import com.blocks.model.Blocks;
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

public class HBlocksSemanticsValidator {

	public void validate(Blocks blocks) {

		if (blocks.getBasePath() == null) {
			throwException("<blocks> base path can't be null");
		}

		if (blocks.getId() == null) {
			throwException("<blocks> id can't be null");
		}

		for (Block block : blocks.getBlocks()) {
			validate(block);
		}
	}

	public void validate(Block block) {

		if (block.getId() == null) {
			throwException("<block> id can't be null");
		}

		validateVariablesAndElements(block, block.getId());

	}

	private void validateExport(Export export, String immediateParentId) {

		if (export.getId() == null) {
			throwException("<export> id can't be null with in [" + immediateParentId + "]");
		}

		if (export.getQueryFile() == null) {
			throwException(
					"query file can't be null with in [" + immediateParentId + "] EXPORT[" + export.getId() + "]");
		}
	}

	private void validatePrint(Print print, String immediateParentId) {

		if (print.getText() == null) {
			throwException("<print> text can't be null with in [" + immediateParentId + "]");
		}

	}

	private void validateFor(For rof, String immediateParentId) {

		if (rof.getId() == null) {
			throwException("<for> id can't be null with in [" + immediateParentId + "]");
		}

		if (rof.getQueryFile() == null) {
			throwException("query file can't be null with in [" + immediateParentId + "] FOR[" + rof.getId() + "]");
		}

		validateVariablesAndElements(rof, rof.getId());

	}

	private void validateQuery(Query query, String immediateParentId) {

		if (query.getId() == null) {
			throwException("<query> id can't be null with in [" + immediateParentId + "]");
		}

		if (query.getQueryFile() == null) {
			throwException("query file can't be null with in [" + immediateParentId + "] QUERY[" + query.getId() + "]");
		}
	}

	private void validateElse(Else eLsE, String immediateParentId) {

		if (eLsE.getId() == null) {
			throwException("<else> id can't be null with in [" + immediateParentId + "]");
		}

		validateVariablesAndElements(eLsE, eLsE.getId());

	}

	private void validateElseIf(ElseIf elseif, String immediateParentId) {

		if (elseif.getId() == null) {
			throwException("<elseif> id can't be null with in [" + immediateParentId + "]");
		}

		if (elseif.getCondition() == null) {
			throwException(
					"condition can't be null with in [" + immediateParentId + "] ELSEIF[" + elseif.getId() + "]");
		}

		validateVariablesAndElements(elseif, elseif.getId());

	}

	private void validateIf(If fi, String immediateParentId) {

		if (fi.getId() == null) {
			throwException("<if> id can't be null with in [" + immediateParentId + "]");
		}

		if (fi.getCondition() == null) {
			throwException("condition can't be null with in [" + immediateParentId + "] IF[" + fi.getId() + "]");
		}

		validateVariablesAndElements(fi, fi.getId());

	}

	public void validateVariable(Variable variable, String immediateParentId) {

		String name = variable.getName();
		String type = variable.getType();

		if (name == null) {
			throwException("<variable> name can't be null with in [" + immediateParentId + "]");
		} else if (name.startsWith(":")) {
			throwException("<variable> [" + name + "] name can't start with : with in [" + immediateParentId + "]");
		}

		if (type == null) {
			throwException("<variable> [" + name + "] type can't be null with in [" + immediateParentId + "]");
		} else {
			List<String> acceptableTypes = Variable.acceptableTypes;
			if (!acceptableTypes.contains(type)) {
				throwException("<variable> [" + name + "] type [" + type + "] invalid with in [" + immediateParentId
						+ "], It can be only one of: " + acceptableTypes.toString());
			}
		}

	}

	private void validateVariablesAndElements(Parent parent, String immediateParentId) {
		for (Variable variable : parent.getVariables()) {
			validateVariable(variable, immediateParentId);
		}

		Element lastElementValidated = null;

		for (Element element : parent.getElements()) {
			validateElement(element, immediateParentId, lastElementValidated);
			lastElementValidated = element;
		}
	}

	private void validateElement(Element element, String immediateParentId, Element lastElementValidated) {

		if (element instanceof If) {
			validateIf((If) element, immediateParentId);
		} else if (element instanceof ElseIf) {
			if (!(lastElementValidated instanceof If || lastElementValidated instanceof ElseIf)) {
				throwException("INVALID BLOCKS CONFIGURATION: ELSEIF[" + ((ElseIf) element).getId()
						+ "] spotted without a preceeding IF OR ELSEIF with in[" + immediateParentId + "]");
			}
			validateElseIf((ElseIf) element, immediateParentId);
		} else if (element instanceof Else) {
			if (!(lastElementValidated instanceof If || lastElementValidated instanceof ElseIf)) {
				throwException("INVALID BLOCKS CONFIGURATION: ELSE[" + ((Else) element).getId()
						+ "] spotted without a preceeding IF OR ELSEIF with in[" + immediateParentId + "]");
			}
			validateElse((Else) element, immediateParentId);
		} else if (element instanceof Export) {
			validateExport((Export) element, immediateParentId);
		} else if (element instanceof Query) {
			validateQuery((Query) element, immediateParentId);
		} else if (element instanceof Print) {
			validatePrint((Print) element, immediateParentId);
		} else if (element instanceof For) {
			validateFor((For) element, immediateParentId);
		}
	}

	private void throwException(String message) {
		throw new RuntimeException(message);
	}

}
