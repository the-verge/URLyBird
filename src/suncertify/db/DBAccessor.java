package suncertify.db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The DBAccessor class provides direct access to the underlying database file.
 * It uses a RandomAccessFile instance to read from and write to the file.
 * The constructor and public methods of this class throw DBException which 
 * extends RunTimeException. This exception wraps checked Exceptions such as 
 * FileNotFoundException, IOException and UnsupportedEncodingException in order
 * to deal with these while still maintaining compliance with the supplied DB.java 
 * interface.
 * 
 * @author 
 */
public class DBAccessor {
    
    /**
     * The number of fields in a record.
     */
    private static final int NUMBER_OF_FIELDS_IN_RECORD = 7;
    
    /**
     * The number of bytes in a record before the data
     * section begins (the first byte contains the deleted
     * record flag).
     */
    private static final int RECORD_DATA_SECTION_OFFSET = 1;
    
    /**
     * The number of bytes in the database file before the data section begins.
     */
    private static final int FILE_DATA_SECTION_OFFSET = 56;
    
    /**
     * The value that denotes a deleted
     * record in the database file.
     */
    private static final byte DELETED_FLAG = (byte) 0xFF;
    
    /**
     * An array that holds the lengths of the data fields of a 
     * record in the database file.
     */
    private static final int[] FIELD_LENGTHS_ARRAY = {Room.NAME_LENGTH, Room.LOCATION_LENGTH, 
        Room.SIZE_LENGTH, Room.SMOKING_LENGTH, Room.RATE_LENGTH, Room.DATE_LENGTH, Room.OWNER_LENGTH};
    
    /**
     * The location of the database that will be used by all
     * instances of the DBAccessor class.  The first object created from
     * the DBAccessor class will initialize this variable, after which 
     * it will not change.
     */
    private static String databaseLocation;
    
    /**
     * The RandomAccessfile instance <code>database</code> 
     * is used to directly read from and write to the 
     * database file.
     */
    private RandomAccessFile database;
    
    /**
     * Logger instance for DBAccessor.java.
     */
    private Logger log = Logger.getLogger("DBAccessor.java");
    
    /**
     * Class constructor. All instances of the class share access
     * to the same database file.
     * @param dbLocation the path to the database file.
     * @throws DBAccessException if the file cannot be found.
     */
    public DBAccessor(String dbLocation) {
        if (databaseLocation == null) {
            databaseLocation = dbLocation;
        }
        else if (dbLocation != databaseLocation) {
            log.logp(Level.WARNING, "DBAccessor.java", "Constructor", 
                    "Ignored database file path "
                  + "- database location already initialised");
        }
        try {
            database = new RandomAccessFile(databaseLocation, "rw");
        } catch (FileNotFoundException e) {
            throw new DBException("Database file not found", e);
        }
    }
    
    /**
     * Reads a record from the database file.
     * @param recNo the record number to retrieve.
     * @return a <code>String</code> array containing
     * the fields of the record.
     * @throws RecordNotFoundException 
     * @throws DBException if an IOException is thrown when 
     * trying to read from the database file.
     */
    public String[] readRecord(int recNo) throws RecordNotFoundException {
        log.entering("DBAccessor.java", "readRecord", recNo);
        final long position = findPositionInFile(recNo);
        byte[] record;
        String[] result;

        try {
            record = retrieveRecord(position);
            // WHAT IF RECORD NUMBER DOES NOT EXIST OR POSITION IN FILE > DATABASE LENGTH?
            if (isDeletedRecord(record)) {
                log.warning("Tried to retrieve deleted record: number " + recNo);
                throw new RecordNotFoundException();
            }
            result = recordToStringArray(record);
        } catch (UnsupportedEncodingException e) {
            throw new DBException("Could not decode data", e);
        } catch (IOException e) {
            throw new DBException("Could not retrieve record", e);
        }
        
        log.exiting("DBAccessor.java", "readRecord", result);
        
        return result;
    }
    
    /**
     * Retrieves a record from a specified position
     * in the file.
     * @param position the position of the record in the file.
     * @return <code>byte[]</code> the bytes that constitute
     * the record.
     * @throws IOException 
     */
    private byte[] retrieveRecord(long position) throws IOException {
        // NEED TO THROW RecordNotFoundException HERE IF POSITION NOT VALID
        log.entering("DBAccessor.java", "retrieveRecord", position);
    	final byte[] record = new byte[Room.RECORD_LENGTH];
        
        synchronized (database) {
            database.seek(position);
            database.readFully(record);
        }
        
        return record;
    }
    
