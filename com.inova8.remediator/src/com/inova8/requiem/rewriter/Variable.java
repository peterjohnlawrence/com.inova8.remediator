package com.inova8.requiem.rewriter;

import java.util.HashMap;

public class Variable extends Term {
    protected int m_index;

    protected Variable(int index) {
        m_index=index;
    }
    
    public String getName() {
        return "?"+m_index;
    }
    
    public int getArity() {
        return 0;
    }
    
    public Term getArgument(int argumentIndex) {
        throw new IndexOutOfBoundsException();
    }
    
    public Term[] getArguments() {
        throw new IndexOutOfBoundsException();
    }
    
    public boolean contains(Term term) {
        return equals(term);
    }
    
    public Term apply(Substitution substitution,TermFactory termFactory) {
        Term result=substitution.get(this);
        if (result!=null)
            return result;
        else
            return this;
    }
    
    public Term offsetVariables(TermFactory termFactory,int offset) {
        return termFactory.getVariable(m_index+offset);
    }
    
    public Term renameVariables(TermFactory termFactory, HashMap<Variable, Integer> mapping) {
        return termFactory.getVariable(mapping.get(this));
    }
    
    public int getMinVariableIndex() {
        return m_index;
    }
    
    public void toString(StringBuffer buffer) {
        buffer.append(getName());
    }
    
    public int getDepth()
    {
    	return 0;
    }
    
    public Term getVariableOrConstant()
    {
    	return this;
    }
    
    public String getFunctionalPrefix()
    {
    	return "";
    }
    
    public void setIndex(int index)
    {
    	this.m_index = index;
    }
    
    public boolean equals(Object that){
    	if(that instanceof Variable){
    		return this.getName().equals(((Variable)that).getName());
    	}
    	return false;
    }
    
    public int hashCode(){
    	return this.m_index;
    }
}
