package com.ecfeed.core.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

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

public class LogHelperCore {

    private enum Target { CONSOLE, FILE };

    private static final Logger logger;
    private static final String patternFile;
    private static final String patternFileInstance;

    private static Target target = Target.FILE;
    private static String tag = "[Core] ";
    private static String[] locations = new String[]{"logs", "log", "/home/gradle/log"};
    private static String patternLog = "%d{HH:mm:ss.SSS} [%thread] %-5level - %msg%n";
    private static String sizeCap = "500 mb";
    private static int historyCap = 3;

    private static final String INDENT = "  ";

    static {
        String parsedLocation = getPath();
        String parsedLocationAbsolute = Paths.get(parsedLocation).toAbsolutePath().toString();

        System.out.println("logs: " + parsedLocationAbsolute);

        patternFileInstance = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss").format(new Date());
        patternFile = parsedLocation + File.separator + "core." + patternFileInstance + ".%d{yyyy-MM-dd}.log";

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        Appender<ILoggingEvent> appender = getAppender(context);

        logger = context.getLogger("MainCore");
        logger.setAdditive(false);
        logger.setLevel(Level.WARN);
        logger.addAppender(appender);

        logInfo("Initialized");
    }

    private static String getPath() {

        for (String location : locations) {
            if (Files.exists(Paths.get(location))) {
                return location;
            }
        }

        return "";
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

        logger.error(tag + message);
    }

    public static void logCatch(Exception e) {

        logger.error(tag + e.toString());
    }

    public static void log(String message) {

        logInfo("[ALG-LOG] " + message);
    }

    public static void log(String message, Object o) {

        logInfo("[ALG-LOG] " + message);
        logInfo(INDENT + o.toString());
    }

    public static void log(String message, List<?> o) {

        logInfo("[ALG-LOG] " + message);

        int counter = 0;

        for (Object element : o) {
            logCounterAndElement(counter, element);
            counter++;
        }
    }

    public static void log(String message, Multiset<?> o) {

        logInfo("[ALG-LOG] " + message);

        int counter = 0;

        for (Object element : Multisets.copyHighestCountFirst(o).elementSet()) {
            logCounterAndElement(counter, element);
            counter++;
        }

    }

    private static void logCounterAndElement(int counter, Object element) {

        if (element == null) {
            LogHelperCore.logInfo(INDENT + "[ " + counter + " ]");
        } else {
            LogHelperCore.logInfo(INDENT + "[ " + counter + " ] [ " + element.toString() + " ]");
        }

    }

}
