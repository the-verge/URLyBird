package test.db;

import suncertify.db.DB;
import suncertify.db.Data;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

public class MockClient implements Runnable {
    
    private DB data;
    
    private long lockCookie;
    
    private int recNo;
    
    private long runTime;
    
    public MockClient(DB data, int recNo, long runTime) {
        this.data = data;
        this.recNo = recNo;
        this.runTime = runTime;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + ": starting up....");
        try {
            lockCookie = data.lock(recNo);
        } catch (RecordNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + ": doing some work.....");
        long endTime = System.currentTimeMillis() + runTime;
        while (System.currentTimeMillis() < endTime) {
            
        }
        try {
            data.unlock(recNo, lockCookie);
        } catch (RecordNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + ": finished....");
    }
    
    

}
