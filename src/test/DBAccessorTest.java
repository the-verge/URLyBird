package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import suncertify.db.DBAccessor;
import suncertify.db.RecordNotFoundException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.JVM)
public class DBAccessorTest {
	
	static final int FILE_DATA_SECTION_OFFSET = 56;
    
    static final int RECORD_LENGTH = 160;
    
    static DBAccessor accessor = new DBAccessor("/home/ejhnhng/URLyBird/db-1x3.db");
    
    static RandomAccessFile database = accessor.getDatabase();
    
    static long initialDatabaseLength;
    
    @BeforeClass
    public static void getInitialDBLength() throws IOException {
        initialDatabaseLength = database.length();
        assertEquals(initialDatabaseLength, 4856);
    }
    
    @AfterClass
    public static void resetDB() throws IOException, RecordNotFoundException {
        String[] resetData = {"Bed & Breakfast & Business", "Lendmarch", "6", "Y", "$170.00", "2005/03/10", ""};
        accessor.updateRecord(30, resetData);
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
        
        database.close();
    }

    @Test
    public void readRecordTest() throws RecordNotFoundException {
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
    public void appendRecordTest() throws IOException, RecordNotFoundException {
    	
        long initialFileLength = database.length();
        String[] data = {"Newgrange", "Kildare", "4", "Y", "$250.54", "2014/07/21", "12345678"};
        int recordNumber = accessor.createRecord(data);
        
        assertEquals(31, recordNumber);
        assertEquals(initialFileLength + RECORD_LENGTH, database.length());
        
        String[] newRecord = accessor.readRecord(recordNumber);
        assertArrayEquals(data, newRecord);
        
    }
    
    @Test
    public void insertRecordTest() throws IOException, RecordNotFoundException {
    	accessor.deleteRecord(3);
    	assertEquals(1, accessor.getDeletedRecordsList().size());
    	assertEquals(3, (int) accessor.getDeletedRecordsList().get(0));
    	
        String[] data = {"Wynn's", "Dublin", "2", "N", "$120", "2014/07/29", "77777777"};
        
        int recordNumber = accessor.createRecord(data);
        assertEquals(3, recordNumber);
        assertEquals(0, accessor.getDeletedRecordsList().size());
        
        String[] newRecord = accessor.readRecord(recordNumber);
        assertArrayEquals(data, newRecord);
    }
    
    @Test
    public void updateRecordTest() throws RecordNotFoundException {
        String[] newData = {"The Mews", "Donegal", "4", "Y", "$250.54", "2014/07/21", "12345678"};
        accessor.updateRecord(30, newData);
        
        String[] updatedRecord = accessor.readRecord(30);
        assertArrayEquals(newData, updatedRecord);
    }
    
    @Test
    public void calculateRecordNumberTest() throws IOException {
    	int recordNumber;
    	recordNumber = accessor.calculateRecordNumber(FILE_DATA_SECTION_OFFSET);
    	assertEquals(1, recordNumber);
    	recordNumber = accessor.calculateRecordNumber(FILE_DATA_SECTION_OFFSET + RECORD_LENGTH);
    	assertEquals(2, recordNumber);
    	recordNumber = accessor.calculateRecordNumber(FILE_DATA_SECTION_OFFSET + (2 * RECORD_LENGTH));
    	assertEquals(3, recordNumber);
    }
    
    @Test
    public void firstAvailablePositionTest() throws IOException {
    	long firstAvailablePosition = accessor.firstAvailablePosition();
    	long recordPosition = accessor.calculateFilePosition(32); //suspect
    	assertEquals(recordPosition, firstAvailablePosition);
    }
    
    @Test
    public void matchRecordTest() throws RecordNotFoundException {
        String[] record = accessor.readRecord(1);
        String[] criteria = {null, "sMaLlViLl", "4", "Y", "$210.0", "2005/05/02", null};
        boolean match = accessor.matchRecord(record, criteria);
        assertTrue(match);
    }
    
    @Test
    public void matchNullCriteriaTest() throws RecordNotFoundException {
        String[] record = accessor.readRecord(1);
        String[] nullCriteria = {"", null, null, null, null, null, null};
        boolean match = accessor.matchRecord(record, nullCriteria);
        assertFalse(match);
    }
    
    @Test
    public void matchRecordsTest() {
        int[] matches;
    
        String[] criteria = {null, "sMaLlViLl", "4", "Y", "$210.0", "2005/05/02", null};
        matches = accessor.find(criteria);
        
        assertEquals(1, matches.length);
        assertEquals(1, matches[0]);
        
        String[] dewDropCriteria = {"Dew Drop Inn", null, null, null, null, null, null};
        matches = accessor.find(dewDropCriteria);
        assertEquals(3, matches.length);
        assertEquals(1, matches[0]);
        assertEquals(5, matches[1]);
        assertEquals(25, matches[2]);
    }
    
    @Test
    public void matchSimilarRecordsTest() {
        
        String[] room1 = {"The Shelbourne", "Dublin", "2", "N", "$250", "2014/08/01", ""};
        String[] room2 = {"The Shelbourne", "Dublin", "2", "Y", "$150", "2013/07/01", ""};
        String[] room3 = {"The Shelbourne", "Dublin", "1", "Y", "$100", "2012/08/01", ""};
        
        String[] criteria1 = {"The Shelbourne", "Dublin", "2", "N", null, null, null};
        String[] criteria2 = {"The Shelbourne", "Dublin", "2", null, null, null, null};
        String[] criteria3 = {"The Shelbourne", "Dublin", null, null, null, null, null};
        
        int room1Number = accessor.createRecord(room1);
        int room2Number = accessor.createRecord(room2);
        int room3Number = accessor.createRecord(room3);
        
        int[] matchOneRecord = accessor.find(criteria1);
        assertEquals(1, matchOneRecord.length);
        assertEquals(room1Number, matchOneRecord[0]);
        
        int[] matchTwoRecords = accessor.find(criteria2);
        assertEquals(2, matchTwoRecords.length);
        assertEquals(room1Number, matchTwoRecords[0]);
        assertEquals(room2Number, matchTwoRecords[1]);
        
        int[] matchThreeRecords = accessor.find(criteria3);
        assertEquals(3, matchThreeRecords.length);
        assertEquals(room1Number, matchThreeRecords[0]);
        assertEquals(room2Number, matchThreeRecords[1]);
        assertEquals(room3Number, matchThreeRecords[2]);
    }
    
    @Test
    public void retrieveAllRecordsTest() throws IOException, RecordNotFoundException {
        ArrayList<String[]> allRecords = accessor.retrieveAllRecords();
        int total = allRecords.size();
        assertEquals(34, total);
        
        for(int i = 0; i < allRecords.size(); i++) {
            String[] record = accessor.readRecord(i + 1);
            assertArrayEquals(record, allRecords.get(i));
        }
    }
    
    @Test(expected=RecordNotFoundException.class)
    public void recordNotFoundExceptionTest() throws RecordNotFoundException {
        assertEquals(0, accessor.getDeletedRecordsList().size());
        accessor.deleteRecord(34);
        assertEquals(1, accessor.getDeletedRecordsList().size());
        assertEquals(34, (int) accessor.getDeletedRecordsList().get(0));
        accessor.readRecord(34);
    }
    
    @Test
    public void retrieveAllRecordsAfterDeleteTest() throws IOException {
        ArrayList<String[]> allRecords;
        int total;
        
        accessor.deleteRecord(31);
        allRecords = accessor.retrieveAllRecords();
        total = allRecords.size();
        assertEquals(32, total);
        
        accessor.deleteRecord(32);
        allRecords = accessor.retrieveAllRecords();
        total = allRecords.size();
        assertEquals(31, total);
       
        accessor.deleteRecord(33);
        allRecords = accessor.retrieveAllRecords();
        total = allRecords.size();
        assertEquals(30, total);
        
    }
    
    
}
