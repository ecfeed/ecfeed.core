package com.ecfeed.core.exception;

public class GeneratorException extends RuntimeException {

    private String streamMessage = "";

    public String getStreamMessage() {
        return this.streamMessage;
    }

    public void setStreamMessage(String streamMessage) {
        this.streamMessage = streamMessage;
    }

    public GeneratorException(String errorMessage) {
        super(errorMessage);
    }

    public GeneratorException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
