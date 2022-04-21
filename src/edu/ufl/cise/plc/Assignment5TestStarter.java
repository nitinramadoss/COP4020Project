package edu.ufl.cise.plc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import edu.ufl.cise.plc.CodeGenVisitor;
import edu.ufl.cise.plc.CompilerComponentFactory;
import edu.ufl.cise.plc.IParser;
//import edu.ufl.cise.plc.ImageResources;
import edu.ufl.cise.plc.TypeCheckException;
import edu.ufl.cise.plc.TypeCheckVisitor;
import edu.ufl.cise.plc.ast.ASTNode;
import edu.ufl.cise.plc.ast.ASTVisitor;
import edu.ufl.cise.plc.ast.Program;
//import edu.ufl.cise.plc.runtime.ColorTuple;
import edu.ufl.cise.plc.runtime.ConsoleIO;
import edu.ufl.cise.plc.runtime.javaCompilerClassLoader.DynamicClassLoader;
import edu.ufl.cise.plc.runtime.javaCompilerClassLoader.DynamicCompiler;

class Assignment5TestStarter {
	
	//Default package name for generated code
	String packageName = "cop4020sp22Package";

	boolean VERBOSE = true;

	private void show(Object obj) {
		if (VERBOSE)
			System.out.println(obj);
	}


	static final float DELTA = .001f;

	String getName(ASTNode ast) throws Exception {
		if (ast instanceof Program) {
			return ((Program) ast).getName();
		} else
			throw new Exception("bug--expected ast was program");
	}

	String getReturnType(ASTNode ast) throws Exception {
		if (ast instanceof Program) {
			return ((Program) ast).getReturnType().toString();
		} else
			throw new Exception("bug--expected ast was program");
	}


	void checkResult(String input, Object expectedResult) throws Exception {
		checkResult(input, null, expectedResult);
	}

