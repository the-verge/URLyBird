package suncertify.application;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A utility class whose only function is to set logging levels
 *
 * @author John Harding
 */
public class Utils {
    
    public static void setLogLevel(Logger log, Level level) {
        /**
         * No SecurityManager exists so java.lang.SecurityException
         * will not be thrown.
         */
        log.setUseParentHandlers(false);
        log.setLevel(level);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(level);
        log.addHandler(handler);
    }
}
