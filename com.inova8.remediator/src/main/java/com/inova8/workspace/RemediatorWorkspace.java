package com.inova8.workspace;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.jena.atlas.logging.Log;
import org.semanticweb.owl.model.OWLOntologyURIMapper;
import org.semanticweb.owl.util.AutoURIMapper;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntDocumentManager.ReadFailureHandler;
import com.hp.hpl.jena.rdf.model.Model;

public class RemediatorWorkspace {
	   class FileImportNotFound implements ReadFailureHandler {

	        public void handleFailedRead(String url, Model model, Exception e) {
	            Log.warn(this, "Missing import: "+url );
	        }
	    }
	private OntDocumentManager documentManager;

	private AutoURIMapper autoURIMapper;

	public RemediatorWorkspace(File rootDirectory, boolean recursive, Set<String> extensions) throws FileNotFoundException {
		super();
		initialize(rootDirectory,  recursive,  extensions);
	}

	public RemediatorWorkspace(File rootDirectory, boolean recursive) throws FileNotFoundException {
		super();
		Set<String> extensions = new HashSet<String>(Arrays.asList(new String[]{ "rdf", "owl","xml","ttl","trg","nt","nq" }));
		initialize(rootDirectory,  recursive,  extensions);
	}

	private void initialize(File rootDirectory, boolean recursive,
			Set<String> extensions) throws FileNotFoundException {
		if (rootDirectory.isDirectory()) {
			autoURIMapper = new AutoURIMapper(rootDirectory, recursive);
			if (extensions != null)
				autoURIMapper.setFileExtensions(extensions);
			documentManager = new OntDocumentManager();
			documentManager.setReadFailureHandler(new FileImportNotFound());
			autoURIMapper.update();
			updateDocumentManager();
		} else {
			throw new FileNotFoundException(rootDirectory.toString());
		}
	}

	private void updateDocumentManager() {
		for (URI ontologyURI : autoURIMapper.getOntologyURIs()) {
			documentManager.addAltEntry(ontologyURI.toString(),
					autoURIMapper.getPhysicalURI(ontologyURI).toString());
		}
	}

	public URI getPhysicalURI(java.net.URI ontologyURI) {
		
		return autoURIMapper.getPhysicalURI(ontologyURI);
	}

	public Set<String> getFileExtensions() {
		return autoURIMapper.getFileExtensions();
	}

	public Set<URI> getOntologyURIs() {
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
