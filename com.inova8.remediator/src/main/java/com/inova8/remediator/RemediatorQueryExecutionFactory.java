package com.inova8.remediator;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.inova8.sparql.engine.QueryEngineRemediator;

public class RemediatorQueryExecutionFactory {

	private RemediatorQueryExecutionFactory() {
	}

	public static QueryExecution create(
			RemediatorFederatedQuery remediatorFederatedQuery) {
		OntModel model =  ModelFactory.createOntologyModel(); 
		QueryEngineRemediator.register();
		return QueryExecutionFactory.create(remediatorFederatedQuery
				.getRewrittenQuery(),model);
	}

	public static QueryExecution create(RemediatorQuery remediatorQuery,
			Void voidModel, Boolean optimize) {
		RemediatorFederatedQuery remediatorFederatedQuery = new RemediatorFederatedQuery(
				remediatorQuery, voidModel, optimize);
		OntModel model =  ModelFactory.createOntologyModel(); 
		QueryEngineRemediator.register();
		return QueryExecutionFactory.create(remediatorFederatedQuery
				.getRewrittenQuery(),model);
	}

	public static QueryExecution create(String queryString, Void voidModel,
			Boolean optimize) {
		RemediatorQuery remediatorQuery = new RemediatorQuery(queryString);
		RemediatorFederatedQuery remediatorFederatedQuery = new RemediatorFederatedQuery(
				remediatorQuery, voidModel, optimize);
		OntModel model =  ModelFactory.createOntologyModel(); 
		QueryEngineRemediator.register();
		return QueryExecutionFactory.create(remediatorFederatedQuery
				.getRewrittenQuery(),model);
	}

	public static QueryExecution create(String rewrittenQueryString) {
		QueryEngineRemediator.register();
		OntModel model =  ModelFactory.createOntologyModel(); 
		return QueryExecutionFactory.create(rewrittenQueryString,model);
	}
}
