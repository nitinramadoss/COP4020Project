package edu.ufl.cise.plc;

import static edu.ufl.cise.plc.IToken.Kind.ASSIGN;
import static edu.ufl.cise.plc.IToken.Kind.GT;
import static edu.ufl.cise.plc.IToken.Kind.PLUS;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintStream;
import java.util.List;

import edu.ufl.cise.plc.ast.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import edu.ufl.cise.plc.CompilerComponentFactory;
import edu.ufl.cise.plc.IParser;
import edu.ufl.cise.plc.SyntaxException;
import edu.ufl.cise.plc.TypeCheckException;
import edu.ufl.cise.plc.TypeCheckVisitor;
import edu.ufl.cise.plc.ast.Types.Type;

class StarterTests {
	private ASTNode getAST(String input) throws Exception {
		IParser parser = CompilerComponentFactory.getParser(input);
		return parser.parse();
	}

//makes it easy to turn output on and off (and less typing than System.out.println)
	static final boolean VERBOSE = true;

	void show(Object obj) {
		if (VERBOSE) {
			System.out.println(obj);
		}
	}

	static PrintStream out = System.out;
	static int seconds = 2;

	private ASTNode checkTypes(ASTNode ast) throws Exception {
		TypeCheckVisitor v = (TypeCheckVisitor) CompilerComponentFactory.getTypeChecker();
		ast.visit(v, null);
		return ast;
	}

	@DisplayName("test0")
	@Test
	public void test0(TestInfo testInfo) throws Exception {
		String input = """
				boolean b() ^ true;
				""";
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		checkTypes(ast);
		show(ast);
		assertThat("", ast, instanceOf(Program.class));
		assertEquals(Type.BOOLEAN, ((Program) ast).getReturnType());
		List<NameDef> params = ((Program) ast).getParams();
		assertEquals(0, params.size());
		List<ASTNode> decsAndStatements = ((Program) ast).getDecsAndStatements();
		assertEquals(1, decsAndStatements.size());
		ASTNode var0 = decsAndStatements.get(0);
		assertThat("", var0, instanceOf(ReturnStatement.class));
		Expr var1 = ((ReturnStatement) var0).getExpr();
		assertThat("", var1, instanceOf(BooleanLitExpr.class));
		assertTrue(((BooleanLitExpr) var1).getValue());
		assertEquals(Type.BOOLEAN, var1.getType());
		assertThat(var1.getCoerceTo(), anyOf(nullValue(), is(var1.getType())));
	}

	@DisplayName("test1")
	@Test
	public void test1(TestInfo testInfo) throws Exception {
		String input = """
				boolean b() ^ 42.35; #type error

				""";
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}

	@DisplayName("test2")
	@Test
	public void test2(TestInfo testInfo) throws Exception {
		String input = """
				float f() ^ 10.3
				""";
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		Exception e = assertThrows(SyntaxException.class, () -> {
			@SuppressWarnings("unused")
			ASTNode ast = getAST(input);
		});
		show("Expected SyntaxException:     " + e);
	}

	@DisplayName("test3")
	@Test
	public void test3(TestInfo testInfo) throws Exception {
		String input = """
				void withParams(string s, image s)
				""";
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}

	@DisplayName("test4")
	@Test
	public void test4(TestInfo testInfo) throws Exception {
		String input = """
				int g() ^0;
				""";
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		checkTypes(ast);
		show(ast);
		assertThat("", ast, instanceOf(Program.class));
		assertEquals(Type.INT, ((Program) ast).getReturnType());
		List<NameDef> params = ((Program) ast).getParams();
		assertEquals(0, params.size());
		List<ASTNode> decsAndStatements = ((Program) ast).getDecsAndStatements();
		assertEquals(1, decsAndStatements.size());
		ASTNode var0 = decsAndStatements.get(0);
		assertThat("", var0, instanceOf(ReturnStatement.class));
		Expr var1 = ((ReturnStatement) var0).getExpr();
		assertThat("", var1, instanceOf(IntLitExpr.class));
		assertEquals(0, ((IntLitExpr) var1).getValue());
		assertEquals(Type.INT, var1.getType());
		assertThat(var1.getCoerceTo(), anyOf(nullValue(), is(var1.getType())));
	}

