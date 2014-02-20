package com.inova8.remediator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;

import com.inova8.workspace.RemediatorWorkspace;

public class RemediatorWorkspaceFactory {

	private  RemediatorWorkspaceFactory() {
	}

	static RemediatorWorkspace create(File rootDirectory, boolean recursive, Set<String> extensions) throws FileNotFoundException {
		
		return new RemediatorWorkspace(rootDirectory, recursive, extensions);

	}
	static RemediatorWorkspace create(File rootDirectory, boolean recursive) throws FileNotFoundException {
		
		return new RemediatorWorkspace(rootDirectory, recursive);

	}
}
