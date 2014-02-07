package com.inova8.remediator;

public class MergedJoin {
	private QueryClauses queryClauses = new QueryClauses();
	private QueryVars queryVars = new QueryVars();
	private Double optimizerCost;
	private Integer optimizerCount;

	public MergedJoin() {
		super();
		// TODO Auto-generated constructor stub
	}

	public QueryClauses getQueryClauses() {
		return queryClauses;
	}

	public void setQueryClauses(QueryClauses queryClauses) {
		this.queryClauses = queryClauses;
	}

	public QueryVars getQueryVariables() {
		return queryVars;
	}

	public void setQueryVariables(QueryVars queryVars) {
		this.queryVars = queryVars;
	}

	public boolean contains(QueryClause queryClause) {
		return queryClauses.contains(queryClause);
	}

	public void add(QueryClause queryClause) {
		queryClauses.add(queryClause);
	}
	public void add(QueryVar queryVar) {
		queryVars.add(queryVar);
	}
	public int size() {
		return queryClauses.size();
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

	@Override
	public String toString() {
		return "MergedJoin [ optimizerCost="
				+ optimizerCost + "\n, queryVars=" + queryVars + "\n, queryClauses=" + queryClauses +  "]";
	}


}
