package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import suncertify.db.DBAccessor;
import suncertify.db.Room;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DBAccessorTest {
    
    static final int RECORD_LENGTH = 160;
    
    static DBAccessor accessor = new DBAccessor("/Users/john/workspace/URLyBird/db-1x3.db");
    
    static RandomAccessFile database = accessor.getDatabase();
    
    static long initialDatabaseLength;
    
    @BeforeClass
    public static void getInitialDBLength() throws IOException {
        initialDatabaseLength = database.length();
        assertEquals(initialDatabaseLength, 4856);
    }
    
    @AfterClass
    public static void resetDB() throws IOException {
        String[] resetData = {"Bed & Breakfast & Business", "Lendmarch", "6", "Y", "$170.00", "2005/03/10", ""};
        accessor.updateRecord(30, resetData, 1L);
        String[] data = accessor.readRecord(30);
        
        assertEquals("Bed & Breakfast & Business", data[0]);
        assertEquals("Lendmarch", data[1]);
        assertEquals("6", data[2]);
        assertEquals("Y", data[3]);
        assertEquals("$170.00", data[4]);
        assertEquals("2005/03/10", data[5]);
        assertEquals("", data[6]);
        
        database.setLength(initialDatabaseLength);
        assertEquals(4856, database.length());
    }

    @Before
    public void setUp() throws Exception {
        
    }

    @Test
    public void readRecordTest() {
        String[] data;
        
        data = accessor.readRecord(1);
        assertEquals("Dew Drop Inn", data[0]);
        assertEquals("Smallville", data[1]);
        assertEquals("4", data[2]);
        assertEquals("Y", data[3]);
        assertEquals("$210.00", data[4]);
        assertEquals("2005/05/02", data[5]);
        assertEquals("", data[6]);
        
        data = accessor.readRecord(2);
        assertEquals("Elephant Inn", data[0]);
        assertEquals("Smallville", data[1]);
        assertEquals("4", data[2]);
        assertEquals("Y", data[3]);
        assertEquals("$160.00", data[4]);
        assertEquals("2004/05/06", data[5]);
        assertEquals("", data[6]);
        
    }
    
    @Test
    public void createRecordTest() throws IOException {
        // For now, record always appended to end of file
        long initialFileLength = database.length();
        String[] data = {"Newgrange", "Kildare", "4", "Y", "$250.54", "2014/07/21", "12345678"};
        int recordNumber = accessor.createRecord(data);
        assertEquals(31, recordNumber);
        assertEquals(initialFileLength + RECORD_LENGTH, database.length());
        
        String[] newRecord = accessor.readRecord(recordNumber);
        
        assertEquals("Newgrange", data[0]);
        assertEquals("Kildare", data[1]);
        assertEquals("4", data[2]);
        assertEquals("Y", data[3]);
        assertEquals("$250.54", data[4]);
        assertEquals("2014/07/21", data[5]);
        assertEquals("12345678", data[6]);
    }
    
    @Test
    public void deleteRecordTest() {
        //accessor.deleteRecord(1, 1L);
    }
    
    @Test
    public void updateRecordTest() {
        String[] updatedData = {"The Mews", "Donegal", "4", "Y", "$250.54", "2014/07/21", "12345678"};
        accessor.updateRecord(30, updatedData, 1L);
        
        String[] updatedRecord = accessor.readRecord(30);
        
        assertEquals("The Mews", updatedRecord[0]);
        assertEquals("Donegal", updatedRecord[1]);
        assertEquals("4", updatedRecord[2]);
        assertEquals("Y", updatedRecord[3]);
        assertEquals("$250.54", updatedRecord[4]);
        assertEquals("2014/07/21", updatedRecord[5]);
        assertEquals("12345678", updatedRecord[6]);
        
    }
    
    @Test
    public void updateWithNullFieldsTest() {
        String[] updatedData = {"The Mews", null, null, null, null, null, null};
        accessor.updateRecord(30, updatedData, 1L);
        String[] data = accessor.readRecord(30);
        
        assertEquals("The Mews", data[0]);
        assertEquals("Lendmarch", data[1]);
//        assertEquals("6", data[2]);
//        assertEquals("Y", data[3]);
//        assertEquals("$170.00", data[4]);
//        assertEquals("2005/03/10", data[5]);
//        assertEquals("", data[6]);
    }
    
    @Test
    public void matchRecordTest() {
        String[] criteria = {null, "sMaLlViLl", "4", "Y", "$210.0", "2005/05/02", null};
        boolean match = accessor.matchRecord(null, criteria);
        assertTrue(match);
    }
    
    @Test
    public void matchNullCriteriaTest() {
        String[] nullCriteria = {null, null, null, null, null, null, null};
        boolean match = accessor.matchRecord(null, nullCriteria);
        assertFalse(match);
    }
    
}
