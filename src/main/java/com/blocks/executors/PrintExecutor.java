package com.blocks.executors;

import org.apache.log4j.Logger;

import com.blocks.dao.DBConfiguration;
import com.blocks.dao.DBQueryExecutor;
import com.blocks.model.Print;

public class PrintExecutor {

	private static Logger logger = Logger.getLogger(PrintExecutor.class);

	public void execute(Print printElement, String basePath, String immediateParentId,
			DBConfiguration dbConfiguration) {

		String text = printElement.getText();

		if (text != null && !text.isEmpty()) {
			text = new DBQueryExecutor().substituteVariables(printElement.getParent(), immediateParentId, text);
			logger.info(text);
			System.out.println(text);
		}

	}
}
