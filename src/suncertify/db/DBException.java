package suncertify.db;

/**
 * This exception is implemented to handle exceptions
 * such as FileNotFoundException, IOException and
 * UnsupportedEncodingException which result from operations in the 
 * DBAccessor and LockManager classes. It extends RuntimeException 
 * (which is not a checked exception) in order to maintain compliance
 * with supplied DB.java interface.
 * @author john
 *
 */
public class DBException extends RuntimeException {
    
    /**
     * The SUID.
     */
    private static final long serialVersionUID = 1991L;

    /**
     * Construct a new <code>DBException</code>.
     */
    public DBException() {
        super();
    }
    
    /**
     * Construct a new <code>DBException</code> with message,
     * that wraps another exception.
     * @param message the exception message.
     * @param throwable the <code>Throwable</code> to wrap in
     * <code>DBException</code>.
     */
    public DBException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
