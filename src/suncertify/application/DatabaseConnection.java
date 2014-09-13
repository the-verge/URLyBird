package suncertify.application;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.UnknownHostException;

import suncertify.db.DB;
import suncertify.db.Data;
import suncertify.network.DataProxy;
import suncertify.network.DataRemoteAdapter;
import suncertify.network.NetworkException;

public class DatabaseConnection {
	
	public static DB getLocalConnection(String dbLocation) {
		return new Data(dbLocation);
	}
	
	public static DB getRemoteConnection(String hostname, int port) {
		DB data = null;
		String url = "rmi://" + hostname + ":" + port + "/Data";
		try {
			DataRemoteAdapter remote = (DataRemoteAdapter) Naming.lookup(url);
			data = new DataProxy(remote);
		} catch (UnknownHostException e) {
			throw new NetworkException("Cannot resolve host " + hostname, e);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			throw new NetworkException("Network error:\nCheck hostname and port", e);
		} 
		return data;
	}
}
