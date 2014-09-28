package suncertify.db;


public interface RecordLocker {
    
    long lockRecord(int recNo);
    
    void unlockRecord(int recNo, long cookie) throws SecurityException;

}

