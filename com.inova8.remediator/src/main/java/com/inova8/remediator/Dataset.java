package com.inova8.remediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.jena.atlas.logging.Log;
import org.apache.xerces.util.URI;
import org.apache.xerces.util.URI.MalformedURIException;
import com.inova8.requiem.rewriter.Clause;
import com.inova8.requiem.rewriter.Term;
import com.inova8.requiem.rewriter.TermFactory;
import com.inova8.requiem.rewriter.Variable;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.PrefixMapping;

class Dataset {
	protected OntResource dataset;

	protected OntModel model;
	protected OntResource sparqlEndPoint;
	private OntResource uriSpace;
	protected Vocabularies vocabularies = new Vocabularies();
	private Integer triples;

	private Integer entities;
	private Integer classes;
	private Integer properties;
	private Integer distinctSubjects;
	private Integer distinctObjects;

	private Partitions partitions;
	private PrefixMapping prefixes;
	private String prefix;

	private QueryClauses queryClauses = new QueryClauses();
	private HashMap<QueryVar,DatasetQueryVarLinkset > datasetQueryVarLinksets = new HashMap<QueryVar,DatasetQueryVarLinkset >();

	private static TermFactory termFactory = new TermFactory();
	private String datasetVocabularyQuery = "PREFIX void: <" + Void.NS + ">\n"
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			+ "SELECT ?vocabulary \n" + "WHERE  {\n"
			+ "    ?dataset rdf:type void:Dataset .\n"
			+ "    ?dataset void:vocabulary ?vocabulary .\n" + "}\n";
	private static String propertyPartitionStatisticsQuery = "SELECT  ?p (COUNT(DISTINCT ?o ) AS ?count )\n"
			+ "WHERE  { ?s ?p ?o }\n" + "GROUP BY ?p\n" + "ORDER BY ?count";
	private static String classPartitionStatisticsQuery = "SELECT  ?class (COUNT(?s) AS ?count )\n"
			+ "WHERE { ?s a ?class }\n" + "GROUP BY ?class";
	private static String datasetStatisticsQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			+ "SELECT ?triples  ?entities ?classes ?properties ?distinctSubjects ?distinctObjects \n"
			+ "WHERE {\n"
			+ "{SELECT (COUNT(*) AS ?triples) { ?s ?p ?o  }}\n"
			+ "{SELECT (COUNT(distinct ?s) AS ?entities) { ?s a []  }}\n"
			+ "{SELECT (COUNT(distinct ?o) AS ?classes) { ?s rdf:type ?o }}\n"
			+ "{SELECT (COUNT(distinct ?p) AS ?properties){ ?s ?p ?o }}\n"
			+ "{SELECT (COUNT(DISTINCT ?s ) AS ?distinctSubjects) {  ?s ?p ?o   } }\n"
			+ "{SELECT (COUNT(DISTINCT ?o ) AS ?distinctObjects) {  ?s ?p ?o  filter(!isLiteral(?o))} }\n"
			+ "}";
	private static String datasetPartitionQuery = "PREFIX void: <" + Void.NS
			+ ">\n" + "SELECT  ?Dataset  ?class ?entities ?type \n"
			+ "WHERE {\n" + "{ ?Dataset a void:Dataset .\n"
			+ "?Dataset void:classPartition ?ClassPartition .\n"
			+ "BIND( 'C' as  ?type ) .\n"
			+ "?ClassPartition void:class ?class .\n"
			+ "?ClassPartition void:entities ?entities .\n" + "} UNION {\n"
			+ "?Dataset a void:Dataset .\n"
			+ "?Dataset void:propertyPartition ?ClassPartition .\n"
			+ "BIND( P' as  ?type ) .\n"
			+ "?ClassPartition void:property ?class .\n"
			+ "?ClassPartition void:triples ?entities .\n" + "}\n" + "}";

	private static String classPartitionQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			+ "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
			+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
			+ "SELECT ?class \n"
			+ "WHERE {\n"
			+ "{ ?class a rdfs:Class .}\n"
			+ " UNION\n" + "{ ?class a owl:Class .}\n" + "}";

