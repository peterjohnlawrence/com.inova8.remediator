package com.inova8.requiem.parser;

import java.util.ArrayList;
import java.util.HashSet;

import com.inova8.requiem.rewriter.Clause;
import com.inova8.requiem.rewriter.Term;
import com.inova8.requiem.rewriter.TermFactory;
import org.semanticweb.HermiT.owlapi.structural.OwlNormalization;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividualAxiom;
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import org.semanticweb.owl.model.OWLObjectComplementOf;
import org.semanticweb.owl.model.OWLObjectOneOf;
import org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.vocab.NamespaceOWLOntologyFormat;



public class ELHIOClausifier {

	private TermFactory m_termFactory;
	private OWLOntologyManager manager;
	private int functionalSymbolIndex = 0;
	private int artificialConceptIndex = 0;
	private boolean isELHIO = false;
	private ArrayList<Clause> clauses;
	private HashSet<String> concepts;
	private HashSet<String> roles;
	private NamespaceOWLOntologyFormat namespaceOWLOntologyFormat;
	
	
	public ELHIOClausifier(TermFactory termFactory, OWLOntologyManager manager, NamespaceOWLOntologyFormat namespaceOWLOntologyFormat){
		this.m_termFactory = termFactory; 
		this.manager = manager;
		this.namespaceOWLOntologyFormat =namespaceOWLOntologyFormat;
	}
	
	/**
	 * Gets a set of normalized axioms and converts it into a set of clauses.
	 * We only consider concept inclusions, object property inclusions, and facts:
	 * 
	 *  OWLSubClassAxiom
	 *  OWLEquivalentClassesAxiom
	 *  OWLObjectSubPropertyAxiom
	 *  OWLEquivalentObjectPropertiesAxiom
	 *  OWLObjectPropertyDomainAxiom
	 *  OWLObjectPropertyRangeAxiom
	 *  OWLInverseObjectPropertiesAxiom
	 *  OWLClassAssertionAxiom
	 *  OWLObjectPropertyAssertionAxiom
	 * 
	 * @param normalization the set of axioms to be converted.
	 * @return a set of clauses.
	 */
	public ArrayList<Clause> getClauses(OwlNormalization normalization) throws Exception
	{
		clauses = new ArrayList<Clause>();
		concepts = new HashSet<String>();
		roles = new HashSet<String>(); 
		
		//Transform facts into clauses
		for(OWLIndividualAxiom fact: normalization.getFacts()){
        	addClauses(fact);
        }
		
        //Transform role inclusions into clauses
        for(OWLObjectPropertyExpression[] roleInclusion: normalization.getObjectPropertyInclusions()){
        	addClauses(roleInclusion);
        }
        
        //TODO Transform role inclusions into clauses
        for(OWLDataPropertyExpression[] roleInclusion: normalization.getDataPropertyInclusions()){
        	addClauses(roleInclusion);
        }

        ArrayList<OWLDescription[]> conceptInclusions = new ArrayList<OWLDescription[]>();
		//Concept inclusions
        for(OWLDescription[] conceptInclusion: normalization.getConceptInclusions()){
        	
        	if(!isValidConceptInclusion(conceptInclusion)){
        		printIgnoring(conceptInclusion);
        	}
        	else{
        		//Create an ArrayList of OWLDescription objects in which the head atom is in the first element
            	ArrayList<OWLDescription> rule = new ArrayList<OWLDescription>();
            	int i = 0;
        		for(OWLDescription atom: conceptInclusion){ 
        			if(atom instanceof OWLObjectComplementOf || (atom instanceof OWLObjectAllRestriction && (((OWLObjectAllRestriction) atom).getFiller() instanceof OWLObjectComplementOf || ((OWLObjectAllRestriction) atom).getFiller().toString().equals("Nothing")))){
        				i++;
        				rule.add(atom);
        			}
        			else{
        				rule.add(0,atom);
        			}
        		}
        		
        		//Verifying hornness
        		if(rule.size() != i+1){
        			printIgnoring(conceptInclusion);
        		}
        		else{
        			//Get rid of universals
            		//head of the form: \forall R.B
            		if(rule.get(0) instanceof OWLObjectAllRestriction){
            			conceptInclusions.addAll(getRidOfUniversals(rule));
            		}
            		else{
            			conceptInclusions.add(conceptInclusion);
            		}		
        		}
        	}
        }
        
        //Normalize concept inclusions and transform into clauses
        for(OWLDescription[] conceptInclusion: conceptInclusions){
        	for(OWLDescription[] normalizedConceptInclusion: getNormalizedConceptInclusions(conceptInclusion)){
        		addClauses(normalizedConceptInclusion);
        	}
        }
        
        if(isELHIO){
        	addEqualityClauses();
        }
        
        return clauses;
	}
	

