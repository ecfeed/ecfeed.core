package com.ecfeed.core.exception;

public class ClientException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 616034714039396827L;

	public ClientException(String errorMessage) {
        super(errorMessage);
    }

    public ClientException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

}