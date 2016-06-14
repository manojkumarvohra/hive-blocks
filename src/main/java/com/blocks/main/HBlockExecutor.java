package com.blocks.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.blocks.dao.DBConfiguration;
import com.blocks.executors.BlocksExecutor;
import com.blocks.model.Blocks;
import com.blocks.util.HBlocksJobParser;
import com.blocks.util.HBlocksSemanticsValidator;

public class HBlockExecutor {

	private static Logger logger = Logger.getLogger(HBlockExecutor.class);

	public static void main(String[] args) throws IOException {

		if (args.length != 2) {
			throw new RuntimeException(
					"Invalid Usage: HBlockExecutor requires two arguments in order: blocks-xml file path, blocks properties file path");
		}

		Blocks blocks = null;
		HBlocksJobParser hBlocksJobParser = new HBlocksJobParser();
		
		try {
			blocks = hBlocksJobParser.getBlocksFromXML(args[0]);
		} catch (FileNotFoundException e) {

			System.out.println("Unable to find xml file");
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println(
				"\n--------------------------------Executing XML---------------------------------------------\n");
		hBlocksJobParser.printXML(args);
		System.out.println(
				"\n------------------------------------------------------------------------------------------\n");
		
		System.out.println("Validating Blocks Semantics");
		HBlocksSemanticsValidator semanticsValidator = new HBlocksSemanticsValidator();
		semanticsValidator.validate(blocks);
		System.out.println("Blocks Semantics are correct");
		
		DBConfiguration dbConfiguration = getDbConfiguration(args);
		BlocksExecutor blocksExecutor = new BlocksExecutor();
		blocksExecutor.execute(blocks, dbConfiguration);
	}

	private static DBConfiguration getDbConfiguration(String[] args) {
		Properties dbProperties = new Properties();
		InputStream inputStream = null;
		DBConfiguration dbConfiguration = null;

		try {

			inputStream = new FileInputStream(new File(args[1]));

			dbProperties.load(inputStream);

			String jdbcDriver = dbProperties.getProperty("jdbcdriver");
			String dbUrl = dbProperties.getProperty("dburl");
			String dbUserid = dbProperties.getProperty("dbuserid");
			String dbPassword = dbProperties.getProperty("dbpassword");

			dbConfiguration = new DBConfiguration(jdbcDriver, dbUrl, dbUserid, dbPassword);

		} catch (IOException ex) {
			String errorMessage = "Unable to load db properties." + ex.getMessage();
			throw new RuntimeException(errorMessage);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.warn("Unable to close properties stream." + e.getMessage());
				}
			}
		}

		return dbConfiguration;
	}	
}
