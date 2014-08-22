package test.db;

import suncertify.db.Data;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

public class MockClient implements Runnable {
    
    private Data data;
    
    private long lockCookie;
    
    private int recNo;
    
    
    public MockClient(Data data, int recNo) {
        this.data = data;
        this.recNo = recNo;
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
        long endTime = System.currentTimeMillis() + 5000;
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
