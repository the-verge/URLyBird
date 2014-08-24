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
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.text.MaskFormatter;

import suncertify.db.DBException;
import suncertify.db.RecordNotFoundException;
 

public class MainWindow extends JFrame implements Observer {
	
	private BusinessModel businessModel;
	
	private GUIController guiController = new GUIController();
	
	private RoomTableModel tableModel = new RoomTableModel();
	
	private JTable table = new JTable(tableModel);
	
	private JTextField nameTextField;
	
	private JTextField locationTextField;
	
	private JTextField customerIdTextField;
	
	private JButton searchButton;
	
	private JButton bookButton;
	
	public static void main(String[] args) {
		new MainWindow(new BusinessModel());
	}
    
    public MainWindow(BusinessModel businessModel) {
        super("URLyBird");
        this.businessModel = businessModel;
        this.businessModel.addObserver(this);
        setUpGUI();
        setUpTable();
    }
    
    private void setUpGUI() {
    	setDefaultCloseOperation(EXIT_ON_CLOSE);
        JMenuBar menuBar = new JMenuBar();
        JMenu appMenu = new JMenu("Application");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new QuitActionListener());
        appMenu.add(exitMenuItem);
        menuBar.add(appMenu);
        this.setJMenuBar(menuBar);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        
        GridBagConstraints sidebarConstraints = new GridBagConstraints();
        sidebarConstraints.gridx = 0;
        sidebarConstraints.gridy = 0;
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
        bookPanelConstraints.ipadx = 4;
        bookPanelConstraints.ipady = 10;
        bookPanelConstraints.anchor = GridBagConstraints.EAST;
        mainPanel.add(this.BookPanel(), bookPanelConstraints);
        
        this.add(mainPanel);
        this.setMinimumSize(new Dimension(750, 575));
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
    }
    
    private JPanel SideBarPanel() {
    	JPanel panel = new JPanel(new GridBagLayout());
    	panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        
        JLabel nameLabel = new JLabel("Name");
        GridBagConstraints nameLabelConstraints = new GridBagConstraints();
        nameLabelConstraints.gridx = 0;
        nameLabelConstraints.gridy = 1;
        nameLabelConstraints.insets = new Insets(8, 8, 0, 0);
        nameLabelConstraints.ipady = 4;
        nameLabelConstraints.insets = new Insets(100, 5, 5, 5);
        panel.add(nameLabel, nameLabelConstraints);
        
        this.nameTextField = new JTextField(15);
        GridBagConstraints nameFieldConstraints = new GridBagConstraints();
        nameFieldConstraints.gridx = 1;
        nameFieldConstraints.gridy = 1;
        nameFieldConstraints.insets = new Insets(100, 5, 5, 5);
        panel.add(nameTextField, nameFieldConstraints);
        
        JLabel locationLabel = new JLabel("Location");
        GridBagConstraints locationLabelConstraints = new GridBagConstraints();
        locationLabelConstraints.gridx = 0;
        locationLabelConstraints.gridy = 2;
        panel.add(locationLabel, locationLabelConstraints);
        
        this.locationTextField = new JTextField(15);
        GridBagConstraints locationFieldConstraints = new GridBagConstraints();
        locationFieldConstraints.gridx = 1;
        locationFieldConstraints.gridy = 2;
        panel.add(locationTextField, locationFieldConstraints);
        
        this.searchButton = new JButton("Search");
        searchButton.addActionListener(new SearchButtonListener());
        GridBagConstraints searchButtonConstraints = new GridBagConstraints();
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
    	
    	JLabel customerIdLabel = new JLabel("Customer ID:");
    	GridBagConstraints customerLabelConstraints = new GridBagConstraints();
    	customerLabelConstraints.gridx = 0;
    	customerLabelConstraints.gridy = 0;
    	customerLabelConstraints.insets = new Insets(10, 0, 0, 15);
        panel.add(customerIdLabel, customerLabelConstraints);
        
        MaskFormatter eightDigits = null;
		try {
			eightDigits = new MaskFormatter("########");
			this.customerIdTextField = new JFormattedTextField(eightDigits);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		customerIdTextField.setColumns(10);
		GridBagConstraints customerFieldConstraints = new GridBagConstraints();
        customerFieldConstraints.gridx = 1;
        customerFieldConstraints.gridy = 0;
        customerFieldConstraints.insets = new Insets(10, 0, 0, 25);
        panel.add(customerIdTextField, customerFieldConstraints);
        
        bookButton = new JButton("Book");
        bookButton.setEnabled(false);
        customerIdTextField.addKeyListener(new ButtonEnabler(customerIdTextField, bookButton, 8));
        bookButton.addActionListener(new BookButtonListener());
        GridBagConstraints bookButtonConstraints = new GridBagConstraints();
        bookButtonConstraints.gridx = 2;
        bookButtonConstraints.gridy = 0;
        bookButtonConstraints.insets = new Insets(10, 0, 0, 25);
        bookButtonConstraints.anchor = GridBagConstraints.SOUTHEAST;
        panel.add(bookButton, bookButtonConstraints);
        
        return panel;
    }
    
    private void setUpTable() {
        Map<Integer, Room> allRooms = new LinkedHashMap<Integer, Room>();
        SearchCriteria criteria = new SearchCriteria();
        criteria.matchAllRecords();
		try {
		    allRooms = businessModel.searchRooms(criteria);
		    tableModel.setRoomMap(allRooms);
		} catch (DBException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		} catch (RecordNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private class QuitActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
    }
    
    private class ButtonEnabler implements KeyListener {
    	
    	JTextField textField;
    	
    	JButton button;
    	
    	int inputLength;
    	
    	ButtonEnabler(JTextField textField, JButton button, int inputLength) {
    		this.textField = textField;
    		this.button = button;
    		this.inputLength = inputLength;
    	}

		@Override
		public void keyReleased(KeyEvent arg0) {
			String input = this.textField.getText().trim();
			boolean inputIsLongEnough = input.length() >= this.inputLength;
			boolean aRowIsSelected = table.getSelectedRow() != -1;
			
			if (inputIsLongEnough && aRowIsSelected) {
				this.button.setEnabled(true);
			}
			else {
				this.button.setEnabled(false);
			}
		}
		
		@Override
		public void keyPressed(KeyEvent arg0) {}

		@Override
		public void keyTyped(KeyEvent arg0) {}
    	
    }
    
    private class SearchButtonListener implements ActionListener {

		@Override
        public void actionPerformed(ActionEvent e) {
            String name = nameTextField.getText().trim();
            String location = locationTextField.getText().trim();
            Map<Integer, Room> matches = new LinkedHashMap<Integer, Room>();
            SearchCriteria criteria = new SearchCriteria();
            
            if (name.equals("") && location.equals("")) {
                criteria.matchAllRecords();
            }
            else {
                criteria.matchName(name)    
                        .matchLocation(location);
            }
            
            try {
                matches = businessModel.searchRooms(criteria);
            } catch (DBException e1) {
                JOptionPane.showMessageDialog(MainWindow.this, e1.getMessage());
            } catch (RecordNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            
            tableModel.setRoomMap(matches);
        }
    }
    
    private class BookButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
		    int selectedRowIndex = table.getSelectedRow();
	        String customerId = customerIdTextField.getText();
		    Room room = tableModel.getRoom(selectedRowIndex);
		    room.setOwner(customerId);
		    
		    try {
                businessModel.book(room);
            } catch (RecordNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
		    customerIdTextField.setText("");
		    bookButton.setEnabled(false);
		}
    }

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		tableModel.fireTableDataChanged();
	}
    
}