	private static String propertyPartitionQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			+ "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
			+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
			+ "SELECT ?property\n"
			+ "WHERE {\n"
			+ "{ ?property a rdf:Property .}\n"
			+ " UNION\n"
			+ "{ ?property a owl:ObjectProperty .}\n"
			+ " UNION\n"
			+ "{ ?property a owl:DatatypeProperty .}\n" + "}";
    
	private static String variableClassName =Variable.class.getName();
	
	Dataset(OntModel model, OntResource dataset,
			OntResource sparqlEndPoint) {

		this.model = model;
		this.dataset = dataset;

		this.sparqlEndPoint = sparqlEndPoint;

	}

	Dataset(OntModel model, OntResource dataset,
			OntResource sparqlEndPoint, OntResource uriSpace, String prefix)
			throws MalformedURIException {

		this.model = model;
		this.dataset = dataset;
		this.sparqlEndPoint = sparqlEndPoint;
		this.uriSpace = uriSpace;
		loadVocabularies();
		if (prefix == null) {
			this.prefix = "J" + dataset.hashCode();
		} else {
			this.prefix = prefix;
		}
		this.partitions = new Partitions(model);
	}

	public OntResource getDataset() {
		return dataset;
	}

	boolean addClassPartition(OntResource clazz, Integer entities) {
		return partitions.addClassPartition(clazz, entities);
	}

	void addClause(Clause additionalClause) {
		ArrayList<Term> bodyTerm = new ArrayList<Term>();
		// Remove those terms that are not recognized by this datasource
		for (Term term : additionalClause.getBody()) {
			if (isRecognizedTerm(term)) {
				bodyTerm.add(term);
			}
		}
		// If anything left than add as another clause, as long as that clause
		// does not already exist.

		if (!bodyTerm.isEmpty()) {
			Term bodyTermArray[] = new Term[bodyTerm.size()];
			bodyTerm.toArray(bodyTermArray);
			if (!isSubsumed(bodyTermArray)) {
				Clause newClause = new Clause(bodyTermArray,
						getHeadFromBody(bodyTermArray));
				// Make sure that the clause is not disconnected/Cartesian, if
				// so separate into separate clauses.
				ArrayList<Clause> disconnectedClauses = detectDisconnectedClauses(newClause);
				// Remove any existing clauses that would be subsumed by these
				// new clauses.
				for (Clause disconnectedClause : disconnectedClauses) {
					Term[] disconnectedBody = disconnectedClause.getBody();
					cleanUpClauses(disconnectedBody);
					queryClauses.add(new QueryClause(new Clause(disconnectedBody,getHeadFromBody(disconnectedBody)), this));

				}
			}
		}
	}

	private boolean isSubsumed(Term[] newBodyTerm) {
		boolean clauseFound = false;
		for (Clause clause : queryClauses) {
			clauseFound = isThisSubsumedInThat(newBodyTerm, clause.getBody());
			// No point looking any further so stop now
			if (clauseFound)
				break;
		}
		return clauseFound;
	}

	private boolean isThisSubsumedInThat(Term[] thisTerm, Term[] thatTerm) {
		boolean termFound = true;
		boolean clauseFound = true;
		for (Term thisAtom : thisTerm) {
			// Check if each new term of the new clause is within the body of
			// this clause.
			// If it is then we can say that the new clause is subsumed in this
			// clause, so no need to add new clause
			termFound = false;
			for (Term thatAtom : thatTerm) {
				// TODO string match is not the best technique if the toString
				// changes but it works and toString is unlikely to change.
				if (thatAtom.toString().equals(thisAtom.toString())) {
					termFound = true;
					break;
				}
			}
			clauseFound &= termFound;
		}
		return clauseFound;
	}

	private void cleanUpClauses(Term[] thatTerm) {
		// Since a new clause has been added, check that the existing clauses
		// are not subsumed into the new clause. If so remove them.
		Iterator<QueryClause> clauseIterator = queryClauses.iterator();
		while (clauseIterator.hasNext()) {
			if (isThisSubsumedInThat(clauseIterator.next().getBody(), thatTerm)) {
				clauseIterator.remove();
			}
		}
	}

