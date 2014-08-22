package test.db;

import suncertify.db.Data;

/**
 * A 'test' of record locking
 * @author 
 *
 */
public class LockTest {

    public static void main(String[] args) throws InterruptedException {
        
        Data data = new Data("/Users/john/workspace/URLyBird/db-1x3.db");
        
        
        Thread thread1 = new Thread(new MockClient(data, 1), "CLIENT 1");
        Thread thread2 = new Thread(new MockClient(data, 1), "CLIENT 2");
        Thread thread3 = new Thread(new MockClient(data, 1), "CLIENT 3");
        
        thread1.start();
        
        Thread.sleep(3000);
        
        thread2.start();
        
        //Thread.sleep(3000);
        
        thread3.start();
        
        thread1.join();
        thread2.join();
        thread3.join();
        
        System.out.println("Main thread finished.");
        
    }

}
