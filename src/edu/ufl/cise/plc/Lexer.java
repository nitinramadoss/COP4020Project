package edu.ufl.cise.plc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

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
        String literal = "";

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
                        default -> {
                            if (Pattern.matches("[a-z]|[A-Z]|[$]|[_]", "" + ch)) {
                                currState = State.IDENT;
                            } else if (Pattern.matches("[1-9]", "" + ch)) {
                                currState = State.INT_LIT;
                            }
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
                    if (Pattern.matches("[0-9]", "" + ch)) {
                        literal += ch;
                    } else if (ch == ' ') {
                        currState = State.START;
                    } else {
                        throw new LexicalException("Invalid float literal!", line, posInLine);
                    }

                }
                case INT_LIT -> {
                    if (ch == '.') {
                        currState = State.FLOAT_LIT;
                    } else if (Pattern.matches("[1-9]", "" + ch)) {
                        literal += ch;
                    } else if (ch == ' ') {
                        currState = State.START;
                    } else {
                        throw new LexicalException("Invalid int literal!", line, posInLine);
                    }

                }
                case INT_ZERO_LIT -> {
                    if (ch == '.') {
                        currState = State.FLOAT_LIT;
                    } else if (ch == ' ') {
                        currState = State.START;
                    } else {
                        throw new LexicalException("Invalid int literal!", line, posInLine);
                    }
                }
                case STRING_LIT -> {
//                    if (ch == '"') {
//                        literal += ch;
//                        return makeToken(IToken.Kind.STRING_LIT, literal);
//                    } else if (Pattern.matches("[a-z]|[1-9]|[A-Z]", "" + ch)) {
//
//                    }
                }
                case MINUS -> {
                    literal += ch;

                    if (ch == '>') {
                        return makeToken(IToken.Kind.RARROW, literal);
                    } else if (ch == ' ') {
                        currState = State.START;
                        return makeToken(IToken.Kind.RARROW, literal);
                    } else {
                        throw new LexicalException("Invalid token!", line, posInLine);
                    }
                }
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