	private ArrayList<Clause> detectDisconnectedClauses(Clause newClause) {

		ArrayList<Clause> disconnectedClauses = new ArrayList<Clause>();
		// Create list of variables which will be the starting points of
		// depth-first searching (dfs). As they are traversed they will be
		// removed from this list until it is empty.
		Set<Variable> whiteVariables = new HashSet<Variable>();
		whiteVariables.addAll(newClause.getVariables());
		if (!whiteVariables.isEmpty()) {
			do {
				Variable whiteVariable = whiteVariables.iterator().next();
				// Add all terms into the whiteTerm set. These will then be
				// moved to
				// the greyTerm and onto the final set once they have been
				// determined to be part of a disconnected subgraph

				Set<Term> whiteEdges = new HashSet<Term>(
						Arrays.asList(newClause.getBody()));
				Set<Term> greyEdges = new HashSet<Term>();
				Set<Term> blackEdges = new HashSet<Term>();
				Set<Variable> blackVariables = new HashSet<Variable>();
				blackVariables.add(whiteVariable);
				whiteVariables.remove(whiteVariable);
				//Clean out any edges that have no variables with which to link			
		        Iterator<Term> termItr = whiteEdges.iterator(); 
		        while(termItr.hasNext()) {
		        	Term term = termItr.next();
					if (getVariableArity(term)==0){
						blackEdges.add(term);
						termItr.remove();
					}		        	   
		        }
								
				dfs(whiteVariable, whiteEdges, greyEdges, blackEdges,
						whiteVariables, blackVariables);
				disconnectedClauses.add(new Clause(blackEdges
						.toArray(new Term[blackEdges.size()]),
						variablesToHeadTerm(blackVariables)));
				whiteVariables.removeAll(blackVariables);

			} while (!whiteVariables.isEmpty());
		} else {
			disconnectedClauses.add(newClause);
		}
		return disconnectedClauses;
	}

	private void dfs(Variable whiteVariable, Set<Term> whiteEdges,
			Set<Term> greyEdges, Set<Term> blackEdges,
			Set<Variable> whiteVariables, Set<Variable> blackVariables) {

		Term whiteTerm = findEdge(whiteVariable, whiteEdges);

		while (whiteTerm != null) {
			if (getVariableArity(whiteTerm) > 1) {
					greyEdges.add(whiteTerm);
				whiteEdges.remove(whiteTerm);
				Variable newBodyVariable = findVariable(whiteVariable,
						whiteTerm);
				blackVariables.add(newBodyVariable);
				whiteVariables.remove(newBodyVariable);
				dfs(newBodyVariable, whiteEdges, greyEdges, blackEdges,
						whiteVariables, blackVariables);
				blackEdges.add(whiteTerm);
				whiteEdges.remove(whiteTerm);

			} else {// arity=1;
				blackEdges.add(whiteTerm);
				whiteEdges.remove(whiteTerm);
				dfs(whiteVariable, whiteEdges, greyEdges, blackEdges,
						whiteVariables, blackVariables);
			}
			whiteTerm = findEdge(whiteVariable, whiteEdges);
		}
		Term greyTerm = findEdge(whiteVariable, greyEdges);
		if (greyTerm != null) {
			blackEdges.add(greyTerm);
			greyEdges.remove(greyTerm);
		}

	}
    private int getVariableArity(Term term){
    	int count=0;
    	for (Term argument: term.getArguments()){
    		//argument.getClass().getName().equals("org.oxford.comlab.requiem.rewriter.Variable")
    		//org.oxford.comlab.requiem.rewriter.FunctionalTerm
    		if (argument.getClass().getName().equals(variableClassName)) count++;
    	}
    	return count;
    }
	private Term findEdge(Variable bodyVariable, Set<Term> edges) {
		for (Term term : edges) {
			Term edge = findEdge(bodyVariable, term);
			if (edge != null)
				return edge;
		}
		return null;
	}

	private Term findEdge(Variable bodyVariable, Term edge) {
		// Find an edge of specified status in the body of the new clause that
		// is related to the bodyVariable.
		for (Term term : edge.getArguments()) {
			Term t = term.getVariableOrConstant();
			if ((t instanceof Variable) && (t.equals(bodyVariable)))
				return edge;
		}
		return null;
	}

