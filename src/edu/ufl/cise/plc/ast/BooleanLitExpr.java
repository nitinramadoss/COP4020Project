package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;

public class BooleanLitExpr extends Expr  {
	
	public BooleanLitExpr(IToken firstToken){
		super(firstToken);
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitBooleanLitExpr(this, arg);
	}

	public boolean getValue() {
		return firstToken.getBooleanValue();
	}

	@Override
	public String toString() {
		return "BooleanLitExpr [getValue()=" + getValue() + "]";
	}


	
	
	
}
