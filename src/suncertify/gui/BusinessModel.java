package suncertify.gui;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observer;

import suncertify.db.DB;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

/**
 * 
 * @author john
 *
 */
public class BusinessModel {
	
	private Observer observer;
	
	private DB dataAccess;
	
	public BusinessModel(DB dataAccess) {
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
	
	public void book(Room room) throws RecordNotFoundException, SecurityException {
	    int recNo = room.getRecNo();
	    String[] data = room.getData();
	    
	    if (alreadyBooked(recNo)) {
	    	throw new RecordNotFoundException();
	    }
	    else {
	        long lockCookie = dataAccess.lock(recNo);
            try {
                dataAccess.update(recNo, data, lockCookie);
            } catch (SecurityException e) {
                throw new SecurityException();
            }
            try {
                dataAccess.unlock(recNo, lockCookie);
            } catch (SecurityException e) {
                /**
                 * Nothing of use can be displayed to the
                 * client if for some reason a SecurityException
                 * is thrown when unlocking a record.  Given the 
                 * locking mechanism that is in place, 
                 * SecurityException should never be thrown,
                 * but it is handled here by not doing anything.
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
	
	
}
