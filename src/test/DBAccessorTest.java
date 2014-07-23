package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import suncertify.db.DBAccessor;
import suncertify.db.Room;

import org.junit.Before;
import org.junit.Test;

public class DBAccessorTest {
    
    static final int RECORD_LENGTH = 160;
    
    DBAccessor accessor = new DBAccessor("/Users/john/workspace/URLyBird/db-1x3.db");
    
    RandomAccessFile database = accessor.getDatabase();

    @Before
    public void setUp() throws Exception {
        
    }

    @Test
    public void readRecordTest() {
        String[] data;
        
        data = accessor.readRecord(1);
        assertEquals(data[0], "Dew Drop Inn");
        assertEquals(data[1], "Smallville");
        assertEquals(data[2], "4");
        assertEquals(data[3], "Y");
        assertEquals(data[4], "$210.00");
        assertEquals(data[5], "2005/05/02");
        assertEquals(data[6], "");
        
        data = accessor.readRecord(2);
        assertEquals(data[0], "Elephant Inn");
        assertEquals(data[1], "Smallville");
        assertEquals(data[2], "4");
        assertEquals(data[3], "Y");
        assertEquals(data[4], "$160.00");
        assertEquals(data[5], "2004/05/06");
        assertEquals(data[6], "");
        
    }
    
    @Test
    public void createRecordTest() throws IOException {
        // For now, record always appended to end of file
        long initialFileLength = database.length();
        String[] data = {"Newgrange", "Kildare", "4", "Y", "$250.54", "2014/07/21", "12345678"};
        accessor.createRecord(data);
        assertEquals(initialFileLength + RECORD_LENGTH, database.length());
        
        String[] newRecord = accessor.readRecord(31);
        
        assertEquals(newRecord[0], "Newgrange");
        assertEquals(newRecord[1], "Kildare");
        assertEquals(newRecord[2], "4");
        assertEquals(newRecord[3], "Y");
        assertEquals(newRecord[4], "$250.54");
        assertEquals(newRecord[5], "2014/07/21");
        assertEquals(newRecord[6], "12345678");
    }
    
    @Test
    public void deleteRecordTest() {
        accessor.deleteRecord(1, 1L);
    }
    
    @Test
    public void updateRecordTest() {
        String[] updatedData = {"The Mews", "Donegal", "4", "Y", "$250.54", "2014/07/21", "12345678"};
        accessor.updateRecord(30, updatedData, 1L);
        
        String[] updatedRecord = accessor.readRecord(30);
        
        assertEquals(updatedRecord[0], "The Mews");
        assertEquals(updatedRecord[1], "Donegal");
        assertEquals(updatedRecord[2], "4");
        assertEquals(updatedRecord[3], "Y");
        assertEquals(updatedRecord[4], "$250.54");
        assertEquals(updatedRecord[5], "2014/07/21");
        assertEquals(updatedRecord[6], "12345678");
    }
    
    @Test
    public void matchRecordTest() {
//        String[] criteria = {"Dew", "Smallville", "4", "Y", "", "2005/05/02", null};
//        boolean match = accessor.matchRecord(null, criteria);
//        assertEquals(match, true);
    }

}