	private Variable findVariable(Variable bodyVariable, Term edge) {
		for (Term term : edge.getArguments()) {
			Term t = term.getVariableOrConstant();
			if ((t instanceof Variable) && (!t.equals(bodyVariable)))
				return (Variable) term;
		}
		return null;
	}

	boolean addPropertyPartition(OntResource property, Integer triples) {
		return partitions.addPropertyPartition(property, triples);
	}

	public Integer getClasses() {
		return classes;
	}

	public QueryClauses getQueryClauses() {
		return queryClauses;
	}

	public Integer getEntities() {
		return entities;
	}

	private Term getHeadFromBody(Term[] bodyTerms) {
		ArrayList<Variable> bodyVariables = new ArrayList<Variable>();

		// Get the variables of the body
		for (int j = 0; j < bodyTerms.length; j++) {
			for (int i = 0; i < bodyTerms[j].getArguments().length; i++) {
				Term t = bodyTerms[j].getArguments()[i].getVariableOrConstant();
				if ((t instanceof Variable) && !(bodyVariables.contains(t))) {
					bodyVariables.add((Variable) t);
				}
			}
		}
		Term[] vAtom = new Term[bodyVariables.size()];
		return termFactory.getFunctionalTerm("Q", bodyVariables.toArray(vAtom));
	}

	public Integer getObjects() {
		return distinctObjects;
	}

	Partitions getPartitions() {
		return this.partitions;
	}

	public String getPrefix() {
		return prefix;
	}

	public PrefixMapping getPrefixes() {
		return prefixes;
	}

	public Integer getProperties() {
		return properties;
	}

	public OntResource getSparqlEndPoint() {
		return sparqlEndPoint;
	}

	public Integer getSubjects() {
		return distinctSubjects;
	}

	public Integer getTriples() {
		return triples;
	}

	public OntResource getUriSpace() {
		return uriSpace;
	}

	Vocabularies getVocabularies() {
		return this.vocabularies;
	}

