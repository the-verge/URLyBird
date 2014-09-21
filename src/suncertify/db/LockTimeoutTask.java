package suncertify.db;

import java.util.Map;
import java.util.TimerTask;

public class LockTimeoutTask extends TimerTask {
    
    private Map<Integer, Long> lockMap;
    
    private int recNo;
    
    private long cookie;
    
    private String mutex;
    
    LockTimeoutTask(Map<Integer, Long> lockMap, int recNo, long cookie, String mutex) {
        this.lockMap = lockMap;
        this.recNo = recNo;
        this.cookie = cookie;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        if (lockMap.get(recNo) == cookie) {
            lockMap.remove(recNo);
            synchronized (mutex) {
                mutex.notifyAll();
            }
            System.out.println("REMOVED " + recNo + " FROM MAP AND NOTIFIED ALL THREADS.....");
        }
    }

}
