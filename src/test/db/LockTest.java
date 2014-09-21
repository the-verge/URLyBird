package test.db;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import suncertify.network.DataProxy;
import suncertify.network.DataRemoteAdapter;

/**
 * A 'test' of record locking
 * @author 
 *
 */
public class LockTest {

    public static void main(String[] args) throws InterruptedException, MalformedURLException, RemoteException, NotBoundException {
        
        //Data data = new Data("/Users/john/workspace/URLyBird/db-1x3.db");
        DataRemoteAdapter remote = (DataRemoteAdapter) Naming.lookup("rmi://127.0.0.1:1099/Data");
        DataProxy data = new DataProxy(remote);
        
        Thread thread1 = new Thread(new MockClient(data, 1, 8000), "CLIENT 1");
        Thread thread2 = new Thread(new MockClient(data, 1, 4000), "CLIENT 2");
        Thread thread3 = new Thread(new MockClient(data, 1, 8000), "CLIENT 3");
        
        thread1.start();
        
        Thread.sleep(3000);
        
        thread2.start();
        
        //Thread.sleep(3000);
        
        //thread3.start();
        
        thread1.join();
        thread2.join();
        //thread3.join();
        
        System.out.println("Main thread finished.");
        
    }

}
