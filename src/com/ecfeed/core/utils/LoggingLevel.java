package com.ecfeed.core.utils;

public enum LoggingLevel {

    DETAILED,
    STANDARD;

    private static LoggingLevel fLoggingLevel = DETAILED;

    public static LoggingLevel get() {
        return fLoggingLevel;
    }
}
