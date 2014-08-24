package test.gui;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import suncertify.db.RecordNotFoundException;
import suncertify.gui.Room;
import suncertify.gui.BusinessModel;
import suncertify.gui.SearchCriteria;

public class BusinessModelTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void searchAllRoomsTest() throws RecordNotFoundException {
        BusinessModel model = new BusinessModel();
        SearchCriteria criteria = new SearchCriteria();
        criteria.matchAllRecords();
        Map<Integer, Room> matches = model.searchRooms(criteria);
        assertEquals(30, matches.size());
    }
    
    @Test
    public void searchRoomsTest() throws RecordNotFoundException {
        BusinessModel model = new BusinessModel();
        SearchCriteria criteria = new SearchCriteria();
        criteria.matchName("dew");
        Map<Integer, Room> matches = model.searchRooms(criteria);
        assertEquals(3, matches.size());
        int count = 0;
        criteria.matchName("w");
        matches = model.searchRooms(criteria);
        assertEquals(1, matches.size());
    }

}
