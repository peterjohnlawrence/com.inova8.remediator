package com.inova8.remediator;

import java.util.HashMap;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class Partitions {
	protected OntModel model;
	private HashMap<String, Partition> partitions = new HashMap<String, Partition>();

	public Partitions(OntModel model) {
		this.model = model;
	}

	boolean addClassPartition(OntResource clazz, Integer entities) {
		return partitions
				.put(clazz.getURI(), new ClassPartition(model, clazz, entities)) != null;
	}

	boolean addPartition(Partition partition) {
		return partitions.put(partition.toString(), partition) != null;
	}

	boolean addPropertyPartition(OntResource property, Integer triples) {
		return partitions.put(property.getURI(), new PropertyPartition(model, property,
				triples)) != null;
	}

	public boolean contains(String string) {
		boolean test = partitions.containsKey(string);
		return test;
	}
	public Integer getEntities(String className) {
		
		return ((ClassPartition) partitions.get(className)).getEntities();
	}
	public Integer getTriples(String propertyName) {
		
		return ((PropertyPartition) partitions.get(propertyName)).getTriples();
	}
	public OntModel write(OntModel ontModel, Resource dataset) {
		// <dataset>
		// void:classPartition [ void:class <class>; void:entities <entities>];
		// void:classPartition [ void:class <class>; void:entities <entities>];
		// .
		
		//Remove existing partition information
		StmtIterator listClassPartitions = dataset
				.listProperties(Void.classPartition);
		for (; listClassPartitions.hasNext();) {
			listClassPartitions.next().getObject().asResource()
					.removeProperties();
		}
		dataset.removeAll(Void.classPartition);
		StmtIterator listPropertyPartitions = dataset
				.listProperties(Void.propertyPartition);
		for (; listPropertyPartitions.hasNext();) {
			listPropertyPartitions.next().getObject().asResource()
					.removeProperties();
		}
		dataset.removeAll(Void.propertyPartition);
		
		//Add new  partition information
		for (Partition partition : partitions.values()) {
			OntResource bNode = ontModel.createOntResource(null);
			if (partition instanceof PropertyPartition) {
				bNode.addProperty(Void.property,
						((PropertyPartition) partition).getProperty());
				if(((PropertyPartition) partition).getTriples()!=null) bNode.addLiteral(Void.triples,
						((PropertyPartition) partition).getTriples());
				dataset.addProperty(Void.propertyPartition, bNode);
			} else if (partition instanceof ClassPartition) {
				bNode.addProperty(Void.class_,
						((ClassPartition) partition).getClazz());
				if(((ClassPartition) partition).getEntities()!=null) bNode.addLiteral(Void.entities,
						((ClassPartition) partition).getEntities());
				dataset.addProperty(Void.classPartition, bNode);
			}

		}
		return ontModel;
	}
}
