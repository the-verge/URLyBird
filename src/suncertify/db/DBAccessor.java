package suncertify.db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The DBAccessor class provides direct access to the underlying database file.
 * It uses a RandomAccessFile instance to read from and write to the file.
 * 
 * @author 
 */
public class DBAccessor {
    
    /**
     * <code>int</code> value that denotes where the data
     * section of the database files begins.
     */
    private static final int DATA_SECTION_OFFSET = 56;
    
    /**
     * <code>byte</code> value that denotes a deleted
     * record in the database file.
     */
    private final byte deletedFlag = (byte) 0xFF;
    
    /**
     * The location of the database that will be used by all
     * instances of the DBAccessor class.  The first object created from
     * the DBAccessor class will initialize this variable, after which 
     * it will not change.
     */
    private static String databaseLocation;
    
    /**
     * An array that holds the lengths of the data fields of a 
     * record in the database file.
     */
    private final static int[] fieldLengths = {Room.NAME_LENGTH, Room.LOCATION_LENGTH, 
        Room.SIZE_LENGTH, Room.SMOKING_LENGTH, Room.RATE_LENGTH, Room.DATE_LENGTH, Room.OWNER_LENGTH};
    
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
        String[] result = {};
        result = recordToStringArray(record);
        
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
        byte[] record = stringArrayToRecord(data, false);
        
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
     * Flags a record as being deleted.
     * @param recNo the number of the record to delete.
     * @param lockCookie
     */
    public void deleteRecord(int recNo, long lockCookie) {
        log.entering("DBAccessor.java", "deleteRecord", recNo);
        long position = findPositionInFile(recNo);
        synchronized (database) {
            try {
                database.seek(position);
                database.writeByte(deletedFlag);
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
        byte[] record = stringArrayToRecord(data, true);
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
        String[] result = new String[fieldLengths.length];
        
        for (int i = 0; i < fieldLengths.length; i++) {
            int fieldLength = fieldLengths[i];
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
     * @param createRecord
     * @return
     */
    private byte[] stringArrayToRecord(String[] data, boolean createRecord) {
        byte[] emptyRecordByteArray = new byte[Room.RECORD_LENGTH];
        String emptyRecordString = new String(emptyRecordByteArray);
        StringBuilder builder = new StringBuilder(emptyRecordString);
        int startPosition = Room.VALID_RECORD_LENGTH;
        
        for (int i = 0; i < fieldLengths.length; i++) {
            int endPosition = startPosition + data[i].length();
            builder.replace(startPosition, endPosition, data[i]);
            int maxFieldLength = fieldLengths[i];
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
        return DATA_SECTION_OFFSET + ((recNo - 1) * Room.RECORD_LENGTH);
    }
    
    /**
     * Calculates the record number from the postion of a record in the file.
     * @param filePosition the position of the record in the database file.
     * @return <code>int</code> record number of the file at the specified
     * position.
     * @throws IOException
     */
    private int calculateRecordNumber(long filePosition) throws IOException {
        return (int) (database.length() - DATA_SECTION_OFFSET) / Room.RECORD_LENGTH + 1;
    }
    
    public RandomAccessFile getDatabase() {
        return this.database;
    }

}
