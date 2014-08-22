package suncertify.gui;

public class GUIController {
	
	private BusinessModel businessModel;
	
	public GUIController() {
		
	}
	
	public GUIController(BusinessModel businessModel) {
		this.businessModel = businessModel;
	}
    
    public void book() {
        System.out.println("Book");
    }
    
    public void search() {
        System.out.println("Search");
    }

}
