// Copyright 2008 by Oxford University; see license.txt for details

package com.inova8.requiem.rewriter;

public class SelectionFunctionUnfold extends SelectionFunction {

	public void selectAtoms(Clause c) {
		//Case analysis depending on the clause type
				
		//		Initialize
		setType(c);
		c.m_selectedHead = false;
		c.m_selectedBody = new boolean[c.getBody().length];
		
		switch(c.m_type){
		case 'O':
			// Select all the body atoms
			for(int i=0; i < c.getBody().length; i++)
				c.m_selectedBody[i] = true;
			break;
		// case 'U'
		default:
			// Select head
			c.m_selectedHead = true;
			break;
		}
    }
	
	private void setType(Clause c)
	{	
		if(c.isQueryClause()){
			c.m_type = 'O';
		}
		else{
			if(c.getBody().length == 1){
				c.m_type = 'U';
			}
			else{
				c.m_type = 'O';
			}
		}
	}

	public boolean isToBePruned(Clause c){
		return c.m_type == 'U';
	}
}
