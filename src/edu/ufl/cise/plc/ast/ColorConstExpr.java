package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;

public class ColorConstExpr extends Expr{

	public ColorConstExpr(IToken firstToken) {
		super(firstToken);
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitColorConstExpr(this,arg);
	}

	@Override
	public String toString() {
		return "ColorConstExpr [getText()=" + getText() + "]";
	}

}
