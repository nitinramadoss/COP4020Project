package edu.ufl.cise.plc;

import java.util.ArrayList;

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
    private class Node {
        State state;
        ArrayList<Node> children;

        public Node(State state) {
            this.state = state;
        }
    }

    private void createDFA(){
        Node start = new Node(State.START);
        // implement
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
