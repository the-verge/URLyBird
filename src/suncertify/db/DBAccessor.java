package suncertify.db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The DBAccessor class provides direct access to the underlying database file.
 * It uses a RandomAccessFile instance to read from and write to the file.
 * The constructor and public methods of this class (excluding getters) throw 
 * DBException which extends RunTimeException. This exception wraps checked 
 * Exceptions such as FileNotFoundException, IOException and 
 * UnsupportedEncodingException in order to deal with these while still 
 * maintaining <code>Datacompliance</code> with the supplied DB.java 
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
     * List that holds the numbers (calculated from position in file)
     * of deleted records.
     */
    private static List<Integer> deletedRecordsList = new ArrayList<Integer>();
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
        //WRONG 1ST TRY RENDERS THIS UNUSABLE
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
            initialiseDeletedRecordsList();
        } catch (FileNotFoundException e) {
            throw new DBException("Database file not found", e);
        } catch (IOException e) {
            throw new DBException("Error reading file", e);
        }
    }
    
    /**
     * Reads a record from the database file.
     * @param recNo the record number to retrieve.
     * @return a <code>String</code> array containing
     * 			the fields of the record.
     * @throws RecordNotFoundException if the record doesn't
     * 			exist in the database.
     * @throws DBException if an IOException is thrown when 
     * 			trying to read from the database file.
     */
    public String[] readRecord(int recNo) throws RecordNotFoundException {
        log.entering("DBAccessor.java", "readRecord", recNo);
        final long position = calculateFilePosition(recNo);
        byte[] record;
        String[] result;
        
        try {
            if (!recordExists(recNo)) {
                throw new RecordNotFoundException("Record number " + recNo + " does not exist");
            }
            else {
                record = retrieveRecord(position);
                result = recordToStringArray(record); 
            }
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
     * 			the record.
     * @throws IOException if there is an error reading from 
     * 			the file.
     */
    private byte[] retrieveRecord(long position) throws IOException {
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
     * 		   fields of the record to update.
     * @throws DBException if an IOException is thrown when
     * 			trying to write to the database file.
     */
    public void updateRecord(int recNo, String[] data) {
        final long position = calculateFilePosition(recNo);
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
     * @throws DBException if an IOException is thrown 
     * 			when attempting to write to the database file.
     */
    public void deleteRecord(int recNo) {
        log.entering("DBAccessor.java", "deleteRecord", recNo);
        long position = calculateFilePosition(recNo);
        synchronized (database) {
            try {
                database.seek(position);
                database.writeByte(DELETED_FLAG);
            } catch (IOException e) {
                throw new DBException("Could not delete record", e);
            }
        }
        synchronized(deletedRecordsList) {
            deletedRecordsList.add(recNo);
        }
    }
    
    /**
     * Searches the database file for records whose fields match
     * the supplied criteria. 
     * @param criteria the criteria for which to search.
     * @return <code>int[]</code> the record numbers that match the
     * 			criteria.
     * @throws DBException if an IOException is thrown 
     * 			when attempting to read from the database file.
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
     * 			the fields of all records.
     * @throws IOException if an error occurs when reading
     * 			from the file.
     */
    public ArrayList<String[]> retrieveAllRecords() throws IOException {
        log.entering("DBAccessor.java", "retrieveAllRecords");
        
        ArrayList<String[]> result = new ArrayList<String[]>();
        long filePosition = FILE_DATA_SECTION_OFFSET;
        
        while (filePosition < database.length()) {
            
            int recordNumber = calculateRecordNumber(filePosition);
            
            if (isDeletedRecord(recordNumber)) {
                log.fine("Found deleted record at position " + filePosition);
                filePosition += Room.RECORD_LENGTH;
                continue;
            }
            else {
                byte[] record = retrieveRecord(filePosition);
                String[] data = recordToStringArray(record);
                result.add(data);
                filePosition += Room.RECORD_LENGTH;
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
     * 		   for each element in the record field array.  An attempt is made
     * 		   to match the beginning of <code>data[n]</code> 
     * 		   with <code>criteria[n]</code>.
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
     * 			when attempting to write to the database file.
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
    
    public long firstAvailablePosition() throws IOException {
    	synchronized(deletedRecordsList) {
    	    if (deletedRecordsList.size() > 0) {
                int recordNumber = deletedRecordsList.get(0);
                // IF THE WRITE FAILS THERE WILL BE A DELETED RECORD THAT IS NOT FLAGGED AS SUCH
                // THAT IS, IT WAS REMOVED FROM THE LIST BEFORE WE KNEW THAT THEN WRITE WAS 
                // SUCCESSFUL.
                deletedRecordsList.remove(0);
                return calculateFilePosition(recordNumber);
            }
    	}
        return database.length();
    }
    
    /**
     * Creates a <code>String</code> array from a <code>byte</code>
     * array read from the database file.
     * @param record <code>byte</code> array read from database file.
     * @return <code>String</code> array containing the fields of
     * 			the record.
     * @throws UnsupportedEncodingException 
     */
    private String[] recordToStringArray(byte[] record) throws UnsupportedEncodingException {
        
        int offset = RECORD_DATA_SECTION_OFFSET;
        String[] data = new String[FIELD_LENGTHS_ARRAY.length];
        String field;
        
        for (int i = 0; i < FIELD_LENGTHS_ARRAY.length; i++) {
            int fieldLength = FIELD_LENGTHS_ARRAY[i];
            // POSSIBLE NPE
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
     * 		   fields of the record to be written to file.
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
     * 			is stored.
     */
    public long calculateFilePosition(int recNo) {
        return FILE_DATA_SECTION_OFFSET + ((recNo - 1) * Room.RECORD_LENGTH);
    }
    
    /**
     * Calculates the record number from the position of a record in the file.
     * @param filePosition the position of the record in the database file.
     * @return <code>int</code> record number of the file at the specified
     * 			position.
     */
    public int calculateRecordNumber(long filePosition) {
        return (int) (filePosition - FILE_DATA_SECTION_OFFSET) / Room.RECORD_LENGTH + 1;
    }
    
    public boolean recordExists(int recNo) {
        long position = calculateFilePosition(recNo);
        
        try {
            if (!validFilePosition(position) || isDeletedRecord(recNo)) {
                return false;
            }
        } catch (IOException e) {
            throw new DBException("Could not determine whether record number" + recNo + " exists", e);
        }
        return true;
    }
    
    private boolean isDeletedRecord(int recNo) {
        synchronized (deletedRecordsList) {
            return deletedRecordsList.contains(recNo); 
        }
    }
    
    private boolean validFilePosition(long position) throws IOException {
        return (position < database.length()) && (position > 0);
    }
    
    public void initialiseDeletedRecordsList() throws IOException {
        long filePosition = FILE_DATA_SECTION_OFFSET;
        while (filePosition < database.length()) {
            if(database.readByte() == DELETED_FLAG) {
                int recordNumber = calculateRecordNumber(filePosition);
                deletedRecordsList.add(recordNumber);
            }
            filePosition += Room.RECORD_LENGTH;
        }
    }
    
    public RandomAccessFile getDatabase() {
        return database;
    }
    
    public List<Integer> getDeletedRecordsList() {
        return deletedRecordsList;
    }
    

}
