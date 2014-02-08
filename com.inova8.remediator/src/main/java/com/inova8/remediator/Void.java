package com.inova8.remediator;

/* CVS $Id: $ */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.apache.jena.atlas.logging.Log;
import com.inova8.requiem.parser.ELHIOParser;
import com.inova8.requiem.rewriter.Clause;
import com.inova8.requiem.rewriter.Term;
import com.inova8.requiem.rewriter.TermFactory;
import com.inova8.workspace.Workspace;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.vocabulary.OWL;

class Void {
	private Workspace workspace;
	private OntModelSpec voidModelSpec;

	private OntModel voidModel;
	private OntModel vocabularyModel;
	private InfModel infModel;
	private Reasoner boundReasoner;
	private Datasets datasets = new Datasets();
	private Linksets linksets = new Linksets();
	private Boolean partitionStatisticsAvailable = false;
	
	private ArrayList<Clause> conjunctiveClauses = new ArrayList<Clause>();

	private static TermFactory termFactory = new TermFactory();
	private static final ELHIOParser parser = new ELHIOParser(termFactory);


	private String DS = "DS";
	private String datasetQuery = "PREFIX void: <" + NS + ">\n"
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			+ "SELECT ?dataset ?sparqlEndPoint ?uriSpace \n" + "WHERE  {\n"
			+ "    ?dataset rdf:type void:Dataset .\n"
			+ "    ?dataset void:sparqlEndpoint ?sparqlEndPoint .\n"
			+ "    OPTIONAL {\n"
			+ "        ?dataset void:uriSpace ?uriSpace .\n" + "    }\n"
			+ "    FILTER ( ! EXISTS {?c void:classPartition ?dataset } ) .\n"
			+ "}\n";
	private static String linksetQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			+ "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
			+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
			+ "PREFIX void: <http://rdfs.org/ns/void#>\n"
			+ "SELECT ?linkset ?sparqlEndPoint ?linkPredicate ?subjectsDataset ?subjectsTarget ?subjectsClass ?objectsDataset ?objectsTarget ?objectsClass\n"
			+ "WHERE {\n"
			+ "?linkset a void:Linkset .\n"
			+ "OPTIONAL{?linkset void:sparqlEndpoint ?sparqlEndPoint .}\n"
			+ "OPTIONAL{?linkset void:linkPredicate ?alternateLinkPredicate .\n"
			+ "}\n"
			+ "BIND(coalesce(?alternateLinkPredicate, owl:sameAs) as ?linkPredicate)\n"
			+ "OPTIONAL{?linkset void:subjectsTarget ?subjectsTarget .\n"
			+ "?subjectsTarget  a void:Dataset .\n"
			+ "?subjectsDataset void:classPartition ?subjectsTarget .\n"
			+ "?subjectsTarget void:class ?subjectsClass .\n"
			+ "?linkset void:objectsTarget ?objectsTarget .\n"
			+ "?objectsTarget a void:Dataset .\n"
			+ "?objectsDataset void:classPartition ?objectsTarget .\n"
			+ "?objectsTarget void:class ?objectsClass .\n" + "}}\n";

	/**
	 * Instantiates a new void.
	 * 
	 * @param voidModel
	 *            the void model
	 * @param voidURI
	 *            the void uri
	 */
	Void(OntModel voidModel, String voidURI, Boolean gatherStatistics) {
		this.voidModel = voidModel;
		buildVoidModel(voidURI, gatherStatistics);
	}

	Void(Workspace workspace, String voidURI, Boolean gatherStatistics) {
		this.workspace = workspace;
		voidModelSpec = new OntModelSpec(OntModelSpec.OWL_MEM);
		voidModelSpec.setDocumentManager(this.workspace.getDocumentManager());
		voidModelSpec.getDocumentManager().setProcessImports(true);
		voidModel = ModelFactory.createOntologyModel(voidModelSpec);
		buildVoidModel(voidURI, gatherStatistics);
		try {
			conjunctiveClauses = parser.getClauses(this.writeVocabularyModel(), this.getWorkspace().getOWLOntologyURIMapper());
		} catch (Exception e) {
			Log.debug(this,
					"Failed to parse clauses: " + e.getMessage());
		}

	}

	private void buildVoidModel(String voidURI, Boolean gatherStatistics) {
		this.voidModel.read(voidURI);
		// TODO remove
		//this.voidModel.writeAll(System.out, "RDF/XML-ABBREV");
		loadVoidDatasets();
		loadVoidLinksets();
		buildVocabularyModel();
		loadPartitions();
		if (gatherStatistics)
			updatePartitionStatistics();
		buildInferenceModel();
	}

	private void buildInferenceModel() {
		// TODO Auto-generated method stub

		OntModel vocabularyModel = this.getVocabularyModel();
		vocabularyModel.loadImports();
		//TODO which reasoner should we use as default. RDFSSimple is by far the fastest
		//TODO Reasoner boundReasoner = ReasonerRegistry.getOWLMicroReasoner();
		this.boundReasoner = ReasonerRegistry.getRDFSSimpleReasoner();
		this.boundReasoner = this.boundReasoner.bindSchema(this.vocabularyModel);
	}

