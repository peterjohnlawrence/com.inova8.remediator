// $ANTLR 3.2 Sep 23, 2009 12:02:23 /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g 2009-12-04 13:21:58


package com.inova8.requiem.parser;
import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;

class ConjunctiveQueriesLexer extends Lexer {
	private static final int SENTENCE=4;
	private static final int T__12=12;
	private static final int INT=8;
	private static final int NUMBER=6;
	private static final int EOF=-1;
	private static final int WS=10;
	private static final int T__13=13;
	private static final int ALPHA=7;
	private static final int CHAR=9;
	private static final int T__16=16;
	private static final int ALPHAVAR=5;
	private static final int T__14=14;
	private static final int T__11=11;
	private static final int T__15=15;


        private List<String> errors = new LinkedList<String>();
        
        public void displayRecognitionError(String[] tokenNames,RecognitionException e) {
            String hdr = getErrorHeader(e);
            String msg = getErrorMessage(e, tokenNames);
            errors.add(hdr + " " + msg);
        }
        
        public List<String> getErrors() {
            return errors;
        }    


    // delegates
    // delegators

    public ConjunctiveQueriesLexer() {;} 
    public ConjunctiveQueriesLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public ConjunctiveQueriesLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "/Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g"; }

    // $ANTLR start "T__11"
    private final void mT__11() throws RecognitionException {
        try {
            int _type = T__11;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:24:7: ( '<-' )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:24:9: '<-'
            {
            match("<-"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__11"

    // $ANTLR start "T__12"
    private final void mT__12() throws RecognitionException {
        try {
            int _type = T__12;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:25:7: ( ',' )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:25:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__12"

    // $ANTLR start "T__13"
    public final void mT__13() throws RecognitionException {
        try {
            int _type = T__13;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:26:7: ( 'Q' )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:26:9: 'Q'
            {
            match('Q'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__13"

    // $ANTLR start "T__14"
    public final void mT__14() throws RecognitionException {
        try {
            int _type = T__14;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:27:7: ( '(' )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:27:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__14"

    // $ANTLR start "T__15"
    public final void mT__15() throws RecognitionException {
        try {
            int _type = T__15;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:28:7: ( ')' )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:28:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__15"

    // $ANTLR start "T__16"
    public final void mT__16() throws RecognitionException {
        try {
            int _type = T__16;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:29:7: ( '?' )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:29:9: '?'
            {
            match('?'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__16"

    // $ANTLR start "NUMBER"
    public final void mNUMBER() throws RecognitionException {
        try {
            int _type = NUMBER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:326:9: ( ( '0' .. '9' )+ )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:326:11: ( '0' .. '9' )+
            {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:326:11: ( '0' .. '9' )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='0' && LA1_0<='9')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:326:11: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NUMBER"

    // $ANTLR start "SENTENCE"
    public final void mSENTENCE() throws RecognitionException {
        try {
            int _type = SENTENCE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:328:10: ( '\"' ALPHAVAR ( ' ' ALPHAVAR )* '\"' )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:328:12: '\"' ALPHAVAR ( ' ' ALPHAVAR )* '\"'
            {
            match('\"'); 
            mALPHAVAR(); 
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:328:25: ( ' ' ALPHAVAR )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==' ') ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:328:26: ' ' ALPHAVAR
            	    {
            	    match(' '); 
            	    mALPHAVAR(); 

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SENTENCE"

    // $ANTLR start "ALPHAVAR"
    public final void mALPHAVAR() throws RecognitionException {
        try {
            int _type = ALPHAVAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:330:11: ( ( ALPHA | INT | CHAR )+ )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:330:13: ( ALPHA | INT | CHAR )+
            {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:330:13: ( ALPHA | INT | CHAR )+
            int cnt3=0;
            loop3:
            do {
                int alt3=4;
                switch ( input.LA(1) ) {
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                    {
                    alt3=1;
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    {
                    alt3=2;
                    }
                    break;
                case '!':
                case '#':
                case '%':
                case '&':
                case '*':
                case '+':
                case '-':
                case '.':
                case ':':
                case '=':
                case '@':
                case '[':
                case ']':
                case '_':
                    {
                    alt3=3;
                    }
                    break;

                }

                switch (alt3) {
            	case 1 :
            	    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:330:14: ALPHA
            	    {
            	    mALPHA(); 

            	    }
            	    break;
            	case 2 :
            	    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:330:22: INT
            	    {
            	    mINT(); 

            	    }
            	    break;
            	case 3 :
            	    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:330:28: CHAR
            	    {
            	    mCHAR(); 

            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ALPHAVAR"

    // $ANTLR start "CHAR"
    public final void mCHAR() throws RecognitionException {
        try {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:333:8: ( ( '[' | ']' | '_' | '-' | '*' | '&' | '@' | '!' | '#' | '%' | '+' | '=' | ':' | '.' ) )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:333:12: ( '[' | ']' | '_' | '-' | '*' | '&' | '@' | '!' | '#' | '%' | '+' | '=' | ':' | '.' )
            {
            if ( input.LA(1)=='!'||input.LA(1)=='#'||(input.LA(1)>='%' && input.LA(1)<='&')||(input.LA(1)>='*' && input.LA(1)<='+')||(input.LA(1)>='-' && input.LA(1)<='.')||input.LA(1)==':'||input.LA(1)=='='||input.LA(1)=='@'||input.LA(1)=='['||input.LA(1)==']'||input.LA(1)=='_' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "CHAR"

    // $ANTLR start "ALPHA"
    public final void mALPHA() throws RecognitionException {
        try {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:336:9: ( ( 'a' .. 'z' | 'A' .. 'Z' )+ )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:336:13: ( 'a' .. 'z' | 'A' .. 'Z' )+
            {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:336:13: ( 'a' .. 'z' | 'A' .. 'Z' )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0>='A' && LA4_0<='Z')||(LA4_0>='a' && LA4_0<='z')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:
            	    {
            	    if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt4 >= 1 ) break loop4;
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "ALPHA"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:339:7: ( ( '0' .. '9' )+ )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:339:11: ( '0' .. '9' )+
            {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:339:11: ( '0' .. '9' )+
            int cnt5=0;
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( ((LA5_0>='0' && LA5_0<='9')) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:339:11: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt5 >= 1 ) break loop5;
                        EarlyExitException eee =
                            new EarlyExitException(5, input);
                        throw eee;
                }
                cnt5++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:341:7: ( ( ' ' | '\\t' | ( '\\r' | '\\r\\n' ) )+ )
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:341:11: ( ' ' | '\\t' | ( '\\r' | '\\r\\n' ) )+
            {
            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:341:11: ( ' ' | '\\t' | ( '\\r' | '\\r\\n' ) )+
            int cnt7=0;
            loop7:
            do {
                int alt7=4;
                switch ( input.LA(1) ) {
                case ' ':
                    {
                    alt7=1;
                    }
                    break;
                case '\t':
                    {
                    alt7=2;
                    }
                    break;
                case '\r':
                    {
                    alt7=3;
                    }
                    break;

                }

                switch (alt7) {
            	case 1 :
            	    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:341:12: ' '
            	    {
            	    match(' '); 

            	    }
            	    break;
            	case 2 :
            	    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:341:16: '\\t'
            	    {
            	    match('\t'); 

            	    }
            	    break;
            	case 3 :
            	    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:341:21: ( '\\r' | '\\r\\n' )
            	    {
            	    // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:341:21: ( '\\r' | '\\r\\n' )
            	    int alt6=2;
            	    int LA6_0 = input.LA(1);

            	    if ( (LA6_0=='\r') ) {
            	        int LA6_1 = input.LA(2);

            	        if ( (LA6_1=='\n') ) {
            	            alt6=2;
            	        }
            	        else {
            	            alt6=1;}
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 6, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt6) {
            	        case 1 :
            	            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:341:22: '\\r'
            	            {
            	            match('\r'); 

            	            }
            	            break;
            	        case 2 :
            	            // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:341:27: '\\r\\n'
            	            {
            	            match("\r\n"); 


            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt7 >= 1 ) break loop7;
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        throw eee;
                }
                cnt7++;
            } while (true);

            skip();

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    public void mTokens() throws RecognitionException {
        // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:1:8: ( T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | NUMBER | SENTENCE | ALPHAVAR | WS )
        int alt8=10;
        alt8 = dfa8.predict(input);
        switch (alt8) {
            case 1 :
                // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:1:10: T__11
                {
                mT__11(); 

                }
                break;
            case 2 :
                // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:1:16: T__12
                {
                mT__12(); 

                }
                break;
            case 3 :
                // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:1:22: T__13
                {
                mT__13(); 

                }
                break;
            case 4 :
                // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:1:28: T__14
                {
                mT__14(); 

                }
                break;
            case 5 :
                // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:1:34: T__15
                {
                mT__15(); 

                }
                break;
            case 6 :
                // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:1:40: T__16
                {
                mT__16(); 

                }
                break;
            case 7 :
                // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:1:46: NUMBER
                {
                mNUMBER(); 

                }
                break;
            case 8 :
                // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:1:53: SENTENCE
                {
                mSENTENCE(); 

                }
                break;
            case 9 :
                // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:1:62: ALPHAVAR
                {
                mALPHAVAR(); 

                }
                break;
            case 10 :
                // /Users/hekanibru/Documents/DPhil/Prototype/Grammar/ConjunctiveQueries.g:1:71: WS
                {
                mWS(); 

                }
                break;

        }

    }


    protected DFA8 dfa8 = new DFA8(this);
    static final String DFA8_eotS =
        "\3\uffff\1\13\3\uffff\1\14\5\uffff";
    static final String DFA8_eofS =
        "\15\uffff";
    static final String DFA8_minS =
        "\1\11\2\uffff\1\41\3\uffff\1\41\5\uffff";
    static final String DFA8_maxS =
        "\1\172\2\uffff\1\172\3\uffff\1\172\5\uffff";
    static final String DFA8_acceptS =
        "\1\uffff\1\1\1\2\1\uffff\1\4\1\5\1\6\1\uffff\1\10\1\11\1\12\1\3"+
        "\1\7";
    static final String DFA8_specialS =
        "\15\uffff}>";
    static final String[] DFA8_transitionS = {
            "\1\12\3\uffff\1\12\22\uffff\1\12\1\11\1\10\1\11\1\uffff\2\11"+
            "\1\uffff\1\4\1\5\2\11\1\2\2\11\1\uffff\12\7\1\11\1\uffff\1\1"+
            "\1\11\1\uffff\1\6\21\11\1\3\12\11\1\uffff\1\11\1\uffff\1\11"+
            "\1\uffff\32\11",
            "",
            "",
            "\1\11\1\uffff\1\11\1\uffff\2\11\3\uffff\2\11\1\uffff\2\11\1"+
            "\uffff\13\11\2\uffff\1\11\2\uffff\34\11\1\uffff\1\11\1\uffff"+
            "\1\11\1\uffff\32\11",
            "",
            "",
            "",
            "\1\11\1\uffff\1\11\1\uffff\2\11\3\uffff\2\11\1\uffff\2\11\1"+
            "\uffff\12\7\1\11\2\uffff\1\11\2\uffff\34\11\1\uffff\1\11\1\uffff"+
            "\1\11\1\uffff\32\11",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA8_eot = DFA.unpackEncodedString(DFA8_eotS);
    static final short[] DFA8_eof = DFA.unpackEncodedString(DFA8_eofS);
    static final char[] DFA8_min = DFA.unpackEncodedStringToUnsignedChars(DFA8_minS);
    static final char[] DFA8_max = DFA.unpackEncodedStringToUnsignedChars(DFA8_maxS);
    static final short[] DFA8_accept = DFA.unpackEncodedString(DFA8_acceptS);
    static final short[] DFA8_special = DFA.unpackEncodedString(DFA8_specialS);
    static final short[][] DFA8_transition;

    static {
        int numStates = DFA8_transitionS.length;
        DFA8_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA8_transition[i] = DFA.unpackEncodedString(DFA8_transitionS[i]);
        }
    }

    class DFA8 extends DFA {

        public DFA8(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = DFA8_eot;
            this.eof = DFA8_eof;
            this.min = DFA8_min;
            this.max = DFA8_max;
            this.accept = DFA8_accept;
            this.special = DFA8_special;
            this.transition = DFA8_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | NUMBER | SENTENCE | ALPHAVAR | WS );";
        }
    }
 

}