package edu.ufl.cise.plc;

import static edu.ufl.cise.plc.IToken.Kind.AND;
import static edu.ufl.cise.plc.IToken.Kind.BANG;
import static edu.ufl.cise.plc.IToken.Kind.COLOR_OP;
import static edu.ufl.cise.plc.IToken.Kind.MINUS;
import static edu.ufl.cise.plc.IToken.Kind.PLUS;
import static edu.ufl.cise.plc.IToken.Kind.TIMES;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import edu.ufl.cise.plc.CompilerComponentFactory;
import edu.ufl.cise.plc.IParser;
import edu.ufl.cise.plc.LexicalException;
import edu.ufl.cise.plc.SyntaxException;
import edu.ufl.cise.plc.ast.ASTNode;
import edu.ufl.cise.plc.ast.BinaryExpr;
import edu.ufl.cise.plc.ast.BooleanLitExpr;
import edu.ufl.cise.plc.ast.ConditionalExpr;
import edu.ufl.cise.plc.ast.Expr;
import edu.ufl.cise.plc.ast.FloatLitExpr;
import edu.ufl.cise.plc.ast.IdentExpr;
import edu.ufl.cise.plc.ast.IntLitExpr;
import edu.ufl.cise.plc.ast.PixelSelector;
import edu.ufl.cise.plc.ast.StringLitExpr;
import edu.ufl.cise.plc.ast.UnaryExpr;
import edu.ufl.cise.plc.ast.UnaryExprPostfix;

