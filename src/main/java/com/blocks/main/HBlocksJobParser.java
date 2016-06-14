package com.blocks.main;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

import com.blocks.model.Block;
import com.blocks.model.Blocks;
import com.blocks.model.Else;
import com.blocks.model.ElseIf;
import com.blocks.model.Export;
import com.blocks.model.For;
import com.blocks.model.If;
import com.blocks.model.Print;
import com.blocks.model.Query;
import com.blocks.model.Variable;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class HBlocksJobParser {
	XStream xstream = null;
	
	
	public HBlocksJobParser() {
		this.xstream = new XStream(new StaxDriver());
		prepare(xstream);
	}

	public Blocks getBlocksFromXML(String xmlPath) throws FileNotFoundException {

		File xml = new File(xmlPath);

		FileInputStream xmlInputStream = new FileInputStream(xml);

		Blocks blocks = (Blocks) xstream.fromXML(xmlInputStream);

		return blocks;
	}

	public LinkedList<Block> getBlocksListFromXML(String xmlPath) throws FileNotFoundException {
		Blocks blocks = (Blocks) getBlocksFromXML(xmlPath);
		return blocks.getBlocks();
	}

	private void prepare(XStream xstream) {

		// do aliasing
		xstream.alias("blocks", Blocks.class);
		xstream.alias("block", Block.class);
		xstream.alias("export", Export.class);
		xstream.alias("for", For.class);
		xstream.alias("print", Print.class);
		xstream.alias("variable", Variable.class);
		xstream.alias("if", If.class);
		xstream.alias("elseif", ElseIf.class);
		xstream.alias("else", Else.class);
		xstream.alias("query", Query.class);

		// set implicit collections
		xstream.addImplicitCollection(Blocks.class, "blocks", "block", Block.class);

		xstream.addImplicitCollection(Block.class, "variables", "variable", Variable.class);
		xstream.addImplicitCollection(Block.class, "elements", "export", Export.class);
		xstream.addImplicitCollection(Block.class, "elements", "if", If.class);
		xstream.addImplicitCollection(Block.class, "elements", "elseif", ElseIf.class);
		xstream.addImplicitCollection(Block.class, "elements", "else", Else.class);
		xstream.addImplicitCollection(Block.class, "elements", "query", Query.class);
		xstream.addImplicitCollection(Block.class, "elements", "print", Print.class);
		xstream.addImplicitCollection(Block.class, "elements", "for", For.class);

		xstream.addImplicitCollection(If.class, "variables", "variable", Variable.class);
		xstream.addImplicitCollection(If.class, "elements", "export", Export.class);
		xstream.addImplicitCollection(If.class, "elements", "if", If.class);
		xstream.addImplicitCollection(If.class, "elements", "elseif", ElseIf.class);
		xstream.addImplicitCollection(If.class, "elements", "else", Else.class);
		xstream.addImplicitCollection(If.class, "elements", "query", Query.class);
		xstream.addImplicitCollection(If.class, "elements", "print", Print.class);
		xstream.addImplicitCollection(If.class, "elements", "for", For.class);

		xstream.addImplicitCollection(ElseIf.class, "variables", "variable", Variable.class);
		xstream.addImplicitCollection(ElseIf.class, "elements", "export", Export.class);
		xstream.addImplicitCollection(ElseIf.class, "elements", "if", If.class);
		xstream.addImplicitCollection(ElseIf.class, "elements", "elseif", ElseIf.class);
		xstream.addImplicitCollection(ElseIf.class, "elements", "else", Else.class);
		xstream.addImplicitCollection(ElseIf.class, "elements", "query", Query.class);
		xstream.addImplicitCollection(ElseIf.class, "elements", "print", Print.class);
		xstream.addImplicitCollection(ElseIf.class, "elements", "for", For.class);

		xstream.addImplicitCollection(Else.class, "variables", "variable", Variable.class);
		xstream.addImplicitCollection(Else.class, "elements", "export", Export.class);
		xstream.addImplicitCollection(Else.class, "elements", "if", If.class);
		xstream.addImplicitCollection(Else.class, "elements", "elseif", ElseIf.class);
		xstream.addImplicitCollection(Else.class, "elements", "else", Else.class);
		xstream.addImplicitCollection(Else.class, "elements", "query", Query.class);
		xstream.addImplicitCollection(Else.class, "elements", "print", Print.class);
		xstream.addImplicitCollection(Else.class, "elements", "for", For.class);

		xstream.addImplicitCollection(For.class, "variables", "variable", Variable.class);
		xstream.addImplicitCollection(For.class, "elements", "export", Export.class);
		xstream.addImplicitCollection(For.class, "elements", "if", If.class);
		xstream.addImplicitCollection(For.class, "elements", "elseif", ElseIf.class);
		xstream.addImplicitCollection(For.class, "elements", "else", Else.class);
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

		xstream.useAttributeFor(ElseIf.class, "id");
		xstream.useAttributeFor(ElseIf.class, "condition");

		xstream.useAttributeFor(Else.class, "id");

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
	private void printXMLFromObject(XStream xstream, Blocks blocks) {

		String xml = xstream.toXML(blocks);
		System.out.println(formatXml(xml));
	}

	public void printXML(String[] args) throws IOException {

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
