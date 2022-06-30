package com.ecfeed.core.utils;

import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

import java.util.List;

public class AlgoLogger {

    private static final String INDENT = "  ";

    public static void log(String message, int logLevel, int controllingVariable) {

        if (!shouldLogThisMessage(logLevel, controllingVariable)) {
            return;
        }

        logHeaderAndMessage(message);
    }

    public static void log(String message, Object o, int logLevel, int controllingVariable) {

        if (!shouldLogThisMessage(logLevel, controllingVariable)) {
            return;
        }

        logHeaderAndMessage(message);
        LogHelperCore.logInfo(INDENT + o.toString());
    }

    public static void log(String message, List<?> o, int logLevel, int controllingVariable) {

        if (!shouldLogThisMessage(logLevel, controllingVariable)) {
            return;
        }

        logHeaderAndMessage(message);

        int counter = 0;

        for (Object element : o) {
            logCounterAndElement(counter, element);
            counter++;
        }
    }

    public static void log(String message, Multiset<?> o, int logLevel, int controllingVariable) {

        if (!shouldLogThisMessage(logLevel, controllingVariable)) {
            return;
        }

        logHeaderAndMessage(message);

        int counter = 0;

        for (Object element : Multisets.copyHighestCountFirst(o).elementSet()) {
            logCounterAndElement(counter, element);
            counter++;
        }

    }

    private static void logCounterAndElement(int counter, Object element) {

        LogHelperCore.logInfo(INDENT + "[ " + counter + " ] [ " + element.toString() + " ]");
    }

    private static boolean shouldLogThisMessage(int logLevel, int controllingVariable) {

        return (logLevel <= controllingVariable);
    }

    private static void logHeaderAndMessage(String message) {

        LogHelperCore.logInfo("[ALG-LOG] " + message);
    }
}
