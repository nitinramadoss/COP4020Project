package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;
import edu.ufl.cise.plc.ast.Types.Type;

public class ReadStatement extends Statement {

	final String name;
	final PixelSelector selector;
	final Expr source;
//	Type targetType;
	Declaration targetDec;
	
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

//	public Type getTargetType() {
//		return targetType;
//	}
//
//	public void setTargetType(Type targetType) {
//		this.targetType = targetType;
//	}
	public Declaration getTargetDec() {
		return targetDec;
	}	

	public void setTargetDec(Declaration targetDec) {
		this.targetDec = targetDec;
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
