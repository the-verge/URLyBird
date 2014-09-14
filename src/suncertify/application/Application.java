package suncertify.application;

import javax.swing.JOptionPane;

import suncertify.db.DB;
import suncertify.db.DBException;
import suncertify.gui.BusinessModel;
import suncertify.gui.ConnectionDialog;
import suncertify.gui.GUIController;
import suncertify.gui.MainWindow;
import suncertify.gui.ServerWindow;
import suncertify.network.NetworkException;

public class Application {
    
    private ApplicationMode mode;
    
    private String databaseLocation;
    
    private String hostname;
    
    private int port;
	
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
	    this.mode = mode;
		Configuration config = null;
        try {
            config = PropertiesAccessor.getConfiguration(mode);
        } catch (ConfigurationException e) {
            JOptionPane.showMessageDialog(null, "Error reading configuration data.\nPlease enter details manually.", 
                    "Could not load previous configuration", JOptionPane.ERROR_MESSAGE);
        }
        ConnectionDialog dialog = new ConnectionDialog(mode, config);
	    
		databaseLocation = dialog.getDatabaseLocation();
		hostname = dialog.getHostname();
		port = dialog.getPort();
		
		if (mode == ApplicationMode.STANDALONE_CLIENT) {
			createLocalConnection(databaseLocation);
		}
		else if (mode == ApplicationMode.NETWORK_CLIENT) {
			createRemoteConnection(hostname, port);
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
		    saveConfiguration(); // investigate
		} catch (DBException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), 
					"Connection error", JOptionPane.ERROR_MESSAGE);
			//System.exit(1);
		}
	}
	
	private void createRemoteConnection(String hostname, int port) {
		try {
			DB dataAccess = DatabaseConnection.getRemoteConnection(hostname, port);
			createClientGUI(dataAccess);
		    saveConfiguration();
		} catch (NetworkException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), 
					"Could not connect to server", JOptionPane.ERROR_MESSAGE);
			// MAYBE SHOULD LEAVE DIALOG OPEN
		}
	}
	
	private void createClientGUI(DB dataAccess) {
		BusinessModel model = new BusinessModel(dataAccess);
	    MainWindow view = new MainWindow(model);
	    GUIController controller = new GUIController(model, view);
	}
	
	private void saveConfiguration() {
	    Configuration config = null;
	    if (mode == ApplicationMode.STANDALONE_CLIENT) {
	        config = new Configuration(databaseLocation);
	    }
	    else if (mode == ApplicationMode.NETWORK_CLIENT) {
	        String portNumber = Integer.toString(port);
	        config = new Configuration(hostname, portNumber);
	    }
	    try {
            PropertiesAccessor.saveConfiguration(config, mode);
        } catch (ConfigurationException e) {
            //LOG EXCEPTION - Can't really do anything about this
        }
	}

}
