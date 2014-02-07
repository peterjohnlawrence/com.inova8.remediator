package com.inova8.requiem.rewriter;

import java.util.Arrays;
import java.util.HashMap;

public class FunctionalTerm extends Term{
    protected final String m_name;
    protected Term[] m_arguments;
    
    protected FunctionalTerm(String name,Term[] arguments) {
        m_name=name;
        m_arguments=arguments;
    }
    
    public String getName() {
        return m_name;
    }
    
    public int getArity() {
        return m_arguments.length;
    }
    
    public Term getArgument(int argumentIndex) {
    	return m_arguments[argumentIndex];
    }
    
    public Term[] getArguments(){
    	return m_arguments;
    }
    
    public boolean contains(Term term) {
        if (equals(term))
            return true;
        for (int index=m_arguments.length-1;index>=0;--index)
            if (m_arguments[index].contains(term))
                return true;
       return false;         
    }
    
    public Term apply(Substitution substitution,TermFactory termFactory) {
        if (m_arguments.length==0 || substitution.isEmpty())
            return this;
        else {
            Term[] arguments=new Term[m_arguments.length];
            for (int index=m_arguments.length-1;index>=0;--index)
                arguments[index]=m_arguments[index].apply(substitution,termFactory);
            return termFactory.getFunctionalTerm(m_name,arguments);
        }
    }
    
    public Term offsetVariables(TermFactory termFactory,int offset) {
        if (m_arguments.length==0)
            return this;
        else {
            Term[] arguments=new Term[m_arguments.length];
            for (int index=m_arguments.length-1;index>=0;--index)
                arguments[index]=m_arguments[index].offsetVariables(termFactory,offset);
            return termFactory.getFunctionalTerm(m_name,arguments);
        }
    }

    public Term renameVariables(TermFactory termFactory, HashMap<Variable, Integer> mapping) {
        if (m_arguments.length==0)
            return this;
        else {
            Term[] arguments=new Term[m_arguments.length];
            for (int index=m_arguments.length-1;index>=0;--index)
                arguments[index]=m_arguments[index].renameVariables(termFactory, mapping);
            return termFactory.getFunctionalTerm(m_name,arguments);
        }
    }
    
    public int getMinVariableIndex() {
        int minVariableIndex=Integer.MAX_VALUE;
        for (int index=m_arguments.length-1;index>=0;--index)
            minVariableIndex=Math.min(minVariableIndex,m_arguments[index].getMinVariableIndex());
        return minVariableIndex;
    }
    
    public void toString(StringBuffer buffer) {
        buffer.append(getName());
        if (m_arguments.length>0) {
            buffer.append('(');
            for (int index=0;index<m_arguments.length;index++) {
                if (index!=0)
                    buffer.append(',');
                m_arguments[index].toString(buffer);
            }
            buffer.append(')');
        }
    }
    
    public int getDepth(){
    	return getDepth(this);
    }
    
    private int getDepth(Term t){
    	if(t.getArity() == 0)
    		return 0;
    	else
    	{	
    		int[] depths = new int[t.getArity()];
    		for(int i=0; i < t.getArity(); i++)
    			depths[i] = getDepth((Term)t.getArgument(i));
    		Arrays.sort(depths);
    		return 1 + depths[t.getArity()-1];
    	}
    }
    
    public Term getVariableOrConstant(){
    	if(this.getArity() == 0)
    		return this;
    	else
    		return this.m_arguments[0].getVariableOrConstant(); //All functional terms have at most one element
    }
    
    public String getFunctionalPrefix()
    {
    	if(this.getArity() == 0){
    		return this.m_name;
    	}
    	else{
    		String arguments = "";
    		for(int i=0; i<this.m_arguments.length; i++){
    			arguments += this.m_arguments[i].getFunctionalPrefix();
    		}
    		return this.m_name + arguments;
    	}
    }
}
