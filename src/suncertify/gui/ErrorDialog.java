package suncertify.gui;

import java.awt.Container;
import javax.swing.JOptionPane;

public class ErrorDialog {
    
    public static void showDialog(Container parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

}