	@DisplayName("test5")
	@Test
	public void test5(TestInfo testInfo) throws Exception {
		String input = """
				int g()
				^ "Hello";  #error, wrong return type

				""";
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}

	@DisplayName("test6")
	@Test
	public void test6(TestInfo testInfo) throws Exception {
		String input = """
				int f(int x)
				 		int y = x;
				 		^ y;

				""";
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		checkTypes(ast);
		show(ast);
		assertThat("", ast, instanceOf(Program.class));
		assertEquals(Type.INT, ((Program) ast).getReturnType());
		List<NameDef> params = ((Program) ast).getParams();
		assertEquals(1, params.size());
		NameDef var0 = params.get(0);
		assertThat("", var0, instanceOf(NameDef.class));
		assertEquals(Type.INT, var0.getType());
		assertEquals("x", var0.getName());
		List<ASTNode> decsAndStatements = ((Program) ast).getDecsAndStatements();
		assertEquals(2, decsAndStatements.size());
		ASTNode var1 = decsAndStatements.get(0);
		assertThat("", var1, instanceOf(VarDeclaration.class));
		NameDef var2 = ((VarDeclaration) var1).getNameDef();
		assertThat("", var2, instanceOf(NameDef.class));
		assertEquals(Type.INT, var2.getType());
		assertEquals("y", var2.getName());
		Expr var3 = ((VarDeclaration) var1).getExpr();
		assertThat("", var3, instanceOf(IdentExpr.class));
		assertEquals("x", var3.getText());
		assertEquals(Type.INT, var3.getType());
		assertThat(var3.getCoerceTo(), anyOf(nullValue(), is(var3.getType())));
		assertEquals(ASSIGN, ((VarDeclaration) var1).getOp().getKind());
		ASTNode var4 = decsAndStatements.get(1);
		assertThat("", var4, instanceOf(ReturnStatement.class));
		Expr var5 = ((ReturnStatement) var4).getExpr();
		assertThat("", var5, instanceOf(IdentExpr.class));
		assertEquals("y", var5.getText());
		assertEquals(Type.INT, var5.getType());
		assertThat(var5.getCoerceTo(), anyOf(nullValue(), is(var5.getType())));
	}

	@DisplayName("test7")
	@Test
	public void test7(TestInfo testInfo) throws Exception {
		String input = """
				   int f(int x)
				int y;
				    ^ y;  #error:  y not initilized

				""";
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}

	@DisplayName("test8")
	@Test
	public void test8(TestInfo testInfo) throws Exception {
		String input = """
				float f(int x)
				float z = x;  #coerce x to float
				^z;

				""";
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		checkTypes(ast);
		show(ast);
		assertThat("", ast, instanceOf(Program.class));
		assertEquals(Type.FLOAT, ((Program) ast).getReturnType());
		List<NameDef> params = ((Program) ast).getParams();
		assertEquals(1, params.size());
		NameDef var0 = params.get(0);
		assertThat("", var0, instanceOf(NameDef.class));
		assertEquals(Type.INT, var0.getType());
		assertEquals("x", var0.getName());
		List<ASTNode> decsAndStatements = ((Program) ast).getDecsAndStatements();
		assertEquals(2, decsAndStatements.size());
		ASTNode var1 = decsAndStatements.get(0);
		assertThat("", var1, instanceOf(VarDeclaration.class));
		NameDef var2 = ((VarDeclaration) var1).getNameDef();
		assertThat("", var2, instanceOf(NameDef.class));
		assertEquals(Type.FLOAT, var2.getType());
		assertEquals("z", var2.getName());
		Expr var3 = ((VarDeclaration) var1).getExpr();
		assertThat("", var3, instanceOf(IdentExpr.class));
		assertEquals("x", var3.getText());
		assertEquals(Type.INT, var3.getType());
		assertEquals(Type.FLOAT, var3.getCoerceTo());
		assertEquals(ASSIGN, ((VarDeclaration) var1).getOp().getKind());
		ASTNode var4 = decsAndStatements.get(1);
		assertThat("", var4, instanceOf(ReturnStatement.class));
		Expr var5 = ((ReturnStatement) var4).getExpr();
		assertThat("", var5, instanceOf(IdentExpr.class));
		assertEquals("z", var5.getText());
		assertEquals(Type.FLOAT, var5.getType());
		assertThat(var5.getCoerceTo(), anyOf(nullValue(), is(var5.getType())));
	}

