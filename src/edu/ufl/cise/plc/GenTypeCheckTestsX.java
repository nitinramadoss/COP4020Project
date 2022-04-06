package edu.ufl.cise.plc;

import static edu.ufl.cise.plc.IToken.Kind.MINUS;
import static edu.ufl.cise.plc.IToken.Kind.BANG;
import static org.hamcrest.CoreMatchers.*;
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
import edu.ufl.cise.plc.ast.ColorConstExpr;
import edu.ufl.cise.plc.ast.ColorExpr;
import edu.ufl.cise.plc.ast.ConditionalExpr;
import edu.ufl.cise.plc.ast.ConsoleExpr;
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
import edu.ufl.cise.plc.TypeCheckVisitor;
import edu.ufl.cise.plc.TypeCheckException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import java.time.Duration;
import java.io.PrintStream;
import java.util.List;

class GenTypeCheckTestsX{
private ASTNode getAST(String input) throws Exception {
	IParser parser = CompilerComponentFactory.getParser(input);
	return parser.parse();
}


//makes it easy to turn output on and off (and less typing than System.out.println)
static final boolean VERBOSE = true;
void show(Object obj) {
	if(VERBOSE) {
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
@DisplayName("test000")
@Test public void test000(TestInfo testInfo) throws Exception{
String input = """
string Strings(string a, string b)
int a;
float b;
image c;
string e;
boolean f;

""";
show("\n\n----- test000 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
Exception e = assertThrows(TypeCheckException.class, () -> {
checkTypes(ast);
});
show("Expected TypeCheckException:     " + e);
});
}

@DisplayName("test001")
@Test public void test001(TestInfo testInfo) throws Exception{
String input = """
string f(boolean c)
^ if (c) "c == true" else "c == false" fi;

""";
show("\n\n----- test001 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.STRING, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(1, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.BOOLEAN, ((NameDef) var0).getType());
assertEquals("c", ((NameDef) var0).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var1= decsAndStatements.get(0);
assertThat("",var1,instanceOf(ReturnStatement.class));
Expr var2= ((ReturnStatement) var1).getExpr();
assertThat("",var2,instanceOf(ConditionalExpr.class));
Expr var3= ((ConditionalExpr) var2).getCondition();
assertThat("",var3,instanceOf(IdentExpr.class));
assertEquals("c", var3.getText());
assertEquals(Type.BOOLEAN,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
Expr var4= ((ConditionalExpr) var2).getTrueCase();
assertThat("",var4,instanceOf(StringLitExpr.class));
assertEquals("c == true", ((StringLitExpr) var4).getValue());
assertEquals(Type.STRING,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((ConditionalExpr) var2).getFalseCase();
assertThat("",var5,instanceOf(StringLitExpr.class));
assertEquals("c == false", ((StringLitExpr) var5).getValue());
assertEquals(Type.STRING,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(Type.STRING,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
});
}

@DisplayName("test002")
@Test public void test002(TestInfo testInfo) throws Exception{
String input = """
void withInitializedDecs()
string e = "hello";

""";
show("\n\n----- test002 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.VOID, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.STRING, ((NameDef) var1).getType());
assertEquals("e", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(StringLitExpr.class));
assertEquals("hello", ((StringLitExpr) var2).getValue());
assertEquals(Type.STRING,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
});
}

@DisplayName("test003")
@Test public void test003(TestInfo testInfo) throws Exception{
String input = """
void withInitializedDecs()
boolean c = true;

""";
show("\n\n----- test003 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.VOID, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.BOOLEAN, ((NameDef) var1).getType());
assertEquals("c", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(BooleanLitExpr.class));
assertTrue(((BooleanLitExpr) var2).getValue());
assertEquals(Type.BOOLEAN,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
});
}

@DisplayName("test004")
@Test public void test004(TestInfo testInfo) throws Exception{
String input = """
image f(int widthAndHeight)
image[widthAndHeight,widthAndHeight] a;
image[widthAndHeight,widthAndHeight] b;
a[x,y] = <<y, y, 0>>;
b[x,y] = << getRed a[y,x], getGreen a[y,x], getBlue a[y,x] >>;
^ b;

""";
show("\n\n----- test004 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.IMAGE, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(1, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var0).getType());
assertEquals("widthAndHeight", ((NameDef) var0).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(5, decsAndStatements.size());
ASTNode var1= decsAndStatements.get(0);
assertThat("",var1,instanceOf(VarDeclaration.class));
NameDef var2= ((VarDeclaration) var1).getNameDef();
assertThat("",var2,instanceOf(NameDef.class));
assertEquals(Type.IMAGE, ((NameDef) var2).getType());
assertEquals("a", ((NameDef) var2).getName());
Dimension var3= ((NameDefWithDim) var2).getDim();
assertThat("",var3,instanceOf(Dimension.class));
Expr var4= ((Dimension) var3).getWidth();
assertThat("",var4,instanceOf(IdentExpr.class));
assertEquals("widthAndHeight", var4.getText());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((Dimension) var3).getHeight();
assertThat("",var5,instanceOf(IdentExpr.class));
assertEquals("widthAndHeight", var5.getText());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
ASTNode var6= decsAndStatements.get(1);
assertThat("",var6,instanceOf(VarDeclaration.class));
NameDef var7= ((VarDeclaration) var6).getNameDef();
assertThat("",var7,instanceOf(NameDef.class));
assertEquals(Type.IMAGE, ((NameDef) var7).getType());
assertEquals("b", ((NameDef) var7).getName());
Dimension var8= ((NameDefWithDim) var7).getDim();
assertThat("",var8,instanceOf(Dimension.class));
Expr var9= ((Dimension) var8).getWidth();
assertThat("",var9,instanceOf(IdentExpr.class));
assertEquals("widthAndHeight", var9.getText());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
Expr var10= ((Dimension) var8).getHeight();
assertThat("",var10,instanceOf(IdentExpr.class));
assertEquals("widthAndHeight", var10.getText());
assertEquals(Type.INT,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
ASTNode var11= decsAndStatements.get(2);
assertThat("",var11,instanceOf(AssignmentStatement.class));
assertEquals("a", ((AssignmentStatement)var11).getName());
PixelSelector var12= ((AssignmentStatement) var11).getSelector();
assertThat("",var12,instanceOf(PixelSelector.class));
Expr var13= ((PixelSelector) var12).getX();
assertThat("",var13,instanceOf(IdentExpr.class));
assertEquals("x", var13.getText());
assertEquals(Type.INT,var13.getType());
assertThat(var13.getCoerceTo(),anyOf(nullValue(), is(var13.getType())));
Expr var14= ((PixelSelector) var12).getY();
assertThat("",var14,instanceOf(IdentExpr.class));
assertEquals("y", var14.getText());
assertEquals(Type.INT,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
Expr var15= ((AssignmentStatement) var11).getExpr();
assertThat("",var15,instanceOf(ColorExpr.class));
Expr var16= ((ColorExpr) var15).getRed();
assertThat("",var16,instanceOf(IdentExpr.class));
assertEquals("y", var16.getText());
assertEquals(Type.INT,var16.getType());
assertThat(var16.getCoerceTo(),anyOf(nullValue(), is(var16.getType())));
Expr var17= ((ColorExpr) var15).getGreen();
assertThat("",var17,instanceOf(IdentExpr.class));
assertEquals("y", var17.getText());
assertEquals(Type.INT,var17.getType());
assertThat(var17.getCoerceTo(),anyOf(nullValue(), is(var17.getType())));
Expr var18= ((ColorExpr) var15).getBlue();
assertThat("",var18,instanceOf(IntLitExpr.class));
assertEquals(0, ((IntLitExpr) var18).getValue());
assertEquals(Type.INT,var18.getType());
assertThat(var18.getCoerceTo(),anyOf(nullValue(), is(var18.getType())));
assertEquals(Type.COLOR,var15.getType());
assertThat(var15.getCoerceTo(),anyOf(nullValue(), is(var15.getType())));
ASTNode var19= decsAndStatements.get(3);
assertThat("",var19,instanceOf(AssignmentStatement.class));
assertEquals("b", ((AssignmentStatement)var19).getName());
PixelSelector var20= ((AssignmentStatement) var19).getSelector();
assertThat("",var20,instanceOf(PixelSelector.class));
Expr var21= ((PixelSelector) var20).getX();
assertThat("",var21,instanceOf(IdentExpr.class));
assertEquals("x", var21.getText());
assertEquals(Type.INT,var21.getType());
assertThat(var21.getCoerceTo(),anyOf(nullValue(), is(var21.getType())));
Expr var22= ((PixelSelector) var20).getY();
assertThat("",var22,instanceOf(IdentExpr.class));
assertEquals("y", var22.getText());
assertEquals(Type.INT,var22.getType());
assertThat(var22.getCoerceTo(),anyOf(nullValue(), is(var22.getType())));
Expr var23= ((AssignmentStatement) var19).getExpr();
assertThat("",var23,instanceOf(ColorExpr.class));
Expr var24= ((ColorExpr) var23).getRed();
assertThat("",var24,instanceOf(UnaryExpr.class));
assertEquals(COLOR_OP, ((UnaryExpr) var24).getOp().getKind());
Expr var25= ((UnaryExpr) var24).getExpr();
assertThat("",var25,instanceOf(UnaryExprPostfix.class));
Expr var26= ((UnaryExprPostfix) var25).getExpr();
assertThat("",var26,instanceOf(IdentExpr.class));
assertEquals("a", var26.getText());
assertEquals(Type.IMAGE,var26.getType());
assertThat(var26.getCoerceTo(),anyOf(nullValue(), is(var26.getType())));
PixelSelector var27= ((UnaryExprPostfix) var25).getSelector();
assertThat("",var27,instanceOf(PixelSelector.class));
Expr var28= ((PixelSelector) var27).getX();
assertThat("",var28,instanceOf(IdentExpr.class));
assertEquals("y", var28.getText());
assertEquals(Type.INT,var28.getType());
assertThat(var28.getCoerceTo(),anyOf(nullValue(), is(var28.getType())));
Expr var29= ((PixelSelector) var27).getY();
assertThat("",var29,instanceOf(IdentExpr.class));
assertEquals("x", var29.getText());
assertEquals(Type.INT,var29.getType());
assertThat(var29.getCoerceTo(),anyOf(nullValue(), is(var29.getType())));
assertEquals(Type.INT,var25.getType());
assertEquals(Type.COLOR,var25.getCoerceTo());
assertEquals(Type.INT,var24.getType());
assertThat(var24.getCoerceTo(),anyOf(nullValue(), is(var24.getType())));
Expr var30= ((ColorExpr) var23).getGreen();
assertThat("",var30,instanceOf(UnaryExpr.class));
assertEquals(COLOR_OP, ((UnaryExpr) var30).getOp().getKind());
Expr var31= ((UnaryExpr) var30).getExpr();
assertThat("",var31,instanceOf(UnaryExprPostfix.class));
Expr var32= ((UnaryExprPostfix) var31).getExpr();
assertThat("",var32,instanceOf(IdentExpr.class));
assertEquals("a", var32.getText());
assertEquals(Type.IMAGE,var32.getType());
assertThat(var32.getCoerceTo(),anyOf(nullValue(), is(var32.getType())));
PixelSelector var33= ((UnaryExprPostfix) var31).getSelector();
assertThat("",var33,instanceOf(PixelSelector.class));
Expr var34= ((PixelSelector) var33).getX();
assertThat("",var34,instanceOf(IdentExpr.class));
assertEquals("y", var34.getText());
assertEquals(Type.INT,var34.getType());
assertThat(var34.getCoerceTo(),anyOf(nullValue(), is(var34.getType())));
Expr var35= ((PixelSelector) var33).getY();
assertThat("",var35,instanceOf(IdentExpr.class));
assertEquals("x", var35.getText());
assertEquals(Type.INT,var35.getType());
assertThat(var35.getCoerceTo(),anyOf(nullValue(), is(var35.getType())));
assertEquals(Type.INT,var31.getType());
assertEquals(Type.COLOR,var31.getCoerceTo());
assertEquals(Type.INT,var30.getType());
assertThat(var30.getCoerceTo(),anyOf(nullValue(), is(var30.getType())));
Expr var36= ((ColorExpr) var23).getBlue();
assertThat("",var36,instanceOf(UnaryExpr.class));
assertEquals(COLOR_OP, ((UnaryExpr) var36).getOp().getKind());
Expr var37= ((UnaryExpr) var36).getExpr();
assertThat("",var37,instanceOf(UnaryExprPostfix.class));
Expr var38= ((UnaryExprPostfix) var37).getExpr();
assertThat("",var38,instanceOf(IdentExpr.class));
assertEquals("a", var38.getText());
assertEquals(Type.IMAGE,var38.getType());
assertThat(var38.getCoerceTo(),anyOf(nullValue(), is(var38.getType())));
PixelSelector var39= ((UnaryExprPostfix) var37).getSelector();
assertThat("",var39,instanceOf(PixelSelector.class));
Expr var40= ((PixelSelector) var39).getX();
assertThat("",var40,instanceOf(IdentExpr.class));
assertEquals("y", var40.getText());
assertEquals(Type.INT,var40.getType());
assertThat(var40.getCoerceTo(),anyOf(nullValue(), is(var40.getType())));
Expr var41= ((PixelSelector) var39).getY();
assertThat("",var41,instanceOf(IdentExpr.class));
assertEquals("x", var41.getText());
assertEquals(Type.INT,var41.getType());
assertThat(var41.getCoerceTo(),anyOf(nullValue(), is(var41.getType())));
assertEquals(Type.INT,var37.getType());
assertEquals(Type.COLOR,var37.getCoerceTo());
assertEquals(Type.INT,var36.getType());
assertThat(var36.getCoerceTo(),anyOf(nullValue(), is(var36.getType())));
assertEquals(Type.COLOR,var23.getType());
assertThat(var23.getCoerceTo(),anyOf(nullValue(), is(var23.getType())));
ASTNode var42= decsAndStatements.get(4);
assertThat("",var42,instanceOf(ReturnStatement.class));
Expr var43= ((ReturnStatement) var42).getExpr();
assertThat("",var43,instanceOf(IdentExpr.class));
assertEquals("b", var43.getText());
assertEquals(Type.IMAGE,var43.getType());
assertThat(var43.getCoerceTo(),anyOf(nullValue(), is(var43.getType())));
});
}

@DisplayName("test005")
@Test public void test005(TestInfo testInfo) throws Exception{
String input = """
color f()
color a = <<50,60,70>>;
color b = <<13,14,15>>;
color c = <<2,3,4>>;
^ a+b-c;

""";
show("\n\n----- test005 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(4, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var1).getType());
assertEquals("a", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(ColorExpr.class));
Expr var3= ((ColorExpr) var2).getRed();
assertThat("",var3,instanceOf(IntLitExpr.class));
assertEquals(50, ((IntLitExpr) var3).getValue());
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
Expr var4= ((ColorExpr) var2).getGreen();
assertThat("",var4,instanceOf(IntLitExpr.class));
assertEquals(60, ((IntLitExpr) var4).getValue());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((ColorExpr) var2).getBlue();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(70, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(Type.COLOR,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var6= decsAndStatements.get(1);
assertThat("",var6,instanceOf(VarDeclaration.class));
NameDef var7= ((VarDeclaration) var6).getNameDef();
assertThat("",var7,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var7).getType());
assertEquals("b", ((NameDef) var7).getName());
Expr var8= ((VarDeclaration) var6).getExpr();
assertThat("",var8,instanceOf(ColorExpr.class));
Expr var9= ((ColorExpr) var8).getRed();
assertThat("",var9,instanceOf(IntLitExpr.class));
assertEquals(13, ((IntLitExpr) var9).getValue());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
Expr var10= ((ColorExpr) var8).getGreen();
assertThat("",var10,instanceOf(IntLitExpr.class));
assertEquals(14, ((IntLitExpr) var10).getValue());
assertEquals(Type.INT,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
Expr var11= ((ColorExpr) var8).getBlue();
assertThat("",var11,instanceOf(IntLitExpr.class));
assertEquals(15, ((IntLitExpr) var11).getValue());
assertEquals(Type.INT,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
assertEquals(Type.COLOR,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var6).getOp().getKind());
ASTNode var12= decsAndStatements.get(2);
assertThat("",var12,instanceOf(VarDeclaration.class));
NameDef var13= ((VarDeclaration) var12).getNameDef();
assertThat("",var13,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var13).getType());
assertEquals("c", ((NameDef) var13).getName());
Expr var14= ((VarDeclaration) var12).getExpr();
assertThat("",var14,instanceOf(ColorExpr.class));
Expr var15= ((ColorExpr) var14).getRed();
assertThat("",var15,instanceOf(IntLitExpr.class));
assertEquals(2, ((IntLitExpr) var15).getValue());
assertEquals(Type.INT,var15.getType());
assertThat(var15.getCoerceTo(),anyOf(nullValue(), is(var15.getType())));
Expr var16= ((ColorExpr) var14).getGreen();
assertThat("",var16,instanceOf(IntLitExpr.class));
assertEquals(3, ((IntLitExpr) var16).getValue());
assertEquals(Type.INT,var16.getType());
assertThat(var16.getCoerceTo(),anyOf(nullValue(), is(var16.getType())));
Expr var17= ((ColorExpr) var14).getBlue();
assertThat("",var17,instanceOf(IntLitExpr.class));
assertEquals(4, ((IntLitExpr) var17).getValue());
assertEquals(Type.INT,var17.getType());
assertThat(var17.getCoerceTo(),anyOf(nullValue(), is(var17.getType())));
assertEquals(Type.COLOR,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var12).getOp().getKind());
ASTNode var18= decsAndStatements.get(3);
assertThat("",var18,instanceOf(ReturnStatement.class));
Expr var19= ((ReturnStatement) var18).getExpr();
assertThat("",var19,instanceOf(BinaryExpr.class));
assertEquals(MINUS, ((BinaryExpr) var19).getOp().getKind());
Expr var20= ((BinaryExpr) var19).getLeft();
assertThat("",var20,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var20).getOp().getKind());
Expr var21= ((BinaryExpr) var20).getLeft();
assertThat("",var21,instanceOf(IdentExpr.class));
assertEquals("a", var21.getText());
assertEquals(Type.COLOR,var21.getType());
assertThat(var21.getCoerceTo(),anyOf(nullValue(), is(var21.getType())));
Expr var22= ((BinaryExpr) var20).getRight();
assertThat("",var22,instanceOf(IdentExpr.class));
assertEquals("b", var22.getText());
assertEquals(Type.COLOR,var22.getType());
assertThat(var22.getCoerceTo(),anyOf(nullValue(), is(var22.getType())));
assertEquals(Type.COLOR,var20.getType());
assertThat(var20.getCoerceTo(),anyOf(nullValue(), is(var20.getType())));
Expr var23= ((BinaryExpr) var19).getRight();
assertThat("",var23,instanceOf(IdentExpr.class));
assertEquals("c", var23.getText());
assertEquals(Type.COLOR,var23.getType());
assertThat(var23.getCoerceTo(),anyOf(nullValue(), is(var23.getType())));
assertEquals(Type.COLOR,var19.getType());
assertThat(var19.getCoerceTo(),anyOf(nullValue(), is(var19.getType())));
});
}

@DisplayName("test006")
@Test public void test006(TestInfo testInfo) throws Exception{
String input = """
image BDP0()
int Z = 255;
image[1024,1024] a;
a[x,y] = <<(Z*x*y)/((1024-1)*(1024-1)), (Z*x*y)/((1024-1)*(1024-1)),(Z*x*y)/((1024-1)*(1024-1))>>;
^ a;

""";
show("\n\n----- test006 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.IMAGE, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(4, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var1).getType());
assertEquals("Z", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(IntLitExpr.class));
assertEquals(255, ((IntLitExpr) var2).getValue());
assertEquals(Type.INT,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(VarDeclaration.class));
NameDef var4= ((VarDeclaration) var3).getNameDef();
assertThat("",var4,instanceOf(NameDef.class));
assertEquals(Type.IMAGE, ((NameDef) var4).getType());
assertEquals("a", ((NameDef) var4).getName());
Dimension var5= ((NameDefWithDim) var4).getDim();
assertThat("",var5,instanceOf(Dimension.class));
Expr var6= ((Dimension) var5).getWidth();
assertThat("",var6,instanceOf(IntLitExpr.class));
assertEquals(1024, ((IntLitExpr) var6).getValue());
assertEquals(Type.INT,var6.getType());
assertThat(var6.getCoerceTo(),anyOf(nullValue(), is(var6.getType())));
Expr var7= ((Dimension) var5).getHeight();
assertThat("",var7,instanceOf(IntLitExpr.class));
assertEquals(1024, ((IntLitExpr) var7).getValue());
assertEquals(Type.INT,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
ASTNode var8= decsAndStatements.get(2);
assertThat("",var8,instanceOf(AssignmentStatement.class));
assertEquals("a", ((AssignmentStatement)var8).getName());
PixelSelector var9= ((AssignmentStatement) var8).getSelector();
assertThat("",var9,instanceOf(PixelSelector.class));
Expr var10= ((PixelSelector) var9).getX();
assertThat("",var10,instanceOf(IdentExpr.class));
assertEquals("x", var10.getText());
assertEquals(Type.INT,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
Expr var11= ((PixelSelector) var9).getY();
assertThat("",var11,instanceOf(IdentExpr.class));
assertEquals("y", var11.getText());
assertEquals(Type.INT,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
Expr var12= ((AssignmentStatement) var8).getExpr();
assertThat("",var12,instanceOf(ColorExpr.class));
Expr var13= ((ColorExpr) var12).getRed();
assertThat("",var13,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var13).getOp().getKind());
Expr var14= ((BinaryExpr) var13).getLeft();
assertThat("",var14,instanceOf(BinaryExpr.class));
assertEquals(TIMES, ((BinaryExpr) var14).getOp().getKind());
Expr var15= ((BinaryExpr) var14).getLeft();
assertThat("",var15,instanceOf(BinaryExpr.class));
assertEquals(TIMES, ((BinaryExpr) var15).getOp().getKind());
Expr var16= ((BinaryExpr) var15).getLeft();
assertThat("",var16,instanceOf(IdentExpr.class));
assertEquals("Z", var16.getText());
assertEquals(Type.INT,var16.getType());
assertThat(var16.getCoerceTo(),anyOf(nullValue(), is(var16.getType())));
Expr var17= ((BinaryExpr) var15).getRight();
assertThat("",var17,instanceOf(IdentExpr.class));
assertEquals("x", var17.getText());
assertEquals(Type.INT,var17.getType());
assertThat(var17.getCoerceTo(),anyOf(nullValue(), is(var17.getType())));
assertEquals(Type.INT,var15.getType());
assertThat(var15.getCoerceTo(),anyOf(nullValue(), is(var15.getType())));
Expr var18= ((BinaryExpr) var14).getRight();
assertThat("",var18,instanceOf(IdentExpr.class));
assertEquals("y", var18.getText());
assertEquals(Type.INT,var18.getType());
assertThat(var18.getCoerceTo(),anyOf(nullValue(), is(var18.getType())));
assertEquals(Type.INT,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
Expr var19= ((BinaryExpr) var13).getRight();
assertThat("",var19,instanceOf(BinaryExpr.class));
assertEquals(TIMES, ((BinaryExpr) var19).getOp().getKind());
Expr var20= ((BinaryExpr) var19).getLeft();
assertThat("",var20,instanceOf(BinaryExpr.class));
assertEquals(MINUS, ((BinaryExpr) var20).getOp().getKind());
Expr var21= ((BinaryExpr) var20).getLeft();
assertThat("",var21,instanceOf(IntLitExpr.class));
assertEquals(1024, ((IntLitExpr) var21).getValue());
assertEquals(Type.INT,var21.getType());
assertThat(var21.getCoerceTo(),anyOf(nullValue(), is(var21.getType())));
Expr var22= ((BinaryExpr) var20).getRight();
assertThat("",var22,instanceOf(IntLitExpr.class));
assertEquals(1, ((IntLitExpr) var22).getValue());
assertEquals(Type.INT,var22.getType());
assertThat(var22.getCoerceTo(),anyOf(nullValue(), is(var22.getType())));
assertEquals(Type.INT,var20.getType());
assertThat(var20.getCoerceTo(),anyOf(nullValue(), is(var20.getType())));
Expr var23= ((BinaryExpr) var19).getRight();
assertThat("",var23,instanceOf(BinaryExpr.class));
assertEquals(MINUS, ((BinaryExpr) var23).getOp().getKind());
Expr var24= ((BinaryExpr) var23).getLeft();
assertThat("",var24,instanceOf(IntLitExpr.class));
assertEquals(1024, ((IntLitExpr) var24).getValue());
assertEquals(Type.INT,var24.getType());
assertThat(var24.getCoerceTo(),anyOf(nullValue(), is(var24.getType())));
Expr var25= ((BinaryExpr) var23).getRight();
assertThat("",var25,instanceOf(IntLitExpr.class));
assertEquals(1, ((IntLitExpr) var25).getValue());
assertEquals(Type.INT,var25.getType());
assertThat(var25.getCoerceTo(),anyOf(nullValue(), is(var25.getType())));
assertEquals(Type.INT,var23.getType());
assertThat(var23.getCoerceTo(),anyOf(nullValue(), is(var23.getType())));
assertEquals(Type.INT,var19.getType());
assertThat(var19.getCoerceTo(),anyOf(nullValue(), is(var19.getType())));
assertEquals(Type.INT,var13.getType());
assertThat(var13.getCoerceTo(),anyOf(nullValue(), is(var13.getType())));
Expr var26= ((ColorExpr) var12).getGreen();
assertThat("",var26,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var26).getOp().getKind());
Expr var27= ((BinaryExpr) var26).getLeft();
assertThat("",var27,instanceOf(BinaryExpr.class));
assertEquals(TIMES, ((BinaryExpr) var27).getOp().getKind());
Expr var28= ((BinaryExpr) var27).getLeft();
assertThat("",var28,instanceOf(BinaryExpr.class));
assertEquals(TIMES, ((BinaryExpr) var28).getOp().getKind());
Expr var29= ((BinaryExpr) var28).getLeft();
assertThat("",var29,instanceOf(IdentExpr.class));
assertEquals("Z", var29.getText());
assertEquals(Type.INT,var29.getType());
assertThat(var29.getCoerceTo(),anyOf(nullValue(), is(var29.getType())));
Expr var30= ((BinaryExpr) var28).getRight();
assertThat("",var30,instanceOf(IdentExpr.class));
assertEquals("x", var30.getText());
assertEquals(Type.INT,var30.getType());
assertThat(var30.getCoerceTo(),anyOf(nullValue(), is(var30.getType())));
assertEquals(Type.INT,var28.getType());
assertThat(var28.getCoerceTo(),anyOf(nullValue(), is(var28.getType())));
Expr var31= ((BinaryExpr) var27).getRight();
assertThat("",var31,instanceOf(IdentExpr.class));
assertEquals("y", var31.getText());
assertEquals(Type.INT,var31.getType());
assertThat(var31.getCoerceTo(),anyOf(nullValue(), is(var31.getType())));
assertEquals(Type.INT,var27.getType());
assertThat(var27.getCoerceTo(),anyOf(nullValue(), is(var27.getType())));
Expr var32= ((BinaryExpr) var26).getRight();
assertThat("",var32,instanceOf(BinaryExpr.class));
assertEquals(TIMES, ((BinaryExpr) var32).getOp().getKind());
Expr var33= ((BinaryExpr) var32).getLeft();
assertThat("",var33,instanceOf(BinaryExpr.class));
assertEquals(MINUS, ((BinaryExpr) var33).getOp().getKind());
Expr var34= ((BinaryExpr) var33).getLeft();
assertThat("",var34,instanceOf(IntLitExpr.class));
assertEquals(1024, ((IntLitExpr) var34).getValue());
assertEquals(Type.INT,var34.getType());
assertThat(var34.getCoerceTo(),anyOf(nullValue(), is(var34.getType())));
Expr var35= ((BinaryExpr) var33).getRight();
assertThat("",var35,instanceOf(IntLitExpr.class));
assertEquals(1, ((IntLitExpr) var35).getValue());
assertEquals(Type.INT,var35.getType());
assertThat(var35.getCoerceTo(),anyOf(nullValue(), is(var35.getType())));
assertEquals(Type.INT,var33.getType());
assertThat(var33.getCoerceTo(),anyOf(nullValue(), is(var33.getType())));
Expr var36= ((BinaryExpr) var32).getRight();
assertThat("",var36,instanceOf(BinaryExpr.class));
assertEquals(MINUS, ((BinaryExpr) var36).getOp().getKind());
Expr var37= ((BinaryExpr) var36).getLeft();
assertThat("",var37,instanceOf(IntLitExpr.class));
assertEquals(1024, ((IntLitExpr) var37).getValue());
assertEquals(Type.INT,var37.getType());
assertThat(var37.getCoerceTo(),anyOf(nullValue(), is(var37.getType())));
Expr var38= ((BinaryExpr) var36).getRight();
assertThat("",var38,instanceOf(IntLitExpr.class));
assertEquals(1, ((IntLitExpr) var38).getValue());
assertEquals(Type.INT,var38.getType());
assertThat(var38.getCoerceTo(),anyOf(nullValue(), is(var38.getType())));
assertEquals(Type.INT,var36.getType());
assertThat(var36.getCoerceTo(),anyOf(nullValue(), is(var36.getType())));
assertEquals(Type.INT,var32.getType());
assertThat(var32.getCoerceTo(),anyOf(nullValue(), is(var32.getType())));
assertEquals(Type.INT,var26.getType());
assertThat(var26.getCoerceTo(),anyOf(nullValue(), is(var26.getType())));
Expr var39= ((ColorExpr) var12).getBlue();
assertThat("",var39,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var39).getOp().getKind());
Expr var40= ((BinaryExpr) var39).getLeft();
assertThat("",var40,instanceOf(BinaryExpr.class));
assertEquals(TIMES, ((BinaryExpr) var40).getOp().getKind());
Expr var41= ((BinaryExpr) var40).getLeft();
assertThat("",var41,instanceOf(BinaryExpr.class));
assertEquals(TIMES, ((BinaryExpr) var41).getOp().getKind());
Expr var42= ((BinaryExpr) var41).getLeft();
assertThat("",var42,instanceOf(IdentExpr.class));
assertEquals("Z", var42.getText());
assertEquals(Type.INT,var42.getType());
assertThat(var42.getCoerceTo(),anyOf(nullValue(), is(var42.getType())));
Expr var43= ((BinaryExpr) var41).getRight();
assertThat("",var43,instanceOf(IdentExpr.class));
assertEquals("x", var43.getText());
assertEquals(Type.INT,var43.getType());
assertThat(var43.getCoerceTo(),anyOf(nullValue(), is(var43.getType())));
assertEquals(Type.INT,var41.getType());
assertThat(var41.getCoerceTo(),anyOf(nullValue(), is(var41.getType())));
Expr var44= ((BinaryExpr) var40).getRight();
assertThat("",var44,instanceOf(IdentExpr.class));
assertEquals("y", var44.getText());
assertEquals(Type.INT,var44.getType());
assertThat(var44.getCoerceTo(),anyOf(nullValue(), is(var44.getType())));
assertEquals(Type.INT,var40.getType());
assertThat(var40.getCoerceTo(),anyOf(nullValue(), is(var40.getType())));
Expr var45= ((BinaryExpr) var39).getRight();
assertThat("",var45,instanceOf(BinaryExpr.class));
assertEquals(TIMES, ((BinaryExpr) var45).getOp().getKind());
Expr var46= ((BinaryExpr) var45).getLeft();
assertThat("",var46,instanceOf(BinaryExpr.class));
assertEquals(MINUS, ((BinaryExpr) var46).getOp().getKind());
Expr var47= ((BinaryExpr) var46).getLeft();
assertThat("",var47,instanceOf(IntLitExpr.class));
assertEquals(1024, ((IntLitExpr) var47).getValue());
assertEquals(Type.INT,var47.getType());
assertThat(var47.getCoerceTo(),anyOf(nullValue(), is(var47.getType())));
Expr var48= ((BinaryExpr) var46).getRight();
assertThat("",var48,instanceOf(IntLitExpr.class));
assertEquals(1, ((IntLitExpr) var48).getValue());
assertEquals(Type.INT,var48.getType());
assertThat(var48.getCoerceTo(),anyOf(nullValue(), is(var48.getType())));
assertEquals(Type.INT,var46.getType());
assertThat(var46.getCoerceTo(),anyOf(nullValue(), is(var46.getType())));
Expr var49= ((BinaryExpr) var45).getRight();
assertThat("",var49,instanceOf(BinaryExpr.class));
assertEquals(MINUS, ((BinaryExpr) var49).getOp().getKind());
Expr var50= ((BinaryExpr) var49).getLeft();
assertThat("",var50,instanceOf(IntLitExpr.class));
assertEquals(1024, ((IntLitExpr) var50).getValue());
assertEquals(Type.INT,var50.getType());
assertThat(var50.getCoerceTo(),anyOf(nullValue(), is(var50.getType())));
Expr var51= ((BinaryExpr) var49).getRight();
assertThat("",var51,instanceOf(IntLitExpr.class));
assertEquals(1, ((IntLitExpr) var51).getValue());
assertEquals(Type.INT,var51.getType());
assertThat(var51.getCoerceTo(),anyOf(nullValue(), is(var51.getType())));
assertEquals(Type.INT,var49.getType());
assertThat(var49.getCoerceTo(),anyOf(nullValue(), is(var49.getType())));
assertEquals(Type.INT,var45.getType());
assertThat(var45.getCoerceTo(),anyOf(nullValue(), is(var45.getType())));
assertEquals(Type.INT,var39.getType());
assertThat(var39.getCoerceTo(),anyOf(nullValue(), is(var39.getType())));
assertEquals(Type.COLOR,var12.getType());
assertThat(var12.getCoerceTo(),anyOf(nullValue(), is(var12.getType())));
ASTNode var52= decsAndStatements.get(3);
assertThat("",var52,instanceOf(ReturnStatement.class));
Expr var53= ((ReturnStatement) var52).getExpr();
assertThat("",var53,instanceOf(IdentExpr.class));
assertEquals("a", var53.getText());
assertEquals(Type.IMAGE,var53.getType());
assertThat(var53.getCoerceTo(),anyOf(nullValue(), is(var53.getType())));
});
}

@DisplayName("test007")
@Test public void test007(TestInfo testInfo) throws Exception{
String input = """
image f(int widthAndHeight)
image[widthAndHeight,widthAndHeight] a;
image[widthAndHeight,widthAndHeight] b;
a[x,y] = <<y, y, 0>>;
b[x,y] = << getRed a[y,x], 0, 0 >>;
^ b;

""";
show("\n\n----- test007 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.IMAGE, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(1, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var0).getType());
assertEquals("widthAndHeight", ((NameDef) var0).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(5, decsAndStatements.size());
ASTNode var1= decsAndStatements.get(0);
assertThat("",var1,instanceOf(VarDeclaration.class));
NameDef var2= ((VarDeclaration) var1).getNameDef();
assertThat("",var2,instanceOf(NameDef.class));
assertEquals(Type.IMAGE, ((NameDef) var2).getType());
assertEquals("a", ((NameDef) var2).getName());
Dimension var3= ((NameDefWithDim) var2).getDim();
assertThat("",var3,instanceOf(Dimension.class));
Expr var4= ((Dimension) var3).getWidth();
assertThat("",var4,instanceOf(IdentExpr.class));
assertEquals("widthAndHeight", var4.getText());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((Dimension) var3).getHeight();
assertThat("",var5,instanceOf(IdentExpr.class));
assertEquals("widthAndHeight", var5.getText());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
ASTNode var6= decsAndStatements.get(1);
assertThat("",var6,instanceOf(VarDeclaration.class));
NameDef var7= ((VarDeclaration) var6).getNameDef();
assertThat("",var7,instanceOf(NameDef.class));
assertEquals(Type.IMAGE, ((NameDef) var7).getType());
assertEquals("b", ((NameDef) var7).getName());
Dimension var8= ((NameDefWithDim) var7).getDim();
assertThat("",var8,instanceOf(Dimension.class));
Expr var9= ((Dimension) var8).getWidth();
assertThat("",var9,instanceOf(IdentExpr.class));
assertEquals("widthAndHeight", var9.getText());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
Expr var10= ((Dimension) var8).getHeight();
assertThat("",var10,instanceOf(IdentExpr.class));
assertEquals("widthAndHeight", var10.getText());
assertEquals(Type.INT,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
ASTNode var11= decsAndStatements.get(2);
assertThat("",var11,instanceOf(AssignmentStatement.class));
assertEquals("a", ((AssignmentStatement)var11).getName());
PixelSelector var12= ((AssignmentStatement) var11).getSelector();
assertThat("",var12,instanceOf(PixelSelector.class));
Expr var13= ((PixelSelector) var12).getX();
assertThat("",var13,instanceOf(IdentExpr.class));
assertEquals("x", var13.getText());
assertEquals(Type.INT,var13.getType());
assertThat(var13.getCoerceTo(),anyOf(nullValue(), is(var13.getType())));
Expr var14= ((PixelSelector) var12).getY();
assertThat("",var14,instanceOf(IdentExpr.class));
assertEquals("y", var14.getText());
assertEquals(Type.INT,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
Expr var15= ((AssignmentStatement) var11).getExpr();
assertThat("",var15,instanceOf(ColorExpr.class));
Expr var16= ((ColorExpr) var15).getRed();
assertThat("",var16,instanceOf(IdentExpr.class));
assertEquals("y", var16.getText());
assertEquals(Type.INT,var16.getType());
assertThat(var16.getCoerceTo(),anyOf(nullValue(), is(var16.getType())));
Expr var17= ((ColorExpr) var15).getGreen();
assertThat("",var17,instanceOf(IdentExpr.class));
assertEquals("y", var17.getText());
assertEquals(Type.INT,var17.getType());
assertThat(var17.getCoerceTo(),anyOf(nullValue(), is(var17.getType())));
Expr var18= ((ColorExpr) var15).getBlue();
assertThat("",var18,instanceOf(IntLitExpr.class));
assertEquals(0, ((IntLitExpr) var18).getValue());
assertEquals(Type.INT,var18.getType());
assertThat(var18.getCoerceTo(),anyOf(nullValue(), is(var18.getType())));
assertEquals(Type.COLOR,var15.getType());
assertThat(var15.getCoerceTo(),anyOf(nullValue(), is(var15.getType())));
ASTNode var19= decsAndStatements.get(3);
assertThat("",var19,instanceOf(AssignmentStatement.class));
assertEquals("b", ((AssignmentStatement)var19).getName());
PixelSelector var20= ((AssignmentStatement) var19).getSelector();
assertThat("",var20,instanceOf(PixelSelector.class));
Expr var21= ((PixelSelector) var20).getX();
assertThat("",var21,instanceOf(IdentExpr.class));
assertEquals("x", var21.getText());
assertEquals(Type.INT,var21.getType());
assertThat(var21.getCoerceTo(),anyOf(nullValue(), is(var21.getType())));
Expr var22= ((PixelSelector) var20).getY();
assertThat("",var22,instanceOf(IdentExpr.class));
assertEquals("y", var22.getText());
assertEquals(Type.INT,var22.getType());
assertThat(var22.getCoerceTo(),anyOf(nullValue(), is(var22.getType())));
Expr var23= ((AssignmentStatement) var19).getExpr();
assertThat("",var23,instanceOf(ColorExpr.class));
Expr var24= ((ColorExpr) var23).getRed();
assertThat("",var24,instanceOf(UnaryExpr.class));
assertEquals(COLOR_OP, ((UnaryExpr) var24).getOp().getKind());
Expr var25= ((UnaryExpr) var24).getExpr();
assertThat("",var25,instanceOf(UnaryExprPostfix.class));
Expr var26= ((UnaryExprPostfix) var25).getExpr();
assertThat("",var26,instanceOf(IdentExpr.class));
assertEquals("a", var26.getText());
assertEquals(Type.IMAGE,var26.getType());
assertThat(var26.getCoerceTo(),anyOf(nullValue(), is(var26.getType())));
PixelSelector var27= ((UnaryExprPostfix) var25).getSelector();
assertThat("",var27,instanceOf(PixelSelector.class));
Expr var28= ((PixelSelector) var27).getX();
assertThat("",var28,instanceOf(IdentExpr.class));
assertEquals("y", var28.getText());
assertEquals(Type.INT,var28.getType());
assertThat(var28.getCoerceTo(),anyOf(nullValue(), is(var28.getType())));
Expr var29= ((PixelSelector) var27).getY();
assertThat("",var29,instanceOf(IdentExpr.class));
assertEquals("x", var29.getText());
assertEquals(Type.INT,var29.getType());
assertThat(var29.getCoerceTo(),anyOf(nullValue(), is(var29.getType())));
assertEquals(Type.INT,var25.getType());
assertEquals(Type.COLOR,var25.getCoerceTo());
assertEquals(Type.INT,var24.getType());
assertThat(var24.getCoerceTo(),anyOf(nullValue(), is(var24.getType())));
Expr var30= ((ColorExpr) var23).getGreen();
assertThat("",var30,instanceOf(IntLitExpr.class));
assertEquals(0, ((IntLitExpr) var30).getValue());
assertEquals(Type.INT,var30.getType());
assertThat(var30.getCoerceTo(),anyOf(nullValue(), is(var30.getType())));
Expr var31= ((ColorExpr) var23).getBlue();
assertThat("",var31,instanceOf(IntLitExpr.class));
assertEquals(0, ((IntLitExpr) var31).getValue());
assertEquals(Type.INT,var31.getType());
assertThat(var31.getCoerceTo(),anyOf(nullValue(), is(var31.getType())));
assertEquals(Type.COLOR,var23.getType());
assertThat(var23.getCoerceTo(),anyOf(nullValue(), is(var23.getType())));
ASTNode var32= decsAndStatements.get(4);
assertThat("",var32,instanceOf(ReturnStatement.class));
Expr var33= ((ReturnStatement) var32).getExpr();
assertThat("",var33,instanceOf(IdentExpr.class));
assertEquals("b", var33.getText());
assertEquals(Type.IMAGE,var33.getType());
assertThat(var33.getCoerceTo(),anyOf(nullValue(), is(var33.getType())));
});
}

@DisplayName("test008")
@Test public void test008(TestInfo testInfo) throws Exception{
String input = """
float testWriteReadFloat1()
float x = 34.5678;
write x -> "testWriteReadFloat1";
float s;
s <- "testWriteReadFloat1";
^ s;

""";
show("\n\n----- test008 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.FLOAT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(5, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.FLOAT, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(FloatLitExpr.class));
assertEquals(34.5678f, ((FloatLitExpr) var2).getValue());
assertEquals(Type.FLOAT,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(WriteStatement.class));
Expr var4= ((WriteStatement) var3).getSource();
assertThat("",var4,instanceOf(IdentExpr.class));
assertEquals("x", var4.getText());
assertEquals(Type.FLOAT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((WriteStatement) var3).getDest();
assertThat("",var5,instanceOf(StringLitExpr.class));
assertEquals("testWriteReadFloat1", ((StringLitExpr) var5).getValue());
assertEquals(Type.STRING,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
ASTNode var6= decsAndStatements.get(2);
assertThat("",var6,instanceOf(VarDeclaration.class));
NameDef var7= ((VarDeclaration) var6).getNameDef();
assertThat("",var7,instanceOf(NameDef.class));
assertEquals(Type.FLOAT, ((NameDef) var7).getType());
assertEquals("s", ((NameDef) var7).getName());
ASTNode var8= decsAndStatements.get(3);
assertThat("",var8,instanceOf(ReadStatement.class));
assertEquals("s", ((ReadStatement)var8).getName());
assertNull(((ReadStatement) var8).getSelector());
Expr var9= ((ReadStatement) var8).getSource();
assertThat("",var9,instanceOf(StringLitExpr.class));
assertEquals("testWriteReadFloat1", ((StringLitExpr) var9).getValue());
assertEquals(Type.STRING,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
ASTNode var10= decsAndStatements.get(4);
assertThat("",var10,instanceOf(ReturnStatement.class));
Expr var11= ((ReturnStatement) var10).getExpr();
assertThat("",var11,instanceOf(IdentExpr.class));
assertEquals("s", var11.getText());
assertEquals(Type.FLOAT,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
});
}

@DisplayName("test009")
@Test public void test009(TestInfo testInfo) throws Exception{
String input = """
string testWriteReadString0()
string x = "hello";
write x -> "testWriteReadString0";
string s <- "testWriteReadString0";
^ s;

""";
show("\n\n----- test009 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.STRING, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(4, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.STRING, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(StringLitExpr.class));
assertEquals("hello", ((StringLitExpr) var2).getValue());
assertEquals(Type.STRING,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(WriteStatement.class));
Expr var4= ((WriteStatement) var3).getSource();
assertThat("",var4,instanceOf(IdentExpr.class));
assertEquals("x", var4.getText());
assertEquals(Type.STRING,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((WriteStatement) var3).getDest();
assertThat("",var5,instanceOf(StringLitExpr.class));
assertEquals("testWriteReadString0", ((StringLitExpr) var5).getValue());
assertEquals(Type.STRING,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
ASTNode var6= decsAndStatements.get(2);
assertThat("",var6,instanceOf(VarDeclaration.class));
NameDef var7= ((VarDeclaration) var6).getNameDef();
assertThat("",var7,instanceOf(NameDef.class));
assertEquals(Type.STRING, ((NameDef) var7).getType());
assertEquals("s", ((NameDef) var7).getName());
Expr var8= ((VarDeclaration) var6).getExpr();
assertThat("",var8,instanceOf(StringLitExpr.class));
assertEquals("testWriteReadString0", ((StringLitExpr) var8).getValue());
assertEquals(Type.STRING,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
assertEquals(LARROW, ((VarDeclaration) var6).getOp().getKind());
ASTNode var9= decsAndStatements.get(3);
assertThat("",var9,instanceOf(ReturnStatement.class));
Expr var10= ((ReturnStatement) var9).getExpr();
assertThat("",var10,instanceOf(IdentExpr.class));
assertEquals("s", var10.getText());
assertEquals(Type.STRING,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
});
}

@DisplayName("test010")
@Test public void test010(TestInfo testInfo) throws Exception{
String input = """
int y(string s)

""";
show("\n\n----- test010 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.INT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(1, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.STRING, ((NameDef) var0).getType());
assertEquals("s", ((NameDef) var0).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(0, decsAndStatements.size());
});
}

@DisplayName("test011")
@Test public void test011(TestInfo testInfo) throws Exception{
String input = """
boolean b()

""";
show("\n\n----- test011 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.BOOLEAN, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(0, decsAndStatements.size());
});
}

@DisplayName("test012")
@Test public void test012(TestInfo testInfo) throws Exception{
String input = """
int testMultipleWrites()
int x0 = 34;
int x1 = 56;
string file = "testMultipleWrites";
write x0 -> file;
write x1 -> file;
int x3 ;
x3 <- file;
write "x3="-> console;
write x3 -> console;
int x4;
x4 <- file;
write "  x4="-> console;
write x4 -> console;
^x3;

""";
show("\n\n----- test012 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.INT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(14, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var1).getType());
assertEquals("x0", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(IntLitExpr.class));
assertEquals(34, ((IntLitExpr) var2).getValue());
assertEquals(Type.INT,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(VarDeclaration.class));
NameDef var4= ((VarDeclaration) var3).getNameDef();
assertThat("",var4,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var4).getType());
assertEquals("x1", ((NameDef) var4).getName());
Expr var5= ((VarDeclaration) var3).getExpr();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(56, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var3).getOp().getKind());
ASTNode var6= decsAndStatements.get(2);
assertThat("",var6,instanceOf(VarDeclaration.class));
NameDef var7= ((VarDeclaration) var6).getNameDef();
assertThat("",var7,instanceOf(NameDef.class));
assertEquals(Type.STRING, ((NameDef) var7).getType());
assertEquals("file", ((NameDef) var7).getName());
Expr var8= ((VarDeclaration) var6).getExpr();
assertThat("",var8,instanceOf(StringLitExpr.class));
assertEquals("testMultipleWrites", ((StringLitExpr) var8).getValue());
assertEquals(Type.STRING,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var6).getOp().getKind());
ASTNode var9= decsAndStatements.get(3);
assertThat("",var9,instanceOf(WriteStatement.class));
Expr var10= ((WriteStatement) var9).getSource();
assertThat("",var10,instanceOf(IdentExpr.class));
assertEquals("x0", var10.getText());
assertEquals(Type.INT,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
Expr var11= ((WriteStatement) var9).getDest();
assertThat("",var11,instanceOf(IdentExpr.class));
assertEquals("file", var11.getText());
assertEquals(Type.STRING,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
ASTNode var12= decsAndStatements.get(4);
assertThat("",var12,instanceOf(WriteStatement.class));
Expr var13= ((WriteStatement) var12).getSource();
assertThat("",var13,instanceOf(IdentExpr.class));
assertEquals("x1", var13.getText());
assertEquals(Type.INT,var13.getType());
assertThat(var13.getCoerceTo(),anyOf(nullValue(), is(var13.getType())));
Expr var14= ((WriteStatement) var12).getDest();
assertThat("",var14,instanceOf(IdentExpr.class));
assertEquals("file", var14.getText());
assertEquals(Type.STRING,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
ASTNode var15= decsAndStatements.get(5);
assertThat("",var15,instanceOf(VarDeclaration.class));
NameDef var16= ((VarDeclaration) var15).getNameDef();
assertThat("",var16,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var16).getType());
assertEquals("x3", ((NameDef) var16).getName());
ASTNode var17= decsAndStatements.get(6);
assertThat("",var17,instanceOf(ReadStatement.class));
assertEquals("x3", ((ReadStatement)var17).getName());
assertNull(((ReadStatement) var17).getSelector());
Expr var18= ((ReadStatement) var17).getSource();
assertThat("",var18,instanceOf(IdentExpr.class));
assertEquals("file", var18.getText());
assertEquals(Type.STRING,var18.getType());
assertThat(var18.getCoerceTo(),anyOf(nullValue(), is(var18.getType())));
ASTNode var19= decsAndStatements.get(7);
assertThat("",var19,instanceOf(WriteStatement.class));
Expr var20= ((WriteStatement) var19).getSource();
assertThat("",var20,instanceOf(StringLitExpr.class));
assertEquals("x3=", ((StringLitExpr) var20).getValue());
assertEquals(Type.STRING,var20.getType());
assertThat(var20.getCoerceTo(),anyOf(nullValue(), is(var20.getType())));
Expr var21= ((WriteStatement) var19).getDest();
assertThat("",var21,instanceOf(ConsoleExpr.class));
assertEquals(Type.CONSOLE,var21.getType());
assertThat(var21.getCoerceTo(),anyOf(nullValue(), is(var21.getType())));
ASTNode var22= decsAndStatements.get(8);
assertThat("",var22,instanceOf(WriteStatement.class));
Expr var23= ((WriteStatement) var22).getSource();
assertThat("",var23,instanceOf(IdentExpr.class));
assertEquals("x3", var23.getText());
assertEquals(Type.INT,var23.getType());
assertThat(var23.getCoerceTo(),anyOf(nullValue(), is(var23.getType())));
Expr var24= ((WriteStatement) var22).getDest();
assertThat("",var24,instanceOf(ConsoleExpr.class));
assertEquals(Type.CONSOLE,var24.getType());
assertThat(var24.getCoerceTo(),anyOf(nullValue(), is(var24.getType())));
ASTNode var25= decsAndStatements.get(9);
assertThat("",var25,instanceOf(VarDeclaration.class));
NameDef var26= ((VarDeclaration) var25).getNameDef();
assertThat("",var26,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var26).getType());
assertEquals("x4", ((NameDef) var26).getName());
ASTNode var27= decsAndStatements.get(10);
assertThat("",var27,instanceOf(ReadStatement.class));
assertEquals("x4", ((ReadStatement)var27).getName());
assertNull(((ReadStatement) var27).getSelector());
Expr var28= ((ReadStatement) var27).getSource();
assertThat("",var28,instanceOf(IdentExpr.class));
assertEquals("file", var28.getText());
assertEquals(Type.STRING,var28.getType());
assertThat(var28.getCoerceTo(),anyOf(nullValue(), is(var28.getType())));
ASTNode var29= decsAndStatements.get(11);
assertThat("",var29,instanceOf(WriteStatement.class));
Expr var30= ((WriteStatement) var29).getSource();
assertThat("",var30,instanceOf(StringLitExpr.class));
assertEquals("  x4=", ((StringLitExpr) var30).getValue());
assertEquals(Type.STRING,var30.getType());
assertThat(var30.getCoerceTo(),anyOf(nullValue(), is(var30.getType())));
Expr var31= ((WriteStatement) var29).getDest();
assertThat("",var31,instanceOf(ConsoleExpr.class));
assertEquals(Type.CONSOLE,var31.getType());
assertThat(var31.getCoerceTo(),anyOf(nullValue(), is(var31.getType())));
ASTNode var32= decsAndStatements.get(12);
assertThat("",var32,instanceOf(WriteStatement.class));
Expr var33= ((WriteStatement) var32).getSource();
assertThat("",var33,instanceOf(IdentExpr.class));
assertEquals("x4", var33.getText());
assertEquals(Type.INT,var33.getType());
assertThat(var33.getCoerceTo(),anyOf(nullValue(), is(var33.getType())));
Expr var34= ((WriteStatement) var32).getDest();
assertThat("",var34,instanceOf(ConsoleExpr.class));
assertEquals(Type.CONSOLE,var34.getType());
assertThat(var34.getCoerceTo(),anyOf(nullValue(), is(var34.getType())));
ASTNode var35= decsAndStatements.get(13);
assertThat("",var35,instanceOf(ReturnStatement.class));
Expr var36= ((ReturnStatement) var35).getExpr();
assertThat("",var36,instanceOf(IdentExpr.class));
assertEquals("x3", var36.getText());
assertEquals(Type.INT,var36.getType());
assertThat(var36.getCoerceTo(),anyOf(nullValue(), is(var36.getType())));
});
}

@DisplayName("test013")
@Test public void test013(TestInfo testInfo) throws Exception{
String input = """
image chessBoard(int size)
image[size,size] a;
image[size,size] b;
image[size,size] c;
int stripSize = size/2;
a[x,y] = YELLOW;
b[x,y] = BLUE;
c[x,y] = if (y > stripSize) YELLOW else BLUE fi;
^c;

""";
show("\n\n----- test013 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.IMAGE, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(1, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var0).getType());
assertEquals("size", ((NameDef) var0).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(8, decsAndStatements.size());
ASTNode var1= decsAndStatements.get(0);
assertThat("",var1,instanceOf(VarDeclaration.class));
NameDef var2= ((VarDeclaration) var1).getNameDef();
assertThat("",var2,instanceOf(NameDef.class));
assertEquals(Type.IMAGE, ((NameDef) var2).getType());
assertEquals("a", ((NameDef) var2).getName());
Dimension var3= ((NameDefWithDim) var2).getDim();
assertThat("",var3,instanceOf(Dimension.class));
Expr var4= ((Dimension) var3).getWidth();
assertThat("",var4,instanceOf(IdentExpr.class));
assertEquals("size", var4.getText());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((Dimension) var3).getHeight();
assertThat("",var5,instanceOf(IdentExpr.class));
assertEquals("size", var5.getText());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
ASTNode var6= decsAndStatements.get(1);
assertThat("",var6,instanceOf(VarDeclaration.class));
NameDef var7= ((VarDeclaration) var6).getNameDef();
assertThat("",var7,instanceOf(NameDef.class));
assertEquals(Type.IMAGE, ((NameDef) var7).getType());
assertEquals("b", ((NameDef) var7).getName());
Dimension var8= ((NameDefWithDim) var7).getDim();
assertThat("",var8,instanceOf(Dimension.class));
Expr var9= ((Dimension) var8).getWidth();
assertThat("",var9,instanceOf(IdentExpr.class));
assertEquals("size", var9.getText());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
Expr var10= ((Dimension) var8).getHeight();
assertThat("",var10,instanceOf(IdentExpr.class));
assertEquals("size", var10.getText());
assertEquals(Type.INT,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
ASTNode var11= decsAndStatements.get(2);
assertThat("",var11,instanceOf(VarDeclaration.class));
NameDef var12= ((VarDeclaration) var11).getNameDef();
assertThat("",var12,instanceOf(NameDef.class));
assertEquals(Type.IMAGE, ((NameDef) var12).getType());
assertEquals("c", ((NameDef) var12).getName());
Dimension var13= ((NameDefWithDim) var12).getDim();
assertThat("",var13,instanceOf(Dimension.class));
Expr var14= ((Dimension) var13).getWidth();
assertThat("",var14,instanceOf(IdentExpr.class));
assertEquals("size", var14.getText());
assertEquals(Type.INT,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
Expr var15= ((Dimension) var13).getHeight();
assertThat("",var15,instanceOf(IdentExpr.class));
assertEquals("size", var15.getText());
assertEquals(Type.INT,var15.getType());
assertThat(var15.getCoerceTo(),anyOf(nullValue(), is(var15.getType())));
ASTNode var16= decsAndStatements.get(3);
assertThat("",var16,instanceOf(VarDeclaration.class));
NameDef var17= ((VarDeclaration) var16).getNameDef();
assertThat("",var17,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var17).getType());
assertEquals("stripSize", ((NameDef) var17).getName());
Expr var18= ((VarDeclaration) var16).getExpr();
assertThat("",var18,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var18).getOp().getKind());
Expr var19= ((BinaryExpr) var18).getLeft();
assertThat("",var19,instanceOf(IdentExpr.class));
assertEquals("size", var19.getText());
assertEquals(Type.INT,var19.getType());
assertThat(var19.getCoerceTo(),anyOf(nullValue(), is(var19.getType())));
Expr var20= ((BinaryExpr) var18).getRight();
assertThat("",var20,instanceOf(IntLitExpr.class));
assertEquals(2, ((IntLitExpr) var20).getValue());
assertEquals(Type.INT,var20.getType());
assertThat(var20.getCoerceTo(),anyOf(nullValue(), is(var20.getType())));
assertEquals(Type.INT,var18.getType());
assertThat(var18.getCoerceTo(),anyOf(nullValue(), is(var18.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var16).getOp().getKind());
ASTNode var21= decsAndStatements.get(4);
assertThat("",var21,instanceOf(AssignmentStatement.class));
assertEquals("a", ((AssignmentStatement)var21).getName());
PixelSelector var22= ((AssignmentStatement) var21).getSelector();
assertThat("",var22,instanceOf(PixelSelector.class));
Expr var23= ((PixelSelector) var22).getX();
assertThat("",var23,instanceOf(IdentExpr.class));
assertEquals("x", var23.getText());
assertEquals(Type.INT,var23.getType());
assertThat(var23.getCoerceTo(),anyOf(nullValue(), is(var23.getType())));
Expr var24= ((PixelSelector) var22).getY();
assertThat("",var24,instanceOf(IdentExpr.class));
assertEquals("y", var24.getText());
assertEquals(Type.INT,var24.getType());
assertThat(var24.getCoerceTo(),anyOf(nullValue(), is(var24.getType())));
Expr var25= ((AssignmentStatement) var21).getExpr();
assertThat("",var25,instanceOf(ColorConstExpr.class));
assertEquals("YELLOW", var25.getText());
assertEquals(Type.COLOR,var25.getType());
assertThat(var25.getCoerceTo(),anyOf(nullValue(), is(var25.getType())));
ASTNode var26= decsAndStatements.get(5);
assertThat("",var26,instanceOf(AssignmentStatement.class));
assertEquals("b", ((AssignmentStatement)var26).getName());
PixelSelector var27= ((AssignmentStatement) var26).getSelector();
assertThat("",var27,instanceOf(PixelSelector.class));
Expr var28= ((PixelSelector) var27).getX();
assertThat("",var28,instanceOf(IdentExpr.class));
assertEquals("x", var28.getText());
assertEquals(Type.INT,var28.getType());
assertThat(var28.getCoerceTo(),anyOf(nullValue(), is(var28.getType())));
Expr var29= ((PixelSelector) var27).getY();
assertThat("",var29,instanceOf(IdentExpr.class));
assertEquals("y", var29.getText());
assertEquals(Type.INT,var29.getType());
assertThat(var29.getCoerceTo(),anyOf(nullValue(), is(var29.getType())));
Expr var30= ((AssignmentStatement) var26).getExpr();
assertThat("",var30,instanceOf(ColorConstExpr.class));
assertEquals("BLUE", var30.getText());
assertEquals(Type.COLOR,var30.getType());
assertThat(var30.getCoerceTo(),anyOf(nullValue(), is(var30.getType())));
ASTNode var31= decsAndStatements.get(6);
assertThat("",var31,instanceOf(AssignmentStatement.class));
assertEquals("c", ((AssignmentStatement)var31).getName());
PixelSelector var32= ((AssignmentStatement) var31).getSelector();
assertThat("",var32,instanceOf(PixelSelector.class));
Expr var33= ((PixelSelector) var32).getX();
assertThat("",var33,instanceOf(IdentExpr.class));
assertEquals("x", var33.getText());
assertEquals(Type.INT,var33.getType());
assertThat(var33.getCoerceTo(),anyOf(nullValue(), is(var33.getType())));
Expr var34= ((PixelSelector) var32).getY();
assertThat("",var34,instanceOf(IdentExpr.class));
assertEquals("y", var34.getText());
assertEquals(Type.INT,var34.getType());
assertThat(var34.getCoerceTo(),anyOf(nullValue(), is(var34.getType())));
Expr var35= ((AssignmentStatement) var31).getExpr();
assertThat("",var35,instanceOf(ConditionalExpr.class));
Expr var36= ((ConditionalExpr) var35).getCondition();
assertThat("",var36,instanceOf(BinaryExpr.class));
assertEquals(GT, ((BinaryExpr) var36).getOp().getKind());
Expr var37= ((BinaryExpr) var36).getLeft();
assertThat("",var37,instanceOf(IdentExpr.class));
assertEquals("y", var37.getText());
assertEquals(Type.INT,var37.getType());
assertThat(var37.getCoerceTo(),anyOf(nullValue(), is(var37.getType())));
Expr var38= ((BinaryExpr) var36).getRight();
assertThat("",var38,instanceOf(IdentExpr.class));
assertEquals("stripSize", var38.getText());
assertEquals(Type.INT,var38.getType());
assertThat(var38.getCoerceTo(),anyOf(nullValue(), is(var38.getType())));
assertEquals(Type.BOOLEAN,var36.getType());
assertThat(var36.getCoerceTo(),anyOf(nullValue(), is(var36.getType())));
Expr var39= ((ConditionalExpr) var35).getTrueCase();
assertThat("",var39,instanceOf(ColorConstExpr.class));
assertEquals("YELLOW", var39.getText());
assertEquals(Type.COLOR,var39.getType());
assertThat(var39.getCoerceTo(),anyOf(nullValue(), is(var39.getType())));
Expr var40= ((ConditionalExpr) var35).getFalseCase();
assertThat("",var40,instanceOf(ColorConstExpr.class));
assertEquals("BLUE", var40.getText());
assertEquals(Type.COLOR,var40.getType());
assertThat(var40.getCoerceTo(),anyOf(nullValue(), is(var40.getType())));
assertEquals(Type.COLOR,var35.getType());
assertThat(var35.getCoerceTo(),anyOf(nullValue(), is(var35.getType())));
ASTNode var41= decsAndStatements.get(7);
assertThat("",var41,instanceOf(ReturnStatement.class));
Expr var42= ((ReturnStatement) var41).getExpr();
assertThat("",var42,instanceOf(IdentExpr.class));
assertEquals("c", var42.getText());
assertEquals(Type.IMAGE,var42.getType());
assertThat(var42.getCoerceTo(),anyOf(nullValue(), is(var42.getType())));
});
}

@DisplayName("test014")
@Test public void test014(TestInfo testInfo) throws Exception{
String input = """
void withUninitializedDecs()
int a;
float b;
image c;
string e;
boolean f;
color e;

""";
show("\n\n----- test014 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
Exception e = assertThrows(TypeCheckException.class, () -> {
checkTypes(ast);
});
show("Expected TypeCheckException:     " + e);
});
}

@DisplayName("test015")
@Test public void test015(TestInfo testInfo) throws Exception{
String input = """
void y(string s)

""";
show("\n\n----- test015 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.VOID, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(1, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.STRING, ((NameDef) var0).getType());
assertEquals("s", ((NameDef) var0).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(0, decsAndStatements.size());
});
}

@DisplayName("test016")
@Test public void test016(TestInfo testInfo) throws Exception{
String input = """
color testWriteColorToFile(string file)
color x = <<3,4,5>>;
write x -> file;
^x;

""";
show("\n\n----- test016 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(1, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.STRING, ((NameDef) var0).getType());
assertEquals("file", ((NameDef) var0).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var1= decsAndStatements.get(0);
assertThat("",var1,instanceOf(VarDeclaration.class));
NameDef var2= ((VarDeclaration) var1).getNameDef();
assertThat("",var2,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var2).getType());
assertEquals("x", ((NameDef) var2).getName());
Expr var3= ((VarDeclaration) var1).getExpr();
assertThat("",var3,instanceOf(ColorExpr.class));
Expr var4= ((ColorExpr) var3).getRed();
assertThat("",var4,instanceOf(IntLitExpr.class));
assertEquals(3, ((IntLitExpr) var4).getValue());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((ColorExpr) var3).getGreen();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(4, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
Expr var6= ((ColorExpr) var3).getBlue();
assertThat("",var6,instanceOf(IntLitExpr.class));
assertEquals(5, ((IntLitExpr) var6).getValue());
assertEquals(Type.INT,var6.getType());
assertThat(var6.getCoerceTo(),anyOf(nullValue(), is(var6.getType())));
assertEquals(Type.COLOR,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var1).getOp().getKind());
ASTNode var7= decsAndStatements.get(1);
assertThat("",var7,instanceOf(WriteStatement.class));
Expr var8= ((WriteStatement) var7).getSource();
assertThat("",var8,instanceOf(IdentExpr.class));
assertEquals("x", var8.getText());
assertEquals(Type.COLOR,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
Expr var9= ((WriteStatement) var7).getDest();
assertThat("",var9,instanceOf(IdentExpr.class));
assertEquals("file", var9.getText());
assertEquals(Type.STRING,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
ASTNode var10= decsAndStatements.get(2);
assertThat("",var10,instanceOf(ReturnStatement.class));
Expr var11= ((ReturnStatement) var10).getExpr();
assertThat("",var11,instanceOf(IdentExpr.class));
assertEquals("x", var11.getText());
assertEquals(Type.COLOR,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
});
}

@DisplayName("test017")
@Test public void test017(TestInfo testInfo) throws Exception{
String input = """
float f()
float a = 20.0;
float b = 6.0;
^ a*b;

""";
show("\n\n----- test017 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.FLOAT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.FLOAT, ((NameDef) var1).getType());
assertEquals("a", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(FloatLitExpr.class));
assertEquals(20.0f, ((FloatLitExpr) var2).getValue());
assertEquals(Type.FLOAT,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(VarDeclaration.class));
NameDef var4= ((VarDeclaration) var3).getNameDef();
assertThat("",var4,instanceOf(NameDef.class));
assertEquals(Type.FLOAT, ((NameDef) var4).getType());
assertEquals("b", ((NameDef) var4).getName());
Expr var5= ((VarDeclaration) var3).getExpr();
assertThat("",var5,instanceOf(FloatLitExpr.class));
assertEquals(6.0f, ((FloatLitExpr) var5).getValue());
assertEquals(Type.FLOAT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var3).getOp().getKind());
ASTNode var6= decsAndStatements.get(2);
assertThat("",var6,instanceOf(ReturnStatement.class));
Expr var7= ((ReturnStatement) var6).getExpr();
assertThat("",var7,instanceOf(BinaryExpr.class));
assertEquals(TIMES, ((BinaryExpr) var7).getOp().getKind());
Expr var8= ((BinaryExpr) var7).getLeft();
assertThat("",var8,instanceOf(IdentExpr.class));
assertEquals("a", var8.getText());
assertEquals(Type.FLOAT,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
Expr var9= ((BinaryExpr) var7).getRight();
assertThat("",var9,instanceOf(IdentExpr.class));
assertEquals("b", var9.getText());
assertEquals(Type.FLOAT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
assertEquals(Type.FLOAT,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
});
}

@DisplayName("test018")
@Test public void test018(TestInfo testInfo) throws Exception{
String input = """
boolean f() ^ false;

""";
show("\n\n----- test018 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.BOOLEAN, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(ReturnStatement.class));
Expr var1= ((ReturnStatement) var0).getExpr();
assertThat("",var1,instanceOf(BooleanLitExpr.class));
assertFalse(((BooleanLitExpr) var1).getValue());
assertEquals(Type.BOOLEAN,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
});
}

@DisplayName("test019")
@Test public void test019(TestInfo testInfo) throws Exception{
String input = """
int f()
int a = 20;
int b = 6;
^ a*b;

""";
show("\n\n----- test019 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.INT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var1).getType());
assertEquals("a", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(IntLitExpr.class));
assertEquals(20, ((IntLitExpr) var2).getValue());
assertEquals(Type.INT,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(VarDeclaration.class));
NameDef var4= ((VarDeclaration) var3).getNameDef();
assertThat("",var4,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var4).getType());
assertEquals("b", ((NameDef) var4).getName());
Expr var5= ((VarDeclaration) var3).getExpr();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(6, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var3).getOp().getKind());
ASTNode var6= decsAndStatements.get(2);
assertThat("",var6,instanceOf(ReturnStatement.class));
Expr var7= ((ReturnStatement) var6).getExpr();
assertThat("",var7,instanceOf(BinaryExpr.class));
assertEquals(TIMES, ((BinaryExpr) var7).getOp().getKind());
Expr var8= ((BinaryExpr) var7).getLeft();
assertThat("",var8,instanceOf(IdentExpr.class));
assertEquals("a", var8.getText());
assertEquals(Type.INT,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
Expr var9= ((BinaryExpr) var7).getRight();
assertThat("",var9,instanceOf(IdentExpr.class));
assertEquals("b", var9.getText());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
assertEquals(Type.INT,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
});
}

@DisplayName("test020")
@Test public void test020(TestInfo testInfo) throws Exception{
String input = """
int a(int b)
^b;

""";
show("\n\n----- test020 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.INT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(1, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var0).getType());
assertEquals("b", ((NameDef) var0).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var1= decsAndStatements.get(0);
assertThat("",var1,instanceOf(ReturnStatement.class));
Expr var2= ((ReturnStatement) var1).getExpr();
assertThat("",var2,instanceOf(IdentExpr.class));
assertEquals("b", var2.getText());
assertEquals(Type.INT,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
});
}

@DisplayName("test021")
@Test public void test021(TestInfo testInfo) throws Exception{
String input = """
image f(int widthAndHeight)
image[widthAndHeight,widthAndHeight] a;
a[x,y] = <<y, y, 0>>;
^ a;

""";
show("\n\n----- test021 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.IMAGE, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(1, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var0).getType());
assertEquals("widthAndHeight", ((NameDef) var0).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var1= decsAndStatements.get(0);
assertThat("",var1,instanceOf(VarDeclaration.class));
NameDef var2= ((VarDeclaration) var1).getNameDef();
assertThat("",var2,instanceOf(NameDef.class));
assertEquals(Type.IMAGE, ((NameDef) var2).getType());
assertEquals("a", ((NameDef) var2).getName());
Dimension var3= ((NameDefWithDim) var2).getDim();
assertThat("",var3,instanceOf(Dimension.class));
Expr var4= ((Dimension) var3).getWidth();
assertThat("",var4,instanceOf(IdentExpr.class));
assertEquals("widthAndHeight", var4.getText());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((Dimension) var3).getHeight();
assertThat("",var5,instanceOf(IdentExpr.class));
assertEquals("widthAndHeight", var5.getText());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
ASTNode var6= decsAndStatements.get(1);
assertThat("",var6,instanceOf(AssignmentStatement.class));
assertEquals("a", ((AssignmentStatement)var6).getName());
PixelSelector var7= ((AssignmentStatement) var6).getSelector();
assertThat("",var7,instanceOf(PixelSelector.class));
Expr var8= ((PixelSelector) var7).getX();
assertThat("",var8,instanceOf(IdentExpr.class));
assertEquals("x", var8.getText());
assertEquals(Type.INT,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
Expr var9= ((PixelSelector) var7).getY();
assertThat("",var9,instanceOf(IdentExpr.class));
assertEquals("y", var9.getText());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
Expr var10= ((AssignmentStatement) var6).getExpr();
assertThat("",var10,instanceOf(ColorExpr.class));
Expr var11= ((ColorExpr) var10).getRed();
assertThat("",var11,instanceOf(IdentExpr.class));
assertEquals("y", var11.getText());
assertEquals(Type.INT,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
Expr var12= ((ColorExpr) var10).getGreen();
assertThat("",var12,instanceOf(IdentExpr.class));
assertEquals("y", var12.getText());
assertEquals(Type.INT,var12.getType());
assertThat(var12.getCoerceTo(),anyOf(nullValue(), is(var12.getType())));
Expr var13= ((ColorExpr) var10).getBlue();
assertThat("",var13,instanceOf(IntLitExpr.class));
assertEquals(0, ((IntLitExpr) var13).getValue());
assertEquals(Type.INT,var13.getType());
assertThat(var13.getCoerceTo(),anyOf(nullValue(), is(var13.getType())));
assertEquals(Type.COLOR,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
ASTNode var14= decsAndStatements.get(2);
assertThat("",var14,instanceOf(ReturnStatement.class));
Expr var15= ((ReturnStatement) var14).getExpr();
assertThat("",var15,instanceOf(IdentExpr.class));
assertEquals("a", var15.getText());
assertEquals(Type.IMAGE,var15.getType());
assertThat(var15.getCoerceTo(),anyOf(nullValue(), is(var15.getType())));
});
}

@DisplayName("test022")
@Test public void test022(TestInfo testInfo) throws Exception{
String input = """
void withParams(foo x)

""";
show("\n\n----- test022 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
Exception e = assertThrows(SyntaxException.class, () -> {
@SuppressWarnings("unused")
ASTNode ast = getAST(input);
});
show("Expected SyntaxException:     " + e);
});
}

@DisplayName("test023")
@Test public void test023(TestInfo testInfo) throws Exception{
String input = """
boolean f() boolean x; x = false; ^ x;

""";
show("\n\n----- test023 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.BOOLEAN, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.BOOLEAN, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
ASTNode var2= decsAndStatements.get(1);
assertThat("",var2,instanceOf(AssignmentStatement.class));
assertEquals("x", ((AssignmentStatement)var2).getName());
assertNull(((AssignmentStatement) var2).getSelector());
Expr var3= ((AssignmentStatement) var2).getExpr();
assertThat("",var3,instanceOf(BooleanLitExpr.class));
assertFalse(((BooleanLitExpr) var3).getValue());
assertEquals(Type.BOOLEAN,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
ASTNode var4= decsAndStatements.get(2);
assertThat("",var4,instanceOf(ReturnStatement.class));
Expr var5= ((ReturnStatement) var4).getExpr();
assertThat("",var5,instanceOf(IdentExpr.class));
assertEquals("x", var5.getText());
assertEquals(Type.BOOLEAN,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
});
}

@DisplayName("test024")
@Test public void test024(TestInfo testInfo) throws Exception{
String input = """
string f() string x = "this is a string"; ^ x;

""";
show("\n\n----- test024 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.STRING, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(2, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.STRING, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(StringLitExpr.class));
assertEquals("this is a string", ((StringLitExpr) var2).getValue());
assertEquals(Type.STRING,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(ReturnStatement.class));
Expr var4= ((ReturnStatement) var3).getExpr();
assertThat("",var4,instanceOf(IdentExpr.class));
assertEquals("x", var4.getText());
assertEquals(Type.STRING,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
});
}

@DisplayName("test025")
@Test public void test025(TestInfo testInfo) throws Exception{
String input = """
boolean a() ^ false;

""";
show("\n\n----- test025 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.BOOLEAN, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(ReturnStatement.class));
Expr var1= ((ReturnStatement) var0).getExpr();
assertThat("",var1,instanceOf(BooleanLitExpr.class));
assertFalse(((BooleanLitExpr) var1).getValue());
assertEquals(Type.BOOLEAN,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
});
}

@DisplayName("test026")
@Test public void test026(TestInfo testInfo) throws Exception{
String input = """
color f()
^ LIGHT_GRAY;

""";
show("\n\n----- test026 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(ReturnStatement.class));
Expr var1= ((ReturnStatement) var0).getExpr();
assertThat("",var1,instanceOf(ColorConstExpr.class));
assertEquals("LIGHT_GRAY", var1.getText());
assertEquals(Type.COLOR,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
});
}

@DisplayName("test027")
@Test public void test027(TestInfo testInfo) throws Exception{
String input = """
color f()
^ BLACK;

""";
show("\n\n----- test027 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(ReturnStatement.class));
Expr var1= ((ReturnStatement) var0).getExpr();
assertThat("",var1,instanceOf(ColorConstExpr.class));
assertEquals("BLACK", var1.getText());
assertEquals(Type.COLOR,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
});
}

@DisplayName("test028")
@Test public void test028(TestInfo testInfo) throws Exception{
String input = """
color f() color x; x = <<34,56,78>>; ^ x;

""";
show("\n\n----- test028 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
ASTNode var2= decsAndStatements.get(1);
assertThat("",var2,instanceOf(AssignmentStatement.class));
assertEquals("x", ((AssignmentStatement)var2).getName());
assertNull(((AssignmentStatement) var2).getSelector());
Expr var3= ((AssignmentStatement) var2).getExpr();
assertThat("",var3,instanceOf(ColorExpr.class));
Expr var4= ((ColorExpr) var3).getRed();
assertThat("",var4,instanceOf(IntLitExpr.class));
assertEquals(34, ((IntLitExpr) var4).getValue());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((ColorExpr) var3).getGreen();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(56, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
Expr var6= ((ColorExpr) var3).getBlue();
assertThat("",var6,instanceOf(IntLitExpr.class));
assertEquals(78, ((IntLitExpr) var6).getValue());
assertEquals(Type.INT,var6.getType());
assertThat(var6.getCoerceTo(),anyOf(nullValue(), is(var6.getType())));
assertEquals(Type.COLOR,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
ASTNode var7= decsAndStatements.get(2);
assertThat("",var7,instanceOf(ReturnStatement.class));
Expr var8= ((ReturnStatement) var7).getExpr();
assertThat("",var8,instanceOf(IdentExpr.class));
assertEquals("x", var8.getText());
assertEquals(Type.COLOR,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
});
}

@DisplayName("test029")
@Test public void test029(TestInfo testInfo) throws Exception{
String input = """
image f(int widthAndHeight)
image[widthAndHeight,widthAndHeight] a;
a[x,y] = <<x-y, x-y, x-y>>;
^ a;

""";
show("\n\n----- test029 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.IMAGE, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(1, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var0).getType());
assertEquals("widthAndHeight", ((NameDef) var0).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var1= decsAndStatements.get(0);
assertThat("",var1,instanceOf(VarDeclaration.class));
NameDef var2= ((VarDeclaration) var1).getNameDef();
assertThat("",var2,instanceOf(NameDef.class));
assertEquals(Type.IMAGE, ((NameDef) var2).getType());
assertEquals("a", ((NameDef) var2).getName());
Dimension var3= ((NameDefWithDim) var2).getDim();
assertThat("",var3,instanceOf(Dimension.class));
Expr var4= ((Dimension) var3).getWidth();
assertThat("",var4,instanceOf(IdentExpr.class));
assertEquals("widthAndHeight", var4.getText());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((Dimension) var3).getHeight();
assertThat("",var5,instanceOf(IdentExpr.class));
assertEquals("widthAndHeight", var5.getText());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
ASTNode var6= decsAndStatements.get(1);
assertThat("",var6,instanceOf(AssignmentStatement.class));
assertEquals("a", ((AssignmentStatement)var6).getName());
PixelSelector var7= ((AssignmentStatement) var6).getSelector();
assertThat("",var7,instanceOf(PixelSelector.class));
Expr var8= ((PixelSelector) var7).getX();
assertThat("",var8,instanceOf(IdentExpr.class));
assertEquals("x", var8.getText());
assertEquals(Type.INT,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
Expr var9= ((PixelSelector) var7).getY();
assertThat("",var9,instanceOf(IdentExpr.class));
assertEquals("y", var9.getText());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
Expr var10= ((AssignmentStatement) var6).getExpr();
assertThat("",var10,instanceOf(ColorExpr.class));
Expr var11= ((ColorExpr) var10).getRed();
assertThat("",var11,instanceOf(BinaryExpr.class));
assertEquals(MINUS, ((BinaryExpr) var11).getOp().getKind());
Expr var12= ((BinaryExpr) var11).getLeft();
assertThat("",var12,instanceOf(IdentExpr.class));
assertEquals("x", var12.getText());
assertEquals(Type.INT,var12.getType());
assertThat(var12.getCoerceTo(),anyOf(nullValue(), is(var12.getType())));
Expr var13= ((BinaryExpr) var11).getRight();
assertThat("",var13,instanceOf(IdentExpr.class));
assertEquals("y", var13.getText());
assertEquals(Type.INT,var13.getType());
assertThat(var13.getCoerceTo(),anyOf(nullValue(), is(var13.getType())));
assertEquals(Type.INT,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
Expr var14= ((ColorExpr) var10).getGreen();
assertThat("",var14,instanceOf(BinaryExpr.class));
assertEquals(MINUS, ((BinaryExpr) var14).getOp().getKind());
Expr var15= ((BinaryExpr) var14).getLeft();
assertThat("",var15,instanceOf(IdentExpr.class));
assertEquals("x", var15.getText());
assertEquals(Type.INT,var15.getType());
assertThat(var15.getCoerceTo(),anyOf(nullValue(), is(var15.getType())));
Expr var16= ((BinaryExpr) var14).getRight();
assertThat("",var16,instanceOf(IdentExpr.class));
assertEquals("y", var16.getText());
assertEquals(Type.INT,var16.getType());
assertThat(var16.getCoerceTo(),anyOf(nullValue(), is(var16.getType())));
assertEquals(Type.INT,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
Expr var17= ((ColorExpr) var10).getBlue();
assertThat("",var17,instanceOf(BinaryExpr.class));
assertEquals(MINUS, ((BinaryExpr) var17).getOp().getKind());
Expr var18= ((BinaryExpr) var17).getLeft();
assertThat("",var18,instanceOf(IdentExpr.class));
assertEquals("x", var18.getText());
assertEquals(Type.INT,var18.getType());
assertThat(var18.getCoerceTo(),anyOf(nullValue(), is(var18.getType())));
Expr var19= ((BinaryExpr) var17).getRight();
assertThat("",var19,instanceOf(IdentExpr.class));
assertEquals("y", var19.getText());
assertEquals(Type.INT,var19.getType());
assertThat(var19.getCoerceTo(),anyOf(nullValue(), is(var19.getType())));
assertEquals(Type.INT,var17.getType());
assertThat(var17.getCoerceTo(),anyOf(nullValue(), is(var17.getType())));
assertEquals(Type.COLOR,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
ASTNode var20= decsAndStatements.get(2);
assertThat("",var20,instanceOf(ReturnStatement.class));
Expr var21= ((ReturnStatement) var20).getExpr();
assertThat("",var21,instanceOf(IdentExpr.class));
assertEquals("a", var21.getText());
assertEquals(Type.IMAGE,var21.getType());
assertThat(var21.getCoerceTo(),anyOf(nullValue(), is(var21.getType())));
});
}

@DisplayName("test030")
@Test public void test030(TestInfo testInfo) throws Exception{
String input = """
int f()
int a = 20;
int b = 4;
^ a/b;

""";
show("\n\n----- test030 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.INT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var1).getType());
assertEquals("a", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(IntLitExpr.class));
assertEquals(20, ((IntLitExpr) var2).getValue());
assertEquals(Type.INT,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(VarDeclaration.class));
NameDef var4= ((VarDeclaration) var3).getNameDef();
assertThat("",var4,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var4).getType());
assertEquals("b", ((NameDef) var4).getName());
Expr var5= ((VarDeclaration) var3).getExpr();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(4, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var3).getOp().getKind());
ASTNode var6= decsAndStatements.get(2);
assertThat("",var6,instanceOf(ReturnStatement.class));
Expr var7= ((ReturnStatement) var6).getExpr();
assertThat("",var7,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var7).getOp().getKind());
Expr var8= ((BinaryExpr) var7).getLeft();
assertThat("",var8,instanceOf(IdentExpr.class));
assertEquals("a", var8.getText());
assertEquals(Type.INT,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
Expr var9= ((BinaryExpr) var7).getRight();
assertThat("",var9,instanceOf(IdentExpr.class));
assertEquals("b", var9.getText());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
assertEquals(Type.INT,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
});
}

@DisplayName("test031")
@Test public void test031(TestInfo testInfo) throws Exception{
String input = """
void f()
int a;
a <- console;
write a+1 -> console;

""";
show("\n\n----- test031 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.VOID, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var1).getType());
assertEquals("a", ((NameDef) var1).getName());
ASTNode var2= decsAndStatements.get(1);
assertThat("",var2,instanceOf(ReadStatement.class));
assertEquals("a", ((ReadStatement)var2).getName());
assertNull(((ReadStatement) var2).getSelector());
Expr var3= ((ReadStatement) var2).getSource();
assertThat("",var3,instanceOf(ConsoleExpr.class));
assertEquals(Type.CONSOLE,var3.getType());
assertEquals(Type.INT,var3.getCoerceTo());
ASTNode var4= decsAndStatements.get(2);
assertThat("",var4,instanceOf(WriteStatement.class));
Expr var5= ((WriteStatement) var4).getSource();
assertThat("",var5,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var5).getOp().getKind());
Expr var6= ((BinaryExpr) var5).getLeft();
assertThat("",var6,instanceOf(IdentExpr.class));
assertEquals("a", var6.getText());
assertEquals(Type.INT,var6.getType());
assertThat(var6.getCoerceTo(),anyOf(nullValue(), is(var6.getType())));
Expr var7= ((BinaryExpr) var5).getRight();
assertThat("",var7,instanceOf(IntLitExpr.class));
assertEquals(1, ((IntLitExpr) var7).getValue());
assertEquals(Type.INT,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
Expr var8= ((WriteStatement) var4).getDest();
assertThat("",var8,instanceOf(ConsoleExpr.class));
assertEquals(Type.CONSOLE,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
});
}

@DisplayName("test032")
@Test public void test032(TestInfo testInfo) throws Exception{
String input = """
void withParams(int i, boolean b, float f, string s, image i)

""";
show("\n\n----- test032 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
Exception e = assertThrows(TypeCheckException.class, () -> {
checkTypes(ast);
});
show("Expected TypeCheckException:     " + e);
});
}

@DisplayName("test033")
@Test public void test033(TestInfo testInfo) throws Exception{
String input = """
int f(int a, int b)
int x = a+b;
return if (x > 0) a else b fi;

""";
show("\n\n----- test033 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
Exception e = assertThrows(SyntaxException.class, () -> {
@SuppressWarnings("unused")
ASTNode ast = getAST(input);
});
show("Expected SyntaxException:     " + e);
});
}

@DisplayName("test034")
@Test public void test034(TestInfo testInfo) throws Exception{
String input = """
int y() ^42;

""";
show("\n\n----- test034 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.INT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(ReturnStatement.class));
Expr var1= ((ReturnStatement) var0).getExpr();
assertThat("",var1,instanceOf(IntLitExpr.class));
assertEquals(42, ((IntLitExpr) var1).getValue());
assertEquals(Type.INT,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
});
}

@DisplayName("test035")
@Test public void test035(TestInfo testInfo) throws Exception{
String input = """
void v()

""";
show("\n\n----- test035 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.VOID, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(0, decsAndStatements.size());
});
}

@DisplayName("test036")
@Test public void test036(TestInfo testInfo) throws Exception{
String input = """
color f()
color a = <<50,60,70>>;
color b = <<13,14,15>>;
^ a*b;

""";
show("\n\n----- test036 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var1).getType());
assertEquals("a", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(ColorExpr.class));
Expr var3= ((ColorExpr) var2).getRed();
assertThat("",var3,instanceOf(IntLitExpr.class));
assertEquals(50, ((IntLitExpr) var3).getValue());
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
Expr var4= ((ColorExpr) var2).getGreen();
assertThat("",var4,instanceOf(IntLitExpr.class));
assertEquals(60, ((IntLitExpr) var4).getValue());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((ColorExpr) var2).getBlue();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(70, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(Type.COLOR,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var6= decsAndStatements.get(1);
assertThat("",var6,instanceOf(VarDeclaration.class));
NameDef var7= ((VarDeclaration) var6).getNameDef();
assertThat("",var7,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var7).getType());
assertEquals("b", ((NameDef) var7).getName());
Expr var8= ((VarDeclaration) var6).getExpr();
assertThat("",var8,instanceOf(ColorExpr.class));
Expr var9= ((ColorExpr) var8).getRed();
assertThat("",var9,instanceOf(IntLitExpr.class));
assertEquals(13, ((IntLitExpr) var9).getValue());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
Expr var10= ((ColorExpr) var8).getGreen();
assertThat("",var10,instanceOf(IntLitExpr.class));
assertEquals(14, ((IntLitExpr) var10).getValue());
assertEquals(Type.INT,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
Expr var11= ((ColorExpr) var8).getBlue();
assertThat("",var11,instanceOf(IntLitExpr.class));
assertEquals(15, ((IntLitExpr) var11).getValue());
assertEquals(Type.INT,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
assertEquals(Type.COLOR,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var6).getOp().getKind());
ASTNode var12= decsAndStatements.get(2);
assertThat("",var12,instanceOf(ReturnStatement.class));
Expr var13= ((ReturnStatement) var12).getExpr();
assertThat("",var13,instanceOf(BinaryExpr.class));
assertEquals(TIMES, ((BinaryExpr) var13).getOp().getKind());
Expr var14= ((BinaryExpr) var13).getLeft();
assertThat("",var14,instanceOf(IdentExpr.class));
assertEquals("a", var14.getText());
assertEquals(Type.COLOR,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
Expr var15= ((BinaryExpr) var13).getRight();
assertThat("",var15,instanceOf(IdentExpr.class));
assertEquals("b", var15.getText());
assertEquals(Type.COLOR,var15.getType());
assertThat(var15.getCoerceTo(),anyOf(nullValue(), is(var15.getType())));
assertEquals(Type.COLOR,var13.getType());
assertThat(var13.getCoerceTo(),anyOf(nullValue(), is(var13.getType())));
});
}

@DisplayName("test037")
@Test public void test037(TestInfo testInfo) throws Exception{
String input = """
void withParams(float x)

""";
show("\n\n----- test037 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.VOID, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(1, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.FLOAT, ((NameDef) var0).getType());
assertEquals("x", ((NameDef) var0).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(0, decsAndStatements.size());
});
}

@DisplayName("test038")
@Test public void test038(TestInfo testInfo) throws Exception{
String input = """
color f()
^ PINK;

""";
show("\n\n----- test038 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(ReturnStatement.class));
Expr var1= ((ReturnStatement) var0).getExpr();
assertThat("",var1,instanceOf(ColorConstExpr.class));
assertEquals("PINK", var1.getText());
assertEquals(Type.COLOR,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
});
}

@DisplayName("test039")
@Test public void test039(TestInfo testInfo) throws Exception{
String input = """
int f()
int a = 20;
int b = 6;
^ a%b;

""";
show("\n\n----- test039 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.INT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var1).getType());
assertEquals("a", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(IntLitExpr.class));
assertEquals(20, ((IntLitExpr) var2).getValue());
assertEquals(Type.INT,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(VarDeclaration.class));
NameDef var4= ((VarDeclaration) var3).getNameDef();
assertThat("",var4,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var4).getType());
assertEquals("b", ((NameDef) var4).getName());
Expr var5= ((VarDeclaration) var3).getExpr();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(6, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var3).getOp().getKind());
ASTNode var6= decsAndStatements.get(2);
assertThat("",var6,instanceOf(ReturnStatement.class));
Expr var7= ((ReturnStatement) var6).getExpr();
assertThat("",var7,instanceOf(BinaryExpr.class));
assertEquals(MOD, ((BinaryExpr) var7).getOp().getKind());
Expr var8= ((BinaryExpr) var7).getLeft();
assertThat("",var8,instanceOf(IdentExpr.class));
assertEquals("a", var8.getText());
assertEquals(Type.INT,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
Expr var9= ((BinaryExpr) var7).getRight();
assertThat("",var9,instanceOf(IdentExpr.class));
assertEquals("b", var9.getText());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
assertEquals(Type.INT,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
});
}

@DisplayName("test040")
@Test public void test040(TestInfo testInfo) throws Exception{
String input = """
void withParams(string x)

""";
show("\n\n----- test040 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.VOID, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(1, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.STRING, ((NameDef) var0).getType());
assertEquals("x", ((NameDef) var0).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(0, decsAndStatements.size());
});
}

@DisplayName("test041")
@Test public void test041(TestInfo testInfo) throws Exception{
String input = """
string testRead2()
string x;
x <- console;
^ x;

""";
show("\n\n----- test041 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.STRING, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.STRING, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
ASTNode var2= decsAndStatements.get(1);
assertThat("",var2,instanceOf(ReadStatement.class));
assertEquals("x", ((ReadStatement)var2).getName());
assertNull(((ReadStatement) var2).getSelector());
Expr var3= ((ReadStatement) var2).getSource();
assertThat("",var3,instanceOf(ConsoleExpr.class));
assertEquals(Type.CONSOLE,var3.getType());
assertEquals(Type.STRING,var3.getCoerceTo());
ASTNode var4= decsAndStatements.get(2);
assertThat("",var4,instanceOf(ReturnStatement.class));
Expr var5= ((ReturnStatement) var4).getExpr();
assertThat("",var5,instanceOf(IdentExpr.class));
assertEquals("x", var5.getText());
assertEquals(Type.STRING,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
});
}

@DisplayName("test042")
@Test public void test042(TestInfo testInfo) throws Exception{
String input = """
void withInitializedDecs()
int a = 0;

""";
show("\n\n----- test042 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.VOID, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var1).getType());
assertEquals("a", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(IntLitExpr.class));
assertEquals(0, ((IntLitExpr) var2).getValue());
assertEquals(Type.INT,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
});
}

@DisplayName("test043")
@Test public void test043(TestInfo testInfo) throws Exception{
String input = """
string y() ^ "hello";

""";
show("\n\n----- test043 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.STRING, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(ReturnStatement.class));
Expr var1= ((ReturnStatement) var0).getExpr();
assertThat("",var1,instanceOf(StringLitExpr.class));
assertEquals("hello", ((StringLitExpr) var1).getValue());
assertEquals(Type.STRING,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
});
}

@DisplayName("test044")
@Test public void test044(TestInfo testInfo) throws Exception{
String input = """
color f()
^ GREEN;

""";
show("\n\n----- test044 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(ReturnStatement.class));
Expr var1= ((ReturnStatement) var0).getExpr();
assertThat("",var1,instanceOf(ColorConstExpr.class));
assertEquals("GREEN", var1.getText());
assertEquals(Type.COLOR,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
});
}

@DisplayName("test045")
@Test public void test045(TestInfo testInfo) throws Exception{
String input = """
color f() color x = <<100,200,255>>; ^ x;

""";
show("\n\n----- test045 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(2, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(ColorExpr.class));
Expr var3= ((ColorExpr) var2).getRed();
assertThat("",var3,instanceOf(IntLitExpr.class));
assertEquals(100, ((IntLitExpr) var3).getValue());
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
Expr var4= ((ColorExpr) var2).getGreen();
assertThat("",var4,instanceOf(IntLitExpr.class));
assertEquals(200, ((IntLitExpr) var4).getValue());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((ColorExpr) var2).getBlue();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(255, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(Type.COLOR,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var6= decsAndStatements.get(1);
assertThat("",var6,instanceOf(ReturnStatement.class));
Expr var7= ((ReturnStatement) var6).getExpr();
assertThat("",var7,instanceOf(IdentExpr.class));
assertEquals("x", var7.getText());
assertEquals(Type.COLOR,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
});
}

@DisplayName("test046")
@Test public void test046(TestInfo testInfo) throws Exception{
String input = """
int f(int x)
int y;
^ y;

""";
show("\n\n----- test046 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
Exception e = assertThrows(TypeCheckException.class, () -> {
checkTypes(ast);
});
show("Expected TypeCheckException:     " + e);
});
}

@DisplayName("test047")
@Test public void test047(TestInfo testInfo) throws Exception{
String input = """
int f(int x)
int y;
y = x+2;
^y;

""";
show("\n\n----- test047 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.INT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(1, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var0).getType());
assertEquals("x", ((NameDef) var0).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var1= decsAndStatements.get(0);
assertThat("",var1,instanceOf(VarDeclaration.class));
NameDef var2= ((VarDeclaration) var1).getNameDef();
assertThat("",var2,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var2).getType());
assertEquals("y", ((NameDef) var2).getName());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(AssignmentStatement.class));
assertEquals("y", ((AssignmentStatement)var3).getName());
assertNull(((AssignmentStatement) var3).getSelector());
Expr var4= ((AssignmentStatement) var3).getExpr();
assertThat("",var4,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var4).getOp().getKind());
Expr var5= ((BinaryExpr) var4).getLeft();
assertThat("",var5,instanceOf(IdentExpr.class));
assertEquals("x", var5.getText());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
Expr var6= ((BinaryExpr) var4).getRight();
assertThat("",var6,instanceOf(IntLitExpr.class));
assertEquals(2, ((IntLitExpr) var6).getValue());
assertEquals(Type.INT,var6.getType());
assertThat(var6.getCoerceTo(),anyOf(nullValue(), is(var6.getType())));
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
ASTNode var7= decsAndStatements.get(2);
assertThat("",var7,instanceOf(ReturnStatement.class));
Expr var8= ((ReturnStatement) var7).getExpr();
assertThat("",var8,instanceOf(IdentExpr.class));
assertEquals("y", var8.getText());
assertEquals(Type.INT,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
});
}

@DisplayName("test048")
@Test public void test048(TestInfo testInfo) throws Exception{
String input = """
int f(int a, int b)
int x;
x = a+b;
return x;

""";
show("\n\n----- test048 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
Exception e = assertThrows(SyntaxException.class, () -> {
@SuppressWarnings("unused")
ASTNode ast = getAST(input);
});
show("Expected SyntaxException:     " + e);
});
}

@DisplayName("test049")
@Test public void test049(TestInfo testInfo) throws Exception{
String input = """
string f()
return "hello,world";

""";
show("\n\n----- test049 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
Exception e = assertThrows(SyntaxException.class, () -> {
@SuppressWarnings("unused")
ASTNode ast = getAST(input);
});
show("Expected SyntaxException:     " + e);
});
}

@DisplayName("test050")
@Test public void test050(TestInfo testInfo) throws Exception{
String input = """
color f()
color two = <<2,2,2>>;
write "two = \n" -> console;
write two -> console;
color div = <<50,60,70>> /two;
write "div = " -> console;
write div -> console;
^ div;

""";
show("\n\n----- test050 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(7, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var1).getType());
assertEquals("two", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(ColorExpr.class));
Expr var3= ((ColorExpr) var2).getRed();
assertThat("",var3,instanceOf(IntLitExpr.class));
assertEquals(2, ((IntLitExpr) var3).getValue());
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
Expr var4= ((ColorExpr) var2).getGreen();
assertThat("",var4,instanceOf(IntLitExpr.class));
assertEquals(2, ((IntLitExpr) var4).getValue());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((ColorExpr) var2).getBlue();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(2, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(Type.COLOR,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var6= decsAndStatements.get(1);
assertThat("",var6,instanceOf(WriteStatement.class));
Expr var7= ((WriteStatement) var6).getSource();
assertThat("",var7,instanceOf(StringLitExpr.class));
assertEquals("two = \n", ((StringLitExpr) var7).getValue());
assertEquals(Type.STRING,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
Expr var8= ((WriteStatement) var6).getDest();
assertThat("",var8,instanceOf(ConsoleExpr.class));
assertEquals(Type.CONSOLE,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
ASTNode var9= decsAndStatements.get(2);
assertThat("",var9,instanceOf(WriteStatement.class));
Expr var10= ((WriteStatement) var9).getSource();
assertThat("",var10,instanceOf(IdentExpr.class));
assertEquals("two", var10.getText());
assertEquals(Type.COLOR,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
Expr var11= ((WriteStatement) var9).getDest();
assertThat("",var11,instanceOf(ConsoleExpr.class));
assertEquals(Type.CONSOLE,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
ASTNode var12= decsAndStatements.get(3);
assertThat("",var12,instanceOf(VarDeclaration.class));
NameDef var13= ((VarDeclaration) var12).getNameDef();
assertThat("",var13,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var13).getType());
assertEquals("div", ((NameDef) var13).getName());
Expr var14= ((VarDeclaration) var12).getExpr();
assertThat("",var14,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var14).getOp().getKind());
Expr var15= ((BinaryExpr) var14).getLeft();
assertThat("",var15,instanceOf(ColorExpr.class));
Expr var16= ((ColorExpr) var15).getRed();
assertThat("",var16,instanceOf(IntLitExpr.class));
assertEquals(50, ((IntLitExpr) var16).getValue());
assertEquals(Type.INT,var16.getType());
assertThat(var16.getCoerceTo(),anyOf(nullValue(), is(var16.getType())));
Expr var17= ((ColorExpr) var15).getGreen();
assertThat("",var17,instanceOf(IntLitExpr.class));
assertEquals(60, ((IntLitExpr) var17).getValue());
assertEquals(Type.INT,var17.getType());
assertThat(var17.getCoerceTo(),anyOf(nullValue(), is(var17.getType())));
Expr var18= ((ColorExpr) var15).getBlue();
assertThat("",var18,instanceOf(IntLitExpr.class));
assertEquals(70, ((IntLitExpr) var18).getValue());
assertEquals(Type.INT,var18.getType());
assertThat(var18.getCoerceTo(),anyOf(nullValue(), is(var18.getType())));
assertEquals(Type.COLOR,var15.getType());
assertThat(var15.getCoerceTo(),anyOf(nullValue(), is(var15.getType())));
Expr var19= ((BinaryExpr) var14).getRight();
assertThat("",var19,instanceOf(IdentExpr.class));
assertEquals("two", var19.getText());
assertEquals(Type.COLOR,var19.getType());
assertThat(var19.getCoerceTo(),anyOf(nullValue(), is(var19.getType())));
assertEquals(Type.COLOR,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var12).getOp().getKind());
ASTNode var20= decsAndStatements.get(4);
assertThat("",var20,instanceOf(WriteStatement.class));
Expr var21= ((WriteStatement) var20).getSource();
assertThat("",var21,instanceOf(StringLitExpr.class));
assertEquals("div = ", ((StringLitExpr) var21).getValue());
assertEquals(Type.STRING,var21.getType());
assertThat(var21.getCoerceTo(),anyOf(nullValue(), is(var21.getType())));
Expr var22= ((WriteStatement) var20).getDest();
assertThat("",var22,instanceOf(ConsoleExpr.class));
assertEquals(Type.CONSOLE,var22.getType());
assertThat(var22.getCoerceTo(),anyOf(nullValue(), is(var22.getType())));
ASTNode var23= decsAndStatements.get(5);
assertThat("",var23,instanceOf(WriteStatement.class));
Expr var24= ((WriteStatement) var23).getSource();
assertThat("",var24,instanceOf(IdentExpr.class));
assertEquals("div", var24.getText());
assertEquals(Type.COLOR,var24.getType());
assertThat(var24.getCoerceTo(),anyOf(nullValue(), is(var24.getType())));
Expr var25= ((WriteStatement) var23).getDest();
assertThat("",var25,instanceOf(ConsoleExpr.class));
assertEquals(Type.CONSOLE,var25.getType());
assertThat(var25.getCoerceTo(),anyOf(nullValue(), is(var25.getType())));
ASTNode var26= decsAndStatements.get(6);
assertThat("",var26,instanceOf(ReturnStatement.class));
Expr var27= ((ReturnStatement) var26).getExpr();
assertThat("",var27,instanceOf(IdentExpr.class));
assertEquals("div", var27.getText());
assertEquals(Type.COLOR,var27.getType());
assertThat(var27.getCoerceTo(),anyOf(nullValue(), is(var27.getType())));
});
}

@DisplayName("test051")
@Test public void test051(TestInfo testInfo) throws Exception{
String input = """
void withUninitializedDecs()
float b;

""";
show("\n\n----- test051 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.VOID, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.FLOAT, ((NameDef) var1).getType());
assertEquals("b", ((NameDef) var1).getName());
});
}

@DisplayName("test052")
@Test public void test052(TestInfo testInfo) throws Exception{
String input = """
color f()
color a = <<50,60,70>>;
color b = <<13,14,15>>;
color c = <<2,3,4>>;
^ a+b*c;

""";
show("\n\n----- test052 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(4, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var1).getType());
assertEquals("a", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(ColorExpr.class));
Expr var3= ((ColorExpr) var2).getRed();
assertThat("",var3,instanceOf(IntLitExpr.class));
assertEquals(50, ((IntLitExpr) var3).getValue());
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
Expr var4= ((ColorExpr) var2).getGreen();
assertThat("",var4,instanceOf(IntLitExpr.class));
assertEquals(60, ((IntLitExpr) var4).getValue());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((ColorExpr) var2).getBlue();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(70, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(Type.COLOR,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var6= decsAndStatements.get(1);
assertThat("",var6,instanceOf(VarDeclaration.class));
NameDef var7= ((VarDeclaration) var6).getNameDef();
assertThat("",var7,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var7).getType());
assertEquals("b", ((NameDef) var7).getName());
Expr var8= ((VarDeclaration) var6).getExpr();
assertThat("",var8,instanceOf(ColorExpr.class));
Expr var9= ((ColorExpr) var8).getRed();
assertThat("",var9,instanceOf(IntLitExpr.class));
assertEquals(13, ((IntLitExpr) var9).getValue());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
Expr var10= ((ColorExpr) var8).getGreen();
assertThat("",var10,instanceOf(IntLitExpr.class));
assertEquals(14, ((IntLitExpr) var10).getValue());
assertEquals(Type.INT,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
Expr var11= ((ColorExpr) var8).getBlue();
assertThat("",var11,instanceOf(IntLitExpr.class));
assertEquals(15, ((IntLitExpr) var11).getValue());
assertEquals(Type.INT,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
assertEquals(Type.COLOR,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var6).getOp().getKind());
ASTNode var12= decsAndStatements.get(2);
assertThat("",var12,instanceOf(VarDeclaration.class));
NameDef var13= ((VarDeclaration) var12).getNameDef();
assertThat("",var13,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var13).getType());
assertEquals("c", ((NameDef) var13).getName());
Expr var14= ((VarDeclaration) var12).getExpr();
assertThat("",var14,instanceOf(ColorExpr.class));
Expr var15= ((ColorExpr) var14).getRed();
assertThat("",var15,instanceOf(IntLitExpr.class));
assertEquals(2, ((IntLitExpr) var15).getValue());
assertEquals(Type.INT,var15.getType());
assertThat(var15.getCoerceTo(),anyOf(nullValue(), is(var15.getType())));
Expr var16= ((ColorExpr) var14).getGreen();
assertThat("",var16,instanceOf(IntLitExpr.class));
assertEquals(3, ((IntLitExpr) var16).getValue());
assertEquals(Type.INT,var16.getType());
assertThat(var16.getCoerceTo(),anyOf(nullValue(), is(var16.getType())));
Expr var17= ((ColorExpr) var14).getBlue();
assertThat("",var17,instanceOf(IntLitExpr.class));
assertEquals(4, ((IntLitExpr) var17).getValue());
assertEquals(Type.INT,var17.getType());
assertThat(var17.getCoerceTo(),anyOf(nullValue(), is(var17.getType())));
assertEquals(Type.COLOR,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var12).getOp().getKind());
ASTNode var18= decsAndStatements.get(3);
assertThat("",var18,instanceOf(ReturnStatement.class));
Expr var19= ((ReturnStatement) var18).getExpr();
assertThat("",var19,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var19).getOp().getKind());
Expr var20= ((BinaryExpr) var19).getLeft();
assertThat("",var20,instanceOf(IdentExpr.class));
assertEquals("a", var20.getText());
assertEquals(Type.COLOR,var20.getType());
assertThat(var20.getCoerceTo(),anyOf(nullValue(), is(var20.getType())));
Expr var21= ((BinaryExpr) var19).getRight();
assertThat("",var21,instanceOf(BinaryExpr.class));
assertEquals(TIMES, ((BinaryExpr) var21).getOp().getKind());
Expr var22= ((BinaryExpr) var21).getLeft();
assertThat("",var22,instanceOf(IdentExpr.class));
assertEquals("b", var22.getText());
assertEquals(Type.COLOR,var22.getType());
assertThat(var22.getCoerceTo(),anyOf(nullValue(), is(var22.getType())));
Expr var23= ((BinaryExpr) var21).getRight();
assertThat("",var23,instanceOf(IdentExpr.class));
assertEquals("c", var23.getText());
assertEquals(Type.COLOR,var23.getType());
assertThat(var23.getCoerceTo(),anyOf(nullValue(), is(var23.getType())));
assertEquals(Type.COLOR,var21.getType());
assertThat(var21.getCoerceTo(),anyOf(nullValue(), is(var21.getType())));
assertEquals(Type.COLOR,var19.getType());
assertThat(var19.getCoerceTo(),anyOf(nullValue(), is(var19.getType())));
});
}

@DisplayName("test053")
@Test public void test053(TestInfo testInfo) throws Exception{
String input = """
color testWriteReadColor0()
color x = <<128,129,130>>;
write x -> "testWriteReadColor0";
color s <- "testWriteReadColor0";
^ s;

""";
show("\n\n----- test053 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(4, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(ColorExpr.class));
Expr var3= ((ColorExpr) var2).getRed();
assertThat("",var3,instanceOf(IntLitExpr.class));
assertEquals(128, ((IntLitExpr) var3).getValue());
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
Expr var4= ((ColorExpr) var2).getGreen();
assertThat("",var4,instanceOf(IntLitExpr.class));
assertEquals(129, ((IntLitExpr) var4).getValue());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((ColorExpr) var2).getBlue();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(130, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(Type.COLOR,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var6= decsAndStatements.get(1);
assertThat("",var6,instanceOf(WriteStatement.class));
Expr var7= ((WriteStatement) var6).getSource();
assertThat("",var7,instanceOf(IdentExpr.class));
assertEquals("x", var7.getText());
assertEquals(Type.COLOR,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
Expr var8= ((WriteStatement) var6).getDest();
assertThat("",var8,instanceOf(StringLitExpr.class));
assertEquals("testWriteReadColor0", ((StringLitExpr) var8).getValue());
assertEquals(Type.STRING,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
ASTNode var9= decsAndStatements.get(2);
assertThat("",var9,instanceOf(VarDeclaration.class));
NameDef var10= ((VarDeclaration) var9).getNameDef();
assertThat("",var10,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var10).getType());
assertEquals("s", ((NameDef) var10).getName());
Expr var11= ((VarDeclaration) var9).getExpr();
assertThat("",var11,instanceOf(StringLitExpr.class));
assertEquals("testWriteReadColor0", ((StringLitExpr) var11).getValue());
assertEquals(Type.STRING,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
assertEquals(LARROW, ((VarDeclaration) var9).getOp().getKind());
ASTNode var12= decsAndStatements.get(3);
assertThat("",var12,instanceOf(ReturnStatement.class));
Expr var13= ((ReturnStatement) var12).getExpr();
assertThat("",var13,instanceOf(IdentExpr.class));
assertEquals("s", var13.getText());
assertEquals(Type.COLOR,var13.getType());
assertThat(var13.getCoerceTo(),anyOf(nullValue(), is(var13.getType())));
});
}

@DisplayName("test054")
@Test public void test054(TestInfo testInfo) throws Exception{
String input = """
void BDP0()
int Z = 255;
image[1024,1024] a;
int s = 16;
a[x,y] = <<(x/s)%((y/s)+1), (x/s)%((y/s)+1), (x/s)%((y/s)+1)>>*5;
write a -> console;

""";
show("\n\n----- test054 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.VOID, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(5, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var1).getType());
assertEquals("Z", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(IntLitExpr.class));
assertEquals(255, ((IntLitExpr) var2).getValue());
assertEquals(Type.INT,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(VarDeclaration.class));
NameDef var4= ((VarDeclaration) var3).getNameDef();
assertThat("",var4,instanceOf(NameDef.class));
assertEquals(Type.IMAGE, ((NameDef) var4).getType());
assertEquals("a", ((NameDef) var4).getName());
Dimension var5= ((NameDefWithDim) var4).getDim();
assertThat("",var5,instanceOf(Dimension.class));
Expr var6= ((Dimension) var5).getWidth();
assertThat("",var6,instanceOf(IntLitExpr.class));
assertEquals(1024, ((IntLitExpr) var6).getValue());
assertEquals(Type.INT,var6.getType());
assertThat(var6.getCoerceTo(),anyOf(nullValue(), is(var6.getType())));
Expr var7= ((Dimension) var5).getHeight();
assertThat("",var7,instanceOf(IntLitExpr.class));
assertEquals(1024, ((IntLitExpr) var7).getValue());
assertEquals(Type.INT,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
ASTNode var8= decsAndStatements.get(2);
assertThat("",var8,instanceOf(VarDeclaration.class));
NameDef var9= ((VarDeclaration) var8).getNameDef();
assertThat("",var9,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var9).getType());
assertEquals("s", ((NameDef) var9).getName());
Expr var10= ((VarDeclaration) var8).getExpr();
assertThat("",var10,instanceOf(IntLitExpr.class));
assertEquals(16, ((IntLitExpr) var10).getValue());
assertEquals(Type.INT,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var8).getOp().getKind());
ASTNode var11= decsAndStatements.get(3);
assertThat("",var11,instanceOf(AssignmentStatement.class));
assertEquals("a", ((AssignmentStatement)var11).getName());
PixelSelector var12= ((AssignmentStatement) var11).getSelector();
assertThat("",var12,instanceOf(PixelSelector.class));
Expr var13= ((PixelSelector) var12).getX();
assertThat("",var13,instanceOf(IdentExpr.class));
assertEquals("x", var13.getText());
assertEquals(Type.INT,var13.getType());
assertThat(var13.getCoerceTo(),anyOf(nullValue(), is(var13.getType())));
Expr var14= ((PixelSelector) var12).getY();
assertThat("",var14,instanceOf(IdentExpr.class));
assertEquals("y", var14.getText());
assertEquals(Type.INT,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
Expr var15= ((AssignmentStatement) var11).getExpr();
assertThat("",var15,instanceOf(BinaryExpr.class));
assertEquals(TIMES, ((BinaryExpr) var15).getOp().getKind());
Expr var16= ((BinaryExpr) var15).getLeft();
assertThat("",var16,instanceOf(ColorExpr.class));
Expr var17= ((ColorExpr) var16).getRed();
assertThat("",var17,instanceOf(BinaryExpr.class));
assertEquals(MOD, ((BinaryExpr) var17).getOp().getKind());
Expr var18= ((BinaryExpr) var17).getLeft();
assertThat("",var18,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var18).getOp().getKind());
Expr var19= ((BinaryExpr) var18).getLeft();
assertThat("",var19,instanceOf(IdentExpr.class));
assertEquals("x", var19.getText());
assertEquals(Type.INT,var19.getType());
assertThat(var19.getCoerceTo(),anyOf(nullValue(), is(var19.getType())));
Expr var20= ((BinaryExpr) var18).getRight();
assertThat("",var20,instanceOf(IdentExpr.class));
assertEquals("s", var20.getText());
assertEquals(Type.INT,var20.getType());
assertThat(var20.getCoerceTo(),anyOf(nullValue(), is(var20.getType())));
assertEquals(Type.INT,var18.getType());
assertThat(var18.getCoerceTo(),anyOf(nullValue(), is(var18.getType())));
Expr var21= ((BinaryExpr) var17).getRight();
assertThat("",var21,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var21).getOp().getKind());
Expr var22= ((BinaryExpr) var21).getLeft();
assertThat("",var22,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var22).getOp().getKind());
Expr var23= ((BinaryExpr) var22).getLeft();
assertThat("",var23,instanceOf(IdentExpr.class));
assertEquals("y", var23.getText());
assertEquals(Type.INT,var23.getType());
assertThat(var23.getCoerceTo(),anyOf(nullValue(), is(var23.getType())));
Expr var24= ((BinaryExpr) var22).getRight();
assertThat("",var24,instanceOf(IdentExpr.class));
assertEquals("s", var24.getText());
assertEquals(Type.INT,var24.getType());
assertThat(var24.getCoerceTo(),anyOf(nullValue(), is(var24.getType())));
assertEquals(Type.INT,var22.getType());
assertThat(var22.getCoerceTo(),anyOf(nullValue(), is(var22.getType())));
Expr var25= ((BinaryExpr) var21).getRight();
assertThat("",var25,instanceOf(IntLitExpr.class));
assertEquals(1, ((IntLitExpr) var25).getValue());
assertEquals(Type.INT,var25.getType());
assertThat(var25.getCoerceTo(),anyOf(nullValue(), is(var25.getType())));
assertEquals(Type.INT,var21.getType());
assertThat(var21.getCoerceTo(),anyOf(nullValue(), is(var21.getType())));
assertEquals(Type.INT,var17.getType());
assertThat(var17.getCoerceTo(),anyOf(nullValue(), is(var17.getType())));
Expr var26= ((ColorExpr) var16).getGreen();
assertThat("",var26,instanceOf(BinaryExpr.class));
assertEquals(MOD, ((BinaryExpr) var26).getOp().getKind());
Expr var27= ((BinaryExpr) var26).getLeft();
assertThat("",var27,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var27).getOp().getKind());
Expr var28= ((BinaryExpr) var27).getLeft();
assertThat("",var28,instanceOf(IdentExpr.class));
assertEquals("x", var28.getText());
assertEquals(Type.INT,var28.getType());
assertThat(var28.getCoerceTo(),anyOf(nullValue(), is(var28.getType())));
Expr var29= ((BinaryExpr) var27).getRight();
assertThat("",var29,instanceOf(IdentExpr.class));
assertEquals("s", var29.getText());
assertEquals(Type.INT,var29.getType());
assertThat(var29.getCoerceTo(),anyOf(nullValue(), is(var29.getType())));
assertEquals(Type.INT,var27.getType());
assertThat(var27.getCoerceTo(),anyOf(nullValue(), is(var27.getType())));
Expr var30= ((BinaryExpr) var26).getRight();
assertThat("",var30,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var30).getOp().getKind());
Expr var31= ((BinaryExpr) var30).getLeft();
assertThat("",var31,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var31).getOp().getKind());
Expr var32= ((BinaryExpr) var31).getLeft();
assertThat("",var32,instanceOf(IdentExpr.class));
assertEquals("y", var32.getText());
assertEquals(Type.INT,var32.getType());
assertThat(var32.getCoerceTo(),anyOf(nullValue(), is(var32.getType())));
Expr var33= ((BinaryExpr) var31).getRight();
assertThat("",var33,instanceOf(IdentExpr.class));
assertEquals("s", var33.getText());
assertEquals(Type.INT,var33.getType());
assertThat(var33.getCoerceTo(),anyOf(nullValue(), is(var33.getType())));
assertEquals(Type.INT,var31.getType());
assertThat(var31.getCoerceTo(),anyOf(nullValue(), is(var31.getType())));
Expr var34= ((BinaryExpr) var30).getRight();
assertThat("",var34,instanceOf(IntLitExpr.class));
assertEquals(1, ((IntLitExpr) var34).getValue());
assertEquals(Type.INT,var34.getType());
assertThat(var34.getCoerceTo(),anyOf(nullValue(), is(var34.getType())));
assertEquals(Type.INT,var30.getType());
assertThat(var30.getCoerceTo(),anyOf(nullValue(), is(var30.getType())));
assertEquals(Type.INT,var26.getType());
assertThat(var26.getCoerceTo(),anyOf(nullValue(), is(var26.getType())));
Expr var35= ((ColorExpr) var16).getBlue();
assertThat("",var35,instanceOf(BinaryExpr.class));
assertEquals(MOD, ((BinaryExpr) var35).getOp().getKind());
Expr var36= ((BinaryExpr) var35).getLeft();
assertThat("",var36,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var36).getOp().getKind());
Expr var37= ((BinaryExpr) var36).getLeft();
assertThat("",var37,instanceOf(IdentExpr.class));
assertEquals("x", var37.getText());
assertEquals(Type.INT,var37.getType());
assertThat(var37.getCoerceTo(),anyOf(nullValue(), is(var37.getType())));
Expr var38= ((BinaryExpr) var36).getRight();
assertThat("",var38,instanceOf(IdentExpr.class));
assertEquals("s", var38.getText());
assertEquals(Type.INT,var38.getType());
assertThat(var38.getCoerceTo(),anyOf(nullValue(), is(var38.getType())));
assertEquals(Type.INT,var36.getType());
assertThat(var36.getCoerceTo(),anyOf(nullValue(), is(var36.getType())));
Expr var39= ((BinaryExpr) var35).getRight();
assertThat("",var39,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var39).getOp().getKind());
Expr var40= ((BinaryExpr) var39).getLeft();
assertThat("",var40,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var40).getOp().getKind());
Expr var41= ((BinaryExpr) var40).getLeft();
assertThat("",var41,instanceOf(IdentExpr.class));
assertEquals("y", var41.getText());
assertEquals(Type.INT,var41.getType());
assertThat(var41.getCoerceTo(),anyOf(nullValue(), is(var41.getType())));
Expr var42= ((BinaryExpr) var40).getRight();
assertThat("",var42,instanceOf(IdentExpr.class));
assertEquals("s", var42.getText());
assertEquals(Type.INT,var42.getType());
assertThat(var42.getCoerceTo(),anyOf(nullValue(), is(var42.getType())));
assertEquals(Type.INT,var40.getType());
assertThat(var40.getCoerceTo(),anyOf(nullValue(), is(var40.getType())));
Expr var43= ((BinaryExpr) var39).getRight();
assertThat("",var43,instanceOf(IntLitExpr.class));
assertEquals(1, ((IntLitExpr) var43).getValue());
assertEquals(Type.INT,var43.getType());
assertThat(var43.getCoerceTo(),anyOf(nullValue(), is(var43.getType())));
assertEquals(Type.INT,var39.getType());
assertThat(var39.getCoerceTo(),anyOf(nullValue(), is(var39.getType())));
assertEquals(Type.INT,var35.getType());
assertThat(var35.getCoerceTo(),anyOf(nullValue(), is(var35.getType())));
assertEquals(Type.COLOR,var16.getType());
assertThat(var16.getCoerceTo(),anyOf(nullValue(), is(var16.getType())));
Expr var44= ((BinaryExpr) var15).getRight();
assertThat("",var44,instanceOf(IntLitExpr.class));
assertEquals(5, ((IntLitExpr) var44).getValue());
assertEquals(Type.INT,var44.getType());
assertEquals(Type.COLOR,var44.getCoerceTo());
assertEquals(Type.COLOR,var15.getType());
assertThat(var15.getCoerceTo(),anyOf(nullValue(), is(var15.getType())));
ASTNode var45= decsAndStatements.get(4);
assertThat("",var45,instanceOf(WriteStatement.class));
Expr var46= ((WriteStatement) var45).getSource();
assertThat("",var46,instanceOf(IdentExpr.class));
assertEquals("a", var46.getText());
assertEquals(Type.IMAGE,var46.getType());
assertThat(var46.getCoerceTo(),anyOf(nullValue(), is(var46.getType())));
Expr var47= ((WriteStatement) var45).getDest();
assertThat("",var47,instanceOf(ConsoleExpr.class));
assertEquals(Type.CONSOLE,var47.getType());
assertThat(var47.getCoerceTo(),anyOf(nullValue(), is(var47.getType())));
});
}

@DisplayName("test055")
@Test public void test055(TestInfo testInfo) throws Exception{
String input = """
boolean f() boolean x = true; ^ x;

""";
show("\n\n----- test055 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.BOOLEAN, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(2, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.BOOLEAN, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(BooleanLitExpr.class));
assertTrue(((BooleanLitExpr) var2).getValue());
assertEquals(Type.BOOLEAN,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(ReturnStatement.class));
Expr var4= ((ReturnStatement) var3).getExpr();
assertThat("",var4,instanceOf(IdentExpr.class));
assertEquals("x", var4.getText());
assertEquals(Type.BOOLEAN,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
});
}

@DisplayName("test056")
@Test public void test056(TestInfo testInfo) throws Exception{
String input = """
void withUninitializedDecs()
color e;

""";
show("\n\n----- test056 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.VOID, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var1).getType());
assertEquals("e", ((NameDef) var1).getName());
});
}

@DisplayName("test057")
@Test public void test057(TestInfo testInfo) throws Exception{
String input = """
float f() float x; x = 56.67; ^ x;

""";
show("\n\n----- test057 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.FLOAT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.FLOAT, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
ASTNode var2= decsAndStatements.get(1);
assertThat("",var2,instanceOf(AssignmentStatement.class));
assertEquals("x", ((AssignmentStatement)var2).getName());
assertNull(((AssignmentStatement) var2).getSelector());
Expr var3= ((AssignmentStatement) var2).getExpr();
assertThat("",var3,instanceOf(FloatLitExpr.class));
assertEquals(56.67f, ((FloatLitExpr) var3).getValue());
assertEquals(Type.FLOAT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
ASTNode var4= decsAndStatements.get(2);
assertThat("",var4,instanceOf(ReturnStatement.class));
Expr var5= ((ReturnStatement) var4).getExpr();
assertThat("",var5,instanceOf(IdentExpr.class));
assertEquals("x", var5.getText());
assertEquals(Type.FLOAT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
});
}

@DisplayName("test058")
@Test public void test058(TestInfo testInfo) throws Exception{
String input = """
color f()
color a = <<50,60,70>>;
color b = <<13,14,15>>;
color c = <<2,3,4>>;
^ (a+b)*c;

""";
show("\n\n----- test058 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(4, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var1).getType());
assertEquals("a", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(ColorExpr.class));
Expr var3= ((ColorExpr) var2).getRed();
assertThat("",var3,instanceOf(IntLitExpr.class));
assertEquals(50, ((IntLitExpr) var3).getValue());
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
Expr var4= ((ColorExpr) var2).getGreen();
assertThat("",var4,instanceOf(IntLitExpr.class));
assertEquals(60, ((IntLitExpr) var4).getValue());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((ColorExpr) var2).getBlue();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(70, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(Type.COLOR,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var6= decsAndStatements.get(1);
assertThat("",var6,instanceOf(VarDeclaration.class));
NameDef var7= ((VarDeclaration) var6).getNameDef();
assertThat("",var7,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var7).getType());
assertEquals("b", ((NameDef) var7).getName());
Expr var8= ((VarDeclaration) var6).getExpr();
assertThat("",var8,instanceOf(ColorExpr.class));
Expr var9= ((ColorExpr) var8).getRed();
assertThat("",var9,instanceOf(IntLitExpr.class));
assertEquals(13, ((IntLitExpr) var9).getValue());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
Expr var10= ((ColorExpr) var8).getGreen();
assertThat("",var10,instanceOf(IntLitExpr.class));
assertEquals(14, ((IntLitExpr) var10).getValue());
assertEquals(Type.INT,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
Expr var11= ((ColorExpr) var8).getBlue();
assertThat("",var11,instanceOf(IntLitExpr.class));
assertEquals(15, ((IntLitExpr) var11).getValue());
assertEquals(Type.INT,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
assertEquals(Type.COLOR,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var6).getOp().getKind());
ASTNode var12= decsAndStatements.get(2);
assertThat("",var12,instanceOf(VarDeclaration.class));
NameDef var13= ((VarDeclaration) var12).getNameDef();
assertThat("",var13,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var13).getType());
assertEquals("c", ((NameDef) var13).getName());
Expr var14= ((VarDeclaration) var12).getExpr();
assertThat("",var14,instanceOf(ColorExpr.class));
Expr var15= ((ColorExpr) var14).getRed();
assertThat("",var15,instanceOf(IntLitExpr.class));
assertEquals(2, ((IntLitExpr) var15).getValue());
assertEquals(Type.INT,var15.getType());
assertThat(var15.getCoerceTo(),anyOf(nullValue(), is(var15.getType())));
Expr var16= ((ColorExpr) var14).getGreen();
assertThat("",var16,instanceOf(IntLitExpr.class));
assertEquals(3, ((IntLitExpr) var16).getValue());
assertEquals(Type.INT,var16.getType());
assertThat(var16.getCoerceTo(),anyOf(nullValue(), is(var16.getType())));
Expr var17= ((ColorExpr) var14).getBlue();
assertThat("",var17,instanceOf(IntLitExpr.class));
assertEquals(4, ((IntLitExpr) var17).getValue());
assertEquals(Type.INT,var17.getType());
assertThat(var17.getCoerceTo(),anyOf(nullValue(), is(var17.getType())));
assertEquals(Type.COLOR,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var12).getOp().getKind());
ASTNode var18= decsAndStatements.get(3);
assertThat("",var18,instanceOf(ReturnStatement.class));
Expr var19= ((ReturnStatement) var18).getExpr();
assertThat("",var19,instanceOf(BinaryExpr.class));
assertEquals(TIMES, ((BinaryExpr) var19).getOp().getKind());
Expr var20= ((BinaryExpr) var19).getLeft();
assertThat("",var20,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var20).getOp().getKind());
Expr var21= ((BinaryExpr) var20).getLeft();
assertThat("",var21,instanceOf(IdentExpr.class));
assertEquals("a", var21.getText());
assertEquals(Type.COLOR,var21.getType());
assertThat(var21.getCoerceTo(),anyOf(nullValue(), is(var21.getType())));
Expr var22= ((BinaryExpr) var20).getRight();
assertThat("",var22,instanceOf(IdentExpr.class));
assertEquals("b", var22.getText());
assertEquals(Type.COLOR,var22.getType());
assertThat(var22.getCoerceTo(),anyOf(nullValue(), is(var22.getType())));
assertEquals(Type.COLOR,var20.getType());
assertThat(var20.getCoerceTo(),anyOf(nullValue(), is(var20.getType())));
Expr var23= ((BinaryExpr) var19).getRight();
assertThat("",var23,instanceOf(IdentExpr.class));
assertEquals("c", var23.getText());
assertEquals(Type.COLOR,var23.getType());
assertThat(var23.getCoerceTo(),anyOf(nullValue(), is(var23.getType())));
assertEquals(Type.COLOR,var19.getType());
assertThat(var19.getCoerceTo(),anyOf(nullValue(), is(var19.getType())));
});
}

@DisplayName("test059")
@Test public void test059(TestInfo testInfo) throws Exception{
String input = """
void withInitializedDecs()
float b = 10.01;

""";
show("\n\n----- test059 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.VOID, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.FLOAT, ((NameDef) var1).getType());
assertEquals("b", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(FloatLitExpr.class));
assertEquals(10.01f, ((FloatLitExpr) var2).getValue());
assertEquals(Type.FLOAT,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
});
}

@DisplayName("test060")
@Test public void test060(TestInfo testInfo) throws Exception{
String input = """
string withUninitializedDecs()
int a;
float b;
image c;
string e;
boolean f;
color e;

""";
show("\n\n----- test060 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
Exception e = assertThrows(TypeCheckException.class, () -> {
checkTypes(ast);
});
show("Expected TypeCheckException:     " + e);
});
}

@DisplayName("test061")
@Test public void test061(TestInfo testInfo) throws Exception{
String input = """
int f(int a, int b)
int c = a+b;
^ c - a;

""";
show("\n\n----- test061 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.INT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(2, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var0).getType());
assertEquals("a", ((NameDef) var0).getName());
NameDef var1= params.get(1);
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var1).getType());
assertEquals("b", ((NameDef) var1).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(2, decsAndStatements.size());
ASTNode var2= decsAndStatements.get(0);
assertThat("",var2,instanceOf(VarDeclaration.class));
NameDef var3= ((VarDeclaration) var2).getNameDef();
assertThat("",var3,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var3).getType());
assertEquals("c", ((NameDef) var3).getName());
Expr var4= ((VarDeclaration) var2).getExpr();
assertThat("",var4,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var4).getOp().getKind());
Expr var5= ((BinaryExpr) var4).getLeft();
assertThat("",var5,instanceOf(IdentExpr.class));
assertEquals("a", var5.getText());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
Expr var6= ((BinaryExpr) var4).getRight();
assertThat("",var6,instanceOf(IdentExpr.class));
assertEquals("b", var6.getText());
assertEquals(Type.INT,var6.getType());
assertThat(var6.getCoerceTo(),anyOf(nullValue(), is(var6.getType())));
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var2).getOp().getKind());
ASTNode var7= decsAndStatements.get(1);
assertThat("",var7,instanceOf(ReturnStatement.class));
Expr var8= ((ReturnStatement) var7).getExpr();
assertThat("",var8,instanceOf(BinaryExpr.class));
assertEquals(MINUS, ((BinaryExpr) var8).getOp().getKind());
Expr var9= ((BinaryExpr) var8).getLeft();
assertThat("",var9,instanceOf(IdentExpr.class));
assertEquals("c", var9.getText());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
Expr var10= ((BinaryExpr) var8).getRight();
assertThat("",var10,instanceOf(IdentExpr.class));
assertEquals("a", var10.getText());
assertEquals(Type.INT,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
assertEquals(Type.INT,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
});
}

