package suncertify.gui;

public class Application {
    
	public static void main(String[] args) {
		new Application().start(args);
	}
	
	private void start(String[] args) {
		String mode = args[0].trim();
		//String mode = "alone";
		
		if (mode.equalsIgnoreCase("server")) {
			showServerWindow();
		}
		else if (mode.equalsIgnoreCase("alone")) {
			showConnectionDialog(ApplicationMode.STANDALONE_CLIENT);
		}
		else if (mode.equalsIgnoreCase("")) {
			showConnectionDialog(ApplicationMode.NETWORK_CLIENT);
		}
		else {
			// Error dialog
		}
		
	}
	
	private void showConnectionDialog(ApplicationMode mode) {
		ConnectionDialog dialog = new ConnectionDialog(mode);
		String dbLocation = dialog.getDatabaseLocation();
		int port = dialog.getPort();
		createClient(dbLocation);
	}
	
	private void showServerWindow() {
		new ServerWindow();
	}
	
	private void createClient(String dbLocation) {
		BusinessModel model = new BusinessModel(dbLocation);
	    MainWindow view = new MainWindow(model);
	    GUIController controller = new GUIController(model, view);
	}

}
