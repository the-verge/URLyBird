package test.gui;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import suncertify.db.Data;
import suncertify.db.RecordNotFoundException;
import suncertify.application.BusinessService;
import suncertify.application.Room;
import suncertify.presentation.RoomTableModel;
import suncertify.application.SearchCriteria;

public class RoomTableModelTest {
    
    @Before
    public void setUp() throws Exception {
        
    }

    @Test
    public void getValueAtTest() throws RecordNotFoundException {
    	Data data = new Data("/home/ejhnhng/URLyBird/db-1x3.db");
        BusinessService model = new BusinessService(data);
        SearchCriteria criteria = new SearchCriteria();
        RoomTableModel tableModel = new RoomTableModel();
        
        criteria.matchName("Dew");
        Map<Integer, Room> matches = model.searchRooms(criteria);
        tableModel.setRoomMap(matches);
        
        assertEquals("Dew Drop Inn", tableModel.getValueAt(0, 0));
        assertEquals("Whoville", tableModel.getValueAt(1, 1));
        assertEquals("6", tableModel.getValueAt(2, 2));
    }

}
