package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;

public class ConditionalExpr extends Expr {
	
	final Expr condition;
	final Expr trueCase;
	final Expr falseCase;
	
	public ConditionalExpr(IToken firstToken, Expr condition, Expr trueCase, Expr falseCase) {
		super(firstToken);
		this.condition = condition;
		this.trueCase = trueCase;
		this.falseCase = falseCase;
	}

	public Expr getCondition() {
		return condition;
	}

	public Expr getTrueCase() {
		return trueCase;
	}

	public Expr getFalseCase() {
		return falseCase;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitConditionalExpr(this,arg);
	}

	@Override
	public String toString() {
		return "ConditionalExpr [condition=" + condition + ", trueCase=" + trueCase + ", falseCase=" + falseCase + "]";
	}
	
	

}
