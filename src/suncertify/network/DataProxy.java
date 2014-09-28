package suncertify.network;

import java.rmi.RemoteException;

import suncertify.db.DB;
import suncertify.db.DBException;
import suncertify.db.DuplicateKeyException;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

/**
 * <code>DataProxy</code> adapts an instance of 
 * <code>DataRemoteAdapterImpl</code>.  It implements
 * the <code>DB</code> interface.  This adaption allows
 * the GUI layer to accept a remote object of type <code>DB</code>.
 * The GUI layer is therefore not concerned about whether
 * the database connection is local, or provided by a
 * remote database server application.
 * 
 * @author john
 *
 */
public class DataProxy implements DB {
	
    /**
     * The <code>DataRemoteAdapter</code> instance that 
     * this class adapts.
     */
	private DataRemoteAdapter database;
	
	/**
	 * Class constructor.
	 * @param database the <code>DataRemoteAdapter</code>
	 *        that this class adapts.
	 */
	public DataProxy(DataRemoteAdapter database) {
		this.database = database;
	}
	
	/**
	 * {@inheritDoc}
	 * @throws DBException if the read operation fails.
	 */
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
	
	/**
	 * {@inheritDoc}
	 * @throws DBException if the update operation fails.
	 */
	@Override
	public void update(int recNo, String[] data, long lockCookie)
			throws RecordNotFoundException, SecurityException {
		
		try {
			database.update(recNo, data, lockCookie);
		} catch (RemoteException e) {
			throw new NetworkException("Network error", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @throws DBException if the delete operation fails.
	 */
	@Override
	public void delete(int recNo, long lockCookie)
			throws RecordNotFoundException, SecurityException {
		try {
			database.delete(recNo, lockCookie);
		} catch (RemoteException e) {
			throw new NetworkException("Network error", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @throws DBException if the find operation fails.
	 */
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
	
	/**
	 * {@inheritDoc}
	 * @throws DBException if the create operation fails.
	 */
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
	
	/**
	 * {@inheritDoc}
	 */
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
	
	/**
	 * {@inheritDoc}
	 */
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
