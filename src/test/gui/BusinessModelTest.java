package test.gui;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import suncertify.db.Data;
import suncertify.db.RecordNotFoundException;
import suncertify.application.BusinessService;
import suncertify.application.Room;
import suncertify.application.SearchCriteria;

public class BusinessModelTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void searchAllRoomsTest() throws RecordNotFoundException {
    	Data data = new Data("/home/ejhnhng/URLyBird/db-1x3.db");
        BusinessService model = new BusinessService(data);
        SearchCriteria criteria = new SearchCriteria();
        criteria.matchAllRecords();
        Map<Integer, Room> matches = model.searchRooms(criteria);
        assertEquals(30, matches.size());
    }
    
    @Test
    public void searchRoomsTest() throws RecordNotFoundException {
    	Data data = new Data("/home/ejhnhng/URLyBird/db-1x3.db");
        BusinessService model = new BusinessService(data);
        SearchCriteria criteria = new SearchCriteria();
        criteria.matchName("dew");
        Map<Integer, Room> matches = model.searchRooms(criteria);
        assertEquals(3, matches.size());
        criteria.matchName("w");
        matches = model.searchRooms(criteria);
        assertEquals(1, matches.size());
    }

}
