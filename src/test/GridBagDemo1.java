package test;

import javax.swing.*;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class GridBagDemo1 extends JFrame {
	 
    public GridBagDemo1(){
        initGUI();
    }
 
    public void initGUI() {
 
        setTitle("");
 
        JPanel panel = new JPanel(new GridBagLayout());
        this.getContentPane().add(panel);
 
        JTable t = new JTable(null);
 
        JLabel label = new JLabel("My Things");
 
        JPanel tableButtonPanel = new JPanel();
        tableButtonPanel.add(new JButton("Add Thing"));
        tableButtonPanel.add(new JButton("Delete Thing"));
        tableButtonPanel.add(new JButton("Modify Thing"));
 
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(new JButton("Print"));
        buttonPanel.add(new JButton("History"));
        buttonPanel.add(new JButton("Preferences"));
        buttonPanel.add(new JButton("Another Button"));
        buttonPanel.add(new JButton("Add Another"));
        buttonPanel.add(new JButton("Yet Another"));
 
        JPanel detailsPanel = new JPanel();
        detailsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
 
        GridBagConstraints gbc = new GridBagConstraints();
 
        gbc.gridx = 0;
        gbc.gridy = 0;
 
        panel.add(label, gbc);
 
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JScrollPane(t), gbc);
 
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(tableButtonPanel, gbc);
 
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
 
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        panel.add(detailsPanel, gbc);
 
        this.pack();
 
        this.setVisible(true);
    }
 
    public static void main(String[] args) {
        GridBagDemo1 frame = new GridBagDemo1();
 
        frame.pack();
        frame.setVisible(true);
    }
}