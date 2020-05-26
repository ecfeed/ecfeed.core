package com.ecfeed.core.exception;

public class GeneratorExceptionClient extends GeneratorException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 616034714039396827L;

	public GeneratorExceptionClient(String errorMessage) {
        super(errorMessage);
    }

    public GeneratorExceptionClient(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

}