package edu.ufl.cise.plc;

import static edu.ufl.cise.plc.IToken.Kind.MINUS;
import static edu.ufl.cise.plc.IToken.Kind.BANG;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import org.junit.jupiter.api.Test;

import edu.ufl.cise.plc.CompilerComponentFactory;
import edu.ufl.cise.plc.IParser;
import edu.ufl.cise.plc.IToken;
import edu.ufl.cise.plc.IToken.Kind;
import static edu.ufl.cise.plc.IToken.Kind.*;
import edu.ufl.cise.plc.ast.ASTNode;
import edu.ufl.cise.plc.ast.AssignmentStatement;
import edu.ufl.cise.plc.ast.BooleanLitExpr;
import edu.ufl.cise.plc.ast.ConditionalExpr;
import edu.ufl.cise.plc.ast.ConsoleExpr;
import edu.ufl.cise.plc.ast.ColorExpr;
import edu.ufl.cise.plc.ast.ColorConstExpr;
import edu.ufl.cise.plc.ast.Dimension;
import edu.ufl.cise.plc.ast.Expr;
import edu.ufl.cise.plc.ast.FloatLitExpr;
import edu.ufl.cise.plc.ast.IdentExpr;
import edu.ufl.cise.plc.ast.IntLitExpr;
import edu.ufl.cise.plc.ast.PixelSelector;
import edu.ufl.cise.plc.ast.Program;
import edu.ufl.cise.plc.ast.NameDef;
import edu.ufl.cise.plc.ast.NameDefWithDim;
import edu.ufl.cise.plc.ast.Types;
import static edu.ufl.cise.plc.ast.Types.Type;
import edu.ufl.cise.plc.ast.ReadStatement;
import edu.ufl.cise.plc.ast.ReturnStatement;
import edu.ufl.cise.plc.ast.StringLitExpr;
import edu.ufl.cise.plc.ast.UnaryExpr;
import edu.ufl.cise.plc.ast.UnaryExprPostfix;
import edu.ufl.cise.plc.ast.BinaryExpr;
import edu.ufl.cise.plc.ast.WriteStatement;
import edu.ufl.cise.plc.ast.VarDeclaration;
import edu.ufl.cise.plc.LexicalException;
import edu.ufl.cise.plc.SyntaxException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import java.io.PrintStream;
import java.util.List;

class Assignment3StarterTest {

	private ASTNode getAST(String input) throws Exception {
		IParser parser = CompilerComponentFactory.getParser(input);
		return parser.parse();
	}

	// makes it easy to turn output on and off (and less typing than
	// System.out.println)
	static final boolean VERBOSE = true;

	void show(Object obj) {
		if (VERBOSE) {
			System.out.println(obj);
		}
	}