	//TODO
	    private void addClauses(OWLDataPropertyExpression[] axiom) {
		// TODO Auto-generated method stub
		if(isValidDataPropertyInclusion(axiom)){
			//TODO String role0 = axiom[0].getNamedProperty().toString();
			String role0 = axiom[0].asOWLDataProperty().getURI().toString();
			//String role0 = axiom[0].getNamedProperty().asOWLObjectProperty().getURI().toString();
			//TODO String role1 = axiom[1].getNamedProperty().toString();
			String role1 = axiom[1].asOWLDataProperty().getURI().toString();
			//String role1 = axiom[1].getNamedProperty().asOWLObjectProperty().getURI().toString();
			Term X_0 = m_termFactory.getVariable(0);
		    Term X_1 = m_termFactory.getVariable(1);
			Term P1 = null;
			Term P2 = null;

			if((!axiom[0].toString().contains("InverseOf") && axiom[1].toString().contains("InverseOf")) ||
				(axiom[0].toString().contains("InverseOf") && !axiom[1].toString().contains("InverseOf"))){
				P1 = m_termFactory.getFunctionalTerm(role0,X_0,X_1);
				P2 = m_termFactory.getFunctionalTerm(role1,X_1,X_0);
			}
			else if((axiom[0].toString().contains("InverseOf") && axiom[1].toString().contains("InverseOf")) ||
			       (!axiom[0].toString().contains("InverseOf") && !axiom[1].toString().contains("InverseOf"))){
				P1 = m_termFactory.getFunctionalTerm(role0,X_0,X_1);
				P2 = m_termFactory.getFunctionalTerm(role1,X_0,X_1);
			}
			
			clauses.add(new Clause(new Term[]{P1}, P2));
			roles.add(role0);
			roles.add(role1);
		}
		else{
			System.err.print("ignoring role inclusion: ");
        	for(int j=0; j<axiom.length; j++){
            	System.err.print(axiom[j].toString() + " ");
            }
            System.err.println();
		}	
	}
	    private boolean isValidDataPropertyInclusion(OWLDataPropertyExpression[] axiom){	
			return axiom.length == 2 && !axiom[0].toString().equals("TopDataProperty") && !axiom[1].toString().equals("TopDataProperty");
	    }
	/**
	 * Converts an individual axiom into a clause.
	 * Only facts of the form P(a) or P(a,b).
	 * @param axiom the axiom to be converted.
	 */
	private void addClauses(OWLIndividualAxiom axiom)
	{
		//A(a)
		if(axiom instanceof OWLClassAssertionAxiom){
			//TODO String conceptName = ((OWLClassAssertionAxiom) axiom).getDescription().toString();
			String conceptName = ((OWLClassAssertionAxiom) axiom).getDescription().asOWLClass().getURI().toString();
			//TODO String constant0 = ((OWLClassAssertionAxiom) axiom).getIndividual().toString();
			String constant0 = ((OWLClassAssertionAxiom) axiom).getIndividual().getURI().toString();
			Term c_0 = m_termFactory.getConstant(constant0);
			Term P = m_termFactory.getFunctionalTerm(conceptName,c_0);
			clauses.add(new Clause(new Term[]{}, P));
			concepts.add(conceptName);
		}
		//P(a,b)
		else if(axiom instanceof OWLObjectPropertyAssertionAxiom){
			//TODO String roleName = ((OWLObjectPropertyAssertionAxiom) axiom).getProperty().toString();
			String roleName = ((OWLObjectPropertyAssertionAxiom) axiom).getProperty().asOWLObjectProperty().getURI().toString();
			String constant0 = ((OWLObjectPropertyAssertionAxiom) axiom).getSubject().toString();
			String constant1 = ((OWLObjectPropertyAssertionAxiom) axiom).getObject().toString();
			Term c_0 = m_termFactory.getConstant(constant0);
			Term c_1 = m_termFactory.getConstant(constant1);
			Term P = m_termFactory.getFunctionalTerm(roleName,c_0,c_1);
			clauses.add(new Clause(new Term[]{}, P));
			roles.add(roleName);
		}
		else{
    		System.err.println("Ignoring invalid individual axiom: " + axiom.toString());
		}
	}
	
