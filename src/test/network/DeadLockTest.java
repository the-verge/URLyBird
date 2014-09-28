package test.network;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import suncertify.db.RecordNotFoundException;
import suncertify.network.DataProxy;
import suncertify.network.DataRemoteAdapter;

public class DeadLockTest {

    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException, RecordNotFoundException {
        DataRemoteAdapter remote = (DataRemoteAdapter) Naming.lookup("rmi://127.0.0.1:1099/Data");
        DataProxy data = new DataProxy(remote);
        long cookie = data.lock(5);
    }

}
