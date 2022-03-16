package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;

public class Dimension extends ASTNode {

	final Expr width;
	final Expr height;
	
	public Dimension(IToken firstToken, Expr width, Expr height) {
		super(firstToken);
		this.width = width;
		this.height = height;
	}
	
	public Expr getWidth() {
		return width;
	}

	public Expr getHeight() {
		return height;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitDimension(this, arg);
	}

	@Override
	public String toString() {
		return "Dimension [width=" + width + ", height=" + height + "]";
	}


	
}
