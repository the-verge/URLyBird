package suncertify.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.MaskFormatter;


public class ServerWindow extends JFrame {
    
    private static final int LOWEST_VALID_PORT = 1025;
    
    private static final int HIGHEST_VALID_PORT = 65535;
    
    private static final int MINIMUM_LOCATION_LENGTH = 6;
    
    private JLabel locationLabel = new JLabel("Database location");
    
    private JLabel portLabel = new JLabel("Port");
    
    private JTextField locationTextField = new JTextField();
    
    private JTextField portTextField;
    
    private JButton startButton = new JButton("Start Server");
    
    private JButton exitButton = new JButton("Exit");
    
    private JButton browseButton = new JButton(" Browse ");
    
    private JFileChooser chooser = new JFileChooser(".");
    
    private String databaseLocation;
    
    private int port;
    
    public static void main(String[] args) {
        new ServerWindow();
    }
    
    protected ServerWindow() {
        super("URLyBird Server");
        addListeners();
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        mainPanel.add(serverPanel());
        
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
        startButton.addActionListener(new StartButtonListener());
        startButton.setEnabled(false);
        
        DocumentListener listener = new StartButtonEnabler();
        locationTextField.getDocument().addDocumentListener(listener);
        initialisePortTextField();
        portTextField.getDocument().addDocumentListener(listener);
    }
    
    private void initialisePortTextField() {
        MaskFormatter fiveDigits = null;
        try {
            fiveDigits = new MaskFormatter("#####");
            portTextField = new JFormattedTextField(fiveDigits);
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
        
    private JPanel serverPanel() {
        
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
    
    private class BrowseButtonListener implements ActionListener {
        
        ServerWindow parent = ServerWindow.this;
        
        @Override
        public void actionPerformed(ActionEvent e) {
            int returnVal = parent.chooser.showOpenDialog(parent);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try {
                    databaseLocation = file.getCanonicalPath();
                    locationTextField.setText(databaseLocation);
                    startButton.setEnabled(true);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
             }
        }
    }
    
    private class ExitButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
    
    private class StartButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO
            System.out.println(locationTextField.getText());
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
                return true;
            }        
        
        return false;
    }
    
    protected String getDatabaseLocation() {
        return databaseLocation;
    }
    
    public int getPort() {
        return port;
    }

}
