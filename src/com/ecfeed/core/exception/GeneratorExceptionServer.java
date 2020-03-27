package com.ecfeed.core.exception;

public class GeneratorExceptionServer extends GeneratorException {

    public GeneratorExceptionServer(String errorMessage) {
        super(errorMessage);
    }

    public GeneratorExceptionServer(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

}
