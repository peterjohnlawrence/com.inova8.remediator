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

import org.apache.jena.atlas.logging.Log;
import org.junit.After;
import org.junit.Before;

import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.ModelFactory;
//import com.hp.hpl.jena.sparql.engine.ref.QueryEngineRef;
import com.inova8.remediator.Void;
import com.inova8.sparql.engine.QueryEngineRemediator;
import com.inova8.workspace.Workspace;

import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

/**
 * @author PeterL
 * 
 */

public class QueryTest {
	
	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}

	public QueryTest() {
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
		path = "./test/models/";
		queryFolder = "Q1";
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testQuery() throws Exception {
		rewriteString = readFile(path + queryFolder + "/test"+ 5 + "/rewrite.txt",
				Charset.defaultCharset());

			queryExecute(rewriteString,5);

	}

	private void queryExecute(String rewriteString,Integer testNumber) throws Exception {
		queryResultString = readFile(path + queryFolder + "/test"+ testNumber + "/queryresult.txt",
				Charset.defaultCharset());
        OntModel model =  ModelFactory.createOntologyModel(); 
		Query query = QueryFactory.create(rewriteString);
		QueryEngineRemediator.register();
		QueryExecution qexec = QueryExecutionFactory.create(query,model);
		try {
			ResultSet results = qexec.execSelect();
			String resultString = ResultSetFormatter.asText(results);
			assertEquals(resultString.toString(), queryResultString.replaceAll("\\s+",
					""), resultString.toString().replaceAll("\\s+", ""));
		} catch (Exception e) {
			Log.debug(Void.class, "Failed query:" + e.getStackTrace().toString());
			fail(e.getStackTrace().toString());
		} finally {
			qexec.close();
		}
	}
}
