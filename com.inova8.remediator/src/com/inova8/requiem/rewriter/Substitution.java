package com.inova8.requiem.rewriter;

import java.util.Map;
import java.util.HashMap;

@SuppressWarnings("serial")
public class Substitution extends HashMap<Variable,Term> {

	protected static final Substitution IDENTITY=new Substitution() {
        public Term put(Variable key,Term value) {
            throw new UnsupportedOperationException();
        }
    };

    public static Substitution mostGeneralUnifier(Term term1,Term term2,TermFactory termFactory) {
        Substitution result=new Substitution();
        if (mostGeneralUnifier(term1,term2,result,termFactory))
            return result;
        else
            return null;
    }
    
    protected static boolean mostGeneralUnifier(Term term1,Term term2,Substitution substitution,TermFactory termFactory) {
        term1=term1.apply(substitution,termFactory);
        term2=term2.apply(substitution,termFactory);
        if (term1.equals(term2))
            return true;
        if (term1 instanceof FunctionalTerm && term2 instanceof FunctionalTerm) {
            if (term1.getArity()!=term2.getArity() || !term1.getName().equals(term2.getName()))
                return false;
            for (int index=0;index<term1.getArity();index++)
                if (!mostGeneralUnifier(term1.getArgument(index),term2.getArgument(index),substitution,termFactory))
                    return false;
            return true;
        }
        else if (term1 instanceof Variable) {
            Variable variable1=(Variable)term1;
            if (term2.contains(variable1)) // Occurs check!
                return false;
            extendSubstitution(substitution,variable1,term2,termFactory);
            return true;
        }
        else {
            Variable variable2=(Variable)term2;
            if (term1.contains(variable2)) // Occurs check!
                return false;
            extendSubstitution(substitution,variable2,term1,termFactory);
            return true;
        }
    }
    
    protected static void extendSubstitution(Substitution substitution,Variable variable,Term term,TermFactory termFactory) {
        if (substitution.containsKey(variable))
            throw new IllegalStateException();
        Substitution temp=new Substitution();
        temp.put(variable,term);
        for (Map.Entry<Variable,Term> entry : substitution.entrySet()) {
            Term applied=entry.getValue().apply(temp,termFactory);
            entry.setValue(applied);
        }
        substitution.put(variable,term);
    }
}
