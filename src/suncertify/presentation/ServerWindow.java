package suncertify.presentation;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import suncertify.network.NetworkException;
import suncertify.network.Server;

public class ServerWindow extends JFrame {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1991L;

    private static final int LOWEST_VALID_PORT = 1025;
    
    private static final int HIGHEST_VALID_PORT = 65535;
    
    private static final int MINIMUM_LOCATION_LENGTH = 4;
    
    private JLabel locationLabel = new JLabel("Database location");
    
    private JLabel portLabel = new JLabel("Port");
    
    private JTextField locationTextField = new JTextField();
    
    private JTextField portTextField = new JTextField();
    
    private JButton startButton = new JButton("Start");
    
    private JButton exitButton = new JButton("Exit");
    
    private JButton browseButton = new JButton(" Browse ");
    
    private JFileChooser chooser = new JFileChooser(".");
    
    private FileFilter filter = new FileNameExtensionFilter("db files", "db");
    
    private String databaseLocation;
    
    private int port;
    
    public ServerWindow() {
        super("URLyBird Server");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addListeners();
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        mainPanel.add(createServerPanel());
        
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setMinimumSize(getSize());
        setResizable(false);
        setVisible(true);
    }
    
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
    
    private void addExitListener() {
        addWindowListener(new WindowAdapter() {
            
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Stopping server...");
                Server.closeDatabaseConnection();
            }
        });
    }
    
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
			} catch (NetworkException ex) {
				Dialogs.showErrorDialog(parent, ex.getMessage(), "Could not start server");
				System.exit(1);
			}
        }
    }
    
    private class ExitButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Stopping server...");
            Server.closeDatabaseConnection();
            System.exit(0);
        }
    }
    
    private class StartButtonEnabler implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            check();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            check();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            check();
        }
        
        public void check() {
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
    
    
    private boolean validPort(String portNumber) {
        int port = 0;
        try {
            port = Integer.parseInt(portNumber);
        } catch (NumberFormatException e) {
            // LOG EXCEPTION
        }
        if (port >= LOWEST_VALID_PORT && port <= HIGHEST_VALID_PORT) {
            this.port = port;
            portTextField.setBorder(BorderFactory.createEmptyBorder());
            return true;
        }        
        portTextField.setBorder(BorderFactory.createLineBorder(Color.RED));
        return false;
    }
    
    public String getDatabaseLocation() {
        return databaseLocation;
    }
    
    public int getPort() {
        return port;
    }

}

