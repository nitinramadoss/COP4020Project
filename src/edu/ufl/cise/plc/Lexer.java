package edu.ufl.cise.plc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Lexer implements ILexer {
    //DFA states
    private enum State {
        START,
        IDENT,
        INT_LIT,
        FLOAT_LIT,
        STRING_LIT,
        RESERVED,
        TYPE,
        IMAGE_OP,
        COLOR_OP,
        COLOR_CONST,
        BOOLEAN_LIT,
        OTHER_KEYWORDS,
        COMMENT,
        WHITE_SPACE
    }
    // DFA representation
    private HashMap<State, List<State>> dfa;

    private void createDFA(){
        dfa = new HashMap<>();
        dfa.put(State.START, Arrays.asList(State.IDENT, State.COMMENT, State.WHITE_SPACE, State.TYPE));
    }

    @Override
    public IToken next() throws LexicalException {
        return null;
    }

    @Override
    public IToken peek() throws LexicalException {
        return null;
    }
}
