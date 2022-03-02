package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;

public class ReadStatement extends Statement {

	final String name;
	final PixelSelector selector;
	final Expr source;
	
	
	public ReadStatement(IToken firstToken, String name, PixelSelector selector, Expr source) {
		super(firstToken);
		this.name = name;
		this.selector = selector;
		this.source = source;
	}
	
	public String getName() {
		return name;
	}

	public PixelSelector getSelector() {
		return selector;
	}


	public Expr getSource() {
		return source;
	}


	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitReadStatement(this, arg);
	}


	@Override
	public String toString() {
		return "ReadStatement [name=" + name + ", selector=" + selector + ", source=" + source + "]";
	}


}
