package com.ecfeed.core.exception;

public class GeneratorException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7245190958322769521L;

    public GeneratorException(String errorMessage) {
        super(errorMessage);
    }

    public GeneratorException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
