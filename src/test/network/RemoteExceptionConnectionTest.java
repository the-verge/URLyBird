package test.network;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.junit.Before;
import org.junit.Test;

import suncertify.db.DBException;
import suncertify.db.RecordNotFoundException;
import suncertify.network.DataProxy;
import suncertify.network.DataRemoteAdapter;
import suncertify.network.DataRemoteAdapterImpl;
import suncertify.network.Server;

public class RemoteExceptionConnectionTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test(expected = DBException.class)
    public void remoteObjectConstructionTestWithInvalidFilePath() throws RemoteException {
        DataRemoteAdapterImpl remoteObject = new DataRemoteAdapterImpl("/Users/john/db-144.db");
    }
    
    @Test(expected = DBException.class)
    public void startServerWithInvalidFilePathTest() throws RemoteException {
        Server.startServer("/Users/john/db-144.db", 1097);
    }
    
    @Test(expected = RecordNotFoundException.class)
    public void remoteObjectRecordNotFoundTest() throws RemoteException, MalformedURLException, NotBoundException, RecordNotFoundException {
        Server.startServer("/Users/john/workspace/urlybird/db-1x3.db", 1098);
        DataRemoteAdapter remote = (DataRemoteAdapter) Naming.lookup("rmi://127.0.0.1:1098/Data");
        DataProxy data = new DataProxy(remote);
        String[] record = data.read(-1);
    }
    
}
