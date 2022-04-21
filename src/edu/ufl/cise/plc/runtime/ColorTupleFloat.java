package edu.ufl.cise.plc.runtime;

/**
 * 
 * This class implements the color type with components having float types
 * in PLCLang. 
 * 
 * This class does not restrict color components to be in the range [0,256).
 * 
 * Objects of this class are immutable.
 * 
 * @author Beverly Sanders
 *
 */
 
import java.io.Serializable;
import java.util.Objects;

public class ColorTupleFloat implements Serializable{
	
	private static final long serialVersionUID = -3867273454790003519L;
	
	final float red;
	final float green;
	final float blue;
	
	
	public ColorTupleFloat(float red, float green, float blue) {
		super();
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	/**
	 * Creates a ColorTupleFloat where all color components have the same, given, value
	 * @param val
	 */
	public ColorTupleFloat(float val) {
		this(val,val,val);
	}
		
	
	/**
	 * Creates a ColorTupleFloat with the same color values as the given ColorTuple
	 * 
	 * @param c
	 */
	public ColorTupleFloat(ColorTuple c) {
		this((float)c.red, (float)c.green, (float)c.blue);
	}	
	
	public float red() {
		return red;
	}

	public float green() {
		return green;
	}

	public float blue() {
		return blue;
	}

	public int pack() {
		return (new ColorTuple(this)).pack();
	}

	@Override
	public String toString() {
		return "ColorTupleFloat [red=" + red + ", green=" + green + ", blue=" + blue + "]";
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
		ColorTupleFloat other = (ColorTupleFloat) obj;
		return Float.floatToIntBits(blue) == Float.floatToIntBits(other.blue)
				&& Float.floatToIntBits(green) == Float.floatToIntBits(other.green)
				&& Float.floatToIntBits(red) == Float.floatToIntBits(other.red);
	}


}