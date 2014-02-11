package com.inova8.remediator;

import com.hp.hpl.jena.ontology.OntResource;

abstract class Partition {
	protected OntResource partition;

	Partition(OntResource partition ) {
		super();

		this.partition = partition;
	}
	
}
