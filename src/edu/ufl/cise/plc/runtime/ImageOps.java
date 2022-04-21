package edu.ufl.cise.plc.runtime;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * An image is represented by a 2D array of pixels. An image is implemented 
 * as an instance of java.awt.image.BufferedImage using the default color model.  
 *
 * The BufferedImage class offers a way to get and set the pixel value,  
 * getRGB(int x, int y), which returns an int, and setRGB(int x, int y, int rgb);  
 * In these methods, pixels are represented as an int that encodes the 3 colors 
 * plus the alpha (transparency) value.  
 * 
 * In PLCLang, we do not deal with the alpha value--whenever required, it is set to 0xff.
 * 
 * In our language, the type color is represented by an instance of the ColorTuple class, 
 * or in some cases by an instance of the ColorTupleFloat class.
 * 
 * In an image, the color of a pixel is packed into an int, where 8 bits are available 
 * for the alpha, red, green, and blue components.  The ColorTuple
 * class provides methods for converting between a ColorTuple and packed int 
 * representation. 
 * 
 */

public class ImageOps {
	
	/**  
	 * returns the pixel at the x,y location in the given image in packed int form.
	 * 
	 * @param image
	 * @param x
	 * @param y
	 * @return
	 */
	public static int getPackedColor(BufferedImage image, int x, int y) {
		return image.getRGB(x,y);
	}
	
	/**
	 * returns the pixel at the x,y location in the given image in ColorTuple form.
	 * @param image
	 * @param x
	 * @param y
	 * @return
	 */
	public static ColorTuple getColorTuple(BufferedImage image, int x, int y) {
		int packedColor = image.getRGB(x, y);
		return ColorTuple.unpack(packedColor);
	}
	
	/**
	 * sets the pixel at the x,y location in the given image to the given int value.
	 * 
	 * @param image
	 * @param x
	 * @param y
	 * @param packedColor
	 */
	public static void setColor(BufferedImage image, int x, int y, int packedColor) {
		image.setRGB(x, y, packedColor);
	}
	
	/**
	 * Sets the pixel at the x,y location in the given image to the value represented 
	 * by the given ColorTuple.  The color values will be truncated if necessary.  
	 * 
	 * @param image
	 * @param x
	 * @param y
	 * @param colorTuple
	 */
	public static void setColor(BufferedImage image, int x, int y, ColorTuple colorTuple) {
		image.setRGB(x, y, colorTuple.pack());
	}
	
