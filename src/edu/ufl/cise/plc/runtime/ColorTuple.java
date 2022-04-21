package edu.ufl.cise.plc.runtime;

import java.awt.Color;
import java.io.Serializable;
import java.util.Objects;

/**
 * This class implements the color type in PLCLang. It also provides routines
 * for converting back and forth between a representation of pixels as a
 * ColorTuple, and as an int.
 * 
 * This class does not restrict color components to be in the range [0,256).
 * When a ColorTuple object is packed into an int, values less than 0 will be
 * changed to 0 and values greater than 255 will be set to 255.
 * 
 * Objects of this class are immutable.
 * 
 * @author Beverly Sanders
 *
 */
public class ColorTuple implements Serializable {

	private static final long serialVersionUID = 1758421158722901761L;

	public final int red;
	public final int green;
	public final int blue;

	public ColorTuple(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	/**
	 * This constructor takes a single int value and creates a ColorTuple object
	 * with the given value in all three color components.
	 * 
	 * @see unpack to create a ColorTuple from an int interpreted as a packed pixel.
	 * 
	 * @param value
	 */
	public ColorTuple(int value) {
		this(value, value, value);
	}

	/**
	 * This constructor takes a ColorFloatTuple and returns a ColorTuple with color
	 * intensity values obtained by rounding the ColorFloatTuple values to the
	 * nearest int.
	 * 
	 * @param floatTuple
	 */
	public ColorTuple(ColorTupleFloat floatTuple) {
		this(Math.round(floatTuple.red()), Math.round(floatTuple.green()), Math.round(floatTuple.blue()));
	}

	/**
	 * 
	 * @return the red component of this ColorTuple
	 */
	int red() {
		return red;
	}

	/**
	 * 
	 * @return the green component of this ColorTuple
	 */
	int green() {
		return green;
	}

	/**
	 * 
	 * @return the blue component of this ColorTuple
	 */
	int blue() {
		return blue;
	}

	/**
	 * Color constants in PLCLang are obtained from the constants defined in the
	 * java.awt.Color class.
	 * 
	 * @param color
	 * @return
	 */
	public static ColorTuple toColorTuple(Color color) {
		return unpack(color.getRGB());
	}

	/**
	 * truncates an int to value in range of [0,256)
	 * 
	 * @param z
	 * @return value in [0,256)
	 */
	private static int truncate(int z) {
		return z < 0 ? 0 : (z > 255 ? 255 : z);
	}

	/**
	 * Return a packed pixel representation of this ColorTuple
	 * 
	 * @return
	 */
	public int pack() {
		return 0xFF << SHIFT_ALPHA | truncate(red) << SHIFT_RED | truncate(green) << SHIFT_GRN
				| truncate(blue) << SHIFT_BLU;
	}

	/*
	 * create a packed color with the given color component values. Values less than
	 * 0 or greater than 255 are truncated.
	 */
	public static int makePackedColor(int redVal, int grnVal, int bluVal) {
		int pixel = ((0xFF << SHIFT_ALPHA) | (truncate(redVal) << SHIFT_RED) | (truncate(grnVal) << SHIFT_GRN)
				| (truncate(bluVal) << SHIFT_BLU));

		return pixel;
	}

	/**
	 * Interprets an int as a packed color and returns the equivalent ColorTuple
	 * object. The alpha component is ignored.
	 * 
	 * @param packedColor
	 * @return
	 */
	public static ColorTuple unpack(int packedColor) {
		return new ColorTuple(getRed(packedColor), getGreen(packedColor), getBlue(packedColor));
	}

	/**
	 * Returns the red component of an int interpreted as a packed color
	 * 
	 * @param packedColor
	 * @return
	 */
	public static int getRed(int packedColor) {
		return (packedColor & SELECT_RED) >> SHIFT_RED;
	}

	/**
	 * Static method to return red component of the given colorTuple. Provided for
	 * convenience.
	 * 
	 * @param colorTuple
	 * @return
	 */
	public static int getRed(ColorTuple colorTuple) {
		return colorTuple.red();
	}

	/**
	 * Returns the green component of an int interpreted as a packed color
	 * 
	 * @param packedColor
	 * @return
	 */
	public static int getGreen(int packedColor) {
		return (packedColor & SELECT_GRN) >> SHIFT_GRN;
	}

	/**
	 * Static method to return green component of the given ColorTuple.
	 * 
	 * @param colorTuple
	 * @return
	 */
	public static int getGreen(ColorTuple colorTuple) {
		return colorTuple.green();
	}

	/**
	 * Returns the blue component of an int interpreted as a packed color
	 * 
	 * @param packedColor
	 * @return
	 */
	public static int getBlue(int packedColor) {
		return (packedColor & SELECT_BLU) >> SHIFT_BLU;
	}

	/**
	 * Static method to return the blue component of the given ColorTuple.
	 * 
	 * @param colorTuple
	 * @return
	 */
	public static int getBlue(ColorTuple colorTuple) {
		return colorTuple.blue();
	}

	/** Constants used in building and select color components from a packed int */
	public static final int SELECT_RED = 0x00ff0000;
	public static final int SELECT_GRN = 0x0000ff00;
	public static final int SELECT_BLU = 0x000000ff;
	public static final int SELECT_ALPHA = 0xff000000;
	public static final int SHIFT_ALPHA = 24;
	public static final int SHIFT_RED = 16;
	public static final int SHIFT_GRN = 8;
	public static final int SHIFT_BLU = 0;

	/**
	 * Returns String showing packed pixel in hex format. Alpha, red, green, and
	 * blue component are each two digits. This is not required for the project, but
	 * may be useful when debugging.
	 * 
	 * @param packedPixel
	 * @return
	 */
	public static String packedToString(int packedPixel) {
		return Integer.toHexString(packedPixel);
	}

	@Override
	public String toString() {
		return "ColorTuple [red=" + red + ", green=" + green + ", blue=" + blue + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(blue, green, red);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ColorTuple other = (ColorTuple) obj;
		return blue == other.blue && green == other.green && red == other.red;
	}

}