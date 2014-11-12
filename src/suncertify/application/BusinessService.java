package suncertify.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import suncertify.db.CloseableDB;
import suncertify.db.DBException;
import suncertify.db.DuplicateKeyException;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;
import suncertify.network.NetworkException;

/**
 * The <code>BusinessService</code> class is a utility
 * class used by clients.  It accesses the database via 
 * a <code>CloseableDB</code> instance, which may provide
 * a local or remote database connection.
 * 
 * @author John Harding
 */
public class BusinessService extends Observable {
    
    /**
     * Index of the owner field in a database record.
     */
    private static final int OWNER_INDEX = 6;
	
    /**
     * An <code>Observer</code> instance.
     */
	private List<Observer> observers = new ArrayList<Observer>();
	
	/**
	 * Provides database connectivity.
	 */
	private CloseableDB dataAccess;
	
	/**
	 * Class constructor.
	 * @param dataAccess a <code>CloseableDB</code>
	 *        instance that provides database connectivity.
	 */
	public BusinessService(CloseableDB dataAccess) {
		this.dataAccess = dataAccess;
	}
	
	/**
	* Adds an <code>Observer</code> to the <code>observers</code> list.
	* @param o an <code>Observer</code> instance.
	*/
	public void addObserver(Observer o) {
	    observers.add(o);
	}
	
	/**
     * Informs observers that data has changed.
     */
    public void fireDataChangeEvent() {
        for (Observer o: observers) {
            if (dataAccess.hasLocalDatabaseConnection()) {
                o.update(this, ApplicationMode.STANDALONE_CLIENT);
            }
            else {
                o.update(this, ApplicationMode.NETWORK_CLIENT);
            }
        }
    }
	
	/**
	 * Searches for data matching supplied criteria.
	 * @param criteria <code>SearchCriteria</code> instance.
	 * @return Map which is used to map match[i]
	 *         to a database record number.
	 * @throws DBException if a database failure occurs.
	 * @throws NetworkException if the <code>CloseableDB</code>
	 *         instance provides a remote database connection
	 *         and a network error occurs.
	 */
	public Map<Integer, Room> searchRooms(SearchCriteria criteria) {
        
        String[] searchCriteria = criteria.getCriteria();
        int[] matchingRecordNumbers = dataAccess.find(searchCriteria);
        Map<Integer, Room> roomMap = new LinkedHashMap<Integer, Room>();
        int tableIndex = 0;

        for (int recNo: matchingRecordNumbers) {
            try {
                String[] data = dataAccess.read(recNo);
                Room room = new Room(recNo, data);
                roomMap.put(tableIndex, room);
                tableIndex++;
            }
            catch (RecordNotFoundException e) {
                /**
                 * RecordNotFoundException will be thrown by the DB.read(int recNo)
                 * method if the record is marked as deleted. It is possible that
                 * another client that uses Data.java has deleted a record number
                 * that matched the search criteria before this method invokes the
                 * DB.read(int recNo) method.  We don't want to display deleted
                 * records, so the exception is caught here and the loop continues.
                 * Only non deleted records will be passed to the Room constructor.
                 */
                continue;
            }
        }

        return roomMap;
    }   
	
	/**
	 * Books a room.
	 * @param room <code>Room</code> object encapsulating the data of the
	 *        record to be updated.
	 * @throws RecordNotFoundException if the requested room has already
	 *         been booked.
	 * @throws SecurityException if an attempt is made to book a 
	 *         record with an invalid lock cookie.
	 * @throws DBException if a database failure occurs.
     * @throws NetworkException if the <code>CloseableDB</code>
     *         instance provides a remote database connection
     *         and a network error occurs.
     * @throws RoomAlreadyBookedException if the room is already booked.
	 */
	public void bookRoom(Room room, String customerId) throws RecordNotFoundException,
            SecurityException, RoomAlreadyBookedException {

	    int recNo = room.getRecNo();
        long lockCookie = 0;

        try {
            lockCookie = dataAccess.lock(recNo);
            if (alreadyBooked(recNo)) {
                fireDataChangeEvent();
                throw new RoomAlreadyBookedException();
            }
            room.setOwner(customerId);
            String[] data = room.getData();
            dataAccess.update(recNo, data, lockCookie);
            fireDataChangeEvent();
        } finally {
            try {
                dataAccess.unlock(recNo, lockCookie);
            } catch (Exception e) {
                /**
                 * Nothing of use can be conveyed to the user
                 * if an Exception is thrown while trying to
                 * unlock a record, so Exceptions are propagated
                 * no further.
                 */
            }
        }

	}
	
	/**
	 * Checks if a given record is already booked.
	 * @param recNo the record number to check.
	 * @return boolean.
	 * @throws RecordNotFoundException if the record
     *         does not exist in the database.
     * @throws DBException if a database failure occurs.
     * @throws NetworkException if the <code>CloseableDB</code>
     *         instance provides a remote database connection
     *         and a network error occurs.
     */
	private boolean alreadyBooked(int recNo) throws RecordNotFoundException {
		String[] roomData = dataAccess.read(recNo);
		String owner = roomData[OWNER_INDEX];
		if (owner.equals("")) {
			return false;
		}
		return true;
	}
	
	/**
	 * Deletes a room.
	 * @param room a <code>Room</code> object encapsulating the data of the
	 *        record to delete.
	 * @throws RecordNotFoundException if the record does not exist.
	 *         (It could potentially have been deleted by another client).
	 * @throws SecurityException if an attempt is made to delete a record 
	 *         with an invalid lock cookie.
     * @throws DBException if a database failure occurs.
     * @throws NetworkException if the <code>CloseableDB</code>
     *         instance provides a remote database connection
     *         and a network error occurs.
	 */
	public void deleteRoom(Room room) throws RecordNotFoundException, SecurityException {
        int recNo = room.getRecNo();
        
        long lockCookie = dataAccess.lock(recNo);
        
        /**
         * Exceptions are caught and re-thrown here
         * to convey to a user that the booking was 
         * unsuccessful.  Given that both the delete
         * and unlock methods potentially throw the
         * same exceptions, placing both methods in
         * the same try-catch block would not allow
         * us to make a distinction between failure
         * in deleting the record and failure
         * in unlocking the record after a successful
         * delete operation.
         */
        try {
            dataAccess.delete(recNo, lockCookie);
        } catch (SecurityException e) {
            throw e;
        } catch (DBException e) {
            throw e;
        } catch (NetworkException e) {
            throw e;
        }
        
        try {
            dataAccess.unlock(recNo, lockCookie);
        } catch (Exception e) {
            /**
             * Nothing of use can be conveyed to the
             * user if for some reason an Exception
             * is thrown when unlocking a record. As
             * such the exception is handled here by
             * not doing anything.
             */
        }
        
        fireDataChangeEvent();
    }
	
	/**
	 * Creates a room.
	 * @param data the fields of the record to be created.
	 * @throws DuplicateKeyException
	 */
	public void createRoom(String[] data) {
        try {
            dataAccess.create(data);
        } catch (DuplicateKeyException e) {
            /**
             * DuplicateKeyException will never be thrown.
             */
        }
    }
	
	/**
	 * Closes local database connections.
	 */
	public void cleanUp() {
	    try {
	        boolean shouldClose = dataAccess.hasLocalDatabaseConnection();
	        if (shouldClose) {
	            dataAccess.closeDatabaseConnection();
	        }
        } catch (IOException e) {
            /**
             * Nothing of use can be conveyed to the user
             * if closing the database connection fails,
             * so this exception is propagated no further.
             */
        }
	}
}
