package suncertify.network;

import java.net.BindException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
	
	private static Registry rmiRegistry;
	
	public static void startServer(String dbLocation, int port) {
		try {
			rmiRegistry = LocateRegistry.createRegistry(port);
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
	
	public static void stopServer(int port) {
		try {
			if (rmiRegistry != null) {
				//Naming.unbind("rmi://127.0.0.1/Data");
			    UnicastRemoteObject.unexportObject(rmiRegistry, true);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
