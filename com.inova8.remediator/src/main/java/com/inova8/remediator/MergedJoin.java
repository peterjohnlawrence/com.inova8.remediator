package com.inova8.remediator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.op.OpSequence;
import com.hp.hpl.jena.sparql.algebra.op.OpUnion;

public class MergedJoin {
	private TreeMap<Dataset,QueryClauses> datasetQueryClauses = new TreeMap<Dataset,QueryClauses>();
	//TODO private QueryClauses queryClauses = new QueryClauses();
	private QueryVars queryVars = new QueryVars();
	private Double optimizerCost;
	private Integer optimizerCount;

	public MergedJoin() {
		super();
	}

	public void add(QueryClause queryClause) {
		QueryClauses dsQueryClauses;
		if(!datasetQueryClauses.containsKey(queryClause.getDataset())){
			dsQueryClauses =  datasetQueryClauses.put(queryClause.getDataset(),new QueryClauses() );
		}
		dsQueryClauses = datasetQueryClauses.get(queryClause.getDataset());
		dsQueryClauses.add(queryClause);

	}

	public void add(QueryVar queryVar) {
		queryVars.add(queryVar);
	}

	public boolean contains(QueryClause queryClause) {
		for(QueryClauses dsQueryClauses:datasetQueryClauses.values() ){
			if (dsQueryClauses.contains(queryClause)) return true;
		}
		return false;
		//TODO return queryClauses.contains(queryClause);
	}
	public void createOpSequence(OpSequence opSequence,
			DatasetQueryVarLinksets pendingLinksetOpServices,
			QueryVars globalQueryVars,
			DatasetQueryVarLinksets initialLinksetOpServices, RemediatorQuery remediatorQuery) {
		this.prependLinkQueries(opSequence,  pendingLinksetOpServices,globalQueryVars);
		//this.insertMergedJoin(opSequence);
		this.createMergedJoin(opSequence, remediatorQuery,globalQueryVars);
		this.appendLinkQueries(opSequence,  pendingLinksetOpServices, initialLinksetOpServices,globalQueryVars);
	}
	
	private void prependLinkQueries(OpSequence opSequence,
			DatasetQueryVarLinksets pendingLinksetOpServices, QueryVars queryVars) {
		for (QueryClause queryClause : this.getQueryClauses()) {
			for (QueryVar queryVar : queryClause.getClauseVariables(queryVars)) {
				if (queryVar.isLinked()) {
					for (DatasetQueryVarLinkset datasetQueryVarLinkset : queryVar.getDatasetQueryVars().keySet()) {
						if (pendingLinksetOpServices.contains(datasetQueryVarLinkset)
								&& datasetQueryVarLinkset.getDataset() == queryClause.getDataset()) {
							opSequence.add(queryVar.getDatasetQueryVars().get(datasetQueryVarLinkset).getLinksetOpService()
									.getOpService());
							DatasetQueryVarLinkset otherDatasetQueryVarLinkset = queryVar.getDatasetQueryVars().get(datasetQueryVarLinkset)
									.getOtherDatasetQueryVarLinkset();
							pendingLinksetOpServices.remove(datasetQueryVarLinkset);
							pendingLinksetOpServices.remove(otherDatasetQueryVarLinkset);
						}
					}
				}
			}
		}
	}

	private void createMergedJoin(OpSequence opSequence, RemediatorQuery remediatorQuery, QueryVars globalQueryVars) {
		Iterator<Entry<Dataset, QueryClauses>> datasetQueryClauseIterator = datasetQueryClauses.entrySet().iterator();
		Op priorOp;
		Dataset priorDataset;
		if (datasetQueryClauses.size() > 1) {
			priorDataset = datasetQueryClauseIterator.next().getKey();
			priorOp = priorDataset.getMergedJoin(datasetQueryClauses.get(priorDataset),remediatorQuery,globalQueryVars);
			OpUnion priorOpUnion = null;
			Dataset nextDataset;
			while (datasetQueryClauseIterator.hasNext()) {
				nextDataset = datasetQueryClauseIterator.next().getKey();
				priorOpUnion = new OpUnion(priorOp, nextDataset.getMergedJoin(datasetQueryClauses.get(nextDataset),remediatorQuery,globalQueryVars));
				priorOp = priorOpUnion;
			}
		} else {
			priorDataset = datasetQueryClauseIterator.next().getKey();
			//priorOp  =insertDatasetMergedJoin(priorDataset,datasetQueryClauses.get(priorDataset));
			priorOp  =priorDataset.getMergedJoin(datasetQueryClauses.get(priorDataset),remediatorQuery,globalQueryVars);
			}
		opSequence.add(priorOp);
	}

	private void appendLinkQueries(OpSequence opSequence, 
			DatasetQueryVarLinksets pendingLinksetOpServices,
			DatasetQueryVarLinksets initialLinksetOpServices, QueryVars queryVars) {
		for (QueryClause queryClause : this.getQueryClauses()) {
			for (QueryVar queryVar : queryClause.getClauseVariables(queryVars)) {
				if (queryVar.isLinked()) {
					for (DatasetQueryVarLinkset datasetQueryVarLinkset : queryVar.getDatasetQueryVars().keySet()) {
						if (initialLinksetOpServices.contains(datasetQueryVarLinkset)
								&& datasetQueryVarLinkset.getDataset() == queryClause.getDataset()) {
							DatasetQueryVarLinkset otherDatasetQueryVarLinkset = queryVar.getDatasetQueryVars().get(datasetQueryVarLinkset)
									.getOtherDatasetQueryVarLinkset();
							pendingLinksetOpServices.add(otherDatasetQueryVarLinkset);
							initialLinksetOpServices.remove(datasetQueryVarLinkset);
							initialLinksetOpServices.remove(otherDatasetQueryVarLinkset);
						}
					}
				}
			}
		}
	}	
	public Double getOptimizerCost() {
		return optimizerCost;
	}

	public int getOptimizerCount() {
		return optimizerCount;
	}

	public QueryClauses getQueryClauses() {
		QueryClauses tempQueryClauses = new QueryClauses();
		for(QueryClauses dsQueryClauses:datasetQueryClauses.values() ){
			tempQueryClauses.addAll(dsQueryClauses);
		}
		return tempQueryClauses;
		//TODO return queryClauses;
	}
	public QueryVars getQueryVariables() {
		return queryVars;
	}
	public void setOptimizerCost(Double optimizerCost) {
		this.optimizerCost = optimizerCost;
	}

	public void setOptimizerCount(int optimizerCount) {
		this.optimizerCount = optimizerCount;
	}

	public void setQueryVariables(QueryVars queryVars) {
		this.queryVars = queryVars;
	}

	public int size() {
		int size=0;
		for(QueryClauses dsQueryClauses:datasetQueryClauses.values() ){
			size+= dsQueryClauses.size();
		}
		return size;
		//TODO return queryClauses.size();
	}

	@Override
	public String toString() {
		return "MergedJoin [ optimizerCost="
				+ optimizerCost + "\n,\tqueryVars=" + queryVars + "\n,\tdatasetQueries [\n" + datasetQueryClausesToString() +  "]";
	}
	
	private String datasetQueryClausesToString(){
		String returnString="";
		for(Map.Entry<Dataset,QueryClauses> entry : datasetQueryClauses.entrySet() ){
			
			returnString += "\t\tdataset=" + entry.getKey().toString() +"\n\t" + entry.getValue().toString();
		}	
		return returnString;
	}

	protected TreeMap<Dataset,QueryClauses> getDatasetQueryClauses() {
		return datasetQueryClauses;
	}




}
