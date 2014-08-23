package suncertify.gui;

import suncertify.db.RecordNotFoundException;

public class GUIController {
	
	private BusinessModel businessModel;
	
	public GUIController() {
		
	}
	
	public GUIController(BusinessModel businessModel) {
		this.businessModel = businessModel;
	}
    
    public void book(Room room) throws RecordNotFoundException {
        businessModel.book(room);
    }
    
    public void search() {
        System.out.println("Search");
    }

}
