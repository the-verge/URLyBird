package suncertify.gui;

public class ApplicationRunner {
    
    private BusinessModel model = new BusinessModel();
    
    private MainWindow view = new MainWindow(model);
    
    private GUIController controller = new GUIController(model, view);
    
	public static void main(String[] args) {
	    new ApplicationRunner();
	}

}
