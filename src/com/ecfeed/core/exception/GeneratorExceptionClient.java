package com.ecfeed.core.exception;

public class GeneratorExceptionClient extends GeneratorException {

    public GeneratorExceptionClient(String errorMessage) {
        super(errorMessage);
    }

    public GeneratorExceptionClient(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

}