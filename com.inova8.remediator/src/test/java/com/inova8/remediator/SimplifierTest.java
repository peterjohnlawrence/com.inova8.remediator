package com.inova8.remediator;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpAsQuery;
import com.hp.hpl.jena.sparql.algebra.TransformBase;
import com.hp.hpl.jena.sparql.algebra.TransformCopy;
import com.hp.hpl.jena.sparql.algebra.Transformer;
import com.inova8.workspace.Workspace;

import junit.framework.TestCase;

/**
 * The class <code>SimplifierTest</code> contains tests for the class {@link <code>Simplifier</code>}
 * 
 * @pattern JUnit Test Case
 * 
 * @generatedBy CodePro at 2/8/14 8:38 PM
 * 
 * @author PeterL
 * 
 * @version $Revision$
 */
public class SimplifierTest extends TestCase {

	/**
	 * Construct new test instance
	 * 
	 * @param name
	 *            the test name
	 */
	public SimplifierTest(String name) {
		super(name);
	}

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}

	String URL;
	String ontologyModelFile;
	String queryString;
	String simplifiedOpsString;
	String rewriteString;
	String planString;
	String queryResultString;
	String queryFolder;
	String path;

	OntModelSpec ontModelSpec;
	OntModel ontModel;
	Void queryVoid;
	Void queryVoid1;
	Workspace workspace;

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		path = "./test/models/";
		queryFolder = "Q1";
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testSimplifier() throws Exception {
		queryString = readFile(path + queryFolder + "/test" + 8
				+ "/query.txt", Charset.defaultCharset());
		simplifiedOpsString = readFile(path + queryFolder + "/test" + 8
				+ "/simplifiedops.txt", Charset.defaultCharset());

		Query query = QueryFactory.create(queryString);
		Op operations = Algebra.compile(query);
		//Simplifier  simplifier = new Simplifier();
		//TransformCopy  simplifier = new TransformCopy();
		TransformBase  simplifier = new TransformBase();
		Op simplifiedOp = Transformer.transform(simplifier, operations);
		simplifiedOp.toString();
		Query rewrittenQuery = OpAsQuery.asQuery(simplifiedOp);
		assertEquals(simplifiedOp.toString(), simplifiedOpsString.replaceAll("\\s+",
				""), simplifiedOp.toString().replaceAll("\\s+", ""));

	}

}
