package suncertify.presentation;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import suncertify.application.ApplicationMode;
import suncertify.application.Configuration;

/**
 * This class provides a GUI to enable the user to
 * specify database connection details. If the application
 * is started in stand alone mode, the user can specify the
 * location of the database file.  If the application is run in
 * network client mode, the user can input the hostname or IP
 * address of the machine that hosts the database server. The
 * port on which the server is running must also be specified.
 *
 * @author John Harding
 */
public class ConnectionDialog extends JDialog {
    
    /**
     * The SUID
     */
    private static final long serialVersionUID = 1661L;
    
    /**
     * The lowest valid port number.
     */
    private static final int LOWEST_VALID_PORT = 1025;
    
    /**
     * The highest valid port number.
     */
    private static final int HIGHEST_VALID_PORT = 65535;
    
    /**
     * The minimum possible path length for a database file
     */
    private static final int MINIMUM_LOCATION_LENGTH = 4;
    
    /**
     * JLabel for database location field.
     */
    private JLabel locationLabel = new JLabel("Database location");
    
    /**
     * JLabel for port field.
     */
    private JLabel portLabel = new JLabel("Port");
    
    /**
     * JLabel for hostname field.
     */
    private JLabel hostLabel = new JLabel("Hostname");
    
    /**
     * JTextField for database location details.
     */
    private JTextField locationTextField = new JTextField();
    
    /**
     * JTextField for hostname details.
     */
    private JTextField hostnameTextField = new JTextField();
    
    /**
     * JTextField for port details.
     */
    private JTextField portTextField = new JTextField();
    
    /**
     * JButton used to connect to server.
     */
    private JButton connectButton = new JButton("Connect");
    
    /**
     * JButton used to exit the application.
     */
    private JButton exitButton = new JButton("Exit");
    
    /**
     * JButton used to open file chooser.
     */
    private JButton browseButton = new JButton(" Browse ");
    
    /**
     * JFileChooser used to browse for database file.
     */
    private JFileChooser chooser = new JFileChooser(".");
    
    /**
     * FileFilter used to restrict types of files shown.
     */
    private FileFilter filter = new FileNameExtensionFilter("db files", "db");
    
    /**
     * The mode in which the application is to be run.
     */
    private ApplicationMode mode;
    
    /**
     * The location of the database file.
     */
    private String databaseLocation = "";
    
    /**
     * The database server hostname.
     */
    private String hostname = "";
    
    /**
     * The port on which the database server is listening.
     */
    private int port;
    
    /**
     * Class constructor.
     * @param mode the mode in which the application was started in.
     * @param config the configuration data saved from the previous
     *        run of the application.
     */
    public ConnectionDialog(ApplicationMode mode, Configuration config) {
        this.mode = mode;
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addListeners();
        
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        
        if (mode == ApplicationMode.STANDALONE_CLIENT) {
            this.setTitle("Connect to a local database");
            mainPanel.add(standAlonePanel());
        }
        else if (mode == ApplicationMode.NETWORK_CLIENT) {
            this.setTitle("Connect to a remote database");
            mainPanel.add(networkClientPanel());
        }
        
        if (config != null) {
            loadConfigurationData(config);
        }

        this.setModal(true);
        this.add(mainPanel);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setMinimumSize(this.getSize());
        this.setResizable(false);
        this.setVisible(true);
    }
    
    /**
     * Displays the configuration data saved from the previous
     * run of the application.
     * @param config the configuration data.
     */
    private void loadConfigurationData(Configuration config) {
       
        if (mode == ApplicationMode.STANDALONE_CLIENT) {
            String location = config.getDatabaseLocation();
            
            if (location != null) {
                databaseLocation = location;
                locationTextField.setText(databaseLocation);
            }
        }
        else if (mode == ApplicationMode.NETWORK_CLIENT) {
            String host = config.getHostname();
            String portNumber = config.getPort();
            
            if (host != null) {
                hostname = host;
                hostnameTextField.setText(hostname);
            }
            if (portNumber != null) {
                port = Integer.parseInt(portNumber); // number format exception
                portTextField.setText(portNumber);
            }
        }
    }
    
    /**
     * Adds listeners to the various GUI components.
     */
    private void addListeners() {
        
        addExitListener();
        browseButton.addActionListener(new BrowseButtonListener());
        exitButton.addActionListener(new ExitButtonListener());
        connectButton.addActionListener(new ConnectButtonListener());
        connectButton.setEnabled(false);
        
        DocumentListener listener = new ConnectButtonEnabler();
        locationTextField.getDocument().addDocumentListener(listener);
        hostnameTextField.getDocument().addDocumentListener(listener);
        portTextField.getDocument().addDocumentListener(listener);
    }
    
