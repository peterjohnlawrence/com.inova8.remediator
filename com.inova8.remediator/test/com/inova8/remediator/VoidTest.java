package com.inova8.remediator;

import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.inova8.remediator.Void;
import com.inova8.remediator.Workspace;

public class VoidTest {
	String path;
	String URL;
	String queryFolder;
	OntModelSpec voidModelSpec;
	OntModel voidModel;
	OntModelSpec vocabularyModelSpec;
	OntModel vocabularyModel;
	Workspace workspace;

	@Before
	public void setUp() throws Exception {
		String[] extensions = { ".rdf", ".owl" };

		path = "./resources/Q1";

	    workspace = new Workspace(new File(path), true);

		URL = "http://inova8.com/people/void";
		queryFolder = "Q1";
		voidModelSpec = new OntModelSpec(OntModelSpec.OWL_MEM);

		voidModelSpec.setDocumentManager(workspace.getDocumentManager());

		voidModel = ModelFactory.createOntologyModel(voidModelSpec);

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testBuildVocabulary() {
		Void queryVoid = new Void(voidModel, URL,true);
		//queryVoid.buildVocabularyModel();
		queryVoid.getVocabularyModel().write(System.out,"RDF/XML-ABBREV");
	}

	@Test
	public final void testVoid() {
		Void queryVoid = new Void(voidModel, URL,true);
		// ontModel.getBaseModel().write(System.out,"RDF/XML-ABBREV");
		//queryVoid.updatePartitionStatistics();

		queryVoid.getVoidModel().write(System.out,"RDF/XML-ABBREV");
	}
	
	@Test
	public final void testVoidWorkspace() {
		Void queryVoid = new Void(workspace, URL,true);
		// ontModel.getBaseModel().write(System.out,"RDF/XML-ABBREV");
		//queryVoid.updatePartitionStatistics();
		queryVoid.getVoidModel().write(System.out,"RDF/XML-ABBREV");
	}
}
