package edu.ufl.cise.plc.runtime.javaCompilerClassLoader;

import java.io.IOException;

import edu.ufl.cise.plc.CompilerComponentFactory;
import edu.ufl.cise.plc.ast.ASTNode;
import edu.ufl.cise.plc.ast.Program;

public class PLCLangExec {
	
	public final String packageName;
	
	public PLCLangExec(String packageName, boolean VERBOSE) {
		super();
		this.packageName = packageName;
		this.VERBOSE = VERBOSE;
	}

	final boolean VERBOSE;

	private void show(Object obj) throws IOException {
		if (VERBOSE)
			System.out.println(obj);
	}
	
	public Object exec(String input, Object[] params) throws Exception {
		//Lex and parse to obtain AST
		ASTNode ast = CompilerComponentFactory.getParser(input).parse();
		//Type check and decorate AST with declaration and type info
		ast.visit(CompilerComponentFactory.getTypeChecker(), null);
		//Generate Java code
		String className = ((Program) ast).getName();
		String fullyQualifiedName = packageName != "" ? packageName + '.' + className : className;
		String javaCode = (String) ast.visit(CompilerComponentFactory.getCodeGenerator(packageName), null);
		show(javaCode);
		//Invoke Java compiler to obtain bytecode
		byte[] byteCode = DynamicCompiler.compile(fullyQualifiedName, javaCode);
		//Load generated classfile and execute its apply method.
		Object result = DynamicClassLoader.loadClassAndRunMethod(byteCode, fullyQualifiedName, "apply", params);
		return result;
	}

}
