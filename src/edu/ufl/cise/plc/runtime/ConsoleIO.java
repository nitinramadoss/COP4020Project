package edu.ufl.cise.plc.runtime;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.InputMismatchException;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class ConsoleIO {

	/**
	 * Destination of "console" output. Can be changed to redirect output. Generated
	 * code should use ConsoleIO.console.println(...) etc. instead of System.out.println
	 */
	public static PrintStream console = System.out;
	
	public void setConsole(PrintStream out) {
		console = out;
	}

	/** Source of "console" input. */
	static InputStream consoleInput = System.in;
	
	public void setConsoleInput(InputStream in) {
		consoleInput = in;
	}

	/*
	 * java.util.Scanner for input from "console" Implementation is a singleton.
	 */
	private static Scanner scanner;

	private static Scanner getScanner() {
		if (scanner == null) {
			scanner = new Scanner(consoleInput);
		}
		return scanner;
	}

	/**
	 * Reads a value of the given type from the console. The type must be one of
	 * "INT", "FLOAT", "STRING", or "COLOR". If the scanner cannot convert the input
	 * token to the expected type, it will print "INVALID INPUT" and read another
	 * token.
	 * 
	 * See readImage to read an image.
	 * 
	 * @param type   type of value to read
	 * @param prompt prompt to user for value
	 * @return
	 */
	public static Object readValueFromConsole(String type, String prompt) {
		console.print(prompt);
		Scanner scanner = getScanner();
		try {
			return switch (type) {
			case "INT" -> scanner.nextInt();
			case "FLOAT" -> scanner.nextFloat();
			case "STRING" -> scanner.nextLine();
			case "COLOR" -> {
				int r = scanner.nextInt();
				int g = scanner.nextInt();
				int b = scanner.nextInt();
				yield new ColorTuple(r, g, b);
			}
			case "BOOLEAN" -> scanner.nextBoolean();
			default -> throw new IllegalArgumentException("Compiler bug Unexpected value: " + type);
			};
		} catch (InputMismatchException e) {
			console.print("INVALID INPUT ");
			getScanner().next(); // throw away invalid input token
			return readValueFromConsole(type, prompt);
		}
	}

	/**
	 * Displays the given image on the screen.
	 * 
	 * @param image
	 */
	public static void displayImageOnScreen(BufferedImage image) {
		System.err.println("in displayImageOnScreen: image = " + image);
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setSize(image.getWidth(), image.getHeight());
		JLabel label = new JLabel(new ImageIcon(image));
		frame.add(label);
		frame.pack();
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					frame.setVisible(true);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}

	}
	

	

}