	private void loadPartitions() {
		datasets.updatePartitions(voidModel);
	}
	public ArrayList<Clause> getConjunctiveClauses() {
		return conjunctiveClauses;
	}
	private void buildVocabularyModel() {
		OntModelSpec vocabularyModelSpec = new OntModelSpec(
				OntModelSpec.OWL_MEM);
		vocabularyModelSpec.setDocumentManager(voidModel.getDocumentManager());
		vocabularyModelSpec.getDocumentManager().setProcessImports(false);

		vocabularyModel = ModelFactory.createOntologyModel(vocabularyModelSpec);
		Ontology vocabularyOntology = vocabularyModel
				.createOntology("http://inova8.com/consolidatedmodel");

		for (Dataset dataset : datasets) {
			for (OntResource vocabulary : dataset.getVocabularies()) {
				try {
					vocabularyOntology.addProperty(OWL.imports, vocabulary);
				} catch (Exception e) {
					Log.debug(Void.class,
							"Failed to locate dataset vocabulary: "
									+ vocabulary + "  " + e.getMessage());
				}
			}
		}
		for (Linkset linkset : linksets) {
			for (OntResource vocabulary : linkset.getVocabularies()) {
				try {
					vocabularyOntology.addProperty(OWL.imports, vocabulary);
				} catch (Exception e) {
					Log.debug(Void.class,
							"Failed to locate linkset vocabulary: "
									+ vocabulary + "  " + e.getMessage());
				}
			}
		}
	}

	/**
	 * Gets the datasets.
	 * 
	 * @return the datasets
	 */
	public Datasets getDatasets() {
		return datasets;
	}

	/**
	 * Gets the linksets.
	 * 
	 * @return the linksets
	 */
	public Linksets getLinksets() {
		return linksets;
	}

	/**
	 * Vocabulary model: A model assembled from the: dataset vocabularies, which
	 * contain the description or schema of each model referred to in the
	 * dataset. linkset vocabularies, which contain the mapping between the two
	 * models referred to in the linkset.
	 * 
	 * @return the vocabulary ontology model
	 */
	public OntModel getVocabularyModel() {

		return vocabularyModel;
	}

	/**
	 * Gets the void model.
	 * 
	 * @return the void model
	 */
	public OntModel getVoidModel() {

		return this.voidModel;
	}

	public Workspace getWorkspace() {
		return workspace;
	}

	private void loadVoidDatasets() {

		Query query = QueryFactory.create(datasetQuery);
		QueryExecution qexec = QueryExecutionFactory.create(query, voidModel);

		try {
			ResultSet results = qexec.execSelect();

			for (int index = 1; results.hasNext(); index++) {

				QuerySolution soln = results.nextSolution();
				OntResource dataset = soln.getResource("dataset").as(
						OntResource.class);
				OntResource sparqlEndPoint = soln.getResource("sparqlEndPoint")
						.as(OntResource.class);
				OntResource uriSpace = (soln.getResource("uriSpace") != null) ? soln
						.getResource("uriSpace").as(OntResource.class) : null;
				String prefix = DS + index;
				datasets.add(new Dataset(voidModel, dataset, sparqlEndPoint,
						uriSpace, prefix));
			}
		} catch (Exception e) {
			Log.debug(Void.class, "Failed datasetQuery");
			Log.debug(Void.class, e.getStackTrace().toString());
		} finally {
			qexec.close();
		}
	}

