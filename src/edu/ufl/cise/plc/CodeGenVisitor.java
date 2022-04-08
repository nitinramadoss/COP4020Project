package edu.ufl.cise.plc;

import edu.ufl.cise.plc.ast.*;
import edu.ufl.cise.plc.ast.Types.Type;

import java.util.ArrayList;

public class CodeGenVisitor implements ASTVisitor {
    private String packageName;

    public static String toStringType(Type t) {
        return switch(t) {
            case BOOLEAN -> "boolean";
            case COLOR -> "color";
            case CONSOLE -> "console";
            case FLOAT -> "float";
            case IMAGE -> "image";
            case INT -> "int";
            case STRING -> "String";
            case VOID -> "void";
            default -> throw new IllegalArgumentException("Unexpected type value: " + t);
        };
    }

    public static String toBoxedType(Type t) {
        return switch(t) {
            case BOOLEAN -> "Boolean";
            case FLOAT -> "Float";
            case INT -> "Integer";
            case STRING -> "String";
            default -> throw new IllegalArgumentException("Unexpected type value: " + t);
        };
    }

    public CodeGenVisitor(String packageName) {
        this.packageName = packageName;
    }


    @Override
    public Object visitProgram(Program program, Object arg) throws Exception {
//        <package declaration>
//        <imports>
//        import edu.ufl.cise.plc.runtime.*
//        public class <name> {
        //        public static <returnType> apply( <params> ) {
        //        <decsAndStatements>
//        }
//        }


        CodeGenStringBuilder sb = new CodeGenStringBuilder();
        sb.append("package " + packageName + ";\n");
        sb.append("import edu.ufl.cise.plc.runtime.*;\n");
        sb.append("public class " + program.getName() + " {\n");
        sb.append("\tpublic static " + toStringType(program.getReturnType()) + " apply( ");

        int c = 0;
        for (NameDef p : program.getParams()) {
            p.visit(this, sb);
            c += 1;

            if (c != program.getParams().size()) sb.comma().space();
        }

        sb.rparen().append("{").newline();
        for (ASTNode ds : program.getDecsAndStatements()) {
            sb.append("\t");
            ds.visit(this, sb);
        }

        sb.append("}}");
        return sb.getString();
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
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        sb.quotes().newline().append(stringLitExpr.getValue()).quotes();
        return sb;
    }

    @Override
    public Object visitIntLitExpr(IntLitExpr intLitExpr, Object arg) throws Exception {
//        Java int literal corresponding to value
//
//        If coerceTo != null and coerceTo != INT, add cast to coerced type.

        CodeGenStringBuilder sb =  (CodeGenStringBuilder) arg;

        Type coerce = intLitExpr.getCoerceTo();
        if (coerce != null && coerce != Type.INT) {
            String type = toStringType(coerce);
            sb.type(type);
        }
        return sb.append(intLitExpr.getText());
    }

    @Override
    public Object visitFloatLitExpr(FloatLitExpr floatLitExpr, Object arg) throws Exception {
//        Java float literal corresponding to value.
//
//        If coerceTo != null and coerceTo != FLOAT, add cast to coerced type.
//
//        Recall Java float literals must have f appended.  E.g.  12.3 in source is 12.3f in Java.
//        (12.3 in Java is a double–if you do this your program will probably run, but fail test cases that check for equality)
        CodeGenStringBuilder sb =  (CodeGenStringBuilder) arg;

        Type coerce = floatLitExpr.getCoerceTo();
        if (coerce != null && coerce != Type.FLOAT) {
            String type = toStringType(coerce);
            sb.type(type);
        }
        return sb.append(floatLitExpr.getText() + "f");

    }

    @Override
    public Object visitColorConstExpr(ColorConstExpr colorConstExpr, Object arg) throws Exception {
        return null;
    }

