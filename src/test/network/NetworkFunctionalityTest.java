package test.network;

import static org.junit.Assert.assertNotNull;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.junit.BeforeClass;
import org.junit.Test;

import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;
import suncertify.network.DataProxy;
import suncertify.network.DataRemoteAdapter;
import suncertify.network.Server;

public class NetworkFunctionalityTest {

    @BeforeClass
    public static void setUp() throws Exception {
        Server.startServer("/Users/john/workspace/urlybird/db-1x3.db", 1099);
    }

    @Test
    public void remoteObjectTest() throws MalformedURLException, RemoteException, NotBoundException, RecordNotFoundException, SecurityException {
        DataRemoteAdapter remote = (DataRemoteAdapter) Naming.lookup("rmi://127.0.0.1:1099/Data");
        DataProxy data = new DataProxy(remote);
        assertNotNull(data);
        long cookie = data.lock(1);
        System.out.println(cookie);
        data.unlock(1, cookie);
        System.out.println("test finished");
    }

}
