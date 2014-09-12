package suncertify.network;

import java.rmi.RemoteException;

import suncertify.db.DB;
import suncertify.db.DuplicateKeyException;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

public class DataProxy implements DB {
	
	private DataRemoteAdapter database;
	
	public DataProxy(DataRemoteAdapter database) {
		this.database = database;
	}
	
	// WHAT ABOUT IOExceptions / DBExceptions ?
	@Override
	public String[] read(int recNo) throws RecordNotFoundException {
		String[] result = null;
		try {
			result = database.read(recNo);
		} catch (RemoteException e) {
			throw new NetworkException("Network error", e);
		}
		return result;
	}

	@Override
	public void update(int recNo, String[] data, long lockCookie)
			throws RecordNotFoundException, SecurityException {
		
		try {
			database.update(recNo, data, lockCookie);
		} catch (RemoteException e) {
			throw new NetworkException("Network error", e);
		}
	}

	@Override
	public void delete(int recNo, long lockCookie)
			throws RecordNotFoundException, SecurityException {
		try {
			database.delete(recNo, lockCookie);
		} catch (RemoteException e) {
			throw new NetworkException("Network error", e);
		}
	}

	@Override
	public int[] find(String[] criteria) {
		int[] result = null;
		try {
			result = database.find(criteria);
		} catch (RemoteException e) {
			throw new NetworkException("Network error", e);
		}
		return result;
	}

	@Override
	public int create(String[] data) throws DuplicateKeyException {
		int recNo = 0;
		try {
			recNo = database.create(data);
		} catch (RemoteException e) {
			throw new NetworkException("Network error", e);
		}
		return recNo;
	}

	@Override
	public long lock(int recNo) throws RecordNotFoundException {
		long lockCookie = 1;
		try {
			lockCookie = database.lock(recNo);
		} catch (RemoteException e) {
			throw new NetworkException("Network error", e);
		} 
		return lockCookie;
	}

	@Override
	public void unlock(int recNo, long cookie) throws RecordNotFoundException,
			SecurityException {
		
		try {
			database.unlock(recNo, cookie);
		} catch (RemoteException e) {
			throw new NetworkException("Network error", e);
		}
	}
	
}
