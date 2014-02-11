package com.inova8.remediator;

import com.hp.hpl.jena.ontology.OntResource;

public class PropertyPartition extends Partition {
	private Integer triples;

	public PropertyPartition( OntResource property, Integer triples) {
		super( property);
		this.triples = triples;
	}

	public OntResource getProperty() {
		return partition;
	}

	public Integer getTriples() {
		return triples;
	}

	public void setProperty(OntResource property) {
		this.partition = property;
	}

	public void setTriples(Integer triples) {
		this.triples = triples;
	}
	

	@Override
	public String toString() {
		return this.partition.toString();
	}
}
