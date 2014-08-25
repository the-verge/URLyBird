package suncertify.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

/**
 * ALL OF THIS CONTENT SHOULD BE IN A DIALOG!!
 * @author john
 *
 */
public class ConnectionDialog extends JFrame {
    
    private JDialog dialog;
    
    private JLabel locationLabel = new JLabel("Database location");
    
    private JLabel portLabel = new JLabel("Port");
    
    private JLabel hostLabel = new JLabel("Hostname");
    
    private JTextField locationTextField = new JTextField();
    
    // should be formatted text field
    private JTextField portTextField = new JTextField();
    
    private JButton connectButton = new JButton("Connect");
    
    private JButton exitButton = new JButton("Exit");
    
    private JButton browseButton = new JButton(" Browse ");
    
    private JFileChooser chooser = new JFileChooser(".");
    
    private String connectionType;
    
    private String databaseLocation;
    
    private int port;
    
    public static void main(String[] args) {
        new ConnectionDialog("network");
        new ConnectionDialog("alone");
    }
    
    protected ConnectionDialog(String type) {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        
        if (type == "alone") {
            this.setTitle("Connect to a local database");
            mainPanel.add(StandAlonePanel());
        }
        else if (type == "network") {
            this.setTitle("Connect to a remote database");
            mainPanel.add(NetworkClientPanel());
        }
        
        this.add(mainPanel);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setMinimumSize(this.getSize());
        this.setResizable(false);
    }
    
    private JPanel StandAlonePanel() {
        
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
        panel.add(locationTextField, c);
        
        c = new GridBagConstraints();
        c.gridx = 3;
        c.gridy = 0;
        browseButton.addActionListener(new BrowseButtonListener());
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
    
    private JPanel NetworkClientPanel() {
        
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
        portTextField.setColumns(8);
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
            int returnVal = parent.chooser.showOpenDialog(parent);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try {
                    databaseLocation = file.getCanonicalPath();
                    locationTextField.setText(databaseLocation);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
             }
        }
        
    }
    
    protected String getConnectionType() {
        return connectionType;
    }
    
    protected String getDatabaseLocation() {
        return databaseLocation;
    }
    
    public int getPort() {
        return port;
    }

}
