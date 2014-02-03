package com.inova8.remediator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.inova8.remediator.Linkset;

public class LinksetTest {

	 Linkset kennedy_book;
	 Linkset kennedy_movie;
	 Linkset movie_book;
	 OntModel linksetModel;
	@Before
	public void setUp() throws Exception {
		linksetModel = ModelFactory.createOntologyModel();

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testLinkset() {
		kennedy_book = new Linkset(linksetModel, null, linksetModel.createOntResource("http://inova8.com/linking/kennedy-book" ),null,null,null, null, null, null, null, null);
		kennedy_movie = new Linkset(linksetModel, null, linksetModel.createOntResource("http://inova8.com/linking/kennedy_movie" ),null,null,null, null, null, null, null, null);
		movie_book = new Linkset(linksetModel, null, linksetModel.createOntResource("http://inova8.com/linking/movie_book" ),null,null,null, null, null, null, null, null);
	}

}
