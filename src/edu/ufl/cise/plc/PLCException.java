package edu.ufl.cise.plc;

/**
 * This is the superclass of all the exceptions that will be thrown in our project
 */

import edu.ufl.cise.plc.IToken.SourceLocation;

@SuppressWarnings("serial")
public class PLCException extends Exception {

	public PLCException(String message) {
		super(message);
	}

	public PLCException(Throwable cause) {
		super(cause);
	}
	
	public PLCException(String error_message, int line, int column) {
		super(line + ":" + column + "  " + error_message);
	}

	public PLCException(String error_message, SourceLocation loc) {
		super(loc.line()+ ":" + loc.column() + " " + error_message);
	}

}
