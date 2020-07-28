package org.youtube.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogUtil {

    private static final Logger logger = Logger.getLogger("");

    public static void init() {
        try {
            // This block configure the logger with handler and formatter
            String path = System.getProperty("user.dir");
            FileHandler fh = new FileHandler(path + "/log.txt");
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
        logger.info(message);
    }

    public static void severe(String message) {
        logger.severe(message);
    }

    public static void warning(String message) {
        logger.warning(message);
    }
}
