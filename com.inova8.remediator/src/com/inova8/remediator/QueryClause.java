package com.inova8.remediator;

import org.apache.xerces.util.URI;
import org.apache.xerces.util.URI.MalformedURIException;
import org.oxford.comlab.requiem.rewriter.Clause;
import org.oxford.comlab.requiem.rewriter.FunctionalTerm;
import org.oxford.comlab.requiem.rewriter.Term;
import org.oxford.comlab.requiem.rewriter.Variable;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.algebra.op.OpService;

public class QueryClause extends Clause implements Comparable<QueryClause>{

	private Dataset dataset;
	private Double optimizerCost;
	private int optimizerCount;
	private QueryVar optimizerVar;
	private OpService opService;

	public QueryClause(Clause clause, Dataset dataset) {
		super(clause.getBody(), clause.getHead());
		this.dataset = dataset;
	}
	
	public OntModel getDataModel(QueryVars queryVars){

		OntModel dataModel = ModelFactory.createOntologyModel();

		for(Term term: super.getBody()){
			dataModel.add(dataModel.asStatement(queryClauseToTriple(term,queryVars)));
		}
		return dataModel;
	}

	private Triple queryClauseToTriple(Term term, QueryVars queryVars) {
		if (term.getArity() == 1) {
			Node clas = NodeFactory.createURI(term.getName());
			Node object = termToNode(term.getArgument(0),queryVars);
			return Triple
					.create(object,
							NodeFactory
									.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
							clas);
		} else if (term.getArity() == 2) {
			Node pred = NodeFactory.createURI(term.getName());
			Node subj = termToNode( term.getArgument(0),queryVars);
			Node obj = termToNode( term.getArgument(1),queryVars);
			return Triple.create(subj, pred, obj);
		} else {
			return null;
		}
	}
	
	private Node termToNode( Term term, QueryVars queryVars) {
		if (term instanceof FunctionalTerm) {
			try {
				URI uri = new URI(term.getName());
				return NodeFactory.createURI(uri.toString());
			} catch (MalformedURIException e) {
				return NodeFactory.createLiteral(term.toString());
			}
		} else if (term instanceof Variable) {		
			return NodeFactory.createURI("http://inova8.com/variable#" + queryVars.get(term.getMinVariableIndex()).getName());
		} else {
			return null;
		}
	}
	public Double cost(QueryVars resolvedQueryVariables, QueryVars queryVars, QueryVars queryVariables2) {
		Double selectivityTerm = null;
		QueryVars unboundQueryVariables = new QueryVars();
		for (Term bodyTerm : this.getBody()) {
			if (bodyTerm.getArity() == 1) {
				Double selectivityClass = selectivityClass(bodyTerm.getName());
				Double selectivityInstance = selectivityInstance(
						bodyTerm.getArgument(0), resolvedQueryVariables,unboundQueryVariables,queryVars);
				selectivityTerm = dataset.getTriples()*selectivityInstance * selectivityClass;
			} else if (bodyTerm.getArity() == 2) {
				Double selectivitySubject = selectivitySubject(
						bodyTerm.getArgument(0), resolvedQueryVariables,unboundQueryVariables,queryVars);
				Double selectivityPredicate = selectivityPredicate(bodyTerm
						.getName());
				Double selectivityObject = selectivityObject(
						bodyTerm.getArgument(1), resolvedQueryVariables,unboundQueryVariables,queryVars);
				selectivityTerm = dataset.getTriples()*selectivitySubject * selectivityPredicate
						* selectivityObject;
			} else {
				return null;
			}
		}
		return selectivityTerm;
	}
	private Double selectivityClass(String className) {
		return 1.0 / dataset.getPartitions().getEntities(className);
	}

