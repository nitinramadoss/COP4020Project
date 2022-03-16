package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;

public class WriteStatement extends Statement {

	final Expr source;
	final Expr dest;
	
	
	public WriteStatement(IToken firstToken, Expr source, Expr dest) {
		super(firstToken);
		this.source = source;
		this.dest = dest;
	}


	public Expr getSource() {
		return source;
	}

	public Expr getDest() {
		return dest;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitWriteStatement(this,arg);
	}


	@Override
	public String toString() {
		return "WriteStatement [source=" + source + ", dest=" + dest + "]";
	}

}
