package edu.ufl.cise.plc;

import edu.ufl.cise.plc.IToken.SourceLocation;

@SuppressWarnings("serial")
public class TypeCheckException extends PLCException {

	
	public TypeCheckException(String message) {
		super(message);
	}
	
	public TypeCheckException(Throwable cause) {
		super(cause);
	}
	
	public TypeCheckException(String message, SourceLocation loc) {
		super(message, loc);
	}
	
	public TypeCheckException(String error_message, int line, int column) {
		super(line + ":" + column + "  " + error_message);
	}

}