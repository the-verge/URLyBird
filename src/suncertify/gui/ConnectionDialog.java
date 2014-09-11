package suncertify.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.MaskFormatter;


/**
 * @author john
 *
 */
public class ConnectionDialog extends JDialog {
    
    private static final int LOWEST_VALID_PORT = 1025;
    
    private static final int HIGHEST_VALID_PORT = 65535;
    
    private static final int MINIMUM_LOCATION_LENGTH = 4;
    
    private JLabel locationLabel = new JLabel("Database location");
    
    private JLabel portLabel = new JLabel("Port");
    
    private JLabel hostLabel = new JLabel("Hostname");
    
    private JTextField locationTextField = new JTextField();
    
    private JTextField portTextField = new JTextField();
    
    private JButton connectButton = new JButton("Connect");
    
    private JButton exitButton = new JButton("Exit");
    
    private JButton browseButton = new JButton(" Browse ");
    
    private JFileChooser chooser = new JFileChooser(".");
    
    private ApplicationMode connectionType;
    
    private String databaseLocation;
    
    private int port;
    
    protected ConnectionDialog(ApplicationMode type) {
        connectionType = type;
        addListeners();
        
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        
        if (type == ApplicationMode.STANDALONE_CLIENT) {
            this.setTitle("Connect to a local database");
            mainPanel.add(standAlonePanel());
        }
        else if (type == ApplicationMode.NETWORK_CLIENT) {
            this.setTitle("Connect to a remote database");
            mainPanel.add(networkClientPanel());
        }
        
        this.setModal(true);
        this.add(mainPanel);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setMinimumSize(this.getSize());
        this.setResizable(false);
        this.setVisible(true);
    }
    
    private void addListeners() {
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { 
                System.exit(0);
            }
        });

        browseButton.addActionListener(new BrowseButtonListener());
        exitButton.addActionListener(new ExitButtonListener());
        connectButton.addActionListener(new ConnectButtonListener());
        connectButton.setEnabled(false);
        
        DocumentListener listener = new ConnectButtonEnabler();
        locationTextField.getDocument().addDocumentListener(listener);
        portTextField.getDocument().addDocumentListener(listener);
    }
    
    private JPanel standAlonePanel() {
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c;
        
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
        locationTextField.setColumns(25);
        locationTextField.setEditable(true);
        panel.add(locationTextField, c);
        
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
    
    private class ExitButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
    
    private class ConnectButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
        	databaseLocation = locationTextField.getText();
            ConnectionDialog.this.dispose();
        }
    }
    
    private class ConnectButtonEnabler implements DocumentListener {

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
            
            if (connectionType == ApplicationMode.STANDALONE_CLIENT) {
                if (location.length() > MINIMUM_LOCATION_LENGTH) {
                    connectButton.setEnabled(true);
                }
                else {
                    connectButton.setEnabled(false);
                }
            }
            else if (connectionType == ApplicationMode.NETWORK_CLIENT) {
                if (location.length() > MINIMUM_LOCATION_LENGTH && validPort(port)) {
                    connectButton.setEnabled(true);
                }
                else {
                    connectButton.setEnabled(false);
                }
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
    
    public ApplicationMode getConnectionType() {
        return connectionType;
    }
    
    public String getDatabaseLocation() {
        return databaseLocation;
    }
    
    public int getPort() {
        return port;
    }

}
