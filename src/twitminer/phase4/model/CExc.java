package twitminer.phase4.model;

import twitminer.phase4.view.DialogMessage;

public class CExc extends Exception {
    private static final long serialVersionUID = 5935487267428528001L;

    private String title;
    private String  message;
    
    public CExc(String title, String message) {
        super();
        this.title = title;
        this.message = message;
    }
    
    public void show() {
        DialogMessage.showErrorMessage(title, message);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
}
