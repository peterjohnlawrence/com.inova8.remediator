package com.inova8.remediator;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import com.inova8.workspace.RemediatorWorkspace;

public class RemediatorVoidFactory {

	private RemediatorVoidFactory() {

	}
    static public Void create(RemediatorWorkspace workspace, String voidURI, Boolean gatherStatistics) throws URISyntaxException{
    	return new Void(workspace,voidURI,gatherStatistics);
    }
    static public Void create(File rootDirectory, String voidURI, Boolean gatherStatistics) throws URISyntaxException, FileNotFoundException{
    	RemediatorWorkspace workspace = new RemediatorWorkspace(rootDirectory, true);
    	return new Void(workspace,voidURI,gatherStatistics);
    }
}
