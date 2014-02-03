package com.inova8.remediator;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;

public abstract class Partition {
	protected OntResource partition;
	protected OntModel model;
	public Partition(OntModel model, OntResource partition ) {
		super();
		this.model = model;
		this.partition = partition;
	}
	
}
