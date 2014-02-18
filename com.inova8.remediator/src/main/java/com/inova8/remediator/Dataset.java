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

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
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
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.core.Var;
import com.inova8.requiem.rewriter.Clause;
import com.inova8.requiem.rewriter.FunctionalTerm;
import com.inova8.requiem.rewriter.Term;
import com.inova8.requiem.rewriter.TermFactory;
import com.inova8.requiem.rewriter.Variable;

class Dataset {
	private static final String HTTP_WWW_W3_ORG_1999_02_22_RDF_SYNTAX_NS_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	private static final Integer DEFAULT_DISTINCTSUBJECTS = 100;
	private static final Integer DEFAULT_CLASSES = 10;
	private static final Integer DEFAULT_ENTITIES = 100;
	private static final Integer DEFAULT_PROPERTIES = 100;
	private static final int DEFAULT_DISTINCTOBJECTS = 100;
	private static final int DEFAULT_TRIPLES = 10000;
	private static final int DEFAULT_SUBJECTS = 1000;
	protected OntResource dataset;
	private Resource datasetStatistics;
	protected Void voidInstance;
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
	
	protected Dataset(Void voidInstance, OntResource dataset,
			OntResource sparqlEndPoint) {

		this.voidInstance = voidInstance;
		this.dataset = dataset;
		this.sparqlEndPoint = sparqlEndPoint;

	}

