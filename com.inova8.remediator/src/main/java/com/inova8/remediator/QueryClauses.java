package com.inova8.remediator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

class QueryClauses extends TreeSet<QueryClause>{

	private static final long serialVersionUID = -4516774815436811543L;

	public QueryClauses() {
		super();
	}

	QueryClauses(Collection<? extends QueryClause> c) {
		super(c);
	}


	@Override
	public boolean add(QueryClause e) {
		
		return super.add(e);
	}

	@Override
	public String toString() {
		Set<QueryClause> thisSet = new TreeSet<QueryClause>(this);
		String output= "\tqueryClauses [\n";
		for (QueryClause queryClause: thisSet)
		{
			output+= "\t\t\t"+queryClause.toString()+"\n";
		}		
		return output + "\t\t]\n";
	}
}
