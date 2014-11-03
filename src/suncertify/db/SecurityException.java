package suncertify.db;

/**
 * Thrown if an attempt is made to update, delete
 * or unlock a record with a cookie other than the
 * cookie that the record was locked with.
 *
 * @author John Harding
 */
public class SecurityException extends Exception {
    
    /**
     * The SUID.
     */
    private static final long serialVersionUID = 1551L;

    /**
     * Construct a new <code>SecurityException</code>.
     */
    public SecurityException() {
        super();
    }
    
    /**
     * Construct a new <code>SecurityException</code>
     * with message.
     * @param message the exception message.
     */
    public SecurityException(String message) {
        super(message);
    }

}
