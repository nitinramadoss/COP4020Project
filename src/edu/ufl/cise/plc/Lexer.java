package edu.ufl.cise.plc;

import java.util.HashMap;
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
        ASSIGNMENT,
        COMMENT
    }

    private final String rawInput;
    private int posOverall, posInLine, line;
    IToken.SourceLocation tokenStart;
    private State currState;

    private HashMap<String, IToken.Kind> reserved;


    public Lexer(String rawInput) {
        this.rawInput = rawInput;
        this.posOverall = this.posInLine = this.line = 0;
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

    private void goBack() {
        posOverall--;
        posInLine--;
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
                if (currState != State.START) {
                    throw new LexicalException("Unexpected end of file", tokenStart);
                } else {
                    switch (currState) {
                        case INT_LIT -> {
                            return makeToken(IToken.Kind.INT_LIT, literal.toString());
                        }
                    }
                }

                return makeToken(IToken.Kind.EOF);
            }
            char ch = advance();

            switch(currState) {
                case START -> {
                    tokenStart = new IToken.SourceLocation(line, posInLine-1);
                    switch (ch) {
                        case ' ', '\t', '\r' -> {}

                        case '\n' -> {
                            posInLine = 0;
                            line++;
                        }

                        // Single character tokens
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

                        // State-switching characters
                        case '#' -> {
                            currState = State.COMMENT;
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
                            if (Character.isJavaIdentifierStart(ch)) {
                                literal.append(ch);
                                currState = State.IDENT;
                            } else {
                                throw new LexicalException("Character is not supported", tokenStart);
                            }
                        }
                    }
                }
                case IDENT -> {
                    String litString = literal.toString();
                    if (Character.isJavaIdentifierPart(ch)) {
                        literal.append(ch);
                    } else {
                        goBack();
                        return makeToken(IToken.Kind.IDENT, litString);
                    }
                }
                case FLOAT_LIT -> {
                    if (Character.isDigit(ch)) {
                        literal.append(ch);
                    } else {
                        goBack();
                        return makeToken(IToken.Kind.FLOAT_LIT, literal.toString());
                    }

                }
                case INT_LIT -> {
                    if (ch == '.') {
                        literal.append(ch);
                        currState = State.FLOAT_LIT;
                    } else if (Character.isDigit(ch)) {
                        literal.append(ch);
                    } else {
                        try {
                            int x = Integer.parseInt(literal.toString());
                        } catch (NumberFormatException e) {
                            throw new LexicalException("Number is too large to be formatted as an integer", tokenStart);
                        }

                        goBack();
                        return makeToken(IToken.Kind.INT_LIT, literal.toString());
                    }

                }
                case INT_ZERO_LIT -> {
                    if (ch == '.') {
                        literal.append(ch);
                        currState = State.FLOAT_LIT;
                    } else {
                        goBack();
                        return makeToken(IToken.Kind.INT_LIT, literal.toString());
                    }
                }
                case STRING_LIT -> {
                    if (ch == '"') {
                        literal.append(ch);
                        return makeToken(IToken.Kind.STRING_LIT, literal.toString());
                    } else {
                        literal.append(ch);
                    }
                }
                case MINUS -> {
                        if (ch == '>') {
                        literal.append(ch);
                        return makeToken(IToken.Kind.RARROW, literal.toString());
                    } else {
                        goBack();
                        return makeToken(IToken.Kind.MINUS, literal.toString());
                    }
                }
                case EXCLAMATION -> {
                    if (ch == '=') {
                        literal.append(ch);
                        return makeToken(IToken.Kind.NOT_EQUALS, literal.toString());
                    } else {
                        goBack();
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
                        goBack();
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
                        goBack();
                        return makeToken(IToken.Kind.LT, literal.toString());
                    }
                }
                case ASSIGNMENT -> {
                    if (ch == '=') {
                        literal.append(ch);
                        return makeToken(IToken.Kind.EQUALS, literal.toString());
                    } else {
                        goBack();
                        return makeToken(IToken.Kind.ASSIGN, literal.toString());
                    }
                }
                case COMMENT -> {
                    if (ch == '\n') {
                        goBack();
                        currState = State.START;
                    }
                }
            }
        }
    }

    @Override
    public IToken peek() throws LexicalException {
        int tmp_po = posOverall;
        int tmp_pil = posInLine;
        int tmp_line = line;
        IToken.SourceLocation tmp_itsl = tokenStart;

        IToken tmp = next();

        posOverall = tmp_po;
        posInLine = tmp_pil;
        line = tmp_line;
        tokenStart = tmp_itsl;

        return tmp;
    }
}
