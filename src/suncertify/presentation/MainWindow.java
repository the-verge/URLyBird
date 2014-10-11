package suncertify.presentation;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.MaskFormatter;

import suncertify.db.DBException;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;
import suncertify.network.NetworkException;
 

public class MainWindow extends JFrame implements Observer {
	
	/**
     * The SUID
     */
    private static final long serialVersionUID = 1771L;
    
    private static final String EIGHT_DIGITS_MASK = "########";

    private BusinessService service;
    
    private SearchCriteria lastSearch = null;
    
	private RoomTableModel tableModel = new RoomTableModel();
	
	private JTable table;
	
	private JTextField nameTextField;
	
	private JTextField locationTextField;
	
	private JTextField customerIdTextField;
	
	private JButton searchButton;
	
	private JButton bookButton;
	
    public MainWindow(BusinessService businessService) {
        super("URLyBird");
        service = businessService;
        service.addObserver(this);
        createStripedTable();
        table.setSelectionBackground(Color.decode("#8AA37B"));
        setUpGUI();
        refreshTable();
    }
    
    @Override
    public void update(Observable arg0, Object arg1) {
        // TODO Auto-generated method stub
        tableModel.fireTableDataChanged();
    }
    
    private void createStripedTable() {
        
        table = new JTable(tableModel) {
            
            private static final long serialVersionUID = 2442L;

            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    comp.setBackground(getBackground());
                    if(row % 2 == 0) {
                        comp.setBackground(Color.decode("#E0EEEE"));
                    }
                }
                return comp;
            }
        };
    }
    
    private void setUpGUI() {
    	setDefaultCloseOperation(EXIT_ON_CLOSE);
    	addExitListener();
        JMenuBar menuBar = new JMenuBar();
        JMenu appMenu = new JMenu("Application");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new QuitActionListener());
        appMenu.add(exitMenuItem);
        menuBar.add(appMenu);
        setJMenuBar(menuBar);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        
        GridBagConstraints c;
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(createSideBarPanel(), c);
        
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        mainPanel.add(createTablePanel(), c);
        
        c = new GridBagConstraints();
        c.gridx = 1;
        c.ipadx = 4;
        c.ipady = 10;
        c.anchor = GridBagConstraints.EAST;
        mainPanel.add(createBookPanel(), c);
        
        add(mainPanel);
        setMinimumSize(new Dimension(750, 575));
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
    }
    
    private JPanel createSideBarPanel() {
    	JPanel panel = new JPanel(new GridBagLayout());
    	panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
    	
    	GridBagConstraints c;
        
        JLabel nameLabel = new JLabel("Name");
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(8, 8, 0, 0);
        c.ipady = 4;
        c.insets = new Insets(100, 5, 5, 5);
        panel.add(nameLabel, c);
        
        nameTextField = new JTextField(15);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.insets = new Insets(100, 5, 5, 5);
        panel.add(nameTextField, c);
        
        JLabel locationLabel = new JLabel("Location");
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        panel.add(locationLabel, c);
        
        locationTextField = new JTextField(15);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 2;
        panel.add(locationTextField, c);
        
        searchButton = new JButton("Search");
        searchButton.addActionListener(new SearchButtonListener());
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 3;
        c.weighty = 1.0;
        c.insets = new Insets(10, 5, 5, 5);
        c.anchor = GridBagConstraints.NORTHEAST;
        panel.add(searchButton, c);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
    	JPanel panel = new JPanel(new GridLayout(1,1));
    	panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        panel.add(new JScrollPane(table));
        
        return panel;
    }
    
    private JPanel createBookPanel() {
    	
    	JPanel panel = new JPanel(new GridBagLayout());
    	
    	GridBagConstraints c;
    	
    	JLabel customerIdLabel = new JLabel("Customer ID:");
    	c = new GridBagConstraints();
    	c.gridx = 0;
    	c.gridy = 0;
    	c.insets = new Insets(10, 0, 0, 15);
        panel.add(customerIdLabel, c);
        
        MaskFormatter eightDigits = null;
		try {
			eightDigits = new MaskFormatter(EIGHT_DIGITS_MASK);
			customerIdTextField = new JFormattedTextField(eightDigits);
		} catch (ParseException e1) {
			/**
			 * While catch blocks that swallow exceptions are a bad idea
			 * in this case I feel that it is acceptable given that the
			 * mask to be used is a pre-defined hard coded constant.  As
			 * a result ParseException should never be thrown. 
			 */
		}
		customerIdTextField.setColumns(10);
		c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(10, 0, 0, 25);
        panel.add(customerIdTextField, c);
        
        bookButton = new JButton("Book");
        bookButton.setEnabled(false);
        customerIdTextField.addKeyListener(new ButtonEnabler(customerIdTextField, bookButton, 8));
        bookButton.addActionListener(new BookButtonListener());
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 0;
        c.insets = new Insets(10, 0, 0, 25);
        c.anchor = GridBagConstraints.SOUTHEAST;
        panel.add(bookButton, c);

        return panel;
    }
    
    private void refreshTable() {
        Map<Integer, Room> rooms = new LinkedHashMap<Integer, Room>();
        SearchCriteria criteria;
        if (lastSearch == null) {
            criteria = new SearchCriteria();
            criteria.matchAllRecords();
        }
        else {
            criteria = lastSearch;
        }
		try {
		    rooms = service.searchRooms(criteria);
		    tableModel.setRoomMap(rooms);
		} catch (DBException e) {
            Dialogs.showErrorDialog(this, "Cannot show the latest bookings", "Database error");
        } catch (NetworkException e) {
            Dialogs.showErrorDialog(this, "Cannot show the latest bookings", "Network error");
        }
    }
    
    private void addExitListener() {
        addWindowListener(new WindowAdapter() {
            
            @Override
            public void windowClosing(WindowEvent e) {
                service.cleanUp();
            }
        });
    }
    
    private class QuitActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
		    service.cleanUp();
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
        
        MainWindow parent = MainWindow.this;
        
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
                matches = service.searchRooms(criteria);
            } catch (DBException ex) {
                Dialogs.showErrorDialog(parent, "Could not retrieve data", "Database error");
            } catch (NetworkException ex) {
                Dialogs.showErrorDialog(parent, "Could not retrieve data", "Network error");
            }
            lastSearch = criteria;
            tableModel.setRoomMap(matches);
        }
    }
    
    private class BookButtonListener implements ActionListener {
    	
    	MainWindow parent = MainWindow.this;
    	
		@Override
		public void actionPerformed(ActionEvent e) {
		    int selectedRowIndex = table.getSelectedRow();
	        String customerId = customerIdTextField.getText();
		    Room room = tableModel.getRoom(selectedRowIndex);
		    room.setOwner(customerId);
		    
		    try {
		        service.bookRoom(room);
            } catch (RecordNotFoundException ex) {
                Dialogs.showErrorDialog(parent, "Sorry, this room is no longer available", 
                            "Room already booked");
            } catch (SecurityException ex) {
                Dialogs.showErrorDialog(parent, "Could not complete booking", "Error");
            } catch (DBException ex) {
                Dialogs.showErrorDialog(parent, "Could not complete booking", "Database error");
            } catch (NetworkException ex) {
                Dialogs.showErrorDialog(parent, "Could not complete booking", "Network error");
            } finally {
                refreshTable();
            }
		    
		    customerIdTextField.setText("");
		    bookButton.setEnabled(false);
		}
    }
	
}