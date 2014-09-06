package test.gui;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import suncertify.db.RecordNotFoundException;
import suncertify.gui.BusinessModel;
import suncertify.gui.Room;
import suncertify.gui.RoomTableModel;
import suncertify.gui.SearchCriteria;

public class RoomTableModelTest {
    
    @Before
    public void setUp() throws Exception {
        
    }

    @Test
    public void getValueAtTest() throws RecordNotFoundException {
        BusinessModel model = new BusinessModel();
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