	/**
	 * Converts an object property expression into a set of clauses.
	 * We only consider object property expressions of the form P1(^-) \sqsubseteq P2(^-)
	 * @param axiom the axiom to be converted
	 */
	private void addClauses(OWLObjectPropertyExpression[] axiom)
	{
		if(isValidObjectPropertyInclusion(axiom)){
			//TODO String role0 = axiom[0].getNamedProperty().toString();
			String role0 = axiom[0].getNamedProperty().asOWLObjectProperty().getURI().toString();
			//TODO String role1 = axiom[1].getNamedProperty().toString();
			String role1 = axiom[1].getNamedProperty().asOWLObjectProperty().getURI().toString();
			Term X_0 = m_termFactory.getVariable(0);
		    Term X_1 = m_termFactory.getVariable(1);
			Term P1 = null;
			Term P2 = null;

			if((!axiom[0].toString().contains("InverseOf") && axiom[1].toString().contains("InverseOf")) ||
				(axiom[0].toString().contains("InverseOf") && !axiom[1].toString().contains("InverseOf"))){
				P1 = m_termFactory.getFunctionalTerm(role0,X_0,X_1);
				P2 = m_termFactory.getFunctionalTerm(role1,X_1,X_0);
			}
			else if((axiom[0].toString().contains("InverseOf") && axiom[1].toString().contains("InverseOf")) ||
			       (!axiom[0].toString().contains("InverseOf") && !axiom[1].toString().contains("InverseOf"))){
				P1 = m_termFactory.getFunctionalTerm(role0,X_0,X_1);
				P2 = m_termFactory.getFunctionalTerm(role1,X_0,X_1);
			}
			
			clauses.add(new Clause(new Term[]{P1}, P2));
			roles.add(role0);
			roles.add(role1);
		}
		else{
			System.err.print("ignoring role inclusion: ");
        	for(int j=0; j<axiom.length; j++){
            	System.err.print(axiom[j].toString() + " ");
            }
            System.err.println();
		}
	}
	
	/**
	 * Verifies that the given object property inclusion is valid, i.e. that it consists of exactly two atoms, none of which is TopObjectProperty
	 * @param axiom
	 * @return
	 */
    private boolean isValidObjectPropertyInclusion(OWLObjectPropertyExpression[] axiom){	
		return axiom.length == 2 && !axiom[0].toString().equals("TopObjectProperty") && !axiom[1].toString().equals("TopObjectProperty");
    }

