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

    private void addToken(IToken.Kind kind) {
        addToken(kind, null);
    }

    private void addToken(IToken.Kind kind, String literal) {
        Token t = new Token(kind, literal, tokenStart);
        tokens.add(t);
    }

    private void createDFA(char[] chars){
        dfa = new HashMap<>();
        dfa.put(State.START, Arrays.asList(State.IDENT, State.COMMENT, State.WHITE_SPACE, State.TYPE));

        currState = State.START;
        int startPos;

        while (true) {
            if (posOverall >= rawInput.length()) {
                addToken(IToken.Kind.EOF);
                break;
            }
            char ch = advance();

            switch(currState) {
                case START -> {
                    tokenStart = new IToken.SourceLocation(line, posInLine);
                    switch (ch) {
                        case ' ', '\t', '\r' -> {}

                        case '\n' -> {
                            posInLine = 0;
                            line += 1;
                        }

                        // Following cases apply to single-character tokens. Possibly simplify w/ Map<char, Kind>
                        case '+' -> {
                            addToken(IToken.Kind.PLUS);
                        }
                        case '*' -> {
                            addToken(IToken.Kind.TIMES);
                        }
                        case '/' -> {
                            addToken(IToken.Kind.DIV);
                        }
                        case '%' -> {
                            addToken(IToken.Kind.MOD);
                        }
                        case '&' -> {
                            addToken(IToken.Kind.AND);
                        }
                        case '|' -> {
                            addToken(IToken.Kind.OR);
                        }
                        case ',' -> {
                            addToken(IToken.Kind.COMMA);
                        }
                        case '^' -> {
                            addToken(IToken.Kind.RETURN);
                        }
                        case ';' -> {
                            addToken(IToken.Kind.SEMI);
                        }

                        // Non single-character tokens


                    }
                }
                // Cases for other states
            }
        }
        for (Token token : tokens) {
            System.out.println(token.getKind());
        }
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
