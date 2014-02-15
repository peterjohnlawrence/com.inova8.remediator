package com.inova8.requiem.rewriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class Resolution{
	private Saturator m_saturator;

    public Resolution(Saturator saturator){
    	this.m_saturator = saturator;
    }
    
    public ArrayList<Clause> generateResolvents(Clause givenClause, Collection<Clause> workedOffClauses) {
    	
        ArrayList<Clause> result = new ArrayList<Clause>();
        
        //If the head of givenClause is selected, then givenClause is the side premise
        if(givenClause.m_selectedHead){
        	for (Clause workedOffClause : workedOffClauses) {
        		for (int mainPremiseIndex = 0; mainPremiseIndex < workedOffClause.getBody().length; mainPremiseIndex++){
        			//If the body atom B of workedOffClause is selected and B has the same arity/name as the head atom of givenClause, then try to resolve
        			//System.out.println("Given: " + givenClause.getHead().getName());
        			//System.out.println(workedOffClause.getBody()[mainPremiseIndex].getName());
        			if((workedOffClause.m_selectedBody[mainPremiseIndex]) && 
        			   (workedOffClause.getBody()[mainPremiseIndex].getArity() == givenClause.getHead().getArity()) && 
        			   (workedOffClause.getBody()[mainPremiseIndex].getName().equals(givenClause.getHead().getName()))){
        				Clause resolvent = resolve(workedOffClause, givenClause, mainPremiseIndex);
                        if (resolvent != null){
                            result.add(resolvent);
                        }
        			}
        		}
        	}
        }
        
        //If a body atom of givenClause is selected, then givenClause is the main premise
        for (int mainPremiseIndex = 0; mainPremiseIndex < givenClause.getBody().length; mainPremiseIndex++){
        	if(givenClause.m_selectedBody[mainPremiseIndex]){
        		for (Clause workedOffClause : workedOffClauses) {
        			//If the head atom B of workedOffClause is selected and B has the same arity/name as the body atom of givenClause, then try to resolve
        			if(workedOffClause.m_selectedHead &&
        	           workedOffClause.getHead().getArity() == givenClause.getBody()[mainPremiseIndex].getArity() && 
        	           workedOffClause.getHead().getName().equals(givenClause.getBody()[mainPremiseIndex].getName())){
        				Clause resolvent = resolve(givenClause, workedOffClause, mainPremiseIndex);
    					if (resolvent != null){
    						result.add(resolvent);
    					}
        			}
        		}
        	}
        }
        
        return result;
    }
    
    private Clause resolve(Clause mainPremise, Clause sidePremise, int mainPremiseAtomIndex) {
    	
    	//Rename the variables of the side premise
    	int numberOfVariablesMainPremise = mainPremise.getVariables().size();
    	Clause sidePremiseRenamed = sidePremise.renameVariables(this.m_saturator.getTermFactory(), numberOfVariablesMainPremise);
        
    	Term mainAtom = mainPremise.getBody()[mainPremiseAtomIndex];
        Term sideAtom = sidePremiseRenamed.getHead();
        
        Substitution unifier = Substitution.mostGeneralUnifier(mainAtom, sideAtom, this.m_saturator.getTermFactory());
        
        if (unifier == null)
            return null;        
        Set<Term> newBody = new LinkedHashSet<Term>();
        //Copy the atoms from the main premise
        for (int index=0; index < mainPremise.getBody().length; index++){
            if (index != mainPremiseAtomIndex){
            	Term newBodyAtom = mainPremise.getBody()[index].apply(unifier, this.m_saturator.getTermFactory());
            	((FunctionalTerm)newBodyAtom).originIndex=((FunctionalTerm)mainPremise.getBody()[index]).originIndex;
                newBody.add(newBodyAtom);
            }
        }
        //Copy the atoms from the side premise
        for (int index=0; index < sidePremiseRenamed.getBody().length;index++){
        	Term newBodyAtom =sidePremiseRenamed.getBody()[index].apply(unifier, this.m_saturator.getTermFactory());
        	((FunctionalTerm)newBodyAtom).originIndex=((FunctionalTerm)mainAtom).originIndex;
            newBody.add(newBodyAtom);
        }
            
        //New body and head
        Term[] body = new Term[newBody.size()];
        newBody.toArray(body);
        Term head = mainPremise.getHead().apply(unifier, this.m_saturator.getTermFactory());
           
        Clause resolvent = new Clause(body, head);	
        
        //Rename variables in resolvent
        ArrayList<Variable> variablesResolvent = resolvent.getVariables();
        HashMap<Variable,Integer> variableMapping = new HashMap<Variable,Integer>(); 
        for(int i=0; i < variablesResolvent.size(); i++){
        	variableMapping.put(variablesResolvent.get(i),i);
        }
        Clause resolventRenamed = resolvent.renameVariables(this.m_saturator.getTermFactory(), variableMapping);
        
        return resolventRenamed;
    }   
}