    /**
     * Gets rid of universals
     */
    private ArrayList<OWLDescription[]> getRidOfUniversals(ArrayList<OWLDescription> rule) throws Exception{
    	ArrayList<OWLDescription[]> result =  new ArrayList<OWLDescription[]>();
    	OWLDescription head = rule.get(0);
		//T \implies \forall R.B
		//\exists R^- \implies B
		if(rule.size() == 1){
			OWLDescription[] ci1 = new OWLDescription[2];

			ci1[0] = ((OWLObjectAllRestriction)head).getFiller();
			ci1[1] = manager.getOWLDataFactory().getOWLObjectSomeRestriction(((OWLObjectAllRestriction)head).getProperty().getInverseProperty(), manager.getOWLDataFactory().getOWLThing()).getComplementNNF();
			result.add(ci1);
		}
		//body \implies \forall R.B
		else{
			OWLDescription[] ci1 = new OWLDescription[rule.size()];
			OWLDescription[] ci2 = new OWLDescription[2];
			
			//body \implies AUX$i
			ci1[0] = manager.getOWLDataFactory().getOWLClass(new java.net.URI("http://requiem#AUX$" + artificialConceptIndex++));
			for(int j=1; j<rule.size(); j++){
				ci1[j] = rule.get(j);
			}
			result.add(ci1);
			
			//AUX$i \implies \forall R.B
			// \exists R^-.AUX$i \implies B
			ci2[0] = ((OWLObjectAllRestriction)head).getFiller();
			ci2[1] = manager.getOWLDataFactory().getOWLObjectSomeRestriction(((OWLObjectAllRestriction)head).getProperty().getInverseProperty(), ci1[0]).getComplementNNF();
			
			result.add(ci2);
		}
	return result;
    }
    
    /**
     * Obtains a normalized set of concept inclusions for a given concept inclusion
     */
    private ArrayList<OWLDescription[]> getNormalizedConceptInclusions(OWLDescription[] conceptInclusion) throws Exception{
    	ArrayList<OWLDescription[]> result =  new ArrayList<OWLDescription[]>();
    	
    	//Create an ArrayList of OWLDescription objects in which the head atom is in the first element
    	ArrayList<OWLDescription> rule = new ArrayList<OWLDescription>();
		for(OWLDescription atom: conceptInclusion){ 
			if(atom instanceof OWLObjectComplementOf || (atom instanceof OWLObjectAllRestriction && (((OWLObjectAllRestriction) atom).getFiller() instanceof OWLObjectComplementOf || ((OWLObjectAllRestriction) atom).getFiller().toString().equals("Nothing")))){
				rule.add(atom);
			}
			else{
				rule.add(0,atom);
			}
		}
    	
		OWLDescription head = rule.get(0);
		
		//head of the form: C
		//body \implies C
		if(head instanceof OWLClass){
			//atom \implies C
			if(rule.size() == 2){
				result.add(conceptInclusion);
			}
			//atom1, ..., atomn \implies C
			else{
				//Normalize body atoms unless all they are all classes
				OWLDescription[] normalizedAxiom = new OWLDescription[rule.size()];
				normalizedAxiom[0] = rule.get(0);
				boolean allBodyAtomsClasses = true;
				for(int i=1; i < rule.size(); i++){
					//atom1, ..., notClassi, ..., atomn \implies C
					if(!(rule.get(i).getComplementNNF() instanceof OWLClass)){
						allBodyAtomsClasses = false;
						normalizedAxiom[i] = manager.getOWLDataFactory().getOWLClass(new java.net.URI("http://requiem#AUX$" + artificialConceptIndex++)).getComplementNNF();
						
						//notClassi \implies AUX$i
						OWLDescription[] auxAxiom = new OWLDescription[2];
						auxAxiom[0] = rule.get(i);
						auxAxiom[1] = normalizedAxiom[i].getComplementNNF();
						result.add(auxAxiom);
					}
					else{
						normalizedAxiom[i] = rule.get(i);
					}
				}
				if(allBodyAtomsClasses){
					result.add(conceptInclusion);
				}
				else{
					result.add(normalizedAxiom);
				}
			}
		}
		else{
			//Normalize body atoms unless all they are all classes
			OWLDescription[] normalizedAxiom = new OWLDescription[rule.size()];
			normalizedAxiom[0] = rule.get(0);
			boolean allBodyAtomsClasses = true;
			for(int i=1; i < rule.size(); i++){
				//atom1, ..., maybeNotClassi, ..., atomn \implies C
				if(!(rule.get(i).getComplementNNF() instanceof OWLClass)){
					allBodyAtomsClasses = false;
					normalizedAxiom[i] = manager.getOWLDataFactory().getOWLClass(new java.net.URI("http://requiem#AUX$" + artificialConceptIndex++)).getComplementNNF();
					
					//maybeNotClassi \implies AUX$i
					OWLDescription[] auxAxiom = new OWLDescription[2];
					auxAxiom[0] = rule.get(i);
					auxAxiom[1] = normalizedAxiom[i].getComplementNNF();
					result.add(auxAxiom);
				}
				else{
					normalizedAxiom[i] = rule.get(i);
				}
			}
			if(allBodyAtomsClasses){
				result.add(conceptInclusion);
			}
			else{
				result.add(normalizedAxiom);
			}
		}
    	
    	return result;
    }
    
    
    private void printIgnoring(OWLDescription[] conceptInclusion){
    	System.err.print("ignoring invalid concept inclusion: ");
		for(int j=0; j < conceptInclusion.length; j++){
			System.err.print(conceptInclusion[j].toString() + " ");
		}
		System.err.println();
    }
	
