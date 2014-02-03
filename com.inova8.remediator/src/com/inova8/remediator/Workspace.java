package com.inova8.remediator;

import java.io.File;
import java.net.URI;
import java.util.Set;

import org.semanticweb.owl.model.OWLOntologyURIMapper;
import org.semanticweb.owl.util.AutoURIMapper;

import com.hp.hpl.jena.ontology.OntDocumentManager;

public class Workspace {
	private OntDocumentManager documentManager;

	private AutoURIMapper autoURIMapper;

	public Workspace(File rootDirectory, boolean recursive, Set<String> extensions) {
		super();
		autoURIMapper = new AutoURIMapper(rootDirectory, recursive);
		autoURIMapper.setFileExtensions(extensions);
		documentManager= new OntDocumentManager(); 
		autoURIMapper.update();
		updateDocumentManager();

	}

	public Workspace(File rootDirectory, boolean recursive) {
		super();
		autoURIMapper = new AutoURIMapper(rootDirectory, recursive);
		autoURIMapper.update();
		documentManager= new OntDocumentManager(); 
		updateDocumentManager();
	}

	private void updateDocumentManager() {
		for (URI ontologyURI : autoURIMapper.getOntologyURIs()) {
			documentManager.addAltEntry(ontologyURI.toString(),
					autoURIMapper.getPhysicalURI(ontologyURI).toString());
		}
	}

	URI getPhysicalURI(java.net.URI ontologyURI) {
		
		return autoURIMapper.getPhysicalURI(ontologyURI);
	}

	Set<String> getFileExtensions() {
		return autoURIMapper.getFileExtensions();
	}

	Set<URI> getOntologyURIs() {
		return autoURIMapper.getOntologyURIs();
	}

	public OntDocumentManager getDocumentManager() {
		return documentManager;
	}

	public OWLOntologyURIMapper getOWLOntologyURIMapper() {
		return autoURIMapper;
	}

	@Override
	public String toString() {
		return autoURIMapper.toString();
	}
}