	@DisplayName("test0")
	@Test
	public void test0(TestInfo testInfo) throws Exception {
		String input = """
				boolean b()
				""";
		show("-------------");
		show(input);
		ASTNode ast = getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(Program.class));
		assertEquals(Type.BOOLEAN, ((Program) ast).getReturnType());
		List<NameDef> params = ((Program) ast).getParams();
		assertEquals(0, params.size());
		List<ASTNode> decsAndStatements = ((Program) ast).getDecsAndStatements();
		assertEquals(0, decsAndStatements.size());
	}

	@DisplayName("test1")
	@Test
	public void test1(TestInfo testInfo) throws Exception {
		String input = """
				float f()
				""";
		show("-------------");
		show(input);
		ASTNode ast = getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(Program.class));
		assertEquals(Type.FLOAT, ((Program) ast).getReturnType());
		List<NameDef> params = ((Program) ast).getParams();
		assertEquals(0, params.size());
		List<ASTNode> decsAndStatements = ((Program) ast).getDecsAndStatements();
		assertEquals(0, decsAndStatements.size());
	}

	@DisplayName("test2")
	@Test
	public void test2(TestInfo testInfo) throws Exception {
		String input = """
				image im()
				""";
		show("-------------");
		show(input);
		ASTNode ast = getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(Program.class));
		assertEquals(Type.IMAGE, ((Program) ast).getReturnType());
		List<NameDef> params = ((Program) ast).getParams();
		assertEquals(0, params.size());
		List<ASTNode> decsAndStatements = ((Program) ast).getDecsAndStatements();
		assertEquals(0, decsAndStatements.size());
	}

	@DisplayName("test3")
	@Test
	public void test3(TestInfo testInfo) throws Exception {
		String input = """
				int i()
				""";
		show("-------------");
		show(input);
		ASTNode ast = getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(Program.class));
		assertEquals(Type.INT, ((Program) ast).getReturnType());
		List<NameDef> params = ((Program) ast).getParams();
		assertEquals(0, params.size());
		List<ASTNode> decsAndStatements = ((Program) ast).getDecsAndStatements();
		assertEquals(0, decsAndStatements.size());
	}

	@DisplayName("test4")
	@Test
	public void test4(TestInfo testInfo) throws Exception {
		String input = """
				string s()
				""";
		show("-------------");
		show(input);
		ASTNode ast = getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(Program.class));
		assertEquals(Type.STRING, ((Program) ast).getReturnType());
		List<NameDef> params = ((Program) ast).getParams();
		assertEquals(0, params.size());
		List<ASTNode> decsAndStatements = ((Program) ast).getDecsAndStatements();
		assertEquals(0, decsAndStatements.size());
	}

	@DisplayName("test5")
	@Test
	public void test5(TestInfo testInfo) throws Exception {
		String input = """
				void v()
				""";
		show("-------------");
		show(input);
		ASTNode ast = getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(Program.class));
		assertEquals(Type.VOID, ((Program) ast).getReturnType());
		List<NameDef> params = ((Program) ast).getParams();
		assertEquals(0, params.size());
		List<ASTNode> decsAndStatements = ((Program) ast).getDecsAndStatements();
		assertEquals(0, decsAndStatements.size());
	}

	@DisplayName("test6")
	@Test
	public void test6(TestInfo testInfo) throws Exception {
		String input = """
				void withParams(int i, boolean b, float f, string s, image i)
				""";
		show("-------------");
		show(input);
		ASTNode ast = getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(Program.class));
		assertEquals(Type.VOID, ((Program) ast).getReturnType());
		List<NameDef> params = ((Program) ast).getParams();
		assertEquals(5, params.size());
		NameDef var0 = params.get(0);
		assertThat("", var0, instanceOf(NameDef.class));
		assertEquals(Type.INT, ((NameDef) var0).getType());
		assertEquals("i", ((NameDef) var0).getName());
		NameDef var1 = params.get(1);
		assertThat("", var1, instanceOf(NameDef.class));
		assertEquals(Type.BOOLEAN, ((NameDef) var1).getType());
		assertEquals("b", ((NameDef) var1).getName());
		NameDef var2 = params.get(2);
		assertThat("", var2, instanceOf(NameDef.class));
		assertEquals(Type.FLOAT, ((NameDef) var2).getType());
		assertEquals("f", ((NameDef) var2).getName());
		NameDef var3 = params.get(3);
		assertThat("", var3, instanceOf(NameDef.class));
		assertEquals(Type.STRING, ((NameDef) var3).getType());
		assertEquals("s", ((NameDef) var3).getName());
		NameDef var4 = params.get(4);
		assertThat("", var4, instanceOf(NameDef.class));
		assertEquals(Type.IMAGE, ((NameDef) var4).getType());
		assertEquals("i", ((NameDef) var4).getName());
		List<ASTNode> decsAndStatements = ((Program) ast).getDecsAndStatements();
		assertEquals(0, decsAndStatements.size());
	}

	@DisplayName("test7")
	@Test
	public void test7(TestInfo testInfo) throws Exception {
		String input = """
				string withUninitializedDecs()
				    int a;
				    float b;
				    image c;
				    string e;
				    boolean f;
				    color e;

				""";
		show("-------------");
		show(input);
		ASTNode ast = getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(Program.class));
		assertEquals(Type.STRING, ((Program) ast).getReturnType());
		List<NameDef> params = ((Program) ast).getParams();
		assertEquals(0, params.size());
		List<ASTNode> decsAndStatements = ((Program) ast).getDecsAndStatements();
		assertEquals(6, decsAndStatements.size());
		ASTNode var0 = decsAndStatements.get(0);
		assertThat("", var0, instanceOf(VarDeclaration.class));
		NameDef var1 = ((VarDeclaration) var0).getNameDef();
		assertThat("", var1, instanceOf(NameDef.class));
		assertEquals(Type.INT, ((NameDef) var1).getType());
		assertEquals("a", ((NameDef) var1).getName());
		ASTNode var2 = decsAndStatements.get(1);
		assertThat("", var2, instanceOf(VarDeclaration.class));
		NameDef var3 = ((VarDeclaration) var2).getNameDef();
		assertThat("", var3, instanceOf(NameDef.class));
		assertEquals(Type.FLOAT, ((NameDef) var3).getType());
		assertEquals("b", ((NameDef) var3).getName());
		ASTNode var4 = decsAndStatements.get(2);
		assertThat("", var4, instanceOf(VarDeclaration.class));
		NameDef var5 = ((VarDeclaration) var4).getNameDef();
		assertThat("", var5, instanceOf(NameDef.class));
		assertEquals(Type.IMAGE, ((NameDef) var5).getType());
		assertEquals("c", ((NameDef) var5).getName());
		ASTNode var6 = decsAndStatements.get(3);
		assertThat("", var6, instanceOf(VarDeclaration.class));
		NameDef var7 = ((VarDeclaration) var6).getNameDef();
		assertThat("", var7, instanceOf(NameDef.class));
		assertEquals(Type.STRING, ((NameDef) var7).getType());
		assertEquals("e", ((NameDef) var7).getName());
		ASTNode var8 = decsAndStatements.get(4);
		assertThat("", var8, instanceOf(VarDeclaration.class));
		NameDef var9 = ((VarDeclaration) var8).getNameDef();
		assertThat("", var9, instanceOf(NameDef.class));
		assertEquals(Type.BOOLEAN, ((NameDef) var9).getType());
		assertEquals("f", ((NameDef) var9).getName());
		ASTNode var10 = decsAndStatements.get(5);
		assertThat("", var10, instanceOf(VarDeclaration.class));
		NameDef var11 = ((VarDeclaration) var10).getNameDef();
		assertThat("", var11, instanceOf(NameDef.class));
		assertEquals(Type.COLOR, ((NameDef) var11).getType());
		assertEquals("e", ((NameDef) var11).getName());
	}

	@DisplayName("test8")
	@Test
	public void test8(TestInfo testInfo) throws Exception {
		String input = """
				void a123(int a, boolean b, string s, float f, image i)
					int a0 = 0;
					float b0 = 10.01;
					boolean c0 = true;
					boolean c1 = false;
					string s0 = "hello";
					color color0 = <<0,0,0>>;
					color color1 = BLACK;

				""";
		show("-------------");
		show(input);
		ASTNode ast = getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(Program.class));
		assertEquals(Type.VOID, ((Program) ast).getReturnType());
		List<NameDef> params = ((Program) ast).getParams();
		assertEquals(5, params.size());
		NameDef var0 = params.get(0);
		assertThat("", var0, instanceOf(NameDef.class));
		assertEquals(Type.INT, ((NameDef) var0).getType());
		assertEquals("a", ((NameDef) var0).getName());
		NameDef var1 = params.get(1);
		assertThat("", var1, instanceOf(NameDef.class));
		assertEquals(Type.BOOLEAN, ((NameDef) var1).getType());
		assertEquals("b", ((NameDef) var1).getName());
		NameDef var2 = params.get(2);
		assertThat("", var2, instanceOf(NameDef.class));
		assertEquals(Type.STRING, ((NameDef) var2).getType());
		assertEquals("s", ((NameDef) var2).getName());
		NameDef var3 = params.get(3);
		assertThat("", var3, instanceOf(NameDef.class));
		assertEquals(Type.FLOAT, ((NameDef) var3).getType());
		assertEquals("f", ((NameDef) var3).getName());
		NameDef var4 = params.get(4);
		assertThat("", var4, instanceOf(NameDef.class));
		assertEquals(Type.IMAGE, ((NameDef) var4).getType());
		assertEquals("i", ((NameDef) var4).getName());
		List<ASTNode> decsAndStatements = ((Program) ast).getDecsAndStatements();
		assertEquals(7, decsAndStatements.size());
		ASTNode var5 = decsAndStatements.get(0);
		assertThat("", var5, instanceOf(VarDeclaration.class));
		NameDef var6 = ((VarDeclaration) var5).getNameDef();
		assertThat("", var6, instanceOf(NameDef.class));
		assertEquals(Type.INT, ((NameDef) var6).getType());
		assertEquals("a0", ((NameDef) var6).getName());
		Expr var7 = ((VarDeclaration) var5).getExpr();
		assertThat("", var7, instanceOf(IntLitExpr.class));
		assertEquals(0, ((IntLitExpr) var7).getValue());
		assertEquals(ASSIGN, ((VarDeclaration) var5).getOp().getKind());
		ASTNode var8 = decsAndStatements.get(1);
		assertThat("", var8, instanceOf(VarDeclaration.class));
		NameDef var9 = ((VarDeclaration) var8).getNameDef();
		assertThat("", var9, instanceOf(NameDef.class));
		assertEquals(Type.FLOAT, ((NameDef) var9).getType());
		assertEquals("b0", ((NameDef) var9).getName());
		Expr var10 = ((VarDeclaration) var8).getExpr();
		assertThat("", var10, instanceOf(FloatLitExpr.class));
		assertEquals(10.01f, ((FloatLitExpr) var10).getValue());
		assertEquals(ASSIGN, ((VarDeclaration) var8).getOp().getKind());
		ASTNode var11 = decsAndStatements.get(2);
		assertThat("", var11, instanceOf(VarDeclaration.class));
		NameDef var12 = ((VarDeclaration) var11).getNameDef();
		assertThat("", var12, instanceOf(NameDef.class));
		assertEquals(Type.BOOLEAN, ((NameDef) var12).getType());
		assertEquals("c0", ((NameDef) var12).getName());
		Expr var13 = ((VarDeclaration) var11).getExpr();
		assertThat("", var13, instanceOf(BooleanLitExpr.class));
		assertTrue(((BooleanLitExpr) var13).getValue());
		assertEquals(ASSIGN, ((VarDeclaration) var11).getOp().getKind());
		ASTNode var14 = decsAndStatements.get(3);
		assertThat("", var14, instanceOf(VarDeclaration.class));
		NameDef var15 = ((VarDeclaration) var14).getNameDef();
		assertThat("", var15, instanceOf(NameDef.class));
		assertEquals(Type.BOOLEAN, ((NameDef) var15).getType());
		assertEquals("c1", ((NameDef) var15).getName());
		Expr var16 = ((VarDeclaration) var14).getExpr();
		assertThat("", var16, instanceOf(BooleanLitExpr.class));
		assertFalse(((BooleanLitExpr) var16).getValue());
		assertEquals(ASSIGN, ((VarDeclaration) var14).getOp().getKind());
		ASTNode var17 = decsAndStatements.get(4);
		assertThat("", var17, instanceOf(VarDeclaration.class));
		NameDef var18 = ((VarDeclaration) var17).getNameDef();
		assertThat("", var18, instanceOf(NameDef.class));
		assertEquals(Type.STRING, ((NameDef) var18).getType());
		assertEquals("s0", ((NameDef) var18).getName());
		Expr var19 = ((VarDeclaration) var17).getExpr();
		assertThat("", var19, instanceOf(StringLitExpr.class));
		assertEquals("hello", ((StringLitExpr) var19).getValue());
		assertEquals(ASSIGN, ((VarDeclaration) var17).getOp().getKind());
		ASTNode var20 = decsAndStatements.get(5);
		assertThat("", var20, instanceOf(VarDeclaration.class));
		NameDef var21 = ((VarDeclaration) var20).getNameDef();
		assertThat("", var21, instanceOf(NameDef.class));
		assertEquals(Type.COLOR, ((NameDef) var21).getType());
		assertEquals("color0", ((NameDef) var21).getName());
		Expr var22 = ((VarDeclaration) var20).getExpr();
		assertThat("", var22, instanceOf(ColorExpr.class));
		Expr var23 = ((ColorExpr) var22).getRed();
		assertThat("", var23, instanceOf(IntLitExpr.class));
		assertEquals(0, ((IntLitExpr) var23).getValue());
		Expr var24 = ((ColorExpr) var22).getGreen();
		assertThat("", var24, instanceOf(IntLitExpr.class));
		assertEquals(0, ((IntLitExpr) var24).getValue());
		Expr var25 = ((ColorExpr) var22).getBlue();
		assertThat("", var25, instanceOf(IntLitExpr.class));
		assertEquals(0, ((IntLitExpr) var25).getValue());
		assertEquals(ASSIGN, ((VarDeclaration) var20).getOp().getKind());
		ASTNode var26 = decsAndStatements.get(6);
		assertThat("", var26, instanceOf(VarDeclaration.class));
		NameDef var27 = ((VarDeclaration) var26).getNameDef();
		assertThat("", var27, instanceOf(NameDef.class));
		assertEquals(Type.COLOR, ((NameDef) var27).getType());
		assertEquals("color1", ((NameDef) var27).getName());
		Expr var28 = ((VarDeclaration) var26).getExpr();
		assertThat("", var28, instanceOf(ColorConstExpr.class));
		assertEquals("BLACK", var28.getText());
		assertEquals(ASSIGN, ((VarDeclaration) var26).getOp().getKind());
	}

	@DisplayName("test9")
	@Test
	public void test9(TestInfo testInfo) throws Exception {
		String input = """
				     #read statements
				void a123()
					int a1 <- console;
					float b1 <- console;
					string s1 <- console;
					image[100,200] m1 <- "this is a url";


				""";
		show("-------------");
		show(input);
		ASTNode ast = getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(Program.class));
		assertEquals(Type.VOID, ((Program) ast).getReturnType());
		List<NameDef> params = ((Program) ast).getParams();
		assertEquals(0, params.size());
		List<ASTNode> decsAndStatements = ((Program) ast).getDecsAndStatements();
		assertEquals(4, decsAndStatements.size());
		ASTNode var0 = decsAndStatements.get(0);
		assertThat("", var0, instanceOf(VarDeclaration.class));
		NameDef var1 = ((VarDeclaration) var0).getNameDef();
		assertThat("", var1, instanceOf(NameDef.class));
		assertEquals(Type.INT, ((NameDef) var1).getType());
		assertEquals("a1", ((NameDef) var1).getName());
		Expr var2 = ((VarDeclaration) var0).getExpr();
		assertThat("", var2, instanceOf(ConsoleExpr.class));
		assertEquals(LARROW, ((VarDeclaration) var0).getOp().getKind());
		ASTNode var3 = decsAndStatements.get(1);
		assertThat("", var3, instanceOf(VarDeclaration.class));
		NameDef var4 = ((VarDeclaration) var3).getNameDef();
		assertThat("", var4, instanceOf(NameDef.class));
		assertEquals(Type.FLOAT, ((NameDef) var4).getType());
		assertEquals("b1", ((NameDef) var4).getName());
		Expr var5 = ((VarDeclaration) var3).getExpr();
		assertThat("", var5, instanceOf(ConsoleExpr.class));
		assertEquals(LARROW, ((VarDeclaration) var3).getOp().getKind());
		ASTNode var6 = decsAndStatements.get(2);
		assertThat("", var6, instanceOf(VarDeclaration.class));
		NameDef var7 = ((VarDeclaration) var6).getNameDef();
		assertThat("", var7, instanceOf(NameDef.class));
		assertEquals(Type.STRING, ((NameDef) var7).getType());
		assertEquals("s1", ((NameDef) var7).getName());
		Expr var8 = ((VarDeclaration) var6).getExpr();
		assertThat("", var8, instanceOf(ConsoleExpr.class));
		assertEquals(LARROW, ((VarDeclaration) var6).getOp().getKind());
		ASTNode var9 = decsAndStatements.get(3);
		assertThat("", var9, instanceOf(VarDeclaration.class));
		NameDef var10 = ((VarDeclaration) var9).getNameDef();
		assertThat("", var10, instanceOf(NameDef.class));
		assertEquals(Type.IMAGE, ((NameDef) var10).getType());
		assertEquals("m1", ((NameDef) var10).getName());
		Dimension var11 = ((NameDefWithDim) var10).getDim();
		assertThat("", var11, instanceOf(Dimension.class));
		Expr var12 = ((Dimension) var11).getWidth();
		assertThat("", var12, instanceOf(IntLitExpr.class));
		assertEquals(100, ((IntLitExpr) var12).getValue());
		Expr var13 = ((Dimension) var11).getHeight();
		assertThat("", var13, instanceOf(IntLitExpr.class));
		assertEquals(200, ((IntLitExpr) var13).getValue());
		Expr var14 = ((VarDeclaration) var9).getExpr();
		assertThat("", var14, instanceOf(StringLitExpr.class));
		assertEquals("this is a url", ((StringLitExpr) var14).getValue());
		assertEquals(LARROW, ((VarDeclaration) var9).getOp().getKind());
	}

	@DisplayName("test10")
	@Test
	public void test10(TestInfo testInfo) throws Exception {
		String input = """
				int progWithReturnStatement0() ^ 0;

				""";
		show("-------------");
		show(input);
		ASTNode ast = getAST(input);
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
	}

	@DisplayName("test11")
	@Test
	public void test11(TestInfo testInfo) throws Exception {
		String input = """
				boolean progWithReturnStatement1() ^ true;

				""";
		show("-------------");
		show(input);
		ASTNode ast = getAST(input);
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
	}

	@DisplayName("test12")
	@Test
	public void test12(TestInfo testInfo) throws Exception {
		String input = """
				int f(int a, int b)
				int c = a+b;
				   ^ c - a;


				""";
		show("-------------");
		show(input);
		ASTNode ast = getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(Program.class));
		assertEquals(Type.INT, ((Program) ast).getReturnType());
		List<NameDef> params = ((Program) ast).getParams();
		assertEquals(2, params.size());
		NameDef var0 = params.get(0);
		assertThat("", var0, instanceOf(NameDef.class));
		assertEquals(Type.INT, ((NameDef) var0).getType());
		assertEquals("a", ((NameDef) var0).getName());
		NameDef var1 = params.get(1);
		assertThat("", var1, instanceOf(NameDef.class));
		assertEquals(Type.INT, ((NameDef) var1).getType());
		assertEquals("b", ((NameDef) var1).getName());
		List<ASTNode> decsAndStatements = ((Program) ast).getDecsAndStatements();
		assertEquals(2, decsAndStatements.size());
		ASTNode var2 = decsAndStatements.get(0);
		assertThat("", var2, instanceOf(VarDeclaration.class));
		NameDef var3 = ((VarDeclaration) var2).getNameDef();
		assertThat("", var3, instanceOf(NameDef.class));
		assertEquals(Type.INT, ((NameDef) var3).getType());
		assertEquals("c", ((NameDef) var3).getName());
		Expr var4 = ((VarDeclaration) var2).getExpr();
		assertThat("", var4, instanceOf(BinaryExpr.class));
		assertEquals(PLUS, ((BinaryExpr) var4).getOp().getKind());
		Expr var5 = ((BinaryExpr) var4).getLeft();
		assertThat("", var5, instanceOf(IdentExpr.class));
		assertEquals("a", var5.getText());
		Expr var6 = ((BinaryExpr) var4).getRight();
		assertThat("", var6, instanceOf(IdentExpr.class));
		assertEquals("b", var6.getText());
		assertEquals(ASSIGN, ((VarDeclaration) var2).getOp().getKind());
		ASTNode var7 = decsAndStatements.get(1);
		assertThat("", var7, instanceOf(ReturnStatement.class));
		Expr var8 = ((ReturnStatement) var7).getExpr();
		assertThat("", var8, instanceOf(BinaryExpr.class));
		assertEquals(MINUS, ((BinaryExpr) var8).getOp().getKind());
		Expr var9 = ((BinaryExpr) var8).getLeft();
		assertThat("", var9, instanceOf(IdentExpr.class));
		assertEquals("c", var9.getText());
		Expr var10 = ((BinaryExpr) var8).getRight();
		assertThat("", var10, instanceOf(IdentExpr.class));
		assertEquals("a", var10.getText());
	}

	@DisplayName("test13")
	@Test
	public void test13(TestInfo testInfo) throws Exception {
		String input = """
				string f(boolean c)
				^ if (c) "c == true" else "c == false" fi;

				""";
		show("-------------");
		show(input);
		ASTNode ast = getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(Program.class));
		assertEquals(Type.STRING, ((Program) ast).getReturnType());
		List<NameDef> params = ((Program) ast).getParams();
		assertEquals(1, params.size());
		NameDef var0 = params.get(0);
		assertThat("", var0, instanceOf(NameDef.class));
		assertEquals(Type.BOOLEAN, ((NameDef) var0).getType());
		assertEquals("c", ((NameDef) var0).getName());
		List<ASTNode> decsAndStatements = ((Program) ast).getDecsAndStatements();
		assertEquals(1, decsAndStatements.size());
		ASTNode var1 = decsAndStatements.get(0);
		assertThat("", var1, instanceOf(ReturnStatement.class));
		Expr var2 = ((ReturnStatement) var1).getExpr();
		assertThat("", var2, instanceOf(ConditionalExpr.class));
		Expr var3 = ((ConditionalExpr) var2).getCondition();
		assertThat("", var3, instanceOf(IdentExpr.class));
		assertEquals("c", var3.getText());
		Expr var4 = ((ConditionalExpr) var2).getTrueCase();
		assertThat("", var4, instanceOf(StringLitExpr.class));
		assertEquals("c == true", ((StringLitExpr) var4).getValue());
		Expr var5 = ((ConditionalExpr) var2).getFalseCase();
		assertThat("", var5, instanceOf(StringLitExpr.class));
		assertEquals("c == false", ((StringLitExpr) var5).getValue());
	}

	@DisplayName("test14")
	@Test
	public void test14(TestInfo testInfo) throws Exception {
		String input = """
				void f()
				int a;
				a <- console;
				write a+1 -> console;

				""";
		show("-------------");
		show(input);
		ASTNode ast = getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(Program.class));
		assertEquals(Type.VOID, ((Program) ast).getReturnType());
		List<NameDef> params = ((Program) ast).getParams();
		assertEquals(0, params.size());
		List<ASTNode> decsAndStatements = ((Program) ast).getDecsAndStatements();
		assertEquals(3, decsAndStatements.size());
		ASTNode var0 = decsAndStatements.get(0);
		assertThat("", var0, instanceOf(VarDeclaration.class));
		NameDef var1 = ((VarDeclaration) var0).getNameDef();
		assertThat("", var1, instanceOf(NameDef.class));
		assertEquals(Type.INT, ((NameDef) var1).getType());
		assertEquals("a", ((NameDef) var1).getName());
		ASTNode var2 = decsAndStatements.get(1);
		assertThat("", var2, instanceOf(ReadStatement.class));
		assertEquals("a", ((ReadStatement) var2).getName());
		assertNull(((ReadStatement) var2).getSelector());
		Expr var3 = ((ReadStatement) var2).getSource();
		assertThat("", var3, instanceOf(ConsoleExpr.class));
		ASTNode var4 = decsAndStatements.get(2);
		assertThat("", var4, instanceOf(WriteStatement.class));
		Expr var5 = ((WriteStatement) var4).getSource();
		assertThat("", var5, instanceOf(BinaryExpr.class));
		assertEquals(PLUS, ((BinaryExpr) var5).getOp().getKind());
		Expr var6 = ((BinaryExpr) var5).getLeft();
		assertThat("", var6, instanceOf(IdentExpr.class));
		assertEquals("a", var6.getText());
		Expr var7 = ((BinaryExpr) var5).getRight();
		assertThat("", var7, instanceOf(IntLitExpr.class));
		assertEquals(1, ((IntLitExpr) var7).getValue());
		Expr var8 = ((WriteStatement) var4).getDest();
		assertThat("", var8, instanceOf(ConsoleExpr.class));
	}

	@DisplayName("test15")
	@Test
	public void test15(TestInfo testInfo) throws Exception {
		String input = """
				void f()
				^ ---3;

				""";
		show("-------------");
		show(input);
		ASTNode ast = getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(Program.class));
		assertEquals(Type.VOID, ((Program) ast).getReturnType());
		List<NameDef> params = ((Program) ast).getParams();
		assertEquals(0, params.size());
		List<ASTNode> decsAndStatements = ((Program) ast).getDecsAndStatements();
		assertEquals(1, decsAndStatements.size());
		ASTNode var0 = decsAndStatements.get(0);
		assertThat("", var0, instanceOf(ReturnStatement.class));
		Expr var1 = ((ReturnStatement) var0).getExpr();
		assertThat("", var1, instanceOf(UnaryExpr.class));
		assertEquals(MINUS, ((UnaryExpr) var1).getOp().getKind());
		Expr var2 = ((UnaryExpr) var1).getExpr();
		assertThat("", var2, instanceOf(UnaryExpr.class));
		assertEquals(MINUS, ((UnaryExpr) var2).getOp().getKind());
		Expr var3 = ((UnaryExpr) var2).getExpr();
		assertThat("", var3, instanceOf(UnaryExpr.class));
		assertEquals(MINUS, ((UnaryExpr) var3).getOp().getKind());
		Expr var4 = ((UnaryExpr) var3).getExpr();
		assertThat("", var4, instanceOf(IntLitExpr.class));
		assertEquals(3, ((IntLitExpr) var4).getValue());
	}

	@DisplayName("test16")
	@Test
	public void test16(TestInfo testInfo) throws Exception {
		String input = """
				float a()
				float x;
				x = 3.33 * 5.55;
				write x -> console;
				^ x + 1;

				""";
		show("-------------");
		show(input);
		ASTNode ast = getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(Program.class));
		assertEquals(Type.FLOAT, ((Program) ast).getReturnType());
		List<NameDef> params = ((Program) ast).getParams();
		assertEquals(0, params.size());
		List<ASTNode> decsAndStatements = ((Program) ast).getDecsAndStatements();
		assertEquals(4, decsAndStatements.size());
		ASTNode var0 = decsAndStatements.get(0);
		assertThat("", var0, instanceOf(VarDeclaration.class));
		NameDef var1 = ((VarDeclaration) var0).getNameDef();
		assertThat("", var1, instanceOf(NameDef.class));
		assertEquals(Type.FLOAT, ((NameDef) var1).getType());
		assertEquals("x", ((NameDef) var1).getName());
		ASTNode var2 = decsAndStatements.get(1);
		assertThat("", var2, instanceOf(AssignmentStatement.class));
		assertEquals("x", ((AssignmentStatement) var2).getName());
		assertNull(((AssignmentStatement) var2).getSelector());
		Expr var3 = ((AssignmentStatement) var2).getExpr();
		assertThat("", var3, instanceOf(BinaryExpr.class));
		assertEquals(TIMES, ((BinaryExpr) var3).getOp().getKind());
		Expr var4 = ((BinaryExpr) var3).getLeft();
		assertThat("", var4, instanceOf(FloatLitExpr.class));
		assertEquals(3.33f, ((FloatLitExpr) var4).getValue());
		Expr var5 = ((BinaryExpr) var3).getRight();
		assertThat("", var5, instanceOf(FloatLitExpr.class));
		assertEquals(5.55f, ((FloatLitExpr) var5).getValue());
		ASTNode var6 = decsAndStatements.get(2);
		assertThat("", var6, instanceOf(WriteStatement.class));
		Expr var7 = ((WriteStatement) var6).getSource();
		assertThat("", var7, instanceOf(IdentExpr.class));
		assertEquals("x", var7.getText());
		Expr var8 = ((WriteStatement) var6).getDest();
		assertThat("", var8, instanceOf(ConsoleExpr.class));
		ASTNode var9 = decsAndStatements.get(3);
		assertThat("", var9, instanceOf(ReturnStatement.class));
		Expr var10 = ((ReturnStatement) var9).getExpr();
		assertThat("", var10, instanceOf(BinaryExpr.class));
		assertEquals(PLUS, ((BinaryExpr) var10).getOp().getKind());
		Expr var11 = ((BinaryExpr) var10).getLeft();
		assertThat("", var11, instanceOf(IdentExpr.class));
		assertEquals("x", var11.getText());
		Expr var12 = ((BinaryExpr) var10).getRight();
		assertThat("", var12, instanceOf(IntLitExpr.class));
		assertEquals(1, ((IntLitExpr) var12).getValue());
	}

	@DisplayName("test17")
	@Test
	public void test17(TestInfo testInfo) throws Exception {
		String input = """
				#this has a syntax error
				int f()
				^ 42

				""";
		show("-------------");
		show(input);
		Exception e = assertThrows(SyntaxException.class, () -> {
			@SuppressWarnings("unused")
			ASTNode ast = getAST(input);
		});
		show("Expected SyntaxException:     " + e);
	}

	@DisplayName("test18")
	@Test
	public void test18(TestInfo testInfo) throws Exception {
		String input = """
				#this has a lexical error
				int f()
				^ @

				""";
		show("-------------");
		show(input);
		Exception e = assertThrows(LexicalException.class, () -> {
			@SuppressWarnings("unused")
			ASTNode ast = getAST(input);
		});
		show("Expected LexicalException:     " + e);
	}

}
