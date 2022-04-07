package edu.ufl.cise.plc;

// This class eliminates hard coded dependencies on the actual Lexer class.  You can call your lexer whatever you
// want as long as it implements the ILexer interface, and you have provided an appropriate body for the getLexer method


import edu.ufl.cise.plc.ast.ASTVisitor;

public class CompilerComponentFactory {
	
	// This method will be invoked to get an instance of your Lexer.
	public static ILexer getLexer(String input) {
		return new Lexer(input);
	}

	// This method will be invoked to get an instance of your Parser.
	public static IParser getParser(String input) {
		return new Parser(input);
	}

	// This method will be invoked to get an instance of your TypeChecker.
    public static ASTVisitor getTypeChecker() {
		return new TypeCheckVisitor();
    }

	// This method will be invoked to get an instance of your CodeGenerator.
	public static ASTVisitor getCodeGenerator(String packageName) {
		return new CodeGenVisitor(packageName);
	}
}
