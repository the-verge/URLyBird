package suncertify.application;

public class ConfigurationException extends Exception {
    
    /**
     * The SUID.
     */
    private static final long serialVersionUID = 1111L;

    /**
     * Construct a new <code>ConfigurationException</code>.
     */
    public ConfigurationException() {
        super();
    }
    
    /**
     * Construct a new <code>ConfigurationException</code> with message,
     * that wraps another exception.
     * @param message the exception message.
     * @param throwable the <code>Throwable</code> to wrap in
     *         <code>DBException</code>.
     */
    public ConfigurationException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
