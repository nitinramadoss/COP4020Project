package edu.ufl.cise.plc.ast;

import java.util.List;

import edu.ufl.cise.plc.IToken;
import edu.ufl.cise.plc.ast.Types.Type;

public class Program extends ASTNode {	
	
	final Type returnType;
	final String name; 
	final List<NameDef> params;
	final List<ASTNode> decsAndStatements;

	public Program(IToken firstToken, Type returnType, String name, List<NameDef> params,
			List<ASTNode> decsAndStatements) {
		super(firstToken);
		this.returnType = returnType;
		this.name = name;
		this.params = params;
		this.decsAndStatements = decsAndStatements;
	}

	public Type getReturnType() {
		return returnType;
	}

	public String getName() {
		return name;
	}

	public List<NameDef> getParams() {
		return params;
	}

	public List<ASTNode> getDecsAndStatements() {
		return decsAndStatements;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitProgram(this, arg);
	}

	@Override
	public String toString() {
		return "Program [returnType=" + returnType + ", name=" + name + ", params=" + params + ", decsAndStatements="
				+ decsAndStatements + "]";
	}

}