	/**
	 * Returns a new image containing only the red component of the given image.  
	 * This method can be used to implement the getRed operator applied to an image.
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage extractRed(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int pixel = getPackedColor(image, x, y);
				int red = ColorTuple.getRed(pixel);
				int redPixel = ColorTuple.makePackedColor(red, 0, 0);
				newImage.setRGB(x, y, redPixel);
			}
		}
		return newImage;
	}

	/**
	 * Returns a new image containing only the green component of the given image.  
	 * This can be used to implement the getGreen operator applied to an image.
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage extractGreen(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int pixel = getPackedColor(image, x, y);
				int green = ColorTuple.getGreen(pixel);
				int greenPixel = ColorTuple.makePackedColor(0, green, 0);
				newImage.setRGB(x, y, greenPixel);
			}
		}
		return newImage;
	}

	/**
	 * Returns a new image containing only the blue component of the given image.  
	 * This can be used to implement the getBlue operator applied to an image.
	 * 
	 * @param image
	 * @return
	 */	
	public static BufferedImage extractBlue(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int pixel = getPackedColor(image, x, y);
				int blue = ColorTuple.getBlue(pixel);
				int bluePixel = ColorTuple.makePackedColor(0, 0, blue);
				newImage.setRGB(x, y, bluePixel);
			}
		}
		return newImage;
	}


	public enum OP {
		PLUS, MINUS, TIMES, DIV, MOD
	}
	
	public enum BoolOP {
		EQUALS, NOT_EQUALS
	}
	
	
	/**
	 * Returns a new ColorTuple object obtained by applying given operator componentwise to the given
	 * ColorTuple objects.
	 * 
	 * @param op
	 * @param left
	 * @param right
	 * @return
	 */
	public static ColorTuple binaryTupleOp(OP op, ColorTuple left, ColorTuple right) {
		return switch(op) {
		case PLUS -> new ColorTuple (left.red() + right.red(), left.green() + right.green(), left.blue() + right.blue());
		case MINUS -> new ColorTuple (left.red() - right.red(), left.green() - right.green(), left.blue() - right.blue());
		case TIMES -> new ColorTuple (left.red() * right.red(), left.green() * right.green(), left.blue() * right.blue());
		case DIV -> new ColorTuple (left.red() / right.red(), left.green() / right.green(), left.blue() / right.blue());
		case MOD -> new ColorTuple (left.red() % right.red(), left.green() % right.green(), left.blue() % right.blue());
		default -> throw new IllegalArgumentException("Compiler/runtime error Unexpected value: " + op);
		};
	}
	
	
	/**
	 * Returns a new ColorTupleFloat object obtained by applying given operator componentwise to the given
	 * ColorTupleFloat objects.
	 * 
	 * @param op
	 * @param left
	 * @param right
	 * @return
	 */
	public static ColorTupleFloat binaryTupleOp(OP op, ColorTupleFloat left, ColorTupleFloat right) {
		return  switch(op) {
		case PLUS -> new ColorTupleFloat (left.red() + right.red(), left.green() + right.green(), left.blue() + right.blue());
		case MINUS -> new ColorTupleFloat (left.red() - right.red(), left.green() - right.green(), left.blue() - right.blue());
		case TIMES -> new ColorTupleFloat (left.red() * right.red(), left.green() * right.green(), left.blue() * right.blue());
		case DIV -> new ColorTupleFloat (left.red() / right.red(), left.green() / right.green(), left.blue() / right.blue());
		case MOD -> new ColorTupleFloat (left.red() % right.red(), left.green() % right.green(), left.blue() % right.blue());
		};
	}
	
	/**
	 * Applies operator to two ColorTuples and returns boolean value
	 * 
	 * @param op
	 * @param left
	 * @param right
	 * @return
	 */
	public static boolean binaryTupleOp(BoolOP op, ColorTuple left, ColorTuple right) {
		return switch(op) {
		case EQUALS -> left.equals(right);
		case NOT_EQUALS ->  !left.equals(right);
		default -> throw new IllegalArgumentException("Compiler/runtime error Unexpected value: " + op);
		};
	}
	
	/**
	 * Returns a new BufferedImage obtained by applying the given binary operator
	 * to each color component in each pixel in the given images.
	 * 
	 * If the images do not have the same shape, a PLCRuntimeException is thrown.
	 * 
	 * @param op
	 * @param left
	 * @param right
	 * @return
	 */
	
	public static BufferedImage binaryImageImageOp(OP op, BufferedImage left, BufferedImage right) {
		int lwidth = left.getWidth();
		int rwidth = right.getWidth();
		int lheight = left.getHeight();
		int rheight = right.getHeight();
		if (lwidth != rwidth || lheight != rheight) {
			throw new PLCRuntimeException("Attempting binary operation on images with unequal sizes");
		}
		BufferedImage result = new BufferedImage(lwidth, lheight, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < lwidth; x++) {
			for (int y = 0; y < lheight; y++) {
				ColorTuple leftColor = ColorTuple.unpack(left.getRGB(x, y));
				ColorTuple rightColor = ColorTuple.unpack(right.getRGB(x, y));
				ColorTuple newColor = binaryTupleOp(op, leftColor, rightColor);
				result.setRGB(x, y, newColor.pack());
			}
		}
		return result;
	}
	
	/**
	 * Returns a new buffered image obtained by applying the given binary operation 
	 * to each color component in each pixel in the given image (left) and the int value (right).
	 * 
	 * @param op
	 * @param left
	 * @param right
	 * @return
	 */
	public static BufferedImage binaryImageScalarOp(OP op, BufferedImage left, int right) {
		int lwidth = left.getWidth();
		int lheight = left.getHeight();
		BufferedImage result = new BufferedImage(lwidth, lheight, BufferedImage.TYPE_INT_RGB);
		ColorTuple rightColor = new ColorTuple(right);
		for (int x = 0; x < lwidth; x++) {
			for (int y = 0; y < lheight; y++) {
				ColorTuple leftColor = ColorTuple.unpack(left.getRGB(x, y));
				ColorTuple newColor = binaryTupleOp(op, leftColor, rightColor);
				result.setRGB(x, y, newColor.pack());
			}
		}
		return result;
	}
	
