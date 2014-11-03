package suncertify.db;

/**
 * This exception is not thrown at any point in the application.
 * The reasons for this are outlined in choices.txt.
 * It is implemented as it is a requirement of the project. 
 *
 * @author John Harding
 */
public class DuplicateKeyException extends Exception {
    
    /**
     * The SUID.
     */
    private static final long serialVersionUID = 1331L;

    /**
     * Construct a new <code>DuplicateKeyException</code>.
     */
    public DuplicateKeyException() {
        super();
    }
    
    /**
     * Construct a new <code>DuplicateKeyException</code>
     * with message.
     * @param message the exception message.
     */
    public DuplicateKeyException(String message) {
        super(message);
    }

}
