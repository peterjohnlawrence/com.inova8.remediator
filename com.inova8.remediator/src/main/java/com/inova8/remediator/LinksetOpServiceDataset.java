package com.inova8.remediator;

public class LinksetOpServiceDataset {
	private LinksetOpService linksetOpService;

	private DatasetQueryVarLinkset otherDatasetQueryVarLinkset;

	public LinksetOpServiceDataset(LinksetOpService linksetOpService, DatasetQueryVarLinkset otherDatasetQueryVar) {
		super();
		this.linksetOpService = linksetOpService;
		this.otherDatasetQueryVarLinkset = otherDatasetQueryVar;
	}

	public LinksetOpService getLinksetOpService() {
		return linksetOpService;
	}

	public DatasetQueryVarLinkset getOtherDatasetQueryVarLinkset() {
		return otherDatasetQueryVarLinkset;
	}

	@Override
	public String toString() {
		return "LinksetOpServiceDataset [linksetOpService=" + linksetOpService + ", otherDatasetQueryVar="
				+ otherDatasetQueryVarLinkset + "]";
	}
	
}
