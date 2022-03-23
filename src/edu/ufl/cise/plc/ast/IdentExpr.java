package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;

public class IdentExpr extends Expr {
	
	Declaration dec;
		
	public IdentExpr(IToken firstToken) {
		super(firstToken);
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitIdentExpr(this, arg);
	}

	@Override
	public String toString() {
		return "IdentExpr [dec=" + dec + ", type=" + type + ", coerceTo=" + coerceTo + "]";
	}

	public Declaration getDec() {
		return dec;
	}

	public void setDec(Declaration dec) {
		this.dec = dec;
	}

	
}
