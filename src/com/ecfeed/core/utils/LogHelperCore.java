package com.ecfeed.core.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class LogHelperCore {

    private enum Target { CONSOLE, FILE };

    private static final Logger logger;
    private static final String patternFile;
    private static final String patternFileInstance;

    private static Target target = Target.FILE;
    private static String tag = "[Core] ";
    private static String location = "logs";
    private static String patternLog = "%d{HH:mm:ss.SSS} [%thread] %-5level - %msg%n";
    private static String sizeCap = "500 mb";
    private static int historyCap = 3;

    private static final String INDENT = "  ";

    static {

        if (!Files.exists(Paths.get(location))) {
            target = Target.CONSOLE;
            System.out.println("The " + location + "could not be accessed");
        }

        patternFileInstance = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss").format(new Date());
        patternFile = location + File.separator + "db." + patternFileInstance + ".%d{yyyy-MM-dd}.log";

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        Appender<ILoggingEvent> appender = getAppender(context);

        logger = context.getLogger("Main");
        logger.setAdditive(false);
        logger.setLevel(Level.DEBUG);
        logger.addAppender(appender);
    }

    private static Appender<ILoggingEvent> getAppender(LoggerContext context) {

        if (target == Target.CONSOLE) {
            return getConsoleAppender(context);
        } else {
            return getFileAppender(context);
        }
    }

    private static ConsoleAppender<ILoggingEvent> getConsoleAppender(LoggerContext context) {
        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<ILoggingEvent>();

        consoleAppender.setContext(context);
        consoleAppender.setName("console");
        consoleAppender.setEncoder(getEncoder(context));
        consoleAppender.start();

        return consoleAppender;
    }

    private static RollingFileAppender<ILoggingEvent> getFileAppender(LoggerContext context) {
        RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<ILoggingEvent>();

        fileAppender.setContext(context);
        fileAppender.setName("file");
        fileAppender.setEncoder(getEncoder(context));
        fileAppender.setAppend(true);
        fileAppender.setPrudent(false);
        fileAppender.setImmediateFlush(true);

        TimeBasedRollingPolicy<Object> rollingPolicy = new TimeBasedRollingPolicy<Object>();
        rollingPolicy.setContext(context);
        rollingPolicy.setParent(fileAppender);
        rollingPolicy.setFileNamePattern(patternFile);
        rollingPolicy.setMaxHistory(historyCap);
        rollingPolicy.setTotalSizeCap(FileSize.valueOf(sizeCap));
        rollingPolicy.start();

        fileAppender.setRollingPolicy(rollingPolicy);
        fileAppender.start();

        return fileAppender;
    }


    private static PatternLayoutEncoder getEncoder(LoggerContext context) {
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();

        encoder.setContext(context);
        encoder.setPattern(patternLog);
        encoder.start();

        return encoder;
    }

    public static void logInfo(String message) {

        logger.info(tag + message);
    }

    public static void logError(String message) {

        logger.error(tag + message);
    }

    public static void logThrow(String message) {
        JSONObject json = new JSONObject();

        json.put("type", "Exception thrown");
        json.put("message", message);

        StackTraceElement[] stackElements = new Throwable().getStackTrace();
        StackTraceElement currentElement = stackElements[2];

        json.put("current", getCurrentStackElement(currentElement));
        json.put("stack", getStack(stackElements));

        LogHelperCore.logError(json.toString());
    }

    public static void logCatch(Exception e) {
        JSONObject json = new JSONObject();

        json.put("type", "Exception caught");
        json.put("message", ExceptionHelper.createErrorMessage(e));

        StackTraceElement element = new Throwable().getStackTrace()[1];

        json.put("current", getCurrentStackElement(element));

        LogHelperCore.logError(json.toString());
    }

    private static JSONObject getCurrentStackElement(StackTraceElement element) {
        JSONObject json = new JSONObject();

        json.put("file", element.getFileName());
        json.put("class", element.getClassName());
        json.put("method", element.getMethodName());
        json.put("line", element.getLineNumber());

        return json;
    }

    private static JSONObject getStack(StackTraceElement[] stackElements) {
        JSONObject json = new JSONObject();

        JSONArray array = new JSONArray();

        for (StackTraceElement element : stackElements) {
            array.put(getStackElement(element));
        }

        json.put("stack", array);

        return json;
    }

    private static JSONObject getStackElement(StackTraceElement element) {
        JSONObject json = new JSONObject();

        json.put("class", element.getClassName());
        json.put("method", element.getMethodName());
        json.put("line", element.getLineNumber());

        return json;
    }

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
