package suncertify.db;

import java.util.Map;

import suncertify.db.SecurityException;

public interface RecordLocker {
    
    long lockRecord(int recNo);
    
    void unlockRecord(int recNo, long cookie) throws SecurityException;

    Map<Integer, Long> getLockMap();

}

