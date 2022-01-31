package edu.ufl.cise.plc;

import edu.ufl.cise.plc.IToken.SourceLocation;

@SuppressWarnings("serial")
public class LexicalException extends PLCException {

	public LexicalException(String error_message, SourceLocation loc) {
		super(error_message, loc);
	}

	public LexicalException(String message) {
		super(message);
	}

	public LexicalException(Throwable cause) {
		super(cause);
	}
	
	public LexicalException(String error_message, int line, int column) {
		super(line + ":" + column + "  " + error_message);
	}

}
