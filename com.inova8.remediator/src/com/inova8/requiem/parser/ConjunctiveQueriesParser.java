// $ANTLR 3.2 Sep 23, 2009 12:02:23 /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g 2009-12-04 13:21:58


package com.inova8.requiem.parser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.BitSet;
import org.antlr.runtime.DFA;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import com.inova8.requiem.rewriter.Clause;
import com.inova8.requiem.rewriter.Term;
import com.inova8.requiem.rewriter.TermFactory;

public class ConjunctiveQueriesParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "SENTENCE", "ALPHAVAR", "NUMBER", "ALPHA", "INT", "CHAR", "WS", "'<-'", "','", "'Q'", "'('", "')'", "'?'"
    };
    public static final int SENTENCE=4;
    public static final int T__12=12;
    public static final int INT=8;
    public static final int WS=10;
    public static final int NUMBER=6;
    public static final int EOF=-1;
    public static final int CHAR=9;
    public static final int ALPHA=7;
    public static final int T__13=13;
    public static final int T__16=16;
    public static final int ALPHAVAR=5;
    public static final int T__14=14;
    public static final int T__11=11;
    public static final int T__15=15;

    // delegates
    // delegators


        public ConjunctiveQueriesParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public ConjunctiveQueriesParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return ConjunctiveQueriesParser.tokenNames; }
    public String getGrammarFileName() { return "/Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g"; }



    private TermFactory termFactory = new TermFactory();
    private ArrayList<Term> headAtoms = new ArrayList<Term>();
    private ArrayList<Term>	bodyAtoms = new ArrayList<Term>();
    private ArrayList<Term>	headTerms = new ArrayList<Term>();
    private List<String> errors = new LinkedList<String>();

    public void displayRecognitionError(String[] tokenNames,RecognitionException e) {
            String hdr = getErrorHeader(e);
            String msg = getErrorMessage(e, tokenNames);
            errors.add(hdr + " " + msg);
        }
        
    public List<String> getErrors() {
            return errors;
        }

    boolean error1 = false;

    public void resetErrorFlag() {
    	error1 = false;
    }
    	
    public boolean getErrorFlag() {
    	return error1;
    }
    	
    public Clause getQuery() {
    	
    	Term[] hvariables = new Term[headTerms.size()];
    		
    	for(int i=0; i< headTerms.size(); i++){
    		hvariables[i] = headTerms.get(i);
    	}
    		
    	headAtoms.add(termFactory.getFunctionalTerm("Q",hvariables));
    	
    	Term[] hAtoms = new Term[headAtoms.size()];
    		
    	for(int i=0; i< headAtoms.size(); i++){
    		hAtoms[i] = headAtoms.get(i);
    	}
    	
    	Term[] bAtoms = new Term[bodyAtoms.size()];
    		
    	for(int i=0; i< bodyAtoms.size(); i++){
    		bAtoms[i] = bodyAtoms.get(i);
    	}
    	
    	return new Clause(bAtoms, hAtoms[0]);
    }



    // $ANTLR start "parse"
    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:90:1: parse returns [boolean value] : clause EOF ;
    public final boolean parse() throws RecognitionException {
        boolean value = false;

        try {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:91:1: ( clause EOF )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:91:3: clause EOF
            {
            pushFollow(FOLLOW_clause_in_parse47);
            clause();

            state._fsp--;

            match(input,EOF,FOLLOW_EOF_in_parse49); 
             
            		value = !error1; 
            		

            }

        }
        catch (RecognitionException ex) {
             
            //		reportError(ex); 
            		value = false; 
            		throw ex; 
            	
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "parse"


    // $ANTLR start "clause"
    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:101:1: clause : headAtom '<-' bodyAtom ( ',' bodyAtom )* ;
    public final void clause() throws RecognitionException {
        try {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:102:2: ( headAtom '<-' bodyAtom ( ',' bodyAtom )* )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:102:4: headAtom '<-' bodyAtom ( ',' bodyAtom )*
            {
            pushFollow(FOLLOW_headAtom_in_clause70);
            headAtom();

            state._fsp--;

            match(input,11,FOLLOW_11_in_clause72); 
            pushFollow(FOLLOW_bodyAtom_in_clause74);
            bodyAtom();

            state._fsp--;

            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:102:27: ( ',' bodyAtom )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==12) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:102:28: ',' bodyAtom
            	    {
            	    match(input,12,FOLLOW_12_in_clause77); 
            	    pushFollow(FOLLOW_bodyAtom_in_clause79);
            	    bodyAtom();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

        }
        catch (RecognitionException ex) {
             
            		//reportError(ex); 
            		error1 = true; 
            		throw ex; 
            		
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "clause"


    // $ANTLR start "headAtom"
    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:109:1: headAtom : 'Q' '(' distinguishedTerm ( ',' distinguishedTerm )* ')' ;
    public final void headAtom() throws RecognitionException {
        try {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:110:2: ( 'Q' '(' distinguishedTerm ( ',' distinguishedTerm )* ')' )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:110:4: 'Q' '(' distinguishedTerm ( ',' distinguishedTerm )* ')'
            {
            match(input,13,FOLLOW_13_in_headAtom101); 
            match(input,14,FOLLOW_14_in_headAtom103); 
            pushFollow(FOLLOW_distinguishedTerm_in_headAtom105);
            distinguishedTerm();

            state._fsp--;

            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:110:30: ( ',' distinguishedTerm )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==12) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:110:31: ',' distinguishedTerm
            	    {
            	    match(input,12,FOLLOW_12_in_headAtom108); 
            	    pushFollow(FOLLOW_distinguishedTerm_in_headAtom110);
            	    distinguishedTerm();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            match(input,15,FOLLOW_15_in_headAtom114); 

            }

        }
        catch (RecognitionException ex) {
             
            		//reportError(ex); 
            		error1 = true; 
            		throw ex; 
            		
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "headAtom"


    // $ANTLR start "bodyAtom"
    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:117:1: bodyAtom : ( unaryAtom1 | unaryAtom2 | binaryAtom1 | binaryAtom2 | binaryAtom3 | binaryAtom4 );
    public final void bodyAtom() throws RecognitionException {
        try {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:118:2: ( unaryAtom1 | unaryAtom2 | binaryAtom1 | binaryAtom2 | binaryAtom3 | binaryAtom4 )
            int alt3=6;
            alt3 = dfa3.predict(input);
            switch (alt3) {
                case 1 :
                    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:118:4: unaryAtom1
                    {
                    pushFollow(FOLLOW_unaryAtom1_in_bodyAtom136);
                    unaryAtom1();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:119:4: unaryAtom2
                    {
                    pushFollow(FOLLOW_unaryAtom2_in_bodyAtom141);
                    unaryAtom2();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:120:5: binaryAtom1
                    {
                    pushFollow(FOLLOW_binaryAtom1_in_bodyAtom147);
                    binaryAtom1();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:121:4: binaryAtom2
                    {
                    pushFollow(FOLLOW_binaryAtom2_in_bodyAtom152);
                    binaryAtom2();

                    state._fsp--;


                    }
                    break;
                case 5 :
                    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:122:4: binaryAtom3
                    {
                    pushFollow(FOLLOW_binaryAtom3_in_bodyAtom157);
                    binaryAtom3();

                    state._fsp--;


                    }
                    break;
                case 6 :
                    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:123:4: binaryAtom4
                    {
                    pushFollow(FOLLOW_binaryAtom4_in_bodyAtom162);
                    binaryAtom4();

                    state._fsp--;


                    }
                    break;

            }
        }
        catch (RecognitionException ex) {
             
            		//reportError(ex); 
            		error1 = true; 
            		throw ex; 
            		
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "bodyAtom"


    // $ANTLR start "unaryAtom1"
    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:132:1: unaryAtom1 : predicate '(' var ')' ;
    public final void unaryAtom1() throws RecognitionException {
        String predicate1 = null;

        Term var2 = null;


        try {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:133:2: ( predicate '(' var ')' )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:133:4: predicate '(' var ')'
            {
            pushFollow(FOLLOW_predicate_in_unaryAtom1182);
            predicate1=predicate();

            state._fsp--;

            match(input,14,FOLLOW_14_in_unaryAtom1184); 
            pushFollow(FOLLOW_var_in_unaryAtom1186);
            var2=var();

            state._fsp--;

            match(input,15,FOLLOW_15_in_unaryAtom1188); 

            			
            		Term atom = termFactory.getFunctionalTerm(predicate1, var2);
            		bodyAtoms.add(atom);
            	

            }

        }
        catch (RecognitionException ex) {
             
            		//reportError(ex); 
            		error1 = true; 
            		throw ex;  
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "unaryAtom1"


    // $ANTLR start "unaryAtom2"
    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:143:1: unaryAtom2 : predicate '(' constant ')' ;
    public final void unaryAtom2() throws RecognitionException {
        String predicate3 = null;

        Term constant4 = null;


        try {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:144:2: ( predicate '(' constant ')' )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:144:4: predicate '(' constant ')'
            {
            pushFollow(FOLLOW_predicate_in_unaryAtom2208);
            predicate3=predicate();

            state._fsp--;

            match(input,14,FOLLOW_14_in_unaryAtom2210); 
            pushFollow(FOLLOW_constant_in_unaryAtom2212);
            constant4=constant();

            state._fsp--;

            match(input,15,FOLLOW_15_in_unaryAtom2214); 

            			
            		Term atom = termFactory.getFunctionalTerm(predicate3, constant4);
            		bodyAtoms.add(atom);
            	

            }

        }
        catch (RecognitionException ex) {
             
            		//reportError(ex); 
            		error1 = true; 
            		throw ex;  
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "unaryAtom2"


    // $ANTLR start "binaryAtom1"
    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:155:1: binaryAtom1 : predicate '(' var1 ',' var2 ')' ;
    public final void binaryAtom1() throws RecognitionException {
        String predicate5 = null;

        Term var16 = null;

        Term var27 = null;


        try {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:156:2: ( predicate '(' var1 ',' var2 ')' )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:156:4: predicate '(' var1 ',' var2 ')'
            {
            pushFollow(FOLLOW_predicate_in_binaryAtom1235);
            predicate5=predicate();

            state._fsp--;

            match(input,14,FOLLOW_14_in_binaryAtom1237); 
            pushFollow(FOLLOW_var1_in_binaryAtom1239);
            var16=var1();

            state._fsp--;

            match(input,12,FOLLOW_12_in_binaryAtom1241); 
            pushFollow(FOLLOW_var2_in_binaryAtom1243);
            var27=var2();

            state._fsp--;

            match(input,15,FOLLOW_15_in_binaryAtom1245); 

            	
            		Term atom = termFactory.getFunctionalTerm(predicate5, var16, var27);
            		bodyAtoms.add(atom);
            	

            }

        }
        catch (RecognitionException ex) {
             
            		//reportError(ex); 
            		error1 = true; 
            		throw ex; 
            		
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "binaryAtom1"


    // $ANTLR start "binaryAtom2"
    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:167:1: binaryAtom2 : predicate '(' var ',' constant ')' ;
    public final void binaryAtom2() throws RecognitionException {
        String predicate8 = null;

        Term var9 = null;

        Term constant10 = null;


        try {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:168:2: ( predicate '(' var ',' constant ')' )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:168:4: predicate '(' var ',' constant ')'
            {
            pushFollow(FOLLOW_predicate_in_binaryAtom2265);
            predicate8=predicate();

            state._fsp--;

            match(input,14,FOLLOW_14_in_binaryAtom2267); 
            pushFollow(FOLLOW_var_in_binaryAtom2269);
            var9=var();

            state._fsp--;

            match(input,12,FOLLOW_12_in_binaryAtom2271); 
            pushFollow(FOLLOW_constant_in_binaryAtom2273);
            constant10=constant();

            state._fsp--;

            match(input,15,FOLLOW_15_in_binaryAtom2275); 

            	
            		Term atom = termFactory.getFunctionalTerm(predicate8, var9, constant10);
            		bodyAtoms.add(atom);
            	

            }

        }
        catch (RecognitionException ex) {
             
            		//reportError(ex); 
            		error1 = true; 
            		throw ex; 
            		
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "binaryAtom2"


    // $ANTLR start "binaryAtom3"
    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:179:1: binaryAtom3 : predicate '(' constant ',' var ')' ;
    public final void binaryAtom3() throws RecognitionException {
        String predicate11 = null;

        Term constant12 = null;

        Term var13 = null;


        try {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:180:2: ( predicate '(' constant ',' var ')' )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:180:4: predicate '(' constant ',' var ')'
            {
            pushFollow(FOLLOW_predicate_in_binaryAtom3295);
            predicate11=predicate();

            state._fsp--;

            match(input,14,FOLLOW_14_in_binaryAtom3297); 
            pushFollow(FOLLOW_constant_in_binaryAtom3299);
            constant12=constant();

            state._fsp--;

            match(input,12,FOLLOW_12_in_binaryAtom3301); 
            pushFollow(FOLLOW_var_in_binaryAtom3303);
            var13=var();

            state._fsp--;

            match(input,15,FOLLOW_15_in_binaryAtom3305); 

            	
            		Term atom = termFactory.getFunctionalTerm(predicate11, constant12, var13);
            		bodyAtoms.add(atom);
            	

            }

        }
        catch (RecognitionException ex) {
             
            		//reportError(ex); 
            		error1 = true; 
            		throw ex; 
            		
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "binaryAtom3"


    // $ANTLR start "binaryAtom4"
    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:191:1: binaryAtom4 : predicate '(' con1 ',' con2 ')' ;
    public final void binaryAtom4() throws RecognitionException {
        String predicate14 = null;

        Term con115 = null;

        Term con216 = null;


        try {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:192:2: ( predicate '(' con1 ',' con2 ')' )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:192:4: predicate '(' con1 ',' con2 ')'
            {
            pushFollow(FOLLOW_predicate_in_binaryAtom4325);
            predicate14=predicate();

            state._fsp--;

            match(input,14,FOLLOW_14_in_binaryAtom4327); 
            pushFollow(FOLLOW_con1_in_binaryAtom4329);
            con115=con1();

            state._fsp--;

            match(input,12,FOLLOW_12_in_binaryAtom4331); 
            pushFollow(FOLLOW_con2_in_binaryAtom4333);
            con216=con2();

            state._fsp--;

            match(input,15,FOLLOW_15_in_binaryAtom4335); 

            	
            		Term atom = termFactory.getFunctionalTerm(predicate14, con115, con216);
            		bodyAtoms.add(atom);
            	

            }

        }
        catch (RecognitionException ex) {
             
            		//reportError(ex); 
            		error1 = true; 
            		throw ex; 
            		
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "binaryAtom4"


    // $ANTLR start "distinguishedTerm"
    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:203:1: distinguishedTerm : ( distinguishedConstant | distinguishedVar );
    public final void distinguishedTerm() throws RecognitionException {
        try {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:204:2: ( distinguishedConstant | distinguishedVar )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==SENTENCE) ) {
                alt4=1;
            }
            else if ( (LA4_0==16) ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:204:4: distinguishedConstant
                    {
                    pushFollow(FOLLOW_distinguishedConstant_in_distinguishedTerm355);
                    distinguishedConstant();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:205:4: distinguishedVar
                    {
                    pushFollow(FOLLOW_distinguishedVar_in_distinguishedTerm361);
                    distinguishedVar();

                    state._fsp--;


                    }
                    break;

            }
        }
        catch (RecognitionException ex) {
             
            		//reportError(ex); 
            		error1 = true; 
            		throw ex; 
            		
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "distinguishedTerm"


    // $ANTLR start "distinguishedConstant"
    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:211:1: distinguishedConstant : constant ;
    public final void distinguishedConstant() throws RecognitionException {
        Term constant17 = null;


        try {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:212:2: ( constant )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:212:4: constant
            {
            pushFollow(FOLLOW_constant_in_distinguishedConstant377);
            constant17=constant();

            state._fsp--;


            		headTerms.add(constant17);
            	

            }

        }
        catch (RecognitionException ex) {
             
            		//reportError(ex); 
            		error1 = true; 
            		throw ex; 
            		
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "distinguishedConstant"


    // $ANTLR start "distinguishedVar"
    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:221:1: distinguishedVar : '?' number ;
    public final void distinguishedVar() throws RecognitionException {
        String number18 = null;


        try {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:222:2: ( '?' number )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:222:4: '?' number
            {
            match(input,16,FOLLOW_16_in_distinguishedVar396); 
            pushFollow(FOLLOW_number_in_distinguishedVar398);
            number18=number();

            state._fsp--;


            	
            		Integer index = new Integer(number18);
                        	Term var = termFactory.getVariable(index.intValue());
            		headTerms.add(var);
            	

            }

        }
        catch (RecognitionException ex) {
             
            		//reportError(ex); 
            		error1 = true; 
            		throw ex; 
            		
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "distinguishedVar"


    // $ANTLR start "var"
    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:235:1: var returns [Term value] : '?' number ;
    public final Term var() throws RecognitionException {
        Term value = null;

        String number19 = null;


        try {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:236:2: ( '?' number )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:236:4: '?' number
            {
            match(input,16,FOLLOW_16_in_var424); 
            pushFollow(FOLLOW_number_in_var426);
            number19=number();

            state._fsp--;


            	
            		Integer index = new Integer(number19);
                        	Term var = termFactory.getVariable(index.intValue());
            		value = var;
            	

            }

        }
        catch (RecognitionException ex) {
             
            		//reportError(ex); 
            		error1 = true; 
            		throw ex; 
            		
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "var"


    // $ANTLR start "var1"
    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:249:1: var1 returns [Term value] : var ;
    public final Term var1() throws RecognitionException {
        Term value = null;

        Term var20 = null;


        try {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:250:2: ( var )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:250:4: var
            {
            pushFollow(FOLLOW_var_in_var1452);
            var20=var();

            state._fsp--;


            		value = var20;
            	

            }

        }
        catch (RecognitionException ex) {
             
            		//reportError(ex); 
            		error1 = true; 
            		throw ex; 
            		
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "var1"


    // $ANTLR start "var2"
    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:260:1: var2 returns [Term value] : var ;
    public final Term var2() throws RecognitionException {
        Term value = null;

        Term var21 = null;


        try {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:261:2: ( var )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:261:4: var
            {
            pushFollow(FOLLOW_var_in_var2479);
            var21=var();

            state._fsp--;


            		value = var21;
            	

            }

        }
        catch (RecognitionException ex) {
             
            		//reportError(ex); 
            		error1 = true; 
            		throw ex; 
            		
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "var2"


    // $ANTLR start "con1"
    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:271:1: con1 returns [Term value] : constant ;
    public final Term con1() throws RecognitionException {
        Term value = null;

        Term constant22 = null;


        try {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:272:2: ( constant )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:272:4: constant
            {
            pushFollow(FOLLOW_constant_in_con1506);
            constant22=constant();

            state._fsp--;


            		value = constant22;
            	

            }

        }
        catch (RecognitionException ex) {
             
            		//reportError(ex); 
            		error1 = true; 
            		throw ex; 
            		
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "con1"


    // $ANTLR start "con2"
    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:282:1: con2 returns [Term value] : constant ;
    public final Term con2() throws RecognitionException {
        Term value = null;

        Term constant23 = null;


        try {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:283:2: ( constant )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:283:4: constant
            {
            pushFollow(FOLLOW_constant_in_con2533);
            constant23=constant();

            state._fsp--;


            		value = constant23;
            	

            }

        }
        catch (RecognitionException ex) {
             
            		//reportError(ex); 
            		error1 = true; 
            		throw ex; 
            		
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "con2"


    // $ANTLR start "constant"
    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:294:1: constant returns [Term value] : SENTENCE ;
    public final Term constant() throws RecognitionException {
        Term value = null;

        Token SENTENCE24=null;

        try {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:295:2: ( SENTENCE )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:295:4: SENTENCE
            {
            SENTENCE24=(Token)match(input,SENTENCE,FOLLOW_SENTENCE_in_constant562); 

            		String cons = (SENTENCE24!=null?SENTENCE24.getText():null);
            		//Term con = termFactory.getConstant(cons.substring(1, cons.length()-1));
            		Term con = termFactory.getConstant(cons);
            		value = con;
            	

            }

        }
        catch (RecognitionException ex) {
             
            		//reportError(ex); 
            		error1 = true; 
            		throw ex; 
            		
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "constant"


    // $ANTLR start "predicate"
    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:308:1: predicate returns [String value] : ALPHAVAR ;
    public final String predicate() throws RecognitionException {
        String value = null;

        Token ALPHAVAR25=null;

        try {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:309:2: ( ALPHAVAR )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:309:4: ALPHAVAR
            {
            ALPHAVAR25=(Token)match(input,ALPHAVAR,FOLLOW_ALPHAVAR_in_predicate589); 
            value = (ALPHAVAR25!=null?ALPHAVAR25.getText():null);

            }

        }
        catch (RecognitionException ex) {
             
            		//reportError(ex); 
            		error1 = true; 
            		throw ex; 
            		
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "predicate"


    // $ANTLR start "number"
    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:316:1: number returns [String value] : NUMBER ;
    public final String number() throws RecognitionException {
        String value = null;

        Token NUMBER26=null;

        try {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:317:2: ( NUMBER )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:317:4: NUMBER
            {
            NUMBER26=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_number614); 
            value = (NUMBER26!=null?NUMBER26.getText():null);

            }

        }
        catch (RecognitionException ex) {
             
            		//reportError(ex); 
            		error1 = true; 
            		throw ex; 
            		
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "number"

    // Delegated rules


    protected DFA3 dfa3 = new DFA3(this);
    static final String DFA3_eotS =
        "\16\uffff";
    static final String DFA3_eofS =
        "\16\uffff";
    static final String DFA3_minS =
        "\1\5\1\16\1\4\1\14\1\6\1\uffff\1\4\1\14\2\uffff\1\4\3\uffff";
    static final String DFA3_maxS =
        "\1\5\1\16\1\20\1\17\1\6\1\uffff\1\20\1\17\2\uffff\1\20\3\uffff";
    static final String DFA3_acceptS =
        "\5\uffff\1\2\2\uffff\1\5\1\6\1\uffff\1\1\1\4\1\3";
    static final String DFA3_specialS =
        "\16\uffff}>";
    static final String[] DFA3_transitionS = {
            "\1\1",
            "\1\2",
            "\1\3\13\uffff\1\4",
            "\1\6\2\uffff\1\5",
            "\1\7",
            "",
            "\1\11\13\uffff\1\10",
            "\1\12\2\uffff\1\13",
            "",
            "",
            "\1\14\13\uffff\1\15",
            "",
            "",
            ""
    };

    static final short[] DFA3_eot = DFA.unpackEncodedString(DFA3_eotS);
    static final short[] DFA3_eof = DFA.unpackEncodedString(DFA3_eofS);
    static final char[] DFA3_min = DFA.unpackEncodedStringToUnsignedChars(DFA3_minS);
    static final char[] DFA3_max = DFA.unpackEncodedStringToUnsignedChars(DFA3_maxS);
    static final short[] DFA3_accept = DFA.unpackEncodedString(DFA3_acceptS);
    static final short[] DFA3_special = DFA.unpackEncodedString(DFA3_specialS);
    static final short[][] DFA3_transition;

    static {
        int numStates = DFA3_transitionS.length;
        DFA3_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA3_transition[i] = DFA.unpackEncodedString(DFA3_transitionS[i]);
        }
    }

    class DFA3 extends DFA {

        public DFA3(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 3;
            this.eot = DFA3_eot;
            this.eof = DFA3_eof;
            this.min = DFA3_min;
            this.max = DFA3_max;
            this.accept = DFA3_accept;
            this.special = DFA3_special;
            this.transition = DFA3_transition;
        }
        public String getDescription() {
            return "117:1: bodyAtom : ( unaryAtom1 | unaryAtom2 | binaryAtom1 | binaryAtom2 | binaryAtom3 | binaryAtom4 );";
        }
    }
 

    public static final BitSet FOLLOW_clause_in_parse47 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_parse49 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_headAtom_in_clause70 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_11_in_clause72 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_bodyAtom_in_clause74 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_12_in_clause77 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_bodyAtom_in_clause79 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_13_in_headAtom101 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_headAtom103 = new BitSet(new long[]{0x0000000000010010L});
    public static final BitSet FOLLOW_distinguishedTerm_in_headAtom105 = new BitSet(new long[]{0x0000000000009000L});
    public static final BitSet FOLLOW_12_in_headAtom108 = new BitSet(new long[]{0x0000000000010010L});
    public static final BitSet FOLLOW_distinguishedTerm_in_headAtom110 = new BitSet(new long[]{0x0000000000009000L});
    public static final BitSet FOLLOW_15_in_headAtom114 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryAtom1_in_bodyAtom136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryAtom2_in_bodyAtom141 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_binaryAtom1_in_bodyAtom147 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_binaryAtom2_in_bodyAtom152 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_binaryAtom3_in_bodyAtom157 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_binaryAtom4_in_bodyAtom162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_predicate_in_unaryAtom1182 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_unaryAtom1184 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_var_in_unaryAtom1186 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_unaryAtom1188 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_predicate_in_unaryAtom2208 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_unaryAtom2210 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_constant_in_unaryAtom2212 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_unaryAtom2214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_predicate_in_binaryAtom1235 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_binaryAtom1237 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_var1_in_binaryAtom1239 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_binaryAtom1241 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_var2_in_binaryAtom1243 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_binaryAtom1245 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_predicate_in_binaryAtom2265 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_binaryAtom2267 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_var_in_binaryAtom2269 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_binaryAtom2271 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_constant_in_binaryAtom2273 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_binaryAtom2275 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_predicate_in_binaryAtom3295 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_binaryAtom3297 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_constant_in_binaryAtom3299 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_binaryAtom3301 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_var_in_binaryAtom3303 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_binaryAtom3305 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_predicate_in_binaryAtom4325 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_binaryAtom4327 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_con1_in_binaryAtom4329 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_binaryAtom4331 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_con2_in_binaryAtom4333 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_binaryAtom4335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_distinguishedConstant_in_distinguishedTerm355 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_distinguishedVar_in_distinguishedTerm361 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constant_in_distinguishedConstant377 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_distinguishedVar396 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_number_in_distinguishedVar398 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_var424 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_number_in_var426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_var1452 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_var2479 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constant_in_con1506 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constant_in_con2533 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SENTENCE_in_constant562 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALPHAVAR_in_predicate589 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NUMBER_in_number614 = new BitSet(new long[]{0x0000000000000002L});

}