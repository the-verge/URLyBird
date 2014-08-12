package test;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

import java.util.Map;

import suncertify.db.LockManager;
import suncertify.db.SecurityException;

public class LockManagerTest {
    
    LockManager manager = new LockManager();
    
    @Test
    public void numberOfLocksTest() throws SecurityException {
        Map<Integer, Long> lockMap = manager.getLockMap();
        
        long returnedCookie1 = manager.lockRecord(1);
        assertEquals(1, lockMap.size());
        long cookieInMap1 = lockMap.get(1);
        assertEquals(cookieInMap1, returnedCookie1);
        
        long returnedCookie2 = manager.lockRecord(2);
        assertEquals(2, lockMap.size());
        long cookieInMap2 = lockMap.get(2);
        assertEquals(cookieInMap2, returnedCookie2);
        
        manager.unlockRecord(1, returnedCookie1);
        manager.unlockRecord(2, returnedCookie2);
        
        assertEquals(0, lockMap.size());
        
    }

    @Test(expectedExceptions = SecurityException.class)
    public void securityExceptionTest() throws SecurityException {
        manager.lockRecord(1);
        long illegalCookie = 11L;
        manager.unlockRecord(1, illegalCookie);
    }

}
