package com.inova8.remediator;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.jena.atlas.logging.Log;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class Datasets extends ArrayList<Dataset> {

	private String propertyPartitionStatisticsQuery = "PREFIX void: <" + Void.NS + ">\n"
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			+ "SELECT ?Dataset  ?PropertyPartition ?Property ?Triples WHERE\n"
			+ "		 {?Dataset void:propertyPartition ?PropertyPartition .\n"
			+ "		OPTIONAL { ?PropertyPartition void:triples ?Triples .}\n"
			+ "		?PropertyPartition void:property ?Property\n"
			+ "		} ORDER BY ?Dataset";

	private String classPartitionStatisticsQuery = "PREFIX void: <" + Void.NS + ">\n"
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			+ "SELECT ?Dataset  ?ClassPartition ?Class ?Entities WHERE\n"
			+ "		 {?Dataset void:classPartition ?ClassPartition .\n"
			+ "		OPTIONAL { ?ClassPartition void:entities ?Entities .}\n"
			+ "		?ClassPartition void:class ?Class\n"
			+ "		} ORDER BY ?Dataset";
	
	private String datasetStatisticsQuery = "PREFIX void: <" + Void.NS + ">\n"
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"	
			+ "SELECT ?Dataset  ?triples  ?entities ?classes ?properties ?distinctSubjects ?distinctObjects\n"
			+ " WHERE\n"
			+ "{\n"
			+ "?Dataset void:triples ?triples .\n"
			+ "?Dataset void:entities ?entities .\n"
			+ "?Dataset void:classes ?classes .\n"
			+ "?Dataset void:properties ?properties .\n"
			+ "?Dataset void:distinctSubjects ?distinctSubjects .\n"
			+ "?Dataset void:distinctObjects ?distinctObjects .\n"
			+ "} \n"
			+ "ORDER BY ?Dataset";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7551012751076066818L;

	public Datasets() {
		super();

	}

	public Datasets(Collection<? extends Dataset> c) {
		super(c);

	}

	public void queryPartitionStatistics() {
		for (Dataset dataset : this) {
			dataset.queryPartitionStatistics();
		}
		
	}
	public void updatePartitions() {
		for (Dataset dataset : this) {
			dataset.updatePartitions();
	
		}
	}
	public Dataset getDataset(OntResource datasetNode){
		if (datasetNode==null ) return null;
		
		for (Dataset dataset : this) {
			if(dataset.getDataset().equals(datasetNode)) return dataset;
	
		}
		return null;
	}

	public OntModel getStatisticsModel() {
	
		for (Dataset dataset : this) {
			dataset.addStatisticsToModel();
		}
		return null;
	}

	public void loadPartitionStatistics(OntModel voidModel) {
		loadDatasetStatistics(voidModel);
		loadClassPartitionStatistics(voidModel);
		loadPropertyPartitionStatistics(voidModel);
		for (Dataset dataset : this) {
			dataset.addStatisticsToModel();
		}
	}

	private void loadDatasetStatistics(OntModel voidModel) {
		Query query = QueryFactory.create(datasetStatisticsQuery);
		QueryExecution qexec = QueryExecutionFactory.create(query, voidModel);

		try {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				OntResource dataset = soln.getResource("Dataset").as(
						OntResource.class);

				Integer triples = (soln.getLiteral("triples") != null) ? soln
						.getLiteral("triples").getInt() : null;
				Integer entities = (soln.getLiteral("entities") != null) ? soln
						.getLiteral("entities").getInt() : null;
				Integer classes = (soln.getLiteral("classes") != null) ? soln
						.getLiteral("entities").getInt() : null;
				Integer properties = (soln.getLiteral("properties") != null) ? soln
						.getLiteral("properties").getInt() : null;
				Integer distinctSubjects = (soln.getLiteral("distinctSubjects") != null) ? soln
						.getLiteral("distinctSubjects").getInt() : null;
				Integer distinctObjects = (soln.getLiteral("distinctObjects") != null) ? soln
						.getLiteral("distinctObjects").getInt() : null;
				Dataset ds = this.getDataset(dataset);
				if (ds != null){
					ds.setTriples(triples);
					ds.setEntities(entities);
					ds.setClasses(classes);
					ds.setProperties(properties);
					ds.setDistinctSubjects(distinctSubjects);
					ds.setDistinctObjects(distinctObjects);
						
					
				}
			}
		} catch (Exception e) {
			Log.debug(this, "Unable to execute classPartitionStatisticsQuery "
					+ query);
		} finally {
			qexec.close();
		}
	}

	public void loadClassPartitionStatistics(OntModel voidModel) {
		Query query = QueryFactory.create(classPartitionStatisticsQuery);
		QueryExecution qexec = QueryExecutionFactory.create(query, voidModel);

		try {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				OntResource dataset = soln.getResource("Dataset").as(
						OntResource.class);
				OntResource clazz = soln.getResource("Class").as(
						OntResource.class);
				Integer entities = (soln.getLiteral("Entities") != null) ? soln
						.getLiteral("Entities").getInt() : null;	
				Dataset ds = this.getDataset(dataset);		
				if (ds!=null)ds.getPartitions().addClassPartition(clazz, entities);
			}
		} catch (Exception e) {
			Log.debug(
					this,
					"Unable to execute classPartitionStatisticsQuery " + query);
		} finally {
			qexec.close();
		}
	}
	public void loadPropertyPartitionStatistics(OntModel voidModel) {
		Query query = QueryFactory.create(propertyPartitionStatisticsQuery);
		QueryExecution qexec = QueryExecutionFactory.create(query, voidModel);

		try {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				OntResource dataset = soln.getResource("Dataset").as(
						OntResource.class);
				OntResource property = soln.getResource("Property").as(
						OntResource.class);
				Integer triples = (soln.getLiteral("Triples") != null) ? soln
						.getLiteral("Triples").getInt() : null;	
				Dataset ds = this.getDataset(dataset);		
				if (ds!=null)ds.getPartitions().addPropertyPartition(property, triples);
			}
		} catch (Exception e) {
			Log.debug(
					this,
					"Unable to execute propertyPartitionStatisticsQuery " + query);
		} finally {
			qexec.close();
		}
	}


}
