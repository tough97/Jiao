package com.cs.gang.util;

import org.apache.log4j.*;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 5/6/13
 * Time: 8:21 AM
 */
public final class Logger {

    private static final File LOG_DIR = new File("lg");
    static{
        if(!LOG_DIR.exists()){
            LOG_DIR.mkdirs();
        }
    }
    private static final String PATTERN = "%d %-5p [%c{1}] %m%n";
    public static enum Type{
        FATAL, ERROR, WARNING, DEBUG, INFO, TRACE
    };

    public static void log(final Class clazz, final Type type, final Object message){
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(clazz);
        logger.setAdditivity(false);
        Appender appender = null;
        try {
            appender = new RollingFileAppender(new PatternLayout(PATTERN),
                    new File(LOG_DIR, clazz.getName()).getCanonicalPath(), true);

        } catch (final IOException e) {
            e.printStackTrace();
        }
        logger.addAppender(appender);
        switch (type){
            case FATAL:
                logger.setLevel(Level.FATAL);
                logger.fatal(message);
                break;
            case ERROR:
                logger.setLevel(Level.FATAL);
                logger.error(message);
                break;
            case WARNING:
                logger.setLevel(Level.WARN);
                logger.warn(message);
                break;
            case DEBUG:
                logger.setLevel(Level.DEBUG);
                logger.debug(message);
                break;
            case INFO:
                logger.setLevel(Level.INFO);
                logger.info(message);
                break;
            case TRACE:
                logger.setLevel(Level.TRACE);
                logger.trace(message);
                break;
        }
        appender.close();
    }

}
