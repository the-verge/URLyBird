package suncertify.network;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import suncertify.db.DuplicateKeyException;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

/**
 * The <code>DataRemoteAdapter</code> interface is identical to the
 * supplied DB.java interface except that each method also throws
 * <code>RemoteException</code>.  This will enable a class that implements
 * this interface to wrap an instance of the <code>db.Data</code>
 * class and provide identical functionality remotely without having to
 * alter <code>Data</code> or any of its member classes.
 * @see suncertify.network.DataRemoteAdapterImpl
 *
 * @author John Harding
 */
public interface DataRemoteAdapter extends Remote {
    
	/**
     * Reads a record. Returns an array where each
     * element is a record value.
     * 
     * @param recNo the unique identifier of the record to read.
     * @return a <code>String</code> array representing the fields of the record.
     * @throws RecordNotFoundException if the record does not exist
     * 		   in the database.
     * @throws RemoteException if a networking error occurs.
     */
    public String[] read(int recNo) throws RecordNotFoundException, RemoteException;
    
    /**
     * Modifies the fields of a record. The new value for field n 
     * appears in data[n].
     * 
     * @param recNo the unique identifier of the record to update.
     * @param data a <code>String</code> array representing the fields of the record
     * 		  to be updated.
     * @param lockCookie the cookie that the record was locked with.
     * @throws RecordNotFoundException if the record does not exist
     * 		   in the database.
     * @throws SecurityException if the record is locked with a cookie
     * 		   other than lockCookie.
     * @throws RemoteException if a networking error occurs.
     */
    public void update(int recNo, String[] data, long lockCookie)
      throws RecordNotFoundException, SecurityException, RemoteException;
    
    /**
     * Deletes a record, making the record number and associated disk
     * storage available for reuse. 
     * 
     * @param recNo the unique identifier of the record to delete.
     * @param lockCookie the cookie that the record was locked with.
     * @throws RecordNotFoundException if the record does not exist
     * 		   in the database.
     * @throws SecurityException if the record is locked with a cookie
     * 		   other than lockCookie.
     * @throws RemoteException if a networking error occurs.
     */
    public void delete(int recNo, long lockCookie)
      throws RecordNotFoundException, SecurityException, RemoteException;
    
    /**
     * Returns an array of record numbers that match the specified
     * criteria. Field n in the database file is described by
     * criteria[n]. ***A null value in criteria[n] matches any field
     * value***. A non-null  value in criteria[n] matches any field
     * value that begins with criteria[n]. (For example, "Fred"
     * matches "Fred" or "Freddy".)
    
     * @param criteria a String array representing search criteria.
     * @return an array of record numbers that match the criteria.
     * @throws RemoteException if a networking error occurs.
     */
    public int[] find(String[] criteria) throws RemoteException;
    
    /**
     * Creates a new record in the database (possibly reusing a
     * deleted entry). Inserts the given data, and returns the record
     * number of the new record.
     * 
     * @param data String array representing the fields of the record.
     * @return the unique identifier of the new record.
     * @throws DuplicateKeyException if a record with the same key
     * 		   already exists in the database.
     * @throws RemoteException if a networking error occurs.
     */
    public int create(String[] data) throws DuplicateKeyException, RemoteException;
    
    /**
     * Locks a record so that it can only be updated or deleted by this client.
     * Returned value is a cookie that must be used when the record is unlocked,
     * updated, or deleted. If the specified record is already locked by a 
     * different client, the current thread gives up the CPU and consumes no 
     * CPU cycles until the record is unlocked.
     * 
     * @param recNo the unique identifier of the record to lock.
     * @return long cookie a unique token used to identify the owner
     * 		   of the lock on the record.
     * @throws RecordNotFoundException if the record does not exist 
     * 		   in the database or an <code>InterruptedException</code>
     * 		   occurs when trying to lock the record.
     * @throws RemoteException if a networking error occurs.
     */
    public long lock(int recNo) throws RecordNotFoundException, RemoteException;
    
    /**
     * Releases the lock on a record. Cookie must be the cookie
     * returned when the record was locked: otherwise a SecurityException
     * is thrown.
     * 
     * @param recNo the unique identifier of the record to unlock.
     * @param cookie the cookie that the record was locked with.	
     * @throws RecordNotFoundException if the record does not exist
     * 		   in the database.
     * @throws SecurityException if the cookie parameter is not
     * 		   the cookie returned when the record was locked.
     * @throws RemoteException if a networking error occurs.
     */
    public void unlock(int recNo, long cookie)
      throws RecordNotFoundException, SecurityException, RemoteException;
    
    /**
     * Closes the database connection.
     * 
     * @throws IOException if the connection
     *         cannot be closed.
     */
    public void closeDatabaseConnection() throws IOException;

}
