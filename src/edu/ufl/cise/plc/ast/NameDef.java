package edu.ufl.cise.plc.ast;

import edu.ufl.cise.plc.IToken;
import edu.ufl.cise.plc.ast.Types.Type;

public class NameDef extends Declaration {
		
	final Type type;
	final String name;	
	
	public NameDef(IToken firstToken, String type, String name) {
		super(firstToken);
		this.name = name;
		this.type = Types.toType(type);
	}
	
	public NameDef(IToken firstToken, IToken type, IToken name) {
		super(firstToken);
		this.name = name.getText();
		this.type = Types.toType(type.getText());
	}
	
	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitNameDef(this,arg);
	}

	@Override
	public String toString() {
		return "NameDef [name=" + name + ", type=" + type + "]";
	}

	@Override
	public Dimension getDim() {
		return null;
	}
	
	

}