@DisplayName("test062")
@Test public void test062(TestInfo testInfo) throws Exception{
String input = """
color testRead3()
color x;
x <- console;
^ x;

""";
show("\n\n----- test062 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
ASTNode var2= decsAndStatements.get(1);
assertThat("",var2,instanceOf(ReadStatement.class));
assertEquals("x", ((ReadStatement)var2).getName());
assertNull(((ReadStatement) var2).getSelector());
Expr var3= ((ReadStatement) var2).getSource();
assertThat("",var3,instanceOf(ConsoleExpr.class));
assertEquals(Type.CONSOLE,var3.getType());
assertEquals(Type.COLOR,var3.getCoerceTo());
ASTNode var4= decsAndStatements.get(2);
assertThat("",var4,instanceOf(ReturnStatement.class));
Expr var5= ((ReturnStatement) var4).getExpr();
assertThat("",var5,instanceOf(IdentExpr.class));
assertEquals("x", var5.getText());
assertEquals(Type.COLOR,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
});
}

@DisplayName("test063")
@Test public void test063(TestInfo testInfo) throws Exception{
String input = """
void f()
^ ---3;

""";
show("\n\n----- test063 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
Exception e = assertThrows(TypeCheckException.class, () -> {
checkTypes(ast);
});
show("Expected TypeCheckException:     " + e);
});
}

