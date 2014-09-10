package suncertify.gui;

import javax.swing.JOptionPane;

import suncertify.connection.DatabaseConnection;
import suncertify.db.DB;
import suncertify.db.DBException;

public class Application {
	
	public static void main(String[] args) {
		new Application().start(args);
	}
	
	private void start(String[] args) {
		
		if (args.length == 0) {
			showConnectionDialog(ApplicationMode.NETWORK_CLIENT);
		}
		else {
			// only take into account 1st arg
			String mode = args[0].trim();
			if (mode.equalsIgnoreCase("alone")) {
				showConnectionDialog(ApplicationMode.STANDALONE_CLIENT);
			}
			else if (mode.equalsIgnoreCase("server")) {
				showServerWindow();
			}
			else {
				JOptionPane.showMessageDialog(null, "Valid arguments are:\n\n1: server,\n2: alone, \n3: leave blank.", 
						"Invalid arguments", JOptionPane.ERROR_MESSAGE);
				
				System.exit(1);
			}
		}
	}
	
	private void showConnectionDialog(ApplicationMode mode) {
		ConnectionDialog dialog = new ConnectionDialog(mode);
		String dbLocation = dialog.getDatabaseLocation();
		int port = dialog.getPort();
		
		if (mode == ApplicationMode.STANDALONE_CLIENT) {
			createLocalConnection(dbLocation);
		}
		else if (mode == ApplicationMode.STANDALONE_CLIENT){
			createRemoteConnection(dbLocation, port);
		}
	}
	
	private void showServerWindow() {
		new ServerWindow();
	}
	
	private void createLocalConnection(String dbLocation) {
		DB dataAccess = null;
		try {
			dataAccess = DatabaseConnection.getLocalConnection(dbLocation);
			createClientGUI(dataAccess);
		} catch (DBException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), 
					"Connection error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
	}
	
	private void createRemoteConnection(String hostname, int port) {
		DB dataAccess = DatabaseConnection.getRemoteConnection(hostname, port);
		createClientGUI(dataAccess);
	}
	
	private void createClientGUI(DB dataAccess) {
		BusinessModel model = new BusinessModel(dataAccess);
	    MainWindow view = new MainWindow(model);
	    GUIController controller = new GUIController(model, view);
	}

}
