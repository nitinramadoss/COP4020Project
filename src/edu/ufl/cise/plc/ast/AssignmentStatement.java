package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;
import edu.ufl.cise.plc.ast.Types.Type;

public class AssignmentStatement extends Statement {

	final String name;
	final PixelSelector selector;
	final Expr expr;
//	Type targetType;
	Declaration targetDec;

	
	public AssignmentStatement(IToken firstToken, String name, PixelSelector selector, Expr expr) {
		super(firstToken);
		this.name = name;
		this.selector = selector;
		this.expr = expr;
	}

	public String getName() {
		return name;
	}

	public PixelSelector getSelector() {
		return selector;
	}

	public Expr getExpr() {
		return expr;
	}
	
	
	
//	public Type getTargetType() {
//		return targetType;
//	}

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
		return v.visitAssignmentStatement(this, arg);
	}

	@Override
	public String toString() {
		return "AssignmentStatement [name=" + name + ", selector=" + selector + ", expr=" + expr + "]";
	}

}