	@DisplayName("test9")
	@Test
	public void test9(TestInfo testInfo) throws Exception {
		String input = """
				color c()
				^ << 255, 0, 127 >>;

				""";
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		checkTypes(ast);
		show(ast);
		assertThat("", ast, instanceOf(Program.class));
		assertEquals(Type.COLOR, ((Program) ast).getReturnType());
		List<NameDef> params = ((Program) ast).getParams();
		assertEquals(0, params.size());
		List<ASTNode> decsAndStatements = ((Program) ast).getDecsAndStatements();
		assertEquals(1, decsAndStatements.size());
		ASTNode var0 = decsAndStatements.get(0);
		assertThat("", var0, instanceOf(ReturnStatement.class));
		Expr var1 = ((ReturnStatement) var0).getExpr();
		assertThat("", var1, instanceOf(ColorExpr.class));
		Expr var2 = ((ColorExpr) var1).getRed();
		assertThat("", var2, instanceOf(IntLitExpr.class));
		assertEquals(255, ((IntLitExpr) var2).getValue());
		assertEquals(Type.INT, var2.getType());
		assertThat(var2.getCoerceTo(), anyOf(nullValue(), is(var2.getType())));
		Expr var3 = ((ColorExpr) var1).getGreen();
		assertThat("", var3, instanceOf(IntLitExpr.class));
		assertEquals(0, ((IntLitExpr) var3).getValue());
		assertEquals(Type.INT, var3.getType());
		assertThat(var3.getCoerceTo(), anyOf(nullValue(), is(var3.getType())));
		Expr var4 = ((ColorExpr) var1).getBlue();
		assertThat("", var4, instanceOf(IntLitExpr.class));
		assertEquals(127, ((IntLitExpr) var4).getValue());
		assertEquals(Type.INT, var4.getType());
		assertThat(var4.getCoerceTo(), anyOf(nullValue(), is(var4.getType())));
		assertEquals(Type.COLOR, var1.getType());
		assertThat(var1.getCoerceTo(), anyOf(nullValue(), is(var1.getType())));
	}

	@DisplayName("test10")
	@Test
	public void test10(TestInfo testInfo) throws Exception {
		String input = """
				boolean f(int a, int b, int c)
				^ a+b > c;

				""";
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		checkTypes(ast);
		show(ast);
		assertThat("", ast, instanceOf(Program.class));
		assertEquals(Type.BOOLEAN, ((Program) ast).getReturnType());
		List<NameDef> params = ((Program) ast).getParams();
		assertEquals(3, params.size());
		NameDef var0 = params.get(0);
		assertThat("", var0, instanceOf(NameDef.class));
		assertEquals(Type.INT, var0.getType());
		assertEquals("a", var0.getName());
		NameDef var1 = params.get(1);
		assertThat("", var1, instanceOf(NameDef.class));
		assertEquals(Type.INT, var1.getType());
		assertEquals("b", var1.getName());
		NameDef var2 = params.get(2);
		assertThat("", var2, instanceOf(NameDef.class));
		assertEquals(Type.INT, var2.getType());
		assertEquals("c", var2.getName());
		List<ASTNode> decsAndStatements = ((Program) ast).getDecsAndStatements();
		assertEquals(1, decsAndStatements.size());
		ASTNode var3 = decsAndStatements.get(0);
		assertThat("", var3, instanceOf(ReturnStatement.class));
		Expr var4 = ((ReturnStatement) var3).getExpr();
		assertThat("", var4, instanceOf(BinaryExpr.class));
		assertEquals(GT, ((BinaryExpr) var4).getOp().getKind());
		Expr var5 = ((BinaryExpr) var4).getLeft();
		assertThat("", var5, instanceOf(BinaryExpr.class));
		assertEquals(PLUS, ((BinaryExpr) var5).getOp().getKind());
		Expr var6 = ((BinaryExpr) var5).getLeft();
		assertThat("", var6, instanceOf(IdentExpr.class));
		assertEquals("a", var6.getText());
		assertEquals(Type.INT, var6.getType());
		assertThat(var6.getCoerceTo(), anyOf(nullValue(), is(var6.getType())));
		Expr var7 = ((BinaryExpr) var5).getRight();
		assertThat("", var7, instanceOf(IdentExpr.class));
		assertEquals("b", var7.getText());
		assertEquals(Type.INT, var7.getType());
		assertThat(var7.getCoerceTo(), anyOf(nullValue(), is(var7.getType())));
		assertEquals(Type.INT, var5.getType());
		assertThat(var5.getCoerceTo(), anyOf(nullValue(), is(var5.getType())));
		Expr var8 = ((BinaryExpr) var4).getRight();
		assertThat("", var8, instanceOf(IdentExpr.class));
		assertEquals("c", var8.getText());
		assertEquals(Type.INT, var8.getType());
		assertThat(var8.getCoerceTo(), anyOf(nullValue(), is(var8.getType())));
		assertEquals(Type.BOOLEAN, var4.getType());
		assertThat(var4.getCoerceTo(), anyOf(nullValue(), is(var4.getType())));
	}

