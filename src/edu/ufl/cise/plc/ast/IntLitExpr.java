package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;

public class IntLitExpr extends Expr {

	public IntLitExpr(IToken firstToken) {
		super(firstToken);
	}
	
	public int getValue() {
		return firstToken.getIntValue();
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitIntLitExpr(this,arg);
	}

	@Override
	public String toString() {
		return "IntLitExpr [getValue()=" + getValue() + "]";
	}

}
