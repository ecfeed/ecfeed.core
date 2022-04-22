package com.ecfeed.core.utils;

public enum LoggingLevel {

    DETAILED,
    STANDARD;

    private static LoggingLevel fLoggingLevel = STANDARD;

    public static LoggingLevel get() {
        return fLoggingLevel;
    }
}