	/**
	 * Transforms a concept inclusion into a set of clauses representing it.
	 * 
	 * @param axiom the axiom to be converted.
	 */
	private void addClauses(OWLDescription[] conceptInclusion)
	{
		//Create an ArrayList of OWLDescription objects in which the head atom is in the first element
    	ArrayList<OWLDescription> rule = new ArrayList<OWLDescription>();
		for(OWLDescription atom: conceptInclusion){ 
			if(atom instanceof OWLObjectComplementOf || (atom instanceof OWLObjectAllRestriction && (((OWLObjectAllRestriction) atom).getFiller() instanceof OWLObjectComplementOf || ((OWLObjectAllRestriction) atom).getFiller().toString().equals("Nothing")))){
				rule.add(atom);
			}
			else{
				rule.add(0,atom);
			}
		}
		
		if(rule.size() > 1){
			OWLDescription head = rule.get(0);
			
			//head of the form: C
			if(head instanceof OWLClass){
				//TODO String className = ((OWLClass)head).toString();
				String className = ((OWLClass)head).getURI().toString();
				if(rule.size() == 2 && rule.get(1) instanceof OWLObjectOneOf){
					//Obtain the individual
					Object[] individuals = ((OWLObjectOneOf) rule.get(1)).getIndividuals().toArray();
					Term o = m_termFactory.getConstant(individuals[0].toString());
					Term C = m_termFactory.getFunctionalTerm(className, o);
					
					concepts.add(className);
					clauses.add(new Clause(new Term[]{}, C));
				}
				else{
					Term X = m_termFactory.getVariable(0);
					Term C = m_termFactory.getFunctionalTerm(className, X);
					
					//Remove the head from the array to process the body
					rule.remove(head);
					
					//Add clause: C(x) <- body
					clauses.add(new Clause(getBody(rule), C));
					
					//Add C to the set of concepts
					concepts.add(className);
				}
			}
			//head of the form: {o}
			else if(head instanceof OWLObjectOneOf){
				isELHIO = true;
				Term X_0 = m_termFactory.getVariable(0);
				//Obtain the individual
				Object[] individuals = ((OWLObjectOneOf) head).getIndividuals().toArray();
				Term o = m_termFactory.getConstant(individuals[0].toString());
				Term N = m_termFactory.getFunctionalTerm("$", o);
				Term E = m_termFactory.getFunctionalTerm("=", X_0, o);

				//Remove the head from the array to process the body
				rule.remove(head);
				
				//Add clause: N(o)
				clauses.add(new Clause(new Term[]{}, N));
				
				//Add clause: x = o <- body
				clauses.add(new Clause(getBody(rule), E));
			}
			
			// head of the form: \exists R.C
			else if(head instanceof OWLObjectSomeRestriction){
				OWLDescription filler = ((OWLObjectSomeRestriction) head).getFiller();
				OWLObjectPropertyExpression property = ((OWLObjectSomeRestriction) head).getProperty();
				String propertyName = property.getNamedProperty().toString();
				//String propertyName = property.getNamedProperty().asOWLObjectProperty().getURI().toString();
				String className = filler.toString();
				//String className = filler.asOWLClass().getURI().toString();
				Term X_0 = m_termFactory.getVariable(0);
				Term R = null;
				rule.remove(head);
				
				if(filler instanceof OWLObjectOneOf){
					//Obtain the individual
					Object[] individuals = ((OWLObjectOneOf) filler).getIndividuals().toArray();
					Term o = m_termFactory.getConstant(individuals[0].toString());
					//R of the form: P^-
					if(property.toString().contains("InverseOf")){
						R = m_termFactory.getFunctionalTerm(propertyName, o, X_0);
					}
					//R of the form: P
					else{
						R = m_termFactory.getFunctionalTerm(propertyName, X_0, o);
					}
				}
				else{
					Term f_X_0 = m_termFactory.getFunctionalTerm("f" + functionalSymbolIndex++, X_0);		
					//R of the form: P^-
					if(property.toString().contains("InverseOf")){
						R = m_termFactory.getFunctionalTerm(propertyName, f_X_0, X_0);
					}
					//R of the form: P
					else{
						R = m_termFactory.getFunctionalTerm(propertyName, X_0, f_X_0);
					}
					
					if(!(filler.toString().equals("ObjectComplementOf(Nothing)") || filler.toString().equals("Thing"))){
						Term C = m_termFactory.getFunctionalTerm(className,f_X_0);
						
						//Add clause: C(f(x)) <- body
						clauses.add(new Clause(getBody(rule), C));
						
						concepts.add(className);
					}
				}
				//Add clause: P(x,f(x)) <- body | P(f(x),x) <- body
				clauses.add(new Clause(getBody(rule), R));
				
				//Add P to the set of roles
				roles.add(propertyName);
			}
		}
	}
	