	private Double selectivityInstance(Term instanceTerm,
			QueryVars resolvedQueryVariables, QueryVars  unboundQueryVariables, QueryVars queryVars) {
		if (instanceTerm instanceof FunctionalTerm) {
			return 1.0 / (double)(dataset.getEntities());
		} else if (instanceTerm instanceof Variable) {
			// If unbound then selectivity 1.0
			// If variable already resolved then treat it as if bound
			if (resolvedQueryVariables.contains(queryVars.get(instanceTerm
					.getMinVariableIndex()))) {
				return 1.0/(double)(dataset.getEntities());
			} else {
				return 1.0;
			}
		}
		return null;
	}


	private Double selectivitySubject(Term subjectTerm,
			QueryVars resolvedQueryVariables, QueryVars  unboundQueryVariables, QueryVars queryVars) {
		Double selectivitySubject = 1.0 / (double)(dataset.getDistinctSubjects());
		if (subjectTerm instanceof FunctionalTerm) {
			return selectivitySubject;
		} else if (subjectTerm instanceof Variable) {
			// If unbound then selectivity 1.0
			// If variable already resolved then treat it as if bound
			if (resolvedQueryVariables.contains(subjectTerm
					.getMinVariableIndex())) {
				return selectivitySubject;
			} else {
				// If variable already resolved in another term then treat it as if bound
				if(unboundQueryVariables.contains(subjectTerm
					.getMinVariableIndex())){
					return selectivitySubject;
				}else{
					unboundQueryVariables.add(queryVars.get(subjectTerm
					.getMinVariableIndex()));
					return 1.0;
				}

			}
		}
		return null;
	}

	private Double selectivityPredicate(String predicateName) {
		Integer numberPredicateTriples = dataset.getPartitions().getTriples(predicateName);
		if (numberPredicateTriples == null) {
			return (Double) null;
		} else {
			Double selectivity = (double) numberPredicateTriples / (double) (dataset.getTriples());
			return selectivity;
		}
	}

	private Double selectivityObject(Term objectTerm,
			QueryVars resolvedQueryVariables, QueryVars  unboundQueryVariables, QueryVars queryVars) {
		
		Double selectivityObject = dataset.getEntities()*dataset.getProperties()/(double)(dataset.getTriples());
		if (objectTerm instanceof FunctionalTerm) {
			return selectivityObject;
		} else if (objectTerm instanceof Variable) {
			// If unbound then selectivity 1.0
			if (resolvedQueryVariables.contains(objectTerm
					.getMinVariableIndex())) {
				return selectivityObject;
			} else {
				// If variable already resolved in another term then treat it as if bound
				if(unboundQueryVariables.contains(objectTerm
					.getMinVariableIndex())){
					return selectivityObject;
				}else{
					unboundQueryVariables.add(queryVars.get(objectTerm
					.getMinVariableIndex()));
					return 1.0;
				}
			}
		}
		return null;
	}

	public Double getOptimizerCost() {
		return optimizerCost;
	}

	public void setOptimizerCost(Double optimizerCost) {
		this.optimizerCost = optimizerCost;
	}

	public int getOptimizerCount() {
		return optimizerCount;
	}

	public void setOptimizerCount(int optimizerCount) {
		this.optimizerCount = optimizerCount;
	}

	public QueryVar getOptimizerVariable() {
		return optimizerVar;
	}

	public void setOptimizerVariable(QueryVar optimizerVariable) {
		this.optimizerVar = optimizerVariable;
	}

	public Dataset getDataset() {
		return dataset;
	}

	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}
	public QueryVars getClauseVariables(QueryVars queryVars)
	{
		QueryVars clauseVariables=new  QueryVars();
		for ( Term term:super.getHead().getArguments() ){
			clauseVariables.add(queryVars.get(term.getMinVariableIndex()));
		}
		return clauseVariables;
	}

	public OpService getOpService() {
		return opService;
	}

	public void setOpService(OpService opService) {
		this.opService = opService;
	}



	@Override
	public String toString() {
		return "QueryClause [dataset=" + dataset + ", clause=" + super.toString() + "]";
	}

	@Override
	public int compareTo(QueryClause arg0) {
	        return this.toString().compareTo(arg0.toString());
	}
}
