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
import com.inova8.remediator.RemediatorQuery;
import com.inova8.remediator.QueryPlan;
import com.inova8.remediator.Void;
import com.inova8.workspace.RemediatorWorkspace;

/**
 * @author PeterL
 * 
 */
@FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
public class RemediatorFederatedQueryFactoryPerformanceTest {

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
	String queryFolder = "Q1";
	int numberRuns;
	public RemediatorFederatedQueryFactoryPerformanceTest() {
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
		URL = "http://inova8.com/people/void";
		numberRuns = 10;
		voidModel = new Void(workspace, URL, false);
	}

	public final void queryTranslate(Integer testNumber) throws Exception {
		String queryString = readFile(path + queryFolder + "/test" + testNumber
				+ "/query.txt", Charset.defaultCharset());
		for (int numberRuns = 0; numberRuns < 10; numberRuns++) {
			RemediatorQuery query = RemediatorQueryFactory.create(queryString);
			RemediatorFederatedQuery federatedQuery = RemediatorFederatedQueryFactory
					.create(query, voidModel, true);
		}
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

	@Test
	public final void test_rewrite5() throws Exception {
		queryTranslate(5);

	}

	@Test
	public final void test_rewrite6() throws Exception {
		queryTranslate(6);

	}

	@Test
	public final void test_rewrite7() throws Exception {
		queryTranslate(7);

	}

}