	void checkResult(String input, Object[] params, Object expectedResult) throws Exception {
		Object result = exec(input, params);
		show("result = " + result);
		assertEquals(expectedResult, result);
	}

	
	Object exec(String input, Object[] params) throws Exception {
		//Lex and parse, to get AST
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


	private void displayResult(String input, Object[] params) throws Exception {
		Object result = exec(input,params);
		show(result);
	}
	
	// need separate method to check floats due to DELTA.  Note in simple cases, just use normal check.  
	void checkFloatResult(String input,  float expectedResult) throws Exception {
		checkFloatResult(input,  null, expectedResult);
	}

	void checkFloatResult(String input, Object[] params, float expectedResult) throws Exception {
		Object result = exec(input, params);
		show("result = " + result);
		assertEquals(expectedResult, (float) result, DELTA);
	}

	/* The first group of test return literal values of various types */

	@Test
	void test1() throws Exception {
		String input = "int y() ^42;";
		checkResult(input,  null, 42);
	}

	void test2() throws Exception {
		String input = """
				string y() ^ "hello";
				""";
		checkResult(input,  null, "hello");
	}

	@Test
	void test3() throws Exception {
		String input = "float f() ^ 1.2;";
		checkResult(input,  null, 1.2f);
	}

	@Test
	void test4() throws Exception {
		String input = "boolean f() ^ false;";
		checkResult(input,  null, false);
	}


	/* declares and initializes variable in declaration. returns variable */
	@Test
	void test6() throws Exception {
		String input = "int f() int x = 55; ^ x;";
		checkResult(input,  null, 55);
	}

	@Test
	void test7() throws Exception {
		String input = "string f() string x = \"this is a string\"; ^ x;";
		checkResult(input,  null, "this is a string");
	}

	@Test
	void test8() throws Exception {
		String input = "boolean f() boolean x = true; ^ x;";
		checkResult(input, null, true);
	}



	/* Declares variable, initializes in assignment statement, returns */
	@Test
	void test10() throws Exception {
		String input = "int f() int x; x = 33; ^ x;";
		checkResult(input, 33);
	}

	/* Declared variable, initializes in assignment statement, returns */
	@Test
	void test11() throws Exception {
		String input = "float f() float x; x = 56.67; ^ x;";
		checkFloatResult(input,  56.67f);
	}


	/* binary expressions */
	@Test
	void test15() throws Exception {
		String input = """
				int f()
				int a = 3;
				int b = 4;
				^ a+b;
				""";
		checkResult(input,  7);
	}

	@Test
	void test16() throws Exception {
		String input = """
				int f()
				int a = 20;
				int b = 15;
				^ a-b;
				""";
		checkResult(input,  20 - 15);
	}


	@Test
	void test20() throws Exception {
		String input = """
				float f()
				float a = 3.2;
				float b = 4.8;
				^ a+b;
				""";
		checkResult(input,  3.2f + 4.8f);
	}


	@Test
	void testParams1() throws Exception {
		String input = """
				int a(int b, int c)
				^ b+c;
				""";
		int b = 42;
		int c = 64;
		Object[] params = { b, c };
		checkResult(input, params, b + c);
	}

	@Test
	void testParams2() throws Exception {
		String input = """
				float a(float b, float c)
				^ b/c;
				""";
		float b = 42f;
		float c = 64f;
		Object[] params = { b, c };
		checkFloatResult(input, params, b / c);
	}



	@Test
	void testBinaryExpr0() throws Exception {
		String input = """
				int binary(int a, int b, int c)
				^ a + b *2 + c;
				""";
		int a = 33;
		int b = 24;
		int c = 50;
		Object[] params = { a, b, c };
		checkResult(input, params, a + (b * 2) + c);
	}

//	@Test
//	//This test reads from the console.  The tester should see that displayed result is same as entered.
//	void testReadInt0() throws Exception {
//		String input = """
//				int testRead0()
//				int x;
//				x <- console;
//				^ x;
//				         """;
//		displayResult(input, null);
//	}


//	@Test
//	void testMultipleConsoleIO() throws Exception {
//		String input = """
//				int testMultipleWrites()
//				int x0 = 34;
//				int x1 = 56;
//				write x0 + x1 -> console;
//				int x3 ;
//				x3 <- console;
//				write "x3="-> console;
//				write x3 -> console;
//				int x4 = x0;
//				x4 = (x4 + x1)/3;
//				write "  x4="-> console;
//				write x4 -> console;
//				^x4;
//				""";
//		checkResult(input, (34+56)/3);
//	}


	@Test
	void exampleTest() throws Exception {
		String input = "int y() ^42;";
		checkResult(input,  null, 42);
	}


	@Test
	void unaryExp1() throws Exception {
		String input = """
        int unaryExp1(int a, int b)
        int x = a + -b;
        ^ x;
        """;
		int a = 33;
		int b = 24;
		Object[] params = { a, b};
		checkResult(input, params, a - b);
	}

	@Test
	void unaryExp2() throws Exception {
		String input = """
        int unaryExp2(int a, int b, int c)
        int x = a - -b + -c;
        ^ x;
        """;
		int a = 33;
		int b = 24;
		int c = 2;
		Object[] params = { a, b, c};
		checkResult(input, params, a + b - c);
	}

	@Test
	void unaryExp3() throws Exception {
		String input = """
        int unaryExp3()
        int a = 33;
        int b = 24;
        int c = 2;
        int x = a - -b + -c;
        ^ x;
        """;
		checkResult(input, null, 55);
	}

	@Test
	void testConditional1() throws Exception {
		String input = """
        int testConditional1(int a, int b)
        int x = a+b;
        ^ if (x > 0) a else b fi;
        """;
		int a = 33;
		int b = 24;
		Object[] params = { a, b};
		checkResult(input, params, a);
	}

	@Test
	void testConditional2() throws Exception {
		String input = """
        boolean testConditional1()
        boolean x = false;
        ^ if (!x) true else false fi;
        """;
		checkResult(input, null, true);
	}


	@Test
	void testFloat() throws Exception {
		String input = "float y() ^42.0;";
		checkResult(input,  null, 42f);
	}




	@Test
	void testConditional3() throws Exception {
		String input = """
      int f()
         int a = 3;
         int b = 4;
        int c =
        if (a == 4)
             2
          else
            5
         fi;
      ^c;
      """;
		checkResult(input,  5);
	}

	@Test
	void testReturnInCondition4() throws Exception {
		String input = """
      float f()
         int a = 3;
         int b = (8 * 3) + a;
         float c =
         if (b == 27)
            2.5
         else
            6.5
         fi;
        
        ^c;
      """;
		checkResult(input,  2.5f);
	}

	@Test
	void testReturnInCondition5() throws Exception {
		String input = """
    boolean checkType(int b, float c)
    boolean a;
    if ((b+c) < 100)
           a = true
        else
          a = false
       fi;
    ^a;
    """;

		int b = 3;
		float c = 9.5f;
		Object[] params = {b,c};
		checkResult(input, params ,true);
	}


	@Test
	void testCast() throws Exception {//a and d should be cast to a float type
		String input = """
      float f()
         int a = 3;
         float b = 4.2;
         int d = 2;
        float c =  (a + b) + d;
       
      ^c;
      """;
		checkResult(input,  9.2f);
	}




	@Test
	void testp1() throws Exception {
		String input = """
	  		int a()
	  		int c = (5.1+5);
	  		^ c;
	  		""";
		checkResult(input,  null, 10);
	}

	@Test
	void testp2() throws Exception {
		String input = """
	  		int a()
	  		int c = if (5>6) 5.1 else 4.1 fi;
	  		^ c;
	  		""";
		checkResult(input,  null, 4);
	}



	@Test
	void testp3() throws Exception {
		String input = """
	  		int a()
	  		int c = -5.1;
	  		^ c;
	  		""";
		checkResult(input,  null, -5);
	}

	@Test
	void testp4() throws Exception {
		String input = """
	  		float a()
	  		float c = 5+(if (5<6) 5.1 else 4.1 fi);
	  		^ c;
	  		""";
		checkResult(input,  null, 10.1f);
	}

	@Test
	void testp5() throws Exception {
		String input = """
	  		int a()
	  		int c = (if (5<6) 5 else 4 fi)*6;

	  		^ c;
	  		""";
		checkResult(input,  null, 30);
	}

	@Test
	void testCoerce() throws Exception {
		String input = """
     int a()
     boolean x = false;
     int y;
     float c = 5.2;
     float b = 3.7;
     int d;
     d = c + b;
     float z = if (!x) d else 5 fi;
     y = z;    
     ^ y;
     """;
		checkResult(input,  null, 8);
	}

	@Test
	void testStringNotEquals() throws Exception{
		String input = """
			boolean a(string b)
			^ (b != "Test");
			""";
		String b = new String("test");
		Object params[] = {b};
		checkResult(input, params, true);
	}

	@Test
	void testStringEquals() throws Exception{
		String input = """
			boolean a(string b)
			^ (b == "test");
			""";
		String b = new String("test");
		Object params[] = {b};
		checkResult(input, params, true);
	}

	@Test
	void testMany() throws Exception{
		String input = """
			boolean a(int b, float c, string d)
			boolean e;
			e <- console;
			write "e = " -> console;
			write e -> console;
			boolean f = if (b >= c) e else false fi;
			write "\nf = " -> console;
			write f -> console;
			string g = "test";
			boolean h = if (d == g) b >= c else b < c fi;
			^ h;
			""";
		int b = 42;
		float c = 42.0f;
		String d = new String("test");
		Object params[] = {b, c, d};
		checkResult(input, params, true);
	}


	@Test
	void testStringNotEquals2() throws Exception{
		String input = """
				boolean a(string b)
				^ (b != "Test");
				""";
		String b = new String("Test");
		Object params[] = {b};
		checkResult(input, params, false);
	}
	@Test
	void binTest() throws Exception {
		String input = """
        int foo()
        int a = 11 / 5.0;
        ^a;
        """;
		checkResult(input,2);

	}
	@Test
	void params() throws Exception {

		String input = """
           boolean foo(string a, string b)
           ^ !(a == b);
           """;
		String a = "hello";
		String b = "hello";
		Object params[] = {a,b};
		checkResult(input, params,false);
	}
	@Test
	void binMany() throws Exception {
		String input = """
        int foo()
        int a = 10 * 2.5 / 5 + 3 % 4.5 + 0.5;
        ^a;
        """;
		checkResult(input,8);
		// (((((float)10*2.5f)/(float)5)+((float)3%4.5f))+0.5f);

	}

	@Test
	void testUsingJavaKeywordAsIdent_SHOULD_ALWAYS_FAIL() throws Exception{
		String input = """
        boolean a()
        int abstract = 9;
        ^ abstract == 9;
        """;
		//THIS SHOULD FAIL — make sure you get the keywords
		Exception e = assertThrows(Exception.class, () -> {
			checkResult(input, true);
		});
		show("Expected Exception:     " + e);

	}

	@Test
	void testFailedTest() throws Exception{
		String input = """
        string f()
        string x;
        x = "abc";
        ^ x;
        """;
		checkResult(input, "abc");

	}










}
