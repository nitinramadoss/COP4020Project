package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;
import edu.ufl.cise.plc.ast.Types.Type;

public abstract class Declaration extends ASTNode {

	public Declaration(IToken firstToken) {
		super(firstToken);
	}
	

	boolean initialized = false;

	public abstract Type getType();

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
	
	//returns null if dimensions not specified in Declaration.
	public abstract Dimension getDim();


}
