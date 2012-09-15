package twitminer.phase4.view;

import javax.swing.JOptionPane;

public class DialogMessage extends JOptionPane {
    private static final long serialVersionUID = -4555136056647233690L;

    public static void showInfoMessage(String msg) {
        JOptionPane.showMessageDialog(null,
                                      msg,
                                      "Information",
                                      JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void showWarningMessage(String msg) {
        JOptionPane.showMessageDialog(null,
                                      msg,
                                      "Warning",
                                      JOptionPane.WARNING_MESSAGE);
    }
    
    public static void showErrorMessage(String title, String msg) {
        JOptionPane.showMessageDialog(null,
                                      msg,
                                      title + " error",
                                      JOptionPane.ERROR_MESSAGE);
    }
    
}
