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
        INT_ZERO_LIT,
        FLOAT_LIT,
        STRING_LIT,
        MINUS,
        EXCLAMATION,
        R_ARROW,
        L_ARROW,
        ASSIGNMENT
    }

    private final String rawInput;
    private int posOverall, posInLine, line;
    IToken.SourceLocation tokenStart;
    boolean nextCalled = false;
    private State currState;


    public Lexer(String rawInput) {
        this.rawInput = rawInput;
        this.posOverall = this.posInLine = line = 0;
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



    @Override
    public IToken next() throws LexicalException {
        nextCalled = true;
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
                        case '-' -> {
                            currState = State.MINUS;
                        }
                        case '!' -> {
                            currState = State.EXCLAMATION;
                        }
                        case '<' -> {
                            currState = State.L_ARROW;
                        }
                        case '>' -> {
                            currState = State.R_ARROW;
                        }
                        case '=' -> {
                            currState = State.ASSIGNMENT;
                        }
                        case '"' -> {
                            currState = State.STRING_LIT;
                        }
                        case '0' -> {
                            currState = State.INT_ZERO_LIT;
                        }
                        case '1','2','3','4','5','6','7','8','9' -> {
                            currState = State.INT_LIT;
                        }
                        case 'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x', 'y', 'z', 'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V', 'W', 'X', 'Y', 'Z', '$', '_' -> {
                            currState = State.IDENT;
                        }

                    }
                }
                case IDENT -> {
                    switch(ch) {
                        case ' ', '\n', '\t', '\r' -> {
                            currState = State.START;
                        }
                        default -> {
                            throw new LexicalException("Invalid identifier!", line, posInLine);
                        }
                    }
                }
                case FLOAT_LIT -> {

                }
                case INT_LIT -> {

                }
                case INT_ZERO_LIT -> {}
                case STRING_LIT -> {}
                case MINUS -> {}
                case EXCLAMATION -> {}
                case R_ARROW -> {}
                case L_ARROW -> {}
                case ASSIGNMENT -> {}
            }
        }
    }

    @Override
    public IToken peek() throws LexicalException {
        return null;

    }
}
