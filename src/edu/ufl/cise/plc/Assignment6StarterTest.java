package edu.ufl.cise.plc;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.Arrays;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import edu.ufl.cise.plc.LexicalException;
import edu.ufl.cise.plc.SyntaxException;
import edu.ufl.cise.plc.TypeCheckException;
import edu.ufl.cise.plc.ast.ASTNode;
import edu.ufl.cise.plc.ast.Program;
import edu.ufl.cise.plc.runtime.ColorTuple;
import edu.ufl.cise.plc.runtime.ConsoleIO;
import edu.ufl.cise.plc.runtime.FileURLIO;
import edu.ufl.cise.plc.runtime.ImageOps;
import edu.ufl.cise.plc.runtime.ImageOps.OP;
import edu.ufl.cise.plc.runtime.javaCompilerClassLoader.PLCLangExec;

public class Assignment6StarterTest {
	String packageName = "cop4020sp22Package";

	boolean CHECK_TIMEOUT = true;
	boolean CHECK_CONSOLE_IO = true;
	float DELTA = .001f; // used for comparing floats
	int TIMEOUT_SECONDS = 10;

	boolean VERBOSE = true;
	boolean SHOW_IMAGES = true;
	boolean PAUSE_IMAGES = true;
	int PAUSE_MILLIS = 2000;


	private Object show(Object obj) throws IOException {
		if (VERBOSE) {
			if (obj instanceof BufferedImage) {
				show((BufferedImage) obj);
				pauseImageDisplay();
			} else {
				System.out.println(obj);
			}
		}
		return obj;
	}

	private BufferedImage show(BufferedImage image) {
		if (VERBOSE && SHOW_IMAGES) {
			ConsoleIO.displayImageOnScreen(image);
			pauseImageDisplay();
		}
		return image;
	}

	private BufferedImage showRef(BufferedImage image) {
		if (VERBOSE && SHOW_IMAGES) {
			ConsoleIO.displayReferenceImageOnScreen(image);
			pauseImageDisplay();
		}
		return image;
	}

	private void pauseImageDisplay() {
		if (PAUSE_IMAGES) {
			try {
				Thread.sleep(PAUSE_MILLIS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

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

	void checkEqual(Object expected, Object actual) {
		if (expected instanceof BufferedImage) {
			BufferedImage actualImage = (BufferedImage) actual;
			BufferedImage expectedImage = (BufferedImage) expected;
			int[] actualPixels = ImageOps.getRGBPixels(actualImage);
			int[] expectedPixels = ImageOps.getRGBPixels(expectedImage);
			assertArrayEquals(expectedPixels, actualPixels);
		} else if (expected instanceof Float) {
			assertEquals((float) expected, (float) actual, DELTA);
		} else {
			assertEquals(expected, actual);
		}
	}

	Object returnValue;

	Object checkProgram(String input, Object expected, Object... params) throws Exception {
		returnValue = null;
		show("\n\n\n===TEST===\n");
		show(testInfo.getDisplayName());
		show("\n===INPUT===\n");
		show(input);
		show("\n\n\n===OUTPUT===\n");
		if (CHECK_TIMEOUT) {
			assertTimeoutPreemptively(Duration.ofSeconds(TIMEOUT_SECONDS), () -> {
				returnValue = exec(input, params);
			});
		} else {
			returnValue = exec(input, params);
		}

		checkEqual(expected, returnValue);

		return returnValue;
	}

	Object exec(String input, Object... params) throws Exception {
		return (new PLCLangExec(packageName, VERBOSE)).exec(input, params);
	}

	String readFile(File file) throws FileNotFoundException {
		Scanner scanner = new Scanner(file);
		scanner.useDelimiter("\\Z");
		String result;
		if (scanner.hasNext()) {
			result = scanner.next();
		} else {
			result = "";
		}
		scanner.close();
		return result;
	}

	void putInput(Object... objects) throws FileNotFoundException {
		if (CHECK_CONSOLE_IO) {
			PrintStream printStream = new PrintStream(dummyConsoleInput);
			for (Object object : objects) {
				printStream.println(object);
			}
			printStream.close();
			ConsoleIO.consoleInput = new FileInputStream(dummyConsoleInput);
		}

	}

	void checkOutput(Object... objects) throws FileNotFoundException {
		if (CHECK_CONSOLE_IO) {
			File temp = new File("TempOutput.txt");
			PrintStream printStream = new PrintStream(temp);
			for (Object object : objects) {
				printStream.println(object);
			}
			printStream.close();

			String expected = readFile(temp);
			String actual = readFile(dummyConsoleOutput);

			assertEquals(expected, actual);
		}

	}

	BufferedImage getWrittenImage(BufferedImage image) {
		// writing an image to a jpeg changes it due to image compression
		FileURLIO.writeImage((BufferedImage) image, "tempImage");
		return FileURLIO.readImage("tempImage.jpeg");
	}

	void checkFile(String path, Object... expecteds) throws IOException, ClassNotFoundException {
		if (expecteds.length == 1 && expecteds[0] instanceof BufferedImage) {
			checkEqual(expecteds[0], FileURLIO.readImage(path));
			return;
		}

		FileInputStream fis = new FileInputStream(path);
		ObjectInputStream ois = new ObjectInputStream(fis);
		for (Object expected : expecteds) {
			Object actual = ois.readObject();
			checkEqual(expected, actual);
		}

		ois.close();
	}

	void checkConsoleImageIO(BufferedImage... expectedImages) throws Exception {
		assertEquals(expectedImages.length, ConsoleIO.consoleImages.size());
		for (int i = 0; i < expectedImages.length; i++) {
			checkEqual(expectedImages[i], ConsoleIO.consoleImages.get(i));
		}
	}

	TestInfo testInfo;

	@BeforeEach
	void initTestInfo(TestInfo testInfo) {
		this.testInfo = testInfo;
	}

	@AfterEach
	void closeFiles() {
		FileURLIO.closeFiles();
		ConsoleIO.resetConsoleImages();
	}

	File dummyConsoleInput, dummyConsoleOutput;

	@BeforeEach
	void setupConsoleIO() throws FileNotFoundException {
		if (CHECK_CONSOLE_IO) {
			// clear the data
			new PrintWriter("input.txt").close();
			new PrintWriter("output.txt").close();

			// replace the IO with files
			ConsoleIO.resetScanner();
			dummyConsoleInput = new File("input.txt");
			dummyConsoleOutput = new File("output.txt");
			ConsoleIO.setConsoleInput(new FileInputStream(dummyConsoleInput));
			ConsoleIO.setConsole(new PrintStream(dummyConsoleOutput));
		}
	}

	@AfterEach
	void showIOAndClose() throws IOException {
		if (CHECK_CONSOLE_IO) {
			if (VERBOSE) {
				String input = readFile(dummyConsoleInput);
				String output = readFile(dummyConsoleOutput);

				if (!input.isEmpty()) {
					show("\n\n\n===CONSOLE INPUT===\n");
					show(input);
				}

				if (!output.isEmpty()) {
					show("\n\n\n===CONSOLE OUTPUT===\n");
					show(output);
				}
			}

			ConsoleIO.consoleInput.close();
			ConsoleIO.console.close();
		}
	}

	@Test
	void typeCheckError0() throws Exception {
		String input = """
				boolean wrongReturnType()
				string s = "hello";
				^s;
				""";
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkProgram(input, false);
		});
	}

	@Test
	void typeCheckError1() throws Exception {
		String input = """
					void redefineParamName(int i, boolean b, float f, string s, image i)
				""";
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkProgram(input, null);
		});
	}

