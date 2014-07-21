package suncertify.db;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Objects of the Data class are used to create, read, update and delete
 * room records.  It uses the Facade design pattern to delegate database access
 * and record locking responsibilities to the DBAccessor and LockManager classes
 * respectively.
 * 
 * @author 
 * 
 */
public class Data implements DB {
    
    /**
     * The static LockManager instance takes care of record 
     * locking and unlocking.
     */
    private static LockManager lockManager = new LockManager();
    
    /**
     * The static DBAccessor instance manages access to the database.
     */
    private static DBAccessor database;
    
    /**
     * Logger instance for Data.java.
     */
    private Logger log = Logger.getLogger("Data.java");
    
    /**
     * The constructor takes the file path of the database as its only argument.
     * @param dbLocation
     */
    public Data(final String dbLocation) {
        database = new DBAccessor(dbLocation);
    }

    @Override
    public String[] read(int recNo) throws RecordNotFoundException {
        return database.readRecord(recNo);
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
        return lockManager.lockRecord(recNo);
    }

    @Override
    public void unlock(int recNo, long cookie) throws RecordNotFoundException,
            SecurityException {
        lockManager.unlockRecord(recNo, cookie);
    }

}
