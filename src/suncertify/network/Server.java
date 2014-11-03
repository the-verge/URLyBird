package suncertify.network;

import java.io.IOException;
import java.net.BindException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

import suncertify.application.Utils;


/**
 * The <code>Server</code> class starts RMI registry
 * programmatically and registers an instance of 
 * <code>DataRemoteAdapterImpl</code>, binding the
 * <code>DataRemoteAdapterImpl</code> to the name
 * "Data" in the registry.
 *
 * @author John Harding
 */
public class Server {
    
    /**
     * Logger for the Server class.
     */
    private static Logger log = Logger.getLogger(Server.class.getName());

    /**
     * Provides database connections for remote clients.
     */
    private static DataRemoteAdapterImpl remoteObject;
	
    /**
     * Starts RMI registry and registers <code>DataRemoteAdapterImpl</code>.
     * @param dbLocation the location of the database on the database server
     *        machine.
     * @param port the port on which to run RMI registry.
     * @throws DBException if a problem occurs registering
     *         a remote object in the RMI registry.
     * @throws NetworkException if a network error occurs.
     */
	public static void startServer(String dbLocation, int port) {
	    Utils.setLogLevel(log, Level.FINER);
		try {
			LocateRegistry.createRegistry(port);
			registerObject(dbLocation, port);
			log.info("RMI registry running on port " + port);
		} 
		catch (RemoteException e) {
		    log.throwing("Server.java", "startServer", e);
			Throwable cause = e.getCause();
			if (cause instanceof BindException) {
				/**
				 * Throw NetworkException here to remove need to 
				 * have explicit handling of RemoteException in the
				 * ServerWindow GUI. The GUI would be tied to an RMI 
				 * implementation if we handle RemoteException in the GUI.
				 */
				throw new NetworkException("Port " + port + " is already in use", e);
			}
			else {
				throw new NetworkException("Server error", e);
			}
		}
	}
	
	/**
	 * Closes the database connection.
	 */
	public static void closeDatabaseConnection() {
	    try {
	        if (remoteObject != null) {
	            remoteObject.closeDatabaseConnection();
	            log.info("Server stopped...");
	        }
        } catch (IOException e) {
            /**
             * Nothing of use can be conveyed to the user
             * if closing the database connection fails,
             * so this exception is propagated no further.
             */
            log.throwing("Server.java", "closeDatabaseConnection", e);
        }
	}
	
	/**
	 * Exports <code>DataRemoteAdapterImpl</code> to an RMI registry.
	 * @param dbLocation the location of the database on the database server
     *        machine.
	 * @param port the port on which RMI registry is running.
	 * @throws RemoteException if a networking error occurs.
	 * @throws DBException if the <code>DataRemoteAdapterImpl<code>
     *         instance <code>remoteObject</code> cannot be created.
	 */
	private static void registerObject(String dbLocation, int port) throws RemoteException {
		remoteObject = new DataRemoteAdapterImpl(dbLocation);
		Registry registry = LocateRegistry.getRegistry(port);
		registry.rebind("Data", remoteObject);
    }
	
}
