package suncertify.connection;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import suncertify.db.DB;
import suncertify.db.Data;
import suncertify.network.DataProxy;
import suncertify.network.DataRemoteAdapter;

public class DatabaseConnection {
	
	public static DB getLocalConnection(String dbLocation) {
		return new Data(dbLocation);
	}
	
	public static DB getRemoteConnection(String hostname, int port) {
		// Do RMI lookup here and return a DataProxy instance
		DB data = null;
		String url = "rmi://" + hostname + ":" + port + "/Data";
		try {
			DataRemoteAdapter remote = (DataRemoteAdapter) Naming.lookup(url);
			data = new DataProxy(remote);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
}
