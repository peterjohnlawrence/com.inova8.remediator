package com.inova8.remediator;

import java.util.ArrayList;
import java.util.Collection;

class QueryPlan extends ArrayList<MergedJoin>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8712280733551958541L;

	public QueryPlan() {
		super();
	}

	public QueryPlan(Collection<? extends MergedJoin> c) {
		super(c);
	}

	public QueryPlan(int initialCapacity) {
		super(initialCapacity);
	}


	@Override
	public String toString() {
		String output ="QueryPlan [";
		int loop =0;
		for (MergedJoin mergedJoin: this)
		{
			loop++;
			output += "\nStep=" + loop + "\n";
			output +=mergedJoin.toString();
		}	
		
		return output + "\n]";
	}
	
}
