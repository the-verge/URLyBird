package connection;

import suncertify.db.DB;
import suncertify.db.Data;

public class DatabaseConnection {
	
	public static DB getLocalConnection(String dbLocation) {
		return new Data(dbLocation);
	}
	
	public static DB getRemoteConnection(String hostName, int port) {
		// Do RMI lookup here and return a DataProxy instance
		return null;
	}
}
