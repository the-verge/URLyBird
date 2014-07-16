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
     * The location of the database that will be used by all
     * instances of the Data class.  The first object created from
     * the Data class will initialise this variable, after which 
     * it will not change.
     */
    private static String databaseLocation;
    
    /**
     * Logger instance for Data.java.
     */
    private Logger log = Logger.getLogger("Data.java");
    
    /**
     * The constructor takes the file path of the database as its only argument.
     * @param dbLocation
     */
    public Data(final String dbLocation) {
        if (databaseLocation == null) {
            databaseLocation = dbLocation;
        }
        else if (dbLocation != databaseLocation) {
            log.logp(Level.WARNING, "Data.java", "Constructor", 
                    "Ignored database file path "
                  + "- database location already initialised");;
        }
        database = new DBAccessor(databaseLocation);
    }

    @Override
    public String[] read(int recNo) throws RecordNotFoundException {
        return database.readRecord(recNo);
    }

    @Override
    public void update(int recNo, String[] data, long lockCookie)
            throws RecordNotFoundException, SecurityException {
        database.updateRecord(recNo, data, lockCookie);
    }

    @Override
    public void delete(int recNo, long lockCookie)
            throws RecordNotFoundException, SecurityException {
        database.deleteRecord(recNo, lockCookie);
    }

    @Override
    public int[] find(String[] criteria) {
        return database.findRecords(criteria);
    }

    @Override
    public int create(String[] data) throws DuplicateKeyException {
        return database.createRecord(data);
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
