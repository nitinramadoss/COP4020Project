package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;
import edu.ufl.cise.plc.ast.Types.Type;

public abstract class Expr extends ASTNode{

	Type type;
	Type coerceTo;
	
	public Type getCoerceTo() {
		return coerceTo;
	}

	public void setCoerceTo(Type coerceTo) {
		this.coerceTo = coerceTo;
	}

	public Expr(IToken firstToken) {
		super(firstToken);
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Expr [type=" + type + ", coerceTo=" + coerceTo + "]";
	}
	

	
}
