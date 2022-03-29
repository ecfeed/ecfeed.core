package com.ecfeed.core.exception;

public class ServerException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServerException(String errorMessage) {
        super(errorMessage);
    }

    public ServerException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

}
