package com.inova8.remediator;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.inova8.remediator.Dataset;
import com.inova8.remediator.Datasets;
import com.inova8.workspace.RemediatorWorkspace;

public class DatasetTest {
	Datasets datasets= new Datasets();
	Dataset kennedy ;
	Dataset book;
	Dataset movie;
	String path;
	String URL;
	String queryFolder;
	OntModelSpec voidModelSpec;
	OntModel voidModel;
	OntModelSpec vocabularyModelSpec;
	OntModel vocabularyModel;
	RemediatorWorkspace workspace;
	Void queryVoid;
	OntModel datasetModel;


	@Before
	public void setUp() throws Exception {
		path = "./test/models/";
		queryFolder = "Q1";
		workspace = new RemediatorWorkspace(new File(path + queryFolder), true);
		URL = "http://inova8.com/people/void";
		queryVoid = new Void(workspace, URL, true);

		datasetModel = queryVoid.getVoidModel();
		
		kennedy = new Dataset( queryVoid, datasetModel.createOntResource("http://rdfs.org/ns/void#kennedy" ),
				datasetModel.createOntResource("http://localhost:8083/tbl/sparql?default-graph-uri=http://inova8.com/data/kennedys"),
				datasetModel.createOntResource("http://inova8.com/data/kennedys" ), "DS2");
		kennedy.getPartitions().addPropertyPartition(
				datasetModel.createOntResource("http://inova8.com/schema/kennedys-schema#almaMater"), 1);
		kennedy.getPartitions().addPropertyPartition(
				datasetModel.createOntResource("http://inova8.com/schema/kennedys-schema#child"), 1);
		datasets.add(kennedy);

		book = new Dataset(queryVoid, datasetModel.createOntResource("http://rdfs.org/ns/void#book" ),
				datasetModel.createOntResource("http://localhost:8083/tbl/sparql?default-graph-uri=http://inova8.com/data/books"),
						datasetModel.createOntResource("http://inova8.com/data/books"), "DS3");
		book.getPartitions().addPropertyPartition(
				datasetModel.createOntResource("http://inova8.com/schema/books-schema#writtenBy"), 1);
		datasets.add(book);

		movie = new Dataset(queryVoid, datasetModel.createOntResource("http://rdfs.org/ns/void#movie" ),
				datasetModel.createOntResource("http://localhost:8083/tbl/sparql?default-graph-uri=http://inova8.com/data/movies"),
						datasetModel.createOntResource("http://inova8.com/data/movies"), "DS1");
		movie.getPartitions().addPropertyPartition(
				datasetModel.createOntResource("http://inova8.com/schema/movies-schema#playsin"), 1);
		datasets.add(movie);


	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public final void testIsRecognizedTerm() {
		assertTrue("",kennedy.isRecognizedURI("http://inova8.com/schema/kennedys-schema#almaMater"));
		assertTrue("",book.isRecognizedURI("http://inova8.com/schema/books-schema#writtenBy"));
		assertTrue("",!kennedy.isRecognizedURI("http://inova8.com/schema/books-schema#writtenBy"));

	}

}
