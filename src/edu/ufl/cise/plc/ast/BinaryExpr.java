package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;

public class BinaryExpr extends Expr {
	
	final Expr left;
	final IToken op;
	final Expr right;

	public BinaryExpr(IToken firstToken, Expr left, IToken op, Expr right) {
		super(firstToken);
		this.left = left;
		this.op = op;
		this.right = right;
	}
	
	public Expr getLeft() {
		return left;
	}

	public IToken getOp() {
		return op;
	}

	public Expr getRight() {
		return right;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitBinaryExpr(this,arg);
	}

	@Override
	public String toString() {
		return "BinaryExpr [left=" + left + ", op=" + op.getText() + ", right=" + right + "]";
	}

	
}