    @Override
    public Object visitConsoleExpr(ConsoleExpr consoleExpr, Object arg) throws Exception {
// (<boxed(coerceTo)> ConsoleIO.readValueFromConsole( “coerceType”, <prompt>)
// <prompt> is a string that requests the user to enter the desired type.
// <boxed(type)> means the object version of the indicated type: Integer, Boolean, Float, etc.
// The first argument of readValueFromConsole is an all uppercase String literal with corresponding to the type.
// (i.e. one of “INT”, “STRING”, “BOOLEAN”,  “FLOAT”)
// For example, if the PLCLang source has
// j <- console;
// where j is int, then this would translate to
//
//     j = (Integer) ConsoleIO.readValueFromConsole(“INT”, “Enter integer:”);
//
// Note that the “j = “ part would be generated by the parent AssignmentStatement.  See the provided ConsoleIO class.
        CodeGenStringBuilder sb =  (CodeGenStringBuilder) arg;
        sb.type(toBoxedType(consoleExpr.getCoerceTo())).space(); // (Integer)
        sb.append("ConsoleIO.readValueFromConsole"); // ConsoleIO.readValueFromConsole
        sb.lparen().quote().append(toStringType(consoleExpr.getCoerceTo()).toUpperCase()).quote().comma().space(); // (“INT”,
        sb.quote().append("Enter ").append(toBoxedType(consoleExpr.getCoerceTo()).toLowerCase()); // integer:
        sb.colon().quote().rparen().semi(); // ”);

        return sb;
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
        CodeGenStringBuilder sb =  (CodeGenStringBuilder) arg;

//      <identExpr.getText>
//      If coerceTo != null and coerceTo != identExpr.type, add cast to coerced type.
        Type coerce = identExpr.getCoerceTo();
        if (coerce != null && coerce != identExpr.getType()) {
            String type = toStringType(coerce);
            sb.type(type);
        }
        return sb.append(identExpr.getText());
    }

    @Override
    public Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws Exception {
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        sb.lparen().append(conditionalExpr.getCondition().getText()).rparen().question();
        Expr lExpr = conditionalExpr.getTrueCase();
        lExpr.visit(this, sb);

        sb.colon();

        Expr rExpr = conditionalExpr.getFalseCase();
        rExpr.visit(this, sb);

        return sb;
    }

    @Override
    public Object visitDimension(Dimension dimension, Object arg) throws Exception {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws Exception {
        throw new UnsupportedOperationException("Not implemented");
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

        sb.append(readStatement.getName()).space().eq();

        Expr expr = readStatement.getSource();
        expr.visit(this, sb);

        sb.semi();

        return sb;
    }

    @Override
    public Object visitNameDef(NameDef nameDef, Object arg) throws Exception {
        CodeGenStringBuilder sb=  (CodeGenStringBuilder) arg;
        sb.append(toStringType(nameDef.getType())).space().append(nameDef.getName());

        return sb;
    }

    @Override
    public Object visitNameDefWithDim(NameDefWithDim nameDefWithDim, Object arg) throws Exception {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Object visitReturnStatement(ReturnStatement returnStatement, Object arg) throws Exception {
        CodeGenStringBuilder sb=  (CodeGenStringBuilder) arg;
        Expr expr = returnStatement.getExpr();
        sb.append("return ");
        expr.visit(this, sb);
        sb.semi().newline();
        return sb;
    }

    @Override
    public Object visitVarDeclaration(VarDeclaration declaration, Object arg) throws Exception {
        CodeGenStringBuilder sb=  (CodeGenStringBuilder) arg;

        if (!declaration.isInitialized()) {
            sb.append(declaration.getText()).space().append(declaration.getName()).semi();
        } else {
            sb.append(toStringType(declaration.getType()) + " ");
            sb.append(declaration.getName()).eq();
            Expr expr = declaration.getExpr();
            Type coerce = expr.getCoerceTo();

            if (coerce != null) {
                sb.type(toStringType(coerce)).space();
            }

            expr.visit(this, sb);
            sb.semi().newline();
        }

        return sb;
    }

    @Override
    public Object visitUnaryExprPostfix(UnaryExprPostfix unaryExprPostfix, Object arg) throws Exception {
        throw new UnsupportedOperationException("Not implemented");
    }
}
