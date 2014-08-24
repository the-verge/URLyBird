package suncertify.gui;

import suncertify.db.RecordNotFoundException;

public class GUIController {
	
	private BusinessModel model;
	
	private MainWindow view;
	
	public GUIController(BusinessModel model, MainWindow view) {
	    this.model = model;
	    this.view = view;
	    view.addUserGestureListener(this);
	}
	
    public void book(Room room) throws RecordNotFoundException {
        model.book(room);
    }
    
    public void search(SearchCriteria criteria) {
        
    }

}
