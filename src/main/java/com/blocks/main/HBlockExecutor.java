package com.blocks.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import com.blocks.dao.DBConfiguration;
import com.blocks.executors.BlocksExecutor;
import com.blocks.model.Block;
import com.blocks.model.Blocks;
import com.blocks.model.Export;
import com.blocks.model.If;
import com.blocks.model.Loop;
import com.blocks.model.Query;
import com.blocks.model.Variable;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class HBlockExecutor {

	private static Logger logger = Logger.getLogger(HBlockExecutor.class);

	public static void main(String[] args) {

		XStream xstream = new XStream(new StaxDriver());
		prepare(xstream);
		Blocks blocks = null;

		try {
			blocks = getBlocksFromXML(xstream, args);
		} catch (FileNotFoundException e) {

			System.out.println("Unable to find xml file");
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println(
				"\n--------------------------------Executing XML---------------------------------------------\n");
		printXMLFromObject(xstream, blocks);
		System.out.println(
				"\n------------------------------------------------------------------------------------------\n");

		DBConfiguration dbConfiguration = getDbConfiguration();
		BlocksExecutor blocksExecutor = new BlocksExecutor();
		blocksExecutor.execute(blocks, dbConfiguration);
	}

	private static DBConfiguration getDbConfiguration() {
		Properties dbProperties = new Properties();
		InputStream inputStream = null;
		DBConfiguration dbConfiguration = null;

		try {

			dbProperties.load(HBlockExecutor.class.getResourceAsStream("/hive_blocks.properties"));

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

	private static Blocks getBlocksFromXML(XStream xstream, String[] args) throws FileNotFoundException {

		String filePath = null;

		if (args.length == 0) {
			throw new RuntimeException("Error:Please provide the block definitions xml");
		}
		filePath = args[0];

		File xml = new File(filePath);
		FileInputStream xmlInputStream = new FileInputStream(xml);

		Blocks blocks = (Blocks) xstream.fromXML(xmlInputStream);

		return blocks;
	}

	private static void prepare(XStream xstream) {

		// do aliasing
		xstream.alias("blocks", Blocks.class);
		xstream.alias("block", Block.class);
		xstream.alias("export", Export.class);
		xstream.alias("variable", Variable.class);
		xstream.alias("if", If.class);
		xstream.alias("loop", Loop.class);
		xstream.alias("query", Query.class);

		// set implicit collections
		xstream.addImplicitCollection(Blocks.class, "blocks", "block", Block.class);
		xstream.addImplicitCollection(Block.class, "variables", "variable", Variable.class);
		xstream.addImplicitCollection(Block.class, "exports", "export", Export.class);
		xstream.addImplicitCollection(Block.class, "Ifs", "if", If.class);
		xstream.addImplicitCollection(Block.class, "queries", "query", Query.class);
		
		xstream.addImplicitCollection(If.class, "exports", "export", Export.class);
		xstream.addImplicitCollection(If.class, "Ifs", "if", If.class);
		xstream.addImplicitCollection(If.class, "queries", "query", Query.class);
		
		// set attributes
		xstream.useAttributeFor(Blocks.class, "name");
		xstream.useAttributeFor(Blocks.class, "basePath");
		xstream.aliasField("base-path", Blocks.class, "basePath");
		xstream.useAttributeFor(Block.class, "blockId");
		xstream.aliasField("block-id", Block.class, "blockId");
		
		xstream.useAttributeFor(Export.class, "executionOrder");
		xstream.aliasField("execution-order", Export.class, "executionOrder");
		xstream.useAttributeFor(Export.class, "queryFile");
		xstream.aliasField("query-file", Export.class, "queryFile");
		
		xstream.useAttributeFor(If.class, "executionOrder");
		xstream.aliasField("execution-order", If.class, "executionOrder");
		xstream.useAttributeFor(If.class, "condition");
		
		xstream.useAttributeFor(Query.class, "executionOrder");
		xstream.aliasField("execution-order", Query.class, "executionOrder");
		xstream.useAttributeFor(Query.class, "queryFile");
		xstream.aliasField("query-file", Query.class, "queryFile");
		
		xstream.useAttributeFor(Variable.class, "name");
		xstream.useAttributeFor(Variable.class, "type");

		xstream.setMode(XStream.NO_REFERENCES);
	}

	private static void printXMLFromObject(XStream xstream, Blocks blocks) {

		String xml = xstream.toXML(blocks);
		System.out.println(formatXml(xml));
	}

	@SuppressWarnings("unused")
	private static void printXMLFromDummyObject(XStream xstream) {

		Blocks blocks = new Blocks();
		blocks.setBasePath("/hblocks/app");
		blocks.setName("hello-hblocks");

		Block block1 = new Block();
		block1.setBlockId(0);

		// set block1 variables
		Variable b1Var1 = new Variable();
		b1Var1.setName("b1var1");
		b1Var1.setType("int");
		Variable b1Var2 = new Variable();
		b1Var2.setName("b1Var2");
		b1Var2.setType("string");

		block1.addVariables(b1Var1);
		block1.addVariables(b1Var2);

		// set block1 exports
		Export b1Exp1 = new Export();
		b1Exp1.setExecutionOrder(0);
		b1Exp1.setQueryFile("1.hql");
		Export b1Exp2 = new Export();
		b1Exp2.setExecutionOrder(1);
		b1Exp2.setQueryFile("2.hql");
		block1.addExports(b1Exp1);
		block1.addExports(b1Exp2);

		// set block1 IFs
		If if1 = new If();
		if1.setExecutionOrder(2);
		if1.setCondition(":b1Var1 > 0");
		Query if1_query = new Query();
		if1_query.setExecutionOrder(1);
		if1_query.setQueryFile("q1.hql");
		if1.addQueries(if1_query);

		If if1_1 = new If();
		if1_1.setExecutionOrder(0);
		if1_1.setCondition(":b1Var2 = 9");

		Query if1_1_query = new Query();
		if1_1_query.setExecutionOrder(0);
		if1_1_query.setQueryFile("q1_sub_1.hql");

		if1_1.addQueries(if1_1_query);
		if1.addIfs(if1_1);

		block1.addIfs(if1);

		// set block1 Queries
		Query b1_query = new Query();
		b1_query.setExecutionOrder(3);
		b1_query.setQueryFile("q1_b1_master.hql");

		blocks.addBlocks(block1);

		String xml = xstream.toXML(blocks);
		System.out.println(formatXml(xml));
	}

	public static String formatXml(String xml) {

		try {
			Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();

			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			Source xmlSource = new SAXSource(new InputSource(new ByteArrayInputStream(xml.getBytes())));
			StreamResult res = new StreamResult(new ByteArrayOutputStream());

			serializer.transform(xmlSource, res);

			return new String(((ByteArrayOutputStream) res.getOutputStream()).toByteArray());

		} catch (Exception e) {
			return xml;
		}
	}
}
