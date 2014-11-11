package suncertify.application;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import suncertify.db.CloseableDB;
import suncertify.db.DBException;
import suncertify.db.Data;
import suncertify.network.DataProxy;
import suncertify.network.DataRemoteAdapter;
import suncertify.network.NetworkException;

/**
 * The static methods of this class return a <code>CloseableDB</code>
 * instance for both local and remote database connections.
 *
 * @author John Harding
 */
public class DatabaseConnection {
    
    /**
     * Logger for the DatabaseConnection class.
     */
    private static Logger log = Logger.getLogger(DatabaseConnection.class.getName());
    
    /**
     * Returns an object used to connect to a local database.
     * @param dbLocation the path to the database file.
     * @throws DBException if the <code>Data</code> instance 
     *         cannot be instantiated. 
     * @return a <code>Data</code> instance.
     */
	public static CloseableDB getLocalConnection(String dbLocation) {
		return new Data(dbLocation);
	}
	
	/**
	 * Returns an object used to connect to a remote database.
	 * @param hostname the name / ip address of the
     *        machine on which the database server is located.
     * @param port the port that the server application
     *        is running on.
     * @throws NetworkException if networking errors occur. 
	 * @return a <code>DataProxy</code> instance.
	 */
	public static CloseableDB getRemoteConnection(String hostname, int port) {
	    Utils.setLogLevel(log, Level.FINER);
		CloseableDB data;
		String url = "rmi://" + hostname + ":" + port + "/Data";
		try {
			DataRemoteAdapter remote = (DataRemoteAdapter) Naming.lookup(url);
			data = new DataProxy(remote);
		} catch (UnknownHostException e) {
		    log.throwing("DatabaseConnection", "getRemoteConnection", e);
			throw new NetworkException("Cannot resolve host " + hostname, e);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
	        log.throwing("DatabaseConnection", "getRemoteConnection", e);
			throw new NetworkException("Network error:\nCheck hostname and port", e);
		} 
		return data;
	}
}