	//Example Test
	@DisplayName("MyTest")
	@Test
	public void myTest(TestInfo testInfo) throws Exception{
		String input = """
        boolean a()
       
        ^ 3==3 | 2==2;
       
        """;
		ASTNode ast = getAST(input);
		checkTypes(ast);
		show(ast);
	}

	@DisplayName("test11")
	@Test
	public void test11(TestInfo testInfo) throws Exception {
		String input = """
               int a()
                  int b;
                  int c = b;
                  ^ c;

        """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}


	@DisplayName("test12")
	@Test
	public void test12(TestInfo testInfo) throws Exception {
		String input = """
               int a()
                  int b;
                  int c = b + 2;
                  ^ c;

        """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}


	@DisplayName("test13")
	@Test
	public void test13(TestInfo testInfo) throws Exception {
		String input = """
               boolean a()
                  boolean b;
                  boolean c = (b & true);
                  ^ c;

        """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}


	@DisplayName("test14")
	@Test
	public void test14(TestInfo testInfo) throws Exception {
		String input = """
               boolean a()
                  boolean b;
                  boolean c = ((b & true) & false);
                  ^ c;

        """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}

	@DisplayName("test15")
	@Test
	public void test15(TestInfo testInfo) throws Exception {
		String input = """
               void a(int size)
                  image[size,size] a;
                  a[x,y] = <<(x*y) % 255, 0, 0>>;
                  int x = 5;
                  ^ a;

        """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}

	@DisplayName("test16")
	@Test
	public void test16(TestInfo testInfo) throws Exception {
		String input = """
               void z(int size)
                  image[size,size] a;
                  a[x,y] = <<(x*y) % 255, 0, 0>>;
                  x = 5;
                  ^ a;

        """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}


	@DisplayName("test17")
	@Test
	public void test17(TestInfo testInfo) throws Exception {
		String input = """
                    void z(int size)
                        image[size,size] a;
                        a[x+1,y] = <<(x*y) % 255, 0, 0>>;
                        x = 5;
                        ^ a;
 
                """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}

	@DisplayName("test18")
	@Test
	public void test18(TestInfo testInfo) throws Exception {
		String input = """
                    void z(int size)
                        image[size,size] a;
                        a[x,y+1] = <<(x*y) % 255, 0, 0>>;
                        ^ a;
 
                """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}


	@DisplayName("test19")
	@Test
	public void test19(TestInfo testInfo) throws Exception {
		String input = """
                  image test(int size)
                      image[size,size] a;
                      int x = 0;
                      int y = 0;
                      a[x,y] = 5;
                      ^ a;

              """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}

	@DisplayName("test20")
	@Test
	public void test20(TestInfo testInfo) throws Exception {
		String input = """
              image test(int size)
                  image[size,size] a;
                  a[0,0] = 5;
                  ^ a;

              """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}

	@DisplayName("test21")
	@Test
	public void test21(TestInfo testInfo) throws Exception {
		String input = """
            boolean b()
            ^ if (2 + 3)
                true
            else
                false
            fi; #type error
 
                    """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}

