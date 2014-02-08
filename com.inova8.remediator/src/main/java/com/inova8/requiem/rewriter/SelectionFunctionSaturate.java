package com.inova8.requiem.rewriter;

import java.util.Arrays;

public class SelectionFunctionSaturate extends SelectionFunction {
	
	public SelectionFunctionSaturate(){}
	
	public void selectAtoms(Clause c) {
		//Case analysis depending on the clause type
		boolean aux;
		int[] depths;
		int maxDepth;
				
		//Initialize
		setType(c);
		c.m_selectedHead = false;
		c.m_selectedBody = new boolean[c.getBody().length];
		
		switch(c.m_type){
		case 'U':
			// Select the head H if the body is empty or if depth(H) > depth(body); otherwise, select every deepest covering atom
			if(c.getBody().length == 0){
				c.m_selectedHead = true;
			}
			else{
				//Get the depths of every atom (head and body)
				depths = new int[c.getBody().length+1];
				for(int i=0; i < c.getBody().length; i++){
					depths[i] = ((Term) c.getBody()[i]).getDepth();
				}
				depths[c.getBody().length] = ((Term) c.getHead()).getDepth(); //Depth of the head
				
				//Get the maximum depth
				Arrays.sort(depths);
				maxDepth = depths[c.getBody().length];
				
				aux = false;
				for(int i=0; i < c.getBody().length; i++){
					if(((Term) c.getBody()[i]).getDepth() == maxDepth){
						c.m_selectedBody[i] = true;
						aux = true;		//There is at least one body atom that is deepest or as deep as the head
					}
				}
				if(!aux){ //The head is the only deepest atom
					c.m_selectedHead = true;
				}
			}
			break;
		case 'B':
			//Select the binary body atom
			for(int i=0; i < c.getBody().length; i++){
				if (((Term)c.getBody()[i]).getArity() == 2)
					c.m_selectedBody[i] = true;
			}
			break;
		case 'E':
			//Select the equality atom
			if(c.getHead().getName().equals("="))
				c.m_selectedHead = true;
			for(int i=0; i < c.getBody().length; i++){
				if (((Term)c.getBody()[i]).getName().equals("="))
					c.m_selectedBody[i] = true;
			}
			break;
		case 'N':
			//Select the equality atom
			if(c.getHead().getName().equals("$"))
				c.m_selectedHead = true;
			for(int i=0; i < c.getBody().length; i++){
				if (((Term)c.getBody()[i]).getName().equals("$"))
					c.m_selectedBody[i] = true;
			}
			break;
		// case 'Q'
		default:
			// If the head contains functional terms or the body is empty, select the head; otherwise, select every deepest body atom
			if ((((Term)c.getHead()).getDepth()-1 > 0)||(c.getBody().length == 0))
				c.m_selectedHead = true;
			else
			{
				//Get the depths of all the body atoms
				depths = new int[c.getBody().length];
				for(int i=0; i < c.getBody().length; i++){
					depths[i] = ((Term) c.getBody()[i]).getDepth();
				}
				
				//Get the maximum depth
				Arrays.sort(depths);
				maxDepth = depths[c.getBody().length-1];
				
				for(int i=0; i < c.getBody().length; i++){
					if(((Term) c.getBody()[i]).getDepth() == maxDepth){
						c.m_selectedBody[i] = true;
					}
				}
			}
			break;
		}
    }
	
	private void setType(Clause c)
	{
		if(c.isQueryClause()){
			c.m_type = 'Q';		//If c contains the special predicate Q in the head, then c is of type 4
		}
		else if(c.containsNominalPredicate()){
			c.m_type = 'N';		//If c is not of type 4 and it contains the nominal predicate $, then c is of type 1.2, 3.2--3.4
		}
		else if(c.containsEquality()){
			c.m_type = 'E';		//If c is not of type 1.2, 3.2--3.4, 4, and it contains equality, then c is of type 3.1, 3.5--3.7
		}
		else if(c.containsBinaryBodyAtoms()){
			c.m_type = 'B';		//If c is not of type 1.2, 3.1--3.7, 4, and it contains a binary body atom, then c is of type 2.1--2.6
		}
		else{
			c.m_type = 'U';		//If c is not of type 1.2, 2.1--2.6, 3.1--3.7, 4 then c is of type 1.1
		}
	}

	public boolean isToBePruned(Clause c){
		return c.containsFunctionalTerms() || c.containsEquality() || c.containsNominalPredicate();
	}
}