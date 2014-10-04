package suncertify.presentation;

import java.awt.Container;

import javax.swing.JOptionPane;

public class Dialogs {
    
    public static void showErrorDialog(Container parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showInfoDialog(Container parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

}
