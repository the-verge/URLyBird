package suncertify.network;

/**
 * This exception is implemented to handle networking 
 * exceptions such as <code>RemoteException</code> which result from operations
 * performed in the <code>DataProxy</code> class. It extends
 * <code>RuntimeException</code> (which is not a checked exception)
 * in order for <code>DataProxy</code> to maintain compliance with supplied
 * <code>DB</code> interface.
 *
 * @author John Harding
 */
public class NetworkException extends RuntimeException {
    
    /**
     * The SUID.
     */
    private static final long serialVersionUID = 2001L;

    /**
     * Construct a new <code>NetworkException</code>.
     */
    public NetworkException() {
        super();
    }
    
    /**
     * Construct a new <code>NetworkException</code> with message,
     * that wraps another exception.
     * @param message the exception message.
     * @param throwable the <code>Throwable</code> to wrap in
     * 		   <code>NetworkException</code>.
     */
    public NetworkException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
