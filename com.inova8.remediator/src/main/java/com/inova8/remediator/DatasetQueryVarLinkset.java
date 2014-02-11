package com.inova8.remediator;

public class DatasetQueryVarLinkset {

	private Dataset dataset;
	private QueryVar queryVar;
	private Linkset linkset;

	public DatasetQueryVarLinkset(Dataset dataset, QueryVar queryVar, Linkset linkset) {
		super();
		this.dataset = dataset;
		this.queryVar = queryVar;
		this.linkset = linkset;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DatasetQueryVarLinkset other = (DatasetQueryVarLinkset) obj;
		if (dataset == null) {
			if (other.dataset != null)
				return false;
		} else if (!dataset.equals(other.dataset))
			return false;
		if (linkset == null) {
			if (other.linkset != null)
				return false;
		} else if (!linkset.equals(other.linkset))
			return false;
		if (queryVar == null) {
			if (other.queryVar != null)
				return false;
		} else if (!queryVar.equals(other.queryVar))
			return false;
		return true;
	}
	public Dataset getDataset() {
		return dataset;
	}
	public Linkset getLinkset() {
		return linkset;
	}

	public QueryVar getQueryVar() {
		return queryVar;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataset == null) ? 0 : dataset.hashCode());
		result = prime * result + ((linkset == null) ? 0 : linkset.hashCode());
		result = prime * result + ((queryVar == null) ? 0 : queryVar.hashCode());
		return result;
	}
	@Override
	public String toString() {
		return "DatasetQueryVarLinkset [dataset=" + dataset + ", queryVar=" + queryVar + ", linkset=" + linkset + "]";
	}
}
