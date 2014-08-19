package suncertify.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import suncertify.db.Data;

public class BookingModel {
	
	private static List<Observer> observers = new ArrayList<Observer>();
	
	private static List<String[]> allRecords = new ArrayList<String[]>();
	
	private Data dataAccess;
	
	protected BookingModel() {
		this.dataAccess = new Data("/home/ejhnhng/URLyBird/db-1x3.db");
		try {
			allRecords = dataAccess.findAll();
		} catch (IOException e) {
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
	
	public List<String[]> search(String[] criteria) {
		int[] recordNumbers = dataAccess.find(criteria);
		List<String[]> matches = new ArrayList<String[]>();
		for (int i = 0; i < recordNumbers.length; i++) {
			matches.add(allRecords.get(recordNumbers[i] - 1));
		}
		return matches;
	}
	
	public ArrayList<String[]> findAll() throws IOException {
		return dataAccess.findAll();
	}
}