	@DisplayName("test22")
	@Test
	public void test22(TestInfo testInfo) throws Exception {
		String input = """
            boolean test22(int a, int b)
            ^ if (a == b)
                17
            else
                false
            fi; #type error
 
                    """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}

	@DisplayName("test23")
	@Test
	public void test23(TestInfo testInfo) throws Exception {
		String input = """
            boolean test23(int a, int b)
            ^ if (a == b)
                17
            else
                20
            fi; #type error
 
                    """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}

	@DisplayName("test24")
	@Test
	public void test24(TestInfo testInfo) throws Exception {
		String input = """
                    int test()
                        int a;
                        a[x,y] <- "Cannot have pixel selector";
                        ^ a;
 
                    """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}

	@DisplayName("test25")
	@Test
	public void test25(TestInfo testInfo) throws Exception {
		String input = """
                    int test()
                        int a;
                        a <- 10;
                        ^ a;
 
                    """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}

	@DisplayName("test26")
	@Test
	public void test26(TestInfo testInfo) throws Exception {
		String input = """
                    int test()
                        int a;
                        a[x,y] = 10;
                        ^ a;
 
                    """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}

	@DisplayName("test27")
	@Test
	public void test27(TestInfo testInfo) throws Exception {
		String input = """
                    int test()
                        int a;
                        a = "Wrong type";
                        ^ a;
 
                    """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}

	@DisplayName("test28")
	@Test
	public void test28(TestInfo testInfo) throws Exception {
		String input = """
                    image test(int size)
                        image[size,size] a;
                        a = "Wrong type";
                        ^ a;
 
                    """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}

	@DisplayName("test29")
	@Test
	public void test29(TestInfo testInfo) throws Exception {
		String input = """
                    image test(int size)
                        image[size,size] a;
                        a[x,y] = "Wrong type";
                        ^ a;
 
                    """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}

	@DisplayName("test30")
	@Test
	public void test30(TestInfo testInfo) throws Exception {
		String input = """
                    image test(int size)
                        image a;
                        a[x,y] = 10;
                        ^ a;
 
                    """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}

	@DisplayName("test31")
	@Test
	public void test31(TestInfo testInfo) throws Exception {
		String input = """
                    image test(int size)
                        image[true,10] a;
                        a[x,y] = 10;
                        ^ a;
 
                    """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}

	@DisplayName("test32")
	@Test
	public void test32(TestInfo testInfo) throws Exception {
		String input = """
                    int test()
                        int[10,10] a;
                        ^ a;
 
                    """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}

	@DisplayName("test33")
	@Test
	public void test33(TestInfo testInfo) throws Exception{
		String input = """
                        image test(int size)
                            image[size,size] a;
                            a = 10;
                            image b = a;
                            ^ a;
 
                        """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);

		checkTypes(ast);

