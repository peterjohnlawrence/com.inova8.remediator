package com.inova8.remediator;

import java.util.ArrayList;
import java.util.Collection;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;

public class Datasets extends ArrayList<Dataset> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7551012751076066818L;

	public Datasets() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Datasets(Collection<? extends Dataset> c) {
		super(c);
		// TODO Auto-generated constructor stub
	}

	public void updatePartitionStatistics() {
		for (Dataset dataset : this) {
			dataset.updatePartitionStatistics();
		}
		
	}
	public void updatePartitions(OntModel voidModel) {
		for (Dataset dataset : this) {
			dataset.updatePartitions(voidModel);
	
		}
	}
	public Dataset getDataset(OntResource datasetNode){
		if (datasetNode==null ) return (Dataset)null;
		
		for (Dataset dataset : this) {
			if(dataset.getDataset().equals(datasetNode)) return dataset;
	
		}
		return (Dataset)null;
	}


}
