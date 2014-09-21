package suncertify.db;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import suncertify.db.SecurityException;

/**
 * The LockManager class logically locks records.
 * This is necessary when an attempt is made to
 * update or delete a record to ensure a record
 * is being edited by only one thread at a time.
 * 
 * @author john
 *
 */
public class LockManager implements RecordLocker {
    
    /**
     * Mutex object used to synchronize on the 
     * HashMap that maps locked record numbers
     * to the cookie they are locked with.
     */
    private static final String MUTEX = "MUTEX";
    
    /**
     * Maps the numbers of records that are locked to
     * the cookie that they are locked with.
     */
    private static final Map<Integer, Long> LOCKMAP
            = new HashMap<Integer, Long>();
    
    /**
     * The number of cookies that have been generated.
     */
    private static long cookieCount = 0L;
    
    /**
     * Logger instance for the LockManager class.
     */
    private final Logger log = Logger.getLogger(LockManager.class.getName());
    
    private static Timer timer = new Timer(true);
    
    private static List<Long> timedOutLocks = new ArrayList<Long>();
    
    /**
     * Locks a record by adding the record's number to the LOCKMAP
     * HashMap as key, with the generated cookie as value.  If a 
     * thread tries to obtain a lock on a record that is already
     * locked, the thread enters an inactive state consuming no
     * CPU cycles until it has been notified that a record has been
     * unlocked, at which point it will attempt to lock the record 
     * again.
     * @param recNo the number of the record to be locked.
     * @return the cookie that the record has been locked with.
     */
    public long lockRecord(int recNo) {
        String threadName = Thread.currentThread().getName();
        log.entering("LockManager.java", "lockRecord",
                      new Object[]{threadName, recNo});
        
        synchronized (MUTEX) {
            while (LOCKMAP.containsKey(recNo)) {
                System.out.println(threadName + ": Record number " + recNo + " is locked.  waiting...");
                try {
                    MUTEX.wait();
                } catch (InterruptedException e) {
                    log.throwing("LockManager.java", "lockRecord", e);
                    // NEED TO CLARIFY
                    Thread.currentThread().interrupt();
                }
            }
            long cookie = generateCookie();
            
            log.fine("Generated cookie for recNo "  + recNo + ": thread: " 
                     + threadName + " cookie: " + cookie);
            
            LOCKMAP.put(recNo, cookie);
            TimeoutTask timeout = new TimeoutTask(recNo, cookie);
            timer.schedule(timeout, 5000);
            log.fine("Total number of locks: " + LOCKMAP.size());
            System.out.println(threadName + ": Locked record number " + recNo);
            log.exiting("LockManger.java", "lockRecord",
                         new Object[]{threadName, recNo, cookie});
            
            return cookie;
        }
    }
    
    /**
     * Unlocks a record by removing the record number from the 
     * LOCKMAP HashMap.  Other threads are notified that a record
     * has been unlocked.
     * @param recNo the number of the record to be unlocked.
     * @param cookie the cookie that the record was locked with.
     * @throws SecurityException if the value of cookie parameter
     * 			is not the same value as the cookie that the record was 
     * 			locked with.
     */
    public void unlockRecord(int recNo, long cookie) throws SecurityException {
        String threadName = Thread.currentThread().getName();
        
        log.entering("LockManager.java", "unlockRecord",
                      new Object[]{threadName, recNo, cookie});
        
        synchronized (MUTEX) {
            if (timedOutLocks.contains(cookie) || LOCKMAP.get(recNo) == null) {
                System.out.println(threadName + " " + recNo + " LOCK HAS ALREADY TIMED OUT");
            }
            else if (LOCKMAP.get(recNo) == cookie) {
                System.out.println(threadName + ": Unlocking record number " + recNo);
                LOCKMAP.remove(recNo);
                MUTEX.notifyAll();
                System.out.println(threadName + ": Notifying threads that record number " + recNo + " is unlocked");
            }
            else {
                log.warning(threadName + ": An illegal attempt was made to unlock record number " + recNo);
                //throw new SecurityException();
            }
        }
    }
    
    /**
     * Generates a lock cookie.
     * @return lock cookie.
     */
    private long generateCookie() {
        cookieCount += 1;
        return System.currentTimeMillis() + cookieCount;
    }
    
    /**
     * REMOVE AFTER TESTING
     * Gets the HashMap that maps locked record numbers
     * to the cookies they are locked with.
     * @return 
     */
    public Map<Integer, Long> getLockMap() {
        return LOCKMAP;
    }
    
    private class TimeoutTask extends TimerTask {
        
        int recNo;
        
        long lockCookie;
        
        TimeoutTask(int recNo, long cookie) {
            this.recNo = recNo;
            this.lockCookie = cookie;
        }
        
        @Override
        public void run() {
            Long cookie = LOCKMAP.get(recNo);
            
            if (cookie != null && cookie == lockCookie) {
                try {
                    unlockRecord(recNo, cookie);
                } catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    //e.printStackTrace();
                }
                timedOutLocks.add(cookie);
                System.out.println("REMOVED " + recNo + " FROM MAP AND NOTIFIED ALL THREADS.....");
            }
        }

    }

}
