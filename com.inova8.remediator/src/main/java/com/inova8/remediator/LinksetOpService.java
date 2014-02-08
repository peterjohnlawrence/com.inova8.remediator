package com.inova8.remediator;

import com.hp.hpl.jena.sparql.algebra.op.OpService;

public class LinksetOpService {

	private Linkset linkset;
	private OpService opService;

	public LinksetOpService(Linkset linkset, OpService opService) {
		super();
		this.linkset = linkset;
		this.opService = opService;
	}

	public Linkset getLinkset() {
		return linkset;
	}

	public void setLinkset(Linkset linkset) {
		this.linkset = linkset;
	}

	public OpService getOpService() {
		return opService;
	}

	public void setOpService(OpService opService) {
		this.opService = opService;
	}

	@Override
	public String toString() {
		return "LinksetOpService [linkset=" + linkset + "]";
	}
	
}
