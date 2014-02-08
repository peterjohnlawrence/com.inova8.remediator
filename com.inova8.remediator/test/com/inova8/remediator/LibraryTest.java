package com.inova8.remediator;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.jena.atlas.web.HttpException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.inova8.workspace.Workspace;

public class LibraryTest {

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testLibraryWithExtensions() {
		String[] extensionsArray = { ".rdf", ".owl" };
		Set<String> extensions = new HashSet<String>(
				Arrays.asList(extensionsArray));
		Workspace library = new Workspace(
				new File(
						"./test/models/Q1"),
				true, extensions);

	}

	@Test
	public final void testLibraryWithDefaultExtensions() {
		Workspace library = new Workspace(
				new File(
						"./test/models/Q1"),
				true);

	}
	@Test
	public final void testWorkspace() {
		OntModelSpec ontModelSpecs;
		OntModel ontModel;
		Workspace library = new Workspace(
				new File(
						"./test/models/Q1"),
				true);

		ontModelSpecs = new OntModelSpec(OntModelSpec.OWL_MEM);
		ontModelSpecs.setDocumentManager(library.getDocumentManager());
		ontModel = ModelFactory.createOntologyModel(ontModelSpecs);
		ontModel.read("http://inova8.com/people/void");

		ontModel.read("http://inova8.com/void/book-void");

		try {
			ontModel.read("http://inova8.com/peopleall");
		} catch (HttpException e) {
			assertEquals(e.getMessage(), "404 - Not Found");
		}

	}


}
