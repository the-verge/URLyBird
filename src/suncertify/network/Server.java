package suncertify.network;

import java.net.BindException;
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
				/**
				 * Throw NetworkException here to remove need to 
				 * have explicit exception handling in the ServerWindow GUI.
				 * The GUI would be tied to an RMI implementation if we
				 * handle RemoteException in the GUI.
				 */
				throw new NetworkException("Port " + port + " is already in use", e);
			}
			else {
				throw new NetworkException("Network error", e);
			}
		}
	}
	
	private static void registerObject(String dbLocation, int port) throws RemoteException {
		DataRemoteAdapterImpl remoteObject = new DataRemoteAdapterImpl(dbLocation);
		Registry registry = LocateRegistry.getRegistry(port);
		registry.rebind("Data", remoteObject);
    }
	
	/**
	 * PERHAPS REMOVE THIS
	 * @param port
	 */
	public static void stopServer(int port) {
		try {
			if (rmiRegistry != null) {
			    UnicastRemoteObject.unexportObject(rmiRegistry, true);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
