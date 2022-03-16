package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;

public class ReturnStatement extends Statement {
	
	final Expr expr;

	public ReturnStatement(IToken firstToken, Expr expr) {
		super(firstToken);
		this.expr = expr;
	}

	public Expr getExpr() {
		return expr;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitReturnStatement(this,arg);
	}

	@Override
	public String toString() {
		return "ReturnStatement [expr=" + expr + "]";
	}

	
	
}
