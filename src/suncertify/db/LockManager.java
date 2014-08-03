package suncertify.db;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import suncertify.db.SecurityException;

public class LockManager {
    
    private static final String mutex = "MUTEX";
    
    private static final Map<Integer, Long> lockMap = new HashMap<Integer, Long>();
    
    private static long cookieCount = 0L;
    
    private final Logger log = Logger.getLogger("LockManager.java");
    
    public long lockRecord(int recNo) {
        String threadName = Thread.currentThread().getName();
        log.entering("LockManager.java", "lockRecord", new Object[]{threadName, recNo});
        
        synchronized (mutex) {
            while(lockMap.containsKey(recNo)) {
                System.out.println(threadName + ": Record number " + recNo + " is locked.  waiting...");
                try {
                    mutex.wait();
                } catch (InterruptedException e) {
                    log.throwing("LockManager.java", "lockRecord", e);
                    // NEED TO CLARIFY
                    Thread.currentThread().interrupt();
                }
            }
            long cookie = generateCookie();
            log.fine("Generated cookie for recNo "  + recNo + ": thread: " + threadName + " cookie: " + cookie);
            lockMap.put(recNo, cookie);
            log.fine("Total number of locks: " + lockMap.size());
            System.out.println(threadName + ": Locked record number " + recNo);
            log.exiting("LockManger.java", "lockRecord", new Object[]{threadName, recNo, cookie});
            return cookie;
        }
    }

    public void unlockRecord(int recNo, long cookie) throws SecurityException {
        String threadName = Thread.currentThread().getName();
        
        log.entering("LockManager.java", "unlockRecord", new Object[]{threadName, recNo, cookie});
        synchronized (mutex) {
            if (lockMap.get(recNo) == cookie) {
                System.out.println(threadName + ": Unlocking record number " + recNo);
                lockMap.remove(recNo);
                mutex.notifyAll();
                System.out.println(threadName + ": Notifying threads that record number " + recNo + " is unlocked");
            }
            else {
                log.warning("An illegal attempt was made to unlock record number " + recNo);
                throw new SecurityException();
            }
        }
    }
    
    private long generateCookie() {
        cookieCount += 1;
        return System.currentTimeMillis() + cookieCount;
    }
    
    public Map<Integer, Long> getLockMap() {
        return lockMap;
    }

}
