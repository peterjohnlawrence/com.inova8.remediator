/**
 * 
 */
package com.inova8.remediator;

import static org.junit.Assert.*;

import java.io.File;
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
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpAsQuery;
import com.inova8.remediator.FederatedQuery;
import com.inova8.remediator.QueryPlan;
import com.inova8.remediator.Void;
import com.inova8.remediator.Workspace;

/**
 * @author PeterL
 * 
 */

public class RewritePerformanceTest {

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}

	public RewritePerformanceTest() {
		super();
	}

	String URL;
	String ontologyModelFile;
	String queryString;
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
	@Before
	public void setUp() throws Exception {
		path = "./resources/";
		queryFolder = "Q1";

		ontologyModelFile = readFile(path + queryFolder + "/model.txt",
				Charset.defaultCharset());

		workspace = new Workspace(new File(path + queryFolder), true);
		URL = "http://inova8.com/people/void";
		queryVoid = new Void(workspace, URL, true);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testRewritePerformance() throws Exception {
		String rewrittenQuery =queryTranslate(1); 
		for (int i = 0; i <= 100; i++) {
			FederatedQuery federatedQuery1 = new FederatedQuery(queryString);
			Op rewrittenOperations1 = federatedQuery1.rewrite(queryVoid, true);
			Query rewrittenQuery1 = OpAsQuery.asQuery(rewrittenOperations1);

		}
	}
	public final String queryTranslate(Integer testNumber) throws Exception {
		queryString = readFile(path + queryFolder + "/test" + testNumber + "/query.txt",
				Charset.defaultCharset());
		rewriteString = readFile(path + queryFolder + "/test"+ testNumber + "/rewrite.txt",
				Charset.defaultCharset());
		planString = readFile(path + queryFolder + "/test"+ testNumber + "/plan.txt",
				Charset.defaultCharset());

		FederatedQuery federatedQuery = new FederatedQuery(queryString);
		Op rewrittenOperations = federatedQuery.rewrite(queryVoid, true);
		QueryPlan queryPlan = federatedQuery.getQueryPlan();
//		assertEquals(queryPlan.toString(), planString.replaceAll("\\s+",
//				""), queryPlan.toString().replaceAll("\\s+", ""));	
		
		Query rewrittenQuery = OpAsQuery.asQuery(rewrittenOperations);
//
//		assertEquals(rewrittenQuery.toString(), rewriteString.replaceAll("\\s+",
//				""), rewrittenQuery.toString().replaceAll("\\s+", ""));
		return rewrittenQuery.toString();
	}
}
