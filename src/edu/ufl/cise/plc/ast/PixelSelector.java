package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;

public class PixelSelector extends ASTNode {
	
	final Expr x;
	final Expr y;

	public PixelSelector(IToken firstToken, Expr x, Expr y) {
		super(firstToken);
		this.x = x;
		this.y = y;
	}

	public Expr getX() {
		return x;
	}

	public Expr getY() {
		return y;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitPixelSelector(this, arg);
	}

	@Override
	public String toString() {
		return "PixelSelector [x=" + x + ", y=" + y + "]";
	}

	
}
