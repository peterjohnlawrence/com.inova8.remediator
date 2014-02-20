package com.inova8.remediator;

import java.util.ArrayList;
import java.util.Collection;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpService;
import com.hp.hpl.jena.sparql.core.BasicPattern;

public class Linksets extends ArrayList<Linkset>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5968625127422546735L;

	public Linksets() {
		super();
	}

	public Linksets(Collection<? extends Linkset> c) {
		super(c);
	}

	public void updateStatistics() {
		for (Linkset linkset : this) {
			linkset.queryPartitionStatistics();
		}
	}
	public DatasetQueryVarLinksets createLinksetQueryClauses(QueryVars queryVars) {
		DatasetQueryVarLinksets linksetOpServices = new DatasetQueryVarLinksets ();
		for (Linkset linkset : this) {
			if (linkset.supportsInstanceMapping()) {
				QueryVars variablesOfSubjectClass = queryVars.locateVariablesOfClass(linkset.getSubjectsClass());
				for (QueryVar linkQueryVariable : variablesOfSubjectClass) {
					// TODO need to remove the need to pass through
					// queryVariables.
					QueryClauses subjectQueryClauses = linkset.getSubjectsDataset().getClauseVariables(queryVars,
							linkQueryVariable);
					// These dataset queryClause variables could be linked to
					// another dataset queryClause variable
					// So now for each we need to find the 'other' end of the
					// linkset and find any clauses in the objectsDataset that
					// share the same variable.
					// (Does it have to conform to objectsClass?)
					QueryClauses objectQueryClauses = linkset.getObjectsDataset().getClauseVariables(queryVars,
							linkQueryVariable);

					// Now we have both ends of the link that we can create
					// between clause variables, as long as not the same
					// dataset
					// subjectQueryClauses/subjectQueryVariable sameAs
					// objectQueryClauses/objectQueryVariable

					// Generate the SPARQL algebra
					for (QueryClause subjectQueryClause : subjectQueryClauses) {
						for (QueryClause objectQueryClause : objectQueryClauses) {
							linkQueryVariable.setLinked();
							Node linksetNode = NodeFactory.createURI(linkset.getSparqlEndPoint().toString());
							BasicPattern pattern = new BasicPattern();

							Node pred = NodeFactory.createURI(linkset.getLinkPredicate().toString());
							pattern.add(Triple.create(linkQueryVariable.getLinkedVar(linkset.getSubjectsDataset()), pred,linkQueryVariable.getLinkedVar(linkset.getObjectsDataset())));

							DatasetQueryVarLinkset subjectDatasetQueryVarLinkset = linkset.getSubjectsDataset().getDatasetQueryVarLinkset(
									linkQueryVariable,linkset);
							DatasetQueryVarLinkset objectDatasetQueryVarLinkset = linkset.getObjectsDataset().getDatasetQueryVarLinkset(
									linkQueryVariable,linkset);
							LinksetOpService linksetOpService = new LinksetOpService(linkset, new OpService(
									linksetNode, new OpBGP(pattern), false));

							linkQueryVariable.addDatasetQueryVar(subjectDatasetQueryVarLinkset, linksetOpService,
									objectDatasetQueryVarLinkset);

							linkQueryVariable.addDatasetQueryVar(objectDatasetQueryVarLinkset, linksetOpService,
									subjectDatasetQueryVarLinkset);

							linksetOpServices.add(subjectDatasetQueryVarLinkset);
							linksetOpServices.add(objectDatasetQueryVarLinkset);

						}
					}
				}
			}
		}
		return linksetOpServices;
	}
	
}
