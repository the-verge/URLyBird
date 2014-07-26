package suncertify.db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The DBAccessor class provides direct access to the underlying database file.
 * It uses a RandomAccessFile instance to read from and write to the file.
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
     */
    public DBAccessor(String dbLocation) {
        if (databaseLocation == null) {
            databaseLocation = dbLocation;
        }
        else if (dbLocation != databaseLocation) {
            log.logp(Level.WARNING, "DBAccessor.java", "Constructor", 
                    "Ignored database file path "
                  + "- database location already initialised");;
        }
        try {
            database = new RandomAccessFile(databaseLocation, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Reads a record from the database file.
     * @param recNo the record number to retrieve.
     * @return a <code>String</code> array containing
     * the fields of the record.
     */
    public String[] readRecord(int recNo) {
        log.entering("DBAccessor.java", "readRecord", recNo);
        final long position = findPositionInFile(recNo);
        final byte[] record = new byte[Room.RECORD_LENGTH];
        
        synchronized (database) {
            try {
                database.seek(position);
                database.readFully(record);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        String[] result = recordToStringArray(record);
        
        log.exiting("DBAccessor.java", "readRecord", result);
        
        return result;
    }
    
    /**
     * Updates a record.
     * @param recNo the number of the record to update.
     * @param data <code>String</code> array containing the
     * fields of the record to update.
     * @param lockCookie
     */
    public void updateRecord(int recNo, String[] data, long lockCookie) {
        final long position = findPositionInFile(recNo);
        byte[] record = stringArrayToRecord(data);
        
        synchronized (database) {
            try {
                database.seek(position);
                database.write(record);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Flags a record as being deleted by assigning the
     * first bye of the record a value of 0xFF.
     * @param recNo the number of the record to delete.
     * @param lockCookie
     */
    public void deleteRecord(int recNo, long lockCookie) {
        log.entering("DBAccessor.java", "deleteRecord", recNo);
        long position = findPositionInFile(recNo);
        synchronized (database) {
            try {
                database.seek(position);
                database.writeByte(DELETED_FLAG);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public int[] find(String[] criteria) {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * Creates a new record.
     * @param data <code>String</code> array containing the 
     * fields of the record to be created.
     * @return the number of the newly created record.
     */
    public int createRecord(String[] data) {
        log.entering("DBAccessor.java", "createRecord", data);
        byte[] record = stringArrayToRecord(data);
        int recordNumber = 0;
        synchronized (database) {
            try {
                long position = database.length();
                recordNumber = calculateRecordNumber(position);
                database.seek(position);
                database.write(record);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
     */
    private String[] recordToStringArray(byte[] record) {
        Byte validRecordByte = record[0];
        int validRecord = validRecordByte.intValue();
        
        int offset = Room.VALID_RECORD_LENGTH;
        String[] result = new String[FIELD_LENGTHS_ARRAY.length];
        
        for (int i = 0; i < FIELD_LENGTHS_ARRAY.length; i++) {
            int fieldLength = FIELD_LENGTHS_ARRAY[i];
            String field = "";
            try {
                field = new String(record, offset, fieldLength, "US-ASCII").trim();
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            result[i] = field;
            offset += fieldLength;
        }
        return result;
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
        
        for (int i = 0; i < FIELD_LENGTHS_ARRAY.length; i++) {
            String recordField = data[i];
            if (recordField != null) {
                int endPosition = startPosition + recordField.length();
                builder.replace(startPosition, endPosition, recordField);
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
    private long findPositionInFile(int recNo) {
        return FILE_DATA_SECTION_OFFSET + ((recNo - 1) * Room.RECORD_LENGTH);
    }
    
    /**
     * Calculates the record number from the position of a record in the file.
     * @param filePosition the position of the record in the database file.
     * @return <code>int</code> record number of the file at the specified
     * position.
     * @throws IOException
     */
    private int calculateRecordNumber(long filePosition) throws IOException {
        return (int) (database.length() - FILE_DATA_SECTION_OFFSET) / Room.RECORD_LENGTH + 1;
    }
    
    /**
     * SHOULD BE PRIVATE AFTER TESTING
     * @param recordFields array containing the fields of a record.
     * @param criteria array containing the query criteria
     * for each element in the record field array.  An attempt is made
     * to match <code>recordFields[n]</code> with <code>criteria[n]</code>.
     * @return
     */
    public boolean matchRecord(String[] recordFields, String[] criteria) {
        recordFields = readRecord(1); // hack for testing
        
        int nullCriteria = 0;
        int matches = 0;
        
        for(int i = 0; i < criteria.length; i++) {
            String query = criteria[i];
            String field = recordFields[i];
            if (query == null) {
                nullCriteria++;
                continue;
            }
            else {
                String escapedQuery = Pattern.quote(query);
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
    
    public RandomAccessFile getDatabase() {
        return this.database;
    }

}
