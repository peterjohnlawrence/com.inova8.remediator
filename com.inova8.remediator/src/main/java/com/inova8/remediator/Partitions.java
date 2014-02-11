package com.inova8.remediator;

import java.util.ArrayList;
import java.util.HashMap;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class Partitions {
	protected OntModel model;
	private HashMap<String, Partition> partitions = new HashMap<String, Partition>();
	public Partitions(Void  voidInstance) {
		this.model = voidInstance.getVoidModel();
	}

	boolean addClassPartition(OntResource clazz, Integer entities) {
		return partitions
				.put(clazz.getURI(), new ClassPartition(clazz, entities)) != null;
	}

	boolean addPartition(Partition partition) {
		return partitions.put(partition.toString(), partition) != null;
	}

	boolean addPropertyPartition(OntResource property, Integer triples) {
		return partitions.put(property.getURI(), new PropertyPartition( property,
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
	public void addStatisticsToModel(OntModel statisticsModel, Resource datasetStatistics) {
		// <dataset>
		// void:classPartition [ void:class <class>; void:entities <entities>];
		// void:classPartition [ void:class <class>; void:entities <entities>];
		// .
		
		//Remove existing partition information
		ArrayList<Resource> cpo = new ArrayList<Resource>();
		StmtIterator listClassPartitions = datasetStatistics
				.listProperties(Void.classPartition);
		while (listClassPartitions.hasNext()) {
			Statement cp =  listClassPartitions.next();
			cpo.add(cp.getObject().asResource());
		}
		for(Resource cp:cpo) {
			cp.removeProperties();
		}

		datasetStatistics.removeAll(Void.classPartition);
		
		StmtIterator listPropertyPartitions = datasetStatistics
				.listProperties(Void.propertyPartition);
		while ( listPropertyPartitions.hasNext()) {
			Statement cp =  listPropertyPartitions.next();
			cpo.add(cp.getObject().asResource());
		}
		for(Resource cp:cpo) {
			cp.removeProperties();
		}
		datasetStatistics.removeAll(Void.propertyPartition);
		
		//Add new  partition information
		for (Partition partition : partitions.values()) {
			OntResource bNode = statisticsModel.createOntResource(null);
			if (partition instanceof PropertyPartition) {
				bNode.addProperty(Void.property,
						((PropertyPartition) partition).getProperty());
				if(((PropertyPartition) partition).getTriples()!=null) bNode.addLiteral(Void.triples,
						((PropertyPartition) partition).getTriples());
				datasetStatistics.addProperty(Void.propertyPartition, bNode);
			} else if (partition instanceof ClassPartition) {
				bNode.addProperty(Void.class_,
						((ClassPartition) partition).getClazz());
				if(((ClassPartition) partition).getEntities()!=null) bNode.addLiteral(Void.entities,
						((ClassPartition) partition).getEntities());
				datasetStatistics.addProperty(Void.classPartition, bNode);
			}
		}
	}
}
