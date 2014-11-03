package suncertify.network;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import suncertify.db.CloseableDB;
import suncertify.db.DBException;
import suncertify.db.Data;
import suncertify.db.DuplicateKeyException;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

/**
 * <code>DataRemoteAdapterImpl</code> wraps an instance of 
 * <code>db.Data</code> in order to use it as a remote object.
 * In essence it is an implementation of the Adapter design pattern.
 *
 * @author John Harding
 */
public class DataRemoteAdapterImpl extends UnicastRemoteObject 
		implements DataRemoteAdapter {
    
    /**
     * The SUID.
     */
    private static final long serialVersionUID = 1771L;
    
    /**
     * The <code>DB</code> instance that this class wraps.
     */
    private CloseableDB database;
    
    /**
     * Class constructor.
     * @param dbLocation the path to the database file.
     * @throws RemoteException if a network error occurs.
     * @throws DBException if the <code>DB</code> instance 
     * 			<code>database</code> cannot be instantiated. 
     */
    public DataRemoteAdapterImpl(String dbLocation) throws RemoteException {
        database = new Data(dbLocation);
    }
    
    /**
     * {@inheritDoc}
     * @throws DBException if the read operation fails.
     */
    @Override
    public String[] read(int recNo) throws RecordNotFoundException,
            RemoteException {
    	
        return database.read(recNo);
    }
    
    /**
     * {@inheritDoc}
     * @throws DBException if the update operation fails.
     */
    @Override
    public void update(int recNo, String[] data, long lockCookie)
            throws RecordNotFoundException, SecurityException, RemoteException {
        
        database.update(recNo, data, lockCookie);
    }
    
    /**
     * {@inheritDoc}
     * @throws DBException if the delete operation fails.
     */
    @Override
    public void delete(int recNo, long lockCookie)
            throws RecordNotFoundException, SecurityException, RemoteException {
        
        database.delete(recNo, lockCookie);
    }
    
    /**
     * {@inheritDoc}
     * @throws DBException if the find operation fails.
     */
    @Override
    public int[] find(String[] criteria) throws RemoteException {
        return database.find(criteria);
    }
    
    /**
     * {@inheritDoc}
     * @throws DBException if the create operation fails.
     */
    @Override
    public int create(String[] data) throws DuplicateKeyException,
            RemoteException {
        
        return database.create(data);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public long lock(int recNo) throws RecordNotFoundException, RemoteException {
        return database.lock(recNo);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void unlock(int recNo, long cookie) throws RecordNotFoundException,
            SecurityException, RemoteException {
        
        database.unlock(recNo, cookie);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void closeDatabaseConnection() throws IOException {
        database.closeDatabaseConnection();
    }

    

}
