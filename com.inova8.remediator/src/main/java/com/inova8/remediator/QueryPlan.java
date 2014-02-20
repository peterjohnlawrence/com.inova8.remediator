package com.inova8.remediator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.hp.hpl.jena.sparql.algebra.op.OpSequence;

class QueryPlan extends ArrayList<MergedJoin>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8712280733551958541L;

	public QueryPlan(QueryVars queryVars, Boolean optimize) {
		super();
		
		/**
		 * 1. Sort the required variables according to the number of queries within which they appear. 
		 * 2. Select the queries associated with the variable with the fewest number of queries. 
		 * 3. Create a merge join between the queries within which the chosen variable appears. 
		 * 4. Adjust the sorting of the remaining variables by removing the queries that
		 * have just been added to the query plan. 
		 * 5. Repeat 2-4 until all variables have been resolved.
		 */



			QueryVars resolvedQueryVars = new QueryVars();
			QueryClauses resolvedClauses = new QueryClauses();
			HashSet<QueryVar> lowestCostQueryVars;
			// Keep on iterating through list of query variables until none left to
			// resolve.
			while (resolvedQueryVars.size() < queryVars.size()) {
				// Find the variable(s) involved in the lowest cost/count of clauses
				Double lowestCost = null;
				lowestCostQueryVars = new HashSet<QueryVar>();
				for (QueryVar queryVar : queryVars) {
					if (!resolvedQueryVars.contains(queryVar)) {
						Double variableCost;
						if (optimize) {
							variableCost = queryVar.clauseCost(resolvedClauses, resolvedQueryVars, queryVars);
						} else {
							variableCost = (double) (queryVar.clauseCount(resolvedClauses, resolvedQueryVars));
						}
						if (lowestCost == null) {
							// First pass so initialize lowestCost
							lowestCost = variableCost;
							lowestCostQueryVars = new HashSet<QueryVar>();
							lowestCostQueryVars.add(queryVar);
						} else if (variableCost == null) {
							// Variable has been implicitly eliminated by selection
							// of all its clauses when selecting other variables
							resolvedQueryVars.add(queryVar);
						} else {
							if (variableCost < lowestCost) {
								// New lowest candidate found so replace current
								// selection
								lowestCost = variableCost;
								lowestCostQueryVars = new HashSet<QueryVar>();
								lowestCostQueryVars.add(queryVar);
							} else if (variableCost.equals(lowestCost)) {
								// Exactly matches candidate so add to the list
								lowestCostQueryVars.add(queryVar);
							} else {
								// do nothing
							}
						}
					}
				}
				MergedJoin mergedJoin = new MergedJoin();
				for (QueryVar queryVar : lowestCostQueryVars) {
					for (QueryClause queryClause : queryVar.getClauses()) {
						// Add any unresolved clauses to merged join
						if (!resolvedClauses.contains(queryClause) && !mergedJoin.contains(queryClause))
							mergedJoin.add(queryClause);

					}
					mergedJoin.add(queryVar);
					// Add all resolved clauses so they are not searched again
					resolvedClauses.addAll(queryVar.getClauses());
				}
				mergedJoin.setOptimizerCost(lowestCost);
				// Add mergedJoin to query plan if there is anything to add
				if (mergedJoin.size() > 0)
					this.add(mergedJoin);
				// Add variables to resolved list
				resolvedQueryVars.addAll(lowestCostQueryVars);
			}
		}

	public QueryPlan(Collection<? extends MergedJoin> c) {
		super(c);
	}

	public QueryPlan(int initialCapacity) {
		super(initialCapacity);
	}


	@Override
	public String toString() {
		String output ="QueryPlan [";
		int loop =0;
		for (MergedJoin mergedJoin: this)
		{
			loop++;
			output += "\nStep=" + loop + "\n";
			output +=mergedJoin.toString();
		}	
		
		return output + "\n]";
	}

	public OpSequence createOpSequence(
			QueryVars queryVars,
			DatasetQueryVarLinksets initialLinksetOpServices, RemediatorQuery remediatorQuery) {
		OpSequence opSequence = OpSequence.create();	
	
		DatasetQueryVarLinksets pendingLinksetOpServices = new DatasetQueryVarLinksets();

		for (MergedJoin mergedJoin : this) {
			mergedJoin.createOpSequence(opSequence,  pendingLinksetOpServices,queryVars, initialLinksetOpServices, remediatorQuery);
		}
		return opSequence;
	}
	
}