    /**
     * Updates a record.
     * @param recNo the number of the record to update.
     * @param data <code>String</code> array containing the
     * fields of the record to update.
     * @param lockCookie the cookie that the record was
     * locked with.
     * @throws DBException if an IOException is thrown when
     * trying to write to the database file.
     */
    public void updateRecord(int recNo, String[] data, long lockCookie) {
        final long position = findPositionInFile(recNo);
        byte[] record = stringArrayToRecord(data);
        
        synchronized (database) {
            try {
                database.seek(position);
                database.write(record);
            } catch (IOException e) {
                throw new DBException("Could not update record", e);
            }
        }
    }
    
    /**
     * Flags a record as being deleted by assigning the
     * first bye of the record a value of 0xFF.
     * @param recNo the number of the record to delete.
     * @param lockCookie the cookie that the record was
     * locked with.
     * @throws DBException if an IOException is thrown 
     * when attempting to write to the database file.
     */
    public void deleteRecord(int recNo, long lockCookie) {
        log.entering("DBAccessor.java", "deleteRecord", recNo);
        long position = findPositionInFile(recNo);
        synchronized (database) {
            try {
                database.seek(position);
                database.writeByte(DELETED_FLAG);
            } catch (IOException e) {
                throw new DBException("Could not delete record", e);
            }
        }
    }
    
    /**
     * Searches the database file for records whose fields match
     * the supplied criteria. 
     * @param criteria the criteria for which to search.
     * @return <code>int[]</code> the record numbers that match the
     * criteria.
     * @throws DBException if an IOException is thrown 
     * when attempting to read from the database file.
     */
    public int[] find(String[] criteria) {
        ArrayList<Integer> matches = new ArrayList<Integer>();
        ArrayList<String[]> allData;
        try {
            allData = retrieveAllRecords();
        } catch (IOException e) {
            throw new DBException("Could not retrieve records", e);
        }
        
        for (int i = 0; i < allData.size(); i++) {
            String[] data = allData.get(i);
            boolean match = this.matchRecord(data, criteria);
            int recordNumber = i + 1;
            if (match) {
                matches.add(recordNumber);
            }
        }
        
        return arrayListToArray(matches);
    }
    
    /**
     * Retrieves <code>String</code> array representations
     * of all records in the file.
     * @return <code>ArrayList<String[]></code> containing
     * the fields of all records.
     * @throws IOException
     */
    public ArrayList<String[]> retrieveAllRecords() throws IOException {
        log.entering("DBAccessor.java", "retrieveAllRecords");
        
        ArrayList<String[]> result = new ArrayList<String[]>();
        long filePosition = FILE_DATA_SECTION_OFFSET;
        
        while (filePosition < database.length()) {
            byte[] record = retrieveRecord(filePosition);
            filePosition += Room.RECORD_LENGTH;
            if (isDeletedRecord(record)) {
                log.fine("Found deleted record at position " + filePosition);
                continue;
            }
            else {
                String[] data = recordToStringArray(record);
                result.add(data);
            }
        }
        log.exiting("DBAccessor.java", "retrieveAllRecords", result);
        
        return result;
    }
    
