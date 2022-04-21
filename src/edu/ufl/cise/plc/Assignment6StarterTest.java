package edu.ufl.cise.plc;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import edu.ufl.cise.plc.TypeCheckException;
import edu.ufl.cise.plc.ast.ASTNode;
import edu.ufl.cise.plc.ast.Program;
import edu.ufl.cise.plc.runtime.ColorTuple;
import edu.ufl.cise.plc.runtime.ConsoleIO;
import edu.ufl.cise.plc.runtime.FileURLIO;
import edu.ufl.cise.plc.runtime.ImageOps;
import edu.ufl.cise.plc.runtime.javaCompilerClassLoader.PLCLangExec;

class Assignment6StarterTest {

	// Default package name for generated code
	String packageName = "cop4020sp22Package";
//	String packageName = "";

	boolean VERBOSE = true;

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
		if (VERBOSE) {
			ConsoleIO.displayImageOnScreen(image);
			try {
				pauseImageDisplay();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return image;
	}

	/**
	 * This is the same as show(BufferedImage) except that the image is placed in a different location: 
	 * [0,0] is in center of screen rather than the upper left corner.
	 */

	private BufferedImage showRef(BufferedImage image) {
		if (VERBOSE) {
			ConsoleIO.displayReferenceImageOnScreen(image);
			try {
				pauseImageDisplay();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return image;
	}

	boolean PAUSE = true;
	int MILLIS = 2000;

	private void pauseImageDisplay() throws IOException {
		if (PAUSE) {
			/*
			 * If you would like Junit test to pause for input, uncomment The next two lines
			 * and comment the try-catch block.
			 */
//			System.out.print("Enter any character to terminate: ");
//			System.in.read();
			try {
				Thread.sleep(MILLIS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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

	Object check(String input, Object expectedResult) throws Exception {
		return check(input, null, expectedResult);
	}

	Object check(String input, Object[] params, Object expected) throws Exception {
		Object actual = exec(input, params);
		if (expected instanceof BufferedImage) {
			BufferedImage actualImage = (BufferedImage) actual;
			BufferedImage expectedImage = (BufferedImage) expected;
			int[] actualPixels = ImageOps.getRGBPixels(actualImage);
			int[] expectedPixels = ImageOps.getRGBPixels(expectedImage);
			assertArrayEquals(expectedPixels, actualPixels);
		} else {
			assertEquals(expected, actual);
		}
		return actual;
	}

	Object exec(String input, Object[] params) throws Exception {
		return (new PLCLangExec(packageName, VERBOSE)).exec(input, params);
	}

	Object exec(String input) throws Exception {
		return (new PLCLangExec(packageName, VERBOSE)).exec(input, null);
	}

	// need separate method to check floats due to DELTA. Note in simple cases, just
	// use normal check.
	void checkFloatResult(String input, float expectedResult) throws Exception {
		checkFloatResult(input, null, expectedResult);
	}

	void checkFloatResult(String input, Object[] params, float expectedResult) throws Exception {
		Object result = exec(input, params);
		show("result = " + result);
		assertEquals(expectedResult, (float) result, DELTA);
	}

	@AfterEach
	public void closeFiles() {
		FileURLIO.closeFiles();
	}

	@Test
	void lectureExample3() throws Exception {
		String input = """
				image a(string url, int width, int height)
				      image[width,height] b <- url;
				      ^b;
				      """;
		String url = "https://www.ufl.edu/media/wwwufledu/images/about/aerial_tigert_stadium.jpg";
		Object[] params = { url, 300, 200 };
		BufferedImage refImage = showRef(FileURLIO.readImage(url, 300, 200));
		show(check(input, params, refImage));
	}

	@Test
	void lectureExample4() throws Exception {
		String input = """
				image f(int widthAndHeight) image[widthAndHeight,widthAndHeight] a;
				            a[x,y] = <<x-y, 0, y-x>>;
				            ^ a;
				""";
		int size = 500;
		// create reference image
		BufferedImage refImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				refImage.setRGB(x, y, (new ColorTuple(x - y, 0, y - x)).pack());
			}
		}
		// run PLCLang program
		Object[] params = { size };
		show(check(input, params, refImage));
	    //If you don't want to compare with ref image use show(exec(input,params));
	}
	
	
	@Test
	void lectureExample5() throws Exception {
		String input = """
				image flag(int size)
				   image[size,size] f;
				   int stripSize = size/2;
				   f[x,y] = if (y > stripSize) YELLOW else BLUE fi;
				   ^f;
				   """;
		int size = 1024;		
		BufferedImage refImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				refImage.setRGB(x, y, (y>size/2)? Color.YELLOW.getRGB() : Color.BLUE.getRGB());
			}
		}
		Object[] params = { size };
		show(check(input, params, refImage));
	}
	
	@Test
	void lectureExample6() throws Exception {
		String input = """
				image f(string url)
                image a <- url;
                write a -> console;
                image b = a/3;
                ^b;

				""";
		String url = "https://www.ufl.edu/media/wwwufledu/images/nav/academics.jpg";
		BufferedImage inputImage = FileURLIO.readImage(url);
		int w = inputImage.getWidth();
		int h = inputImage.getHeight();
		BufferedImage refImage = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < w; x++)
			for (int y = 0; y < h; y++) {
				ColorTuple pixel = ColorTuple.unpack(inputImage.getRGB(x, y));
				int newPackedPixel = (new ColorTuple(pixel.red/3,pixel.green/3,pixel.blue/3)).pack();
				refImage.setRGB(x, y, newPackedPixel);
			}
		Object[] params = {url};
		ConsoleIO.displayReferenceImageOnScreen(inputImage);
		//this image should be the same size, but darker than inputImage
		show(check(input, params, refImage));
	}
	
	


	@Test
	void colorArithmetic0() throws Exception {
		String input = """
				color f()
				color a = <<50,60,70>>;
				color b = <<13,14,15>>;
				^ a+b;
				""";
		check(input, new edu.ufl.cise.plc.runtime.ColorTuple(50 + 13, 60 + 14, 70 + 15));
	}

	@Test
	void imageArithemtic0() throws Exception {
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
		int size = w*h;
		BufferedImage refImage = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
		int teal = Color.GREEN.getRGB() | Color.BLUE.getRGB(); //bitwise or, teal = ff00ffff
		System.out.println("teal=" + Integer.toHexString(teal));
		int[] rgbArray = new int[size];
		Arrays.fill(rgbArray, teal);
		refImage.setRGB(0, 0, w,h, rgbArray, 0, w);
		show(check(input, refImage));
	}



}
