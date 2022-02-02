package edu.ufl.cise.plc;

import java.util.ArrayList;
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

    private final String rawInput;
    private int posOverall, posInLine, line;
    IToken.SourceLocation tokenStart;
    private State currState;

    // DFA representation
    private HashMap<State, List<State>> dfa;

    private final List<Token> tokens = new ArrayList<>();

    public Lexer(String rawInput) {
        this.rawInput = rawInput;
        this.posOverall = this.posInLine = line = 0;
        createDFA(rawInput.toCharArray());
    }

    private char advance() {
        posInLine++;
        return rawInput.charAt(posOverall++);
    }

    private Token makeToken(IToken.Kind kind) {
        return makeToken(kind, null);
    }

    private Token makeToken(IToken.Kind kind, String literal) {
        return new Token(kind, literal, tokenStart);
    }

    private void createDFA(char[] chars){
        dfa = new HashMap<>();
        dfa.put(State.START, Arrays.asList(State.IDENT, State.COMMENT, State.WHITE_SPACE, State.TYPE));

    }

    @Override
    public IToken next() throws LexicalException {
        while (true) {
            currState = State.START;

            if (posOverall >= rawInput.length()) {
                return makeToken(IToken.Kind.EOF);
            }
            char ch = advance();

            switch(currState) {
                case START -> {
                    tokenStart = new IToken.SourceLocation(line, posInLine);
                    switch (ch) {
                        case ' ', '\t', '\r' -> {}

                        case '\n' -> {
                            posInLine = 0;
                            line++;
                        }

                        // Following cases apply to single-character tokens. Possibly simplify w/ Map<char, Kind>
                        case '+' -> {
                            return makeToken(IToken.Kind.PLUS);
                        }
                        case '*' -> {
                            return makeToken(IToken.Kind.TIMES);
                        }
                        case '/' -> {
                            return makeToken(IToken.Kind.DIV);
                        }
                        case '%' -> {
                            return makeToken(IToken.Kind.MOD);
                        }
                        case '&' -> {
                            return makeToken(IToken.Kind.AND);
                        }
                        case '|' -> {
                            return makeToken(IToken.Kind.OR);
                        }
                        case ',' -> {
                            return makeToken(IToken.Kind.COMMA);
                        }
                        case '^' -> {
                            return makeToken(IToken.Kind.RETURN);
                        }
                        case ';' -> {
                            return makeToken(IToken.Kind.SEMI);
                        }

                        // Non single-character tokens

                    }
                }
                // Cases for other states
            }
        }
    }

    @Override
    public IToken peek() throws LexicalException {
        return null;

    }
}
