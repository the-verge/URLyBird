package suncertify.network;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

public class Test {

    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException, RecordNotFoundException, SecurityException {
        DataRemoteAdapter data = (DataRemoteAdapter) Naming.lookup("rmi://127.0.0.1/Data"); // for localhost
        long cookie = data.lock(1);
        System.out.println(cookie);
        //data.unlock(1, cookie);
    }

}