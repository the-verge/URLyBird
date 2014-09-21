package suncertify.db;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class LockManagerWithTimeout implements RecordLocker {
    
    /**
     * Maps the numbers of records that are locked to
     * the cookie that they are locked with.
     */
    private static final Map<Integer, Long> LOCKMAP
            = new HashMap<Integer, Long>();
    
    private static Lock lock = new ReentrantLock();
    
    private static Condition lockReleased = lock.newCondition();
    /**
     * The number of cookies that have been generated.
     */
    private static long cookieCount = 0L;

    @Override
    public long lockRecord(int recNo) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void unlockRecord(int recNo, long cookie) throws SecurityException {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public Map<Integer, Long> getLockMap() {
        return LOCKMAP;
    }
    
    /**
     * Generates a lock cookie.
     * @return lock cookie.
     */
    private long generateCookie() {
        cookieCount += 1;
        return System.currentTimeMillis() + cookieCount;
    }

}
