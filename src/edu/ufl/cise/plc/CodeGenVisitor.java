package edu.ufl.cise.plc;

import edu.ufl.cise.plc.ast.*;
import edu.ufl.cise.plc.ast.Types.Type;

public class CodeGenVisitor implements ASTVisitor {
    private String packageName;

    public CodeGenVisitor(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public Object visitBooleanLitExpr(BooleanLitExpr booleanLitExpr, Object arg) throws Exception {
        CodeGenStringBuilder sb=  (CodeGenStringBuilder) arg;
        if (booleanLitExpr.getValue()) {
            sb.append("true");
        } else {
            sb.append("false");
        }
        return sb;
    }

    @Override
    public Object visitStringLitExpr(StringLitExpr stringLitExpr, Object arg) throws Exception {
        CodeGenStringBuilder sb=  (CodeGenStringBuilder) arg;
        sb.quotes().append(stringLitExpr.getValue()).quotes();
        return sb;
    }

    @Override
    public Object visitIntLitExpr(IntLitExpr intLitExpr, Object arg) throws Exception {
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;

        int value = intLitExpr.getValue();
        Types.Type coercedTo = intLitExpr.getCoerceTo();

        if (intLitExpr.getCoerceTo() != null && intLitExpr.getCoerceTo() != Types.Type.INT) {

        }

        sb.append(String.valueOf(value));
        return sb;
    }

    @Override
    public Object visitFloatLitExpr(FloatLitExpr floatLitExpr, Object arg) throws Exception {
        return null;
    }

    @Override
    public Object visitColorConstExpr(ColorConstExpr colorConstExpr, Object arg) throws Exception {
        return null;
    }

    @Override
    public Object visitConsoleExpr(ConsoleExpr consoleExpr, Object arg) throws Exception {
        return null;
    }

    @Override
    public Object visitColorExpr(ColorExpr colorExpr, Object arg) throws Exception {
        return null;
    }

    @Override
    public Object visitUnaryExpr(UnaryExpr unaryExpression, Object arg) throws Exception {
        CodeGenStringBuilder sb =  (CodeGenStringBuilder) arg;
        String op = unaryExpression.getOp().getText();

        sb.lparen().append(op);

        Expr expr = unaryExpression.getExpr();
        expr.visit(this, sb);

        sb.rparen();

        return sb;
    }

    @Override
    public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws Exception {
        CodeGenStringBuilder sb =  (CodeGenStringBuilder) arg;
        Expr leftExpr = binaryExpr.getLeft();
        Expr rightExpr = binaryExpr.getRight();

        Type type = binaryExpr.getType();
        Type leftType = leftExpr.getCoerceTo() != null ? leftExpr.getCoerceTo() : leftExpr.getType();
        Type rightType = rightExpr.getCoerceTo() != null ? rightExpr.getCoerceTo() : rightExpr.getType();

        IToken op = binaryExpr.getOp();

        // if (not handled in assignment 5)  throw new UnsupportedOperationException(“Not implemented”);
        // else:
        sb.lparen();
        leftExpr.visit(this, sb);
        sb.append(op.getText());
        rightExpr.visit(this, sb);
        sb.rparen();

        return sb;
    }

    @Override
    public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws Exception {
        return null;
    }

    @Override
    public Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws Exception {
        CodeGenStringBuilder sb =  (CodeGenStringBuilder) arg;
        sb.lparen().append(conditionalExpr.getText()).question();

        Expr lExpr = conditionalExpr.getTrueCase();
        lExpr.visit(this, sb);

        sb.colon();

        Expr rExpr = conditionalExpr.getFalseCase();
        rExpr.visit(this, sb);

        return sb;
    }

    @Override
    public Object visitDimension(Dimension dimension, Object arg) throws Exception {
        return null;
    }

    @Override
    public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws Exception {
        return null;
    }

    @Override
    public Object visitAssignmentStatement(AssignmentStatement assignmentStatement, Object arg) throws Exception {
        CodeGenStringBuilder sb =  (CodeGenStringBuilder) arg;

        sb.append(assignmentStatement.getName()).eq();

        Expr expr = assignmentStatement.getExpr();
        expr.visit(this, sb);

        sb.semi();

        return sb;
    }

    @Override
    public Object visitWriteStatement(WriteStatement writeStatement, Object arg) throws Exception {
        return null;
    }

    @Override
    public Object visitReadStatement(ReadStatement readStatement, Object arg) throws Exception {
        CodeGenStringBuilder sb =  (CodeGenStringBuilder) arg;

        sb.append(readStatement.getName()).eq();

        Expr expr = readStatement.getSource();
        expr.visit(this, sb);

        sb.semi();

        return sb;
    }

    @Override
    public Object visitProgram(Program program, Object arg) throws Exception {
        return null;
    }

    @Override
    public Object visitNameDef(NameDef nameDef, Object arg) throws Exception {
        CodeGenStringBuilder sb=  (CodeGenStringBuilder) arg;
        sb.append(Types.toStringType(nameDef.getType())).space().append(nameDef.getName());

        return sb;
    }

    @Override
    public Object visitNameDefWithDim(NameDefWithDim nameDefWithDim, Object arg) throws Exception {
        return null;
    }

    @Override
    public Object visitReturnStatement(ReturnStatement returnStatement, Object arg) throws Exception {
        CodeGenStringBuilder sb=  (CodeGenStringBuilder) arg;
        Expr expr = returnStatement.getExpr();
        sb.append("return");
        expr.visit(this, sb);
        sb.semi().newline();
        return sb;
    }

    @Override
    public Object visitVarDeclaration(VarDeclaration declaration, Object arg) throws Exception {
        CodeGenStringBuilder sb=  (CodeGenStringBuilder) arg;

        if (declaration.isInitialized()) {
            sb.append(declaration.getText()).semi();
        } else {
            sb.append(declaration.getText()).eq();
            Expr expr = declaration.getExpr();
            expr.visit(this, sb);
            sb.semi();
        }

        return sb;
    }

    @Override
    public Object visitUnaryExprPostfix(UnaryExprPostfix unaryExprPostfix, Object arg) throws Exception {
        return null;
    }
}
