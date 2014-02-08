package com.inova8.requiem.rewriter;

import java.util.List;
import java.util.ArrayList;

public final class TermFactory {
    protected final List<Variable> m_variables;
    protected Entry[] m_entries;
    protected int m_numberOfTerms;
    protected int m_resizeThreshold;
    
    public TermFactory() {
        m_variables=new ArrayList<Variable>(1000);
        m_entries=new Entry[16];
        m_resizeThreshold=(int)(m_entries.length*0.75);
    }
    public Variable getVariable(int index) {
        while (index>=m_variables.size())
            m_variables.add(null);
        Variable variable=m_variables.get(index);
        if (variable==null) {
            variable=new Variable(index);
            m_variables.set(index,variable);
        }
        return variable;
    }
    public FunctionalTerm getConstant(String name) {
        return getFunctionalTerm(name,Term.NO_ARGUMENTS);
    }
    public FunctionalTerm getFunctionalTerm(String name,Term... arguments) {
        int hashCode=interningHashCode(name,arguments);
        int index=hashCode & (m_entries.length-1);
        Entry entry=m_entries[index];
        while (entry!=null) {
            if (hashCode==entry.m_hashCode && entry.equals(name,arguments))
                return entry.m_term;
            entry=entry.m_next;
        }
        FunctionalTerm functionalTerm=new FunctionalTerm(name,arguments);
        m_entries[index]=new Entry(functionalTerm,m_entries[index]);
        m_numberOfTerms++;
        if (m_numberOfTerms>=m_resizeThreshold)
            resizeEntries();
        return functionalTerm;
    }
    protected void resizeEntries() {
        Entry[] newEntries=new Entry[m_entries.length*2];
        for (int index=m_entries.length-1;index>=0;--index) {
            Entry entry=m_entries[index];
            while (entry!=null) {
                Entry nextEntry=entry.m_next;
                int newIndex=entry.m_hashCode & (newEntries.length-1);
                entry.m_next=newEntries[newIndex];
                newEntries[newIndex]=entry;
                entry=nextEntry;
            }
        }
        m_entries=newEntries;
        m_resizeThreshold=(int)(m_entries.length*0.75);
    }
    
    protected static int interningHashCode(String name,Term[] arguments) {
        int hashCode=name.hashCode();
        for (Term argument : arguments)
            hashCode+=argument.hashCode();
        return hashCode;
    }
    
    protected static final class Entry {
        protected final FunctionalTerm m_term;
        protected final int m_hashCode;
        protected Entry m_next;
        
        public Entry(FunctionalTerm term,Entry next) {
            m_term=term;
            m_hashCode=interningHashCode(m_term.m_name,m_term.m_arguments);
            m_next=next;
        }
        public boolean equals(String name,Term[] arguments) {
            if (!name.equals(m_term.m_name))
                return false;
            if (arguments.length!=m_term.m_arguments.length)
                return false;
            for (int index=arguments.length-1;index>=0;--index)
                if (arguments[index]!=m_term.m_arguments[index])
                    return false;
            return true;
        }
    }
}
