package suncertify.presentation;

import java.awt.Container;

import javax.swing.JOptionPane;

/**
 * This class provides static methods to obtain 
 * <code>JOptionPane</code> information an error
 * dialogs.
 *
 * @author John Harding
 */
public class Dialogs {
    
    /**
     * Provides an error dialog.
     * @param parent the parent GUI element.
     * @param message the message to display.
     * @param title the title of the dialog.
     */
    public static void showErrorDialog(Container parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Provides an information dialog.
     * @param parent the parent GUI element.
     * @param message the message to display.
     * @param title the title of the dialog.
     */
    public static void showInfoDialog(Container parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static int showConfirmQuitDialog(Container parent) {
        int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit?",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        return response;
    }

}
