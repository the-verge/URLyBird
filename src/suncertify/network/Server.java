package suncertify.network;

import java.net.BindException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
	
	public static void startServer(String dbLocation, int port) {
		try {
			LocateRegistry.createRegistry(port);
			registerObject(dbLocation, port);
			System.out.println("RMI registry running on port " + port);

		} 
		catch (RemoteException e) {
			Throwable cause = e.getCause();
			if (cause instanceof BindException) {
				/**
				 * Throw NetworkException here to remove need to 
				 * have explicit handling of RemoteExceptionin the ServerWindow GUI.
				 * The GUI would be tied to an RMI implementation if we
				 * handle RemoteException in the GUI.
				 */
				throw new NetworkException("Port " + port + " is already in use", e);
			}
			else {
				throw new NetworkException("Server error", e);
			}
		}
	}
	
	private static void registerObject(String dbLocation, int port) throws RemoteException {
		DataRemoteAdapterImpl remoteObject = new DataRemoteAdapterImpl(dbLocation);
		Registry registry = LocateRegistry.getRegistry(port);
		registry.rebind("Data", remoteObject);
    }
	
}
