package suncertify.application;

/**
 * This exception is used to wrap <code>IOException</code> if it is thrown
 * from the private methods in the <code>PropertiesAccessor</code> class.
 *
 * @author John Harding
 */
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
     *         <code>ConfigurationException</code>.
     */
    public ConfigurationException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
