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

import suncertify.application.Configuration;
import suncertify.db.DBException;
import suncertify.network.NetworkException;
import suncertify.network.Server;

/**
 * This class provides the GUI component for the 
 * database server. It allows the user to choose the 
 * database file and specify the port on which to 
 * listen for requests.
 *
 * @author John Harding
 */
public class ServerWindow extends JFrame {
    
    /**
     * The SUID.
     */
    private static final long serialVersionUID = 1991L;
    
    /**
     * The lowest possible port number.
     */
    private static final int LOWEST_VALID_PORT = 1025;
    
    /**
     * The highest possible port number.
     */
    private static final int HIGHEST_VALID_PORT = 65535;
    
    /**
     * The smallest possible path length for the database file.
     */
    private static final int MINIMUM_LOCATION_LENGTH = 4;
    
    /**
     * Label for database file location field.
     */
    private JLabel locationLabel = new JLabel("Database location");
    
    /**
     * Label for port field.
     */
    private JLabel portLabel = new JLabel("Port");
    
    /**
     * Database location text field.
     */
    private JTextField locationTextField = new JTextField();
    
    /**
     * Port text field.
     */
    private JTextField portTextField = new JTextField();
    
    /**
     * Start button.
     */
    private JButton startButton = new JButton("Start");
    
    /**
     * Exit button.
     */
    private JButton exitButton = new JButton("Exit");
    
    /**
     * Browse button.
     */
    private JButton browseButton = new JButton(" Browse ");
    
    /**
     * File chooser to enable selection of database file.
     */
    private JFileChooser chooser = new JFileChooser(".");
    
    /**
     * Filter used to restrict the types of files displayed.
     */
    private FileFilter filter = new FileNameExtensionFilter("db files", "db");
    
    /**
     * The location of the database file.
     */
    private String databaseLocation;
    
    /**
     * The port on which the server is to listen for requests.
     */
    private int port;
    
    /**
     * Class constructor.
     */
    public ServerWindow(Configuration config) {
        super("URLyBird Server");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addListeners();
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        mainPanel.add(createServerPanel());
        
        if (config != null) {
            loadConfigurationData(config);
        }
        
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setMinimumSize(getSize());
        setResizable(false);
        setVisible(true);
    }
    
    /**
     * Provides a means of adding an <code>ActionListener</code>
     * to <code>ServerWindow</code> start button.
     * @param listener <code>ActionListener</code> instance.
     */
    public void addExternalListener(ActionListener listener) {
        this.startButton.addActionListener(listener);
    }
    
    /**
     * Adds listeners to various GUI components.
     */
    private void addListeners() {
               
        browseButton.addActionListener(new BrowseButtonListener());
        exitButton.addActionListener(new ExitButtonListener());
        exitButton.setEnabled(false);
        startButton.addActionListener(new StartButtonListener());
        startButton.setEnabled(false);
        
        DocumentListener listener = new StartButtonEnabler();
        locationTextField.getDocument().addDocumentListener(listener);
        portTextField.getDocument().addDocumentListener(listener);
        
        addExitListener();
    }
    
    private void loadConfigurationData(Configuration config) {
        String location = config.getDatabaseLocation();
        String portNumber = config.getPort();
        
        if (location != null) {
            databaseLocation = location;
            locationTextField.setText(databaseLocation);
        }
        if (portNumber != null) {
            port = Integer.parseInt(portNumber); // number format exception
            portTextField.setText(portNumber);
        }
    }
    
    /**
     * Adds GUI components to a JPanel
     */
    private JPanel createServerPanel() {
        
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
        c.gridwidth = 2;
        panel.add(browseButton, c);
        
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
        c.gridx = 2;
        c.gridy = 2;
        c.insets = new Insets(10, 0, 5, 0);
        c.anchor = GridBagConstraints.EAST;
        c.weightx = 1.0;
        panel.add(exitButton, c);
        
        c = new GridBagConstraints();
        c.gridx = 3;
        c.gridy = 2;
        c.insets = new Insets(10, 0, 5, 0);
        c.anchor = GridBagConstraints.EAST;
        c.weightx = 1.0;
        panel.add(startButton, c);
        
        return panel;
    }
    
    /**
     * ActionListener for browse button functionality.
     */
    private class BrowseButtonListener implements ActionListener {
        
        ServerWindow parent = ServerWindow.this;
        
        @Override
        public void actionPerformed(ActionEvent e) {
        	int returnVal = parent.chooser.showDialog(parent, "Select");
            if (returnVal == JFileChooser.APPROVE_OPTION) {
            	 databaseLocation = chooser.getSelectedFile().getAbsolutePath();
                 locationTextField.setText(databaseLocation);
            }
        }
    }
    
    /**
     * ActionListener for start button functionality.
     */
    private class StartButtonListener implements ActionListener {
        
        ServerWindow parent = ServerWindow.this;
        
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
				Server.startServer(databaseLocation, port);
				startButton.setEnabled(false);
		        exitButton.setEnabled(true);
		        portTextField.setEditable(false);
		        browseButton.setEnabled(false);
		        parent.setTitle("URLyBird Server - Running...");
			} catch (DBException | NetworkException ex) {
				Dialogs.showErrorDialog(parent, ex.getMessage(), "Could not start server");
				System.exit(1);
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
     * Adds a listener to the window's close button.
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
            Server.closeDatabaseConnection();
            System.exit(0);
        }
    }
    
    /**
     * DocumentListener that checks if hostname and port meet 
     * acceptable criteria. If so, the start button is enabled.
     */
    private class StartButtonEnabler implements DocumentListener {

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
            String port = portTextField.getText().trim();
            
            if (location.length() > MINIMUM_LOCATION_LENGTH && validPort(port)) {
                startButton.setEnabled(true);
            }
            else {
                startButton.setEnabled(false);
            }
        }
    }
    
    /**
     * Checks whether the port number is valid.
     * @param portNumber  - String representation of port number.
     * @return boolean.
     */
    private boolean validPort(String portNumber) {
        int port = 0;
        try {
            port = Integer.parseInt(portNumber);
        } catch (NumberFormatException e) {
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
     * @return the database file location.
     */
    public String getDatabaseLocation() {
        return databaseLocation;
    }
    
    /**
     * Getter.
     * @return the port number.
     */
    public int getPort() {
        return port;
    }

}

