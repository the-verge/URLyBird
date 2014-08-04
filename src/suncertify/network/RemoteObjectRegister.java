package suncertify.network;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RemoteObjectRegister {
    
    private RemoteObjectRegister() {
        
    }
    
    public static void registerObject(String dbLocation) throws RemoteException {
        DataRemoteAdapterImpl remoteObject = new DataRemoteAdapterImpl(dbLocation);
        Registry registry = LocateRegistry.getRegistry();
        registry.rebind("Data", remoteObject);
    }
    
    public static void registerObject(String dbLocation, int port) throws RemoteException {
        DataRemoteAdapterImpl remoteObject = new DataRemoteAdapterImpl(dbLocation);
        Registry registry = LocateRegistry.getRegistry(port);
        registry.rebind("Data", remoteObject);
    }
    
    public static void registerObject(String dbLocation, String hostname, int port) throws RemoteException {
        DataRemoteAdapterImpl remoteObject = new DataRemoteAdapterImpl(dbLocation);
        Registry registry = LocateRegistry.getRegistry(hostname, port);
        registry.rebind("Data", remoteObject);
    }

    public static void main(String[] args) throws RemoteException {
        registerObject("/Users/john/workspace/URLyBird/db-1x3.db");
    }

}
