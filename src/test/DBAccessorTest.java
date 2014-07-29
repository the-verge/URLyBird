package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import suncertify.db.DBAccessor;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class DBAccessorTest {
	
	static final int FILE_DATA_SECTION_OFFSET = 56;
    
    static final int RECORD_LENGTH = 160;
    
    static DBAccessor accessor = new DBAccessor("/home/ejhnhng/URLyBird/db-1x3.db");
    
    static Class<? extends DBAccessor> DBAccessorClass = accessor.getClass();
    
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
    public void appendRecordTest() throws IOException {
    	
        long initialFileLength = database.length();
        String[] data = {"Newgrange", "Kildare", "4", "Y", "$250.54", "2014/07/21", "12345678"};
        int recordNumber = accessor.createRecord(data);
        assertEquals(31, recordNumber);
        assertEquals(initialFileLength + RECORD_LENGTH, database.length());
        
        String[] newRecord = accessor.readRecord(recordNumber);
        
        assertEquals(newRecord[0], data[0]);
        assertEquals(newRecord[1], data[1]);
        assertEquals(newRecord[2], data[2]);
        assertEquals(newRecord[3], data[3]);
        assertEquals(newRecord[4], data[4]);
        assertEquals(newRecord[5], data[5]);
        assertEquals(newRecord[6], data[6]);
    }
    
    @Test
    public void insertRecordTest() throws IOException {
    	accessor.deleteRecord(3, 1L);
        String[] data = {"Wynn's", "Dublin", "2", "N", "$120", "2014/07/29", "77777777"};
        int recordNumber = accessor.createRecord(data);
        assertEquals(3, recordNumber);
        
        String[] newRecord = accessor.readRecord(recordNumber);
        
        assertEquals(newRecord[0], data[0]);
        assertEquals(newRecord[1], data[1]);
        assertEquals(newRecord[2], data[2]);
        assertEquals(newRecord[3], data[3]);
        assertEquals(newRecord[4], data[4]);
        assertEquals(newRecord[5], data[5]);
        assertEquals(newRecord[6], data[6]);
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
    public void matchRecordTest() {
        String[] criteria = {null, "sMaLlViLl", "4", "Y", "$210.0", "2005/05/02", null};
        boolean match = accessor.matchRecord(null, criteria);
        assertTrue(match);
    }
    
    @Test
    public void matchNullCriteriaTest() {
        String[] nullCriteria = {"", null, null, null, null, null, null};
        boolean match = accessor.matchRecord(null, nullCriteria);
        assertFalse(match);
    }
    
    @Test
    public void calculateRecordNumberTest() throws IOException {
    	int recordNumber;
    	recordNumber = accessor.calculateRecordNumber(FILE_DATA_SECTION_OFFSET);
    	assertEquals(1, recordNumber);
    	recordNumber = accessor.calculateRecordNumber(FILE_DATA_SECTION_OFFSET + RECORD_LENGTH);
    	assertEquals(2, recordNumber);
    	recordNumber = accessor.calculateRecordNumber(FILE_DATA_SECTION_OFFSET + (2* RECORD_LENGTH));
    	assertEquals(3, recordNumber);
    }
    
    @Test
    public void firstAvailablePositionTest() throws IOException {
    	long firstAvailablePosition = accessor.firstAvailablePosition();
    	long recordPosition = accessor.findPositionInFile(32); //suspect
    	assertEquals(recordPosition, firstAvailablePosition);
    }
    
    @Test
    public void retrieveAllRecordsTest() throws IOException {
    	ArrayList<String[]> allRecords = accessor.retrieveAllRecords();
    	int total = allRecords.size();
    	assertEquals(31, total);
    }
    
    
}
