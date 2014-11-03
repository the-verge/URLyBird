package suncertify.db;

/**
 * Thrown when a specified record does not exist
 * or is marked as deleted in the database.
 *
 * @author John Harding
 */
public class RecordNotFoundException extends Exception {
    
    /**
     * The SUID.
     */
    private static final long serialVersionUID = 1441L;

    /**
     * Construct a new <code>RecordNotFoundException</code>.
     */
    public RecordNotFoundException() {
        super();
    }
    
    /**
     * Construct a new <code>RecordNotFoundException</code>
     * with message.
     * @param message the exception message.
     */
    public RecordNotFoundException(String message) {
        super(message);
    }

}
