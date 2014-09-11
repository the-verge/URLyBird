package suncertify.network;

import java.net.BindException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerSetup {
	
	public static void startServer(String dbLocation, int port) {
		try {
			LocateRegistry.createRegistry(port);
			registerObject(dbLocation, port);
			System.out.println("RMI registry running on port " + port);
		} 
		catch (RemoteException e) {
			Throwable cause = e.getCause();
			if (cause instanceof BindException) {
				// Port is already in use
				System.out.println(e.getCause());
			}
			else {
				// Generic error message and log exception
			}
		}
	}
	
	private static void registerObject(String dbLocation, int port) throws RemoteException {
		DataRemoteAdapterImpl remoteObject = new DataRemoteAdapterImpl(dbLocation);
		Registry registry = LocateRegistry.getRegistry(port);
		registry.rebind("Data", remoteObject);
    }
	
	public static void unregisterObject() {
		
	}

}
