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
		// TODO Auto-generated constructor stub
	}

	public QueryPlan(Collection<? extends MergedJoin> c) {
		super(c);
		// TODO Auto-generated constructor stub
	}

	public QueryPlan(int initialCapacity) {
		super(initialCapacity);
		// TODO Auto-generated constructor stub
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
