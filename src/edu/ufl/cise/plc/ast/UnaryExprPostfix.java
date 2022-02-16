package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;

public class UnaryExprPostfix extends Expr {
	
	final Expr expr;
	final PixelSelector selector;
	
	public UnaryExprPostfix(IToken firstToken, Expr e, PixelSelector selector) {
		super(firstToken);
		this.expr = e;
		this.selector = selector;
	}

	public Expr getExpr() {
		return expr;
	}

	public PixelSelector getSelector() {
		return selector;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitUnaryExprPostfix(this, arg);
	}

	@Override
	public String toString() {
		return "UnaryExprPostfix [expr=" + expr + ", selector=" + selector + "]";
	}
	
	

}
