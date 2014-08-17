package suncertify.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;


public class MainWindow extends JFrame {
    
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
        this.add(new SideBar(), BorderLayout.WEST);
        this.pack();
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    
    private class SideBar extends JPanel {
        
        public SideBar() {
            this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            //c.fill = GridBagConstraints.VERTICAL;
            
            JLabel nameLabel = new JLabel("Name");
            c.gridx = 0;
            c.gridy = 0;
            c.anchor = GridBagConstraints.FIRST_LINE_START;
            c.insets = new Insets(8, 8, 0, 0);
            c.ipady = 4;
            this.add(nameLabel, c);
            
            JTextField nameTextField = new JTextField(15);
//            c.ipady = 0;
            c.gridx = 1;
            c.gridy = 0;
            this.add(nameTextField, c);
            
            JLabel locationLabel = new JLabel("Location");
            c.gridx = 0;
            c.gridy = 1;
            this.add(locationLabel, c);
            
            JTextField locationTextField = new JTextField(15);
            c.gridx = 1;
            c.gridy = 1;
            this.add(locationTextField, c);
            
            JButton searchButton = new JButton("Search");
            c.gridx = 1;
            c.gridy = 2;
            c.weighty = 1.0;
            this.add(searchButton, c);
            
        }
    }
    

}
