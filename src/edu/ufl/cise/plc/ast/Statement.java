package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;

public abstract class Statement extends ASTNode {

	public Statement(IToken firstToken) {
		super(firstToken);
	}

}
