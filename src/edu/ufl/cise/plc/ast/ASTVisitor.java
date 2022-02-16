package edu.ufl.cise.plc.ast;

public interface ASTVisitor {
	
	Object visitBooleanLitExpr(BooleanLitExpr booleanLitExpr, Object arg) throws Exception;

	Object visitStringLitExpr(StringLitExpr stringLitExpr, Object arg) throws Exception;

	Object visitIntLitExpr(IntLitExpr intLitExpr, Object arg) throws Exception;

	Object visitFloatLitExpr(FloatLitExpr floatLitExpr, Object arg) throws Exception;

	Object visitUnaryExpr(UnaryExpr unaryExpr, Object arg) throws Exception;

	Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws Exception;

	Object visitIdentExpr(IdentExpr identExpr, Object arg) throws Exception;

	Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws Exception;

	Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws Exception;

	Object visitUnaryExprPostfix(UnaryExprPostfix unaryExprPostfix, Object arg) throws Exception;

}
