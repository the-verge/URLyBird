package suncertify.application;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import suncertify.db.CloseableDB;
import suncertify.db.DBException;
import suncertify.network.NetworkException;
import suncertify.presentation.ConnectionDialog;
import suncertify.presentation.Dialogs;
import suncertify.presentation.MainWindow;
import suncertify.presentation.ServerWindow;

/**
 * The entry point to the application.  This class reads the command line
 * arguments passed by the user and shows a <code>ConnectionDialog</code>
 * or <code>ServerWindow</code> depending on the mode that is specified.
 * Following the successful input of configuration parameters the main 
 * functionality of the application will be available and the configuration
 * data is saved to <code>suncertify.properties</code>.
 *
 * @author John Harding
 */
public class Application {
    
    /**
     * The <code>ApplicationMode</code> that the application is running in.
     */
    private ApplicationMode mode;
    
    /**
     * The location of the database.
     */
    private String databaseLocation;
    
    /**
     * The hostname / ip address of the server - used in networked client mode.
     */
    private String hostname;
    
    /**
     * The port in use.
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
	 * If more than one argument is supplied only the first
	 * is taken into account.
	 * @param args the command line arguments supplied by the user
     *        to indicate which mode should be run.
	 */
	private void start(String[] args) {
		
		if (args.length == 0) {
		    mode = ApplicationMode.NETWORK_CLIENT;
			showConnectionDialog(mode);
		}
		else {
			String arg = args[0].trim();
			if (arg.equalsIgnoreCase("alone")) {
			    mode = ApplicationMode.STANDALONE_CLIENT;
				showConnectionDialog(mode);
			}
			else if (arg.equalsIgnoreCase("server")) {
			    mode = ApplicationMode.SERVER;
				showServerWindow();
			}
			else {
				Dialogs.showErrorDialog(null, "Valid arguments are:\n\n1: server,\n2: alone, \n3: leave blank.", 
				                        "Invalid arguments");
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
		Configuration config = null;
        try {
            config = PropertiesAccessor.getConfiguration(mode);
        } catch (ConfigurationException e) {
            Dialogs.showInfoDialog(null, "Configuration data not found.\nPlease enter "
                    + "database connection\ndetails manually.", "Could not read configuration data");
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
	 * Creates and displays a new <code>ServerWindow</code>.
	 * Adds an action listener to the <code>ServerWindow</code>
	 * in order to determine when the user has pressed the start
	 * button and configuration data is available to save.
	 */
	private void showServerWindow() {
	    Configuration config = null;
        try {
            config = PropertiesAccessor.getConfiguration(mode);
        } catch (ConfigurationException e) {
            Dialogs.showInfoDialog(null, "Configuration data not found.\nPlease enter "
                    + "database connection\ndetails manually.", "Could not read configuration data");
        }
        
		final ServerWindow window = new ServerWindow(config);
		window.addExternalListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                databaseLocation = window.getDatabaseLocation();
                port = window.getPort();
                saveConfiguration();
            }
		});
	}
	
	/**
	 * Establishes a connection to a local database.
	 * Saves the connection configuration data if successful.
	 * @param dbLocation the path to the database file.
	 */
	private void createLocalConnection(String dbLocation) {
		CloseableDB dataAccess;
		try {
			dataAccess = DatabaseConnection.getLocalConnection(dbLocation);
			createClientGUI(dataAccess);
		} catch (DBException e) {
			Dialogs.showErrorDialog(null, e.getMessage(), "Database connection error");
			System.exit(1);
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
			CloseableDB dataAccess = DatabaseConnection.getRemoteConnection(hostname, port);
			createClientGUI(dataAccess);
		} catch (DBException | NetworkException e) {
			Dialogs.showErrorDialog(null, e.getMessage(), "Could not connect to server");
			System.exit(1);
		}
	}
	
	/**
	 * Creates the client GUI.
	 * @param dataAccess the database connection - 
	 *        may be local or remote.
	 */
	private void createClientGUI(CloseableDB dataAccess) {
        saveConfiguration();
		BusinessService service = new BusinessService(dataAccess);
	    new MainWindow(service);
	}
	
	/**
	 * Saves the database connection configuration data to
	 * <code>suncertify.properties</code>.
	 */
	private void saveConfiguration() {
	    Configuration config = null;
	    if (mode == ApplicationMode.STANDALONE_CLIENT) {
	        config = Configuration.standaloneClientConfig(databaseLocation);
	    }
	    else if (mode == ApplicationMode.NETWORK_CLIENT) {
	        String portNumber = Integer.toString(port);
	        config = Configuration.networkClientConfig(hostname, portNumber);
	    }
	    else if (mode == ApplicationMode.SERVER) {
	        String portNumber = Integer.toString(port);
	        config = Configuration.serverConfig(databaseLocation, portNumber);
	    }
	    try {
            PropertiesAccessor.saveConfiguration(config, mode);
        } catch (ConfigurationException e) {
            /**
             * Given that this is a non fatal error, in that normal
             * user functions of the application are still available,
             * execution continues.
             */
        }
	}

}
