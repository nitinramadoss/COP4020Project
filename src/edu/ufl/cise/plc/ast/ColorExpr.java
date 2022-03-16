package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;

public class ColorExpr extends Expr {

	final Expr red;
	final Expr green;
	final Expr blue;
	
	public ColorExpr(IToken firstToken, Expr red, Expr green, Expr blue) {
		super(firstToken);
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitColorExpr(this, arg);
	}

	public Expr getRed() {
		return red;
	}

	public Expr getGreen() {
		return green;
	}
	
	public Expr getBlue() {
		return blue;
	}

	@Override
	public String toString() {
		return "ColorExpr [red=" + red + ", green=" + green + ", blue=" + blue + "]";
	}

}
