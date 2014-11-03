package suncertify.network;

import java.io.IOException;
import java.rmi.RemoteException;

import suncertify.db.CloseableDB;
import suncertify.db.DBException;
import suncertify.db.DuplicateKeyException;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

/**
 * <code>DataProxy</code> adapts an instance of 
 * <code>DataRemoteAdapterImpl</code>.  It implements
 * the <code>CloseableDB</code> interface.  This adaption allows
 * the GUI layer to accept an object that provides a remote
 * database connection that conforms to the <code>CloseableDB</code>
 * (and by extension the <code>DB</code>) interface. The GUI layer 
 * is therefore unconcerned about whether the database 
 * connection is local, or provided by a remote database server
 * application.
 *
 * @author John Harding
 */
public class DataProxy implements CloseableDB {
	
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
	 * @throws DBException if a database failure occurs.
	 * @throws NetworkException if a network error occurs.
	 */
	@Override
	public String[] read(int recNo) throws RecordNotFoundException {
		String[] result;
		try {
			result = database.read(recNo);
		} catch (RemoteException e) {
			throw new NetworkException("Network error", e);
		}
		return result;
	}
	
	/**
	 * {@inheritDoc}
     * @throws DBException if a database failure occurs.
     * @throws NetworkException if a network error occurs.
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
     * @throws DBException if a database failure occurs.
     * @throws NetworkException if a network error occurs.
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
     * @throws DBException if a database failure occurs.
     * @throws NetworkException if a network error occurs.
	 */
	@Override
	public int[] find(String[] criteria) {
		int[] result;
		try {
			result = database.find(criteria);
		} catch (RemoteException e) {
			throw new NetworkException("Network error", e);
		}
		return result;
	}
	
	/**
	 * {@inheritDoc}
     * @throws DBException if a database failure occurs.
     * @throws NetworkException if a network error occurs.
	 */
	@Override
	public int create(String[] data) throws DuplicateKeyException {
		int recNo;
		try {
			recNo = database.create(data);
		} catch (RemoteException e) {
			throw new NetworkException("Network error", e);
		}
		return recNo;
	}
	
	/**
	 * {@inheritDoc}
	 * @throws NetworkException if a network error occurs.
	 */
	@Override
	public long lock(int recNo) throws RecordNotFoundException {
		long lockCookie;
		try {
			lockCookie = database.lock(recNo);
		} catch (RemoteException e) {
			throw new NetworkException("Network error", e);
		} 
		return lockCookie;
	}
	
	/**
	 * {@inheritDoc}
	 * @throws NetworkException if a network error occurs.
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

    @Override
    public void closeDatabaseConnection() throws IOException {
        database.closeDatabaseConnection();
    }

    @Override
    public boolean hasLocalDatabaseConnection() {
        return false;
    }

	
}
