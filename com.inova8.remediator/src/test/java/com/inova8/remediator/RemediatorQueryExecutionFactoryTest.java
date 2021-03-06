package com.inova8.remediator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.jena.atlas.logging.Log;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.inova8.workspace.RemediatorWorkspace;

import junit.framework.TestCase;

/**
 * The class <code>RemediatorQueryFactoryTest</code> contains tests for the
 * class {@link <code>RemediatorQueryFactory</code>}
 *
 * @pattern JUnit Test Case
 *
 * @generatedBy CodePro at 2/13/14 4:10 PM
 *
 * @author PeterL
 *
 * @version $Revision$
 */
public class RemediatorQueryExecutionFactoryTest extends TestCase {

	
	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}
	static void writeFile(String path, String input) throws IOException {
		Files.write(Paths.get(path), input.getBytes(),
				new OpenOption[] { StandardOpenOption.TRUNCATE_EXISTING ,
			StandardOpenOption.CREATE});
	}
	String queryFolder;
	String path;
	Void voidModel;
	/**
	 * Construct new test instance
	 *
	 * @param name the test name
	 */
	public RemediatorQueryExecutionFactoryTest(String name) {
		super(name);
	}

	/**
	 * Perform pre-test initialization
	 *
	 * @throws Exception
	 *
	 * @see TestCase#setUp()
	 */
	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		path = "./test/models/";
		queryFolder = "Q1";
		String workspacePath = "./test/models/Q1";
		RemediatorWorkspace workspace = new RemediatorWorkspace(new File(
				workspacePath), true);
		String voidURI = "http://inova8.com/people/void";
		Boolean gatherStatistics = new Boolean(true);

		try {
			voidModel = RemediatorVoidFactory.create(workspace, voidURI,
					gatherStatistics);
			assert true;
		} catch (URISyntaxException e) {
			assert false;
		}
	}
	public void queryExecute(Integer testNumber) throws Exception {
		String queryString = readFile(path + queryFolder + "/test" + testNumber + "/query.txt",
				Charset.defaultCharset());
		String queryResultString = readFile(path + queryFolder + "/test"+ testNumber + "/queryresult.txt",
				Charset.defaultCharset());
		
		RemediatorQuery query = RemediatorQueryFactory.create(queryString);
		RemediatorFederatedQuery federatedQuery = RemediatorFederatedQueryFactory.create(query, voidModel, false);
		
		QueryExecution  queryExecution = RemediatorQueryExecutionFactory.create(federatedQuery);
		
		try {
			ResultSet results = queryExecution.execSelect();
			String resultString = ResultSetFormatter.asText(results);
			writeFile(path + queryFolder + "/test"
					+ testNumber + "/queryresult.res.txt", resultString.toString() ); 
			assertEquals(resultString.toString(), queryResultString.replaceAll("\\s+",
					""), resultString.toString().replaceAll("\\s+", ""));
		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			queryExecution.close();
		}
	}

	/**
	 * Run the RemediatorQuery create(String) method test
	 */
	
	@Test
	public final void test_execute1() throws Exception {
		queryExecute(1); 

	}
	@Test
	public final void test_execute2() throws Exception {
		queryExecute(2); 

	}

	@Test
	public final void test_execute3() throws Exception {
		queryExecute(3); 	

		}
	@Test
	public final void test_execute4() throws Exception {
		queryExecute(4); 

	}
	@Test
	public final void test_execute5() throws Exception {
		queryExecute(5); 

	}
	@Test
	public final void test_execute6() throws Exception {
		queryExecute(6);

	}
	@Test
	public final void test_execute7() throws Exception {
		queryExecute(7);

	}
	@Test
	public final void test_execute8() throws Exception {
		queryExecute(8);

	}
}

/*$CPS$ This comment was generated by CodePro. Do not edit it.
 * patternId = com.instantiations.assist.eclipse.pattern.testCasePattern
 * strategyId = com.instantiations.assist.eclipse.pattern.testCasePattern.junitTestCase
 * additionalTestNames = 
 * assertTrue = false
 * callTestMethod = true
 * createMain = false
 * createSetUp = true
 * createTearDown = false
 * createTestFixture = false
 * createTestStubs = false
 * methods = create(QString;)
 * package = com.inova8.remediator
 * package.sourceFolder = com.inova8.remediator/src/test/java
 * superclassType = junit.framework.TestCase
 * testCase = RemediatorQueryFactoryTest
 * testClassType = RemediatorQueryFactory
 */