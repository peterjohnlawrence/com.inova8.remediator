package com.inova8.requiem.rewriter;

public class SelectionFunctionUnfoldGreedy extends SelectionFunction {
	protected String pred;
	
	public SelectionFunctionUnfoldGreedy(String p){
		this.pred = p;	
	}
	
	public void selectAtoms(Clause c) {
		//Case analysis depending on the clause type
				
		c.m_selectedHead = false;
		c.m_selectedBody = new boolean[c.getBody().length];
		
		if(c.getHead().getName().equals(pred)){
			c.m_type = 'U';
			c.m_selectedHead = true;
		}
		else{
			c.m_type = 'O';
			for(int i=0; i < c.getBody().length; i++){
				if(c.getBody()[i].getName().equals(pred)){
					c.m_selectedBody[i] = true;
				}
			}
		}
    }
	
	public boolean isToBePruned(Clause c){
		return c.m_type == 'U';
	}
	
}