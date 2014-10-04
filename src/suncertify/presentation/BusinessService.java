package suncertify.presentation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observer;

import suncertify.db.DB;
import suncertify.db.DBException;
import suncertify.db.DuplicateKeyException;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;
import suncertify.network.NetworkException;

/**
 * 
 * @author john
 *
 */
public class BusinessService {
	
	private Observer observer;
	
	private DB dataAccess;
	
	public BusinessService(DB dataAccess) {
		this.dataAccess = dataAccess;
	}
	
	public void addObserver(Observer observer) {
		this.observer = observer;
	}
	
	public void fireModelChangeEvent() {
	    // INVESTIGATE
		observer.update(null, null);
	}
	
	public Map<Integer, Room> searchRooms(SearchCriteria criteria) {
        
        String[] searchCriteria = criteria.getCriteria();
        int[] matchingRecordNumbers = dataAccess.find(searchCriteria);
        Map<Integer, Room> roomMap = new LinkedHashMap<Integer, Room>();
        
        for (int i = 0; i < matchingRecordNumbers.length; i++) {
            int recNo = matchingRecordNumbers[i];
            String[] data = null;
            try {
                data = dataAccess.read(recNo);
                Room room = new Room(recNo, data);
                roomMap.put(i, room);
            } catch (RecordNotFoundException e) {
                /*
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
	
	public void bookRoom(Room room) throws RecordNotFoundException, SecurityException {
	    int recNo = room.getRecNo();
	    String[] data = room.getData();
	    
	    if (alreadyBooked(recNo)) {
	    	throw new RecordNotFoundException();
	    }
	    else {
	        long lockCookie = dataAccess.lock(recNo);
	        
	        /**
	         * Exceptions are caught and re-thrown here
	         * to convey to a user that the booking was 
	         * unsuccessful.  Given that both the update
	         * and unlock methods potentially throw the
	         * same exceptions, placing both methods in
	         * the same try-catch block would not allow
	         * us to make a distinction between failure
	         * in making the booking and failure
	         * in unlocking the record after a successful
	         * update operation.
	         */
            try {
                dataAccess.update(recNo, data, lockCookie);
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
	    }
		fireModelChangeEvent();
	}
	
	private boolean alreadyBooked(int recNo) throws RecordNotFoundException {
		String[] roomData = dataAccess.read(recNo);
		String owner = roomData[6];
		if (owner.equals("")) {
			return false;
		}
		return true;
	}
	
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
        
        fireModelChangeEvent();
    }
	
	public void createRoom(String[] data) throws DuplicateKeyException {
        dataAccess.create(data);
    }
	
	
}