	boolean isRecognizedTerm(Term term) {
		// TODO allow for variableArity of 0
		if (isRecognizedURI(term)) {
			// TODO do we really need to test for explicit URIs other than
			// predicates and classes?
			if (getVariableArity(term) == 0) {
				return true;
			} else {

				if (term.getArity() == 1) {
					return isRecognizedURI(term.getArgument(0));
				} else if (term.getArity() == 2) {
					if (isRecognizedURI(term.getArgument(0))) {
						return isRecognizedURI(term.getArgument(1));
					} else {
						return false;
					}
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
	}

	public boolean isRecognizedURI(String uri) {
		try {
			return isRecognizedURI(new URI(uri));
		} catch (MalformedURIException e) {
			Log.debug(Dataset.class, "Malformed URI: " + uri);
		}
		return false;
	}

	private boolean isRecognizedURI(Term term) {
		//TODO replace or eliminate Term, declaration so pure Jena
		//TODO test if a functional term or a variable
		
//		switch(term.getClass().getName()){
//		case "org.oxford.comlab.requiem.rewriter.Variable":
//			return false;
//		case "org.oxford.comlab.requiem.rewriter.FunctionalTerm":
//			try {
//				URI uri = new URI(term.getName());
//				return isRecognizedURI(uri.toString());
//			} catch (MalformedURIException e) {
//				return true;
//			}
//		default: return false;
//		}
		try {
			URI uri = new URI(term.getName());
			return isRecognizedURI(uri.toString());
		} catch (MalformedURIException e) {
			return true;
		}
	}

	private boolean isRecognizedURI(URI uri) {
		try {
			if (partitions.contains(uri.toString())) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			Log.debug(Dataset.class, "Malformed URI: " + uri.toString());
			Log.debug(Dataset.class, e.getStackTrace().toString());
			return false;

		}

	}

	public void loadVocabularies() {

		QuerySolutionMap binding = new QuerySolutionMap();
		binding.add("dataset", dataset);
		Query query = QueryFactory.create(datasetVocabularyQuery);
		QueryExecution qexec = QueryExecutionFactory.create(query, model,
				binding);

		try {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				OntResource vocabulary = soln.getResource("vocabulary").as(
						OntResource.class);
				vocabularies.add(vocabulary);
			}
		} catch (Exception e) {
			Log.debug(Dataset.class, "Failed datasetVocabularyQuery");
			Log.debug(Dataset.class, e.getStackTrace().toString());

		} finally {
			qexec.close();
		}
	}

	public void setClasses(Integer classes) {
		this.classes = classes;
	}

	public void setEntities(Integer entities) {
		this.entities = entities;
	}

	public void setObjects(Integer objects) {
		this.distinctObjects = objects;
	}

	public void setPredicates(Integer predicates) {
		this.properties = predicates;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setPrefixes(PrefixMapping prefixes) {
		this.prefixes = prefixes;
	}

	public void setSparqlEndPoint(OntResource sparqlEndPoint) {
		this.sparqlEndPoint = sparqlEndPoint;
	}

	public void setSubjects(Integer subjects) {
		this.distinctSubjects = subjects;
	}

	public void setTriples(Integer triples) {
		this.triples = triples;
	}

	public void setUriSpace(OntResource uriSpace) {
		this.uriSpace = uriSpace;
	}

	public void setVocabularies(Vocabularies vocabularies) {
		this.vocabularies = vocabularies;
	}

	@Override
	public String toString() {
		return "Dataset:"+uriSpace.toString();
	}

	private void updateClassPartitionStatistics() {
		Query query = QueryFactory.create(classPartitionStatisticsQuery);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				this.sparqlEndPoint.toString(), query);

		try {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				OntResource clazz = soln.getResource("class").as(
						OntResource.class);
				Integer triples = (soln.getLiteral("count") != null) ? soln
						.getLiteral("count").getInt() : null;
				partitions.addClassPartition(clazz, triples);
			}
		} catch (Exception e) {
			Log.debug(
					Dataset.class,
					"Unable to connect to SPARQLEndpoint to execute classPartitionStatisticsQuery: "
							+ this.sparqlEndPoint.toString());
		} finally {
			qexec.close();
		}
	}

	public void updatePartitions(OntModel voidModel) {
		OntModelSpec partitionModelSpec = new OntModelSpec(OntModelSpec.OWL_MEM);
		partitionModelSpec.setDocumentManager(voidModel.getDocumentManager());
		partitionModelSpec.getDocumentManager().setProcessImports(true);

		OntModel partitionModel = ModelFactory
				.createOntologyModel(partitionModelSpec);

		for (OntResource vocabulary : this.getVocabularies()) {
			try {
				partitionModel.read(vocabulary.getURI());
			} catch (Exception e) {
				Log.debug(Void.class, "Failed to locate dataset vocabulary: "
						+ vocabulary + "  " + e.getMessage());
			}
		}

		updateClassPartition(partitionModel);
		updatePropertyPartition(partitionModel);
		writePartitionsToModel();

	}

	private void updateClassPartition(OntModel partitionModel) {
		Query query = QueryFactory.create(classPartitionQuery);
		QueryExecution qexec = QueryExecutionFactory.create(query,
				partitionModel);

		try {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				OntResource clazz = soln.getResource("class").as(
						OntResource.class);
				partitions.addClassPartition(clazz, null);
			}
		} catch (Exception e) {
			Log.debug(Dataset.class, "Failed to execute classPartitionQuery");
		} finally {
			qexec.close();
		}
	}

