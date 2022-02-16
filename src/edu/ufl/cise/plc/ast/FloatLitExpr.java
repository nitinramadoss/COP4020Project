package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;

public class FloatLitExpr extends Expr {

	public FloatLitExpr(IToken firstToken) {
		super(firstToken);
	}
	
	public float getValue() {
		return firstToken.getFloatValue();
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitFloatLitExpr(this, arg);
	}

	@Override
	public String toString() {
		return "FloatLitExpr [firstToken=" + firstToken + "]";
	}

	
}
