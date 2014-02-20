package com.inova8.remediator;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.*;

import static org.junit.Assert.*;
import com.inova8.workspace.RemediatorWorkspace;

/**
 * The class <code>RemediatorVoidFactoryTest</code> contains tests for the class <code>{@link RemediatorVoidFactory}</code>.
 *
 * @generatedBy CodePro at 2/13/14 8:05 AM
 * @author PeterL
 * @version $Revision: 1.0 $
 */
public class RemediatorVoidFactoryTest {
	/**
	 * Run the Void create(RemediatorWorkspace,String,Boolean) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 2/13/14 8:05 AM
	 */
	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}
	static void writeFile(String path, String input) throws IOException {
		Files.write(Paths.get(path), input.getBytes(),
				new OpenOption[] { StandardOpenOption.TRUNCATE_EXISTING ,
			StandardOpenOption.CREATE});
	}
	@Test
	public void testCreate_NoVoidURI()
		throws Exception {
		String workspacePath = "./test/models/Q1";
		RemediatorWorkspace workspace = new RemediatorWorkspace(new File(workspacePath), true);
		String voidURI = "";
		Boolean gatherStatistics = new Boolean(true);
		
		try{
			Void result = RemediatorVoidFactory.create(workspace, voidURI, gatherStatistics);
			assert false;
		}
		catch (URISyntaxException e)
		{
			assert true;
		}
	}
	@Test
	public void testCreate_MissingURI()
		throws Exception {
		String workspacePath = "./test/models/Q1";
		RemediatorWorkspace workspace = new RemediatorWorkspace(new File(workspacePath), true);
		String voidURI = "http://inova8.com/people/void/missing";
		Boolean gatherStatistics = new Boolean(true);
		
		try{
			Void result = RemediatorVoidFactory.create(workspace, voidURI, gatherStatistics);
			assert false;
		}
		catch (URISyntaxException e)
		{
			assert true;
		}
	}
	@Test
	public void testCreate_BadlyFormedModel() throws Exception {
		String workspacePath = "./test/models/Q1";
		RemediatorWorkspace workspace = new RemediatorWorkspace(new File(workspacePath), true);
		String voidURI = "http://inova8.com/people/void/bad";
		Boolean gatherStatistics = new Boolean(true);
		
		try{
			Void result = RemediatorVoidFactory.create(workspace, voidURI, gatherStatistics);
			assert false;
		}
		catch (URISyntaxException e)
		{
			assert true;
		}
	}
	@Test
	public void testCreate_OK() throws Exception {
		String workspacePath = "./test/models/Q1";
		RemediatorWorkspace workspace = new RemediatorWorkspace(new File(
				workspacePath), true);
		String voidURI = "http://inova8.com/people/void";
		Boolean gatherStatistics = new Boolean(true);

		try {
			Void result = RemediatorVoidFactory.create(workspace, voidURI,
					gatherStatistics);
			assert true;
		} catch (URISyntaxException e) {
			assert false;
		}
	}
	@Test
	public final void test_BuildVocabulary() throws Exception {

		String workspacePath = "./test/models/Q1";
		RemediatorWorkspace workspace = new RemediatorWorkspace(new File(
				workspacePath), true);
		String voidURI = "http://inova8.com/people/void";
		Boolean gatherStatistics = new Boolean(false);
		try {
			Void queryVoid = RemediatorVoidFactory.create(workspace, voidURI, gatherStatistics);
			queryVoid.getVoidModel().write(System.out, "RDF/XML");
			queryVoid.getVocabularyModel().write(System.out, "RDF/XML");
		} catch (Exception e) {
			assert false;
		}
	}

	@Test
	public final void test_StatisticsQuery() throws Exception{
		
		String workspacePath = "./test/models/Q1";
		RemediatorWorkspace workspace = new RemediatorWorkspace(new File(workspacePath), true);
		String voidURI = "http://inova8.com/people/void";
		Boolean gatherStatistics = new Boolean(true);

		try {
			Void queryVoid = RemediatorVoidFactory.create(workspace, voidURI, gatherStatistics);
			
			writeFile( workspacePath +"/statistics/statistics.query.res.ttl",queryVoid.getStatisticsModel().toString());
			//queryVoid.getStatisticsModel().write(System.out,"TURTLE");
		} catch (Exception e) {
			assert false;
		}		
	}
	
	@Test
	public final void test_StatisticsRead()throws Exception {
		
		String workspacePath = "./test/models/Q1";
		RemediatorWorkspace workspace = new RemediatorWorkspace(new File(workspacePath), true);
		String voidURI = "http://inova8.com/people/void";
		Boolean gatherStatistics = new Boolean(false);
		try {
			Void queryVoid = RemediatorVoidFactory.create(workspace, voidURI, gatherStatistics);
			writeFile( workspacePath +"/statistics/statistics.read.res.ttl",queryVoid.getStatisticsModel().toString());

			//queryVoid.getStatisticsModel().write(System.out,"TURTLE");
		} catch (Exception e) {
			assert false;
		}
	}
	
	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 2/13/14 8:05 AM
	 */
	@Before
	public void setUp()
		throws Exception {
		// add additional set up code here
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 2/13/14 8:05 AM
	 */
	@After
	public void tearDown()
		throws Exception {
		// Add additional tear down code here
	}

	/**
	 * Launch the test.
	 *
	 * @param args the command line arguments
	 *
	 * @generatedBy CodePro at 2/13/14 8:05 AM
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(RemediatorVoidFactoryTest.class);
	}
}