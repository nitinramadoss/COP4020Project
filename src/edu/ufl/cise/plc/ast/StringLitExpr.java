package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;

public class StringLitExpr extends Expr{

	public StringLitExpr(IToken firstToken) {
		super(firstToken);
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitStringLitExpr(this,arg);
	}
	
	public String getValue() {
		return firstToken.getStringValue();
	}

	@Override
	public String toString() {
		return "StringLitExpr [getValue()=" + getValue() + "]";
	}

}
