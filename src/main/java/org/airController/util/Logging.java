package org.airController.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Logging {
    private static final Logger logger;

    static {
        try {
            logger = Logger.getLogger(Logging.class.getName());
            Files.createDirectories(Paths.get("log"));
            final String logFileName = new SimpleDateFormat("'log/airController_'yyyyMMdd_HHmm'.log'").format(new Date());
            final FileHandler fileHandler = new FileHandler(logFileName);
            final SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Logger getLogger() {
        return logger;
    }
}
