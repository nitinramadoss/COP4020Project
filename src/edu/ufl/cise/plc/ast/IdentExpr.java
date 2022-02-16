package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;

public class IdentExpr extends Expr {
	
	
	public IdentExpr(IToken firstToken) {
		super(firstToken);
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitIdentExpr(this, arg);
	}

	@Override
	public String toString() {
		return "IdentExpr[" + firstToken.getText() + "]";
	}

}