	protected void updatePropertyPartition(OntModel partitionModel) {
		Query query = QueryFactory.create(propertyPartitionQuery);
		QueryExecution qexec = QueryExecutionFactory.create(query,
				partitionModel);

		try {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				OntResource property = soln.getResource("property").as(
						OntResource.class);
				partitions.addPropertyPartition(property, null);
			}
		} catch (Exception e) {
			Log.debug(Dataset.class,
					"Failed to execute to execute propertyPartitionQuery");
		} finally {
			qexec.close();
		}
	}

	private void updateDatasetPartitionStatistics() {
		Query query = QueryFactory.create(datasetStatisticsQuery);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				this.sparqlEndPoint.toString(), query);

		try {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				// ?triples ?entities ?classes ?predicates ?subjects ?objects
				QuerySolution soln = results.nextSolution();
				triples = (soln.getLiteral("triples") != null) ? soln
						.getLiteral("triples").getInt() : null;
				entities = (soln.getLiteral("entities") != null) ? soln
						.getLiteral("entities").getInt() : null;
				classes = (soln.getLiteral("classes") != null) ? soln
						.getLiteral("classes").getInt() : null;
				properties = (soln.getLiteral("properties") != null) ? soln
						.getLiteral("properties").getInt() : null;
				distinctSubjects = (soln.getLiteral("distinctSubjects") != null) ? soln
						.getLiteral("distinctSubjects").getInt() : null;
				distinctObjects = (soln.getLiteral("distinctObjects") != null) ? soln
						.getLiteral("distinctObjects").getInt() : null;
			}
		} catch (Exception e) {
			Log.debug(Dataset.class,
					"Unable to connect to SPARQLEndpoint to execute datasetStatisticsQuery: "
							+ this.sparqlEndPoint.toString());
		} finally {
		}
		qexec.close();
	}

	protected void updatePropertyPartitionStatistics() {
		Query query = QueryFactory.create(propertyPartitionStatisticsQuery);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				this.sparqlEndPoint.toString(), query);

		try {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				OntResource property = soln.getResource("p").as(
						OntResource.class);
				Integer triples = (soln.getLiteral("count") != null) ? soln
						.getLiteral("count").getInt() : null;
				partitions.addPropertyPartition(property, triples);
			}
		} catch (Exception e) {
			Log.debug(
					Dataset.class,
					"Unable to connect to SPARQLEndpoint to execute propertyPartitionStatisticsQuery: "
							+ this.sparqlEndPoint.toString());
		} finally {
			qexec.close();
		}
	}

	public void updatePartitionStatistics() {
		if (sparqlEndPoint != null) {
			updateDatasetPartitionStatistics();
			updatePropertyPartitionStatistics();
			updateClassPartitionStatistics();
			writePartitionsToModel();
		}
	}

	private Term variablesToHeadTerm(Set<Variable> variables) {

		Term[] vAtom = variables.toArray(new Term[variables.size()]);
		return termFactory.getFunctionalTerm("Q", vAtom);
	}

	public void writePartitionsToModel() {

		// Cleanup existing statistics
		dataset.removeAll(Void.triples);
		dataset.removeAll(Void.entities);
		dataset.removeAll(Void.classes);
		dataset.removeAll(Void.properties);
		dataset.removeAll(Void.distinctSubjects);
		dataset.removeAll(Void.distinctObjects);

		// Now add the new ones
		if (this.getTriples() != null)
			dataset.addLiteral(Void.triples, this.getTriples());
		if (this.getEntities() != null)
			dataset.addLiteral(Void.entities, this.getEntities());
		if (this.getClasses() != null)
			dataset.addLiteral(Void.classes, this.getClasses());
		if (this.getProperties() != null)
			dataset.addLiteral(Void.properties, this.getProperties());
		if (this.getSubjects() != null)
			dataset.addLiteral(Void.distinctSubjects, this.getSubjects());
		if (this.getObjects() != null)
			dataset.addLiteral(Void.distinctObjects, this.getObjects());

		partitions.write(model, dataset);

	}

	public Integer getDistinctSubjects() {
		return distinctSubjects;
	}

	public QueryClauses getClauseVariables(QueryVars queryVars,
			QueryVar queryVar) {
		QueryClauses queryClauses = new QueryClauses();
		for (QueryClause queryClause : this.getQueryClauses()) {
			if (queryClause.getClauseVariables(queryVars).contains(
					queryVar))
				queryClauses.add(queryClause);
		}
		return queryClauses;
	}

	public DatasetQueryVarLinkset getDatasetQueryVarLinkset(QueryVar linkQueryVariable, Linkset linkset) {
		// TODO Change lookup key 
		DatasetQueryVarLinkset tempDatasetQueryVarLinkset = new DatasetQueryVarLinkset(this, linkQueryVariable,linkset);
		if(datasetQueryVarLinksets.containsKey(tempDatasetQueryVarLinkset)){
			return datasetQueryVarLinksets.get(tempDatasetQueryVarLinkset);
		}else{
			datasetQueryVarLinksets.put(linkQueryVariable,tempDatasetQueryVarLinkset);
			return tempDatasetQueryVarLinkset;
		}
	}
}
