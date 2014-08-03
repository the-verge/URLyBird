package suncertify.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

import suncertify.db.DB;
import suncertify.db.Data;
import suncertify.db.DuplicateKeyException;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

public class DataRemoteImpl extends UnicastRemoteObject implements DataRemote {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1771L;
    
    private DB database;
    
    private transient Logger log = Logger.getLogger("DataRemoteImpl.java");

    public DataRemoteImpl(String dbLocation) throws RemoteException {
        database = new Data(dbLocation);
    }

    @Override
    public String[] read(int recNo) throws RecordNotFoundException {
        return database.read(recNo);
    }

    @Override
    public void update(int recNo, String[] data, long lockCookie)
            throws RecordNotFoundException, SecurityException {
        
        database.update(recNo, data, lockCookie);
    }

    @Override
    public void delete(int recNo, long lockCookie)
            throws RecordNotFoundException, SecurityException {
        
        database.delete(recNo, lockCookie);
    }

    @Override
    public int[] find(String[] criteria) {
        return database.find(criteria);
    }

    @Override
    public int create(String[] data) throws DuplicateKeyException {
        return database.create(data);
    }

    @Override
    public long lock(int recNo) throws RecordNotFoundException {
        return database.lock(recNo);
    }

    @Override
    public void unlock(int recNo, long cookie) throws RecordNotFoundException,
            SecurityException {
        
        database.unlock(recNo, cookie);
    }

}