class Assignment2StarterTests {

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
				true
				""";
		show("-------------");
		show(input);
		Expr ast = (Expr) getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(BooleanLitExpr.class));
		assertTrue(((BooleanLitExpr) ast).getValue());
	}

	@DisplayName("test1")
	@Test
	public void test1(TestInfo testInfo) throws Exception {
		String input = """
				"this is a string"
				""";
		show("-------------");
		show(input);
		Expr ast = (Expr) getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(StringLitExpr.class));
		assertEquals("this is a string", ((StringLitExpr) ast).getValue());
	}

	@DisplayName("test2")
	@Test
	public void test2(TestInfo testInfo) throws Exception {
		String input = """
				12.4
				""";
		show("-------------");
		show(input);
		Expr ast = (Expr) getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(FloatLitExpr.class));
		assertEquals(12.4f, ((FloatLitExpr) ast).getValue());
	}

	@DisplayName("test3")
	@Test
	public void test3(TestInfo testInfo) throws Exception {
		String input = """
				var
				""";
		show("-------------");
		show(input);
		Expr ast = (Expr) getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(IdentExpr.class));
		assertEquals("var", ast.getText());
	}

	@DisplayName("test4")
	@Test
	public void test4(TestInfo testInfo) throws Exception {
		String input = """
				!var
				""";
		show("-------------");
		show(input);
		Expr ast = (Expr) getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(UnaryExpr.class));
		assertEquals(BANG, ((UnaryExpr) ast).getOp().getKind());
		Expr var0 = ((UnaryExpr) ast).getExpr();
		assertThat("", var0, instanceOf(IdentExpr.class));
		assertEquals("var", var0.getText());
	}

	@DisplayName("test5")
	@Test
	public void test5(TestInfo testInfo) throws Exception {
		String input = """
				-30
				""";
		show("-------------");
		show(input);
		Expr ast = (Expr) getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(UnaryExpr.class));
		assertEquals(MINUS, ((UnaryExpr) ast).getOp().getKind());
		Expr var1 = ((UnaryExpr) ast).getExpr();
		assertThat("", var1, instanceOf(IntLitExpr.class));
		assertEquals(30, ((IntLitExpr) var1).getValue());
	}

	@DisplayName("test6")
	@Test
	public void test6(TestInfo testInfo) throws Exception {
		String input = """
				a + true
				""";
		show("-------------");
		show(input);
		Expr ast = (Expr) getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(BinaryExpr.class));
		assertEquals(PLUS, ((BinaryExpr) ast).getOp().getKind());
		Expr var2 = ((BinaryExpr) ast).getLeft();
		assertThat("", var2, instanceOf(IdentExpr.class));
		assertEquals("a", var2.getText());
		Expr var3 = ((BinaryExpr) ast).getRight();
		assertThat("", var3, instanceOf(BooleanLitExpr.class));
		assertTrue(((BooleanLitExpr) var3).getValue());
	}

	@DisplayName("test7")
	@Test
	public void test7(TestInfo testInfo) throws Exception {
		String input = """
				b[a,200]
				""";
		show("-------------");
		show(input);
		Expr ast = (Expr) getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(UnaryExprPostfix.class));
		Expr var4 = ((UnaryExprPostfix) ast).getExpr();
		assertThat("", var4, instanceOf(IdentExpr.class));
		assertEquals("b", var4.getText());
		PixelSelector var5 = ((UnaryExprPostfix) ast).getSelector();
		assertThat("", var5, instanceOf(PixelSelector.class));
		Expr var6 = ((PixelSelector) var5).getX();
		assertThat("", var6, instanceOf(IdentExpr.class));
		assertEquals("a", var6.getText());
		Expr var7 = ((PixelSelector) var5).getY();
		assertThat("", var7, instanceOf(IntLitExpr.class));
		assertEquals(200, ((IntLitExpr) var7).getValue());
	}

	@DisplayName("test8")
	@Test
	public void test8(TestInfo testInfo) throws Exception {
		String input = """
				a[x,y]*z
				""";
		show("-------------");
		show(input);
		Expr ast = (Expr) getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(BinaryExpr.class));
		assertEquals(TIMES, ((BinaryExpr) ast).getOp().getKind());
		Expr var8 = ((BinaryExpr) ast).getLeft();
		assertThat("", var8, instanceOf(UnaryExprPostfix.class));
		Expr var9 = ((UnaryExprPostfix) var8).getExpr();
		assertThat("", var9, instanceOf(IdentExpr.class));
		assertEquals("a", var9.getText());
		PixelSelector var10 = ((UnaryExprPostfix) var8).getSelector();
		assertThat("", var10, instanceOf(PixelSelector.class));
		Expr var11 = ((PixelSelector) var10).getX();
		assertThat("", var11, instanceOf(IdentExpr.class));
		assertEquals("x", var11.getText());
		Expr var12 = ((PixelSelector) var10).getY();
		assertThat("", var12, instanceOf(IdentExpr.class));
		assertEquals("y", var12.getText());
		Expr var13 = ((BinaryExpr) ast).getRight();
		assertThat("", var13, instanceOf(IdentExpr.class));
		assertEquals("z", var13.getText());
	}

	@DisplayName("test9")
	@Test
	public void test9(TestInfo testInfo) throws Exception {
		String input = """
				if (a) b else c fi
				""";
		show("-------------");
		show(input);
		Expr ast = (Expr) getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(ConditionalExpr.class));
		Expr var14 = ((ConditionalExpr) ast).getCondition();
		assertThat("", var14, instanceOf(IdentExpr.class));
		assertEquals("a", var14.getText());
		Expr var15 = ((ConditionalExpr) ast).getTrueCase();
		assertThat("", var15, instanceOf(IdentExpr.class));
		assertEquals("b", var15.getText());
		Expr var16 = ((ConditionalExpr) ast).getFalseCase();
		assertThat("", var16, instanceOf(IdentExpr.class));
		assertEquals("c", var16.getText());
	}

	@DisplayName("test10")
	@Test
	public void test10(TestInfo testInfo) throws Exception {
		String input = """
				if (a & b) if (x) y else z fi else c fi
				""";
		show("-------------");
		show(input);
		Expr ast = (Expr) getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(ConditionalExpr.class));
		Expr var17 = ((ConditionalExpr) ast).getCondition();
		assertThat("", var17, instanceOf(BinaryExpr.class));
		assertEquals(AND, ((BinaryExpr) var17).getOp().getKind());
		Expr var18 = ((BinaryExpr) var17).getLeft();
		assertThat("", var18, instanceOf(IdentExpr.class));
		assertEquals("a", var18.getText());
		Expr var19 = ((BinaryExpr) var17).getRight();
		assertThat("", var19, instanceOf(IdentExpr.class));
		assertEquals("b", var19.getText());
		Expr var20 = ((ConditionalExpr) ast).getTrueCase();
		assertThat("", var20, instanceOf(ConditionalExpr.class));
		Expr var21 = ((ConditionalExpr) var20).getCondition();
		assertThat("", var21, instanceOf(IdentExpr.class));
		assertEquals("x", var21.getText());
		Expr var22 = ((ConditionalExpr) var20).getTrueCase();
		assertThat("", var22, instanceOf(IdentExpr.class));
		assertEquals("y", var22.getText());
		Expr var23 = ((ConditionalExpr) var20).getFalseCase();
		assertThat("", var23, instanceOf(IdentExpr.class));
		assertEquals("z", var23.getText());
		Expr var24 = ((ConditionalExpr) ast).getFalseCase();
		assertThat("", var24, instanceOf(IdentExpr.class));
		assertEquals("c", var24.getText());
	}

	@DisplayName("test11")
	@Test
	public void test11(TestInfo testInfo) throws Exception {
		String input = """
				getRed x
				""";
		show("-------------");
		show(input);
		Expr ast = (Expr) getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(UnaryExpr.class));
		assertEquals(COLOR_OP, ((UnaryExpr) ast).getOp().getKind());
		Expr var25 = ((UnaryExpr) ast).getExpr();
		assertThat("", var25, instanceOf(IdentExpr.class));
		assertEquals("x", var25.getText());
	}

	@DisplayName("test12")
	@Test
	public void test12(TestInfo testInfo) throws Exception {
		String input = """
				getGreen getRed x
				""";
		show("-------------");
		show(input);
		Expr ast = (Expr) getAST(input);
		show(ast);
		assertThat("", ast, instanceOf(UnaryExpr.class));
		assertEquals(COLOR_OP, ((UnaryExpr) ast).getOp().getKind());
		Expr var26 = ((UnaryExpr) ast).getExpr();
		assertThat("", var26, instanceOf(UnaryExpr.class));
		assertEquals(COLOR_OP, ((UnaryExpr) var26).getOp().getKind());
		Expr var27 = ((UnaryExpr) var26).getExpr();
		assertThat("", var27, instanceOf(IdentExpr.class));
		assertEquals("x", var27.getText());
	}

	@DisplayName("test13")
	@Test
	public void test13(TestInfo testInfo) throws Exception {
		String input = """
						x + if
				""";
		show("-------------");
		show(input);
		Exception e = assertThrows(SyntaxException.class, () -> {
			getAST(input);
		});
		show(e);
	}

	@DisplayName("test14")
	@Test
	public void test14(TestInfo testInfo) throws Exception {
		String input = """
				x + @
				""";
		show("-------------");
		show(input);
		Exception e = assertThrows(LexicalException.class, () -> {
			getAST(input);
		});
		show(e);
	}
}