@DisplayName("test064")
@Test public void test064(TestInfo testInfo) throws Exception{
String input = """
color f()
color a = <<50,60,70>>;
color b = <<13,14,15>>;
^ a-b;

""";
show("\n\n----- test064 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var1).getType());
assertEquals("a", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(ColorExpr.class));
Expr var3= ((ColorExpr) var2).getRed();
assertThat("",var3,instanceOf(IntLitExpr.class));
assertEquals(50, ((IntLitExpr) var3).getValue());
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
Expr var4= ((ColorExpr) var2).getGreen();
assertThat("",var4,instanceOf(IntLitExpr.class));
assertEquals(60, ((IntLitExpr) var4).getValue());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((ColorExpr) var2).getBlue();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(70, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(Type.COLOR,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var6= decsAndStatements.get(1);
assertThat("",var6,instanceOf(VarDeclaration.class));
NameDef var7= ((VarDeclaration) var6).getNameDef();
assertThat("",var7,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var7).getType());
assertEquals("b", ((NameDef) var7).getName());
Expr var8= ((VarDeclaration) var6).getExpr();
assertThat("",var8,instanceOf(ColorExpr.class));
Expr var9= ((ColorExpr) var8).getRed();
assertThat("",var9,instanceOf(IntLitExpr.class));
assertEquals(13, ((IntLitExpr) var9).getValue());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
Expr var10= ((ColorExpr) var8).getGreen();
assertThat("",var10,instanceOf(IntLitExpr.class));
assertEquals(14, ((IntLitExpr) var10).getValue());
assertEquals(Type.INT,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
Expr var11= ((ColorExpr) var8).getBlue();
assertThat("",var11,instanceOf(IntLitExpr.class));
assertEquals(15, ((IntLitExpr) var11).getValue());
assertEquals(Type.INT,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
assertEquals(Type.COLOR,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var6).getOp().getKind());
ASTNode var12= decsAndStatements.get(2);
assertThat("",var12,instanceOf(ReturnStatement.class));
Expr var13= ((ReturnStatement) var12).getExpr();
assertThat("",var13,instanceOf(BinaryExpr.class));
assertEquals(MINUS, ((BinaryExpr) var13).getOp().getKind());
Expr var14= ((BinaryExpr) var13).getLeft();
assertThat("",var14,instanceOf(IdentExpr.class));
assertEquals("a", var14.getText());
assertEquals(Type.COLOR,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
Expr var15= ((BinaryExpr) var13).getRight();
assertThat("",var15,instanceOf(IdentExpr.class));
assertEquals("b", var15.getText());
assertEquals(Type.COLOR,var15.getType());
assertThat(var15.getCoerceTo(),anyOf(nullValue(), is(var15.getType())));
assertEquals(Type.COLOR,var13.getType());
assertThat(var13.getCoerceTo(),anyOf(nullValue(), is(var13.getType())));
});
}

@DisplayName("test065")
@Test public void test065(TestInfo testInfo) throws Exception{
String input = """
boolean a() ^ true;

""";
show("\n\n----- test065 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.BOOLEAN, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(ReturnStatement.class));
Expr var1= ((ReturnStatement) var0).getExpr();
assertThat("",var1,instanceOf(BooleanLitExpr.class));
assertTrue(((BooleanLitExpr) var1).getValue());
assertEquals(Type.BOOLEAN,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
});
}

@DisplayName("test066")
@Test public void test066(TestInfo testInfo) throws Exception{
String input = """
int f() int x = 55; ^ x;

""";
show("\n\n----- test066 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.INT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(2, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(IntLitExpr.class));
assertEquals(55, ((IntLitExpr) var2).getValue());
assertEquals(Type.INT,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(ReturnStatement.class));
Expr var4= ((ReturnStatement) var3).getExpr();
assertThat("",var4,instanceOf(IdentExpr.class));
assertEquals("x", var4.getText());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
});
}

@DisplayName("test067")
@Test public void test067(TestInfo testInfo) throws Exception{
String input = """
int testWriteIntToFile(string file)
int x = 42;
write x -> file;
^x;

""";
show("\n\n----- test067 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.INT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(1, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.STRING, ((NameDef) var0).getType());
assertEquals("file", ((NameDef) var0).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var1= decsAndStatements.get(0);
assertThat("",var1,instanceOf(VarDeclaration.class));
NameDef var2= ((VarDeclaration) var1).getNameDef();
assertThat("",var2,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var2).getType());
assertEquals("x", ((NameDef) var2).getName());
Expr var3= ((VarDeclaration) var1).getExpr();
assertThat("",var3,instanceOf(IntLitExpr.class));
assertEquals(42, ((IntLitExpr) var3).getValue());
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var1).getOp().getKind());
ASTNode var4= decsAndStatements.get(1);
assertThat("",var4,instanceOf(WriteStatement.class));
Expr var5= ((WriteStatement) var4).getSource();
assertThat("",var5,instanceOf(IdentExpr.class));
assertEquals("x", var5.getText());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
Expr var6= ((WriteStatement) var4).getDest();
assertThat("",var6,instanceOf(IdentExpr.class));
assertEquals("file", var6.getText());
assertEquals(Type.STRING,var6.getType());
assertThat(var6.getCoerceTo(),anyOf(nullValue(), is(var6.getType())));
ASTNode var7= decsAndStatements.get(2);
assertThat("",var7,instanceOf(ReturnStatement.class));
Expr var8= ((ReturnStatement) var7).getExpr();
assertThat("",var8,instanceOf(IdentExpr.class));
assertEquals("x", var8.getText());
assertEquals(Type.INT,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
});
}

