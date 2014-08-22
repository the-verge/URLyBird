package suncertify.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import suncertify.db.DBException;
import suncertify.db.Data;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

public class BusinessModel {
	
	private static List<Observer> observers = new ArrayList<Observer>();
	
	private static List<String[]> allRecords = new ArrayList<String[]>();
	
	private Data dataAccess;
	
	protected BusinessModel() {
		try {
			this.dataAccess = new Data("/home/ejhnhng/URLyBird/db-1x3.db");
			allRecords = dataAccess.findAll();
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
	
	public void book(int recNo, String[] data) throws RecordNotFoundException, SecurityException {
		long lockCookie = dataAccess.lock(recNo);
		dataAccess.update(recNo, data, lockCookie);
		dataAccess.unlock(recNo, lockCookie);
	}
	
	public List<String[]> search(String[] criteria) {
		int[] matchingRecordNumbers = dataAccess.find(criteria);
		List<String[]> matches = new ArrayList<String[]>();
		for (int i = 0; i < matchingRecordNumbers.length; i++) {
			int recordNumber = matchingRecordNumbers[i];
			int correspondingListElement = recordNumber - 1;
			matches.add(allRecords.get(correspondingListElement));
		}
		return matches;
	}
	
	public ArrayList<String[]> findAll() {
		return dataAccess.findAll();
	}
}
