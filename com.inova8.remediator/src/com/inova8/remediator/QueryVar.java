package com.inova8.remediator;

import java.util.HashMap;
import java.util.HashSet;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.Var;

class QueryVar  implements Comparable<QueryVar>{

	private Var var;
	private Boolean optional;
	private QueryClauses queryClauses = new QueryClauses();
	private HashSet<RDFNode> variableClasses = new HashSet<RDFNode>();
	private Boolean linked = false;
	private HashMap<DatasetQueryVarLinkset, LinksetOpServiceDataset> datasetQueryVarLinksets = new HashMap<DatasetQueryVarLinkset, LinksetOpServiceDataset>();

	private static HashSet<String> literalClasses = new HashSet<String>() {
		private static final long serialVersionUID = 1L;
		{
			add("http://www.w3.org/2000/01/rdf-schema#Literal");
			add("http://www.w3.org/2000/01/rdf-schema#string");
			add("http://www.w3.org/2000/01/rdf-schema#boolean");
			add("http://www.w3.org/2000/01/rdf-schema#float");
			add("http://www.w3.org/2000/01/rdf-schema#double");
			add("http://www.w3.org/2000/01/rdf-schema#decimal");
			add("http://www.w3.org/2000/01/rdf-schema#duration");
			add("http://www.w3.org/2000/01/rdf-schema#dateTime");
			add("http://www.w3.org/2000/01/rdf-schema#time");
			add("http://www.w3.org/2000/01/rdf-schema#date");
			add("http://www.w3.org/2000/01/rdf-schema#gYearMonth");
			add("http://www.w3.org/2000/01/rdf-schema#gYear");
			add("http://www.w3.org/2000/01/rdf-schema#gMonthDay");
			add("http://www.w3.org/2000/01/rdf-schema#gDay");
			add("http://www.w3.org/2000/01/rdf-schema#gMonth");
			add("http://www.w3.org/2000/01/rdf-schema#hexBinary");
			add("http://www.w3.org/2000/01/rdf-schema#base64Binary");
			add("http://www.w3.org/2000/01/rdf-schema#anyURI");
			add("http://www.w3.org/2000/01/rdf-schema#QName");
			add("http://www.w3.org/2000/01/rdf-schema#NOTATION");
			add("http://www.w3.org/2000/01/rdf-schema#normalizedString");
			add("http://www.w3.org/2000/01/rdf-schema#token");
			add("http://www.w3.org/2000/01/rdf-schema#language");
			add("http://www.w3.org/2000/01/rdf-schema#IDREFS");
			add("http://www.w3.org/2000/01/rdf-schema#ENTITIES");
			add("http://www.w3.org/2000/01/rdf-schema#NMTOKEN");
			add("http://www.w3.org/2000/01/rdf-schema#NMTOKENS");
			add("http://www.w3.org/2000/01/rdf-schema#Name");
			add("http://www.w3.org/2000/01/rdf-schema#NCName");
			add("http://www.w3.org/2000/01/rdf-schema#ID");
			add("http://www.w3.org/2000/01/rdf-schema#IDREF");
			add("http://www.w3.org/2000/01/rdf-schema#ENTITY");
			add("http://www.w3.org/2000/01/rdf-schema#integer");
			add("http://www.w3.org/2000/01/rdf-schema#nonPositiveInteger");
			add("http://www.w3.org/2000/01/rdf-schema#negativeInteger");
			add("http://www.w3.org/2000/01/rdf-schema#long");
			add("http://www.w3.org/2000/01/rdf-schema#int");
			add("http://www.w3.org/2000/01/rdf-schema#short");
			add("http://www.w3.org/2000/01/rdf-schema#byte");
			add("http://www.w3.org/2000/01/rdf-schema#nonNegativeInteger");
			add("http://www.w3.org/2000/01/rdf-schema#unsignedLong");
			add("http://www.w3.org/2000/01/rdf-schema#unsignedInt");
			add("http://www.w3.org/2000/01/rdf-schema#unsignedShort");
			add("http://www.w3.org/2000/01/rdf-schema#unsignedByte");
			add("http://www.w3.org/2000/01/rdf-schema#positiveInteger");
		}
	};

	public HashMap<DatasetQueryVarLinkset, LinksetOpServiceDataset> getDatasetQueryVars() {
		return datasetQueryVarLinksets;
	}

	QueryVar(Var var, Boolean optional) {
		this.var = var;
		this.optional = optional;
	}

	String getLinkedName(Dataset dataset) {
		if (this.isLinked() || this.isRDFSLiteral()) {
			return dataset.getPrefix().concat(this.getName());
		} else {
			return this.getName();
		}
	}

	Var getLinkedVar(Dataset dataset) {
		return Var.alloc(this.getLinkedName(dataset));
	}

	public Var getVar() {
		return var;
	}

	public void setVar(Var var) {
		this.var = var;
	}

	public Boolean getOptional() {
		return optional;
	}

	public void setOptional(Boolean optional) {
		this.optional = optional;
	}

	public QueryClauses getClauses() {
		;
		return queryClauses;
	}

	public HashSet<RDFNode> getVariableClasses() {
		return variableClasses;
	}

	void addVariableClass(RDFNode variableClass) {
		variableClasses.add(variableClass);
	}

	private Boolean isRDFSLiteral() {
		for (RDFNode rdfNode : variableClasses) {
			if (literalClasses.contains(rdfNode.toString())) {
				return true;
			}
		}
		return false;
	}

	int clauseCount(QueryClauses resolvedClauses, QueryVars resolvedVariables) {
		return (difference(queryClauses, resolvedClauses).size());
	}

	double clauseCost(QueryClauses resolvedClauses, QueryVars resolvedVariables, QueryVars queryVars) {
		QueryClauses remainingClauses = difference(queryClauses, resolvedClauses);
		Double clauseCost = 1.0;
		for (QueryClause remainingClause : remainingClauses) {
			clauseCost *= remainingClause.cost(resolvedVariables, queryVars, queryVars);
		}
		return clauseCost;
	}

	private QueryClauses difference(QueryClauses set1, QueryClauses set2) {
		QueryClauses symmetricDiff = new QueryClauses(set1);
		symmetricDiff.addAll(set2);
		// symmetricDiff now contains the union
		QueryClauses tmp = new QueryClauses(set1);
		tmp.retainAll(set2);
		// tmp now contains the intersection
		symmetricDiff.removeAll(tmp);
		return symmetricDiff;
	}

	public String getName() {
		return var.getName();
	}

	void addClause(QueryClause queryClause) {
		queryClauses.add(queryClause);
	}

	void setLinked() {
		linked = true;
	}

	Boolean isLinked() {
		return linked;
	}

	void addDatasetQueryVar(DatasetQueryVarLinkset datasetQueryVarLinkset, LinksetOpService linksetOpService,
			DatasetQueryVarLinkset otherDatasetQueryVar) {
		datasetQueryVarLinksets.put(datasetQueryVarLinkset, new LinksetOpServiceDataset(linksetOpService, otherDatasetQueryVar));
	}

	@Override
	public String toString() {
		return "Var:"+var.getName();
	}

	@Override
	public int compareTo(QueryVar o) {
		return this.toString().compareTo(o.toString());
	}
}