@DisplayName("test068")
@Test public void test068(TestInfo testInfo) throws Exception{
String input = """
boolean f()
^ <<50,60,70>> == <<13,14,15>>;

""";
show("\n\n----- test068 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.BOOLEAN, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(ReturnStatement.class));
Expr var1= ((ReturnStatement) var0).getExpr();
assertThat("",var1,instanceOf(BinaryExpr.class));
assertEquals(EQUALS, ((BinaryExpr) var1).getOp().getKind());
Expr var2= ((BinaryExpr) var1).getLeft();
assertThat("",var2,instanceOf(ColorExpr.class));
Expr var3= ((ColorExpr) var2).getRed();
assertThat("",var3,instanceOf(IntLitExpr.class));
assertEquals(50, ((IntLitExpr) var3).getValue());
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
Expr var4= ((ColorExpr) var2).getGreen();
assertThat("",var4,instanceOf(IntLitExpr.class));
assertEquals(60, ((IntLitExpr) var4).getValue());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((ColorExpr) var2).getBlue();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(70, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(Type.COLOR,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
Expr var6= ((BinaryExpr) var1).getRight();
assertThat("",var6,instanceOf(ColorExpr.class));
Expr var7= ((ColorExpr) var6).getRed();
assertThat("",var7,instanceOf(IntLitExpr.class));
assertEquals(13, ((IntLitExpr) var7).getValue());
assertEquals(Type.INT,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
Expr var8= ((ColorExpr) var6).getGreen();
assertThat("",var8,instanceOf(IntLitExpr.class));
assertEquals(14, ((IntLitExpr) var8).getValue());
assertEquals(Type.INT,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
Expr var9= ((ColorExpr) var6).getBlue();
assertThat("",var9,instanceOf(IntLitExpr.class));
assertEquals(15, ((IntLitExpr) var9).getValue());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
assertEquals(Type.COLOR,var6.getType());
assertThat(var6.getCoerceTo(),anyOf(nullValue(), is(var6.getType())));
assertEquals(Type.BOOLEAN,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
});
}

@DisplayName("test069")
@Test public void test069(TestInfo testInfo) throws Exception{
String input = """
int f()
int a = 20;
int b = 15;
^ a-b;

""";
show("\n\n----- test069 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.INT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var1).getType());
assertEquals("a", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(IntLitExpr.class));
assertEquals(20, ((IntLitExpr) var2).getValue());
assertEquals(Type.INT,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(VarDeclaration.class));
NameDef var4= ((VarDeclaration) var3).getNameDef();
assertThat("",var4,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var4).getType());
assertEquals("b", ((NameDef) var4).getName());
Expr var5= ((VarDeclaration) var3).getExpr();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(15, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var3).getOp().getKind());
ASTNode var6= decsAndStatements.get(2);
assertThat("",var6,instanceOf(ReturnStatement.class));
Expr var7= ((ReturnStatement) var6).getExpr();
assertThat("",var7,instanceOf(BinaryExpr.class));
assertEquals(MINUS, ((BinaryExpr) var7).getOp().getKind());
Expr var8= ((BinaryExpr) var7).getLeft();
assertThat("",var8,instanceOf(IdentExpr.class));
assertEquals("a", var8.getText());
assertEquals(Type.INT,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
Expr var9= ((BinaryExpr) var7).getRight();
assertThat("",var9,instanceOf(IdentExpr.class));
assertEquals("b", var9.getText());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
assertEquals(Type.INT,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
});
}

@DisplayName("test070")
@Test public void test070(TestInfo testInfo) throws Exception{
String input = """
float a(float b, float c)
^ b/c;

""";
show("\n\n----- test070 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.FLOAT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(2, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.FLOAT, ((NameDef) var0).getType());
assertEquals("b", ((NameDef) var0).getName());
NameDef var1= params.get(1);
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.FLOAT, ((NameDef) var1).getType());
assertEquals("c", ((NameDef) var1).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var2= decsAndStatements.get(0);
assertThat("",var2,instanceOf(ReturnStatement.class));
Expr var3= ((ReturnStatement) var2).getExpr();
assertThat("",var3,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var3).getOp().getKind());
Expr var4= ((BinaryExpr) var3).getLeft();
assertThat("",var4,instanceOf(IdentExpr.class));
assertEquals("b", var4.getText());
assertEquals(Type.FLOAT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((BinaryExpr) var3).getRight();
assertThat("",var5,instanceOf(IdentExpr.class));
assertEquals("c", var5.getText());
assertEquals(Type.FLOAT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(Type.FLOAT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
});
}

@DisplayName("test071")
@Test public void test071(TestInfo testInfo) throws Exception{
String input = """
void withUninitializedDecs()
int a;

""";
show("\n\n----- test071 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.VOID, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var1).getType());
assertEquals("a", ((NameDef) var1).getName());
});
}

@DisplayName("test072")
@Test public void test072(TestInfo testInfo) throws Exception{
String input = """
int testReadWriteInt1()
int x = 3;
write x -> "testReadWriteInt1";
int y <- "testReadWriteInt1";
^ y;

""";
show("\n\n----- test072 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.INT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(4, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(IntLitExpr.class));
assertEquals(3, ((IntLitExpr) var2).getValue());
assertEquals(Type.INT,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(WriteStatement.class));
Expr var4= ((WriteStatement) var3).getSource();
assertThat("",var4,instanceOf(IdentExpr.class));
assertEquals("x", var4.getText());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((WriteStatement) var3).getDest();
assertThat("",var5,instanceOf(StringLitExpr.class));
assertEquals("testReadWriteInt1", ((StringLitExpr) var5).getValue());
assertEquals(Type.STRING,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
ASTNode var6= decsAndStatements.get(2);
assertThat("",var6,instanceOf(VarDeclaration.class));
NameDef var7= ((VarDeclaration) var6).getNameDef();
assertThat("",var7,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var7).getType());
assertEquals("y", ((NameDef) var7).getName());
Expr var8= ((VarDeclaration) var6).getExpr();
assertThat("",var8,instanceOf(StringLitExpr.class));
assertEquals("testReadWriteInt1", ((StringLitExpr) var8).getValue());
assertEquals(Type.STRING,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
assertEquals(LARROW, ((VarDeclaration) var6).getOp().getKind());
ASTNode var9= decsAndStatements.get(3);
assertThat("",var9,instanceOf(ReturnStatement.class));
Expr var10= ((ReturnStatement) var9).getExpr();
assertThat("",var10,instanceOf(IdentExpr.class));
assertEquals("y", var10.getText());
assertEquals(Type.INT,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
});
}

@DisplayName("test073")
@Test public void test073(TestInfo testInfo) throws Exception{
String input = """
string testWriteReadString0()
string x = "bonjour";
write x -> "testWriteReadString0";
string s;
s <- "testWriteReadString0";
^ s;

""";
show("\n\n----- test073 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.STRING, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(5, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.STRING, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(StringLitExpr.class));
assertEquals("bonjour", ((StringLitExpr) var2).getValue());
assertEquals(Type.STRING,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(WriteStatement.class));
Expr var4= ((WriteStatement) var3).getSource();
assertThat("",var4,instanceOf(IdentExpr.class));
assertEquals("x", var4.getText());
assertEquals(Type.STRING,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((WriteStatement) var3).getDest();
assertThat("",var5,instanceOf(StringLitExpr.class));
assertEquals("testWriteReadString0", ((StringLitExpr) var5).getValue());
assertEquals(Type.STRING,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
ASTNode var6= decsAndStatements.get(2);
assertThat("",var6,instanceOf(VarDeclaration.class));
NameDef var7= ((VarDeclaration) var6).getNameDef();
assertThat("",var7,instanceOf(NameDef.class));
assertEquals(Type.STRING, ((NameDef) var7).getType());
assertEquals("s", ((NameDef) var7).getName());
ASTNode var8= decsAndStatements.get(3);
assertThat("",var8,instanceOf(ReadStatement.class));
assertEquals("s", ((ReadStatement)var8).getName());
assertNull(((ReadStatement) var8).getSelector());
Expr var9= ((ReadStatement) var8).getSource();
assertThat("",var9,instanceOf(StringLitExpr.class));
assertEquals("testWriteReadString0", ((StringLitExpr) var9).getValue());
assertEquals(Type.STRING,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
ASTNode var10= decsAndStatements.get(4);
assertThat("",var10,instanceOf(ReturnStatement.class));
Expr var11= ((ReturnStatement) var10).getExpr();
assertThat("",var11,instanceOf(IdentExpr.class));
assertEquals("s", var11.getText());
assertEquals(Type.STRING,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
});
}

@DisplayName("test074")
@Test public void test074(TestInfo testInfo) throws Exception{
String input = """
int binary(int a, int b, int c)
^ a + b *2 + c;

""";
show("\n\n----- test074 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.INT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(3, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var0).getType());
assertEquals("a", ((NameDef) var0).getName());
NameDef var1= params.get(1);
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var1).getType());
assertEquals("b", ((NameDef) var1).getName());
NameDef var2= params.get(2);
assertThat("",var2,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var2).getType());
assertEquals("c", ((NameDef) var2).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var3= decsAndStatements.get(0);
assertThat("",var3,instanceOf(ReturnStatement.class));
Expr var4= ((ReturnStatement) var3).getExpr();
assertThat("",var4,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var4).getOp().getKind());
Expr var5= ((BinaryExpr) var4).getLeft();
assertThat("",var5,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var5).getOp().getKind());
Expr var6= ((BinaryExpr) var5).getLeft();
assertThat("",var6,instanceOf(IdentExpr.class));
assertEquals("a", var6.getText());
assertEquals(Type.INT,var6.getType());
assertThat(var6.getCoerceTo(),anyOf(nullValue(), is(var6.getType())));
Expr var7= ((BinaryExpr) var5).getRight();
assertThat("",var7,instanceOf(BinaryExpr.class));
assertEquals(TIMES, ((BinaryExpr) var7).getOp().getKind());
Expr var8= ((BinaryExpr) var7).getLeft();
assertThat("",var8,instanceOf(IdentExpr.class));
assertEquals("b", var8.getText());
assertEquals(Type.INT,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
Expr var9= ((BinaryExpr) var7).getRight();
assertThat("",var9,instanceOf(IntLitExpr.class));
assertEquals(2, ((IntLitExpr) var9).getValue());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
assertEquals(Type.INT,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
Expr var10= ((BinaryExpr) var4).getRight();
assertThat("",var10,instanceOf(IdentExpr.class));
assertEquals("c", var10.getText());
assertEquals(Type.INT,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
});
}

@DisplayName("test075")
@Test public void test075(TestInfo testInfo) throws Exception{
String input = """
string s()

""";
show("\n\n----- test075 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.STRING, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(0, decsAndStatements.size());
});
}

@DisplayName("test076")
@Test public void test076(TestInfo testInfo) throws Exception{
String input = """
int f()
^ 42

""";
show("\n\n----- test076 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
Exception e = assertThrows(SyntaxException.class, () -> {
@SuppressWarnings("unused")
ASTNode ast = getAST(input);
});
show("Expected SyntaxException:     " + e);
});
}

@DisplayName("test077")
@Test public void test077(TestInfo testInfo) throws Exception{
String input = """
float f()
float a = 20.0;
float b = 7.0;
^ a/b;

""";
show("\n\n----- test077 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.FLOAT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.FLOAT, ((NameDef) var1).getType());
assertEquals("a", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(FloatLitExpr.class));
assertEquals(20.0f, ((FloatLitExpr) var2).getValue());
assertEquals(Type.FLOAT,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(VarDeclaration.class));
NameDef var4= ((VarDeclaration) var3).getNameDef();
assertThat("",var4,instanceOf(NameDef.class));
assertEquals(Type.FLOAT, ((NameDef) var4).getType());
assertEquals("b", ((NameDef) var4).getName());
Expr var5= ((VarDeclaration) var3).getExpr();
assertThat("",var5,instanceOf(FloatLitExpr.class));
assertEquals(7.0f, ((FloatLitExpr) var5).getValue());
assertEquals(Type.FLOAT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var3).getOp().getKind());
ASTNode var6= decsAndStatements.get(2);
assertThat("",var6,instanceOf(ReturnStatement.class));
Expr var7= ((ReturnStatement) var6).getExpr();
assertThat("",var7,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var7).getOp().getKind());
Expr var8= ((BinaryExpr) var7).getLeft();
assertThat("",var8,instanceOf(IdentExpr.class));
assertEquals("a", var8.getText());
assertEquals(Type.FLOAT,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
Expr var9= ((BinaryExpr) var7).getRight();
assertThat("",var9,instanceOf(IdentExpr.class));
assertEquals("b", var9.getText());
assertEquals(Type.FLOAT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
assertEquals(Type.FLOAT,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
});
}

@DisplayName("test078")
@Test public void test078(TestInfo testInfo) throws Exception{
String input = """
color f()
color a = <<50,60,70>>;
color b = <<13,14,15>>;
^ a%b;

""";
show("\n\n----- test078 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var1).getType());
assertEquals("a", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(ColorExpr.class));
Expr var3= ((ColorExpr) var2).getRed();
assertThat("",var3,instanceOf(IntLitExpr.class));
assertEquals(50, ((IntLitExpr) var3).getValue());
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
Expr var4= ((ColorExpr) var2).getGreen();
assertThat("",var4,instanceOf(IntLitExpr.class));
assertEquals(60, ((IntLitExpr) var4).getValue());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((ColorExpr) var2).getBlue();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(70, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(Type.COLOR,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var6= decsAndStatements.get(1);
assertThat("",var6,instanceOf(VarDeclaration.class));
NameDef var7= ((VarDeclaration) var6).getNameDef();
assertThat("",var7,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var7).getType());
assertEquals("b", ((NameDef) var7).getName());
Expr var8= ((VarDeclaration) var6).getExpr();
assertThat("",var8,instanceOf(ColorExpr.class));
Expr var9= ((ColorExpr) var8).getRed();
assertThat("",var9,instanceOf(IntLitExpr.class));
assertEquals(13, ((IntLitExpr) var9).getValue());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
Expr var10= ((ColorExpr) var8).getGreen();
assertThat("",var10,instanceOf(IntLitExpr.class));
assertEquals(14, ((IntLitExpr) var10).getValue());
assertEquals(Type.INT,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
Expr var11= ((ColorExpr) var8).getBlue();
assertThat("",var11,instanceOf(IntLitExpr.class));
assertEquals(15, ((IntLitExpr) var11).getValue());
assertEquals(Type.INT,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
assertEquals(Type.COLOR,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var6).getOp().getKind());
ASTNode var12= decsAndStatements.get(2);
assertThat("",var12,instanceOf(ReturnStatement.class));
Expr var13= ((ReturnStatement) var12).getExpr();
assertThat("",var13,instanceOf(BinaryExpr.class));
assertEquals(MOD, ((BinaryExpr) var13).getOp().getKind());
Expr var14= ((BinaryExpr) var13).getLeft();
assertThat("",var14,instanceOf(IdentExpr.class));
assertEquals("a", var14.getText());
assertEquals(Type.COLOR,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
Expr var15= ((BinaryExpr) var13).getRight();
assertThat("",var15,instanceOf(IdentExpr.class));
assertEquals("b", var15.getText());
assertEquals(Type.COLOR,var15.getType());
assertThat(var15.getCoerceTo(),anyOf(nullValue(), is(var15.getType())));
assertEquals(Type.COLOR,var13.getType());
assertThat(var13.getCoerceTo(),anyOf(nullValue(), is(var13.getType())));
});
}

@DisplayName("test079")
@Test public void test079(TestInfo testInfo) throws Exception{
String input = """
image f()
image[300,400] g;
g <- "https://www.ufl.edu/media/wwwufledu/images/nav/academics.jpg";
^ g;

""";
show("\n\n----- test079 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.IMAGE, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.IMAGE, ((NameDef) var1).getType());
assertEquals("g", ((NameDef) var1).getName());
Dimension var2= ((NameDefWithDim) var1).getDim();
assertThat("",var2,instanceOf(Dimension.class));
Expr var3= ((Dimension) var2).getWidth();
assertThat("",var3,instanceOf(IntLitExpr.class));
assertEquals(300, ((IntLitExpr) var3).getValue());
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
Expr var4= ((Dimension) var2).getHeight();
assertThat("",var4,instanceOf(IntLitExpr.class));
assertEquals(400, ((IntLitExpr) var4).getValue());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
ASTNode var5= decsAndStatements.get(1);
assertThat("",var5,instanceOf(ReadStatement.class));
assertEquals("g", ((ReadStatement)var5).getName());
assertNull(((ReadStatement) var5).getSelector());
Expr var6= ((ReadStatement) var5).getSource();
assertThat("",var6,instanceOf(StringLitExpr.class));
assertEquals("https://www.ufl.edu/media/wwwufledu/images/nav/academics.jpg", ((StringLitExpr) var6).getValue());
assertEquals(Type.STRING,var6.getType());
assertThat(var6.getCoerceTo(),anyOf(nullValue(), is(var6.getType())));
ASTNode var7= decsAndStatements.get(2);
assertThat("",var7,instanceOf(ReturnStatement.class));
Expr var8= ((ReturnStatement) var7).getExpr();
assertThat("",var8,instanceOf(IdentExpr.class));
assertEquals("g", var8.getText());
assertEquals(Type.IMAGE,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
});
}

@DisplayName("test080")
@Test public void test080(TestInfo testInfo) throws Exception{
String input = """
image im()

""";
show("\n\n----- test080 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.IMAGE, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(0, decsAndStatements.size());
});
}

@DisplayName("test081")
@Test public void test081(TestInfo testInfo) throws Exception{
String input = """
void x()

""";
show("\n\n----- test081 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.VOID, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(0, decsAndStatements.size());
});
}

@DisplayName("test082")
@Test public void test082(TestInfo testInfo) throws Exception{
String input = """
void withUninitializedDecs()
image c;

""";
show("\n\n----- test082 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
Exception e = assertThrows(TypeCheckException.class, () -> {
checkTypes(ast);
});
show("Expected TypeCheckException:     " + e);
});
}

@DisplayName("test083")
@Test public void test083(TestInfo testInfo) throws Exception{
String input = """
color testPackUnpack()
color x = << 500, 125, -3 >>;
int p = x;
color z = p;
^ z;

""";
show("\n\n----- test083 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(4, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(ColorExpr.class));
Expr var3= ((ColorExpr) var2).getRed();
assertThat("",var3,instanceOf(IntLitExpr.class));
assertEquals(500, ((IntLitExpr) var3).getValue());
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
Expr var4= ((ColorExpr) var2).getGreen();
assertThat("",var4,instanceOf(IntLitExpr.class));
assertEquals(125, ((IntLitExpr) var4).getValue());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((ColorExpr) var2).getBlue();
assertThat("",var5,instanceOf(UnaryExpr.class));
assertEquals(MINUS, ((UnaryExpr) var5).getOp().getKind());
Expr var6= ((UnaryExpr) var5).getExpr();
assertThat("",var6,instanceOf(IntLitExpr.class));
assertEquals(3, ((IntLitExpr) var6).getValue());
assertEquals(Type.INT,var6.getType());
assertThat(var6.getCoerceTo(),anyOf(nullValue(), is(var6.getType())));
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(Type.COLOR,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var7= decsAndStatements.get(1);
assertThat("",var7,instanceOf(VarDeclaration.class));
NameDef var8= ((VarDeclaration) var7).getNameDef();
assertThat("",var8,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var8).getType());
assertEquals("p", ((NameDef) var8).getName());
Expr var9= ((VarDeclaration) var7).getExpr();
assertThat("",var9,instanceOf(IdentExpr.class));
assertEquals("x", var9.getText());
assertEquals(Type.COLOR,var9.getType());
assertEquals(Type.INT,var9.getCoerceTo());
assertEquals(ASSIGN, ((VarDeclaration) var7).getOp().getKind());
ASTNode var10= decsAndStatements.get(2);
assertThat("",var10,instanceOf(VarDeclaration.class));
NameDef var11= ((VarDeclaration) var10).getNameDef();
assertThat("",var11,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var11).getType());
assertEquals("z", ((NameDef) var11).getName());
Expr var12= ((VarDeclaration) var10).getExpr();
assertThat("",var12,instanceOf(IdentExpr.class));
assertEquals("p", var12.getText());
assertEquals(Type.INT,var12.getType());
assertEquals(Type.COLOR,var12.getCoerceTo());
assertEquals(ASSIGN, ((VarDeclaration) var10).getOp().getKind());
ASTNode var13= decsAndStatements.get(3);
assertThat("",var13,instanceOf(ReturnStatement.class));
Expr var14= ((ReturnStatement) var13).getExpr();
assertThat("",var14,instanceOf(IdentExpr.class));
assertEquals("z", var14.getText());
assertEquals(Type.COLOR,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
});
}

@DisplayName("test084")
@Test public void test084(TestInfo testInfo) throws Exception{
String input = """
float f()
float a = 20.0;
float b = 6.0;
^ a%b;

""";
show("\n\n----- test084 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.FLOAT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.FLOAT, ((NameDef) var1).getType());
assertEquals("a", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(FloatLitExpr.class));
assertEquals(20.0f, ((FloatLitExpr) var2).getValue());
assertEquals(Type.FLOAT,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(VarDeclaration.class));
NameDef var4= ((VarDeclaration) var3).getNameDef();
assertThat("",var4,instanceOf(NameDef.class));
assertEquals(Type.FLOAT, ((NameDef) var4).getType());
assertEquals("b", ((NameDef) var4).getName());
Expr var5= ((VarDeclaration) var3).getExpr();
assertThat("",var5,instanceOf(FloatLitExpr.class));
assertEquals(6.0f, ((FloatLitExpr) var5).getValue());
assertEquals(Type.FLOAT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var3).getOp().getKind());
ASTNode var6= decsAndStatements.get(2);
assertThat("",var6,instanceOf(ReturnStatement.class));
Expr var7= ((ReturnStatement) var6).getExpr();
assertThat("",var7,instanceOf(BinaryExpr.class));
assertEquals(MOD, ((BinaryExpr) var7).getOp().getKind());
Expr var8= ((BinaryExpr) var7).getLeft();
assertThat("",var8,instanceOf(IdentExpr.class));
assertEquals("a", var8.getText());
assertEquals(Type.FLOAT,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
Expr var9= ((BinaryExpr) var7).getRight();
assertThat("",var9,instanceOf(IdentExpr.class));
assertEquals("b", var9.getText());
assertEquals(Type.FLOAT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
assertEquals(Type.FLOAT,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
});
}

@DisplayName("test085")
@Test public void test085(TestInfo testInfo) throws Exception{
String input = """
color f()
color a = <<50,60,70>>;
color b = <<13,14,15>>;
color c = <<2,3,4>>;
^ a+b+c;

""";
show("\n\n----- test085 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(4, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var1).getType());
assertEquals("a", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(ColorExpr.class));
Expr var3= ((ColorExpr) var2).getRed();
assertThat("",var3,instanceOf(IntLitExpr.class));
assertEquals(50, ((IntLitExpr) var3).getValue());
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
Expr var4= ((ColorExpr) var2).getGreen();
assertThat("",var4,instanceOf(IntLitExpr.class));
assertEquals(60, ((IntLitExpr) var4).getValue());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((ColorExpr) var2).getBlue();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(70, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(Type.COLOR,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var6= decsAndStatements.get(1);
assertThat("",var6,instanceOf(VarDeclaration.class));
NameDef var7= ((VarDeclaration) var6).getNameDef();
assertThat("",var7,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var7).getType());
assertEquals("b", ((NameDef) var7).getName());
Expr var8= ((VarDeclaration) var6).getExpr();
assertThat("",var8,instanceOf(ColorExpr.class));
Expr var9= ((ColorExpr) var8).getRed();
assertThat("",var9,instanceOf(IntLitExpr.class));
assertEquals(13, ((IntLitExpr) var9).getValue());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
Expr var10= ((ColorExpr) var8).getGreen();
assertThat("",var10,instanceOf(IntLitExpr.class));
assertEquals(14, ((IntLitExpr) var10).getValue());
assertEquals(Type.INT,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
Expr var11= ((ColorExpr) var8).getBlue();
assertThat("",var11,instanceOf(IntLitExpr.class));
assertEquals(15, ((IntLitExpr) var11).getValue());
assertEquals(Type.INT,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
assertEquals(Type.COLOR,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var6).getOp().getKind());
ASTNode var12= decsAndStatements.get(2);
assertThat("",var12,instanceOf(VarDeclaration.class));
NameDef var13= ((VarDeclaration) var12).getNameDef();
assertThat("",var13,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var13).getType());
assertEquals("c", ((NameDef) var13).getName());
Expr var14= ((VarDeclaration) var12).getExpr();
assertThat("",var14,instanceOf(ColorExpr.class));
Expr var15= ((ColorExpr) var14).getRed();
assertThat("",var15,instanceOf(IntLitExpr.class));
assertEquals(2, ((IntLitExpr) var15).getValue());
assertEquals(Type.INT,var15.getType());
assertThat(var15.getCoerceTo(),anyOf(nullValue(), is(var15.getType())));
Expr var16= ((ColorExpr) var14).getGreen();
assertThat("",var16,instanceOf(IntLitExpr.class));
assertEquals(3, ((IntLitExpr) var16).getValue());
assertEquals(Type.INT,var16.getType());
assertThat(var16.getCoerceTo(),anyOf(nullValue(), is(var16.getType())));
Expr var17= ((ColorExpr) var14).getBlue();
assertThat("",var17,instanceOf(IntLitExpr.class));
assertEquals(4, ((IntLitExpr) var17).getValue());
assertEquals(Type.INT,var17.getType());
assertThat(var17.getCoerceTo(),anyOf(nullValue(), is(var17.getType())));
assertEquals(Type.COLOR,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var12).getOp().getKind());
ASTNode var18= decsAndStatements.get(3);
assertThat("",var18,instanceOf(ReturnStatement.class));
Expr var19= ((ReturnStatement) var18).getExpr();
assertThat("",var19,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var19).getOp().getKind());
Expr var20= ((BinaryExpr) var19).getLeft();
assertThat("",var20,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var20).getOp().getKind());
Expr var21= ((BinaryExpr) var20).getLeft();
assertThat("",var21,instanceOf(IdentExpr.class));
assertEquals("a", var21.getText());
assertEquals(Type.COLOR,var21.getType());
assertThat(var21.getCoerceTo(),anyOf(nullValue(), is(var21.getType())));
Expr var22= ((BinaryExpr) var20).getRight();
assertThat("",var22,instanceOf(IdentExpr.class));
assertEquals("b", var22.getText());
assertEquals(Type.COLOR,var22.getType());
assertThat(var22.getCoerceTo(),anyOf(nullValue(), is(var22.getType())));
assertEquals(Type.COLOR,var20.getType());
assertThat(var20.getCoerceTo(),anyOf(nullValue(), is(var20.getType())));
Expr var23= ((BinaryExpr) var19).getRight();
assertThat("",var23,instanceOf(IdentExpr.class));
assertEquals("c", var23.getText());
assertEquals(Type.COLOR,var23.getType());
assertThat(var23.getCoerceTo(),anyOf(nullValue(), is(var23.getType())));
assertEquals(Type.COLOR,var19.getType());
assertThat(var19.getCoerceTo(),anyOf(nullValue(), is(var19.getType())));
});
}