	private void loadVoidLinksets() {

		Query query = QueryFactory.create(linksetQuery);
		QueryExecution qexec = QueryExecutionFactory.create(query, voidModel);

		try {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				OntResource linkset = soln.getResource("linkset").as(
						OntResource.class);
				OntResource sparqlEndPoint = (soln
						.getResource("sparqlEndPoint") != null) ? soln
						.getResource("sparqlEndPoint").as(OntResource.class)
						: null;
				OntResource linkPredicate = soln.getResource("linkPredicate") != null ? soln
						.getResource("linkPredicate").as(OntResource.class)
						: null;
				OntResource subjectsDataset = soln
						.getResource("subjectsDataset") != null ? soln
						.getResource("subjectsDataset").as(OntResource.class)
						: null;
				OntResource subjectsTarget = soln.getResource("subjectsTarget") != null ? soln
						.getResource("subjectsTarget").as(OntResource.class)
						: null;
				OntResource subjectsClass = soln.getResource("subjectsClass") != null ? soln
						.getResource("subjectsClass").as(OntResource.class)
						: null;
				OntResource objectsDataset = soln.getResource("objectsDataset") != null ? soln
						.getResource("objectsDataset").as(OntResource.class)
						: null;
				OntResource objectsTarget = soln.getResource("objectsTarget") != null ? soln
						.getResource("objectsTarget").as(OntResource.class)
						: null;
				OntResource objectsClass = soln.getResource("objectsClass") != null ? soln
						.getResource("objectsClass").as(OntResource.class)
						: null;
				linksets.add(new Linkset(voidModel, this.datasets, linkset, sparqlEndPoint,linkPredicate,
						subjectsDataset,subjectsTarget, subjectsClass ,objectsDataset, objectsTarget,objectsClass));
			}
		} catch (Exception e) {
			Log.debug(Void.class, "Failed linksetQuery");
			Log.debug(Void.class, e.getStackTrace().toString());
		} finally {
			qexec.close();
		}
	}

	public void updatePartitionStatistics() {
		datasets.updatePartitionStatistics();
		partitionStatisticsAvailable = true;
	}

	public Boolean getPartitionStatisticsAvailable() {
		return partitionStatisticsAvailable;
	}

	// TODO is this really required when the ability to write is inherited from
	// OntModel
	public ByteArrayInputStream writeVocabularyModel() {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		vocabularyModel.write(out, "RDF/XML-ABBREV");
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		return in;
	}
	public void InferVariableClasses(QueryVars queryVars) {

		for (Dataset dataset : this.getDatasets()) {
			for (QueryClause clause : dataset.getQueryClauses()) {

				infModel = ModelFactory.createInfModel(this.boundReasoner,
						clause.getDataModel(queryVars));
				Property rdfType = infModel
						.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
				for (Term term : clause.getHead().getArguments()) {
					Resource nForce = infModel
							.getResource("http://inova8.com/variable#"
									+ queryVars.get(
											term.getMinVariableIndex())
											.getName());
					for (StmtIterator stmtIterator = this.infModel.listStatements(nForce,rdfType, (Resource)null); stmtIterator.hasNext();) {
						Statement stmt = stmtIterator.nextStatement();
						queryVars.get(term.getMinVariableIndex()).addVariableClass(stmt.getObject());
					}					
				}
			}
		}
	}
	/**
	 * <p>
	 * The RDF model that holds the vocabulary terms
	 * </p>
	 */
	private static Model m_model = ModelFactory.createDefaultModel();

	/**
	 * <p>
	 * The namespace of the vocabulary as a string
	 * </p>
	 * .
	 */
	static final String NS = "http://rdfs.org/ns/void#";

	;

	/**
	 * <p>
	 * The rdfs:Class that is the rdf:type of all entities in a class-based
	 * partition.
	 * </p>
	 */
	static final Property class_ = m_model
			.createProperty("http://rdfs.org/ns/void#class");;

	/**
	 * <p>
	 * A subset of a void:Dataset that contains only the entities of a certain
	 * rdfs:Class.
	 * </p>
	 */
	static final Property classPartition = m_model
			.createProperty("http://rdfs.org/ns/void#classPartition");

	/**
	 * <p>
	 * The total number of distinct classes in a void:Dataset. In other words,
	 * the number of distinct resources occurring as objects of rdf:type triples
	 * in the dataset.
	 * </p>
	 */
	static final Property classes = m_model
			.createProperty("http://rdfs.org/ns/void#classes");

	

	/**
	 * <p>
	 * The total number of distinct objects in a void:Dataset. In other words,
	 * the number of distinct resources that occur in the object position of
	 * triples in the dataset. Literals are included in this count.
	 * </p>
	 */
	static final Property distinctObjects = m_model
			.createProperty("http://rdfs.org/ns/void#distinctObjects");

	/**
	 * <p>
	 * The total number of distinct subjects in a void:Dataset. In other words,
	 * the number of distinct resources that occur in the subject position of
	 * triples in the dataset.
	 * </p>
	 */
	static final Property distinctSubjects = m_model
			.createProperty("http://rdfs.org/ns/void#distinctSubjects");

	

	/**
	 * <p>
	 * The total number of entities that are described in a void:Dataset.
	 * </p>
	 */
	static final Property entities = m_model
			.createProperty("http://rdfs.org/ns/void#entities");
	/**
	 * <p>
	 * The total number of distinct properties in a void:Dataset. In other
	 * words, the number of distinct resources that occur in the predicate
	 * position of triples in the dataset.
	 * </p>
	 */
	static final Property properties = m_model
			.createProperty("http://rdfs.org/ns/void#properties");

	/**
	 * <p>
	 * The rdf:Property that is the predicate of all triples in a property-based
	 * partition.
	 * </p>
	 */
	static final Property property = m_model
			.createProperty("http://rdfs.org/ns/void#property");

	/**
	 * <p>
	 * A subset of a void:Dataset that contains only the triples of a certain
	 * rdf:Property.
	 * </p>
	 */
	static final Property propertyPartition = m_model
			.createProperty("http://rdfs.org/ns/void#propertyPartition");

	

	/**
	 * <p>
	 * The total number of triples contained in a void:Dataset.
	 * </p>
	 */
	static final Property triples = m_model
			.createProperty("http://rdfs.org/ns/void#triples");

	/**
	 * <p>
	 * The namespace of the vocabulary as a string
	 * </p>
	 * .
	 * 
	 * @return the uri
	 * @see #NS
	 */
	static String getURI() {
		return NS;
	}
}
