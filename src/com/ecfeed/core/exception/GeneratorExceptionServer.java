package com.ecfeed.core.exception;

public class GeneratorExceptionServer extends GeneratorException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GeneratorExceptionServer(String errorMessage) {
        super(errorMessage);
    }

    public GeneratorExceptionServer(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

}
