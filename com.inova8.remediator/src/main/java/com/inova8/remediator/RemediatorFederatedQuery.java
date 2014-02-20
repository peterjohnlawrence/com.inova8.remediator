package com.inova8.remediator;

import java.util.ArrayList;
import java.util.Set;

import org.apache.jena.atlas.logging.Log;
import org.apache.xerces.util.URI;
import org.apache.xerces.util.URI.MalformedURIException;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpAsQuery;
import com.hp.hpl.jena.sparql.algebra.OpVars;
import com.hp.hpl.jena.sparql.algebra.OpAsQuery.Converter;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpSequence;
import com.hp.hpl.jena.sparql.core.Var;
import com.inova8.requiem.rewriter.Clause;
import com.inova8.requiem.rewriter.FunctionalTerm;
import com.inova8.requiem.rewriter.Rewriter;
import com.inova8.requiem.rewriter.Term;
import com.inova8.requiem.rewriter.TermFactory;
import com.inova8.requiem.rewriter.Variable;

public class RemediatorFederatedQuery {

	private static final String FUNCTIONALTERM_PREFIX = "Q";

	private static final String HTTP_WWW_W3_ORG_1999_02_22_RDF_SYNTAX_NS_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

	final private QueryVars globalQueryVars;

	private static TermFactory termFactory = new TermFactory();
	private static final Rewriter rewriter = new Rewriter();

	private Clause originalClause;
	private ArrayList<Clause> rewrittenClauses = new ArrayList<Clause>();
	private ArrayList<OpBGP> ops = new ArrayList<OpBGP>();
	private Datasets datasets;
	private Linksets linksets;
	private Void voidModel;
	private QueryPlan queryPlan;
	
	private RemediatorQuery remediatorQuery;

	private Boolean optimize;

	private Op rewrittenOperations;

	public RemediatorFederatedQuery(RemediatorQuery remediatorQuery,
			Void voidModel, Boolean optimize) {
		this.remediatorQuery = remediatorQuery;
		this.voidModel = voidModel;
		this.optimize = optimize;
		this.globalQueryVars = this.remediatorQuery.getVariableList();
		this.datasets = voidModel.getDatasets();
		this.linksets = voidModel.getLinksets();

		rewrittenOperations = transform(this.remediatorQuery);
	}

	public Op getRewrittenOperations() {
		return rewrittenOperations;
	}

	public Query getRewrittenQuery() {
		
		return OpAsQuery.asQuery(rewrittenOperations);
	}

	public QueryPlan getQueryPlan() {
		return this.queryPlan;
	}
	public Op transform(RemediatorQuery remediatorQuery) {
		if (OpBGP.isBGP(remediatorQuery.getSimplifiedOperations())) {
			{
				DatasetQueryVarLinksets linksetOpServices;
				originalClause = BGPToClause((OpBGP) remediatorQuery.getSimplifiedOperations());
				rewriteBGP(originalClause);
				voidModel.InferVariableClasses(globalQueryVars);
				globalQueryVars.locateDatasetClauses(datasets);
				linksetOpServices=linksets.createLinksetQueryClauses(globalQueryVars);

				if (optimize && ! voidModel.getPartitionStatisticsAvailable()){
					Log.warn(this, "Statistics not queried nor read so optimization of query plan not avaiable. Heuristic will be used instead");
					this.optimize=false;
				}
				queryPlan = new QueryPlan(globalQueryVars, optimize);
				//datasets.createDatasetQueryClauses(remediatorQuery, globalQueryVars);
				return createOpSequence(linksetOpServices);
			}
		} else {
			Log.warn(this, "Can only transform BGPs " + remediatorQuery.getSimplifiedOperations().toString());
			return null;
		}
	}
	
	private Clause BGPToClause(OpBGP opBGP) {
		Term[] bAtom = new Term[opBGP.getPattern().size()];
		int index = 0;
		for (Triple triple : opBGP.getPattern().getList()) {
			bAtom[index] = tripleToTerm(triple);
			((FunctionalTerm)bAtom[index]).originIndex=triple.hashCode();
			index++;
		}
		Term hAtom = BGPToTerm(opBGP);
		return new Clause(bAtom, hAtom);
	}

	private Term BGPToTerm(OpBGP opBGP) {
		new OpVars();
		Set<Var> variables = OpVars.visibleVars(opBGP);
		Term[] vAtom = new Term[variables.size()];
		int index = 0;
		for (Var variable : variables) {
			vAtom[index] = termFactory.getVariable(globalQueryVars.indexOf(variable));
			index++;
		}
		return termFactory.getFunctionalTerm(FUNCTIONALTERM_PREFIX, vAtom);
	}

