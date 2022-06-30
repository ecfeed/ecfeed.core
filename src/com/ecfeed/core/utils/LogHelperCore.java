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
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;


public class LogHelperCore {

    private static enum Target { CONSOLE, FILE };

    private static final Logger logger;
    private static final String patternFile;
    private static final String patternFileInstance;

    private static Target target = Target.FILE;
    private static String tag = "[Core] ";
    private static String location = "logs";
    private static String patternLog = "%d{HH:mm:ss.SSS} [%thread] %-5level - %msg%n";
    private static String sizeCap = "500 mb";
    private static int historyCap = 3;

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

}
