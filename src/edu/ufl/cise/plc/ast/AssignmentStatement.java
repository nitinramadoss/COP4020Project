package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;

public class AssignmentStatement extends Statement {

	final String name;
	final PixelSelector selector;
	final Expr expr;

	
	public AssignmentStatement(IToken firstToken, String name, PixelSelector selector, Expr expr) {
		super(firstToken);
		this.name = name;
		this.selector = selector;
		this.expr = expr;
	}

	public String getName() {
		return name;
	}

	public PixelSelector getSelector() {
		return selector;
	}

	public Expr getExpr() {
		return expr;
	}
	
	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitAssignmentStatement(this, arg);
	}

	@Override
	public String toString() {
		return "AssignmentStatement [name=" + name + ", selector=" + selector + ", expr=" + expr + "]";
	}

}