	/**
	 * Computes the terms composing the body of a clause and adds extra clauses if necessary
	 * @param body
	 * @return
	 */
	private Term[] getBody(ArrayList<OWLDescription> body){
		ArrayList<Term> result = new ArrayList<Term>();
		
		for(OWLDescription atom: body){
			
			//We work with positive atoms
			atom = atom.getComplementNNF();
			
			//atom of the form: A
			if(atom instanceof OWLClass){
				//TODO String className = ((OWLClass)atom).toString();
				String className = ((OWLClass)atom).asOWLClass().getURI().toString();
				Term X_0 = m_termFactory.getVariable(0);
				Term C = m_termFactory.getFunctionalTerm(className, X_0);
				result.add(C);
				concepts.add(className);
			}
						
			//atom of the form: \exists R.C
			else if(atom instanceof OWLObjectSomeRestriction){
				OWLDescription filler = ((OWLObjectSomeRestriction) atom).getFiller();
				OWLObjectPropertyExpression property = ((OWLObjectSomeRestriction) atom).getProperty();
				//TODO String propertyName = property.getNamedProperty().toString();
				String propertyName = property.getNamedProperty().asOWLObjectProperty().getURI().toString();
				//TODO String className = filler.toString();
				//TODO maybe String className = filler.asOWLClass().getURI().toString();
				String className;
				if(!filler.isLiteral()){
					className = filler.asOWLClass().getURI().toString();
				}else{
					className = filler.toString();
				}
				Term X_0 = m_termFactory.getVariable(0);
				Term X_1 = m_termFactory.getVariable(1);
				Term R = null;
				
				if(filler instanceof OWLObjectOneOf){
					//Obtain the individual
					Object[] individuals = ((OWLObjectOneOf) filler).getIndividuals().toArray();
					Term o = m_termFactory.getConstant(individuals[0].toString());
					//R of the form: P^-
					if(property.toString().contains("InverseOf")){
						R = m_termFactory.getFunctionalTerm(propertyName, o, X_0);
					}
					//R of the form: P
					else{
						R = m_termFactory.getFunctionalTerm(propertyName, X_0, o);
					}
				}
				else{
					//R of the form: P^-
					if(property.toString().contains("InverseOf")){
						R = m_termFactory.getFunctionalTerm(propertyName, X_1, X_0);
					}
					//R of the form: P
					else{
						R = m_termFactory.getFunctionalTerm(propertyName, X_0, X_1);
					}
					
					if(!(filler.toString().equals("ObjectComplementOf(Nothing)") || filler.toString().equals("Thing"))){
						Term C = m_termFactory.getFunctionalTerm(className,X_1);
						
						result.add(C);
						
						concepts.add(className);
					}
				}
				
				result.add(R);
				roles.add(propertyName);
			}
		}
		
		if(result.size() == 0){
			System.out.println(body.toString());
		}
		
		Term[] terms = new Term[result.size()];
		
		int i=0;
		for(Term t: result)	{
			terms[i++] = t;
		}

		
		return terms;
	}

