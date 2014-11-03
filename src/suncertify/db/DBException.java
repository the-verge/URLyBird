package suncertify.db;

/**
 * This exception is implemented to handle exceptions
 * such as <code>FileNotFoundException</code>, <code>IOException</code> and
 * <code>UnsupportedEncodingException</code> which result from operations in the 
 * <code>DBAccessor</code> and <code>LockManager</code> classes. It extends 
 * <code>RuntimeException</code> (which is not a checked exception) in order 
 * to maintain compliance with supplied <code>DB</code> interface.
 *
 * @author John Harding
 */
public class DBException extends RuntimeException {
    
    /**
     * The SUID.
     */
    private static final long serialVersionUID = 1221L;

    /**
     * Construct a new <code>DBException</code>.
     */
    public DBException() {
        super();
    }
    
    /**
     * Construct a new <code>DBException</code> with message.
     * @param message the exception message.
     */
    public DBException(String message) {
        super(message);
    }
    
    /**
     * Construct a new <code>DBException</code> with message,
     * that wraps another exception.
     * @param message the exception message.
     * @param throwable the <code>Throwable</code> to wrap in
     * 		   <code>DBException</code>.
     */
    public DBException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
