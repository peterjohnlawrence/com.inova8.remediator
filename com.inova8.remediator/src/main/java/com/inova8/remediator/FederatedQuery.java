package com.inova8.remediator;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpVars;
import com.hp.hpl.jena.sparql.algebra.Transformer;

public class FederatedQuery {
	private String queryString;

	private Query query;
	private Op operations;
	private Op simplifiedOperations;


	private QueryVars queryVars;

	private Transform transform;


	public FederatedQuery(String queryString) {
		super();
		this.queryString = queryString;
		this.query = QueryFactory.create(this.queryString);
		this.operations = Algebra.compile(this.query);
		queryVars = new QueryVars(
				OpVars.mentionedVars(this.operations));
		Simplifier  simplifier = new Simplifier();
		this.simplifiedOperations = Transformer.transform(simplifier, this.operations);
	}

	public Op getOperations() {
		return operations;
	}
	public Op getSimplifiedOperations() {
		return simplifiedOperations;
	}
	public Query getQuery() {
		return query;
	}

	public QueryPlan getQueryPlan() {
		return transform.getQueryPlan();
	}

	public String getQueryString() {
		return queryString;
	}

	public QueryVars getVariableList() {
		return queryVars;
	}

	public Op rewrite(Void voidModel, Boolean optimize) {


		
		transform = new Transform(queryVars,	voidModel, optimize);
		
		Op op = Transformer.transform(transform, this.simplifiedOperations);

		return op;

	}



}
