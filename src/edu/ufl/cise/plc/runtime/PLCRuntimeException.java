package edu.ufl.cise.plc.runtime;

@SuppressWarnings("serial")
public class PLCRuntimeException extends RuntimeException {

	public PLCRuntimeException() {
		super();
	}

	public PLCRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PLCRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public PLCRuntimeException(String message) {
		super(message);
	}

	public PLCRuntimeException(Throwable cause) {
		super(cause);
	}
	
	

}