    /**
     * Attempts to match a record's fields with supplied criteria.
     * If the criteria String matches the beginning of the record field, 
     * a match in that field is recorded.  For a record to match the criteria, 
     * criteria[n] must match the beginning of data[n] 
     * for all non-null criteria[n].
     * @param data array containing the fields of a record.
     * @param criteria array containing the query criteria
     * for each element in the record field array.  An attempt is made
     * to match the beginning of <code>data[n]</code> 
     * with <code>criteria[n]</code>.
     * @return <code>boolean</code> indicating a match or not.
     */
    public boolean matchRecord(String[] data, String[] criteria) {
        
        int nullCriteria = 0;
        int matches = 0;
        
        for (int i = 0; i < criteria.length; i++) {
            String query = criteria[i];
            String field = data[i];
            // should send null from gui if textfield is empty string
            if (query == null || query.equals("")) {
                nullCriteria++;
                continue;
            }
            else {
                String escapedQuery = Pattern.quote(query);
                // Pattern.CASE_INSENSITIVE assumes US-ASCII
                Pattern pattern = Pattern.compile(escapedQuery, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(field);
                if (matcher.lookingAt()) {
                    matches++;
                }
            }
        }
        
        int fieldsToMatch = NUMBER_OF_FIELDS_IN_RECORD - nullCriteria;
        
        if (matches == fieldsToMatch && nullCriteria != NUMBER_OF_FIELDS_IN_RECORD) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Converts an <code>ArrayList<Integer></code> to an 
     * <code>int</code> array.
     * @param list the ArrayList to convert.
     * @return an <code>int</code> array.
     */
    private int[] arrayListToArray(ArrayList<Integer> list) {
        int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }
    
    /**
     * Creates a new record.
     * @param data <code>String</code> array containing the 
     * fields of the record to be created.
     * @return the number of the newly created record.
     * @throws DBException if an IOException is thrown 
     * when attempting to write to the database file.
     */
    public int createRecord(String[] data) {
        log.entering("DBAccessor.java", "createRecord", data);
        byte[] record = stringArrayToRecord(data);
        int recordNumber = 0;
        synchronized (database) {
            try {
                long position = firstAvailablePosition();
                recordNumber = calculateRecordNumber(position);
                database.seek(position);
                database.write(record);
            } catch (IOException e) {
                throw new DBException("Could not create record", e);
            }
        }
        log.exiting("DBAccessor.java", "createRecord", recordNumber);
        
        return recordNumber;
    }
    
    /**
     * Creates a <code>String</code> array from a <code>byte</code>
     * array read from the database file.
     * @param record <code>byte</code> array read from database file.
     * @return <code>String</code> array containing the fields of
     * the record.
     * @throws UnsupportedEncodingException 
     */
    private String[] recordToStringArray(byte[] record) throws UnsupportedEncodingException {
        
        int offset = RECORD_DATA_SECTION_OFFSET;
        String[] data = new String[FIELD_LENGTHS_ARRAY.length];
        String field;
        
        for (int i = 0; i < FIELD_LENGTHS_ARRAY.length; i++) {
            int fieldLength = FIELD_LENGTHS_ARRAY[i];
            field = new String(record, offset, fieldLength, "US-ASCII").trim();
            data[i] = field;
            offset += fieldLength;
        }
        return data;
    }
    
    /**
     * Converts a <code>String</code> array containing record fields
     * to a <code>byte</code> array that can be written to file.
     * @param data <code>String</code> array containing the 
     * fields of the record to be written to file.
     * @return a <code>byte</code> array that can be written to file.
     */
    private byte[] stringArrayToRecord(String[] data) {
        byte[] emptyRecordByteArray = new byte[Room.RECORD_LENGTH];
        String emptyRecordString = new String(emptyRecordByteArray);
        StringBuilder builder = new StringBuilder(emptyRecordString);
        //BYTES OR CHAR POSITION?
        int startPosition = RECORD_DATA_SECTION_OFFSET;
        // 1st byte will automatically be 0 and therefore be a valid record
        
        for (int i = 0; i < FIELD_LENGTHS_ARRAY.length; i++) {
            String field = data[i];
            if (field != null) {
                int endPosition = startPosition + field.length();
                builder.replace(startPosition, endPosition, field);
            }
            int maxFieldLength = FIELD_LENGTHS_ARRAY[i];
            startPosition += maxFieldLength;
        }
        byte[] record = builder.toString().getBytes();
        
        return record;
    }
    
    /**
     * Finds the position in the file for a given record number.
     * @param recNo the number of the record.
     * @return the position in the file where the record
     * is stored.
     */
    // PRIVATE AFTER TESTING
    public long findPositionInFile(int recNo) {
        return FILE_DATA_SECTION_OFFSET + ((recNo - 1) * Room.RECORD_LENGTH);
    }
    
    /**
     * Calculates the record number from the position of a record in the file.
     * @param filePosition the position of the record in the database file.
     * @return <code>int</code> record number of the file at the specified
     * position.
     */
    public int calculateRecordNumber(long filePosition) {
        return (int) (filePosition - FILE_DATA_SECTION_OFFSET) / Room.RECORD_LENGTH + 1;
    }
    
    /**
     * Finds the first available position to write
     * to in the file.  Accomplished by reading the 
     * first byte of the records in the file 
     * to determine whether they are marked as deleted.  
     * If a deleted record is found, it's position in the file
     * is returned.  If no deleted record is found,
     * the position of the end of the file is
     * returned.
     * @return the first available position in which
     * a record can be written.
     * @throws IOException
     */
    public long firstAvailablePosition() throws IOException {
    	long filePosition = FILE_DATA_SECTION_OFFSET;
    	synchronized (database) {
    		while (filePosition < database.length()) {
    			database.seek(filePosition);
    			byte validRecord = database.readByte();
    			if (validRecord == DELETED_FLAG) {
    				return filePosition;
    			}
    			filePosition += Room.RECORD_LENGTH;
    		}
    		return database.length();
    	}
    }
    
    /**
     * Checks if a record is marked as deleted by comparing
     * the first byte of the record with the 
     * <code>DELETED FLAG<code>
     * @param record the byte representation of a record.
     * @return boolean indicating deleted or not.
     */
    private boolean isDeletedRecord(byte[] record) {
        return record[0] == DELETED_FLAG;
    }
    
    public RandomAccessFile getDatabase() {
        return this.database;
    }

}
