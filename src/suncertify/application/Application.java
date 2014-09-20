package suncertify.application;

import javax.swing.JOptionPane;

import suncertify.db.DB;
import suncertify.db.DBException;
import suncertify.gui.BusinessModel;
import suncertify.gui.ConnectionDialog;
import suncertify.gui.MainWindow;
import suncertify.gui.ServerWindow;
import suncertify.network.NetworkException;

/**
 * The entry point to the application.  This class reads the command line
 * arguments passed by the user and shows a <code>ConnectionDialog</code>
 * or <code>ServerWindow</code> depending on the mode that is specified.
 * Following the successful input of configuration parameters the main 
 * functionality of the application will be available and the configuration
 * data is saved to suncertify.properties (for client modes only).
 * @author john
 *
 */
public class Application {
    
    /**
     * The <code>ApplicationMode</code> that the application is running in.
     */
    private ApplicationMode mode;
    
    /**
     * The location of the database - used when in non-networked client mode.
     */
    private String databaseLocation;
    
    /**
     * The hostname / ip address of the server - used in networked client mode.
     */
    private String hostname;
    
    /**
     * The port on which the server application is listening for requests.
     */
    private int port;
	
    /**
     * The <code>main</code> method application entry point.
     * @param args the command line arguments supplied by the user
     *        to indicate which mode should be run.
     */
	public static void main(String[] args) {
		new Application().start(args);
	}
	
	/**
	 * Examines the supplied command line arguments and
	 * decides whether a client <code>ConnectionDialog</code>
	 * or <code>ServerWindow</code> is to be displayed.
	 * @param args the command line arguments supplied by the user
     *        to indicate which mode should be run.
	 */
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
	
	/**
	 * Reads the saved configuration data from <code>suncertify.properties</code>
	 * and creates an <code>ConnectionDialog</code> the type of which depends upon
	 * the <code>ApplicationMode</code>.  When the dialog is dismissed an attempt
	 * is made to establish a local or remote connection to the database.
	 * @param mode the mode that the application is running in.
	 */
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
	
	/**
	 * Creates and displays a new <code>ServerWindow</code>
	 */
	private void showServerWindow() {
		new ServerWindow();
	}
	
	/**
	 * Establishes a connection to a local database.
	 * Saves the connection configuration data if successful.
	 * @param dbLocation the path to the database file.
	 */
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
	
	/**
	 * Establishes a connection to a remote database.
	 * Saves the connection configuration data if successful.
	 * @param hostname the name / ip address of the
	 *        machine on which the database server is located.
	 * @param port the port that the server application
	 *        is running on.
	 */
	private void createRemoteConnection(String hostname, int port) {
		try {
			DB dataAccess = DatabaseConnection.getRemoteConnection(hostname, port);
			createClientGUI(dataAccess);
		    saveConfiguration();
		} catch (DBException | NetworkException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), 
					"Could not connect to server", JOptionPane.ERROR_MESSAGE);
			// MAYBE SHOULD LEAVE DIALOG OPEN
		}
	}
	
	/**
	 * Creates the client GUI.
	 * @param dataAccess the database connection - 
	 *        may be local or remote.
	 */
	private void createClientGUI(DB dataAccess) {
		BusinessModel model = new BusinessModel(dataAccess);
	    new MainWindow(model);
	}
	
	/**
	 * Saves the database connection configuration data to
	 * <code>suncertify.properties</code>.
	 */
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
