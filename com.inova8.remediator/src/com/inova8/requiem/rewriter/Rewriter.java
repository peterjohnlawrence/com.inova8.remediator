package com.inova8.requiem.rewriter;
import java.util.ArrayList;

import org.apache.jena.atlas.logging.Log;

import com.inova8.requiem.optimizer.Optimizer;


public class Rewriter {

	private static final TermFactory m_termFactory = new TermFactory(); 
	public static final Saturator m_saturator = new Saturator(m_termFactory);
	public static final Optimizer m_optimizer = new Optimizer(m_termFactory);
	
	public ArrayList<Clause> rewrite(ArrayList<Clause> clauses, String mode) throws Exception{
		
		ArrayList<Clause> saturation = new ArrayList<Clause>();
		ArrayList<Clause> rewriting = new ArrayList<Clause>();
			
	    Log.info(this,"Ontology and query loaded ("+ clauses.size() +" clauses)");
	    	
    	//Prune irrelevant clauses based on the dependency graph from the query
    	clauses = m_optimizer.pruneWithDependencyGraph("Q", clauses);
    	
    	//Saturation
    	
    	saturation = m_saturator.saturate(clauses, new SelectionFunctionSaturate(), mode);
    	
    	Log.info(this,"Saturation completed ("+ saturation.size() +" clauses)");
    	
    	//Unfolding
    	if(mode.equals("N") || mode.equals("F")){
    		rewriting = m_saturator.unfoldNaively(saturation);
    	}
    	else{
    		rewriting = m_saturator.unfoldGreedily(saturation);
    	}
    	
    	Log.info(this,"Unfolding completed ("+ rewriting.size() +" clauses)");
    	
    	//Optimize
    	if(!mode.equals("N")){
	    	//Prune irrelevant clauses based on the dependency graph from the query
		    rewriting = m_optimizer.pruneWithDependencyGraph("Q", rewriting);
		    //Prune clauses containing AUX EDB predicates
	    	rewriting = m_optimizer.pruneAUX(rewriting);
			//Prune subsumed clauses
		    rewriting = m_optimizer.querySubsumptionCheck(rewriting);
		    //replace each query with its condensation
		    rewriting = m_optimizer.condensate(rewriting);
    	}
    			    			    	
    	Log.info(this,"Pruning completed ("+ rewriting.size() +" clauses)");
    	
    	return rewriting;	    
	}	
}