	/**
	 * Verifies that a given class inclusion is valid 
	 * @param axiom
	 * @return
	 */
	private boolean isValidConceptInclusion(OWLDescription[] axiom){
		//T \implies \forall R.C
		if(axiom.length == 1 && axiom[0] instanceof OWLObjectAllRestriction){
			return true;
		}

		for(OWLDescription atom: axiom){
					
			// Checking that each atom is of valid form
			if(!(atom instanceof OWLClass || 
				 atom instanceof OWLObjectComplementOf ||
				 atom instanceof OWLObjectOneOf ||
				 atom instanceof OWLObjectSomeRestriction ||
				 atom instanceof OWLObjectAllRestriction)){	
				return false;
			}
			
			// Checking that there is only one nominal per OWLObjectOneOf atom
			if(atom instanceof OWLObjectOneOf){
				if(((OWLObjectOneOf)atom).getIndividuals().size() != 1){
					return false; 
				}
			}
		}
		
		return true; 
	}
	
	/**
	 * Adds the approximation of equality E'_\tbox. 
	 * The special symbol '\N' is '$'.
	 * Equality is '='.
	 * 
	 */
	private void addEqualityClauses(){
	    Term X = m_termFactory.getVariable(0);
		Term Y = m_termFactory.getVariable(1);
		Term Z = m_termFactory.getVariable(2);
		
		Term N_X = m_termFactory.getFunctionalTerm("$",X);
		Term N_Y = m_termFactory.getFunctionalTerm("$",Y);
		Term N_Z = m_termFactory.getFunctionalTerm("$",Z);
		
		Term E_X_Y = m_termFactory.getFunctionalTerm("=",X,Y);
		Term E_Y_X = m_termFactory.getFunctionalTerm("=",Y,X);
		Term E_Y_Z = m_termFactory.getFunctionalTerm("=",Y,Z);
	    
		clauses.add(new Clause(new Term[]{E_Y_X, N_X, N_Y}, E_X_Y));
		
	    for(String concept: concepts){
	    	Term C_X = m_termFactory.getFunctionalTerm(concept,X);
	    	Term C_Y = m_termFactory.getFunctionalTerm(concept,Y);
	    	clauses.add(new Clause(new Term[]{C_X, E_X_Y, N_Y}, C_Y));
	    }
	    for(String role: roles){
	    	Term R_X_Y = m_termFactory.getFunctionalTerm(role,X,Y);
	    	Term R_Y_X = m_termFactory.getFunctionalTerm(role,Y,X);
			Term R_X_Z = m_termFactory.getFunctionalTerm(role,X,Z);
			Term R_Z_X = m_termFactory.getFunctionalTerm(role,Z,X);
	    	clauses.add(new Clause(new Term[]{R_X_Y, E_Y_Z, N_Z}, R_X_Z));
			clauses.add(new Clause(new Term[]{R_Y_X, E_Y_Z, N_Z}, R_Z_X));
	    }
	}
}