	@Test
	void typeCheckError2() throws Exception {
		String input = """
				string imageWithoutSize()
				    int a;
				    float b;
				    image c;
				    string e;
				    boolean f;
				    color e;
				""";
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkProgram(input, null);
		});
	}

	@Test
	void typeCheckError3() throws Exception {
		String input = """
				string redeclareParam(string a, string b)
				   int a;
				   float b;
				   image c;
				   string e;
				   boolean f;

				""";
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkProgram(input, null);
		});
	}

	@Test
	void typeCheckError4() throws Exception {
		String input = """
				void undeclaredIdentm3(int a, boolean b, string s, float f, image i)
				int a0 = 0;
				int a1 <- console;
				float b0 = 10.01;
				float b1 <- console;
				boolean c0 = true;
				boolean c1 = true;
				string s0 = "hello";
				string s1 <- console;
				image[100,200] m0;
				image[100,200] m1 <- "this is a url";
				image m2 = m3;

				""";
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkProgram(input, null);
		});
	}

	@Test
	void typeCheckError5() throws Exception {
		String input = """
				int f(int x)
				int y;
				^ y;  #y not initialized

				""";
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkProgram(input, null);
		});
	}

	@Test
	void typeCheckError6() throws Exception {
		String input = """
				int badBinaryExprType()
				int x;
				string y;
				^ x+y;
				""";
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkProgram(input, null);
		});
	}

	@Test
	void typeCheckError7() throws Exception {
		String input = """
				int badUnaryExprType()
				int x;
				^ !x;
				""";
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkProgram(input, null);
		});
	}

	@Test
	void typeCheckError8() throws Exception {
		String input = """
				int badUnaryExprType()
				string x;
				^ getRed x;
				""";
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkProgram(input, null);
		});
	}

	@Test
	void typeCheckError9() throws Exception {
		String input = """
				int badParamType(boolean b)
				string x = b;
				^ x;
				""";
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkProgram(input, null);
		});
	}

	@Test
	void typeCheckError10() throws Exception {
		String input = """
				int badPixelSelector(int x)
				image[300,300] a;
				a[x,y] = <<19,20,21>>;
				^x;
				""";
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkProgram(input, null);
		});
	}

	@Test
	void typeCheckError11() throws Exception {
		String input = """
				int incompatibleAssignment()
				image[300,300] a;
				a[x,y] = "hello";
				^a;
				""";
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkProgram(input, null);
		});
	}

	@Test
	void typeCheckError12() throws Exception {
		String input = """
				int xNotVisible()
				image[300,300] a;
				a[x,y] = <<44,55,66>>;
				^x;
				""";
		Exception e = assertThrows(TypeCheckException.class, () -> {
			checkProgram(input, null);
		});
	}

	@Test
	void syntaxError0() throws Exception {
		String input = """
				int f()
				^x
				""";
		Exception e = assertThrows(SyntaxException.class, () -> {
			checkProgram(input, null);
		});
	}

	@Test
	void syntaxError1() throws Exception {
		String input = """
				int missingSemi()
				^x
				""";
		Exception e = assertThrows(SyntaxException.class, () -> {
			checkProgram(input, null);
		});
	}

	@Test
	void syntaxError2() throws Exception {
		String input = """
				int badColorOp()
				^ 3 getRed;
				""";
		Exception e = assertThrows(SyntaxException.class, () -> {
			checkProgram(input, null);
		});

	}

	@Test
	void syntaxError3() throws Exception {
		String input = """
				void badParamType(foo x)
				""";
		Exception e = assertThrows(SyntaxException.class, () -> {
			checkProgram(input, null);
		});
	}

	@Test
	void syntaxError4() throws Exception {
		String input = """
				bool badReturnType(int a)
				   ^ a % 2 == 0;
							""";
		Exception e = assertThrows(SyntaxException.class, () -> {
			checkProgram(input, null);
		});
	}

	@Test
	void syntaxError5() throws Exception {
		String input = """
				int unbalancedParens(int a)
				   ^ (2+5)/(3+4;
							""";
		Exception e = assertThrows(SyntaxException.class, () -> {
			checkProgram(input, null);
		});
	}

	@Test
	void lexicalError0() throws Exception {
		String input = """
				boolean badReturnType(int a)
				   ^ a @ 2 == 0;
							""";
		Exception e = assertThrows(LexicalException.class, () -> {
			checkProgram(input, null);
		});
	}

	@Test
	void test1() throws Exception {
		String input = "int y() ^42;";
		checkProgram(input, 42);
	}

	void test2() throws Exception {
		String input = """
				string y() ^ "hello";
				""";
		checkProgram(input, "hello");
	}

	@Test
	void test3() throws Exception {
		String input = "float f() ^ 1.2;";
		checkProgram(input, 1.2f);
	}

	@Test
	void test4() throws Exception {
		String input = "boolean f() ^ false;";
		checkProgram(input, false);
	}

	@Test
	void test5() throws Exception {
		String input = "color f() ^ <<40,50,60>>;";
		checkProgram(input, new ColorTuple(40, 50, 60));
	}

	//	/* declares and initializes variable in declaration. returns variable */
	@Test
	void test6() throws Exception {
		String input = "int f() int x = 55; ^ x;";
		checkProgram(input, 55);
	}

	@Test
	void test7() throws Exception {
		String input = "string f() string x = \"this is a string\"; ^ x;";
		checkProgram(input, "this is a string");
	}

	@Test
	void test8() throws Exception {
		String input = "boolean f() boolean x = true; ^ x;";
		checkProgram(input, true);
	}

	@Test
	void test9() throws Exception {
		String input = "color f() color x = <<100,200,255>>; ^ x;";
		checkProgram(input, new ColorTuple(100, 200, 255));
	}

	//	/* Declared variable, initializes in assignment statement, returns */
	@Test
	void test10() throws Exception {
		String input = "int f() int x; x = 33; ^ x;";
		checkProgram(input, 33);
	}

	//	/* Declared variable, initializes in assignment statement, returns */
	@Test
	void test11() throws Exception {
		String input = "float f() float x; x = 56.67; ^ x;";
		checkProgram(input, 56.67f);
	}

	//	/* Declared variable, initializes in assignment statement, returns */
	@Test
	void test12() throws Exception {
		String input = "string f() string x; x = \"abc\"; ^ x;";
		checkProgram(input, "abc");
	}

	//	/* Declared variable, initializes in assignment statement, returns */
	@Test
	void test13() throws Exception {
		String input = "color f() color x; x = <<34,56,78>>; ^ x;";
		checkProgram(input, new ColorTuple(34, 56, 78));
	}

	@Test
	void test14() throws Exception {
		String input = "boolean f() boolean x; x = false; ^ x; ";
		checkProgram(input, false);
	}

	//	/* binary expressions */
	@Test
	void test15() throws Exception {
		String input = """
				int f()
				int a = 3;
				int b = 4;
				^ a+b;
				""";
		checkProgram(input, 7);
	}

	@Test
	void test16() throws Exception {
		String input = """
				int f()
				int a = 20;
				int b = 15;
				^ a-b;
				""";
		checkProgram(input, 20 - 15);
	}

	@Test
	void test17() throws Exception {
		String input = """
				int f()
				int a = 20;
				int b = 4;
				^ a/b;
				""";
		checkProgram(input, 20 / 4);
	}

	@Test
	void test18() throws Exception {
		String input = """
				int f()
				int a = 20;
				int b = 6;
				^ a%b;
				""";
		checkProgram(input, 20 % 6);
	}

	@Test
	void test19() throws Exception {
		String input = """
				int f()
				int a = 20;
				int b = 6;
				^ a*b;
				""";
		checkProgram(input, 20 * 6);
	}

	@Test
	void test20() throws Exception {
		String input = """
				float f()
				float a = 3.2;
				float b = 4.8;
				^ a+b;
				""";
		checkProgram(input, 3.2f + 4.8f);
	}

	@Test
	void test21() throws Exception {
		String input = """
				float f()
				float a = 20.0;
				float b = 15.3;
				^ a-b;
				""";
		checkProgram(input, 20f - 15.3f);
	}

	@Test
	void test22() throws Exception {
		String input = """
				float f()
				float a = 20.0;
				float b = 7.0;
				^ a/b;
				""";
		checkProgram(input, 20f / 7f);
	}

	@Test
	void test23() throws Exception {
		String input = """
				float f()
				float a = 20.0;
				float b = 6.0;
				^ a%b;
				""";
		checkProgram(input, 20f % 6f);
	}

	@Test
	void test24() throws Exception {
		String input = """
				float f()
				float a = 20.0;
				float b = 6.0;
				^ a*b;
				""";
		checkProgram(input, 20f * 6f);
	}

	@Test
	void test25() throws Exception {
		String input = """
				color f()
				color a = <<50,60,70>>;
				color b = <<13,14,15>>;
				^ a+b;
				""";
		checkProgram(input, new ColorTuple(50 + 13, 60 + 14, 70 + 15));
	}

	@Test
	void test26() throws Exception {
		String input = """
				color f()
				color a = <<50,60,70>>;
				color b = <<13,14,15>>;
				^ a-b;
				""";
		checkProgram(input, new ColorTuple(50 - 13, 60 - 14, 70 - 15));
	}

	@Test
	void test27() throws Exception {
		String input = """
				color f()
				color a = <<50,60,70>>;
				color b = <<13,14,15>>;
				^ a*b;
				""";
		checkProgram(input, new ColorTuple(50 * 13, 60 * 14, 70 * 15));
	}

	@Test
	void test28() throws Exception {
		String input = """
				color f()
				color a = <<50,60,70>>;
				color b = <<13,14,15>>;
				^ a/b;
				""";
		checkProgram(input, new ColorTuple(50 / 13, 60 / 14, 70 / 15));
	}

	@Test
	void test29() throws Exception {
		String input = """
				color f()
				color a = <<50,60,70>>;
				color b = <<13,14,15>>;
				^ a%b;
				""";
		checkProgram(input, new ColorTuple(50 % 13, 60 % 14, 70 % 15));
	}

	@Test
	void test30() throws Exception {
		String input = """
				color f()
				color a = <<50,60,70>>;
				color b = <<13,14,15>>;
				color c = <<2,3,4>>;
				^ a+b+c;
				""";
		checkProgram(input, new ColorTuple(50 + 13 + 2, 60 + 14 + 3, 70 + 15 + 4));
	}

	@Test
	void test31() throws Exception {
		String input = """
				color f()
				color a = <<50,60,70>>;
				color b = <<13,14,15>>;
				color c = <<2,3,4>>;
				^ a+b-c;
				""";
		checkProgram(input, new ColorTuple(50 + 13 - 2, 60 + 14 - 3, 70 + 15 - 4));
	}

	@Test
	void test32() throws Exception {
		String input = """
				color f()
				color a = <<50,60,70>>;
				color b = <<13,14,15>>;
				color c = <<2,3,4>>;
				^ a+b*c;
				""";
		checkProgram(input, new ColorTuple(50 + 13 * 2, 60 + 14 * 3, 70 + 15 * 4));
	}

	@Test
	void test33() throws Exception {
		String input = """
				color f()
				color a = <<50,60,70>>;
				color b = <<13,14,15>>;
				color c = <<2,3,4>>;
				^ (a+b)*c;
				""";
		checkProgram(input, new ColorTuple((50 + 13) * 2, (60 + 14) * 3, (70 + 15) * 4));
	}

	@Test
	void test34() throws Exception {
		String input = """
				boolean f()
				^ <<50,60,70>> == <<13,14,15>>;
				""";
		checkProgram(input, false);
	}

	@Test
	void test35() throws Exception {
		String input = """
				boolean f()
				^ <<50,60,70>> == <<50,60,70>>;
				""";
		checkProgram(input, true);
	}

	@Test
	void test36() throws Exception {
		String input = """
				boolean f()
				^ <<50,60,70>> != <<50,60,70>>;
				""";
		checkProgram(input, false);
	}

	@Test
	void test37() throws Exception {
		String input = """
				color f()
				^ <<50,60,70>> *<<2,2,2>>;
				""";
		checkProgram(input, new ColorTuple(50 * 2, 60 * 2, 70 * 2));
	}

	@Test
	void test38() throws Exception {
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
		checkProgram(input, new ColorTuple(50 / 2, 60 / 2, 70 / 2));
	}


	@Test
	void testBLACK() throws Exception {
		String input = """
				color f()
				^ BLACK;
				""";
		checkProgram(input, ColorTuple.unpack(Color.BLACK.getRGB()));
	}

	@Test
	void testBLUE() throws Exception {
		String input = """
				color f()
				^ BLUE;
				""";
		checkProgram(input, ColorTuple.unpack(Color.BLUE.getRGB()));
	}

	@Test
	void testCYAN() throws Exception {
		String input = """
				color f()
				^ CYAN;
				""";
		checkProgram(input, ColorTuple.unpack(Color.CYAN.getRGB()));
	}

	@Test
	void testDARK_GRAY() throws Exception {
		String input = """
				color f()
				^ DARK_GRAY;
				""";
		checkProgram(input, ColorTuple.unpack(Color.DARK_GRAY.getRGB()));
	}

	@Test
	void testGRAY() throws Exception {
		String input = """
				color f()
				^ GRAY;
				""";
		checkProgram(input, ColorTuple.unpack(Color.GRAY.getRGB()));
	}

	@Test
	void testGREEN() throws Exception {
		String input = """
				color f()
				^ GREEN;
				""";
		checkProgram(input, ColorTuple.unpack(Color.GREEN.getRGB()));
	}

	@Test
	void testLIGHT_GRAY() throws Exception {
		String input = """
				color f()
				^ LIGHT_GRAY;
				""";
		checkProgram(input, ColorTuple.unpack(Color.LIGHT_GRAY.getRGB()));
	}

	@Test
	void testMAGENTA() throws Exception {
		String input = """
				color f()
				^ MAGENTA;
				""";
		checkProgram(input, ColorTuple.unpack(Color.MAGENTA.getRGB()));
	}

	@Test
	void testORANGE() throws Exception {
		String input = """
				color f()
				^ ORANGE;
				""";
		checkProgram(input, ColorTuple.unpack(Color.ORANGE.getRGB()));
	}

	@Test
	void testPINK() throws Exception {
		String input = """
				color f()
				^ PINK;
				""";
		checkProgram(input, ColorTuple.unpack(Color.PINK.getRGB()));
	}

	@Test
	void testRED() throws Exception {
		String input = """
				color f()
				^ RED;
				""";
		checkProgram(input, ColorTuple.unpack(Color.RED.getRGB()));
	}

	@Test
	void testWHITE() throws Exception {
		String input = """
				color f()
				^ WHITE;
				""";
		checkProgram(input, ColorTuple.unpack(Color.WHITE.getRGB()));
	}

	@Test
	void testYELLOW() throws Exception {
		String input = """
				color f()
				^ YELLOW;
				""";
		checkProgram(input, ColorTuple.unpack(Color.YELLOW.getRGB()));
	}

	@Test
	void testImage0() throws Exception {
		String input = """
				image f()
				image g <- "https://sustainable.ufl.edu/wp-content/uploads/2020/07/2020-06-22_ReOpening-7484-1024x683.jpg";
				^ g;
				""";
		BufferedImage refImage = showRef(FileURLIO.readImage(
				"https://sustainable.ufl.edu/wp-content/uploads/2020/07/2020-06-22_ReOpening-7484-1024x683.jpg"));
		checkProgram(input, refImage);
	}

	@Test
	void testImage1() throws Exception {
		String input = """
				image f()
				image g <- "testImage.jpeg";
				^ g;
				""";
		BufferedImage refImage = FileURLIO.readImage(
				"https://sustainable.ufl.edu/wp-content/uploads/2020/07/2020-06-22_ReOpening-7484-1024x683.jpg");
		FileURLIO.writeImage(refImage, "testImage");
		refImage = show(getWrittenImage(refImage));
		checkProgram(input, refImage);
	}

	@Test
	void testImage3() throws Exception {
		String input = """
				image f()
				image[300,400] g;
				g <- "https://sustainable.ufl.edu/wp-content/uploads/2020/07/2020-06-22_ReOpening-7484-1024x683.jpg";
				^ g;
				""";
		BufferedImage refImage = showRef(FileURLIO.readImage(
				"https://sustainable.ufl.edu/wp-content/uploads/2020/07/2020-06-22_ReOpening-7484-1024x683.jpg", 300,
				400));
		checkProgram(input, refImage);
	}

	@Test
	void testImageAssign() throws Exception {
		String input = """
				image f()
				image g <- "https://sustainable.ufl.edu/wp-content/uploads/2020/07/2020-06-22_ReOpening-7484-1024x683.jpg";
				write g -> console;
				image[ (getWidth g)/2, (getHeight g)/2 ] h;
				h = g;
				image k=h;
				k = g; 
				^k;
				""";
		BufferedImage refImage = showRef(FileURLIO.readImage(
				"https://sustainable.ufl.edu/wp-content/uploads/2020/07/2020-06-22_ReOpening-7484-1024x683.jpg"));
		checkProgram(input, refImage);
		checkConsoleImageIO(refImage);
		pauseImageDisplay();
	}

	@Test
	void testImage4() throws Exception {
		String input = """
				image f()
				image[400,400] g;
				g <- "testImage.jpeg";
				^ g;
				""";
		BufferedImage refImage = FileURLIO.readImage(
				"https://lv7ms1pq6dm2sea8j1mrajzw-wpengine.netdna-ssl.com/wp-content/uploads/2021/01/florida-1024x683.jpg");
		FileURLIO.writeImage(refImage, "testImage");
		refImage = show(ImageOps.resize(getWrittenImage(refImage), 400, 400));
		checkProgram(input, refImage);
	}

	@Test
	void testImage5() throws Exception {
		String input = """
				image f()
				image g <- "testImage.jpeg";
				image[getWidth g, getHeight g] h = g;
				^h;
				""";
		BufferedImage refImage = FileURLIO.readImage(
				"https://lv7ms1pq6dm2sea8j1mrajzw-wpengine.netdna-ssl.com/wp-content/uploads/2021/01/florida-1024x683.jpg");
		FileURLIO.writeImage(refImage, "testImage");
		refImage = show(getWrittenImage(refImage));
		checkProgram(input, refImage);
	}

	@Test
	void testImage6() throws Exception {
		String input = """
				image f()
				image g <- "testImage.jpeg";
				image[(getWidth g)/2, (getHeight g)/2] h = g;
				^h;
				""";
		BufferedImage refImage = FileURLIO.readImage(
				"https://lv7ms1pq6dm2sea8j1mrajzw-wpengine.netdna-ssl.com/wp-content/uploads/2021/01/florida-1024x683.jpg");
		FileURLIO.writeImage(refImage, "testImage");
		refImage = show(ImageOps.resize(getWrittenImage(refImage), refImage.getWidth() / 2, refImage.getHeight() / 2));
		checkProgram(input, refImage);
	}

	@Test
	void testImage7() throws Exception {
		String input = """
				image f()
				image g <- "testImage.jpeg";
				image h = getRed g;
				^h;
				""";
		BufferedImage refImage = FileURLIO.readImage(
				"https://lv7ms1pq6dm2sea8j1mrajzw-wpengine.netdna-ssl.com/wp-content/uploads/2021/01/florida-1024x683.jpg");
		FileURLIO.writeImage(refImage, "testImage");
		refImage = show(ImageOps.extractRed(getWrittenImage(refImage)));
		checkProgram(input, refImage);
	}

	@Test
	void testImage8() throws Exception {
		String input = """
				image f()
				image g <- "testImage.jpeg";
				image h = getGreen g;
				^h;
				""";
		BufferedImage refImage = FileURLIO.readImage(
				"https://lv7ms1pq6dm2sea8j1mrajzw-wpengine.netdna-ssl.com/wp-content/uploads/2021/01/florida-1024x683.jpg");
		FileURLIO.writeImage(refImage, "testImage");
		refImage = show(ImageOps.extractGreen(getWrittenImage(refImage)));
		checkProgram(input, refImage);
	}

	@Test
	void testImage9() throws Exception {
		String input = """
				image f()
				image g <- "testImage.jpeg";
				image h = getBlue g;
				^h;
				""";
		BufferedImage refImage = FileURLIO.readImage(
				"https://lv7ms1pq6dm2sea8j1mrajzw-wpengine.netdna-ssl.com/wp-content/uploads/2021/01/florida-1024x683.jpg");
		FileURLIO.writeImage(refImage, "testImage");
		refImage = show(ImageOps.extractBlue(getWrittenImage(refImage)));
		checkProgram(input, refImage);
	}

	@Test
	void testParams0() throws Exception {
		String input = """
				int a(int b)
				^b;
				""";
		checkProgram(input, 42, 42);
	}

	@Test
	void testParams1() throws Exception {
		String input = """
				int a(int b, int c)
				^ b+c;
				""";
		int b = 42;
		int c = 64;
		checkProgram(input, b + c, b, c);
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
		checkProgram(input, b / c, params);
	}

	@Test
	void testParams3() throws Exception {
		String input = """
				image a(string url)
				image b <- url;
				^b;
				""";
		String s = "https://www.instateangels.com/wp-content/uploads/2015/02/university-of-florida-gainesville-uf-campus-quad.jpg";
		Object[] params = { s };
		BufferedImage refImage = FileURLIO.readImage(
				"https://www.instateangels.com/wp-content/uploads/2015/02/university-of-florida-gainesville-uf-campus-quad.jpg");
		checkProgram(input, refImage, params);
	}

	@Test
	void testParams4() throws Exception {
		String input = """
				image a(string url, int width, int height)
				image[width,height] b <- url;
				^b;
				""";
		String s = "https://www.instateangels.com/wp-content/uploads/2015/02/university-of-florida-gainesville-uf-campus-quad.jpg";
		Object[] params = { s, 1000, 2000 };
		BufferedImage refImage = FileURLIO.readImage(
				"https://www.instateangels.com/wp-content/uploads/2015/02/university-of-florida-gainesville-uf-campus-quad.jpg");
		refImage = ImageOps.resize(refImage, (int) params[1], (int) params[2]);
		checkProgram(input, refImage, params);
	}

	@Test
	void testImageOp0() throws Exception {
		String input = """
				image f(int widthAndHeight)
				image[widthAndHeight,widthAndHeight] a;
				a[x,y] = <<x-y, 0, x-y>>;
				^ a;
				""";
		int widthAndHeight = 500;
		BufferedImage refImage = new BufferedImage(widthAndHeight, widthAndHeight, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < refImage.getWidth(); x++) {
			for (int y = 0; y < refImage.getHeight(); y++) {
				ImageOps.setColor(refImage, x, y, new ColorTuple((x - y), 0, (x - y)));
			}
		}
		show(refImage);
		checkProgram(input, refImage, widthAndHeight);
	}

	@Test
	void testImageOp0a() throws Exception { // todo consider removing as this is almost the same as above
		String input = """
				image f(int widthAndHeight)
				image[widthAndHeight,widthAndHeight] a;
				a[x,y] = <<x-y, 0, y-x>>;
				^ a;
				""";
		int widthAndHeight = 500;
		BufferedImage refImage = new BufferedImage(widthAndHeight, widthAndHeight, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < refImage.getWidth(); x++) {
			for (int y = 0; y < refImage.getHeight(); y++) {
				ImageOps.setColor(refImage, x, y, new ColorTuple((x - y), 0, (y - x)));
			}
		}
		show(refImage);
		checkProgram(input, refImage, widthAndHeight);
	}

	@Test
	void testImageOp2() throws Exception {
		String input = """
				image f(int widthAndHeight)
				image[widthAndHeight,widthAndHeight] a;
				a[x,y] = <<y, y, 0>>;
				^ a;
				""";
		int widthAndHeight = 500;
		BufferedImage refImage = new BufferedImage(widthAndHeight, widthAndHeight, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < refImage.getWidth(); x++) {
			for (int y = 0; y < refImage.getHeight(); y++) {
				ImageOps.setColor(refImage, x, y, new ColorTuple(y, y, 0));
			}
		}
		show(refImage);
		checkProgram(input, refImage, widthAndHeight);
	}

	@Test
	void testImageOp3() throws Exception {
		String input = """
				image f(int widthAndHeight)
				image[widthAndHeight,widthAndHeight] a;
				image[widthAndHeight,widthAndHeight] b;
				a[x,y] = <<y, y, 0>>;
				b[x,y] = << getRed a[y,x], getGreen a[y,x], getBlue a[y,x] >>;
				^ b;
				""";
		int widthAndHeight = 500;
		BufferedImage refImage = new BufferedImage(widthAndHeight, widthAndHeight, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < refImage.getWidth(); x++) {
			for (int y = 0; y < refImage.getHeight(); y++) {
				ImageOps.setColor(refImage, x, y, new ColorTuple(x, x, 0));
			}
		}
		show(refImage);
		checkProgram(input, refImage, widthAndHeight);
	}

	@Test
	void testImageOp4() throws Exception {
		String input = """
				image f(int widthAndHeight)
				image[widthAndHeight,widthAndHeight] a;
				image[widthAndHeight,widthAndHeight] b;
				a[x,y] = <<y, y, 0>>;
				b[x,y] = << getRed a[y,x], 0, 0 >>;
				^ b;
				""";
		int widthAndHeight = 500;
		BufferedImage refImage = new BufferedImage(widthAndHeight, widthAndHeight, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < refImage.getWidth(); x++) {
			for (int y = 0; y < refImage.getHeight(); y++) {
				ImageOps.setColor(refImage, x, y, new ColorTuple(x, 0, 0));
			}
		}
		show(refImage);
		checkProgram(input, refImage, widthAndHeight);
	}

	@Test
	void testImageOp5() throws Exception {
		String input = """
				image f(int widthAndHeight, string url)
				image a <- url;
				int w = getWidth a;
				int h = getHeight a;
				int size = if (w > h) h else w fi;
				image[size,size] b;
				b[x,y] = << getRed a[y,x], 0, 0 >>;
				^ b;
				""";
		int widthAndHeight = 500;
		String url = "https://questions.ufl.edu/wp-content/uploads/2012/11/halls1.jpg";
		BufferedImage a = FileURLIO.readImage(url);
		show(a);
		int size = a.getWidth() > a.getHeight() ? a.getHeight() : a.getWidth();
		BufferedImage refImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < refImage.getWidth(); x++) {
			for (int y = 0; y < refImage.getHeight(); y++) {
				ImageOps.setColor(refImage, x, y,
						new ColorTuple(ColorTuple.getRed((ColorTuple.unpack(a.getRGB(y, x)))), 0, 0));
			}
		}
		show(checkProgram(input, refImage, widthAndHeight, url));
	}

	@Test
	void testImageOp6() throws Exception {
		String input = """
				image flag(int size)
				  # image[size,size] a;
				  # image[size,size] b;
				   image[size,size] c;
				   int stripSize = size/2;
				  # a[x,y] = YELLOW;
				  # b[x,y] = BLUE;
				   c[x,y] = if (y > stripSize) YELLOW else BLUE fi;
				   ^c;
				   """;
		int size = 1024;
		BufferedImage c = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		int stripSize = (size / 2);
		for (int x = 0; x < c.getWidth(); x++)
			for (int y = 0; y < c.getHeight(); y++)
				ImageOps.setColor(c, x, y, ((y > stripSize)) ? (ColorTuple.unpack(Color.YELLOW.getRGB()))
						: (ColorTuple.unpack(Color.BLUE.getRGB())));
		checkProgram(input, c, size);
	}

	@Test
	void testImageOp8() throws Exception {
		String input = """
				image addition(int size)
				   image[size,size] f;
				   f[x,y] = x+y;
				   ^f;
				   """;
		int size = 1024;
		BufferedImage f = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < f.getWidth(); x++)
			for (int y = 0; y < f.getHeight(); y++)
				ImageOps.setColor(f, x, y, new ColorTuple(x+y));
		checkProgram(input, f, size);
	}

	@Test
	void testImageOp9() throws Exception {
		String input = """
				image addition(int size)
				   image[size,size] f;
				   f[x,y] = <<x,x,x>>%<<256,256,256>> +  <<y,y,y>>%<<256,256,256>>;
				   ^f;
				   """;
		int size = 1024;
		BufferedImage f = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < f.getWidth(); x++)
			for (int y = 0; y < f.getHeight(); y++)
				ImageOps.setColor(f, x, y, ImageOps.binaryTupleOp(OP.PLUS,
						ImageOps.binaryTupleOp(OP.MOD, new ColorTuple(x, x, x), new ColorTuple(256, 256, 256)),
						ImageOps.binaryTupleOp(OP.MOD, new ColorTuple(y, y, y), new ColorTuple(256, 256, 256))));
		checkProgram(input, f, size);
	}

	@Test
	void testImagetoFile() throws Exception {
		String input = """
				void testImagetoFile(int size)
				   image[size,size] f;
				   f[x,y] = <<x,x,x>>%<<256,256,256>> +  <<y,y,y>>%<<256,256,256>>;
				   write f -> console;
				   write f -> "testImageToFile";  #CORRECTED:  testImagetoFile -> testImageToFile
				   boolean done <- console;
				   """;
		int size = 1024;
		BufferedImage f = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < f.getWidth(); x++)
			for (int y = 0; y < f.getHeight(); y++)
				ImageOps.setColor(f, x, y, ImageOps.binaryTupleOp(OP.PLUS,
						ImageOps.binaryTupleOp(OP.MOD, new ColorTuple(x, x, x), new ColorTuple(256, 256, 256)),
						ImageOps.binaryTupleOp(OP.MOD, new ColorTuple(y, y, y), new ColorTuple(256, 256, 256))));
		BufferedImage refImage = showRef(getWrittenImage(f));
		putInput(true);
		checkProgram(input, null, size);
		checkFile("testImageToFile.jpeg", refImage);
		checkConsoleImageIO(f);
	}

	@Test
	void testImagetoFile1() throws Exception {
		String input = """
				void testImagetoFile1(string url)
				int size = 512;
				   image f0 <- url;
				   write f0 -> console;
				   write f0 -> "testImagetoFile1a";
				   image[size,size] f;
				   f[x,y] = <<x,x,x>>%<<256,256,256>> +  <<y,y,y>>%<<256,256,256>>;
				   write f -> console;
				   write f -> "testImagetoFile1b";
				   """;
		String url = "https://questions.ufl.edu/wp-content/uploads/2012/11/halls1.jpg";
		BufferedImage f0 = FileURLIO.readImage(url);
		BufferedImage f = new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < f.getWidth(); x++)
			for (int y = 0; y < f.getHeight(); y++)
				ImageOps.setColor(f, x, y, ImageOps.binaryTupleOp(OP.PLUS,
						ImageOps.binaryTupleOp(OP.MOD, new ColorTuple(x, x, x), new ColorTuple(256, 256, 256)),
						ImageOps.binaryTupleOp(OP.MOD, new ColorTuple(y, y, y), new ColorTuple(256, 256, 256))));
		checkProgram(input, null, url);
		checkFile("testImagetoFile1a.jpeg", showRef(getWrittenImage(f0)));
		checkFile("testImagetoFile1b.jpeg", showRef(getWrittenImage(f)));
		checkConsoleImageIO(f0, f);
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
		checkProgram(input, a + b * 2 + c, params);
	}

	@Test
	void testImageArithmetic0() throws Exception {
		String input = """
				image f(string url)
				image a <- url;
				write a -> console;
				image b = a/3;
				^b;
				""";
		String url = "https://my.ufl.edu/ps/media/wwwufledu/images/nav/campuslife.jpg";
		BufferedImage a = FileURLIO.readImage(url);
		BufferedImage b = ImageOps.binaryImageScalarOp(OP.DIV, a, 3);
		showRef(b);
		checkProgram(input, b, url);
		checkConsoleImageIO(a);
	}

	@Test
	void testImageArithmetic1() throws Exception {
		String input = """
				image f(string url)
				image a <- url;
				int w = getWidth a;
				int h = getHeight a;
				int strip = w/4;
				image[w,h] b;
				b[x,y] = if ( x%strip < strip/2) a[x,y] else RED fi;
				^b;
				""";
		String url = "https://my.ufl.edu/ps/media/wwwufledu/images/nav/campuslife.jpg";
		BufferedImage a = FileURLIO.readImage(url, null, null);
		int w = (a).getWidth();
		int h = (a).getHeight();
		int strip = (w / 4);
		BufferedImage b = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < b.getWidth(); x++)
			for (int y = 0; y < b.getHeight(); y++)
				ImageOps.setColor(b, x, y, (((x % strip) < (strip / 2))) ? ((ColorTuple.unpack(a.getRGB(x, y))))
						: (ColorTuple.unpack(Color.RED.getRGB())));
		showRef(b);
		show(checkProgram(input, b, url));
		pauseImageDisplay();
	}

	@Test
	void testImageArithmetic2() throws Exception {
		String input = """
				image f(string url)
				image a <- url;
				int w = getWidth a;
				int h = getHeight a;
				int strip = w/4;
				image[w,h] b;
				b[x,y] = if ( x%strip < strip/2) <<getRed a[x,y],0,0>> else <<0,0,getBlue a[x,y]>> fi;
				^b;
				""";
		String url = "https://my.ufl.edu/ps/media/wwwufledu/images/nav/campuslife.jpg";
		BufferedImage a = FileURLIO.readImage(url, null, null);
		int w = (a).getWidth();
		int h = (a).getHeight();
		int strip = (w / 4);
		BufferedImage b = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < b.getWidth(); x++)
			for (int y = 0; y < b.getHeight(); y++)
				ImageOps.setColor(b, x, y,
						(((x % strip) < (strip / 2)))
								? (new ColorTuple(ColorTuple.getRed((ColorTuple.unpack(a.getRGB(x, y)))), 0, 0))
								: (new ColorTuple(0, 0, ColorTuple.getBlue((ColorTuple.unpack(a.getRGB(x, y)))))));
		showRef(b);
		checkProgram(input, b, url);
	}

	@Test
	void testImageArithmetic3() throws Exception {
		String input = """
				image BDP0()
				int Z = 255;
				image[1024,1024] a;
				a[x,y] = <<(x/8*y/8)%(Z+1),0,0>>;
				^ a;
				""";
		BufferedImage a = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < a.getWidth(); x++)
			for (int y = 0; y < a.getHeight(); y++)
				ImageOps.setColor(a, x, y, new ColorTuple(((((x / 8) * y) / 8) % (255 + 1)), 0, 0));
		showRef(a);
		checkProgram(input, a);
	}

	@Test
	void testImageArithmetic4() throws Exception {
		String input = """
				image BDP0()
				int Z = 255;
				image[1024,1024] a;
				a[x,y] = <<(Z*x*y)/((1024-1)*(1024-1)), (Z*x*y)/((1024-1)*(1024-1)),(Z*x*y)/((1024-1)*(1024-1))>>;
				^ a;
				""";
		BufferedImage a = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < a.getWidth(); x++)
			for (int y = 0; y < a.getHeight(); y++)
				ImageOps.setColor(a, x, y, new ColorTuple((((255 * x) * y) / ((1024 - 1) * (1024 - 1))),
						(((255 * x) * y) / ((1024 - 1) * (1024 - 1))), (((255 * x) * y) / ((1024 - 1) * (1024 - 1)))));
		showRef(a);
		checkProgram(input, a);
	}

	@Test
	void testImageArithmetic5() throws Exception {
		String input = """
				image BDP0()
				int Z = 255;
				image[1024,1024] a;
				int s = 16;
				a[x,y] = <<(x/s)%((y/s)+1), (x/s)%((y/s)+1), (x/s)%((y/s)+1)>>*5;
				^ a;
				""";
		int Z = 255;
		BufferedImage a = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_RGB);
		int s = 16;
		for (int x = 0; x < a.getWidth(); x++)
			for (int y = 0; y < a.getHeight(); y++)
				ImageOps.setColor(a, x, y, ImageOps.binaryTupleOp(OP.TIMES,
						new ColorTuple(((x / s) % ((y / s) + 1)), ((x / s) % ((y / s) + 1)), ((x / s) % ((y / s) + 1))),
						(new ColorTuple(5))));
		showRef(a);
		checkProgram(input, a);
	}

	@Test
	void testWrite0() throws Exception {
		String input = """
				void BDP0()
				int Z = 255;
				image[1024,1024] a;
				int s = 16;
				a[x,y] = <<(x/s)%((y/s)+1), (x/s)%((y/s)+1), (x/s)%((y/s)+1)>>*5;
				write a -> console;
				""";
		int Z = 255;
		BufferedImage a = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_RGB);
		int s = 16;
		for (int x = 0; x < a.getWidth(); x++)
			for (int y = 0; y < a.getHeight(); y++)
				ImageOps.setColor(a, x, y, ImageOps.binaryTupleOp(OP.TIMES,
						new ColorTuple(((x / s) % ((y / s) + 1)), ((x / s) % ((y / s) + 1)), ((x / s) % ((y / s) + 1))),
						new ColorTuple(5)));
		checkProgram(input, null);
		checkConsoleImageIO(a);
	}

	@Test
	void testWrite1() throws Exception {
		String input = """
				void BDP0()
				write 42->console;
				write "hello" -> console;
				write 43.6->console;
				write <<1,2,3>> -> console;
				write RED -> console;
				""";
		checkProgram(input, null);
		checkOutput(42, "hello", 43.6, new ColorTuple(1, 2, 3), ColorTuple.unpack(Color.RED.getRGB()));
	}

	@Test
	void testReadInt0() throws Exception {
		String input = """
				int testRead0()
				int x;
				write  "enter 99 to pass test:" -> console;
				x <- console;
				^ x;
				         """;
		putInput(99);
		checkProgram(input, 99);
		checkOutput("enter 99 to pass test:");
	}

	@Test
	void testReadString0() throws Exception {
		String input = """
				string testReadString0()
				string x;
				write "enter string \\"hello\\" (no quotes):" -> console;
				x <- console;
				^ x;
				         """;
		putInput("hello");
		checkProgram(input, "hello");
		checkOutput("enter string \"hello\" (no quotes):");
	}

	@Test
	void testReadInt1() throws Exception {
		String input = """
				int testReadInt1()
				write  "enter 88 to pass test:" -> console;
				int x <- console;
				^ x;
				         """;
		putInput(88);
		checkProgram(input, 88);
		checkOutput("enter 88 to pass test:");
	}

	@Test
	void testReadWriteInt1() throws Exception {
		String input = """
				int testReadWriteInt1()
				int x = 3;
				write x -> "testReadWriteInt1";
				int y <- "testReadWriteInt1";
				^ y;
				         """;
		checkProgram(input, 3);
		checkFile("testReadWriteInt1", 3);
	}

	@Test
	void testReadWriteInt2() throws Exception {
		String input = """
				int testReadWriteInt2()
				int x = 55;
				write x -> "testReadWriteInt2";
				int y;
				y <- "testReadWriteInt2";
				^ y;
				         """;
		checkProgram(input, 55);
		checkFile("testReadWriteInt2", 55);
	}

	@Test
	void testReadBoolean0() throws Exception {
		String input = """
				boolean testReadBoolean0()
				boolean x;
				write "enter true to pass test\n" -> console;
				x <- console;
				^ x;
				         """;
		putInput(true);
		checkProgram(input, true);
		checkOutput("enter true to pass test\n");
	}

	@Test
	void testReadBoolean1() throws Exception {
		String input = """
				boolean testReadBoolean1()
				write "enter false to pass test\n" -> console;
				boolean x <- console;
				^ x;
				         """;
		putInput(false);
		checkProgram(input, false);
		checkOutput("enter false to pass test\n");
	}

	@Test
	void testWriteReadBoolean0() throws Exception {
		String input = """
				boolean testWriteReadBoolean0()
				boolean x = false;
				write x -> "testWriteReadBoolean0";
				boolean y <- "testWriteReadBoolean0";
				^ y;
				         """;
		checkProgram(input, false);
		checkFile("testWriteReadBoolean0", false);
	}

	@Test
	void testWriteReadBoolean1() throws Exception {
		String input = """
				boolean testWriteReadBoolean1()
				boolean x = true;
				write x -> "testWriteReadBoolean1";
				boolean y;
				y <- "testWriteReadBoolean1";
				^ y;
				         """;
		checkProgram(input, true);
		checkFile("testWriteReadBoolean1", true);
	}

	@Test
	void testRead2() throws Exception {
		String input = """
				string testRead2()
				string x;
				write "enter \\\"ciao\\\" to pass test\n" -> console;
				x <- console;
				^ x;
				         """;
		putInput("ciao");
		checkProgram(input, "ciao");
		checkOutput("enter \"ciao\" to pass test\n");
	}

	@Test
	void testRead3() throws Exception {
		String input = """
				color testRead3()
				color x;
				write "enter 3 4 5 to pass test" -> console;
				x <- console;
				^ x;
				         """;
		ColorTuple result = new ColorTuple(3, 4, 5);
		putInput(3, 4, 5);
		checkProgram(input, result);
		checkOutput("enter 3 4 5 to pass test");
	}

	@Test
	void testWriteReadString0() throws Exception {
		String input = """
				string testWriteReadString0()
				string x = "hello";
				write x -> "testWriteReadString0";
				string s <- "testWriteReadString0";
				^ s;
				         """;
		checkProgram(input, "hello");
		checkFile("testWriteReadString0", "hello");
	}

	@Test
	void testWriteReadColor0() throws Exception {
		String input = """
				color testWriteReadColor0()
				color x = <<128,129,130>>;
				write x -> "testWriteReadColor0";
				color s <- "testWriteReadColor0";
				^ s;
				         """;
		ColorTuple result = new ColorTuple(128, 129, 130);
		checkProgram(input, result);
		checkFile("testWriteReadColor0", result);
	}

	@Test
	void testWriteReadColor1() throws Exception {
		String input = """
				color testWriteReadColor1()
				color x = <<128,129,130>>;
				write x -> "testWriteReadColor1";
				color s;
				s <- "testWriteReadColor1";
				^ s;
				         """;
		ColorTuple result = new ColorTuple(128, 129, 130);
		checkProgram(input, result);
		checkFile("testWriteReadColor1", result);  //CORRECTED testWriteReadColor0 -> testWriteReadColor1
	}

	@Test
	void testWriteReadFloat0() throws Exception {
		String input = """
				float testWriteReadFloat0()
				float x = 34.56;
				write x -> "testWriteReadFloat0";
				float s <- "testWriteReadFloat0";
				^ s;
				         """;
		checkProgram(input, 34.56f);
		checkFile("testWriteReadFloat0", 34.56f);
	}

	@Test
	void testWriteReadFloat1() throws Exception {
		String input = """
				float testWriteReadFloat1()
				float x = 34.5678;
				write x -> "testWriteReadFloat1";
				float s;
				s <- "testWriteReadFloat1";
				^ s;
				         """;
		checkProgram(input, 34.5678f);
		checkFile("testWriteReadFloat1", 34.5678f);
	}

	@Test
	void testPackUnpack0() throws Exception {
		String input = """
				color testPackUnpack()
				color x = << 500, 125, -3 >>;
				int p = x;
				color z = <<getRed p, getGreen p, getBlue p>>;
				^ z;
				         """;
		checkProgram(input, new ColorTuple(255, 125, 0));
	}

	@Test
	void testPackUnpack1() throws Exception {
		String input = """
				color testPackUnpack()
				color x = << 500, 125, -3 >>;
				int p = x;
				color z = p;
				^ z;
				         """;
		checkProgram(input, new ColorTuple((new ColorTuple(500,125,-3).pack())));
	}


	@Test
	void testWriteIntToFile() throws Exception {
		String input = """
				int testWriteIntToFile(string file)
				int x = 42;
				write x -> file;
				^x;
				         """;
		Object[] params = { "testWriteIntToFile" };
		checkProgram(input, 42, params);
		checkFile("testWriteIntToFile", 42);
	}

	@Test
	void testWriteBooleanToFile() throws Exception {
		String input = """
				boolean testWriteBooleanToFile(string file)
				boolean x = false;
				write x -> file;
				^x;
				         """;
		Object[] params = { "testWriteBooleanToFile" };
		checkProgram(input, false, params);
		checkFile("testWriteBooleanToFile", false);
	}

	@Test
	void testWriteFloatToFile() throws Exception {
		String input = """
				float testWriteFloatToFile(string file)
				float x = 23.45;
				write x -> file;
				float y;
				y <- file;
				^y;
				         """;
		Object[] params = { "testWriteFloatToFile" };
		checkProgram(input, 23.45f, params);
		checkFile("testWriteFloatToFile", 23.45f);
	}

	@Test
	void testWriteColorToFile() throws Exception {
		String input = """
				color testWriteColorToFile(string file)
				color x = <<3,4,5>>;
				write x -> file;
				^x;
				         """;
		Object[] params = { "testWriteColorToFile" };
		checkProgram(input, new ColorTuple(3, 4, 5), params);
		checkFile("testWriteColorToFile", new ColorTuple(3, 4, 5));
	}

	@Test
	void testMultipleWrites() throws Exception {
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
				write "x4="-> console;
				write x4 -> console;
				^x3;
				""";
		checkProgram(input, 34);
		checkOutput("x3=", 34, "x4=", 56);
		checkFile("testMultipleWrites", 34, 56);
	}

	@Test
	void testImageArithemtic0() throws Exception {
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
		int w = 500;
		int h = 500;
		int size = w * h;
		BufferedImage refImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		int teal = Color.GREEN.getRGB() | Color.BLUE.getRGB(); // bitwise or, teal = ff00ffff
		System.out.println("teal=" + Integer.toHexString(teal));
		int[] rgbArray = new int[size];
		Arrays.fill(rgbArray, teal);
		refImage.setRGB(0, 0, w, h, rgbArray, 0, w);
		showRef(refImage);
		checkProgram(input, refImage);
	}

	@Test
	void testScannerInput0() throws Exception {
		String input = """
				int readAndSum()
				int total = 0;
				int nextVal;
				nextVal <- console;
				total = total + nextVal;
				nextVal <- console;
				total = total + nextVal;
				nextVal <- console;
				total = total + nextVal;
				^total;
				""";
		putInput(1, 2, 3);
		checkProgram(input, 6);
	}

	@Test
	void testScannerInput3() throws Exception {
		String input = """
				string readAndSum()
				float nextVal;
				float  total  <- console;
				nextVal <- console;
				total = total + nextVal;
				nextVal <- console;
				total = total + nextVal;
				write "total" -> console;
				write total -> console;
				string s <- console;
				^s	;
				""";
		putInput(3.14f, 2.71f, -1f, "goodbye");
		checkProgram(input, "goodbye");
		checkOutput("total", 3.14f + 2.71f - 1f);
	}
}
