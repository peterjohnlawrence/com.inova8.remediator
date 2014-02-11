package com.inova8.remediator;

import org.apache.jena.atlas.logging.Log;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Resource;

public class Linkset extends Dataset {

	private OntResource linkPredicate;
	private Dataset subjectsDataset;
	private OntResource subjectsTarget;
	private OntResource subjectsClass;
	private Dataset objectsDataset;
	private OntResource objectsTarget;
	private OntResource objectsClass;
	private Dataset subset;
	//private HashMap<QueryVar,OpService> queryVarLinksetServices = new  HashMap<QueryVar,OpService>();

	private String linksetVocabularyQuery = "PREFIX void: <" + Void.getURI()
			+ ">\n"
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			+ "SELECT ?vocabulary \n" + "WHERE  {\n"
			+ "    ?linkset rdf:type void:Linkset .\n"
			+ "    ?linkset void:vocabulary ?vocabulary .\n" + "}\n";

	public Linkset(Void voidInstance, Datasets datasets, OntResource linkset,
			OntResource sparqlEndPoint, OntResource linkPredicate,
			OntResource subjectsDataset, OntResource subjectsTarget,
			OntResource subjectsClass, OntResource objectsDataset,
			OntResource objectsTarget, OntResource objectsClass) {
		super(voidInstance, linkset, sparqlEndPoint);

		this.linkPredicate = linkPredicate;
		this.subjectsDataset =  datasets.getDataset(subjectsDataset);
		this.subjectsTarget = subjectsTarget;
		this.subjectsClass = subjectsClass;
		this.objectsDataset = datasets.getDataset(objectsDataset);
		this.objectsTarget = objectsTarget;
		this.objectsClass = objectsClass;

		loadVocabularies();
	}

	public Boolean supportsInstanceMapping() {
		return (this.sparqlEndPoint != null && this.linkPredicate != null
				&& this.subjectsClass != null && this.objectsClass != null);
	}

//	public void addOpService(QueryVar queryVar, OpService opService) {
//		this.queryVarLinksetServices.put(queryVar, opService);
//	}


	public Resource getLinkPredicate() {
		return linkPredicate;
	}

	public OntResource getObjectsTarget() {
		return objectsTarget;
	}

	public OntResource getSubjectsTarget() {
		return subjectsTarget;
	}

	public Dataset getSubjectsDataset() {
		return subjectsDataset;
	}

	public OntResource getSubjectsClass() {
		return subjectsClass;
	}

	public Dataset getObjectsDataset() {
		return objectsDataset;
	}

	public OntResource getObjectsClass() {
		return objectsClass;
	}

	public Dataset getSubset() {
		return subset;
	}

	@Override
	public void loadVocabularies() {

		QuerySolutionMap binding = new QuerySolutionMap();
		binding.add("linkset", this.dataset);
		Query query = QueryFactory.create(linksetVocabularyQuery);
		QueryExecution qexec = QueryExecutionFactory.create(query, voidInstance.getVoidModel(),
				binding);

		try {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				OntResource vocabulary = soln.getResource("vocabulary").as(
						OntResource.class);
				vocabularies.add(vocabulary);
			}
		} catch (Exception e) {
			Log.debug(Linkset.class, "Failed linksetVocabularyQuery");
			Log.debug(Linkset.class, e.getStackTrace().toString());
		} finally {
			qexec.close();
		}

	}

	public void setLinkPredicate(OntResource linkPredicate) {
		this.linkPredicate = linkPredicate;
	}

	public void setObjectsTarget(OntResource objectsTarget) {
		this.objectsTarget = objectsTarget;
	}

	public void setSubjectsTarget(OntResource subjectsTarget) {
		this.subjectsTarget = subjectsTarget;
	}

	public void setSubset(Dataset subset) {
		this.subset = subset;
	}

	@Override
	public void updatePartitionStatistics() {
		// TODO complete linkset statistics
		if (sparqlEndPoint != null) {

			updatePropertyPartitionStatistics();

		}
	}

	@Override
	protected void updatePropertyPartitionStatistics() {
		// TODO linkset specific statistics
	}

	@Override
	public String toString() {
		return "Linkset:"+ super.dataset.toString() ;
	}

}