	public ArrayList<Clause> getClauses() {
		return rewrittenClauses;
	}

	public ArrayList<Dataset> getDatasets() {
		return datasets;
	}

	public ArrayList<OpBGP> getOps() {
		return ops;
	}

	private Term nodeToTerm(Node node) {
		if (node.isLiteral()) {
			return termFactory.getConstant(node.getLiteral().toString());
		} else if (node.isURI()) {
			// return termFactory.getConstant(prefixes.qnameFor(node.getURI()));
			return termFactory.getConstant(node.getURI().toString());
		} else if (node.isVariable()) {
			return termFactory.getVariable(globalQueryVars.indexOf((Var) node));
		} else if (node.isBlank()) {
			// TODO handle blank nodes in BGP
			return null;
		} else {
			return null;
		}
	}

	private ArrayList<Clause> rewrite(Clause clause) throws Exception {
		ArrayList<Clause> original = new ArrayList<Clause>();
		original = voidModel.getConjunctiveClauses();
		original.add(clause);
		// N (Naive: no optimizations), F (Full: forward/query subsumption and
		// dependency graph pruning), or G (Greedy: Full plus greedy unfolding).
		return rewriter.rewrite(original, "N");
	}

	public void setClauses(ArrayList<Clause> clauses) {
		this.rewrittenClauses = clauses;
	}

	public void setOps(ArrayList<OpBGP> ops) {
		this.ops = ops;
	}

	private Node termToNode(Dataset dataset, Term term) {
		if (term instanceof FunctionalTerm) {
			try {
				URI uri = new URI(term.getName());
				return NodeFactory.createURI(uri.toString());
			} catch (MalformedURIException e) {
				return NodeFactory.createLiteral(term.toString());
			}
		} else if (term instanceof Variable) {
			QueryVar queryVar = globalQueryVars.get(term.getMinVariableIndex());
			return Var.alloc(queryVar.getLinkedName(dataset));
			} else {
			return null;
		}
	}

	public Triple termToTriple(Dataset dataset, Term term) {
		if (term.getArity() == 1) {
			Node clas = NodeFactory.createURI(term.getName());
			Node object = termToNode(dataset, term.getArgument(0));
			return Triple
					.create(object, NodeFactory.createURI(HTTP_WWW_W3_ORG_1999_02_22_RDF_SYNTAX_NS_TYPE), clas);
		} else if (term.getArity() == 2) {
			Node pred = NodeFactory.createURI(term.getName());
			Node subj = termToNode(dataset, term.getArgument(0));
			Node obj = termToNode(dataset, term.getArgument(1));
			return Triple.create(subj, pred, obj);
		} else {
			return null;
		}
	}

	private OpSequence createOpSequence(DatasetQueryVarLinksets linksetOpServices ) {
		DatasetQueryVarLinksets initialLinksetOpServices = new DatasetQueryVarLinksets();
		initialLinksetOpServices = (DatasetQueryVarLinksets) linksetOpServices.clone();
		return queryPlan.createOpSequence(globalQueryVars, initialLinksetOpServices,remediatorQuery);
	}


	private void rewriteBGP(Clause originalClause) {
		//Clause originalClause = BGPToClause(opBGP);
		try {
			rewrittenClauses = rewrite(originalClause);
			// clauses = rewriteDlite(clause);
		} catch (Exception e) {
			Log.debug(this, "Failed to rewrite clause " + originalClause.toString());
			Log.debug(this, e.getStackTrace().toString());
		}
		for (Clause cl : rewrittenClauses) {
			for (Dataset dataset : datasets) {
				dataset.addClause(cl);
			}
		}
	}

	private FunctionalTerm tripleToTerm(Triple triple) {
		Node pred = triple.getPredicate();
		Node subj = triple.getSubject();
		Node obj = triple.getObject();
		try {
			if (pred.getURI().toString()
					.equals(HTTP_WWW_W3_ORG_1999_02_22_RDF_SYNTAX_NS_TYPE)) {
				if (obj.isVariable()) {
					return termFactory.getFunctionalTerm(pred.getURI()
							.toString(), nodeToTerm(subj), nodeToTerm(obj));
				} else {// Treat as URI, literal will throw exception
					return termFactory.getFunctionalTerm(obj.getURI()
							.toString(), nodeToTerm(subj));
				}
			} else {
				return termFactory.getFunctionalTerm(pred.getURI().toString(),
						nodeToTerm(subj), nodeToTerm(obj));
			}
		} catch (UnsupportedOperationException e) {
			throw e;
		}
	}

}
