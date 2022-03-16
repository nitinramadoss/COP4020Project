package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;

public class ConsoleExpr extends Expr {

	public ConsoleExpr(IToken firstToken) {
		super(firstToken);
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitConsoleExpr(this, arg);
	}

	@Override
	public String toString() {
		return "ConsoleExpr []";
	}

}
