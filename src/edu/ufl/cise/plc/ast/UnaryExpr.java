package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;

public class UnaryExpr extends Expr {

	final IToken op;
	final Expr e;

	public UnaryExpr(IToken firstToken, IToken op, Expr e) {
		super(firstToken);
		this.op = op;
		this.e = e;
	}

	public IToken getOp() {
		return op;
	}

	public Expr getExpr() {
		return e;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitUnaryExpr(this, arg);
	}

	@Override
	public String toString() {
		return "UnaryExpr [op=" + op + ", e=" + e + "]";
	}

}
