package com.blocks.main;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import com.blocks.model.For;
import com.blocks.model.If;
import com.blocks.model.Print;
import com.blocks.model.Query;
import com.blocks.model.Variable;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class HBlockExecutor {

	private static Logger logger = Logger.getLogger(HBlockExecutor.class);

	public static void main(String[] args) throws IOException {

		if (args.length != 2) {
			throw new RuntimeException(
					"Invalid Usage: HBlockExecutor requires two arguments in order: blocks-xml-config, blocks properties file path");
		}

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
		printXML(args);
		System.out.println(
				"\n------------------------------------------------------------------------------------------\n");

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
		xstream.alias("for", For.class);
		xstream.alias("print", Print.class);
		xstream.alias("variable", Variable.class);
		xstream.alias("if", If.class);
		xstream.alias("query", Query.class);

		// set implicit collections
		xstream.addImplicitCollection(Blocks.class, "blocks", "block", Block.class);

		xstream.addImplicitCollection(Block.class, "variables", "variable", Variable.class);
		xstream.addImplicitCollection(Block.class, "elements", "export", Export.class);
		xstream.addImplicitCollection(Block.class, "elements", "if", If.class);
		xstream.addImplicitCollection(Block.class, "elements", "query", Query.class);
		xstream.addImplicitCollection(Block.class, "elements", "print", Print.class);
		xstream.addImplicitCollection(Block.class, "elements", "for", For.class);

		xstream.addImplicitCollection(If.class, "variables", "variable", Variable.class);
		xstream.addImplicitCollection(If.class, "elements", "export", Export.class);
		xstream.addImplicitCollection(If.class, "elements", "if", If.class);
		xstream.addImplicitCollection(If.class, "elements", "query", Query.class);
		xstream.addImplicitCollection(If.class, "elements", "print", Print.class);
		xstream.addImplicitCollection(If.class, "elements", "for", For.class);

		xstream.addImplicitCollection(For.class, "variables", "variable", Variable.class);
		xstream.addImplicitCollection(For.class, "elements", "export", Export.class);
		xstream.addImplicitCollection(For.class, "elements", "if", If.class);
		xstream.addImplicitCollection(For.class, "elements", "query", Query.class);
		xstream.addImplicitCollection(For.class, "elements", "print", Print.class);
		xstream.addImplicitCollection(For.class, "elements", "for", For.class);

		// set attributes
		xstream.useAttributeFor(Blocks.class, "id");
		xstream.useAttributeFor(Blocks.class, "basePath");
		xstream.aliasField("base-path", Blocks.class, "basePath");

		xstream.useAttributeFor(Block.class, "id");

		xstream.useAttributeFor(Export.class, "id");
		xstream.useAttributeFor(Export.class, "queryFile");
		xstream.aliasField("query-file", Export.class, "queryFile");

		xstream.useAttributeFor(If.class, "id");
		xstream.useAttributeFor(If.class, "condition");

		xstream.useAttributeFor(Query.class, "id");
		xstream.useAttributeFor(Query.class, "queryFile");
		xstream.aliasField("query-file", Query.class, "queryFile");

		xstream.useAttributeFor(Variable.class, "name");
		xstream.useAttributeFor(Variable.class, "type");
		xstream.useAttributeFor(Variable.class, "value");

		xstream.useAttributeFor(For.class, "id");
		xstream.useAttributeFor(For.class, "condition");
		xstream.useAttributeFor(For.class, "queryFile");
		xstream.aliasField("query-file", For.class, "queryFile");

		xstream.useAttributeFor(Print.class, "text");

		xstream.setMode(XStream.NO_REFERENCES);
	}

	@SuppressWarnings("unused")
	private static void printXMLFromObject(XStream xstream, Blocks blocks) {

		String xml = xstream.toXML(blocks);
		System.out.println(formatXml(xml));
	}

	private static void printXML(String[] args) throws IOException {

		FileInputStream fis = null;
		BufferedReader br = null;
		String xml = null;

		fis = new FileInputStream(new File(args[0]));
		br = new BufferedReader(new InputStreamReader(fis));
		StringBuffer stringBuffer = new StringBuffer();

		String line = null;
		while ((line = br.readLine()) != null) {
			stringBuffer.append(line.trim());
		}
		br.close();

		xml = stringBuffer.toString().trim();

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