	Dataset(Void voidInstance, OntResource dataset,
			OntResource sparqlEndPoint, OntResource uriSpace, String prefix)
			throws MalformedURIException {

		this.voidInstance = voidInstance;
		this.dataset = dataset;
		this.sparqlEndPoint = sparqlEndPoint;
		this.uriSpace = uriSpace;
		loadVocabularies();
		if (prefix == null) {
			this.prefix = "J" + dataset.hashCode();
		} else {
			this.prefix = prefix;
		}
		this.partitions = new Partitions(this.voidInstance);
		this.datasetStatistics = voidInstance.getStatisticsModel().createResource(dataset.getURI());
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

		// TODO Should not add anonymous variables to head 
		if (!bodyTerm.isEmpty()) {
			Term bodyTermArray[] = new Term[bodyTerm.size()];
			bodyTerm.toArray(bodyTermArray);
			if (!isSubsumed(bodyTermArray)) {
				// Only add to body if included in original head, in this way excluding additional anonymous variables
				Clause newClause = new Clause(bodyTermArray,
						getHeadFromBody(additionalClause.getHead(),bodyTermArray));
				// Make sure that the clause is not disconnected/Cartesian, if
				// so separate into separate clauses.
				ArrayList<Clause> disconnectedClauses = detectDisconnectedClauses(newClause);
				// Remove any existing clauses that would be subsumed by these
				// new clauses.
				for (Clause disconnectedClause : disconnectedClauses) {
					Term[] disconnectedBody = disconnectedClause.getBody();
					cleanUpClauses(disconnectedBody);
					queryClauses.add(new QueryClause(new Clause(disconnectedBody,getHeadFromBody(additionalClause.getHead(), disconnectedBody)), this));

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
				// String match is not the best technique if the toString
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
	public int getDistinctSubjects() {
		if (distinctSubjects ==null){
			return DEFAULT_DISTINCTSUBJECTS;
		}else
		{
			return (int) distinctSubjects;
		}
	}
	public int getClasses() {
		if (classes ==null){
			return DEFAULT_CLASSES;
		}else
		{
			return (int) classes;
		}
	}
	public int getEntities() {
		if (entities ==null){
			return DEFAULT_ENTITIES;
		}else
		{
			return (int) entities;
		}
	}
	public int getSubjects() {
		if (distinctSubjects ==null){
			return DEFAULT_SUBJECTS;
		}else
		{
			return (int) distinctSubjects;
		}
	}

	public int getTriples() {
		if (triples ==null){
			return DEFAULT_TRIPLES;
		}else
		{
			return (int) triples;
		}
	}
	public int getDistinctObjects() {
		if (distinctObjects ==null){
			return DEFAULT_DISTINCTOBJECTS;
		}else
		{
			return (int) distinctObjects;
		}
	}

	public Integer getProperties() {
		if (properties ==null){
			return DEFAULT_PROPERTIES;
		}else
		{
			return (int) properties;
		}
	}
	public QueryClauses getQueryClauses() {
		return queryClauses;
	}

	private Term getHeadFromBody(Term originalHead, Term[] bodyTerms) {
		ArrayList<Variable> bodyVariables = new ArrayList<Variable>();

		// Get the variables of the body
		//TODO Except anonymous variables
		for (int j = 0; j < bodyTerms.length; j++) {
			for (int i = 0; i < bodyTerms[j].getArguments().length; i++) {
				Term t = bodyTerms[j].getArguments()[i].getVariableOrConstant();
				if ((t instanceof Variable) && !(bodyVariables.contains(t)) && Arrays.asList(originalHead.getArguments()).contains(t)) {
					bodyVariables.add((Variable) t);
				}
			}
		}
		Term[] vAtom = new Term[bodyVariables.size()];
		return termFactory.getFunctionalTerm("Q", bodyVariables.toArray(vAtom));
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


	public OntResource getSparqlEndPoint() {
		return sparqlEndPoint;
	}



	public OntResource getUriSpace() {
		return uriSpace;
	}

	Vocabularies getVocabularies() {
		return this.vocabularies;
	}
	public void setDistinctSubjects(Integer distinctSubjects) {
		this.distinctSubjects = distinctSubjects;
	}

	public void setDistinctObjects(Integer distinctObjects) {
		this.distinctObjects = distinctObjects;
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
		QueryExecution qexec = QueryExecutionFactory.create(query, voidInstance.getVoidModel(),
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

	public void setProperties(Integer properties) {
		this.properties = properties;
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
		if (this.dataset!=null){
			return "Dataset:"+dataset.toString();
		} else
		{
			return "Dataset:<unidentified>";
		}
	}



	public void updatePartitions() {
		//Use OntModelSpec.OWL_MEM_RDFS_INF to ensure all default classes and properties are also discovered.
		OntModelSpec partitionModelSpec = new OntModelSpec(OntModelSpec.OWL_MEM_RDFS_INF);
		partitionModelSpec.setDocumentManager(voidInstance.getVoidModel().getDocumentManager());
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
				if (!clazz.isAnon()) partitions.addClassPartition(clazz, null);
			}
		} catch (Exception e) {
			Log.debug(Dataset.class, "Failed to execute classPartitionQuery");
		} finally {
			qexec.close();
		}
	}
	
	private void queryClassPartitionStatistics() {
		Query query = QueryFactory.create(classPartitionStatisticsQuery);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				this.sparqlEndPoint.toString(), query);

		try {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				OntResource clazz = soln.getResource("class").as(
						OntResource.class);
				Integer entities = (soln.getLiteral("count") != null) ? soln
						.getLiteral("count").getInt() : null;
				partitions.addClassPartition(clazz, entities);
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

	private void queryDatasetStatistics() {
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

	protected void queryPropertyPartitionStatistics() {
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

	public void queryPartitionStatistics() {
		if (sparqlEndPoint != null) {
			queryDatasetStatistics();
			queryPropertyPartitionStatistics();
			queryClassPartitionStatistics();
			addStatisticsToModel();
		}
	}

	private Term variablesToHeadTerm(Set<Variable> variables) {

		Term[] vAtom = variables.toArray(new Term[variables.size()]);
		return termFactory.getFunctionalTerm("Q", vAtom);
	}

	public void addStatisticsToModel() {

		// Cleanup existing statistics
		datasetStatistics.removeAll(Void.triples);
		datasetStatistics.removeAll(Void.entities);
		datasetStatistics.removeAll(Void.classes);
		datasetStatistics.removeAll(Void.properties);
		datasetStatistics.removeAll(Void.distinctSubjects);
		datasetStatistics.removeAll(Void.distinctObjects);

		// Now add the new ones
		if (this.triples != null)
			datasetStatistics.addLiteral(Void.triples, this.getTriples());
		if (this.entities != null)
			datasetStatistics.addLiteral(Void.entities, this.getEntities());
		if (this.classes != null)
			datasetStatistics.addLiteral(Void.classes, this.getClasses());
		if (this.properties != null)
			datasetStatistics.addLiteral(Void.properties, this.getProperties());
		if (this.distinctSubjects != null)
			datasetStatistics.addLiteral(Void.distinctSubjects, this.getSubjects());
		if (this.distinctObjects != null)
			datasetStatistics.addLiteral(Void.distinctObjects, this.getDistinctObjects());
		partitions.addStatisticsToModel(voidInstance.getStatisticsModel(), datasetStatistics);
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
		DatasetQueryVarLinkset tempDatasetQueryVarLinkset = new DatasetQueryVarLinkset(this, linkQueryVariable,linkset);
		if(datasetQueryVarLinksets.containsKey(tempDatasetQueryVarLinkset)){
			return datasetQueryVarLinksets.get(tempDatasetQueryVarLinkset);
		}else{
			datasetQueryVarLinksets.put(linkQueryVariable,tempDatasetQueryVarLinkset);
			return tempDatasetQueryVarLinkset;
		}
	}

	private Node termToNode(QueryVars queryVars, Term term) {
		if (term instanceof FunctionalTerm) {
			try {
				URI uri = new URI(term.getName());
				return NodeFactory.createURI(uri.toString());
			} catch (MalformedURIException e) {
				return NodeFactory.createLiteral(term.toString());
			}
		} else if (term instanceof Variable) {
			QueryVar queryVar = queryVars.get(term.getMinVariableIndex());
			if (queryVar!=null){
				return Var.alloc(queryVar.getLinkedName(this));
			}else{
				return Var.alloc(Integer.toString(term.getMinVariableIndex()));
			}
			} else {
			return null;
		}
	}

	public Triple termToTriple( QueryVars queryVars, Term term) {
		if (term.getArity() == 1) {
			Node clas = NodeFactory.createURI(term.getName());
			Node object = termToNode(queryVars, term.getArgument(0));
			return Triple
					.create(object, NodeFactory.createURI(HTTP_WWW_W3_ORG_1999_02_22_RDF_SYNTAX_NS_TYPE), clas);
		} else if (term.getArity() == 2) {
			Node pred = NodeFactory.createURI(term.getName());
			Node subj = termToNode(queryVars, term.getArgument(0));
			Node obj = termToNode(queryVars, term.getArgument(1));
			return Triple.create(subj, pred, obj);
		} else {
			return null;
		}
	}
}
