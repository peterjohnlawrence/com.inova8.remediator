package com.inova8.requiem.parser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import com.inova8.requiem.rewriter.Clause;
import com.inova8.requiem.rewriter.TermFactory;
import org.semanticweb.HermiT.Reasoner.Configuration;
import org.semanticweb.HermiT.owlapi.structural.OwlNormalization;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLOntologySetProvider;
import org.semanticweb.owl.model.OWLOntologyURIMapper;
import org.semanticweb.owl.util.OWLOntologyMerger;
import org.semanticweb.owl.vocab.NamespaceOWLOntologyFormat;
import org.semanticweb.owl.io.StreamInputSource;


/**
 * Converts an ontology and a query into a set of clauses.
 */

public class ELHIOParser {
	
	private TermFactory m_termFactory;
	private OWLOntology ontology;
	
	public ELHIOParser(TermFactory termFactory){
		this.m_termFactory = termFactory;
	}
	
	/**
	 * Reads an ontology from the file specified by 'path' and 
	 * returns a list of clauses representing the ontology.
	 * @param path the ontology to be converted.
	 * @return a set of clauses representing the ontology.
	 * @throws OWLException
	 */
//	public ArrayList<Clause> getClauses(String path) throws Exception
//	{
//		System.out.println(path);
//		//Create ontology manager and load ontology (OWL API)
//		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();		
//		URI physicalURI = URI.create(path);
//        ontology = manager.loadOntologyFromPhysicalURI(physicalURI);
//         
//        //Normalize ontology (HermiT)
//        OwlNormalization normalization = new OwlNormalization(manager.getOWLDataFactory());
//        normalization.processOntology(new Configuration(), ontology);
//        
//        //TODO fixes to get nsm
//        NamespaceOWLOntologyFormat namespaceOWLOntologyFormat = (NamespaceOWLOntologyFormat)(manager.getOntologyFormat(ontology));
//
//        //Clausify ontology
//        ELHIOClausifier clausification = new ELHIOClausifier(this.m_termFactory, manager,namespaceOWLOntologyFormat);
//        ArrayList<Clause> clauses = clausification.getClauses(normalization);
//        
//        //Remove ontology from the ontology manager
//        manager.removeOntology(ontology.getURI());
//        
//		return clauses;
//	}
	public ArrayList<Clause> getClauses(InputStream is,OWLOntologyURIMapper owlOntologyURIMapper) throws Exception
	{
		//Create ontology manager and load ontology (OWL API)
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();		
		manager.addURIMapper(owlOntologyURIMapper);
		//Load the root ontology containing the imports
		ontology = manager.loadOntology(new StreamInputSource(is));
		//Merge the root ontology with the imports into http://consolidated 
		OWLOntologyMerger owlOntologyMerger = new OWLOntologyMerger((OWLOntologySetProvider)manager);
		ontology = owlOntologyMerger.createMergedOntology(manager, new URI("http://inovoa8.com/consolidated"));
        //Normalize ontology (HermiT)
        OwlNormalization normalization = new OwlNormalization(manager.getOWLDataFactory());
        normalization.processOntology(new Configuration(), ontology);
        
        //TODO fixes to get nsm
        NamespaceOWLOntologyFormat namespaceOWLOntologyFormat = (NamespaceOWLOntologyFormat)(manager.getOntologyFormat(ontology));

        //Clausify ontology
        ELHIOClausifier clausification = new ELHIOClausifier(this.m_termFactory, manager,namespaceOWLOntologyFormat);
        ArrayList<Clause> clauses = clausification.getClauses(normalization);
        
        //Remove ontology from the ontology manager
        manager.removeOntology(ontology.getURI());
        
		return clauses;
	}	
	
	public int getNumberOfAxioms(){
	    return ontology == null? null: ontology.getAxioms().size();
	}
	
	public int getNumberOfConcepts(){
		return ontology == null? null: ontology.getReferencedClasses().size();
	}
	
	public int getNumberOfRoles(){
		return ontology == null? null: ontology.getReferencedObjectProperties().size();
	}

}
