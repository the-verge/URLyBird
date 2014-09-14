package suncertify.network;

/**
 * This exception is implemented to handle networking 
 * exceptions such as RemoteException which result from operations 
 * performed in the DataProxy class. It extends RuntimeException 
 * (which is not a checked exception) in order for DataProxy 
 * to maintain compliance with supplied DB interface.
 * @author john
 *
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