@DisplayName("test086")
@Test public void test086(TestInfo testInfo) throws Exception{
String input = """
boolean testWriteReadBoolean1()
boolean x = false;
write x -> "testWriteReadBoolean1";
boolean y;
y <- "testWriteReadBoolean1";
^ y;

""";
show("\n\n----- test086 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.BOOLEAN, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(5, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.BOOLEAN, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(BooleanLitExpr.class));
assertFalse(((BooleanLitExpr) var2).getValue());
assertEquals(Type.BOOLEAN,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(WriteStatement.class));
Expr var4= ((WriteStatement) var3).getSource();
assertThat("",var4,instanceOf(IdentExpr.class));
assertEquals("x", var4.getText());
assertEquals(Type.BOOLEAN,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((WriteStatement) var3).getDest();
assertThat("",var5,instanceOf(StringLitExpr.class));
assertEquals("testWriteReadBoolean1", ((StringLitExpr) var5).getValue());
assertEquals(Type.STRING,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
ASTNode var6= decsAndStatements.get(2);
assertThat("",var6,instanceOf(VarDeclaration.class));
NameDef var7= ((VarDeclaration) var6).getNameDef();
assertThat("",var7,instanceOf(NameDef.class));
assertEquals(Type.BOOLEAN, ((NameDef) var7).getType());
assertEquals("y", ((NameDef) var7).getName());
ASTNode var8= decsAndStatements.get(3);
assertThat("",var8,instanceOf(ReadStatement.class));
assertEquals("y", ((ReadStatement)var8).getName());
assertNull(((ReadStatement) var8).getSelector());
Expr var9= ((ReadStatement) var8).getSource();
assertThat("",var9,instanceOf(StringLitExpr.class));
assertEquals("testWriteReadBoolean1", ((StringLitExpr) var9).getValue());
assertEquals(Type.STRING,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
ASTNode var10= decsAndStatements.get(4);
assertThat("",var10,instanceOf(ReturnStatement.class));
Expr var11= ((ReturnStatement) var10).getExpr();
assertThat("",var11,instanceOf(IdentExpr.class));
assertEquals("y", var11.getText());
assertEquals(Type.BOOLEAN,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
});
}

@DisplayName("test087")
@Test public void test087(TestInfo testInfo) throws Exception{
String input = """
color f()
^ <<50,60,70>> *<<2,2,2>>;

""";
show("\n\n----- test087 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(ReturnStatement.class));
Expr var1= ((ReturnStatement) var0).getExpr();
assertThat("",var1,instanceOf(BinaryExpr.class));
assertEquals(TIMES, ((BinaryExpr) var1).getOp().getKind());
Expr var2= ((BinaryExpr) var1).getLeft();
assertThat("",var2,instanceOf(ColorExpr.class));
Expr var3= ((ColorExpr) var2).getRed();
assertThat("",var3,instanceOf(IntLitExpr.class));
assertEquals(50, ((IntLitExpr) var3).getValue());
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
Expr var4= ((ColorExpr) var2).getGreen();
assertThat("",var4,instanceOf(IntLitExpr.class));
assertEquals(60, ((IntLitExpr) var4).getValue());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((ColorExpr) var2).getBlue();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(70, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(Type.COLOR,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
Expr var6= ((BinaryExpr) var1).getRight();
assertThat("",var6,instanceOf(ColorExpr.class));
Expr var7= ((ColorExpr) var6).getRed();
assertThat("",var7,instanceOf(IntLitExpr.class));
assertEquals(2, ((IntLitExpr) var7).getValue());
assertEquals(Type.INT,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
Expr var8= ((ColorExpr) var6).getGreen();
assertThat("",var8,instanceOf(IntLitExpr.class));
assertEquals(2, ((IntLitExpr) var8).getValue());
assertEquals(Type.INT,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
Expr var9= ((ColorExpr) var6).getBlue();
assertThat("",var9,instanceOf(IntLitExpr.class));
assertEquals(2, ((IntLitExpr) var9).getValue());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
assertEquals(Type.COLOR,var6.getType());
assertThat(var6.getCoerceTo(),anyOf(nullValue(), is(var6.getType())));
assertEquals(Type.COLOR,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
});
}

@DisplayName("test088")
@Test public void test088(TestInfo testInfo) throws Exception{
String input = """
float testWriteReadFloat0()
float x = 34.56;
write x -> "testWriteReadFloat0";
float s <- "testWriteReadFloat0";
^ s;

""";
show("\n\n----- test088 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.FLOAT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(4, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.FLOAT, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(FloatLitExpr.class));
assertEquals(34.56f, ((FloatLitExpr) var2).getValue());
assertEquals(Type.FLOAT,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(WriteStatement.class));
Expr var4= ((WriteStatement) var3).getSource();
assertThat("",var4,instanceOf(IdentExpr.class));
assertEquals("x", var4.getText());
assertEquals(Type.FLOAT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((WriteStatement) var3).getDest();
assertThat("",var5,instanceOf(StringLitExpr.class));
assertEquals("testWriteReadFloat0", ((StringLitExpr) var5).getValue());
assertEquals(Type.STRING,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
ASTNode var6= decsAndStatements.get(2);
assertThat("",var6,instanceOf(VarDeclaration.class));
NameDef var7= ((VarDeclaration) var6).getNameDef();
assertThat("",var7,instanceOf(NameDef.class));
assertEquals(Type.FLOAT, ((NameDef) var7).getType());
assertEquals("s", ((NameDef) var7).getName());
Expr var8= ((VarDeclaration) var6).getExpr();
assertThat("",var8,instanceOf(StringLitExpr.class));
assertEquals("testWriteReadFloat0", ((StringLitExpr) var8).getValue());
assertEquals(Type.STRING,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
assertEquals(LARROW, ((VarDeclaration) var6).getOp().getKind());
ASTNode var9= decsAndStatements.get(3);
assertThat("",var9,instanceOf(ReturnStatement.class));
Expr var10= ((ReturnStatement) var9).getExpr();
assertThat("",var10,instanceOf(IdentExpr.class));
assertEquals("s", var10.getText());
assertEquals(Type.FLOAT,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
});
}

@DisplayName("test089")
@Test public void test089(TestInfo testInfo) throws Exception{
String input = """
color f()
^ WHITE;

""";
show("\n\n----- test089 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(ReturnStatement.class));
Expr var1= ((ReturnStatement) var0).getExpr();
assertThat("",var1,instanceOf(ColorConstExpr.class));
assertEquals("WHITE", var1.getText());
assertEquals(Type.COLOR,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
});
}

@DisplayName("test090")
@Test public void test090(TestInfo testInfo) throws Exception{
String input = """
void foo()
int a <- console;

""";
show("\n\n----- test090 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.VOID, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var1).getType());
assertEquals("a", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(ConsoleExpr.class));
assertEquals(Type.CONSOLE,var2.getType());
assertEquals(Type.INT,var2.getCoerceTo());
assertEquals(LARROW, ((VarDeclaration) var0).getOp().getKind());
});
}

@DisplayName("test091")
@Test public void test091(TestInfo testInfo) throws Exception{
String input = """
color f()
^ MAGENTA;

""";
show("\n\n----- test091 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(ReturnStatement.class));
Expr var1= ((ReturnStatement) var0).getExpr();
assertThat("",var1,instanceOf(ColorConstExpr.class));
assertEquals("MAGENTA", var1.getText());
assertEquals(Type.COLOR,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
});
}

@DisplayName("test092")
@Test public void test092(TestInfo testInfo) throws Exception{
String input = """
boolean f()
^ <<50,60,70>> != <<50,60,70>>;

""";
show("\n\n----- test092 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.BOOLEAN, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(ReturnStatement.class));
Expr var1= ((ReturnStatement) var0).getExpr();
assertThat("",var1,instanceOf(BinaryExpr.class));
assertEquals(NOT_EQUALS, ((BinaryExpr) var1).getOp().getKind());
Expr var2= ((BinaryExpr) var1).getLeft();
assertThat("",var2,instanceOf(ColorExpr.class));
Expr var3= ((ColorExpr) var2).getRed();
assertThat("",var3,instanceOf(IntLitExpr.class));
assertEquals(50, ((IntLitExpr) var3).getValue());
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
Expr var4= ((ColorExpr) var2).getGreen();
assertThat("",var4,instanceOf(IntLitExpr.class));
assertEquals(60, ((IntLitExpr) var4).getValue());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((ColorExpr) var2).getBlue();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(70, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(Type.COLOR,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
Expr var6= ((BinaryExpr) var1).getRight();
assertThat("",var6,instanceOf(ColorExpr.class));
Expr var7= ((ColorExpr) var6).getRed();
assertThat("",var7,instanceOf(IntLitExpr.class));
assertEquals(50, ((IntLitExpr) var7).getValue());
assertEquals(Type.INT,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
Expr var8= ((ColorExpr) var6).getGreen();
assertThat("",var8,instanceOf(IntLitExpr.class));
assertEquals(60, ((IntLitExpr) var8).getValue());
assertEquals(Type.INT,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
Expr var9= ((ColorExpr) var6).getBlue();
assertThat("",var9,instanceOf(IntLitExpr.class));
assertEquals(70, ((IntLitExpr) var9).getValue());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
assertEquals(Type.COLOR,var6.getType());
assertThat(var6.getCoerceTo(),anyOf(nullValue(), is(var6.getType())));
assertEquals(Type.BOOLEAN,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
});
}

@DisplayName("test093")
@Test public void test093(TestInfo testInfo) throws Exception{
String input = """
void withInitializedDecs()
string e = "hello" + "goodbye";

""";
show("\n\n----- test093 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
Exception e = assertThrows(TypeCheckException.class, () -> {
checkTypes(ast);
});
show("Expected TypeCheckException:     " + e);
});
}

@DisplayName("test094")
@Test public void test094(TestInfo testInfo) throws Exception{
String input = """
color testWriteReadColor1()
color x = <<128,129,130>>;
write x -> "testWriteReadColor1";
color s;
s <- "testWriteReadColor1";
^ s;

""";
show("\n\n----- test094 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(5, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(ColorExpr.class));
Expr var3= ((ColorExpr) var2).getRed();
assertThat("",var3,instanceOf(IntLitExpr.class));
assertEquals(128, ((IntLitExpr) var3).getValue());
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
Expr var4= ((ColorExpr) var2).getGreen();
assertThat("",var4,instanceOf(IntLitExpr.class));
assertEquals(129, ((IntLitExpr) var4).getValue());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((ColorExpr) var2).getBlue();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(130, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(Type.COLOR,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var6= decsAndStatements.get(1);
assertThat("",var6,instanceOf(WriteStatement.class));
Expr var7= ((WriteStatement) var6).getSource();
assertThat("",var7,instanceOf(IdentExpr.class));
assertEquals("x", var7.getText());
assertEquals(Type.COLOR,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
Expr var8= ((WriteStatement) var6).getDest();
assertThat("",var8,instanceOf(StringLitExpr.class));
assertEquals("testWriteReadColor1", ((StringLitExpr) var8).getValue());
assertEquals(Type.STRING,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
ASTNode var9= decsAndStatements.get(2);
assertThat("",var9,instanceOf(VarDeclaration.class));
NameDef var10= ((VarDeclaration) var9).getNameDef();
assertThat("",var10,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var10).getType());
assertEquals("s", ((NameDef) var10).getName());
ASTNode var11= decsAndStatements.get(3);
assertThat("",var11,instanceOf(ReadStatement.class));
assertEquals("s", ((ReadStatement)var11).getName());
assertNull(((ReadStatement) var11).getSelector());
Expr var12= ((ReadStatement) var11).getSource();
assertThat("",var12,instanceOf(StringLitExpr.class));
assertEquals("testWriteReadColor1", ((StringLitExpr) var12).getValue());
assertEquals(Type.STRING,var12.getType());
assertThat(var12.getCoerceTo(),anyOf(nullValue(), is(var12.getType())));
ASTNode var13= decsAndStatements.get(4);
assertThat("",var13,instanceOf(ReturnStatement.class));
Expr var14= ((ReturnStatement) var13).getExpr();
assertThat("",var14,instanceOf(IdentExpr.class));
assertEquals("s", var14.getText());
assertEquals(Type.COLOR,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
});
}

@DisplayName("test095")
@Test public void test095(TestInfo testInfo) throws Exception{
String input = """
boolean testReadBoolean0()
boolean x;
x <- console;
^ x;

""";
show("\n\n----- test095 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.BOOLEAN, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.BOOLEAN, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
ASTNode var2= decsAndStatements.get(1);
assertThat("",var2,instanceOf(ReadStatement.class));
assertEquals("x", ((ReadStatement)var2).getName());
assertNull(((ReadStatement) var2).getSelector());
Expr var3= ((ReadStatement) var2).getSource();
assertThat("",var3,instanceOf(ConsoleExpr.class));
assertEquals(Type.CONSOLE,var3.getType());
assertEquals(Type.BOOLEAN,var3.getCoerceTo());
ASTNode var4= decsAndStatements.get(2);
assertThat("",var4,instanceOf(ReturnStatement.class));
Expr var5= ((ReturnStatement) var4).getExpr();
assertThat("",var5,instanceOf(IdentExpr.class));
assertEquals("x", var5.getText());
assertEquals(Type.BOOLEAN,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
});
}

@DisplayName("test096")
@Test public void test096(TestInfo testInfo) throws Exception{
String input = """
float f()

""";
show("\n\n----- test096 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.FLOAT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(0, decsAndStatements.size());
});
}

@DisplayName("test097")
@Test public void test097(TestInfo testInfo) throws Exception{
String input = """
color f()
color a = <<50,60,70>>;
color b = <<13,14,15>>;
^ a+b;

""";
show("\n\n----- test097 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var1).getType());
assertEquals("a", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(ColorExpr.class));
Expr var3= ((ColorExpr) var2).getRed();
assertThat("",var3,instanceOf(IntLitExpr.class));
assertEquals(50, ((IntLitExpr) var3).getValue());
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
Expr var4= ((ColorExpr) var2).getGreen();
assertThat("",var4,instanceOf(IntLitExpr.class));
assertEquals(60, ((IntLitExpr) var4).getValue());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((ColorExpr) var2).getBlue();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(70, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(Type.COLOR,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var6= decsAndStatements.get(1);
assertThat("",var6,instanceOf(VarDeclaration.class));
NameDef var7= ((VarDeclaration) var6).getNameDef();
assertThat("",var7,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var7).getType());
assertEquals("b", ((NameDef) var7).getName());
Expr var8= ((VarDeclaration) var6).getExpr();
assertThat("",var8,instanceOf(ColorExpr.class));
Expr var9= ((ColorExpr) var8).getRed();
assertThat("",var9,instanceOf(IntLitExpr.class));
assertEquals(13, ((IntLitExpr) var9).getValue());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
Expr var10= ((ColorExpr) var8).getGreen();
assertThat("",var10,instanceOf(IntLitExpr.class));
assertEquals(14, ((IntLitExpr) var10).getValue());
assertEquals(Type.INT,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
Expr var11= ((ColorExpr) var8).getBlue();
assertThat("",var11,instanceOf(IntLitExpr.class));
assertEquals(15, ((IntLitExpr) var11).getValue());
assertEquals(Type.INT,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
assertEquals(Type.COLOR,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var6).getOp().getKind());
ASTNode var12= decsAndStatements.get(2);
assertThat("",var12,instanceOf(ReturnStatement.class));
Expr var13= ((ReturnStatement) var12).getExpr();
assertThat("",var13,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var13).getOp().getKind());
Expr var14= ((BinaryExpr) var13).getLeft();
assertThat("",var14,instanceOf(IdentExpr.class));
assertEquals("a", var14.getText());
assertEquals(Type.COLOR,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
Expr var15= ((BinaryExpr) var13).getRight();
assertThat("",var15,instanceOf(IdentExpr.class));
assertEquals("b", var15.getText());
assertEquals(Type.COLOR,var15.getType());
assertThat(var15.getCoerceTo(),anyOf(nullValue(), is(var15.getType())));
assertEquals(Type.COLOR,var13.getType());
assertThat(var13.getCoerceTo(),anyOf(nullValue(), is(var13.getType())));
});
}

@DisplayName("test098")
@Test public void test098(TestInfo testInfo) throws Exception{
String input = """
color f()
color a = <<50,60,70>>;
color b = <<13,14,15>>;
^ a/b;

""";
show("\n\n----- test098 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var1).getType());
assertEquals("a", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(ColorExpr.class));
Expr var3= ((ColorExpr) var2).getRed();
assertThat("",var3,instanceOf(IntLitExpr.class));
assertEquals(50, ((IntLitExpr) var3).getValue());
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
Expr var4= ((ColorExpr) var2).getGreen();
assertThat("",var4,instanceOf(IntLitExpr.class));
assertEquals(60, ((IntLitExpr) var4).getValue());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((ColorExpr) var2).getBlue();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(70, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(Type.COLOR,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var6= decsAndStatements.get(1);
assertThat("",var6,instanceOf(VarDeclaration.class));
NameDef var7= ((VarDeclaration) var6).getNameDef();
assertThat("",var7,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var7).getType());
assertEquals("b", ((NameDef) var7).getName());
Expr var8= ((VarDeclaration) var6).getExpr();
assertThat("",var8,instanceOf(ColorExpr.class));
Expr var9= ((ColorExpr) var8).getRed();
assertThat("",var9,instanceOf(IntLitExpr.class));
assertEquals(13, ((IntLitExpr) var9).getValue());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
Expr var10= ((ColorExpr) var8).getGreen();
assertThat("",var10,instanceOf(IntLitExpr.class));
assertEquals(14, ((IntLitExpr) var10).getValue());
assertEquals(Type.INT,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
Expr var11= ((ColorExpr) var8).getBlue();
assertThat("",var11,instanceOf(IntLitExpr.class));
assertEquals(15, ((IntLitExpr) var11).getValue());
assertEquals(Type.INT,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
assertEquals(Type.COLOR,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var6).getOp().getKind());
ASTNode var12= decsAndStatements.get(2);
assertThat("",var12,instanceOf(ReturnStatement.class));
Expr var13= ((ReturnStatement) var12).getExpr();
assertThat("",var13,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var13).getOp().getKind());
Expr var14= ((BinaryExpr) var13).getLeft();
assertThat("",var14,instanceOf(IdentExpr.class));
assertEquals("a", var14.getText());
assertEquals(Type.COLOR,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
Expr var15= ((BinaryExpr) var13).getRight();
assertThat("",var15,instanceOf(IdentExpr.class));
assertEquals("b", var15.getText());
assertEquals(Type.COLOR,var15.getType());
assertThat(var15.getCoerceTo(),anyOf(nullValue(), is(var15.getType())));
assertEquals(Type.COLOR,var13.getType());
assertThat(var13.getCoerceTo(),anyOf(nullValue(), is(var13.getType())));
});
}

@DisplayName("test099")
@Test public void test099(TestInfo testInfo) throws Exception{
String input = """
boolean testReadBoolean1()
boolean x <- console;
^ x;

""";
show("\n\n----- test099 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.BOOLEAN, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(2, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.BOOLEAN, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(ConsoleExpr.class));
assertEquals(Type.CONSOLE,var2.getType());
assertEquals(Type.BOOLEAN,var2.getCoerceTo());
assertEquals(LARROW, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(ReturnStatement.class));
Expr var4= ((ReturnStatement) var3).getExpr();
assertThat("",var4,instanceOf(IdentExpr.class));
assertEquals("x", var4.getText());
assertEquals(Type.BOOLEAN,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
});
}

@DisplayName("test100")
@Test public void test100(TestInfo testInfo) throws Exception{
String input = """
color f()
^ ORANGE;

""";
show("\n\n----- test100 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(ReturnStatement.class));
Expr var1= ((ReturnStatement) var0).getExpr();
assertThat("",var1,instanceOf(ColorConstExpr.class));
assertEquals("ORANGE", var1.getText());
assertEquals(Type.COLOR,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
});
}

@DisplayName("test101")
@Test public void test101(TestInfo testInfo) throws Exception{
String input = """
boolean f(int x)
int y = x;
^ y;

""";
show("\n\n----- test101 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
Exception e = assertThrows(TypeCheckException.class, () -> {
checkTypes(ast);
});
show("Expected TypeCheckException:     " + e);
});
}

@DisplayName("test102")
@Test public void test102(TestInfo testInfo) throws Exception{
String input = """
int f()
int a = 3;
int b = 4;
^ a+b;

""";
show("\n\n----- test102 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.INT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var1).getType());
assertEquals("a", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(IntLitExpr.class));
assertEquals(3, ((IntLitExpr) var2).getValue());
assertEquals(Type.INT,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(VarDeclaration.class));
NameDef var4= ((VarDeclaration) var3).getNameDef();
assertThat("",var4,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var4).getType());
assertEquals("b", ((NameDef) var4).getName());
Expr var5= ((VarDeclaration) var3).getExpr();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(4, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var3).getOp().getKind());
ASTNode var6= decsAndStatements.get(2);
assertThat("",var6,instanceOf(ReturnStatement.class));
Expr var7= ((ReturnStatement) var6).getExpr();
assertThat("",var7,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var7).getOp().getKind());
Expr var8= ((BinaryExpr) var7).getLeft();
assertThat("",var8,instanceOf(IdentExpr.class));
assertEquals("a", var8.getText());
assertEquals(Type.INT,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
Expr var9= ((BinaryExpr) var7).getRight();
assertThat("",var9,instanceOf(IdentExpr.class));
assertEquals("b", var9.getText());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
assertEquals(Type.INT,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
});
}

@DisplayName("test103")
@Test public void test103(TestInfo testInfo) throws Exception{
String input = """
color f() ^ <<40,50,60>>;

""";
show("\n\n----- test103 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(ReturnStatement.class));
Expr var1= ((ReturnStatement) var0).getExpr();
assertThat("",var1,instanceOf(ColorExpr.class));
Expr var2= ((ColorExpr) var1).getRed();
assertThat("",var2,instanceOf(IntLitExpr.class));
assertEquals(40, ((IntLitExpr) var2).getValue());
assertEquals(Type.INT,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
Expr var3= ((ColorExpr) var1).getGreen();
assertThat("",var3,instanceOf(IntLitExpr.class));
assertEquals(50, ((IntLitExpr) var3).getValue());
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
Expr var4= ((ColorExpr) var1).getBlue();
assertThat("",var4,instanceOf(IntLitExpr.class));
assertEquals(60, ((IntLitExpr) var4).getValue());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
assertEquals(Type.COLOR,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
});
}

@DisplayName("test104")
@Test public void test104(TestInfo testInfo) throws Exception{
String input = """
color f()
^ BLUE;

""";
show("\n\n----- test104 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(ReturnStatement.class));
Expr var1= ((ReturnStatement) var0).getExpr();
assertThat("",var1,instanceOf(ColorConstExpr.class));
assertEquals("BLUE", var1.getText());
assertEquals(Type.COLOR,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
});
}

@DisplayName("test105")
@Test public void test105(TestInfo testInfo) throws Exception{
String input = """
boolean testWriteReadBoolean0()
boolean x = false;
write x -> "testWriteReadBoolean0";
boolean y <- "testWriteReadBoolean0";
^ y;

""";
show("\n\n----- test105 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.BOOLEAN, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(4, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.BOOLEAN, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(BooleanLitExpr.class));
assertFalse(((BooleanLitExpr) var2).getValue());
assertEquals(Type.BOOLEAN,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(WriteStatement.class));
Expr var4= ((WriteStatement) var3).getSource();
assertThat("",var4,instanceOf(IdentExpr.class));
assertEquals("x", var4.getText());
assertEquals(Type.BOOLEAN,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((WriteStatement) var3).getDest();
assertThat("",var5,instanceOf(StringLitExpr.class));
assertEquals("testWriteReadBoolean0", ((StringLitExpr) var5).getValue());
assertEquals(Type.STRING,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
ASTNode var6= decsAndStatements.get(2);
assertThat("",var6,instanceOf(VarDeclaration.class));
NameDef var7= ((VarDeclaration) var6).getNameDef();
assertThat("",var7,instanceOf(NameDef.class));
assertEquals(Type.BOOLEAN, ((NameDef) var7).getType());
assertEquals("y", ((NameDef) var7).getName());
Expr var8= ((VarDeclaration) var6).getExpr();
assertThat("",var8,instanceOf(StringLitExpr.class));
assertEquals("testWriteReadBoolean0", ((StringLitExpr) var8).getValue());
assertEquals(Type.STRING,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
assertEquals(LARROW, ((VarDeclaration) var6).getOp().getKind());
ASTNode var9= decsAndStatements.get(3);
assertThat("",var9,instanceOf(ReturnStatement.class));
Expr var10= ((ReturnStatement) var9).getExpr();
assertThat("",var10,instanceOf(IdentExpr.class));
assertEquals("y", var10.getText());
assertEquals(Type.BOOLEAN,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
});
}

@DisplayName("test106")
@Test public void test106(TestInfo testInfo) throws Exception{
String input = """
image testImageArithemtic1()
image[500,500] blue;
blue = BLUE;
image[500,500] green;
green = GREEN;
image[500,500] teal;
teal = blue + green;
^teal;

""";
show("\n\n----- test106 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.IMAGE, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(7, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.IMAGE, ((NameDef) var1).getType());
assertEquals("blue", ((NameDef) var1).getName());
Dimension var2= ((NameDefWithDim) var1).getDim();
assertThat("",var2,instanceOf(Dimension.class));
Expr var3= ((Dimension) var2).getWidth();
assertThat("",var3,instanceOf(IntLitExpr.class));
assertEquals(500, ((IntLitExpr) var3).getValue());
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
Expr var4= ((Dimension) var2).getHeight();
assertThat("",var4,instanceOf(IntLitExpr.class));
assertEquals(500, ((IntLitExpr) var4).getValue());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
ASTNode var5= decsAndStatements.get(1);
assertThat("",var5,instanceOf(AssignmentStatement.class));
assertEquals("blue", ((AssignmentStatement)var5).getName());
assertNull(((AssignmentStatement) var5).getSelector());
Expr var6= ((AssignmentStatement) var5).getExpr();
assertThat("",var6,instanceOf(ColorConstExpr.class));
assertEquals("BLUE", var6.getText());
assertEquals(Type.COLOR,var6.getType());
assertThat(var6.getCoerceTo(),anyOf(nullValue(), is(var6.getType())));
ASTNode var7= decsAndStatements.get(2);
assertThat("",var7,instanceOf(VarDeclaration.class));
NameDef var8= ((VarDeclaration) var7).getNameDef();
assertThat("",var8,instanceOf(NameDef.class));
assertEquals(Type.IMAGE, ((NameDef) var8).getType());
assertEquals("green", ((NameDef) var8).getName());
Dimension var9= ((NameDefWithDim) var8).getDim();
assertThat("",var9,instanceOf(Dimension.class));
Expr var10= ((Dimension) var9).getWidth();
assertThat("",var10,instanceOf(IntLitExpr.class));
assertEquals(500, ((IntLitExpr) var10).getValue());
assertEquals(Type.INT,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
Expr var11= ((Dimension) var9).getHeight();
assertThat("",var11,instanceOf(IntLitExpr.class));
assertEquals(500, ((IntLitExpr) var11).getValue());
assertEquals(Type.INT,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
ASTNode var12= decsAndStatements.get(3);
assertThat("",var12,instanceOf(AssignmentStatement.class));
assertEquals("green", ((AssignmentStatement)var12).getName());
assertNull(((AssignmentStatement) var12).getSelector());
Expr var13= ((AssignmentStatement) var12).getExpr();
assertThat("",var13,instanceOf(ColorConstExpr.class));
assertEquals("GREEN", var13.getText());
assertEquals(Type.COLOR,var13.getType());
assertThat(var13.getCoerceTo(),anyOf(nullValue(), is(var13.getType())));
ASTNode var14= decsAndStatements.get(4);
assertThat("",var14,instanceOf(VarDeclaration.class));
NameDef var15= ((VarDeclaration) var14).getNameDef();
assertThat("",var15,instanceOf(NameDef.class));
assertEquals(Type.IMAGE, ((NameDef) var15).getType());
assertEquals("teal", ((NameDef) var15).getName());
Dimension var16= ((NameDefWithDim) var15).getDim();
assertThat("",var16,instanceOf(Dimension.class));
Expr var17= ((Dimension) var16).getWidth();
assertThat("",var17,instanceOf(IntLitExpr.class));
assertEquals(500, ((IntLitExpr) var17).getValue());
assertEquals(Type.INT,var17.getType());
assertThat(var17.getCoerceTo(),anyOf(nullValue(), is(var17.getType())));
Expr var18= ((Dimension) var16).getHeight();
assertThat("",var18,instanceOf(IntLitExpr.class));
assertEquals(500, ((IntLitExpr) var18).getValue());
assertEquals(Type.INT,var18.getType());
assertThat(var18.getCoerceTo(),anyOf(nullValue(), is(var18.getType())));
ASTNode var19= decsAndStatements.get(5);
assertThat("",var19,instanceOf(AssignmentStatement.class));
assertEquals("teal", ((AssignmentStatement)var19).getName());
assertNull(((AssignmentStatement) var19).getSelector());
Expr var20= ((AssignmentStatement) var19).getExpr();
assertThat("",var20,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var20).getOp().getKind());
Expr var21= ((BinaryExpr) var20).getLeft();
assertThat("",var21,instanceOf(IdentExpr.class));
assertEquals("blue", var21.getText());
assertEquals(Type.IMAGE,var21.getType());
assertThat(var21.getCoerceTo(),anyOf(nullValue(), is(var21.getType())));
Expr var22= ((BinaryExpr) var20).getRight();
assertThat("",var22,instanceOf(IdentExpr.class));
assertEquals("green", var22.getText());
assertEquals(Type.IMAGE,var22.getType());
assertThat(var22.getCoerceTo(),anyOf(nullValue(), is(var22.getType())));
assertEquals(Type.IMAGE,var20.getType());
assertThat(var20.getCoerceTo(),anyOf(nullValue(), is(var20.getType())));
ASTNode var23= decsAndStatements.get(6);
assertThat("",var23,instanceOf(ReturnStatement.class));
Expr var24= ((ReturnStatement) var23).getExpr();
assertThat("",var24,instanceOf(IdentExpr.class));
assertEquals("teal", var24.getText());
assertEquals(Type.IMAGE,var24.getType());
assertThat(var24.getCoerceTo(),anyOf(nullValue(), is(var24.getType())));
});
}

@DisplayName("test107")
@Test public void test107(TestInfo testInfo) throws Exception{
String input = """
image flag(int size)
image[size,size] f;
int stripSize = size/2;
f[x,y] = if (y > stripSize) YELLOW else BLUE fi;
^f;

""";
show("\n\n----- test107 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.IMAGE, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(1, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var0).getType());
assertEquals("size", ((NameDef) var0).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(4, decsAndStatements.size());
ASTNode var1= decsAndStatements.get(0);
assertThat("",var1,instanceOf(VarDeclaration.class));
NameDef var2= ((VarDeclaration) var1).getNameDef();
assertThat("",var2,instanceOf(NameDef.class));
assertEquals(Type.IMAGE, ((NameDef) var2).getType());
assertEquals("f", ((NameDef) var2).getName());
Dimension var3= ((NameDefWithDim) var2).getDim();
assertThat("",var3,instanceOf(Dimension.class));
Expr var4= ((Dimension) var3).getWidth();
assertThat("",var4,instanceOf(IdentExpr.class));
assertEquals("size", var4.getText());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((Dimension) var3).getHeight();
assertThat("",var5,instanceOf(IdentExpr.class));
assertEquals("size", var5.getText());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
ASTNode var6= decsAndStatements.get(1);
assertThat("",var6,instanceOf(VarDeclaration.class));
NameDef var7= ((VarDeclaration) var6).getNameDef();
assertThat("",var7,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var7).getType());
assertEquals("stripSize", ((NameDef) var7).getName());
Expr var8= ((VarDeclaration) var6).getExpr();
assertThat("",var8,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var8).getOp().getKind());
Expr var9= ((BinaryExpr) var8).getLeft();
assertThat("",var9,instanceOf(IdentExpr.class));
assertEquals("size", var9.getText());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
Expr var10= ((BinaryExpr) var8).getRight();
assertThat("",var10,instanceOf(IntLitExpr.class));
assertEquals(2, ((IntLitExpr) var10).getValue());
assertEquals(Type.INT,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
assertEquals(Type.INT,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var6).getOp().getKind());
ASTNode var11= decsAndStatements.get(2);
assertThat("",var11,instanceOf(AssignmentStatement.class));
assertEquals("f", ((AssignmentStatement)var11).getName());
PixelSelector var12= ((AssignmentStatement) var11).getSelector();
assertThat("",var12,instanceOf(PixelSelector.class));
Expr var13= ((PixelSelector) var12).getX();
assertThat("",var13,instanceOf(IdentExpr.class));
assertEquals("x", var13.getText());
assertEquals(Type.INT,var13.getType());
assertThat(var13.getCoerceTo(),anyOf(nullValue(), is(var13.getType())));
Expr var14= ((PixelSelector) var12).getY();
assertThat("",var14,instanceOf(IdentExpr.class));
assertEquals("y", var14.getText());
assertEquals(Type.INT,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
Expr var15= ((AssignmentStatement) var11).getExpr();
assertThat("",var15,instanceOf(ConditionalExpr.class));
Expr var16= ((ConditionalExpr) var15).getCondition();
assertThat("",var16,instanceOf(BinaryExpr.class));
assertEquals(GT, ((BinaryExpr) var16).getOp().getKind());
Expr var17= ((BinaryExpr) var16).getLeft();
assertThat("",var17,instanceOf(IdentExpr.class));
assertEquals("y", var17.getText());
assertEquals(Type.INT,var17.getType());
assertThat(var17.getCoerceTo(),anyOf(nullValue(), is(var17.getType())));
Expr var18= ((BinaryExpr) var16).getRight();
assertThat("",var18,instanceOf(IdentExpr.class));
assertEquals("stripSize", var18.getText());
assertEquals(Type.INT,var18.getType());
assertThat(var18.getCoerceTo(),anyOf(nullValue(), is(var18.getType())));
assertEquals(Type.BOOLEAN,var16.getType());
assertThat(var16.getCoerceTo(),anyOf(nullValue(), is(var16.getType())));
Expr var19= ((ConditionalExpr) var15).getTrueCase();
assertThat("",var19,instanceOf(ColorConstExpr.class));
assertEquals("YELLOW", var19.getText());
assertEquals(Type.COLOR,var19.getType());
assertThat(var19.getCoerceTo(),anyOf(nullValue(), is(var19.getType())));
Expr var20= ((ConditionalExpr) var15).getFalseCase();
assertThat("",var20,instanceOf(ColorConstExpr.class));
assertEquals("BLUE", var20.getText());
assertEquals(Type.COLOR,var20.getType());
assertThat(var20.getCoerceTo(),anyOf(nullValue(), is(var20.getType())));
assertEquals(Type.COLOR,var15.getType());
assertThat(var15.getCoerceTo(),anyOf(nullValue(), is(var15.getType())));
ASTNode var21= decsAndStatements.get(3);
assertThat("",var21,instanceOf(ReturnStatement.class));
Expr var22= ((ReturnStatement) var21).getExpr();
assertThat("",var22,instanceOf(IdentExpr.class));
assertEquals("f", var22.getText());
assertEquals(Type.IMAGE,var22.getType());
assertThat(var22.getCoerceTo(),anyOf(nullValue(), is(var22.getType())));
});
}

@DisplayName("test108")
@Test public void test108(TestInfo testInfo) throws Exception{
String input = """
int testReadWriteInt2()
int x = 3;
write x -> "testReadWriteInt2";
int y;
y <- "testReadWriteInt2";
^ y;

""";
show("\n\n----- test108 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.INT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(5, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(IntLitExpr.class));
assertEquals(3, ((IntLitExpr) var2).getValue());
assertEquals(Type.INT,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(WriteStatement.class));
Expr var4= ((WriteStatement) var3).getSource();
assertThat("",var4,instanceOf(IdentExpr.class));
assertEquals("x", var4.getText());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((WriteStatement) var3).getDest();
assertThat("",var5,instanceOf(StringLitExpr.class));
assertEquals("testReadWriteInt2", ((StringLitExpr) var5).getValue());
assertEquals(Type.STRING,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
ASTNode var6= decsAndStatements.get(2);
assertThat("",var6,instanceOf(VarDeclaration.class));
NameDef var7= ((VarDeclaration) var6).getNameDef();
assertThat("",var7,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var7).getType());
assertEquals("y", ((NameDef) var7).getName());
ASTNode var8= decsAndStatements.get(3);
assertThat("",var8,instanceOf(ReadStatement.class));
assertEquals("y", ((ReadStatement)var8).getName());
assertNull(((ReadStatement) var8).getSelector());
Expr var9= ((ReadStatement) var8).getSource();
assertThat("",var9,instanceOf(StringLitExpr.class));
assertEquals("testReadWriteInt2", ((StringLitExpr) var9).getValue());
assertEquals(Type.STRING,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
ASTNode var10= decsAndStatements.get(4);
assertThat("",var10,instanceOf(ReturnStatement.class));
Expr var11= ((ReturnStatement) var10).getExpr();
assertThat("",var11,instanceOf(IdentExpr.class));
assertEquals("y", var11.getText());
assertEquals(Type.INT,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
});
}

@DisplayName("test109")
@Test public void test109(TestInfo testInfo) throws Exception{
String input = """
float f() ^ 1.2;

""";
show("\n\n----- test109 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.FLOAT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(ReturnStatement.class));
Expr var1= ((ReturnStatement) var0).getExpr();
assertThat("",var1,instanceOf(FloatLitExpr.class));
assertEquals(1.2f, ((FloatLitExpr) var1).getValue());
assertEquals(Type.FLOAT,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
});
}

@DisplayName("test110")
@Test public void test110(TestInfo testInfo) throws Exception{
String input = """
void BDP0()
write 42->console;
write "hello" -> console;
write 43.6->console;
write <<1,2,3>> -> console;
write RED -> console;

""";
show("\n\n----- test110 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.VOID, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(5, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(WriteStatement.class));
Expr var1= ((WriteStatement) var0).getSource();
assertThat("",var1,instanceOf(IntLitExpr.class));
assertEquals(42, ((IntLitExpr) var1).getValue());
assertEquals(Type.INT,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
Expr var2= ((WriteStatement) var0).getDest();
assertThat("",var2,instanceOf(ConsoleExpr.class));
assertEquals(Type.CONSOLE,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(WriteStatement.class));
Expr var4= ((WriteStatement) var3).getSource();
assertThat("",var4,instanceOf(StringLitExpr.class));
assertEquals("hello", ((StringLitExpr) var4).getValue());
assertEquals(Type.STRING,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((WriteStatement) var3).getDest();
assertThat("",var5,instanceOf(ConsoleExpr.class));
assertEquals(Type.CONSOLE,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
ASTNode var6= decsAndStatements.get(2);
assertThat("",var6,instanceOf(WriteStatement.class));
Expr var7= ((WriteStatement) var6).getSource();
assertThat("",var7,instanceOf(FloatLitExpr.class));
assertEquals(43.6f, ((FloatLitExpr) var7).getValue());
assertEquals(Type.FLOAT,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
Expr var8= ((WriteStatement) var6).getDest();
assertThat("",var8,instanceOf(ConsoleExpr.class));
assertEquals(Type.CONSOLE,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
ASTNode var9= decsAndStatements.get(3);
assertThat("",var9,instanceOf(WriteStatement.class));
Expr var10= ((WriteStatement) var9).getSource();
assertThat("",var10,instanceOf(ColorExpr.class));
Expr var11= ((ColorExpr) var10).getRed();
assertThat("",var11,instanceOf(IntLitExpr.class));
assertEquals(1, ((IntLitExpr) var11).getValue());
assertEquals(Type.INT,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
Expr var12= ((ColorExpr) var10).getGreen();
assertThat("",var12,instanceOf(IntLitExpr.class));
assertEquals(2, ((IntLitExpr) var12).getValue());
assertEquals(Type.INT,var12.getType());
assertThat(var12.getCoerceTo(),anyOf(nullValue(), is(var12.getType())));
Expr var13= ((ColorExpr) var10).getBlue();
assertThat("",var13,instanceOf(IntLitExpr.class));
assertEquals(3, ((IntLitExpr) var13).getValue());
assertEquals(Type.INT,var13.getType());
assertThat(var13.getCoerceTo(),anyOf(nullValue(), is(var13.getType())));
assertEquals(Type.COLOR,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
Expr var14= ((WriteStatement) var9).getDest();
assertThat("",var14,instanceOf(ConsoleExpr.class));
assertEquals(Type.CONSOLE,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
ASTNode var15= decsAndStatements.get(4);
assertThat("",var15,instanceOf(WriteStatement.class));
Expr var16= ((WriteStatement) var15).getSource();
assertThat("",var16,instanceOf(ColorConstExpr.class));
assertEquals("RED", var16.getText());
assertEquals(Type.COLOR,var16.getType());
assertThat(var16.getCoerceTo(),anyOf(nullValue(), is(var16.getType())));
Expr var17= ((WriteStatement) var15).getDest();
assertThat("",var17,instanceOf(ConsoleExpr.class));
assertEquals(Type.CONSOLE,var17.getType());
assertThat(var17.getCoerceTo(),anyOf(nullValue(), is(var17.getType())));
});
}

@DisplayName("test111")
@Test public void test111(TestInfo testInfo) throws Exception{
String input = """
boolean f()
^ <<50,60,70>> == <<50,60,70>>;

""";
show("\n\n----- test111 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.BOOLEAN, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(ReturnStatement.class));
Expr var1= ((ReturnStatement) var0).getExpr();
assertThat("",var1,instanceOf(BinaryExpr.class));
assertEquals(EQUALS, ((BinaryExpr) var1).getOp().getKind());
Expr var2= ((BinaryExpr) var1).getLeft();
assertThat("",var2,instanceOf(ColorExpr.class));
Expr var3= ((ColorExpr) var2).getRed();
assertThat("",var3,instanceOf(IntLitExpr.class));
assertEquals(50, ((IntLitExpr) var3).getValue());
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
Expr var4= ((ColorExpr) var2).getGreen();
assertThat("",var4,instanceOf(IntLitExpr.class));
assertEquals(60, ((IntLitExpr) var4).getValue());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((ColorExpr) var2).getBlue();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(70, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(Type.COLOR,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
Expr var6= ((BinaryExpr) var1).getRight();
assertThat("",var6,instanceOf(ColorExpr.class));
Expr var7= ((ColorExpr) var6).getRed();
assertThat("",var7,instanceOf(IntLitExpr.class));
assertEquals(50, ((IntLitExpr) var7).getValue());
assertEquals(Type.INT,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
Expr var8= ((ColorExpr) var6).getGreen();
assertThat("",var8,instanceOf(IntLitExpr.class));
assertEquals(60, ((IntLitExpr) var8).getValue());
assertEquals(Type.INT,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
Expr var9= ((ColorExpr) var6).getBlue();
assertThat("",var9,instanceOf(IntLitExpr.class));
assertEquals(70, ((IntLitExpr) var9).getValue());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
assertEquals(Type.COLOR,var6.getType());
assertThat(var6.getCoerceTo(),anyOf(nullValue(), is(var6.getType())));
assertEquals(Type.BOOLEAN,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
});
}

@DisplayName("test112")
@Test public void test112(TestInfo testInfo) throws Exception{
String input = """
float f()
float a = 3.2;
float b = 4.8;
^ a+b;

""";
show("\n\n----- test112 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.FLOAT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.FLOAT, ((NameDef) var1).getType());
assertEquals("a", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(FloatLitExpr.class));
assertEquals(3.2f, ((FloatLitExpr) var2).getValue());
assertEquals(Type.FLOAT,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(VarDeclaration.class));
NameDef var4= ((VarDeclaration) var3).getNameDef();
assertThat("",var4,instanceOf(NameDef.class));
assertEquals(Type.FLOAT, ((NameDef) var4).getType());
assertEquals("b", ((NameDef) var4).getName());
Expr var5= ((VarDeclaration) var3).getExpr();
assertThat("",var5,instanceOf(FloatLitExpr.class));
assertEquals(4.8f, ((FloatLitExpr) var5).getValue());
assertEquals(Type.FLOAT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var3).getOp().getKind());
ASTNode var6= decsAndStatements.get(2);
assertThat("",var6,instanceOf(ReturnStatement.class));
Expr var7= ((ReturnStatement) var6).getExpr();
assertThat("",var7,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var7).getOp().getKind());
Expr var8= ((BinaryExpr) var7).getLeft();
assertThat("",var8,instanceOf(IdentExpr.class));
assertEquals("a", var8.getText());
assertEquals(Type.FLOAT,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
Expr var9= ((BinaryExpr) var7).getRight();
assertThat("",var9,instanceOf(IdentExpr.class));
assertEquals("b", var9.getText());
assertEquals(Type.FLOAT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
assertEquals(Type.FLOAT,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
});
}

@DisplayName("test113")
@Test public void test113(TestInfo testInfo) throws Exception{
String input = """
int a() ^ 0;

""";
show("\n\n----- test113 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.INT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(ReturnStatement.class));
Expr var1= ((ReturnStatement) var0).getExpr();
assertThat("",var1,instanceOf(IntLitExpr.class));
assertEquals(0, ((IntLitExpr) var1).getValue());
assertEquals(Type.INT,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
});
}

@DisplayName("test114")
@Test public void test114(TestInfo testInfo) throws Exception{
String input = """
color f()
^ RED;

""";
show("\n\n----- test114 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(ReturnStatement.class));
Expr var1= ((ReturnStatement) var0).getExpr();
assertThat("",var1,instanceOf(ColorConstExpr.class));
assertEquals("RED", var1.getText());
assertEquals(Type.COLOR,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
});
}

@DisplayName("test115")
@Test public void test115(TestInfo testInfo) throws Exception{
String input = """
int f() int x; x = 33; ^ x;

""";
show("\n\n----- test115 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.INT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
ASTNode var2= decsAndStatements.get(1);
assertThat("",var2,instanceOf(AssignmentStatement.class));
assertEquals("x", ((AssignmentStatement)var2).getName());
assertNull(((AssignmentStatement) var2).getSelector());
Expr var3= ((AssignmentStatement) var2).getExpr();
assertThat("",var3,instanceOf(IntLitExpr.class));
assertEquals(33, ((IntLitExpr) var3).getValue());
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
ASTNode var4= decsAndStatements.get(2);
assertThat("",var4,instanceOf(ReturnStatement.class));
Expr var5= ((ReturnStatement) var4).getExpr();
assertThat("",var5,instanceOf(IdentExpr.class));
assertEquals("x", var5.getText());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
});
}

@DisplayName("test116")
@Test public void test116(TestInfo testInfo) throws Exception{
String input = """
void withParams(image x)

""";
show("\n\n----- test116 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.VOID, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(1, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.IMAGE, ((NameDef) var0).getType());
assertEquals("x", ((NameDef) var0).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(0, decsAndStatements.size());
});
}

@DisplayName("test117")
@Test public void test117(TestInfo testInfo) throws Exception{
String input = """
color f()
^ CYAN;

""";
show("\n\n----- test117 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(ReturnStatement.class));
Expr var1= ((ReturnStatement) var0).getExpr();
assertThat("",var1,instanceOf(ColorConstExpr.class));
assertEquals("CYAN", var1.getText());
assertEquals(Type.COLOR,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
});
}

@DisplayName("test118")
@Test public void test118(TestInfo testInfo) throws Exception{
String input = """
image addition(int size)
image[size,size] f;
f[x,y] = x+y;
^f;

""";
show("\n\n----- test118 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.IMAGE, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(1, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var0).getType());
assertEquals("size", ((NameDef) var0).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var1= decsAndStatements.get(0);
assertThat("",var1,instanceOf(VarDeclaration.class));
NameDef var2= ((VarDeclaration) var1).getNameDef();
assertThat("",var2,instanceOf(NameDef.class));
assertEquals(Type.IMAGE, ((NameDef) var2).getType());
assertEquals("f", ((NameDef) var2).getName());
Dimension var3= ((NameDefWithDim) var2).getDim();
assertThat("",var3,instanceOf(Dimension.class));
Expr var4= ((Dimension) var3).getWidth();
assertThat("",var4,instanceOf(IdentExpr.class));
assertEquals("size", var4.getText());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((Dimension) var3).getHeight();
assertThat("",var5,instanceOf(IdentExpr.class));
assertEquals("size", var5.getText());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
ASTNode var6= decsAndStatements.get(1);
assertThat("",var6,instanceOf(AssignmentStatement.class));
assertEquals("f", ((AssignmentStatement)var6).getName());
PixelSelector var7= ((AssignmentStatement) var6).getSelector();
assertThat("",var7,instanceOf(PixelSelector.class));
Expr var8= ((PixelSelector) var7).getX();
assertThat("",var8,instanceOf(IdentExpr.class));
assertEquals("x", var8.getText());
assertEquals(Type.INT,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
Expr var9= ((PixelSelector) var7).getY();
assertThat("",var9,instanceOf(IdentExpr.class));
assertEquals("y", var9.getText());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
Expr var10= ((AssignmentStatement) var6).getExpr();
assertThat("",var10,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var10).getOp().getKind());
Expr var11= ((BinaryExpr) var10).getLeft();
assertThat("",var11,instanceOf(IdentExpr.class));
assertEquals("x", var11.getText());
assertEquals(Type.INT,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
Expr var12= ((BinaryExpr) var10).getRight();
assertThat("",var12,instanceOf(IdentExpr.class));
assertEquals("y", var12.getText());
assertEquals(Type.INT,var12.getType());
assertThat(var12.getCoerceTo(),anyOf(nullValue(), is(var12.getType())));
assertEquals(Type.INT,var10.getType());
assertEquals(Type.COLOR,var10.getCoerceTo());
ASTNode var13= decsAndStatements.get(2);
assertThat("",var13,instanceOf(ReturnStatement.class));
Expr var14= ((ReturnStatement) var13).getExpr();
assertThat("",var14,instanceOf(IdentExpr.class));
assertEquals("f", var14.getText());
assertEquals(Type.IMAGE,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
});
}

@DisplayName("test119")
@Test public void test119(TestInfo testInfo) throws Exception{
String input = """
string f() string x; x = "abc"; ^ x;

""";
show("\n\n----- test119 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.STRING, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.STRING, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
ASTNode var2= decsAndStatements.get(1);
assertThat("",var2,instanceOf(AssignmentStatement.class));
assertEquals("x", ((AssignmentStatement)var2).getName());
assertNull(((AssignmentStatement) var2).getSelector());
Expr var3= ((AssignmentStatement) var2).getExpr();
assertThat("",var3,instanceOf(StringLitExpr.class));
assertEquals("abc", ((StringLitExpr) var3).getValue());
assertEquals(Type.STRING,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
ASTNode var4= decsAndStatements.get(2);
assertThat("",var4,instanceOf(ReturnStatement.class));
Expr var5= ((ReturnStatement) var4).getExpr();
assertThat("",var5,instanceOf(IdentExpr.class));
assertEquals("x", var5.getText());
assertEquals(Type.STRING,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
});
}

@DisplayName("test120")
@Test public void test120(TestInfo testInfo) throws Exception{
String input = """
void withParams(boolean x)

""";
show("\n\n----- test120 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.VOID, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(1, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.BOOLEAN, ((NameDef) var0).getType());
assertEquals("x", ((NameDef) var0).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(0, decsAndStatements.size());
});
}

@DisplayName("test121")
@Test public void test121(TestInfo testInfo) throws Exception{
String input = """
image testImageWriteToFile()
int Z = 255;
image[1024,1024] a;
a[x,y] = <<(x/8*y/8)%(Z+1),0,0>>;
write a -> "testImageWriteToFile";
^ a;

""";
show("\n\n----- test121 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.IMAGE, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(5, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var1).getType());
assertEquals("Z", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(IntLitExpr.class));
assertEquals(255, ((IntLitExpr) var2).getValue());
assertEquals(Type.INT,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(VarDeclaration.class));
NameDef var4= ((VarDeclaration) var3).getNameDef();
assertThat("",var4,instanceOf(NameDef.class));
assertEquals(Type.IMAGE, ((NameDef) var4).getType());
assertEquals("a", ((NameDef) var4).getName());
Dimension var5= ((NameDefWithDim) var4).getDim();
assertThat("",var5,instanceOf(Dimension.class));
Expr var6= ((Dimension) var5).getWidth();
assertThat("",var6,instanceOf(IntLitExpr.class));
assertEquals(1024, ((IntLitExpr) var6).getValue());
assertEquals(Type.INT,var6.getType());
assertThat(var6.getCoerceTo(),anyOf(nullValue(), is(var6.getType())));
Expr var7= ((Dimension) var5).getHeight();
assertThat("",var7,instanceOf(IntLitExpr.class));
assertEquals(1024, ((IntLitExpr) var7).getValue());
assertEquals(Type.INT,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
ASTNode var8= decsAndStatements.get(2);
assertThat("",var8,instanceOf(AssignmentStatement.class));
assertEquals("a", ((AssignmentStatement)var8).getName());
PixelSelector var9= ((AssignmentStatement) var8).getSelector();
assertThat("",var9,instanceOf(PixelSelector.class));
Expr var10= ((PixelSelector) var9).getX();
assertThat("",var10,instanceOf(IdentExpr.class));
assertEquals("x", var10.getText());
assertEquals(Type.INT,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
Expr var11= ((PixelSelector) var9).getY();
assertThat("",var11,instanceOf(IdentExpr.class));
assertEquals("y", var11.getText());
assertEquals(Type.INT,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
Expr var12= ((AssignmentStatement) var8).getExpr();
assertThat("",var12,instanceOf(ColorExpr.class));
Expr var13= ((ColorExpr) var12).getRed();
assertThat("",var13,instanceOf(BinaryExpr.class));
assertEquals(MOD, ((BinaryExpr) var13).getOp().getKind());
Expr var14= ((BinaryExpr) var13).getLeft();
assertThat("",var14,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var14).getOp().getKind());
Expr var15= ((BinaryExpr) var14).getLeft();
assertThat("",var15,instanceOf(BinaryExpr.class));
assertEquals(TIMES, ((BinaryExpr) var15).getOp().getKind());
Expr var16= ((BinaryExpr) var15).getLeft();
assertThat("",var16,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var16).getOp().getKind());
Expr var17= ((BinaryExpr) var16).getLeft();
assertThat("",var17,instanceOf(IdentExpr.class));
assertEquals("x", var17.getText());
assertEquals(Type.INT,var17.getType());
assertThat(var17.getCoerceTo(),anyOf(nullValue(), is(var17.getType())));
Expr var18= ((BinaryExpr) var16).getRight();
assertThat("",var18,instanceOf(IntLitExpr.class));
assertEquals(8, ((IntLitExpr) var18).getValue());
assertEquals(Type.INT,var18.getType());
assertThat(var18.getCoerceTo(),anyOf(nullValue(), is(var18.getType())));
assertEquals(Type.INT,var16.getType());
assertThat(var16.getCoerceTo(),anyOf(nullValue(), is(var16.getType())));
Expr var19= ((BinaryExpr) var15).getRight();
assertThat("",var19,instanceOf(IdentExpr.class));
assertEquals("y", var19.getText());
assertEquals(Type.INT,var19.getType());
assertThat(var19.getCoerceTo(),anyOf(nullValue(), is(var19.getType())));
assertEquals(Type.INT,var15.getType());
assertThat(var15.getCoerceTo(),anyOf(nullValue(), is(var15.getType())));
Expr var20= ((BinaryExpr) var14).getRight();
assertThat("",var20,instanceOf(IntLitExpr.class));
assertEquals(8, ((IntLitExpr) var20).getValue());
assertEquals(Type.INT,var20.getType());
assertThat(var20.getCoerceTo(),anyOf(nullValue(), is(var20.getType())));
assertEquals(Type.INT,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
Expr var21= ((BinaryExpr) var13).getRight();
assertThat("",var21,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var21).getOp().getKind());
Expr var22= ((BinaryExpr) var21).getLeft();
assertThat("",var22,instanceOf(IdentExpr.class));
assertEquals("Z", var22.getText());
assertEquals(Type.INT,var22.getType());
assertThat(var22.getCoerceTo(),anyOf(nullValue(), is(var22.getType())));
Expr var23= ((BinaryExpr) var21).getRight();
assertThat("",var23,instanceOf(IntLitExpr.class));
assertEquals(1, ((IntLitExpr) var23).getValue());
assertEquals(Type.INT,var23.getType());
assertThat(var23.getCoerceTo(),anyOf(nullValue(), is(var23.getType())));
assertEquals(Type.INT,var21.getType());
assertThat(var21.getCoerceTo(),anyOf(nullValue(), is(var21.getType())));
assertEquals(Type.INT,var13.getType());
assertThat(var13.getCoerceTo(),anyOf(nullValue(), is(var13.getType())));
Expr var24= ((ColorExpr) var12).getGreen();
assertThat("",var24,instanceOf(IntLitExpr.class));
assertEquals(0, ((IntLitExpr) var24).getValue());
assertEquals(Type.INT,var24.getType());
assertThat(var24.getCoerceTo(),anyOf(nullValue(), is(var24.getType())));
Expr var25= ((ColorExpr) var12).getBlue();
assertThat("",var25,instanceOf(IntLitExpr.class));
assertEquals(0, ((IntLitExpr) var25).getValue());
assertEquals(Type.INT,var25.getType());
assertThat(var25.getCoerceTo(),anyOf(nullValue(), is(var25.getType())));
assertEquals(Type.COLOR,var12.getType());
assertThat(var12.getCoerceTo(),anyOf(nullValue(), is(var12.getType())));
ASTNode var26= decsAndStatements.get(3);
assertThat("",var26,instanceOf(WriteStatement.class));
Expr var27= ((WriteStatement) var26).getSource();
assertThat("",var27,instanceOf(IdentExpr.class));
assertEquals("a", var27.getText());
assertEquals(Type.IMAGE,var27.getType());
assertThat(var27.getCoerceTo(),anyOf(nullValue(), is(var27.getType())));
Expr var28= ((WriteStatement) var26).getDest();
assertThat("",var28,instanceOf(StringLitExpr.class));
assertEquals("testImageWriteToFile", ((StringLitExpr) var28).getValue());
assertEquals(Type.STRING,var28.getType());
assertThat(var28.getCoerceTo(),anyOf(nullValue(), is(var28.getType())));
ASTNode var29= decsAndStatements.get(4);
assertThat("",var29,instanceOf(ReturnStatement.class));
Expr var30= ((ReturnStatement) var29).getExpr();
assertThat("",var30,instanceOf(IdentExpr.class));
assertEquals("a", var30.getText());
assertEquals(Type.IMAGE,var30.getType());
assertThat(var30.getCoerceTo(),anyOf(nullValue(), is(var30.getType())));
});
}

@DisplayName("test122")
@Test public void test122(TestInfo testInfo) throws Exception{
String input = """
image BDP0()
int Z = 255;
image[1024,1024] a;
int s = 16;
a[x,y] = <<(x/s)%((y/s)+1), (x/s)%((y/s)+1), (x/s)%((y/s)+1)>>*5;
^ a;

""";
show("\n\n----- test122 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.IMAGE, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(5, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var1).getType());
assertEquals("Z", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(IntLitExpr.class));
assertEquals(255, ((IntLitExpr) var2).getValue());
assertEquals(Type.INT,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(VarDeclaration.class));
NameDef var4= ((VarDeclaration) var3).getNameDef();
assertThat("",var4,instanceOf(NameDef.class));
assertEquals(Type.IMAGE, ((NameDef) var4).getType());
assertEquals("a", ((NameDef) var4).getName());
Dimension var5= ((NameDefWithDim) var4).getDim();
assertThat("",var5,instanceOf(Dimension.class));
Expr var6= ((Dimension) var5).getWidth();
assertThat("",var6,instanceOf(IntLitExpr.class));
assertEquals(1024, ((IntLitExpr) var6).getValue());
assertEquals(Type.INT,var6.getType());
assertThat(var6.getCoerceTo(),anyOf(nullValue(), is(var6.getType())));
Expr var7= ((Dimension) var5).getHeight();
assertThat("",var7,instanceOf(IntLitExpr.class));
assertEquals(1024, ((IntLitExpr) var7).getValue());
assertEquals(Type.INT,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
ASTNode var8= decsAndStatements.get(2);
assertThat("",var8,instanceOf(VarDeclaration.class));
NameDef var9= ((VarDeclaration) var8).getNameDef();
assertThat("",var9,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var9).getType());
assertEquals("s", ((NameDef) var9).getName());
Expr var10= ((VarDeclaration) var8).getExpr();
assertThat("",var10,instanceOf(IntLitExpr.class));
assertEquals(16, ((IntLitExpr) var10).getValue());
assertEquals(Type.INT,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var8).getOp().getKind());
ASTNode var11= decsAndStatements.get(3);
assertThat("",var11,instanceOf(AssignmentStatement.class));
assertEquals("a", ((AssignmentStatement)var11).getName());
PixelSelector var12= ((AssignmentStatement) var11).getSelector();
assertThat("",var12,instanceOf(PixelSelector.class));
Expr var13= ((PixelSelector) var12).getX();
assertThat("",var13,instanceOf(IdentExpr.class));
assertEquals("x", var13.getText());
assertEquals(Type.INT,var13.getType());
assertThat(var13.getCoerceTo(),anyOf(nullValue(), is(var13.getType())));
Expr var14= ((PixelSelector) var12).getY();
assertThat("",var14,instanceOf(IdentExpr.class));
assertEquals("y", var14.getText());
assertEquals(Type.INT,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
Expr var15= ((AssignmentStatement) var11).getExpr();
assertThat("",var15,instanceOf(BinaryExpr.class));
assertEquals(TIMES, ((BinaryExpr) var15).getOp().getKind());
Expr var16= ((BinaryExpr) var15).getLeft();
assertThat("",var16,instanceOf(ColorExpr.class));
Expr var17= ((ColorExpr) var16).getRed();
assertThat("",var17,instanceOf(BinaryExpr.class));
assertEquals(MOD, ((BinaryExpr) var17).getOp().getKind());
Expr var18= ((BinaryExpr) var17).getLeft();
assertThat("",var18,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var18).getOp().getKind());
Expr var19= ((BinaryExpr) var18).getLeft();
assertThat("",var19,instanceOf(IdentExpr.class));
assertEquals("x", var19.getText());
assertEquals(Type.INT,var19.getType());
assertThat(var19.getCoerceTo(),anyOf(nullValue(), is(var19.getType())));
Expr var20= ((BinaryExpr) var18).getRight();
assertThat("",var20,instanceOf(IdentExpr.class));
assertEquals("s", var20.getText());
assertEquals(Type.INT,var20.getType());
assertThat(var20.getCoerceTo(),anyOf(nullValue(), is(var20.getType())));
assertEquals(Type.INT,var18.getType());
assertThat(var18.getCoerceTo(),anyOf(nullValue(), is(var18.getType())));
Expr var21= ((BinaryExpr) var17).getRight();
assertThat("",var21,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var21).getOp().getKind());
Expr var22= ((BinaryExpr) var21).getLeft();
assertThat("",var22,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var22).getOp().getKind());
Expr var23= ((BinaryExpr) var22).getLeft();
assertThat("",var23,instanceOf(IdentExpr.class));
assertEquals("y", var23.getText());
assertEquals(Type.INT,var23.getType());
assertThat(var23.getCoerceTo(),anyOf(nullValue(), is(var23.getType())));
Expr var24= ((BinaryExpr) var22).getRight();
assertThat("",var24,instanceOf(IdentExpr.class));
assertEquals("s", var24.getText());
assertEquals(Type.INT,var24.getType());
assertThat(var24.getCoerceTo(),anyOf(nullValue(), is(var24.getType())));
assertEquals(Type.INT,var22.getType());
assertThat(var22.getCoerceTo(),anyOf(nullValue(), is(var22.getType())));
Expr var25= ((BinaryExpr) var21).getRight();
assertThat("",var25,instanceOf(IntLitExpr.class));
assertEquals(1, ((IntLitExpr) var25).getValue());
assertEquals(Type.INT,var25.getType());
assertThat(var25.getCoerceTo(),anyOf(nullValue(), is(var25.getType())));
assertEquals(Type.INT,var21.getType());
assertThat(var21.getCoerceTo(),anyOf(nullValue(), is(var21.getType())));
assertEquals(Type.INT,var17.getType());
assertThat(var17.getCoerceTo(),anyOf(nullValue(), is(var17.getType())));
Expr var26= ((ColorExpr) var16).getGreen();
assertThat("",var26,instanceOf(BinaryExpr.class));
assertEquals(MOD, ((BinaryExpr) var26).getOp().getKind());
Expr var27= ((BinaryExpr) var26).getLeft();
assertThat("",var27,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var27).getOp().getKind());
Expr var28= ((BinaryExpr) var27).getLeft();
assertThat("",var28,instanceOf(IdentExpr.class));
assertEquals("x", var28.getText());
assertEquals(Type.INT,var28.getType());
assertThat(var28.getCoerceTo(),anyOf(nullValue(), is(var28.getType())));
Expr var29= ((BinaryExpr) var27).getRight();
assertThat("",var29,instanceOf(IdentExpr.class));
assertEquals("s", var29.getText());
assertEquals(Type.INT,var29.getType());
assertThat(var29.getCoerceTo(),anyOf(nullValue(), is(var29.getType())));
assertEquals(Type.INT,var27.getType());
assertThat(var27.getCoerceTo(),anyOf(nullValue(), is(var27.getType())));
Expr var30= ((BinaryExpr) var26).getRight();
assertThat("",var30,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var30).getOp().getKind());
Expr var31= ((BinaryExpr) var30).getLeft();
assertThat("",var31,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var31).getOp().getKind());
Expr var32= ((BinaryExpr) var31).getLeft();
assertThat("",var32,instanceOf(IdentExpr.class));
assertEquals("y", var32.getText());
assertEquals(Type.INT,var32.getType());
assertThat(var32.getCoerceTo(),anyOf(nullValue(), is(var32.getType())));
Expr var33= ((BinaryExpr) var31).getRight();
assertThat("",var33,instanceOf(IdentExpr.class));
assertEquals("s", var33.getText());
assertEquals(Type.INT,var33.getType());
assertThat(var33.getCoerceTo(),anyOf(nullValue(), is(var33.getType())));
assertEquals(Type.INT,var31.getType());
assertThat(var31.getCoerceTo(),anyOf(nullValue(), is(var31.getType())));
Expr var34= ((BinaryExpr) var30).getRight();
assertThat("",var34,instanceOf(IntLitExpr.class));
assertEquals(1, ((IntLitExpr) var34).getValue());
assertEquals(Type.INT,var34.getType());
assertThat(var34.getCoerceTo(),anyOf(nullValue(), is(var34.getType())));
assertEquals(Type.INT,var30.getType());
assertThat(var30.getCoerceTo(),anyOf(nullValue(), is(var30.getType())));
assertEquals(Type.INT,var26.getType());
assertThat(var26.getCoerceTo(),anyOf(nullValue(), is(var26.getType())));
Expr var35= ((ColorExpr) var16).getBlue();
assertThat("",var35,instanceOf(BinaryExpr.class));
assertEquals(MOD, ((BinaryExpr) var35).getOp().getKind());
Expr var36= ((BinaryExpr) var35).getLeft();
assertThat("",var36,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var36).getOp().getKind());
Expr var37= ((BinaryExpr) var36).getLeft();
assertThat("",var37,instanceOf(IdentExpr.class));
assertEquals("x", var37.getText());
assertEquals(Type.INT,var37.getType());
assertThat(var37.getCoerceTo(),anyOf(nullValue(), is(var37.getType())));
Expr var38= ((BinaryExpr) var36).getRight();
assertThat("",var38,instanceOf(IdentExpr.class));
assertEquals("s", var38.getText());
assertEquals(Type.INT,var38.getType());
assertThat(var38.getCoerceTo(),anyOf(nullValue(), is(var38.getType())));
assertEquals(Type.INT,var36.getType());
assertThat(var36.getCoerceTo(),anyOf(nullValue(), is(var36.getType())));
Expr var39= ((BinaryExpr) var35).getRight();
assertThat("",var39,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var39).getOp().getKind());
Expr var40= ((BinaryExpr) var39).getLeft();
assertThat("",var40,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var40).getOp().getKind());
Expr var41= ((BinaryExpr) var40).getLeft();
assertThat("",var41,instanceOf(IdentExpr.class));
assertEquals("y", var41.getText());
assertEquals(Type.INT,var41.getType());
assertThat(var41.getCoerceTo(),anyOf(nullValue(), is(var41.getType())));
Expr var42= ((BinaryExpr) var40).getRight();
assertThat("",var42,instanceOf(IdentExpr.class));
assertEquals("s", var42.getText());
assertEquals(Type.INT,var42.getType());
assertThat(var42.getCoerceTo(),anyOf(nullValue(), is(var42.getType())));
assertEquals(Type.INT,var40.getType());
assertThat(var40.getCoerceTo(),anyOf(nullValue(), is(var40.getType())));
Expr var43= ((BinaryExpr) var39).getRight();
assertThat("",var43,instanceOf(IntLitExpr.class));
assertEquals(1, ((IntLitExpr) var43).getValue());
assertEquals(Type.INT,var43.getType());
assertThat(var43.getCoerceTo(),anyOf(nullValue(), is(var43.getType())));
assertEquals(Type.INT,var39.getType());
assertThat(var39.getCoerceTo(),anyOf(nullValue(), is(var39.getType())));
assertEquals(Type.INT,var35.getType());
assertThat(var35.getCoerceTo(),anyOf(nullValue(), is(var35.getType())));
assertEquals(Type.COLOR,var16.getType());
assertThat(var16.getCoerceTo(),anyOf(nullValue(), is(var16.getType())));
Expr var44= ((BinaryExpr) var15).getRight();
assertThat("",var44,instanceOf(IntLitExpr.class));
assertEquals(5, ((IntLitExpr) var44).getValue());
assertEquals(Type.INT,var44.getType());
assertEquals(Type.COLOR,var44.getCoerceTo());
assertEquals(Type.COLOR,var15.getType());
assertThat(var15.getCoerceTo(),anyOf(nullValue(), is(var15.getType())));
ASTNode var45= decsAndStatements.get(4);
assertThat("",var45,instanceOf(ReturnStatement.class));
Expr var46= ((ReturnStatement) var45).getExpr();
assertThat("",var46,instanceOf(IdentExpr.class));
assertEquals("a", var46.getText());
assertEquals(Type.IMAGE,var46.getType());
assertThat(var46.getCoerceTo(),anyOf(nullValue(), is(var46.getType())));
});
}

@DisplayName("test123")
@Test public void test123(TestInfo testInfo) throws Exception{
String input = """
int testRead0()
int x;
x <- console;
^ x;

""";
show("\n\n----- test123 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.INT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
ASTNode var2= decsAndStatements.get(1);
assertThat("",var2,instanceOf(ReadStatement.class));
assertEquals("x", ((ReadStatement)var2).getName());
assertNull(((ReadStatement) var2).getSelector());
Expr var3= ((ReadStatement) var2).getSource();
assertThat("",var3,instanceOf(ConsoleExpr.class));
assertEquals(Type.CONSOLE,var3.getType());
assertEquals(Type.INT,var3.getCoerceTo());
ASTNode var4= decsAndStatements.get(2);
assertThat("",var4,instanceOf(ReturnStatement.class));
Expr var5= ((ReturnStatement) var4).getExpr();
assertThat("",var5,instanceOf(IdentExpr.class));
assertEquals("x", var5.getText());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
});
}

@DisplayName("test124")
@Test public void test124(TestInfo testInfo) throws Exception{
String input = """
int y() ^0;

""";
show("\n\n----- test124 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.INT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(ReturnStatement.class));
Expr var1= ((ReturnStatement) var0).getExpr();
assertThat("",var1,instanceOf(IntLitExpr.class));
assertEquals(0, ((IntLitExpr) var1).getValue());
assertEquals(Type.INT,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
});
}

@DisplayName("test125")
@Test public void test125(TestInfo testInfo) throws Exception{
String input = """
void withInitializedDecs()
color e = << 1, 2, 3>>;

""";
show("\n\n----- test125 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.VOID, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var1).getType());
assertEquals("e", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(ColorExpr.class));
Expr var3= ((ColorExpr) var2).getRed();
assertThat("",var3,instanceOf(IntLitExpr.class));
assertEquals(1, ((IntLitExpr) var3).getValue());
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
Expr var4= ((ColorExpr) var2).getGreen();
assertThat("",var4,instanceOf(IntLitExpr.class));
assertEquals(2, ((IntLitExpr) var4).getValue());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((ColorExpr) var2).getBlue();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(3, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(Type.COLOR,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
});
}

@DisplayName("test126")
@Test public void test126(TestInfo testInfo) throws Exception{
String input = """
int i()

""";
show("\n\n----- test126 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.INT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(0, decsAndStatements.size());
});
}

@DisplayName("test127")
@Test public void test127(TestInfo testInfo) throws Exception{
String input = """
void withUninitializedDecs()
boolean f;

""";
show("\n\n----- test127 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.VOID, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.BOOLEAN, ((NameDef) var1).getType());
assertEquals("f", ((NameDef) var1).getName());
});
}

@DisplayName("test128")
@Test public void test128(TestInfo testInfo) throws Exception{
String input = """
image testImageArithemtic0()
image[500,500] blue;
blue[x,y] = BLUE;
image[500,500] green;
green[a,b] = GREEN;
image[500,500] teal;
teal[x,y] = blue[x,y] + green[x,y];
^teal;

""";
show("\n\n----- test128 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.IMAGE, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(7, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.IMAGE, ((NameDef) var1).getType());
assertEquals("blue", ((NameDef) var1).getName());
Dimension var2= ((NameDefWithDim) var1).getDim();
assertThat("",var2,instanceOf(Dimension.class));
Expr var3= ((Dimension) var2).getWidth();
assertThat("",var3,instanceOf(IntLitExpr.class));
assertEquals(500, ((IntLitExpr) var3).getValue());
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
Expr var4= ((Dimension) var2).getHeight();
assertThat("",var4,instanceOf(IntLitExpr.class));
assertEquals(500, ((IntLitExpr) var4).getValue());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
ASTNode var5= decsAndStatements.get(1);
assertThat("",var5,instanceOf(AssignmentStatement.class));
assertEquals("blue", ((AssignmentStatement)var5).getName());
PixelSelector var6= ((AssignmentStatement) var5).getSelector();
assertThat("",var6,instanceOf(PixelSelector.class));
Expr var7= ((PixelSelector) var6).getX();
assertThat("",var7,instanceOf(IdentExpr.class));
assertEquals("x", var7.getText());
assertEquals(Type.INT,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
Expr var8= ((PixelSelector) var6).getY();
assertThat("",var8,instanceOf(IdentExpr.class));
assertEquals("y", var8.getText());
assertEquals(Type.INT,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
Expr var9= ((AssignmentStatement) var5).getExpr();
assertThat("",var9,instanceOf(ColorConstExpr.class));
assertEquals("BLUE", var9.getText());
assertEquals(Type.COLOR,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
ASTNode var10= decsAndStatements.get(2);
assertThat("",var10,instanceOf(VarDeclaration.class));
NameDef var11= ((VarDeclaration) var10).getNameDef();
assertThat("",var11,instanceOf(NameDef.class));
assertEquals(Type.IMAGE, ((NameDef) var11).getType());
assertEquals("green", ((NameDef) var11).getName());
Dimension var12= ((NameDefWithDim) var11).getDim();
assertThat("",var12,instanceOf(Dimension.class));
Expr var13= ((Dimension) var12).getWidth();
assertThat("",var13,instanceOf(IntLitExpr.class));
assertEquals(500, ((IntLitExpr) var13).getValue());
assertEquals(Type.INT,var13.getType());
assertThat(var13.getCoerceTo(),anyOf(nullValue(), is(var13.getType())));
Expr var14= ((Dimension) var12).getHeight();
assertThat("",var14,instanceOf(IntLitExpr.class));
assertEquals(500, ((IntLitExpr) var14).getValue());
assertEquals(Type.INT,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
ASTNode var15= decsAndStatements.get(3);
assertThat("",var15,instanceOf(AssignmentStatement.class));
assertEquals("green", ((AssignmentStatement)var15).getName());
PixelSelector var16= ((AssignmentStatement) var15).getSelector();
assertThat("",var16,instanceOf(PixelSelector.class));
Expr var17= ((PixelSelector) var16).getX();
assertThat("",var17,instanceOf(IdentExpr.class));
assertEquals("a", var17.getText());
assertEquals(Type.INT,var17.getType());
assertThat(var17.getCoerceTo(),anyOf(nullValue(), is(var17.getType())));
Expr var18= ((PixelSelector) var16).getY();
assertThat("",var18,instanceOf(IdentExpr.class));
assertEquals("b", var18.getText());
assertEquals(Type.INT,var18.getType());
assertThat(var18.getCoerceTo(),anyOf(nullValue(), is(var18.getType())));
Expr var19= ((AssignmentStatement) var15).getExpr();
assertThat("",var19,instanceOf(ColorConstExpr.class));
assertEquals("GREEN", var19.getText());
assertEquals(Type.COLOR,var19.getType());
assertThat(var19.getCoerceTo(),anyOf(nullValue(), is(var19.getType())));
ASTNode var20= decsAndStatements.get(4);
assertThat("",var20,instanceOf(VarDeclaration.class));
NameDef var21= ((VarDeclaration) var20).getNameDef();
assertThat("",var21,instanceOf(NameDef.class));
assertEquals(Type.IMAGE, ((NameDef) var21).getType());
assertEquals("teal", ((NameDef) var21).getName());
Dimension var22= ((NameDefWithDim) var21).getDim();
assertThat("",var22,instanceOf(Dimension.class));
Expr var23= ((Dimension) var22).getWidth();
assertThat("",var23,instanceOf(IntLitExpr.class));
assertEquals(500, ((IntLitExpr) var23).getValue());
assertEquals(Type.INT,var23.getType());
assertThat(var23.getCoerceTo(),anyOf(nullValue(), is(var23.getType())));
Expr var24= ((Dimension) var22).getHeight();
assertThat("",var24,instanceOf(IntLitExpr.class));
assertEquals(500, ((IntLitExpr) var24).getValue());
assertEquals(Type.INT,var24.getType());
assertThat(var24.getCoerceTo(),anyOf(nullValue(), is(var24.getType())));
ASTNode var25= decsAndStatements.get(5);
assertThat("",var25,instanceOf(AssignmentStatement.class));
assertEquals("teal", ((AssignmentStatement)var25).getName());
PixelSelector var26= ((AssignmentStatement) var25).getSelector();
assertThat("",var26,instanceOf(PixelSelector.class));
Expr var27= ((PixelSelector) var26).getX();
assertThat("",var27,instanceOf(IdentExpr.class));
assertEquals("x", var27.getText());
assertEquals(Type.INT,var27.getType());
assertThat(var27.getCoerceTo(),anyOf(nullValue(), is(var27.getType())));
Expr var28= ((PixelSelector) var26).getY();
assertThat("",var28,instanceOf(IdentExpr.class));
assertEquals("y", var28.getText());
assertEquals(Type.INT,var28.getType());
assertThat(var28.getCoerceTo(),anyOf(nullValue(), is(var28.getType())));
Expr var29= ((AssignmentStatement) var25).getExpr();
assertThat("",var29,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var29).getOp().getKind());
Expr var30= ((BinaryExpr) var29).getLeft();
assertThat("",var30,instanceOf(UnaryExprPostfix.class));
Expr var31= ((UnaryExprPostfix) var30).getExpr();
assertThat("",var31,instanceOf(IdentExpr.class));
assertEquals("blue", var31.getText());
assertEquals(Type.IMAGE,var31.getType());
assertThat(var31.getCoerceTo(),anyOf(nullValue(), is(var31.getType())));
PixelSelector var32= ((UnaryExprPostfix) var30).getSelector();
assertThat("",var32,instanceOf(PixelSelector.class));
Expr var33= ((PixelSelector) var32).getX();
assertThat("",var33,instanceOf(IdentExpr.class));
assertEquals("x", var33.getText());
assertEquals(Type.INT,var33.getType());
assertThat(var33.getCoerceTo(),anyOf(nullValue(), is(var33.getType())));
Expr var34= ((PixelSelector) var32).getY();
assertThat("",var34,instanceOf(IdentExpr.class));
assertEquals("y", var34.getText());
assertEquals(Type.INT,var34.getType());
assertThat(var34.getCoerceTo(),anyOf(nullValue(), is(var34.getType())));
assertEquals(Type.INT,var30.getType());
assertEquals(Type.COLOR,var30.getCoerceTo());
Expr var35= ((BinaryExpr) var29).getRight();
assertThat("",var35,instanceOf(UnaryExprPostfix.class));
Expr var36= ((UnaryExprPostfix) var35).getExpr();
assertThat("",var36,instanceOf(IdentExpr.class));
assertEquals("green", var36.getText());
assertEquals(Type.IMAGE,var36.getType());
assertThat(var36.getCoerceTo(),anyOf(nullValue(), is(var36.getType())));
PixelSelector var37= ((UnaryExprPostfix) var35).getSelector();
assertThat("",var37,instanceOf(PixelSelector.class));
Expr var38= ((PixelSelector) var37).getX();
assertThat("",var38,instanceOf(IdentExpr.class));
assertEquals("x", var38.getText());
assertEquals(Type.INT,var38.getType());
assertThat(var38.getCoerceTo(),anyOf(nullValue(), is(var38.getType())));
Expr var39= ((PixelSelector) var37).getY();
assertThat("",var39,instanceOf(IdentExpr.class));
assertEquals("y", var39.getText());
assertEquals(Type.INT,var39.getType());
assertThat(var39.getCoerceTo(),anyOf(nullValue(), is(var39.getType())));
assertEquals(Type.INT,var35.getType());
assertEquals(Type.COLOR,var35.getCoerceTo());
assertEquals(Type.COLOR,var29.getType());
assertThat(var29.getCoerceTo(),anyOf(nullValue(), is(var29.getType())));
ASTNode var40= decsAndStatements.get(6);
assertThat("",var40,instanceOf(ReturnStatement.class));
Expr var41= ((ReturnStatement) var40).getExpr();
assertThat("",var41,instanceOf(IdentExpr.class));
assertEquals("teal", var41.getText());
assertEquals(Type.IMAGE,var41.getType());
assertThat(var41.getCoerceTo(),anyOf(nullValue(), is(var41.getType())));
});
}

@DisplayName("test129")
@Test public void test129(TestInfo testInfo) throws Exception{
String input = """
float f()
float a = 20.0;
float b = 15.3;
^ a-b;

""";
show("\n\n----- test129 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.FLOAT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.FLOAT, ((NameDef) var1).getType());
assertEquals("a", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(FloatLitExpr.class));
assertEquals(20.0f, ((FloatLitExpr) var2).getValue());
assertEquals(Type.FLOAT,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(VarDeclaration.class));
NameDef var4= ((VarDeclaration) var3).getNameDef();
assertThat("",var4,instanceOf(NameDef.class));
assertEquals(Type.FLOAT, ((NameDef) var4).getType());
assertEquals("b", ((NameDef) var4).getName());
Expr var5= ((VarDeclaration) var3).getExpr();
assertThat("",var5,instanceOf(FloatLitExpr.class));
assertEquals(15.3f, ((FloatLitExpr) var5).getValue());
assertEquals(Type.FLOAT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var3).getOp().getKind());
ASTNode var6= decsAndStatements.get(2);
assertThat("",var6,instanceOf(ReturnStatement.class));
Expr var7= ((ReturnStatement) var6).getExpr();
assertThat("",var7,instanceOf(BinaryExpr.class));
assertEquals(MINUS, ((BinaryExpr) var7).getOp().getKind());
Expr var8= ((BinaryExpr) var7).getLeft();
assertThat("",var8,instanceOf(IdentExpr.class));
assertEquals("a", var8.getText());
assertEquals(Type.FLOAT,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
Expr var9= ((BinaryExpr) var7).getRight();
assertThat("",var9,instanceOf(IdentExpr.class));
assertEquals("b", var9.getText());
assertEquals(Type.FLOAT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
assertEquals(Type.FLOAT,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
});
}

@DisplayName("test130")
@Test public void test130(TestInfo testInfo) throws Exception{
String input = """
color f()
^ YELLOW;

""";
show("\n\n----- test130 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(ReturnStatement.class));
Expr var1= ((ReturnStatement) var0).getExpr();
assertThat("",var1,instanceOf(ColorConstExpr.class));
assertEquals("YELLOW", var1.getText());
assertEquals(Type.COLOR,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
});
}

@DisplayName("test131")
@Test public void test131(TestInfo testInfo) throws Exception{
String input = """
color f()
^ GRAY;

""";
show("\n\n----- test131 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(ReturnStatement.class));
Expr var1= ((ReturnStatement) var0).getExpr();
assertThat("",var1,instanceOf(ColorConstExpr.class));
assertEquals("GRAY", var1.getText());
assertEquals(Type.COLOR,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
});
}

@DisplayName("test132")
@Test public void test132(TestInfo testInfo) throws Exception{
String input = """
void withUninitializedDecs()
string e;

""";
show("\n\n----- test132 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.VOID, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.STRING, ((NameDef) var1).getType());
assertEquals("e", ((NameDef) var1).getName());
});
}

@DisplayName("test133")
@Test public void test133(TestInfo testInfo) throws Exception{
String input = """
boolean testWriteBooleanToFile(string file)
boolean x = false;
write x -> file;
^x;

""";
show("\n\n----- test133 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.BOOLEAN, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(1, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.STRING, ((NameDef) var0).getType());
assertEquals("file", ((NameDef) var0).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var1= decsAndStatements.get(0);
assertThat("",var1,instanceOf(VarDeclaration.class));
NameDef var2= ((VarDeclaration) var1).getNameDef();
assertThat("",var2,instanceOf(NameDef.class));
assertEquals(Type.BOOLEAN, ((NameDef) var2).getType());
assertEquals("x", ((NameDef) var2).getName());
Expr var3= ((VarDeclaration) var1).getExpr();
assertThat("",var3,instanceOf(BooleanLitExpr.class));
assertFalse(((BooleanLitExpr) var3).getValue());
assertEquals(Type.BOOLEAN,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var1).getOp().getKind());
ASTNode var4= decsAndStatements.get(1);
assertThat("",var4,instanceOf(WriteStatement.class));
Expr var5= ((WriteStatement) var4).getSource();
assertThat("",var5,instanceOf(IdentExpr.class));
assertEquals("x", var5.getText());
assertEquals(Type.BOOLEAN,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
Expr var6= ((WriteStatement) var4).getDest();
assertThat("",var6,instanceOf(IdentExpr.class));
assertEquals("file", var6.getText());
assertEquals(Type.STRING,var6.getType());
assertThat(var6.getCoerceTo(),anyOf(nullValue(), is(var6.getType())));
ASTNode var7= decsAndStatements.get(2);
assertThat("",var7,instanceOf(ReturnStatement.class));
Expr var8= ((ReturnStatement) var7).getExpr();
assertThat("",var8,instanceOf(IdentExpr.class));
assertEquals("x", var8.getText());
assertEquals(Type.BOOLEAN,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
});
}

@DisplayName("test134")
@Test public void test134(TestInfo testInfo) throws Exception{
String input = """
bool f(int a)
^ a % 2 == 0;

""";
show("\n\n----- test134 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
Exception e = assertThrows(SyntaxException.class, () -> {
@SuppressWarnings("unused")
ASTNode ast = getAST(input);
});
show("Expected SyntaxException:     " + e);
});
}

@DisplayName("test135")
@Test public void test135(TestInfo testInfo) throws Exception{
String input = """
color f()
^ DARK_GRAY;

""";
show("\n\n----- test135 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.COLOR, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(ReturnStatement.class));
Expr var1= ((ReturnStatement) var0).getExpr();
assertThat("",var1,instanceOf(ColorConstExpr.class));
assertEquals("DARK_GRAY", var1.getText());
assertEquals(Type.COLOR,var1.getType());
assertThat(var1.getCoerceTo(),anyOf(nullValue(), is(var1.getType())));
});
}

@DisplayName("test136")
@Test public void test136(TestInfo testInfo) throws Exception{
String input = """
int a(int b, int c)
^ b+c;

""";
show("\n\n----- test136 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.INT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(2, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var0).getType());
assertEquals("b", ((NameDef) var0).getName());
NameDef var1= params.get(1);
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var1).getType());
assertEquals("c", ((NameDef) var1).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var2= decsAndStatements.get(0);
assertThat("",var2,instanceOf(ReturnStatement.class));
Expr var3= ((ReturnStatement) var2).getExpr();
assertThat("",var3,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var3).getOp().getKind());
Expr var4= ((BinaryExpr) var3).getLeft();
assertThat("",var4,instanceOf(IdentExpr.class));
assertEquals("b", var4.getText());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((BinaryExpr) var3).getRight();
assertThat("",var5,instanceOf(IdentExpr.class));
assertEquals("c", var5.getText());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
});
}

@DisplayName("test137")
@Test public void test137(TestInfo testInfo) throws Exception{
String input = """
void withInitializedDecs()
color e = << 11+31, 10-5+37, 42*5/5>>;

""";
show("\n\n----- test137 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.VOID, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.COLOR, ((NameDef) var1).getType());
assertEquals("e", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(ColorExpr.class));
Expr var3= ((ColorExpr) var2).getRed();
assertThat("",var3,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var3).getOp().getKind());
Expr var4= ((BinaryExpr) var3).getLeft();
assertThat("",var4,instanceOf(IntLitExpr.class));
assertEquals(11, ((IntLitExpr) var4).getValue());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((BinaryExpr) var3).getRight();
assertThat("",var5,instanceOf(IntLitExpr.class));
assertEquals(31, ((IntLitExpr) var5).getValue());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
Expr var6= ((ColorExpr) var2).getGreen();
assertThat("",var6,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var6).getOp().getKind());
Expr var7= ((BinaryExpr) var6).getLeft();
assertThat("",var7,instanceOf(BinaryExpr.class));
assertEquals(MINUS, ((BinaryExpr) var7).getOp().getKind());
Expr var8= ((BinaryExpr) var7).getLeft();
assertThat("",var8,instanceOf(IntLitExpr.class));
assertEquals(10, ((IntLitExpr) var8).getValue());
assertEquals(Type.INT,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
Expr var9= ((BinaryExpr) var7).getRight();
assertThat("",var9,instanceOf(IntLitExpr.class));
assertEquals(5, ((IntLitExpr) var9).getValue());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
assertEquals(Type.INT,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
Expr var10= ((BinaryExpr) var6).getRight();
assertThat("",var10,instanceOf(IntLitExpr.class));
assertEquals(37, ((IntLitExpr) var10).getValue());
assertEquals(Type.INT,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
assertEquals(Type.INT,var6.getType());
assertThat(var6.getCoerceTo(),anyOf(nullValue(), is(var6.getType())));
Expr var11= ((ColorExpr) var2).getBlue();
assertThat("",var11,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var11).getOp().getKind());
Expr var12= ((BinaryExpr) var11).getLeft();
assertThat("",var12,instanceOf(BinaryExpr.class));
assertEquals(TIMES, ((BinaryExpr) var12).getOp().getKind());
Expr var13= ((BinaryExpr) var12).getLeft();
assertThat("",var13,instanceOf(IntLitExpr.class));
assertEquals(42, ((IntLitExpr) var13).getValue());
assertEquals(Type.INT,var13.getType());
assertThat(var13.getCoerceTo(),anyOf(nullValue(), is(var13.getType())));
Expr var14= ((BinaryExpr) var12).getRight();
assertThat("",var14,instanceOf(IntLitExpr.class));
assertEquals(5, ((IntLitExpr) var14).getValue());
assertEquals(Type.INT,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
assertEquals(Type.INT,var12.getType());
assertThat(var12.getCoerceTo(),anyOf(nullValue(), is(var12.getType())));
Expr var15= ((BinaryExpr) var11).getRight();
assertThat("",var15,instanceOf(IntLitExpr.class));
assertEquals(5, ((IntLitExpr) var15).getValue());
assertEquals(Type.INT,var15.getType());
assertThat(var15.getCoerceTo(),anyOf(nullValue(), is(var15.getType())));
assertEquals(Type.INT,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
assertEquals(Type.COLOR,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
});
}

@DisplayName("test138")
@Test public void test138(TestInfo testInfo) throws Exception{
String input = """
image addition(int size)
image[size,size] f;
f[x,y] = <<x,x,x>>%<<256,256,256>> +  <<y,y,y>>%<<256,256,256>>;
^f;

""";
show("\n\n----- test138 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.IMAGE, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(1, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var0).getType());
assertEquals("size", ((NameDef) var0).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(3, decsAndStatements.size());
ASTNode var1= decsAndStatements.get(0);
assertThat("",var1,instanceOf(VarDeclaration.class));
NameDef var2= ((VarDeclaration) var1).getNameDef();
assertThat("",var2,instanceOf(NameDef.class));
assertEquals(Type.IMAGE, ((NameDef) var2).getType());
assertEquals("f", ((NameDef) var2).getName());
Dimension var3= ((NameDefWithDim) var2).getDim();
assertThat("",var3,instanceOf(Dimension.class));
Expr var4= ((Dimension) var3).getWidth();
assertThat("",var4,instanceOf(IdentExpr.class));
assertEquals("size", var4.getText());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((Dimension) var3).getHeight();
assertThat("",var5,instanceOf(IdentExpr.class));
assertEquals("size", var5.getText());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
ASTNode var6= decsAndStatements.get(1);
assertThat("",var6,instanceOf(AssignmentStatement.class));
assertEquals("f", ((AssignmentStatement)var6).getName());
PixelSelector var7= ((AssignmentStatement) var6).getSelector();
assertThat("",var7,instanceOf(PixelSelector.class));
Expr var8= ((PixelSelector) var7).getX();
assertThat("",var8,instanceOf(IdentExpr.class));
assertEquals("x", var8.getText());
assertEquals(Type.INT,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
Expr var9= ((PixelSelector) var7).getY();
assertThat("",var9,instanceOf(IdentExpr.class));
assertEquals("y", var9.getText());
assertEquals(Type.INT,var9.getType());
assertThat(var9.getCoerceTo(),anyOf(nullValue(), is(var9.getType())));
Expr var10= ((AssignmentStatement) var6).getExpr();
assertThat("",var10,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var10).getOp().getKind());
Expr var11= ((BinaryExpr) var10).getLeft();
assertThat("",var11,instanceOf(BinaryExpr.class));
assertEquals(MOD, ((BinaryExpr) var11).getOp().getKind());
Expr var12= ((BinaryExpr) var11).getLeft();
assertThat("",var12,instanceOf(ColorExpr.class));
Expr var13= ((ColorExpr) var12).getRed();
assertThat("",var13,instanceOf(IdentExpr.class));
assertEquals("x", var13.getText());
assertEquals(Type.INT,var13.getType());
assertThat(var13.getCoerceTo(),anyOf(nullValue(), is(var13.getType())));
Expr var14= ((ColorExpr) var12).getGreen();
assertThat("",var14,instanceOf(IdentExpr.class));
assertEquals("x", var14.getText());
assertEquals(Type.INT,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
Expr var15= ((ColorExpr) var12).getBlue();
assertThat("",var15,instanceOf(IdentExpr.class));
assertEquals("x", var15.getText());
assertEquals(Type.INT,var15.getType());
assertThat(var15.getCoerceTo(),anyOf(nullValue(), is(var15.getType())));
assertEquals(Type.COLOR,var12.getType());
assertThat(var12.getCoerceTo(),anyOf(nullValue(), is(var12.getType())));
Expr var16= ((BinaryExpr) var11).getRight();
assertThat("",var16,instanceOf(ColorExpr.class));
Expr var17= ((ColorExpr) var16).getRed();
assertThat("",var17,instanceOf(IntLitExpr.class));
assertEquals(256, ((IntLitExpr) var17).getValue());
assertEquals(Type.INT,var17.getType());
assertThat(var17.getCoerceTo(),anyOf(nullValue(), is(var17.getType())));
Expr var18= ((ColorExpr) var16).getGreen();
assertThat("",var18,instanceOf(IntLitExpr.class));
assertEquals(256, ((IntLitExpr) var18).getValue());
assertEquals(Type.INT,var18.getType());
assertThat(var18.getCoerceTo(),anyOf(nullValue(), is(var18.getType())));
Expr var19= ((ColorExpr) var16).getBlue();
assertThat("",var19,instanceOf(IntLitExpr.class));
assertEquals(256, ((IntLitExpr) var19).getValue());
assertEquals(Type.INT,var19.getType());
assertThat(var19.getCoerceTo(),anyOf(nullValue(), is(var19.getType())));
assertEquals(Type.COLOR,var16.getType());
assertThat(var16.getCoerceTo(),anyOf(nullValue(), is(var16.getType())));
assertEquals(Type.COLOR,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
Expr var20= ((BinaryExpr) var10).getRight();
assertThat("",var20,instanceOf(BinaryExpr.class));
assertEquals(MOD, ((BinaryExpr) var20).getOp().getKind());
Expr var21= ((BinaryExpr) var20).getLeft();
assertThat("",var21,instanceOf(ColorExpr.class));
Expr var22= ((ColorExpr) var21).getRed();
assertThat("",var22,instanceOf(IdentExpr.class));
assertEquals("y", var22.getText());
assertEquals(Type.INT,var22.getType());
assertThat(var22.getCoerceTo(),anyOf(nullValue(), is(var22.getType())));
Expr var23= ((ColorExpr) var21).getGreen();
assertThat("",var23,instanceOf(IdentExpr.class));
assertEquals("y", var23.getText());
assertEquals(Type.INT,var23.getType());
assertThat(var23.getCoerceTo(),anyOf(nullValue(), is(var23.getType())));
Expr var24= ((ColorExpr) var21).getBlue();
assertThat("",var24,instanceOf(IdentExpr.class));
assertEquals("y", var24.getText());
assertEquals(Type.INT,var24.getType());
assertThat(var24.getCoerceTo(),anyOf(nullValue(), is(var24.getType())));
assertEquals(Type.COLOR,var21.getType());
assertThat(var21.getCoerceTo(),anyOf(nullValue(), is(var21.getType())));
Expr var25= ((BinaryExpr) var20).getRight();
assertThat("",var25,instanceOf(ColorExpr.class));
Expr var26= ((ColorExpr) var25).getRed();
assertThat("",var26,instanceOf(IntLitExpr.class));
assertEquals(256, ((IntLitExpr) var26).getValue());
assertEquals(Type.INT,var26.getType());
assertThat(var26.getCoerceTo(),anyOf(nullValue(), is(var26.getType())));
Expr var27= ((ColorExpr) var25).getGreen();
assertThat("",var27,instanceOf(IntLitExpr.class));
assertEquals(256, ((IntLitExpr) var27).getValue());
assertEquals(Type.INT,var27.getType());
assertThat(var27.getCoerceTo(),anyOf(nullValue(), is(var27.getType())));
Expr var28= ((ColorExpr) var25).getBlue();
assertThat("",var28,instanceOf(IntLitExpr.class));
assertEquals(256, ((IntLitExpr) var28).getValue());
assertEquals(Type.INT,var28.getType());
assertThat(var28.getCoerceTo(),anyOf(nullValue(), is(var28.getType())));
assertEquals(Type.COLOR,var25.getType());
assertThat(var25.getCoerceTo(),anyOf(nullValue(), is(var25.getType())));
assertEquals(Type.COLOR,var20.getType());
assertThat(var20.getCoerceTo(),anyOf(nullValue(), is(var20.getType())));
assertEquals(Type.COLOR,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
ASTNode var29= decsAndStatements.get(2);
assertThat("",var29,instanceOf(ReturnStatement.class));
Expr var30= ((ReturnStatement) var29).getExpr();
assertThat("",var30,instanceOf(IdentExpr.class));
assertEquals("f", var30.getText());
assertEquals(Type.IMAGE,var30.getType());
assertThat(var30.getCoerceTo(),anyOf(nullValue(), is(var30.getType())));
});
}

@DisplayName("test139")
@Test public void test139(TestInfo testInfo) throws Exception{
String input = """
float testWriteFloatToFile(string file)
float x = 23.45;
write x -> file;
float y;
y <- file;
^y;

""";
show("\n\n----- test139 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.FLOAT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(1, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.STRING, ((NameDef) var0).getType());
assertEquals("file", ((NameDef) var0).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(5, decsAndStatements.size());
ASTNode var1= decsAndStatements.get(0);
assertThat("",var1,instanceOf(VarDeclaration.class));
NameDef var2= ((VarDeclaration) var1).getNameDef();
assertThat("",var2,instanceOf(NameDef.class));
assertEquals(Type.FLOAT, ((NameDef) var2).getType());
assertEquals("x", ((NameDef) var2).getName());
Expr var3= ((VarDeclaration) var1).getExpr();
assertThat("",var3,instanceOf(FloatLitExpr.class));
assertEquals(23.45f, ((FloatLitExpr) var3).getValue());
assertEquals(Type.FLOAT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var1).getOp().getKind());
ASTNode var4= decsAndStatements.get(1);
assertThat("",var4,instanceOf(WriteStatement.class));
Expr var5= ((WriteStatement) var4).getSource();
assertThat("",var5,instanceOf(IdentExpr.class));
assertEquals("x", var5.getText());
assertEquals(Type.FLOAT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
Expr var6= ((WriteStatement) var4).getDest();
assertThat("",var6,instanceOf(IdentExpr.class));
assertEquals("file", var6.getText());
assertEquals(Type.STRING,var6.getType());
assertThat(var6.getCoerceTo(),anyOf(nullValue(), is(var6.getType())));
ASTNode var7= decsAndStatements.get(2);
assertThat("",var7,instanceOf(VarDeclaration.class));
NameDef var8= ((VarDeclaration) var7).getNameDef();
assertThat("",var8,instanceOf(NameDef.class));
assertEquals(Type.FLOAT, ((NameDef) var8).getType());
assertEquals("y", ((NameDef) var8).getName());
ASTNode var9= decsAndStatements.get(3);
assertThat("",var9,instanceOf(ReadStatement.class));
assertEquals("y", ((ReadStatement)var9).getName());
assertNull(((ReadStatement) var9).getSelector());
Expr var10= ((ReadStatement) var9).getSource();
assertThat("",var10,instanceOf(IdentExpr.class));
assertEquals("file", var10.getText());
assertEquals(Type.STRING,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
ASTNode var11= decsAndStatements.get(4);
assertThat("",var11,instanceOf(ReturnStatement.class));
Expr var12= ((ReturnStatement) var11).getExpr();
assertThat("",var12,instanceOf(IdentExpr.class));
assertEquals("y", var12.getText());
assertEquals(Type.FLOAT,var12.getType());
assertThat(var12.getCoerceTo(),anyOf(nullValue(), is(var12.getType())));
});
}

@DisplayName("test140")
@Test public void test140(TestInfo testInfo) throws Exception{
String input = """
void withParams(int x)

""";
show("\n\n----- test140 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.VOID, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(1, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var0).getType());
assertEquals("x", ((NameDef) var0).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(0, decsAndStatements.size());
});
}

@DisplayName("test141")
@Test public void test141(TestInfo testInfo) throws Exception{
String input = """
image BDP0()
int Z = 255;
image[1024,1024] a;
a[x,y] = <<(x/8*y/8)%(Z+1),0,0>>;
^ a;

""";
show("\n\n----- test141 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.IMAGE, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(4, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var1).getType());
assertEquals("Z", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(IntLitExpr.class));
assertEquals(255, ((IntLitExpr) var2).getValue());
assertEquals(Type.INT,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(VarDeclaration.class));
NameDef var4= ((VarDeclaration) var3).getNameDef();
assertThat("",var4,instanceOf(NameDef.class));
assertEquals(Type.IMAGE, ((NameDef) var4).getType());
assertEquals("a", ((NameDef) var4).getName());
Dimension var5= ((NameDefWithDim) var4).getDim();
assertThat("",var5,instanceOf(Dimension.class));
Expr var6= ((Dimension) var5).getWidth();
assertThat("",var6,instanceOf(IntLitExpr.class));
assertEquals(1024, ((IntLitExpr) var6).getValue());
assertEquals(Type.INT,var6.getType());
assertThat(var6.getCoerceTo(),anyOf(nullValue(), is(var6.getType())));
Expr var7= ((Dimension) var5).getHeight();
assertThat("",var7,instanceOf(IntLitExpr.class));
assertEquals(1024, ((IntLitExpr) var7).getValue());
assertEquals(Type.INT,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
ASTNode var8= decsAndStatements.get(2);
assertThat("",var8,instanceOf(AssignmentStatement.class));
assertEquals("a", ((AssignmentStatement)var8).getName());
PixelSelector var9= ((AssignmentStatement) var8).getSelector();
assertThat("",var9,instanceOf(PixelSelector.class));
Expr var10= ((PixelSelector) var9).getX();
assertThat("",var10,instanceOf(IdentExpr.class));
assertEquals("x", var10.getText());
assertEquals(Type.INT,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
Expr var11= ((PixelSelector) var9).getY();
assertThat("",var11,instanceOf(IdentExpr.class));
assertEquals("y", var11.getText());
assertEquals(Type.INT,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
Expr var12= ((AssignmentStatement) var8).getExpr();
assertThat("",var12,instanceOf(ColorExpr.class));
Expr var13= ((ColorExpr) var12).getRed();
assertThat("",var13,instanceOf(BinaryExpr.class));
assertEquals(MOD, ((BinaryExpr) var13).getOp().getKind());
Expr var14= ((BinaryExpr) var13).getLeft();
assertThat("",var14,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var14).getOp().getKind());
Expr var15= ((BinaryExpr) var14).getLeft();
assertThat("",var15,instanceOf(BinaryExpr.class));
assertEquals(TIMES, ((BinaryExpr) var15).getOp().getKind());
Expr var16= ((BinaryExpr) var15).getLeft();
assertThat("",var16,instanceOf(BinaryExpr.class));
assertEquals(DIV, ((BinaryExpr) var16).getOp().getKind());
Expr var17= ((BinaryExpr) var16).getLeft();
assertThat("",var17,instanceOf(IdentExpr.class));
assertEquals("x", var17.getText());
assertEquals(Type.INT,var17.getType());
assertThat(var17.getCoerceTo(),anyOf(nullValue(), is(var17.getType())));
Expr var18= ((BinaryExpr) var16).getRight();
assertThat("",var18,instanceOf(IntLitExpr.class));
assertEquals(8, ((IntLitExpr) var18).getValue());
assertEquals(Type.INT,var18.getType());
assertThat(var18.getCoerceTo(),anyOf(nullValue(), is(var18.getType())));
assertEquals(Type.INT,var16.getType());
assertThat(var16.getCoerceTo(),anyOf(nullValue(), is(var16.getType())));
Expr var19= ((BinaryExpr) var15).getRight();
assertThat("",var19,instanceOf(IdentExpr.class));
assertEquals("y", var19.getText());
assertEquals(Type.INT,var19.getType());
assertThat(var19.getCoerceTo(),anyOf(nullValue(), is(var19.getType())));
assertEquals(Type.INT,var15.getType());
assertThat(var15.getCoerceTo(),anyOf(nullValue(), is(var15.getType())));
Expr var20= ((BinaryExpr) var14).getRight();
assertThat("",var20,instanceOf(IntLitExpr.class));
assertEquals(8, ((IntLitExpr) var20).getValue());
assertEquals(Type.INT,var20.getType());
assertThat(var20.getCoerceTo(),anyOf(nullValue(), is(var20.getType())));
assertEquals(Type.INT,var14.getType());
assertThat(var14.getCoerceTo(),anyOf(nullValue(), is(var14.getType())));
Expr var21= ((BinaryExpr) var13).getRight();
assertThat("",var21,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var21).getOp().getKind());
Expr var22= ((BinaryExpr) var21).getLeft();
assertThat("",var22,instanceOf(IdentExpr.class));
assertEquals("Z", var22.getText());
assertEquals(Type.INT,var22.getType());
assertThat(var22.getCoerceTo(),anyOf(nullValue(), is(var22.getType())));
Expr var23= ((BinaryExpr) var21).getRight();
assertThat("",var23,instanceOf(IntLitExpr.class));
assertEquals(1, ((IntLitExpr) var23).getValue());
assertEquals(Type.INT,var23.getType());
assertThat(var23.getCoerceTo(),anyOf(nullValue(), is(var23.getType())));
assertEquals(Type.INT,var21.getType());
assertThat(var21.getCoerceTo(),anyOf(nullValue(), is(var21.getType())));
assertEquals(Type.INT,var13.getType());
assertThat(var13.getCoerceTo(),anyOf(nullValue(), is(var13.getType())));
Expr var24= ((ColorExpr) var12).getGreen();
assertThat("",var24,instanceOf(IntLitExpr.class));
assertEquals(0, ((IntLitExpr) var24).getValue());
assertEquals(Type.INT,var24.getType());
assertThat(var24.getCoerceTo(),anyOf(nullValue(), is(var24.getType())));
Expr var25= ((ColorExpr) var12).getBlue();
assertThat("",var25,instanceOf(IntLitExpr.class));
assertEquals(0, ((IntLitExpr) var25).getValue());
assertEquals(Type.INT,var25.getType());
assertThat(var25.getCoerceTo(),anyOf(nullValue(), is(var25.getType())));
assertEquals(Type.COLOR,var12.getType());
assertThat(var12.getCoerceTo(),anyOf(nullValue(), is(var12.getType())));
ASTNode var26= decsAndStatements.get(3);
assertThat("",var26,instanceOf(ReturnStatement.class));
Expr var27= ((ReturnStatement) var26).getExpr();
assertThat("",var27,instanceOf(IdentExpr.class));
assertEquals("a", var27.getText());
assertEquals(Type.IMAGE,var27.getType());
assertThat(var27.getCoerceTo(),anyOf(nullValue(), is(var27.getType())));
});
}

@DisplayName("test142")
@Test public void test142(TestInfo testInfo) throws Exception{
String input = """
int f(int x)
int y = x;
^ y;

""";
show("\n\n----- test142 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.INT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(1, params.size());
NameDef var0= params.get(0);
assertThat("",var0,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var0).getType());
assertEquals("x", ((NameDef) var0).getName());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(2, decsAndStatements.size());
ASTNode var1= decsAndStatements.get(0);
assertThat("",var1,instanceOf(VarDeclaration.class));
NameDef var2= ((VarDeclaration) var1).getNameDef();
assertThat("",var2,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var2).getType());
assertEquals("y", ((NameDef) var2).getName());
Expr var3= ((VarDeclaration) var1).getExpr();
assertThat("",var3,instanceOf(IdentExpr.class));
assertEquals("x", var3.getText());
assertEquals(Type.INT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var1).getOp().getKind());
ASTNode var4= decsAndStatements.get(1);
assertThat("",var4,instanceOf(ReturnStatement.class));
Expr var5= ((ReturnStatement) var4).getExpr();
assertThat("",var5,instanceOf(IdentExpr.class));
assertEquals("y", var5.getText());
assertEquals(Type.INT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
});
}

@DisplayName("test143")
@Test public void test143(TestInfo testInfo) throws Exception{
String input = """
float a()
float x;
x = 3.33 * 5.55;
write x -> console;
^ x + 1;

""";
show("\n\n----- test143 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.FLOAT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(4, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.FLOAT, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
ASTNode var2= decsAndStatements.get(1);
assertThat("",var2,instanceOf(AssignmentStatement.class));
assertEquals("x", ((AssignmentStatement)var2).getName());
assertNull(((AssignmentStatement) var2).getSelector());
Expr var3= ((AssignmentStatement) var2).getExpr();
assertThat("",var3,instanceOf(BinaryExpr.class));
assertEquals(TIMES, ((BinaryExpr) var3).getOp().getKind());
Expr var4= ((BinaryExpr) var3).getLeft();
assertThat("",var4,instanceOf(FloatLitExpr.class));
assertEquals(3.33f, ((FloatLitExpr) var4).getValue());
assertEquals(Type.FLOAT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((BinaryExpr) var3).getRight();
assertThat("",var5,instanceOf(FloatLitExpr.class));
assertEquals(5.55f, ((FloatLitExpr) var5).getValue());
assertEquals(Type.FLOAT,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(Type.FLOAT,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
ASTNode var6= decsAndStatements.get(2);
assertThat("",var6,instanceOf(WriteStatement.class));
Expr var7= ((WriteStatement) var6).getSource();
assertThat("",var7,instanceOf(IdentExpr.class));
assertEquals("x", var7.getText());
assertEquals(Type.FLOAT,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
Expr var8= ((WriteStatement) var6).getDest();
assertThat("",var8,instanceOf(ConsoleExpr.class));
assertEquals(Type.CONSOLE,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
ASTNode var9= decsAndStatements.get(3);
assertThat("",var9,instanceOf(ReturnStatement.class));
Expr var10= ((ReturnStatement) var9).getExpr();
assertThat("",var10,instanceOf(BinaryExpr.class));
assertEquals(PLUS, ((BinaryExpr) var10).getOp().getKind());
Expr var11= ((BinaryExpr) var10).getLeft();
assertThat("",var11,instanceOf(IdentExpr.class));
assertEquals("x", var11.getText());
assertEquals(Type.FLOAT,var11.getType());
assertThat(var11.getCoerceTo(),anyOf(nullValue(), is(var11.getType())));
Expr var12= ((BinaryExpr) var10).getRight();
assertThat("",var12,instanceOf(IntLitExpr.class));
assertEquals(1, ((IntLitExpr) var12).getValue());
assertEquals(Type.INT,var12.getType());
assertEquals(Type.FLOAT,var12.getCoerceTo());
assertEquals(Type.FLOAT,var10.getType());
assertThat(var10.getCoerceTo(),anyOf(nullValue(), is(var10.getType())));
});
}

@DisplayName("test144")
@Test public void test144(TestInfo testInfo) throws Exception{
String input = """
void withInitializedDecs()
boolean c = true & false | (true & false);

""";
show("\n\n----- test144 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.VOID, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(1, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.BOOLEAN, ((NameDef) var1).getType());
assertEquals("c", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(BinaryExpr.class));
assertEquals(OR, ((BinaryExpr) var2).getOp().getKind());
Expr var3= ((BinaryExpr) var2).getLeft();
assertThat("",var3,instanceOf(BinaryExpr.class));
assertEquals(AND, ((BinaryExpr) var3).getOp().getKind());
Expr var4= ((BinaryExpr) var3).getLeft();
assertThat("",var4,instanceOf(BooleanLitExpr.class));
assertTrue(((BooleanLitExpr) var4).getValue());
assertEquals(Type.BOOLEAN,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
Expr var5= ((BinaryExpr) var3).getRight();
assertThat("",var5,instanceOf(BooleanLitExpr.class));
assertFalse(((BooleanLitExpr) var5).getValue());
assertEquals(Type.BOOLEAN,var5.getType());
assertThat(var5.getCoerceTo(),anyOf(nullValue(), is(var5.getType())));
assertEquals(Type.BOOLEAN,var3.getType());
assertThat(var3.getCoerceTo(),anyOf(nullValue(), is(var3.getType())));
Expr var6= ((BinaryExpr) var2).getRight();
assertThat("",var6,instanceOf(BinaryExpr.class));
assertEquals(AND, ((BinaryExpr) var6).getOp().getKind());
Expr var7= ((BinaryExpr) var6).getLeft();
assertThat("",var7,instanceOf(BooleanLitExpr.class));
assertTrue(((BooleanLitExpr) var7).getValue());
assertEquals(Type.BOOLEAN,var7.getType());
assertThat(var7.getCoerceTo(),anyOf(nullValue(), is(var7.getType())));
Expr var8= ((BinaryExpr) var6).getRight();
assertThat("",var8,instanceOf(BooleanLitExpr.class));
assertFalse(((BooleanLitExpr) var8).getValue());
assertEquals(Type.BOOLEAN,var8.getType());
assertThat(var8.getCoerceTo(),anyOf(nullValue(), is(var8.getType())));
assertEquals(Type.BOOLEAN,var6.getType());
assertThat(var6.getCoerceTo(),anyOf(nullValue(), is(var6.getType())));
assertEquals(Type.BOOLEAN,var2.getType());
assertThat(var2.getCoerceTo(),anyOf(nullValue(), is(var2.getType())));
assertEquals(ASSIGN, ((VarDeclaration) var0).getOp().getKind());
});
}

@DisplayName("test145")
@Test public void test145(TestInfo testInfo) throws Exception{
String input = """
int testReadInt1()
int x <- console;
^ x;

""";
show("\n\n----- test145 -----");show(input);
assertTimeoutPreemptively(Duration.ofSeconds(seconds), () -> {
ASTNode ast = getAST(input);
checkTypes(ast);
show(ast);
assertThat("",ast,instanceOf(Program.class));
assertEquals(Type.INT, ((Program) ast).getReturnType());
List<NameDef> params = ((Program)ast).getParams();
assertEquals(0, params.size());
List<ASTNode> decsAndStatements = ((Program)ast).getDecsAndStatements();
assertEquals(2, decsAndStatements.size());
ASTNode var0= decsAndStatements.get(0);
assertThat("",var0,instanceOf(VarDeclaration.class));
NameDef var1= ((VarDeclaration) var0).getNameDef();
assertThat("",var1,instanceOf(NameDef.class));
assertEquals(Type.INT, ((NameDef) var1).getType());
assertEquals("x", ((NameDef) var1).getName());
Expr var2= ((VarDeclaration) var0).getExpr();
assertThat("",var2,instanceOf(ConsoleExpr.class));
assertEquals(Type.CONSOLE,var2.getType());
assertEquals(Type.INT,var2.getCoerceTo());
assertEquals(LARROW, ((VarDeclaration) var0).getOp().getKind());
ASTNode var3= decsAndStatements.get(1);
assertThat("",var3,instanceOf(ReturnStatement.class));
Expr var4= ((ReturnStatement) var3).getExpr();
assertThat("",var4,instanceOf(IdentExpr.class));
assertEquals("x", var4.getText());
assertEquals(Type.INT,var4.getType());
assertThat(var4.getCoerceTo(),anyOf(nullValue(), is(var4.getType())));
});
}

}
