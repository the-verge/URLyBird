package suncertify.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
 

public class MainWindow extends JFrame {
	
	private RecordTableModel tableModel = new RecordTableModel();
	
	private JTable table = new JTable(tableModel);
	
	private Controller controller = new Controller();
    
    public static void main(String[] args) {
        new MainWindow();
    }
    
    public MainWindow() {
        super("URLyBird");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JMenuBar menuBar = new JMenuBar();
        JMenu appMenu = new JMenu("Application");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        
        exitMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
            
        });
        
        exitMenuItem.setMnemonic(KeyEvent.VK_Q);
        appMenu.add(exitMenuItem);
        //appMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(appMenu);
        this.setJMenuBar(menuBar);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        
        GridBagConstraints sidebarConstraints = new GridBagConstraints();
        sidebarConstraints.gridx = 0;
        sidebarConstraints.gridy = 0;
//        sidebarConstraints.weighty = 1.0;
//        sidebarConstraints.gridheight = GridBagConstraints.REMAINDER;
        sidebarConstraints.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(this.SideBarPanel(), sidebarConstraints);
        
        GridBagConstraints tablePanelConstraints = new GridBagConstraints();
        tablePanelConstraints.gridx = 1;
        tablePanelConstraints.gridy = 0;
        tablePanelConstraints.weightx = 1.0;
        tablePanelConstraints.weighty = 1.0;
        tablePanelConstraints.fill = GridBagConstraints.BOTH;
        mainPanel.add(this.TablePanel(), tablePanelConstraints);
        
        GridBagConstraints bookPanelConstraints = new GridBagConstraints();
        bookPanelConstraints.gridx = 1;
        //bookPanelConstraints.gridy = 2;
        bookPanelConstraints.ipadx = 4;
        bookPanelConstraints.ipady = 10;
        bookPanelConstraints.anchor = GridBagConstraints.EAST;
        //bookPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(this.BookPanel(), bookPanelConstraints);
        
        this.add(mainPanel);
        this.setMinimumSize(new Dimension(750, 575));
        this.setSize(1200, 700);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
    }
    
    private class SideBarPanel extends JPanel {
        
        JLabel nameLabel;
        
        JTextField nameTextField;
        
        JLabel locationLabel;
        
        JTextField locationTextField;
        
        JButton searchButton;
        
        SideBarPanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            
            GridBagConstraints nameLabelConstraints = new GridBagConstraints();
            nameLabel = new JLabel("Name");
            nameLabelConstraints.gridx = 0;
            nameLabelConstraints.gridy = 1;
            nameLabelConstraints.insets = new Insets(8, 8, 0, 0);
            nameLabelConstraints.ipady = 4;
            nameLabelConstraints.insets = new Insets(100, 5, 5, 5);
            panel.add(nameLabel, nameLabelConstraints);
            
            GridBagConstraints nameFieldConstraints = new GridBagConstraints();
            nameTextField = new JTextField(15);
            nameFieldConstraints.gridx = 1;
            nameFieldConstraints.gridy = 1;
            nameFieldConstraints.insets = new Insets(100, 5, 5, 5);
            panel.add(nameTextField, nameFieldConstraints);
            
            GridBagConstraints locationLabelConstraints = new GridBagConstraints();
            locationLabel = new JLabel("Location");
            locationLabelConstraints.gridx = 0;
            locationLabelConstraints.gridy = 2;
            panel.add(locationLabel, locationLabelConstraints);
            
            GridBagConstraints locationFieldConstraints = new GridBagConstraints();
            locationTextField = new JTextField(15);
            locationFieldConstraints.gridx = 1;
            locationFieldConstraints.gridy = 2;
            panel.add(locationTextField, locationFieldConstraints);
            
            GridBagConstraints searchButtonConstraints = new GridBagConstraints();
            searchButton = new JButton("Search");
            searchButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    controller.search();
                }
                
            });
            searchButtonConstraints.gridx = 1;
            searchButtonConstraints.gridy = 3;
            searchButtonConstraints.weighty = 1.0;
            searchButtonConstraints.insets = new Insets(10, 5, 5, 5);
            searchButtonConstraints.anchor = GridBagConstraints.SOUTHEAST;
            panel.add(searchButton, searchButtonConstraints);
            
            this.add(panel);
            this.setVisible(true);
        }
        
    }
    
    private JPanel SideBarPanel() {
    	JPanel panel = new JPanel(new GridBagLayout());
    	panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        
    	GridBagConstraints nameLabelConstraints = new GridBagConstraints();
        JLabel nameLabel = new JLabel("Name");
        nameLabelConstraints.gridx = 0;
        nameLabelConstraints.gridy = 1;
        nameLabelConstraints.insets = new Insets(8, 8, 0, 0);
        nameLabelConstraints.ipady = 4;
        nameLabelConstraints.insets = new Insets(100, 5, 5, 5);
        panel.add(nameLabel, nameLabelConstraints);
        
        GridBagConstraints nameFieldConstraints = new GridBagConstraints();
        JTextField nameTextField = new JTextField(15);
        nameFieldConstraints.gridx = 1;
        nameFieldConstraints.gridy = 1;
        nameFieldConstraints.insets = new Insets(100, 5, 5, 5);
        panel.add(nameTextField, nameFieldConstraints);
        
        GridBagConstraints locationLabelConstraints = new GridBagConstraints();
        JLabel locationLabel = new JLabel("Location");
        locationLabelConstraints.gridx = 0;
        locationLabelConstraints.gridy = 2;
        panel.add(locationLabel, locationLabelConstraints);
        
        GridBagConstraints locationFieldConstraints = new GridBagConstraints();
        JTextField locationTextField = new JTextField(15);
        locationFieldConstraints.gridx = 1;
        locationFieldConstraints.gridy = 2;
        panel.add(locationTextField, locationFieldConstraints);
        
        GridBagConstraints searchButtonConstraints = new GridBagConstraints();
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                controller.search();
            }
            
        });
        searchButtonConstraints.gridx = 1;
        searchButtonConstraints.gridy = 3;
        searchButtonConstraints.weighty = 1.0;
        searchButtonConstraints.insets = new Insets(10, 5, 5, 5);
        searchButtonConstraints.anchor = GridBagConstraints.NORTHEAST;
        panel.add(searchButton, searchButtonConstraints);
        
        return panel;
    }
    
    private JPanel TablePanel() {
    	JPanel panel = new JPanel(new GridLayout(1,1));
    	panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        panel.add(new JScrollPane(table));
        
        return panel;
    }
    
    private JPanel BookPanel() {
    	
    	JPanel panel = new JPanel(new GridBagLayout());
    	//panel.setBorder(BorderFactory.createLineBorder(Color.RED));
    	
    	GridBagConstraints customerLabelConstraints = new GridBagConstraints();
    	JLabel customerIdLabel = new JLabel("Customer ID:");
    	customerLabelConstraints.gridx = 0;
    	customerLabelConstraints.gridy = 0;
    	customerLabelConstraints.insets = new Insets(10, 0, 0, 15);
        panel.add(customerIdLabel, customerLabelConstraints);
        
        GridBagConstraints customerFieldConstraints = new GridBagConstraints();
        JTextField customerIdTextField = new JTextField(8);
        
        customerFieldConstraints.gridx = 1;
        customerFieldConstraints.gridy = 0;
        customerFieldConstraints.insets = new Insets(10, 0, 0, 25);
        panel.add(customerIdTextField, customerFieldConstraints);
        
        GridBagConstraints bookButtonConstraints = new GridBagConstraints();
        JButton bookButton = new JButton("Book");
        customerIdTextField.addKeyListener(new BookingListener(customerIdTextField, bookButton));
        bookButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                controller.book();
            }
            
        });
        bookButtonConstraints.gridx = 2;
        bookButtonConstraints.gridy = 0;
        bookButtonConstraints.insets = new Insets(10, 0, 0, 25);
        bookButtonConstraints.anchor = GridBagConstraints.SOUTHEAST;
        panel.add(bookButton, bookButtonConstraints);
        
        return panel;
    }
    
    private class BookingListener implements KeyListener {
        
        JTextField text;
        
        JButton button;
        
        BookingListener(JTextField text, JButton button) {
            this.text = text;
            this.button = button;
        }

        @Override
        public void keyTyped(KeyEvent e) {
            
        }

        @Override
        public void keyPressed(KeyEvent e) {
            
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if(this.text.getText().length() == 8) {
                this.button.setEnabled(true);
            }
            else {
                this.button.setEnabled(false);
            }
        }
        
    }
    
}
