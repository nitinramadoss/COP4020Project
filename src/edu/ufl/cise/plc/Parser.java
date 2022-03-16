package edu.ufl.cise.plc;

import edu.ufl.cise.plc.ast.*;
import edu.ufl.cise.plc.ast.Types.Type;

import java.util.ArrayList;

public class Parser implements IParser {
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
        ASTNode p = program();
        match(IToken.Kind.EOF);
        return p;
    }

    void match(IToken.Kind c) throws LexicalException, SyntaxException {
        if (isKind(c)) {
            consume();
        } else {
            throw new SyntaxException(String.format("Syntax error! Expected %s, got %s", c, t.getKind()));
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

    public Program program() throws LexicalException, SyntaxException {
        IToken firstToken = t;

        Type returnType;
        if (isKind(IToken.Kind.TYPE, IToken.Kind.KW_VOID)) {
            returnType = Type.toType(t.getText());
        }
        else {
            throw new SyntaxException("Program() did not see type or void as first token.");
        }
        consume();

        String name;
        if (isKind(IToken.Kind.IDENT)) {
            name = t.getText();
        }
        else {
            throw new SyntaxException("Program() did not see ident as second token.");
        }
        consume();

        match(IToken.Kind.LPAREN);

        ArrayList<NameDef> params = new ArrayList<NameDef>();
        if (!isKind(IToken.Kind.RPAREN)) {
            params.add(nameDef());
            consume();
            while (isKind(IToken.Kind.COMMA)) {
                consume();
                params.add(nameDef());
                consume();
            }
        }
        match(IToken.Kind.RPAREN);

        ArrayList<ASTNode> decsAndStatements = new ArrayList<ASTNode>();

        while (!isKind(IToken.Kind.EOF)) {
            if (isKind(IToken.Kind.TYPE)) {
                decsAndStatements.add(declaration());
            }
            else {
                decsAndStatements.add(statement());
            }

            match(IToken.Kind.SEMI);
        }

        return new Program(firstToken, returnType, name, params, decsAndStatements);
    }

    public NameDef nameDef() throws LexicalException, SyntaxException {
        IToken firstToken = t;
        if (!isKind(IToken.Kind.TYPE)) throw new SyntaxException("Expected type, didn't get that");
        String type = t.getText();
        consume();

        if (isKind(IToken.Kind.IDENT)) {
            String name = t.getText();
            return new NameDef(firstToken, type, name);
        }
        else {
            Dimension dim = dimension();
            String name = t.getText();
            return new NameDefWithDim(firstToken, type, name, dim);
        }
    }

    public Declaration declaration() throws LexicalException, SyntaxException {
        IToken firstToken = t;
        NameDef nameDef = nameDef();
        consume();

        if (isKind(IToken.Kind.ASSIGN, IToken.Kind.LARROW)) {
            IToken op = t;
            consume();

            Expr expr = expr();
            return new VarDeclaration(firstToken, nameDef, op, expr);
        }
        else {
            return new VarDeclaration(firstToken, nameDef, null, null);
        }
    }

    public Expr expr() throws LexicalException, SyntaxException {
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
        Expr conditional;
        Expr cond;
        Expr trueCase;
        Expr falseCase = null;

        consume();
        match(IToken.Kind.LPAREN);
        cond = expr();
        match(IToken.Kind.RPAREN);
        trueCase = expr();


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
        if(isKind(IToken.Kind.BOOLEAN_LIT, IToken.Kind.STRING_LIT, IToken.Kind.INT_LIT,
                IToken.Kind.FLOAT_LIT, IToken.Kind.IDENT, IToken.Kind.COLOR_CONST, IToken.Kind.KW_CONSOLE)) {
            expr = switch (t.getKind()) {
                case BOOLEAN_LIT -> new BooleanLitExpr(firstToken);
                case STRING_LIT -> new StringLitExpr(firstToken);
                case INT_LIT -> new IntLitExpr(firstToken);
                case FLOAT_LIT -> new FloatLitExpr(firstToken);
                case IDENT -> new IdentExpr(firstToken);
                case COLOR_CONST -> new ColorConstExpr(firstToken);
                case KW_CONSOLE -> new ConsoleExpr(firstToken);
                default -> null;
            };

            consume();
        } else if (isKind(IToken.Kind.LPAREN)) {
            consume();
            expr = expr();
            match(IToken.Kind.RPAREN);
        } else if (isKind(IToken.Kind.LANGLE)) {
            consume();
            Expr red = expr();
            match(IToken.Kind.COMMA);
            Expr green = expr();
            match(IToken.Kind.COMMA);
            Expr blue = expr();
            match(IToken.Kind.RANGLE);

            expr = new ColorExpr(firstToken, red, green, blue);
        }
        else {
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

    public Dimension dimension() throws LexicalException, SyntaxException {
        IToken firstToken = t;

        match(IToken.Kind.LSQUARE);
        Expr width = expr();
        match(IToken.Kind.COMMA);
        Expr height = expr();
        match(IToken.Kind.RSQUARE);

        return new Dimension(firstToken, width, height);
    }

    public Statement statement() throws LexicalException, SyntaxException {
        IToken firstToken = t;

        if (isKind(IToken.Kind.IDENT)) {
            String name = t.getText();
            consume();

            PixelSelector selector = pixelSel();

            IToken.Kind opKind = t.getKind();
            consume();

            Expr expr = expr();

            if (opKind == IToken.Kind.ASSIGN) {
                return new AssignmentStatement(firstToken, name, selector, expr);
            }
            else if (opKind == IToken.Kind.LARROW) {
                return new ReadStatement(firstToken, name, selector, expr);
            }
            else {
                throw new SyntaxException("Seeking <- or =, found neither");
            }
        }
        else if (isKind(IToken.Kind.KW_WRITE)) {
            consume();

            Expr source = expr();
            match(IToken.Kind.RARROW);
            Expr dest = expr();

            return new WriteStatement(firstToken, source, dest);
        }
        else if (isKind(IToken.Kind.RETURN)) {
            consume();

            Expr expr = expr();

            return new ReturnStatement(firstToken, expr);
        }
        else {
            throw new SyntaxException("No acceptable syntax found in statement()");
        }
    }
}
