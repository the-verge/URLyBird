package suncertify.gui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;

import suncertify.db.DBException;
import suncertify.db.Data;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

public class BusinessModel {
	
	private static List<Observer> observers = new ArrayList<Observer>();
	
	private Data dataAccess;
	
	protected BusinessModel() {
		try {
			this.dataAccess = new Data("/Users/john/workspace/urlybird/db-1x3.db");
		} catch (DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void addObserver(Observer observer) {
		observers.add(observer);
	}
	
	public void fireModelChangeEvent() {
		for (Observer observer: observers) {
			// change params after
			observer.update(null, null);
		}
	}
	
	public void book(Room room) throws RecordNotFoundException {
	    int recNo = room.getRecNo();
	    String[] data = room.getData();
	    
		try {
            long lockCookie = dataAccess.lock(recNo);
            dataAccess.update(recNo, data, lockCookie);
            dataAccess.unlock(recNo, lockCookie);
        } catch (RecordNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		fireModelChangeEvent();
	}
	
	public Map<Integer, Room> searchRooms(SearchCriteria criteria) throws RecordNotFoundException {
	    
        String[] searchCriteria = criteria.getCriteria();
        int[] matchingRecordNumbers = dataAccess.find(searchCriteria);
        Map<Integer, Room> roomMap = new LinkedHashMap<Integer, Room>();
        
        for (int i = 0; i < matchingRecordNumbers.length; i++) {
            int recNo = matchingRecordNumbers[i];
            String[] data = dataAccess.read(recNo);
            Room room = new Room(recNo, data);
            roomMap.put(i, room);
        }
        
        return roomMap;
    }
	
	public ArrayList<String[]> findAll() {
		return dataAccess.findAll();
	}
}
