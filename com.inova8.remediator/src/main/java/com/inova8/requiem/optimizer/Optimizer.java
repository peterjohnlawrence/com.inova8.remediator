package com.inova8.requiem.optimizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.inova8.requiem.rewriter.Clause;
import com.inova8.requiem.rewriter.Substitution;
import com.inova8.requiem.rewriter.Term;
import com.inova8.requiem.rewriter.TermFactory;
import com.inova8.requiem.rewriter.Variable;

public class Optimizer {
	
	private TermFactory m_termFactory;
	
	public Optimizer(com.inova8.requiem.rewriter.TermFactory mTermfactory){
		this.m_termFactory = mTermfactory;
	}
	
	/**
	 * Prunes a set of clauses based on the dependency graph from a specific predicate
	 * @return
	 */
	public ArrayList<Clause> pruneWithDependencyGraph(String pred, ArrayList<Clause> clauses){
		ArrayList<Clause> result = new ArrayList<Clause>();
		
		ArrayList<String> predicatesToProcess = new ArrayList<String>();
		HashSet<String> reachablePredicates = new HashSet<String>();
		
		predicatesToProcess.add(pred);
		while(predicatesToProcess.size() > 0){
			String p = predicatesToProcess.get(0);
			predicatesToProcess.remove(p);
			
			if(!reachablePredicates.contains(p)){
				reachablePredicates.add(p);
				predicatesToProcess.addAll(getDirectReachablePredicates(p, clauses));
			}
		}
		
		//Keep clauses with reachable heads only
		for(Clause c : clauses){
			if(reachablePredicates.contains(c.getHead().getName())){ 
				result.add(c);
			}
		}
		return result;
	}
	
	private ArrayList<String> getDirectReachablePredicates(String p, ArrayList<Clause> clauses){
		ArrayList<String> result = new ArrayList<String>();
		
		for(Clause c : clauses){
			if(p.equals(c.getHead().getName())){
				for(Term t: c.getBody()){
					result.add(t.getName());
				}
			}
		}
		return result;
	}
	
	/**
	 * Performs a query subsumption test
	 * @return
	 */
	public ArrayList<Clause> querySubsumptionCheck(ArrayList<Clause> clauses) {
		ArrayList<Clause> result = new ArrayList<Clause>();
		
		boolean c1IsSubsumed;
		
		//Identify equivalent clauses
		for (Clause c1 : clauses) {
			for (Clause c2 : clauses) {
				if(!c1.equals(c2)) {
					if(c2.subsumes(c1,false) && c1.subsumes(c2,false)){
						if(c1.m_canonicalRepresentation.length() < c2.m_canonicalRepresentation.length()){
							c2.toBeIgnored = true;
						}else{
							c1.toBeIgnored = true;
						}
					}
				}
			}
		}
		
		//Prune subsumed clauses
		for (Clause c1 : clauses) {
			if(!c1.toBeIgnored){
				c1IsSubsumed = false;
				for (Clause c2 : clauses) {
					if(!c2.toBeIgnored){
					if(!c1.equals(c2)) {
						if(c2.subsumes(c1,false)){
							c1IsSubsumed = true;
							break;
						}
					}
					}
				}
				if(!c1IsSubsumed){
					result.add(c1);
				}
			}
		}
		
		return result;
	}
	
	public ArrayList<Clause> pruneAUX(ArrayList<Clause> clauses){
		HashSet<String> IDBPredicates = new HashSet<String>();
		HashSet<String> EDBPredicates = new HashSet<String>();
		ArrayList<Clause> result = new ArrayList<Clause>();
		
		// Compute the IDB predicates
    	for(Clause c : clauses){
			IDBPredicates.add(c.getHead().getName());
		}
    	
    	// Compute the EDB predicates
    	for(Clause c : clauses){
    		for(Term t: c.getBody())
    			if(!IDBPredicates.contains(t.getName())){
    				EDBPredicates.add(t.getName());
    			}
		}
    	
    	//Prune clauses with AUX EDB predicates
		for(Clause c : clauses){
			boolean isToBePruned = false;
			for(Term t: c.getBody()){
				if(t.getName().contains("AUX$")){
					if(EDBPredicates.contains(t.getName())){
						isToBePruned = true;
						break;
					}
				}
				try{
					//Auxiliary symbols introduced in the normalization (HermiT)
					Integer.parseInt(t.getName());
					if(EDBPredicates.contains(t.getName())){
						isToBePruned = true;
						break;
					}
				}
				catch(NumberFormatException e){}
			}
			
			if(!isToBePruned){
				result.add(c);
			}
		}
    	
		return result;
	}
	
	public ArrayList<Clause> condensate(ArrayList<Clause> clauses){
		
		ArrayList<Clause> result = new ArrayList<Clause>();
		
		for(Clause c:clauses){
			result.add(condensate(c));
		}
		
		return result;
	}
	
	private Clause condensate(Clause clause){
		
		ArrayList<Clause> unprocessed = new ArrayList<Clause>();
		ArrayList<Clause> condensations = new ArrayList<Clause>();
		
		unprocessed.add(clause);
		
		while(!unprocessed.isEmpty()){
			Clause givenClause = unprocessed.remove(0);
			condensations.add(givenClause);
			for(int i = 0; i < givenClause.getBody().length - 1; i++){
				for(int j = i+1; j < givenClause.getBody().length; j++){
					Substitution unifier = Substitution.mostGeneralUnifier(givenClause.getBody()[i], givenClause.getBody()[j], m_termFactory);
					if (unifier != null){
						Clause newQuery = getNewQuery(unifier, givenClause);
						
						boolean isRedundant = false;
						for(Clause unprocessedClause: unprocessed){
							if(unprocessedClause.isEquivalentUpToVariableRenaming(newQuery)){
								isRedundant = true;
							}					
						}
						
						if(!isRedundant){
							for(Clause workedOffClause: condensations){
								if(workedOffClause.isEquivalentUpToVariableRenaming(newQuery)){
									isRedundant = true;
								}
							}
						}
						
						if(!isRedundant){
							unprocessed.add(newQuery);
						}
	                }
				}
			}
		}
		
		Collections.sort(condensations, new Comparator<Clause>(){
			public int compare(Clause c1, Clause c2){
			    return (new Integer(c1.getBody().length)).compareTo(new Integer(c2.getBody().length));
			}
		});

		for(Clause c: condensations){
			if(c.subsumes(clause, false)){
				return c;
			}
		}
		
		return clause;
	
	}
	
	private Clause getNewQuery(Substitution mgu, Clause clause){

        Set<Term> newBody = new LinkedHashSet<Term>();
        
        //Copy the atoms from the main premise
        for (int index=0; index < clause.getBody().length; index++){
            newBody.add(clause.getBody()[index].apply(mgu, this.m_termFactory));
        }

        //New body and head
        Term[] body = new Term[newBody.size()];
        newBody.toArray(body);
        Term head = clause.getHead().apply(mgu, this.m_termFactory);

        Clause newQuery = new Clause(body, head);

        //Rename variables in new query
        ArrayList<Variable> variablesNewQuery = newQuery.getVariables();
        HashMap<Variable,Integer> variableMapping = new
        HashMap<Variable,Integer>();

        for(int i=0; i < variablesNewQuery.size(); i++){
        	variableMapping.put(variablesNewQuery.get(i),i);
        }
        
        Clause newQueryRenamed = newQuery.renameVariables(this.m_termFactory, variableMapping);

        return newQueryRenamed;
	}
}
