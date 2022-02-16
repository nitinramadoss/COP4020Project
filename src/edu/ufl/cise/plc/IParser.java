package edu.ufl.cise.plc;

import edu.ufl.cise.plc.ast.ASTNode;

public interface IParser {
	ASTNode parse() throws PLCException;
	
}
