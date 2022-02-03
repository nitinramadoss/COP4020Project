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

    private HashMap<String, IToken.Kind> reserved;


    public Lexer(String rawInput) {
        this.rawInput = rawInput;
        this.posOverall = this.posInLine = line = 0;
        initMap();
    }

    private void initMap() {
       reserved = new HashMap<String, IToken.Kind>() {{
           put("if", IToken.Kind.KW_IF);
           put("fi", IToken.Kind.KW_FI);
           put("else", IToken.Kind.KW_ELSE);
           put("BLACK", IToken.Kind.COLOR_CONST);
           put("BLUE", IToken.Kind.COLOR_CONST);
           put("CYAN", IToken.Kind.COLOR_CONST);
           put("DARK_GRAY", IToken.Kind.COLOR_CONST);
           put("GRAY", IToken.Kind.COLOR_CONST);
           put("GREEN", IToken.Kind.COLOR_CONST);
           put("LIGHT_GRAY", IToken.Kind.COLOR_CONST);
           put("MAGENTA", IToken.Kind.COLOR_CONST);
           put("ORANGE", IToken.Kind.COLOR_CONST);
           put("PINK", IToken.Kind.COLOR_CONST);
           put("RED", IToken.Kind.COLOR_CONST);
           put("WHITE", IToken.Kind.COLOR_CONST);
           put("YELLOW", IToken.Kind.COLOR_CONST);
           put("write", IToken.Kind.KW_WRITE);
           put("console", IToken.Kind.KW_CONSOLE);
           put("true", IToken.Kind.BOOLEAN_LIT);
           put("false", IToken.Kind.BOOLEAN_LIT);
           put("int", IToken.Kind.TYPE);
           put("float", IToken.Kind.TYPE);
           put("string", IToken.Kind.TYPE);
           put("boolean", IToken.Kind.TYPE);
           put("color", IToken.Kind.TYPE);
           put("image", IToken.Kind.TYPE);
           put("getRed", IToken.Kind.COLOR_OP);
           put("getGreen", IToken.Kind.COLOR_OP);
           put("getBlue", IToken.Kind.COLOR_OP);
           put("getWidth", IToken.Kind.IMAGE_OP);
           put("getHeight", IToken.Kind.IMAGE_OP);
           put("void", IToken.Kind.KW_VOID);
        }};
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
        StringBuilder literal = new StringBuilder();
        currState = State.START;

        while (true) {

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
                        case '(' -> {
                            return makeToken(IToken.Kind.LPAREN);
                        }
                        case ')' -> {
                            return makeToken(IToken.Kind.RPAREN);
                        }
                        case '[' -> {
                            return makeToken(IToken.Kind.LSQUARE);
                        }
                        case ']' -> {
                            return makeToken(IToken.Kind.RSQUARE);
                        }
                        case '-' -> {
                            literal.append(ch);
                            currState = State.MINUS;
                        }
                        case '!' -> {
                            literal.append(ch);
                            currState = State.EXCLAMATION;
                        }
                        case '<' -> {
                            literal.append(ch);
                            currState = State.L_ARROW;
                        }
                        case '>' -> {
                            literal.append(ch);
                            currState = State.R_ARROW;
                        }
                        case '=' -> {
                            literal.append(ch);
                            currState = State.ASSIGNMENT;
                        }
                        case '"' -> {
                            literal.append(ch);
                            currState = State.STRING_LIT;
                        }
                        case '0' -> {
                            literal.append(ch);
                            currState = State.INT_ZERO_LIT;
                        }
                        default -> {
                            if (Pattern.matches("[a-z]|[A-Z]|[$]|[_]", "" + ch)) {
                                literal.append(ch);
                                currState = State.IDENT;
                            } else if (Pattern.matches("[1-9]", "" + ch)) {
                                literal.append(ch);
                                currState = State.INT_LIT;
                            }
                        }
                    }
                }
                case IDENT -> {
                    String litString = literal.toString();
                    if (reserved.containsKey(litString)) {
                        return makeToken(reserved.get(litString), litString);
                    } else if (Pattern.matches("[a-z]|[A-Z]|[$]|[_]|[0-9]", "" + ch)) {
                        literal.append(ch);
                    } else {
                        return makeToken(IToken.Kind.IDENT, litString);
                    }
                }
                case FLOAT_LIT -> {
                    if (Pattern.matches("[0-9]", "" + ch)) {
                        literal.append(ch);
                    } else {
                        return makeToken(IToken.Kind.FLOAT_LIT, literal.toString());
                    }

                }
                case INT_LIT -> {
                    if (ch == '.') {
                        literal.append(ch);
                        currState = State.FLOAT_LIT;
                    } else if (Pattern.matches("[1-9]", "" + ch)) {
                        literal.append(ch);
                    } else {
                        return makeToken(IToken.Kind.INT_LIT, literal.toString());
                    }

                }
                case INT_ZERO_LIT -> {
                    if (ch == '.') {
                        literal.append(ch);
                        currState = State.FLOAT_LIT;
                    } else {
                        return makeToken(IToken.Kind.INT_LIT, literal.toString());
                    }
                }
                case STRING_LIT -> {
                    if (ch == '"') {
                        literal.append(ch);
                        return makeToken(IToken.Kind.STRING_LIT, literal.toString());
                    } else if (Pattern.matches("([\b|\r|\t|\n|\f])|[^\"]", "" + ch)) {
                        literal.append(ch);
                    }
                }
                case MINUS -> {
                    if (ch == '>') {
                        literal.append(ch);
                        return makeToken(IToken.Kind.RARROW, literal.toString());
                    } else {
                        return makeToken(IToken.Kind.MINUS, literal.toString());
                    }
                }
                case EXCLAMATION -> {
                    if (ch == '=') {
                        literal.append(ch);
                        return makeToken(IToken.Kind.NOT_EQUALS, literal.toString());
                    } else {
                        return makeToken(IToken.Kind.BANG, literal.toString());
                    }
                }
                case R_ARROW -> {
                    if (ch == '>') {
                        literal.append(ch);
                        return makeToken(IToken.Kind.RANGLE, literal.toString());
                    } else if (ch == '=') {
                        literal.append(ch);
                        return makeToken(IToken.Kind.GE, literal.toString());
                    } else {
                        return makeToken(IToken.Kind.GT, literal.toString());
                    }
                }
                case L_ARROW -> {
                    if (ch == '-') {
                        literal.append(ch);
                        return makeToken(IToken.Kind.LARROW, literal.toString());
                    } else if (ch == '<') {
                        literal.append(ch);
                        return makeToken(IToken.Kind.LANGLE, literal.toString());
                    } else if (ch == '=') {
                        literal.append(ch);
                        return makeToken(IToken.Kind.LE, literal.toString());
                    } else {
                        return makeToken(IToken.Kind.LT, literal.toString());
                    }
                }
                case ASSIGNMENT -> {
                    if (ch == '=') {
                        literal.append(ch);
                        return makeToken(IToken.Kind.EQUALS, literal.toString());
                    } else {
                        return makeToken(IToken.Kind.ASSIGN, literal.toString());
                    }
                }
            }
        }
    }

    @Override
    public IToken peek() throws LexicalException {
        return null;

    }
}
