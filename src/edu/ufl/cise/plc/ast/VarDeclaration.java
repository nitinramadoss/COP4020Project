package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;
import edu.ufl.cise.plc.ast.Types.Type;

public class VarDeclaration extends Declaration {
		
	final NameDef nameDef;
	final Expr expr;
	final IToken op;
	


	public VarDeclaration(IToken firstToken, NameDef nameDef, IToken op, Expr expr) {
		super(firstToken);
		this.nameDef = nameDef;
		this.op = op;
		this.expr = expr;
	}

	public NameDef getNameDef() {
		return nameDef;
	}
	
	public IToken getOp() {
		return op;
	}

	public Expr getExpr() {
		return expr;
	}

	
	public String getName() {
		return nameDef.getName();
	}
	
	@Override
	public Type getType() {
		return nameDef.getType();
	}

	@Override
	public Dimension getDim() {
		return nameDef.getDim();
	}


	
	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitVarDeclaration(this, arg);
	}

	@Override
	public String toString() {
		return "Declaration [nameDef=" + nameDef + ", expr=" + expr + "]";
	}





}