		List<ASTNode> decsAndStatements = ((Program) ast).getDecsAndStatements();
		ASTNode var0 = decsAndStatements.get(1);
		Expr var1 = ((AssignmentStatement) var0).getExpr();
		assertThat("", var1, instanceOf(IntLitExpr.class));
		assertEquals(Type.INT, var1.getType());
		assertEquals(Type.COLOR, var1.getCoerceTo());
		show(ast);
	}
	@DisplayName("test34")
	@Test
	public void test34(TestInfo testInfo) throws Exception{
		String input = """
	                    color BDP1(int size)
	   			          int Z = 255;
	   			          image[size,size] a;
	   			          a[x,y] = <<(x/8*y/8)%(Z+1), 0, 0>>;
	   			          int b = getRed a[size/2, size/2];
	   				    ^ BLUE * b;
                        """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		checkTypes(ast);
		List<ASTNode> decsAndStatements = ((Program) ast).getDecsAndStatements();
		VarDeclaration var0 = (VarDeclaration) decsAndStatements.get(0);
		assertEquals(Type.INT, var0.getType());
		assertThat("", var0.getExpr(), instanceOf(IntLitExpr.class));
		assertEquals(255, var0.getExpr().getFirstToken().getIntValue());
		AssignmentStatement var1 = (AssignmentStatement) decsAndStatements.get(2);
		assertEquals(Type.INT, var1.getSelector().getX().getType());
		assertEquals(Type.INT, var1.getSelector().getY().getType());
		Expr var2 = var1.getExpr();
		assertThat("", var2, instanceOf(ColorExpr.class));
		Expr var3 = ((ColorExpr) var2).getRed();
		assertThat("", var3, instanceOf(BinaryExpr.class));
		assertEquals(Type.INT, var3.getType());
		VarDeclaration var4 = (VarDeclaration) decsAndStatements.get(3);
		assertThat("", var4.getExpr(), instanceOf(UnaryExpr.class));
		UnaryExpr var5 = (UnaryExpr) var4.getExpr();
		assertEquals("getRed", var5.getOp().getText());
		assertThat("", var5.getExpr(), instanceOf(UnaryExprPostfix.class));
		ReturnStatement var6 = (ReturnStatement) decsAndStatements.get(4);
		assertEquals(Type.COLOR, var6.getExpr().getType());
		BinaryExpr var7 = (BinaryExpr) var6.getExpr();
		assertEquals(Type.COLOR, var7.getLeft().getType());
		assertEquals(Type.INT, var7.getRight().getType());
		assertEquals(Type.COLOR, var7.getRight().getCoerceTo());
		show(ast);
	}

	@DisplayName("test35a")
	@Test
	public void test35a(TestInfo testInfo) throws Exception{
		String input = """
                void f(int y)
			    y <- 25;
                """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
		show("Expected TypeCheckException:     " + e);
	}

	@DisplayName("test35b")
	@Test
	public void test35b(TestInfo testInfo) throws Exception{
		String input = """
                void f(int y)
			    y <- "this is a string";
                """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		checkTypes(ast);
		ReadStatement var0 = (ReadStatement) ((Program) ast).getDecsAndStatements().get(0);
		assertEquals(Type.INT, var0.getTargetDec().getType());
		assertEquals(Type.STRING, var0.getSource().getType());
		Type type = var0.getSource().getCoerceTo();
		if(type!= null)
			assertEquals(Type.STRING, type);
		show(ast);
	}

	@DisplayName("test35c")
	@Test
	public void test35c(TestInfo testInfo) throws Exception{
		String input = """
                void f(int y)
			    y <- console;
                """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		checkTypes(ast);
		ReadStatement var0 = (ReadStatement) ((Program) ast).getDecsAndStatements().get(0);
		assertEquals(Type.INT, var0.getTargetDec().getType());
		assertEquals(Type.CONSOLE, var0.getSource().getType());
		assertEquals(Type.INT, var0.getSource().getCoerceTo());
		show(ast);
	}

	@DisplayName("test21e")
	@Test
	public void test21e(TestInfo testInfo) throws Exception {
		String input = """
           float b()
           ^ if (true)
              2 + 2.2
           else
              3 + 3.3
           fi; #type error

                   """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		checkTypes(ast);

		// DECS AND STATEMENTS
		List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
		assertEquals(1, decsAndStatements.size());

		ASTNode returnStatement = decsAndStatements.get(0);
		Expr conditionalExpr = ((ReturnStatement) returnStatement).getExpr();
		assertEquals(Type.FLOAT, conditionalExpr.getType());
	}

	// tests if variable is initialized in the read statement
	@DisplayName("test36")
	@Test
	public void test36(TestInfo testInfo) throws Exception{
		String input = """
         void b(int y)
         int x;
         x <- console;
         int z = x;
        

                 """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		checkTypes(ast);

	}


	// read statement should have no pixel selector
	@DisplayName("test36a")
	@Test
	public void test36a(TestInfo testInfo) throws Exception{
		String input = """
         void b(int y)
         int x;
         x [1,2] <- console;
        

                 """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkTypes(ast);
		});
	}


	// local variables should be removed from symbol table after
// right hand side of image with pixel selector is evaluated
	@DisplayName("test37")
	@Test
	public void test37(TestInfo testInfo) throws Exception {
		String input = """
            image test(int size)
                image[size,size] a;
                int y = 0;
                a[j,u] = << j, u, 5 >>;
                int j;
                string u = "YES";
                ^ a;

        """;
		show("-------------");
		show(testInfo.getDisplayName());
		show(input);
		ASTNode ast = getAST(input);

		checkTypes(ast);


	}








}
