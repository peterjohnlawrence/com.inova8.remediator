package com.inova8.remediator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.Var;
import com.inova8.requiem.rewriter.Term;

class QueryVars extends ArrayList<QueryVar>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	QueryVars(Collection<Var> variables) {
		super();
		for (Var var:variables){
			this.add(new QueryVar(var,false));
		}
	}

	public QueryVars() {
		super();
	}

	int indexOf(Var variable) {

		for( int index =0; index < this.size(); index++)
			{
			if(this.get(index).getVar().getName().equals(variable.getName())) return index;
		}
		return -1;
	}
	void locateDatasetClauses(ArrayList<Dataset> datasets)
	{
		for (Dataset dataset:datasets)
		{
			for(QueryClause queryClause:dataset.getQueryClauses())
			{
				for (Term argument: queryClause.getHead().getArguments())
				{
					QueryVar var = this.get(argument.getMinVariableIndex());
					var.addClause(queryClause);
					
				}
			}
		}
	}
	QueryVars locateVariablesOfClass(RDFNode variableClass)
	{
		QueryVars classQueryVariables = new QueryVars();
		
		for (QueryVar queryVar: this)
		{
			if (queryVar.getVariableClasses().contains(variableClass)) classQueryVariables.add(queryVar);
		}
		return classQueryVariables;
	}

	@Override
	public String toString() {
		Collections.sort(this);
		String output= "QueryVars [";
		for (QueryVar thisQueryVar: this)
		{
			output+= thisQueryVar.toString()+", ";
		}
		return output+ "]";
	}
	

}
