package edu.ufl.cise.plc;

import edu.ufl.cise.plc.IToken.SourceLocation;

@SuppressWarnings("serial")
public class SyntaxException extends PLCException {

	public SyntaxException(String error_message, SourceLocation loc) {
		super(error_message, loc);
		
	}

	public SyntaxException(String message) {
		super(message);
	}
	
	

}
