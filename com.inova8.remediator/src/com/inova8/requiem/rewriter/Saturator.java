package com.inova8.requiem.rewriter;

import java.util.ArrayList;
import java.util.HashSet;

public class Saturator {
	private TermFactory m_termFactory;
	protected final Resolution m_resolution;
	protected ArrayList<String> m_clausesCanonicals;
	protected ArrayList<Clause> m_workedOffClauses, m_unprocessedClauses;

	public Saturator(TermFactory termFactory) {
		m_termFactory = termFactory;
		m_resolution = new Resolution(this);
		m_workedOffClauses = new ArrayList<Clause>();
		m_unprocessedClauses = new ArrayList<Clause>();
		m_clausesCanonicals = new ArrayList<String>();
	}
	
	public TermFactory getTermFactory(){
		return this.m_termFactory;
	}

	public ArrayList<Clause> unfoldNaively(ArrayList<Clause> clauses) {
		
		ArrayList<Clause> result = new ArrayList<Clause>();
		
		result = this.saturate(clauses, new SelectionFunctionUnfold(), "N");
		
		return result;
	}
	
	public ArrayList<Clause> unfoldGreedily(ArrayList<Clause> clauses) {
		HashSet<String> IDBPredicates = new HashSet<String>();
		ArrayList<Clause> result = new ArrayList<Clause>();
		
		for(Clause c: clauses){
    		result.add(c);
    	}
    	
    	// Compute the IDB predicates
		for(Clause c : clauses){
			IDBPredicates.add(c.getHead().getName()); 
		}
    	
		//int i = 0;
    	for(String p: IDBPredicates){
    		if(!p.equals("Q")){
    			//System.out.println(i++);
    			if(isUnfoldable(p, result)){
    				result = this.saturate(result, new SelectionFunctionUnfoldGreedy(p), "G");
    			}
    		}
    	}    	
		
		return result;
	}
	
	/**
	 * Decides if a predicate is unfoldable
	 */
	private boolean isUnfoldable(String p, ArrayList<Clause> clauses){
		for(Clause c: clauses){
			if(c.getHead().getName().equals(p)){
				if(c.getBody().length > 1){
					for(Term t: c.getBody()){
						if(t.getName().equals(p)){
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	
	// Saturates a set of clauses
	public ArrayList<Clause> saturate(ArrayList<Clause> clauses, SelectionFunction selectionFunction, String mode) {
		int inferences = 0;
		this.m_unprocessedClauses = clauses;
		this.m_clausesCanonicals = new ArrayList<String>();
		this.m_workedOffClauses = new ArrayList<Clause>();

		while (!m_unprocessedClauses.isEmpty()) {
			
			/*if(m_workedOffClauses.size()%100 == 0){
				System.out.println(m_workedOffClauses.size());
			}*/
			
			Clause givenClause = m_unprocessedClauses.remove(0);
			if(!givenClause.isTautology()){
				selectionFunction.selectAtoms(givenClause);
				
				m_workedOffClauses.add(givenClause);
				m_clausesCanonicals.add(givenClause.m_canonicalRepresentation);
				
				ArrayList<Clause> results = m_resolution.generateResolvents(givenClause, m_workedOffClauses);
				for (Clause resolvent : results) {
					inferences++;
					//System.out.println("Inferences: " + inferences);
					if ((!resolvent.isTautology()) && 
						(!isRedundant(resolvent, mode))) { //"N" for SC check, "F" for forward subsumption
						m_unprocessedClauses.add(resolvent);
						m_clausesCanonicals.add(resolvent.m_canonicalRepresentation);
					}
				}
			}
		}
		System.out.println("Inferences: " + inferences);
		return prune(selectionFunction);
	}

	/**
	 * Checks if a given clause is redundant. 
	 * If version == "N", then the method returns true if there is another previously generated clause that is equivalent up to variable renaming to the given clause
	 * If version != "N", then the method returns true if there is another previously generated clause that subsumes the given clause
	 */
	private boolean isRedundant(Clause clause, String version) {
		//Checks if a given clause is contained in m_clausesCanonicals based on its naive canonical representation
		if(m_clausesCanonicals.contains(clause.m_canonicalRepresentation)){
			return true;
		}
		else{
			for(Clause unprocessedClause: this.m_unprocessedClauses){
				if(version.equals("N")){
					if(unprocessedClause.isEquivalentUpToVariableRenaming(clause)){
						return true;
					}					
				}
				else{
					if(unprocessedClause.subsumes(clause, true)){
						return true;
					}
				}
			}
			for(Clause workedOffClause: this.m_workedOffClauses){
				if(version.equals("N")){
					if(workedOffClause.isEquivalentUpToVariableRenaming(clause)){
						return true;
					}					
				}
				else{
					if(workedOffClause.subsumes(clause, true)){
						return true;
					}
				}
			}
		}
		return false;
	}	
	
	/**
	 * Prunes the set of workedOffClauses based on a given selection function
	 * @param selectionFunction
	 * @return
	 */
	private ArrayList<Clause> prune(SelectionFunction selectionFunction) {
		ArrayList<Clause> result = new ArrayList<Clause>();
		this.m_clausesCanonicals.clear();

		for (Clause c : this.m_workedOffClauses) {
			if (!selectionFunction.isToBePruned(c)) {
				result.add(c);
				m_clausesCanonicals.add(c.m_canonicalRepresentation);
			}
		}
		return result;
	}
	
}