package test.network;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import suncertify.db.DB;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;
import suncertify.network.DataProxy;
import suncertify.network.DataRemoteAdapter;

public class ConnectionTest {

    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException, RecordNotFoundException, SecurityException {
        DataRemoteAdapter remote = (DataRemoteAdapter) Naming.lookup("rmi://127.0.0.1:1099/Data");
        DataProxy data = new DataProxy(remote);
        long cookie = data.lock(1);
        System.out.println(cookie);
        data.unlock(1, cookie);
        try {
            updateTest(data);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("main method finished");
    }
    
    public static void updateTest(DB data) throws RecordNotFoundException, SecurityException {
        String[] record = {"Newgrange", "Kildare", "4", "Y", "$250.54", "2014/07/21", "12345678"};
        long cookie = data.lock(2);
        System.out.println("Locking record for update...");
        data.update(2, record, 1L);
        data.unlock(2, cookie);
        System.out.println("Updated and unlocked record...");
    }
    
}
