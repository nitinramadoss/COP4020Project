package edu.ufl.cise.plc;

import edu.ufl.cise.plc.ast.*;

public class Parser implements IParser{
    // Current token
    IToken t;
    Lexer lexer;

    public Parser(String input) {
        lexer = new Lexer(input);
        try {
            consume();
        } catch (LexicalException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ASTNode parse() throws PLCException {
        return expr();
    }

    void match(IToken.Kind c) throws LexicalException, SyntaxException {
        if (isKind(c)) {
            consume();
        } else {
            throw new SyntaxException("Syntax error!");
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

    public Expr expr() throws LexicalException, SyntaxException{
        Expr expr;
        if (isKind(IToken.Kind.KW_IF)) {
            expr = conditionalExpr();
        } else {
            expr = logicalOrExpr();
        }

        return expr;
    }

    public Expr conditionalExpr() throws LexicalException, SyntaxException {
        IToken firstToken = t;
        Expr conditional = null;
        Expr cond = null;
        Expr trueCase = null;
        Expr falseCase = null;

        if (isKind(IToken.Kind.KW_IF)) {
            consume();
            match(IToken.Kind.LPAREN);
            cond = expr();
            match(IToken.Kind.RPAREN);
            trueCase = expr();
        }

        if (isKind(IToken.Kind.KW_ELSE)) {
            consume();
            falseCase = expr();
        }

        match(IToken.Kind.KW_FI);
        conditional = new ConditionalExpr(firstToken, cond, trueCase, falseCase);

        return conditional;
    }

    public Expr logicalOrExpr() throws LexicalException, SyntaxException {
        IToken firstToken = t;
        Expr left;
        Expr right;

        left = logicalAndExpr();
        while(isKind(IToken.Kind.OR)) {
            IToken op = t;
            consume();
            right = logicalAndExpr();
            left = new BinaryExpr(firstToken, left, op, right);
        }

        return left;
    }

    public Expr logicalAndExpr() throws LexicalException, SyntaxException {
        IToken firstToken = t;
        Expr left;
        Expr right;

        left = comparisonExpr();
        while(isKind(IToken.Kind.AND)) {
            IToken op = t;
            consume();
            right = comparisonExpr();
            left = new BinaryExpr(firstToken, left, op, right);
        }

        return left;
    }

    public Expr comparisonExpr() throws LexicalException, SyntaxException {
        IToken firstToken = t;
        Expr left;
        Expr right;

        left = additiveExpr();
        while(isKind(IToken.Kind.GT, IToken.Kind.LT, IToken.Kind.EQUALS, IToken.Kind.NOT_EQUALS, IToken.Kind.GE, IToken.Kind.LE)) {
            IToken op = t;
            consume();
            right = additiveExpr();
            left = new BinaryExpr(firstToken, left, op, right);
        }

        return left;
    }

    public Expr additiveExpr() throws LexicalException, SyntaxException {
        IToken firstToken = t;
        Expr left;
        Expr right;

        left = multiplicativeExpr();
        while(isKind(IToken.Kind.PLUS,IToken.Kind.MINUS)) {
            IToken op = t;
            consume();
            right = multiplicativeExpr();
            left = new BinaryExpr(firstToken, left, op, right);
        }

        return left;
    }

    public Expr multiplicativeExpr() throws LexicalException, SyntaxException {
        IToken firstToken = t;
        Expr left;
        Expr right;

        left = unaryExpr();
        while(isKind(IToken.Kind.TIMES,IToken.Kind.DIV,IToken.Kind.MOD)) {
            IToken op = t;
            consume();
            right = unaryExpr();
            left = new BinaryExpr(firstToken, left, op, right);
        }

        return left;
    }

    public Expr unaryExpr() throws LexicalException, SyntaxException {
        IToken firstToken = t;
        Expr uE;
        Expr e;
        if(isKind(IToken.Kind.BANG,IToken.Kind.MINUS, IToken.Kind.COLOR_OP, IToken.Kind.IMAGE_OP)) {
            IToken op = t;
            consume();
            e = unaryExpr();
            uE = new UnaryExpr(firstToken, op, e);
        } else {
            uE = unaryExprPostFix();
        }

        return uE;
    }

    public Expr unaryExprPostFix() throws LexicalException, SyntaxException {
        IToken firstToken = t;
        Expr primary;
        PixelSelector sel;
        primary = primaryExpr();
        sel = pixelSel();

        if (sel == null) {
            return primary;
        }

        return new UnaryExprPostfix(firstToken, primary, sel);
    }

    public Expr primaryExpr() throws LexicalException, SyntaxException {
        IToken firstToken = t;
        Expr expr;
        if(isKind(IToken.Kind.BOOLEAN_LIT, IToken.Kind.STRING_LIT, IToken.Kind.INT_LIT, IToken.Kind.FLOAT_LIT, IToken.Kind.IDENT)) {
            expr = switch (t.getKind()) {
                case BOOLEAN_LIT -> new BooleanLitExpr(firstToken);
                case STRING_LIT -> new StringLitExpr(firstToken);
                case INT_LIT -> new IntLitExpr(firstToken);
                case FLOAT_LIT -> new FloatLitExpr(firstToken);
                case IDENT -> new IdentExpr(firstToken);
                default -> null;
            };

            consume();
        } else if (isKind(IToken.Kind.LPAREN)) {
            consume();
            expr = expr();
            match(IToken.Kind.RPAREN);
        } else {
            throw new SyntaxException("Syntax error!");
        }

        return expr;
    }

    public PixelSelector pixelSel() throws LexicalException, SyntaxException {
        IToken firstToken = t;
        PixelSelector node = null;
        Expr left;
        Expr right;
        if (isKind(IToken.Kind.LSQUARE)) {
            consume();
            left = expr();
            match(IToken.Kind.COMMA);
            right = expr();
            match(IToken.Kind.RSQUARE);
            node = new PixelSelector(firstToken, left, right);
        }

        return node;
    }
}
