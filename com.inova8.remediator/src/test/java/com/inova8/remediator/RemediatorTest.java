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
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.inova8.remediator.RemediatorQuery;
import com.inova8.remediator.QueryPlan;
import com.inova8.remediator.Void;
import com.inova8.workspace.RemediatorWorkspace;

/**
 * @author PeterL
 * 
 */
@FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
public class RemediatorTest {

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}
	static void writeFile(String path, String input) throws IOException {
		Files.write(Paths.get(path), input.getBytes(),
				new OpenOption[] { StandardOpenOption.TRUNCATE_EXISTING ,
			StandardOpenOption.CREATE});
	}

	String path = "./test/models/";
	String queryFolder = "Q2";
	
	public RemediatorTest() {
		super();
	}

	String URL;

	Void voidModel;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		RemediatorWorkspace workspace = new RemediatorWorkspace(new File(path + queryFolder), true);
		URL = "http://inova8.com/Q2/book-publisher";
		voidModel = new Void(workspace, URL, true);
	}

	public final void queryTranslate(Integer testNumber) throws Exception {
		String queryString = readFile(path + queryFolder + "/test" + testNumber
				+ "/query.txt", Charset.defaultCharset());
		String simplifiedOpsString = readFile(path + queryFolder + "/test"
				+ testNumber + "/simplifiedops.txt", Charset.defaultCharset());
		String rewrittenOpsString = readFile(path + queryFolder + "/test"
				+ testNumber + "/rewrittenops.txt", Charset.defaultCharset());
		String planString = readFile(path + queryFolder + "/test" + testNumber
				+ "/plan.txt", Charset.defaultCharset());
		String rewriteString = readFile(path + queryFolder + "/test" + testNumber
				+ "/rewrite.txt", Charset.defaultCharset());

		RemediatorQuery query = RemediatorQueryFactory.create(queryString);
		
		
		writeFile(path + queryFolder + "/test"
				+ testNumber + "/simplifiedops.res.txt", query.getSimplifiedOperations().toString() );
		assertEquals(
				query.getSimplifiedOperations().toString(),
				simplifiedOpsString.replaceAll("\\s+", ""),
				query.getSimplifiedOperations().toString()
						.replaceAll("\\s+", ""));
		
		RemediatorFederatedQuery federatedQuery = RemediatorFederatedQueryFactory.create(query, voidModel, true);
		
		Op rewrittenOps= federatedQuery.getRewrittenOperations();
		
		writeFile(path + queryFolder + "/test" + testNumber
				+ "/rewrittenops.res.txt",rewrittenOps.toString());
//		assertEquals(rewrittenOps.toString(), rewrittenOpsString.replaceAll("\\s+", ""),
//				rewrittenOps.toString().replaceAll("\\s+", ""));
		
		QueryPlan queryPlan = federatedQuery.getQueryPlan();
		
		writeFile(path + queryFolder + "/test" + testNumber
				+ "/plan.res.txt",queryPlan.toString());
		assertEquals(queryPlan.toString(), planString.replaceAll("\\s+", ""),
				queryPlan.toString().replaceAll("\\s+", ""));

		Query rewrittenQuery = federatedQuery.getRewrittenQuery();
		
		writeFile(path + queryFolder + "/test" + testNumber
				+ "/rewrite.res.txt",rewrittenQuery.toString());

		assertEquals(rewrittenQuery.toString(), rewriteString.replaceAll(
				"\\s+", ""), rewrittenQuery.toString().replaceAll("\\s+", ""));

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void test_rewrite1() throws Exception {
		queryTranslate(1);

	}

	@Test
	public final void test_rewrite2() throws Exception {
		queryTranslate(2);

	}

	@Test
	public final void test_rewrite3() throws Exception {
		queryTranslate(3);

	}

	@Test
	public final void test_rewrite4() throws Exception {
		queryTranslate(4);

	}

}