//	public static BufferedImage binaryImageScalarOp(OP op, BufferedImage left, float right) {
//		int lwidth = left.getWidth();
//		int lheight = left.getHeight();
//		BufferedImage result = new BufferedImage(lwidth, lheight, BufferedImage.TYPE_INT_RGB);
//		ColorTupleFloat rightColor = new ColorTupleFloat(right);
//		for (int x = 0; x < lwidth; x++) {
//			for (int y = 0; y < lheight; y++) {
//				ColorTupleFloat leftColor = new ColorTupleFloat(ColorTuple.unpack(left.getRGB(x, y)));
//				ColorTupleFloat newColor = binaryTupleOp(op, leftColor, rightColor);
//				result.setRGB(x, y, newColor.pack());
//			}
//		}
//		return result;
//	}
//	
//	
//	public static BufferedImage setAllPixels(BufferedImage image, int val) {
//		ColorTuple c = new ColorTuple(val);
//		for (int x = 0; x < image.getWidth(); x++)
//			for (int y = 0; y < image.getHeight(); y++) {
//				image.setRGB(x, y,  c.pack());
//			}
//		return image;
//	}

	
//	/**
//	 * Creates an image of given size.  All color components of all pixels have the indicated 
//	 * color intensity. 
//	 * 
//	 * @param width
//	 * @param height
//	 * @param colorComponentVal
//	 * @return
//	 */
//	public static BufferedImage makeConstantImage(int width, int height, int colorComponentVal) {
//		BufferedImage image = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
//		return setAllPixels(image,colorComponentVal);
//	}
//	

	/**
	 * Returns a new image that is copy of the given BufferedImage 
	 * @param image
	 * @return new image that is copy of the given image
	 */
	public static final BufferedImage clone(BufferedImage image) {
	    BufferedImage clone = new BufferedImage(image.getWidth(),
	            image.getHeight(), image.getType());
	    Graphics2D g2d = clone.createGraphics();
	    g2d.drawImage(image, 0, 0, null);
	    g2d.dispose();
	    return clone;
	}
	
	/**
	 * Returns a new image that is a resized version of the 'before' image.
	 * 
	 * @param image
	 * @param maxX
	 * @param maxY
	 * @return new image that is a resized version of the 'before' image
	 */
	public static BufferedImage resize(BufferedImage image, int maxX,
			int maxY) {
		int w = image.getWidth();
		int h = image.getHeight();
		AffineTransform at = new AffineTransform();
		at.scale(((float) maxX) / w, ((float) maxY) / h);
		AffineTransformOp scaleOp = new AffineTransformOp(at,
				AffineTransformOp.TYPE_BILINEAR);
		BufferedImage newResizedImage= null;
		newResizedImage = scaleOp.filter(image, newResizedImage);
		return newResizedImage;
	}
	

	/**
	 * Returns an array of ints representing the packed pixels of the given image.
	 * 
	 * This can be used in Junit test to compare two images by using 
	 * assertArrayEquals on the array return from this method.
	 * 
	 * @param result
	 * @return array of ints representing the packed pixels of the given image
	 */
	public static int[] getRGBPixels(BufferedImage result) {
		return result.getRGB(0,0,result.getWidth(), result.getHeight(), null,0,result.getWidth());
	}
	
}
