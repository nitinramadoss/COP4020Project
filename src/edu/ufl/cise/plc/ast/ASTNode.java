package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;
import edu.ufl.cise.plc.IToken.SourceLocation;

public abstract class ASTNode  {
	

	final IToken firstToken;

	public ASTNode(IToken firstToken) {
		this.firstToken = firstToken;
	}

	public SourceLocation getSourceLoc() {
		return firstToken.getSourceLocation();
	}

	public String getText() {
		return firstToken.getText();
	}
	
	public abstract Object visit(ASTVisitor v, Object arg) throws  Exception;

	
}