    /**
     * Creates a JPanel used to specify
     * data for a local database connection.
     */
    private JPanel standAlonePanel() {
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c;
        chooser.setFileFilter(filter);
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.ipadx = 15;
        panel.add(locationLabel, c);
        
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        locationTextField.setColumns(40);
        locationTextField.setEditable(false);
        panel.add(locationTextField, c);
        
        c = new GridBagConstraints();
        c.gridx = 3;
        c.gridy = 0;
        panel.add(browseButton, c);
        
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 1;
        c.insets = new Insets(10, 0, 5, 0);
        c.anchor = GridBagConstraints.EAST;
        panel.add(exitButton, c);
        
        c = new GridBagConstraints();
        c.gridx = 3;
        c.gridy = 1;
        c.insets = new Insets(10, 0, 5, 0);
        panel.add(connectButton, c);
        
        return panel;
    }
    
    /**
     * Creates a JPanel used to specify
     * data for a remote database connection.
     */
    private JPanel networkClientPanel() {
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c;
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.ipadx = 15;
        panel.add(hostLabel, c);
        
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        hostnameTextField.setColumns(25);
        panel.add(hostnameTextField, c);
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.ipadx = 15;
        c.anchor = GridBagConstraints.WEST;
        panel.add(portLabel, c);
        
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        portTextField.setColumns(7);
        c.anchor = GridBagConstraints.WEST;
        panel.add(portTextField, c);
        
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 2;
        c.insets = new Insets(10, 0, 5, 0);
        c.anchor = GridBagConstraints.EAST;
        c.weightx = 1.0;
        panel.add(exitButton, c);
        
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 2;
        c.insets = new Insets(10, 0, 5, 0);
        c.anchor = GridBagConstraints.EAST;
        panel.add(connectButton, c);
        
        return panel;
    }
    
    /**
     * ActionListener for browse button functionality.
     */
    private class BrowseButtonListener implements ActionListener {
        
        ConnectionDialog parent = ConnectionDialog.this;
        
        @Override
        public void actionPerformed(ActionEvent e) {
            int returnVal = parent.chooser.showDialog(parent, "Select");
            if (returnVal == JFileChooser.APPROVE_OPTION) {
            	 databaseLocation = chooser.getSelectedFile().getAbsolutePath();
                 locationTextField.setText(databaseLocation);
                 connectButton.setEnabled(true);
            }
        }
    }
    
    /**
     * ActionListener for exit button functionality.
     */
    private class ExitButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            handleExitGesture();
        }
    }

    /**
     * Adds a listener to the dialog's close button.
     */
    private void addExitListener() {
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                handleExitGesture();
            }
        });
    }

    /**
     * Displays a confirm dialog asking the user if they are
     * sure that they want to quit the application. If the
     * user selects 'OK', the application will exit.
     */
    private void handleExitGesture() {
        int response = Dialogs.showConfirmQuitDialog(this);
        if (response == JOptionPane.OK_OPTION) {
            System.exit(0);
        }
    }
    
    /**
     * ActionListener for connect button functionality.
     */
    private class ConnectButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
        	if (mode == ApplicationMode.NETWORK_CLIENT) {
        		hostname = hostnameTextField.getText();
        	}
            ConnectionDialog.this.dispose();
        }
    }
    
    /**
     * DocumentListener that checks that text field data
     * meets criteria. If so, the connect button is enabled.
     */
    private class ConnectButtonEnabler implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            checkFields();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            checkFields();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            checkFields();
        }
        
        public void checkFields() {
            String location = locationTextField.getText().trim();
            String host = hostnameTextField.getText().trim();
            String port = portTextField.getText().trim();
            
            if (mode == ApplicationMode.STANDALONE_CLIENT) {
                if (location.length() > MINIMUM_LOCATION_LENGTH) {
                    connectButton.setEnabled(true);
                }
                else {
                    connectButton.setEnabled(false);
                }
            }
            else if (mode == ApplicationMode.NETWORK_CLIENT) {
                if (host.length() > MINIMUM_LOCATION_LENGTH && validPort(port)) {
                    connectButton.setEnabled(true);
                }
                else {
                    connectButton.setEnabled(false);
                }
            }
        }
    }
    
    /**
     * Checks if the supplied port number is valid.
     * @param portNumber <code>String</code> representation
     *        of the port number.
     * @return boolean.
     */
    private boolean validPort(String portNumber) {
        int port = 0;
        try {
            port = Integer.parseInt(portNumber);
        } catch (NumberFormatException e) {
            portTextField.setBorder(BorderFactory.createLineBorder(Color.RED));
            return false;
        }
        if (port >= LOWEST_VALID_PORT && port <= HIGHEST_VALID_PORT) {
            this.port = port;
            portTextField.setBorder(BorderFactory.createEmptyBorder());
            return true;
        }        
        portTextField.setBorder(BorderFactory.createLineBorder(Color.RED));
        return false;
    }
    
    /**
     * Getter.
     * @return location of the database file (in standalone mode).
     */
    public String getDatabaseLocation() {
        return databaseLocation;
    }
    
    /**
     * Getter.
     * @return the database server hostname.
     */
    public String getHostname() {
    	return hostname;
    }
    
    /**
     * Getter.
     * @return the port on which the database server is listening.
     */
    public int getPort() {
        return port;
    }

}
