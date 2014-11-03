package suncertify.application;

/**
 * This exception is thrown when a client tries to reserve
 * a room that is already booked.
 *
 * @author John Harding
 */
public class RoomAlreadyBookedException extends Exception {

    /**
     * The SUID.
     */
    private static final long serialVersionUID = 3113L;

    /**
     * Construct a new <code>RoomAlreadyBookedException</code>.
     */
    public RoomAlreadyBookedException() {
        super();
    }

}
