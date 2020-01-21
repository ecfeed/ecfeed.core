package com.ecfeed.core.evaluator;

import com.ecfeed.core.utils.AlgoLogger;

public class Sat4jLogger {

    public static void log(String message, ParamsWithChInts o, int logLevel, int controllingVariable) {

        AlgoLogger.log(message, o.getInternalMap(), logLevel, controllingVariable);
    }

    public static void log(String message, Sat4jClauses o, int logLevel, int controllingVariable) {

        AlgoLogger.log(message, o.getInternalList(), logLevel, controllingVariable);
    }

    public static void log(String message, Object o, int logLevel, int controllingVariable) {

        AlgoLogger.log(message, o, logLevel, controllingVariable);
    }

}
