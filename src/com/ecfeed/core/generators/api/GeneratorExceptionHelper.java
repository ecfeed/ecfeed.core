package com.ecfeed.core.generators.api;

import com.ecfeed.core.utils.ExceptionHelper;

public class GeneratorExceptionHelper {

    public enum ThrownExceptionType {
        CLIENT,
        RUNTIME
    }

    // generator used in RAP should throw runtime exceptions in case of invalid request
    // because our frontend controlls the data
    // in case of remote generator it is the client/user who creates the request
    // and then client exception should be thrown
    // which is handled differently in generator service than runtime exception
    // (we do not log client exceptions in standard logging mode)
    private static ThrownExceptionType fThrownExceptionType = ThrownExceptionType.RUNTIME;

    public static void setThrownExceptionType(ThrownExceptionType thrownExceptionType) {
        fThrownExceptionType = thrownExceptionType;
    }

    public static void reportException(String s) {

        if (fThrownExceptionType == ThrownExceptionType.CLIENT) {
            ExceptionHelper.reportClientException(s);
        } else {
            ExceptionHelper.reportRuntimeException(s);
        }
    }
}
