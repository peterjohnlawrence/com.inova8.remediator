package com.inova8.remediator;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.inova8.remediator.Dataset;
import com.inova8.remediator.Datasets;

public class DatasetTest {
	Datasets datasets= new Datasets();
	Dataset kennedy ;
	Dataset book;
	Dataset movie;
	@Before
	public void setUp() throws Exception {
		
		OntModel datasetModel = ModelFactory.createOntologyModel();
		
		kennedy = new Dataset( datasetModel, datasetModel.createOntResource("http://rdfs.org/ns/void#kennedy" ),
				datasetModel.createOntResource("http://localhost:8083/tbl/sparql?default-graph-uri=http://inova8.com/data/kennedys"),
				datasetModel.createOntResource("http://inova8.com/data/kennedys" ), "DS2");
		kennedy.getPartitions().addPropertyPartition(
				datasetModel.createOntResource("http://inova8.com/schema/kennedys-schema#almaMater"), 1);
		kennedy.getPartitions().addPropertyPartition(
				datasetModel.createOntResource("http://inova8.com/schema/kennedys-schema#child"), 1);
		datasets.add(kennedy);

		book = new Dataset(datasetModel, datasetModel.createOntResource("http://rdfs.org/ns/void#book" ),
				datasetModel.createOntResource("http://localhost:8083/tbl/sparql?default-graph-uri=http://inova8.com/data/books"),
						datasetModel.createOntResource("http://inova8.com/data/books"), "DS3");
		book.getPartitions().addPropertyPartition(
				datasetModel.createOntResource("http://inova8.com/schema/books-schema#writtenBy"), 1);
		datasets.add(book);

		movie = new Dataset(datasetModel, datasetModel.createOntResource("http://rdfs.org/ns/void#movie" ),
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
