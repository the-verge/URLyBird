package suncertify.db;

import java.io.IOException;

/**
 * Objects of the <code>Data</code> class are used to create, read, update and delete
 * room records.  It uses the Facade design pattern to delegate database access
 * and record locking responsibilities to the DBAccessor and LockManager classes
 * respectively.
 * 
 * @author John Harding
 */
public class Data implements CloseableDB {
    
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
     * The constructor takes the file path of the database as its only argument.
     * @param dbLocation the path to the database file.
     * @throws DBException if the <code>DBAccessor</code> instance 
     * 			<code>database</code> cannot be instantiated. 
     */
    public Data(final String dbLocation) {
        database = new DBAccessor(dbLocation);
    }
    
    /**
     * {@inheritDoc}
     * @throws DBException if the read operation fails.
     */
    @Override
    public String[] read(int recNo) throws RecordNotFoundException {
        return database.readRecord(recNo);
    }
    
    /**
     * {@inheritDoc}
     * @throws DBException if the update operation fails.
     */
    @Override
    public void update(int recNo, String[] data, long lockCookie)
            throws RecordNotFoundException, SecurityException {
        
        if (isValidCookie(recNo, lockCookie)) {
            database.updateRecord(recNo, data);
        }
        else {
            throw new SecurityException("Attempt to update record " 
                    + recNo + " with invalid lockCookie");
        }
    }
    
    /**
     * {@inheritDoc}
     * @throws DBException if the delete operation fails.
     */
    @Override
    public void delete(int recNo, long lockCookie)
            throws RecordNotFoundException, SecurityException {
        
        if (!database.recordExists(recNo)) {
            throw new RecordNotFoundException("Record number " + recNo + " does not exist");
        }
        
        if (isValidCookie(recNo, lockCookie)) {
            database.deleteRecord(recNo); 
        }
        else {
            throw new SecurityException("Attempt to delete record " 
                    + recNo + " with invalid lockCookie");
        }
    }
    
    /**
     * {@inheritDoc}
     * @throws DBException if the find operation fails.
     */
    @Override
    public int[] find(String[] criteria) {
        return database.find(criteria);
    }
    
    /**
     * {@inheritDoc}
     * @throws DBException if the create operation fails.
     */
    @Override
    public int create(String[] data) throws DuplicateKeyException {
        return database.createRecord(data);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public long lock(int recNo) throws RecordNotFoundException {
        if (!database.recordExists(recNo)) {
            throw new RecordNotFoundException("Record number " + recNo + " does not exist");
        }
        return lockManager.lockRecord(recNo);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void unlock(int recNo, long cookie) throws RecordNotFoundException,
            SecurityException {
        
        if (isValidCookie(recNo, cookie)) {
            lockManager.unlockRecord(recNo, cookie);
        }
        else {
            throw new SecurityException("Attempt to unlock record " 
                    + recNo + " with invalid lockCookie");
        }
    }
    
    private boolean isValidCookie(int recNo, long lockCookie) {
        Long cookie = lockManager.getLockMap().get(recNo);
        
        if (cookie != null && cookie == lockCookie) {
            return true;
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void closeDatabaseConnection() throws IOException {
        database.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasLocalDatabaseConnection() {
        return true;
    }

}
