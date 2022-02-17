package edu.ufl.cise.plc;

import edu.ufl.cise.plc.ast.ASTNode;

public class Parser implements IParser{
    // Current token
    IToken t;
    Lexer lexer;

    public Parser(String input) {
        lexer = new Lexer(input);
    }

    @Override
    public ASTNode parse() throws PLCException {
        return null;
    }

    void match(IToken.Kind c) throws LexicalException, SyntaxException {
        if (isKind(c)) {
            consume();
        } else {
            throw new SyntaxException("Invalid syntax!");
        }
    }

    // Helper functions to check kind of token
    protected boolean isKind(IToken.Kind kind) {
        return t.getKind() == kind;
    }

    protected boolean isKind(IToken.Kind... kinds) {
        for (IToken.Kind k : kinds) {
            if (k == t.getKind())
                return true;
        }
        return false;
    }

    void consume() throws LexicalException {
       t = lexer.next();
    }

    public void expr() {
        try {
            conditionalExpr();
            logicalOrExpr();
        } catch (LexicalException e) {
            e.printStackTrace();
        } catch (SyntaxException e) {
            e.printStackTrace();
        }
    }

    public void conditionalExpr() throws LexicalException, SyntaxException {
        if (isKind(IToken.Kind.KW_IF)) {
            consume();
            match(IToken.Kind.LPAREN);
            expr();
            match(IToken.Kind.RPAREN);
            expr();
        }

        if (isKind(IToken.Kind.KW_ELSE)) {
            consume();
            expr();
            match(IToken.Kind.KW_FI);
        }
    }

    public void logicalOrExpr() throws LexicalException, SyntaxException {
        logicalAndExpr();
        while(isKind(IToken.Kind.OR)) {
            consume();
            logicalAndExpr();
        }
    }

    public void logicalAndExpr() throws LexicalException, SyntaxException {
        comparisonExpr();
        while(isKind(IToken.Kind.AND)) {
            consume();
            comparisonExpr();
        }
    }

    public void comparisonExpr() throws LexicalException, SyntaxException {
        additiveExpr();
        while(isKind(IToken.Kind.GT, IToken.Kind.LT, IToken.Kind.EQUALS, IToken.Kind.NOT_EQUALS, IToken.Kind.GE, IToken.Kind.LE)) {
            consume();
            additiveExpr();
        }
    }

    public void additiveExpr() throws LexicalException, SyntaxException {
        multiplicativeExpr();
        while(isKind(IToken.Kind.PLUS,IToken.Kind.MINUS)) {
            consume();
            multiplicativeExpr();
        }
    }

    public void multiplicativeExpr() throws LexicalException, SyntaxException {
        unaryExpr();
        while(isKind(IToken.Kind.TIMES,IToken.Kind.DIV,IToken.Kind.MOD)) {
            consume();
            unaryExpr();
        }
    }

    public void unaryExpr() throws LexicalException, SyntaxException {
        if(isKind(IToken.Kind.BANG,IToken.Kind.MINUS, IToken.Kind.COLOR_OP, IToken.Kind.IMAGE_OP)) {
            consume();
            unaryExpr();
        } else {
            unaryExprPostFix();
        }
    }

    public void unaryExprPostFix() throws LexicalException, SyntaxException {
        primaryExpr();
        pixelSel();
    }

    public void primaryExpr() throws LexicalException {
        if(isKind(IToken.Kind.BOOLEAN_LIT, IToken.Kind.STRING_LIT, IToken.Kind.INT_LIT, IToken.Kind.FLOAT_LIT, IToken.Kind.IDENT, IToken.Kind.LPAREN)) {
            consume();
        }
    }

    public void pixelSel() throws LexicalException, SyntaxException {
        if (isKind(IToken.Kind.LSQUARE)) {
            consume();
            expr();
            match(IToken.Kind.COMMA);
            expr();
            match(IToken.Kind.RSQUARE);
        }
    }
}
