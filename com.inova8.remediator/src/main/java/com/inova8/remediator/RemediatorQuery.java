package com.inova8.remediator;

import java.util.HashMap;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpAsQuery;
import com.hp.hpl.jena.sparql.algebra.OpVars;
import com.hp.hpl.jena.sparql.algebra.Transformer;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;

public class RemediatorQuery {
	private String queryString;

	private Query originalQuery;
	private Op originalOperations;
	private Op simplifiedOperations;
	private HashMap<Integer,Triple> simplifiedTriples = new HashMap<Integer,Triple> ();

	private QueryVars queryVars;

	public RemediatorQuery(String queryString) {
		super();
		this.queryString = queryString;
		this.originalQuery = QueryFactory.create(this.queryString);
		this.originalOperations = Algebra.compile(this.originalQuery);
		queryVars = new QueryVars(
				OpVars.mentionedVars(this.originalOperations));
		Simplifier  simplifier = new Simplifier();
		this.simplifiedOperations = Transformer.transform(simplifier, this.originalOperations);
		//Create lookup for simplified operations
		for(Triple triple:((OpBGP)this.simplifiedOperations).getPattern().getList()){
			simplifiedTriples.put(triple.hashCode(), triple);
		}
	}

	public Op getOperations() {
		return originalOperations;
	}
	public Op getSimplifiedOperations() {
		return simplifiedOperations;
	}
	public Query getQuery() {
		return originalQuery;
	}

	public String getQueryString() {
		return queryString;
	}

	public QueryVars getVariableList() {
		return queryVars;
	}

	public HashMap<Integer,Triple> getSimplifiedTriples() {
		return simplifiedTriples;
	}





}
