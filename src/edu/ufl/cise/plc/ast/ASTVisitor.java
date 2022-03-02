package edu.ufl.cise.plc.ast;

public interface ASTVisitor {
	
	Object visitBooleanLitExpr(BooleanLitExpr booleanLitExpr, Object arg) throws Exception;

	Object visitStringLitExpr(StringLitExpr stringLitExpr, Object arg) throws Exception;

	Object visitIntLitExpr(IntLitExpr intLitExpr, Object arg) throws Exception;

	Object visitFloatLitExpr(FloatLitExpr floatLitExpr, Object arg) throws Exception;

	Object visitColorConstExpr(ColorConstExpr colorConstExpr, Object arg) throws Exception;

	Object visitConsoleExpr(ConsoleExpr consoleExpr, Object arg) throws Exception;

	Object visitColorExpr(ColorExpr colorExpr, Object arg) throws Exception;

	Object visitUnaryExpr(UnaryExpr unaryExpression, Object arg) throws Exception;

	Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws Exception;

	Object visitIdentExpr(IdentExpr identExpr, Object arg) throws Exception;

	Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws Exception;

	Object visitDimension(Dimension dimension, Object arg) throws Exception;

	Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws Exception;

	Object visitAssignmentStatement(AssignmentStatement assignmentStatement, Object arg) throws Exception;

	Object visitWriteStatement(WriteStatement writeStatement, Object arg) throws Exception;

	Object visitReadStatement(ReadStatement readStatement, Object arg) throws Exception;

	Object visitProgram(Program program, Object arg) throws Exception;

	Object visitNameDef(NameDef nameDef, Object arg) throws Exception;

	Object visitNameDefWithDim(NameDefWithDim nameDefWithDim, Object arg) throws Exception;

	Object visitReturnStatement(ReturnStatement returnStatement, Object arg) throws Exception;

	Object visitVarDeclaration(VarDeclaration declaration, Object arg) throws Exception;

	Object visitUnaryExprPostfix(UnaryExprPostfix unaryExprPostfix, Object arg) throws Exception;;

}
