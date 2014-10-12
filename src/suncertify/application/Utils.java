package suncertify.application;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {
    
    public static void setLogLevel(Logger log, Level level) {
        log.setUseParentHandlers(false);
        log.setLevel(level);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(level);
        log.addHandler(handler);
    }
}
