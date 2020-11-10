package org.youtube.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogUtil {

    private static final Logger logger = Logger.getLogger("");

    public static void init(String logName) {
        try {
            // This block configure the logger with handler and formatter
            String path = System.getProperty("user.dir");
            String logFileName = path + "/logs/" + logName + ".txt";
            //check xem thu muc da ton tai hay chua
            File file = new File(logFileName);
            if (!file.exists()) {
                boolean createFolder = file.getParentFile().mkdirs();
                logger.info("Created logs folder");
            }
            FileHandler fh = new FileHandler(logFileName);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            // the following statement is used to log any messages
            info("Init log message");
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
    }

    public static void info(String message) {
        logger.info(Thread.currentThread() + message);
    }

    public static void severe(String message) {
        logger.severe(Thread.currentThread() + message);
    }

    public static void warning(String message) {
        logger.warning(Thread.currentThread() + message);
    }
}
